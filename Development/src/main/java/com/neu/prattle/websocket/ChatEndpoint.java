package com.neu.prattle.websocket;

/**
 * A simple chat client based on websockets.
 * 
 * @author https://github.com/eugenp/tutorials/java-websocket/src/main/java/com/baeldung/websocket/ChatEndpoint.java
 * @version dated 2017-03-05
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

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
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;

/**
 * The Class ChatEndpoint.
 * 
 * This class handles Messages that arrive on the server.
 */
@ServerEndpoint(value = "/chat/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndpoint {
    
    /** The account service. */
    private UserService accountService = UserServiceImpl.getInstance();
    
    /** The session. */
    private Session session;
    
    /** The Constant chatEndpoints. */
    private static final Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    
    /** The users. */
    private static HashMap<String, String> users = new HashMap<>();

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
        chatEndpoints.add(this);
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
        broadcast(message);
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
        chatEndpoints.remove(this);
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
     * @param message 
     */
    private static void broadcast(Message message) {
        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote()
                            .sendObject(message);
                } catch (IOException | EncodeException e) {
                	/* note: in production, who exactly is looking at the console.  This exception's
                	 *       output should be moved to a logger.
                	 */
                    e.printStackTrace();
                }
            }
        });
    }
}

