package com.neu.prattle.websocket;

/**
 * A simple chat client based on websockets.
 *
 * @author https://github.com/eugenp/tutorials/java-websocket/src/main/java/com/baeldung/websocket/ChatEndpoint.java
 * @version dated 2017-03-05
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import com.neu.prattle.service.MessageServiceImpl;

/**
 * The Class ChatEndpoint.
 *
 * This class handles Messages that arrive on the server.
 */
@ServerEndpoint(value = "/chat/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndpoint {

    /** The account service. */

    private UserService accountService = UserServiceImpl.getInstance();
    private MessageService messageService = MessageServiceImpl.getInstance();

    /** The session. */
    private Session session;

    /** The Constant chatEndpoints.
     * Have to make it not final for the mock test. */
    private static ConcurrentHashMap<String, ChatEndpoint> chatEndpoints = new ConcurrentHashMap<>();

    /** The users. */
    private static HashMap<String, String> users = new HashMap<>();

    /** The logger. */
    private static Logger logger = Logger.getLogger(ChatEndpoint.class.getName());

  private void setAccountService(UserService accountService) {
    this.accountService = accountService;
  }

  private void setSession(Session session) {
    this.session = session;
  }

  /**
	 * On open.
	 *
	 * Handles opening a new session (websocket connection). If the user is a known
	 * user (user management), the session added to the pool of sessions and an
	 * announcement to that pool is made informing them of the new user.
	 *
	 * If the user is not known, the pool is not augmented and an error is sent to
	 * the originator.
	 *
	 * @param session  the web-socket (the connection)
	 * @param username the name of the user (String) used to find the associated
	 *                 UserService object
	 * @throws IOException     Signals that an I/O exception has occurred.
	 * @throws EncodeException the encode exception
	 */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {

        Optional<User> user = accountService.findUserByName(username);
        if (!user.isPresent()) {
            Message error = Message.messageBuilder()
                    .setMessageContent(String.format("User %s could not be found", username))
                    .build();

            session.getBasicRemote().sendObject(error);
            return;
        }

        addEndpoint(session, username);
        Message message = createConnectedMessage(username);
        broadcast(message);
      List<Message> userMessages = messageService.getUserMessages(username);
      for (Message m : userMessages){
        sendMessage(m);
      }
    }

    /**
     * Creates a Message that some user is now connected - that is, a Session was opened
     * successfully.
     *
     * @param username the username
     * @return Message
     */
    private Message createConnectedMessage(String username) {
        return Message.messageBuilder()
                .setFrom(username)
                .setMessageContent("Connected!")
                .build();
    }

    /**
     * Adds a newly opened session to the pool of sessions.
     *
     * @param session    the newly opened session
     * @param username   the user who connected
     */
    private void addEndpoint(Session session, String username) {
        this.session = session;
        chatEndpoints.put(username, this);
        /* users is a hashmap between session ids and users */
        users.put(session.getId(), username);
    }

    /**
     * On message.
     *
     * When a message arrives, broadcast it to all connected users.
     *
     * @param session the session originating the message
     * @param message the text of the inbound message
     */
    @OnMessage
    public void onMessage(Session session, Message message) {
        message.setFrom(users.get(session.getId()));
        messageService.createMessage(message);
        if (message.getTo() == null || message.getTo().length() == 0) {
            broadcast(message);
        }else{
            sendMessage(message);
        }
    }

    /**
     * On close.
     *
     * Closes the session by removing it from the pool of sessions and
     * broadcasting the news to everyone else.
     *
     * @param session the session
     */
    @OnClose
    public void onClose(Session session) {
        chatEndpoints.values().remove(this);
        Message message = new Message();
        message.setFrom(users.get(session.getId()));
        message.setContent("Disconnected!");
        broadcast(message);
    }

    /**
     * On error.
     *
     * Handles situations when an error occurs.  Not implemented.
     *
     * @param session the session with the problem
     * @param throwable the action to be taken.
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    /**
     * Broadcast.
     *
     * Send a Message to each session in the pool of sessions.
     * The Message sending action is synchronized.  That is, if another
     * Message tries to be sent at the same time to the same endpoint,
     * it is blocked until this Message finishes being sent..
     *
     * @param message to be broadcasted
     */
    private static void broadcast(Message message) {
        chatEndpoints.forEach((user, endpoint) -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote()
                            .sendObject(message);

                } catch (IOException | EncodeException | NullPointerException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                }
            }
        });
    }

    /**
     * User to user message.
     *
     * Send a Message to a particular user.
     * @param message to be sent
     */
    private static synchronized void sendMessage(Message message) {
        ChatEndpoint recipientEndpoint = chatEndpoints.get(message.getTo());
        ChatEndpoint senderEndpoint = chatEndpoints.get(message.getFrom());
        if (!chatEndpoints.containsKey(message.getTo())){
            Message errorMessage = new Message();
            errorMessage.setFrom("SYSTEM");
            errorMessage.setContent("The recipient does not exist or is currently offline");
            try {
                senderEndpoint.session.getBasicRemote()
                        .sendObject(errorMessage);
            }catch (IOException | EncodeException | NullPointerException e){
                logger.log(Level.SEVERE, e.getMessage());
            }
            return;
        }
        try{
            recipientEndpoint.session.getBasicRemote()
                    .sendObject(message);
            senderEndpoint.session.getBasicRemote()
                    .sendObject(message);
        }catch (IOException | EncodeException | NullPointerException e){
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}

