package com.neu.prattle.websocket;

/**
 * A simple chat client based on websockets.
 *
 * @author https://github.com/eugenp/tutorials/java-websocket/src/main/java/com/baeldung/websocket/ChatEndpoint.java
 * @version dated 2017-03-05
 */

import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.*;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The Class ChatEndpoint.
 * This class handles Messages that arrive on the server.
 */
@ServerEndpoint(value = "/chat/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndpoint {


  /**
   * The account service.
   */
  private UserServiceWithGroups groupService = UserServiceWithGroupsImpl.getInstance();
  private UserService userService = UserServiceImpl.getInstance();
  private MessageService messageService = MessageServiceImpl.getInstance();

  
  /**
   * The session.
   */
  private Session session;


  /**
   * The Constant chatEndpoints. ConcurrentHashMap should be thread safe.
   */
  private static ConcurrentHashMap<String, ChatEndpoint> chatEndpoints = new ConcurrentHashMap<>();

  /**
   * The users.
   */
  private static HashMap<String, String> users = new HashMap<>();

  /**
   * The logger.
   */
  private static Logger logger = Logger.getLogger(ChatEndpoint.class.getName());

  private void setAccountService(UserServiceWithGroups groupService) {
    this.groupService = groupService;
  }

  private void setSession(Session session) {
    this.session = session;
  }

  /**
   * On open.
   * <p>
   * Handles opening a new session (websocket connection). If the user is a known user (user
   * management), the session added to the pool of sessions and an announcement to that pool is made
   * informing them of the new user.
   * <p>
   * If the user is not known, the pool is not augmented and an error is sent to the originator.
   *
   * @param session  the web-socket (the connection)
   * @param username the name of the user (String) used to find the associated UserService object
   * @throws IOException     Signals that an I/O exception has occurred.
   * @throws EncodeException the encode exception
   */
  @OnOpen
  public void onOpen(Session session, @PathParam("username") String username)
      throws IOException, EncodeException {


    Optional<User> user = userService.findUserByName(username);
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
        if (!m.getContent().equals("friendRequest")) sendMessage(m);
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
   * @param session  the newly opened session
   * @param username the user who connected
   */
  private void addEndpoint(Session session, String username) {
    this.session = session;
    chatEndpoints.put(username, this);
    /* users is a hashmap between session ids and users */
    users.put(session.getId(), username);
  }

  /**
   * On message.
   * <p>
   * When a message arrives, broadcast it to all connected users.
   *
   * @param session the session originating the message
   * @param message the text of the inbound message
   */
  @OnMessage
  public void onMessage(Session session, Message message) {
    message.setFrom(users.get(session.getId()));
        message.setTimestamp();
        messageService.createMessage(message);
        if (message.getTo() == null || message.getTo().length() == 0) {
            broadcast(message);
          return;
        }

    String[] to = message.getTo().trim().split(" ");
    switch (to[0].toUpperCase()) {
      case "GROUP":
        sendGroupMessage(message);
        break;
      case "NEWGROUP":
        addGroup(message);
        break;
      case "":
        broadcast(message);
        break;
      default:
        sendMessage(message);
        break;
    }
  }

  /**
   * On close.
   * <p>
   * Closes the session by removing it from the pool of sessions and broadcasting the news to
   * everyone else.
   *
   * @param session the session
   */
  @OnClose
  public void onClose(Session session) {
    chatEndpoints.remove(users.get(session.getId()), this);
    Message message = new Message();
    message.setFrom(users.get(session.getId()));
    message.setContent("Disconnected!");
    broadcast(message);
  }


  /**
   * On error.
   * <p>
   * Handles situations when an error occurs.  Not implemented.
   *
   * @param session   the session with the problem
   * @param throwable the action to be taken.
   */
  @OnError
  public void onError(Session session, Throwable throwable) {
    // Do error handling here
  }

  /**
   * Broadcast.
   * <p>
   * Send a Message to each session in the pool of sessions. The Message sending action is
   * synchronized.  That is, if another Message tries to be sent at the same time to the same
   * endpoint, it is blocked until this Message finishes being sent..
   *
   * @param message
   */
  private static void broadcast(Message message) {
    chatEndpoints.forEach((user, endpoint) -> {
      synchronized (endpoint) {
        try {
          endpoint.session.getBasicRemote()
              .sendObject(message);
        } catch (IOException | EncodeException |  NullPointerException e) {
          /* note: in production, who exactly is looking at the console.  This exception's
           *       output should be moved to a logger.
           */
          logger.log(Level.SEVERE, e.getMessage());
        }
      }
    });
  }

  /**
   * Sends a direct message to a user. If the user can't be found, sends a message back to user.
   *
   * @param message
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
            if(!message.getContent().equals("friendRequest")) senderEndpoint.session.getBasicRemote()
                    .sendObject(message);
        }catch (IOException | EncodeException | NullPointerException e){
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
  


  /**
   * Sends a message to a group. If group can't be found, does nothing.
   *
   * @param message
   */
  private void sendGroupMessage(Message message) {
    //get groups
    String[] instructions = message.getTo().trim().split(" ");

    //user already registered to send messages
    Optional<User> op = userService.findUserByName(message.getFrom());
    User sender = new User();
    if(op.isPresent()) {
      sender = op.get();
    }
    
    //if group is found, sentMessage to all members
    if (instructions.length > 1) {
      for (int i = 0; i < instructions.length - 1; i++) {
        Optional<BasicGroup> group = groupService
            .findGroupByName(message.getFrom(), instructions[i + 1]);
        if (group.isPresent() && group.get().getMembers().contains(sender)) {
          for (User mem : group.get().getMembers()) {
            Message m = Message.messageBuilder().setFrom(group.get().getName() + ": " + message.getFrom()).setTo(mem.getName()).setMessageContent(message.getContent()).build();
            if (chatEndpoints.containsKey(mem.getName())) {
              synchronized (chatEndpoints.get(mem.getName())) {
                try {
                  chatEndpoints.get(mem.getName()).session.getBasicRemote().sendObject(m);
                } catch (IOException | EncodeException e) {
                  /* note: in production, who exactly is looking at the console.  This exception's
                   *       output should be moved to a logger.
                   */
                  logger.log(Level.SEVERE, e.getMessage());


                }
              }
            }
          }
        }
      }
    }
  }


  /**
   *
   * Depreciated. Using GroupController to add a group.
   *
   * Adds a group to the UserServiceWithGroup groupSet.
   *
   * @param message - received message. Group specifications in body. Space separated: name then
   * members. Groups are stores as a HashMap<User, HashMap<Group Name, Group>>.
   */
  private void addGroup(Message message) {
    User user = new User(message.getFrom());
    String[] content = message.getContent().trim().split(" ");
    String name = content[0];

    //if no members specified, notify the user
    if (content.length < 2) {
      synchronized (chatEndpoints.get(message.getFrom())) {
        try {
          chatEndpoints.get(message.getFrom()).session.getBasicRemote()
              .sendObject(Message.messageBuilder().setTo(message.getFrom())
                  .setMessageContent("No Members specified :(").build());
        } catch (IOException | EncodeException e) {
          logger.log(Level.SEVERE, e.getMessage());
        }
      }
    }

    StringBuilder usersNotExist = new StringBuilder();

    //Build group, add specified members
    Set<User> add = new HashSet<>();
    for (int i = 0; i < content.length - 1; i++) {
      add.add(new User(content[i + 1]));
      if (!userService.findUserByName(content[i + 1]).isPresent()) {
        usersNotExist.append(content[i + 1]);
        usersNotExist.append(" ");
      }
    }
    BasicGroup group = BasicGroup.groupBuilder().setName(name).setMembers(add).build();

    //Notify user if members added do not exist
    if (!usersNotExist.toString().equalsIgnoreCase("")) {
      synchronized (chatEndpoints.get(message.getFrom())) {
        try {
          chatEndpoints.get(message.getFrom()).session.getBasicRemote()
              .sendObject(Message.messageBuilder().setTo(message.getFrom())
                  .setMessageContent("Members not found in service: " + usersNotExist).build());
        } catch (IOException | EncodeException e) {
          logger.log(Level.SEVERE, e.getMessage());
        }

      }
    }

    //try to add group
    try {
      synchronized (groupService) {
        //Add the group
        groupService.addGroup(group);

        //Send Feedback to the user
        synchronized (chatEndpoints.get(message.getFrom())) {
          chatEndpoints.get(message.getFrom()).session.getBasicRemote()
              .sendObject(Message.messageBuilder().setTo(message.getFrom())
                  .setMessageContent("group created: " + group.getName()).build());

        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }
}

