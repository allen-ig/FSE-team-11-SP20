package com.neu.prattle.service;

import com.neu.prattle.model.Message;

import java.util.List;

/**
 * Handles all database interactions for CRUD operations
 * of Message objects
 */
public interface MessageService {



  /**
   * Deletes a message from the database
   * @param message is the message to be deleted
   */
  void deleteMessage(Message message);

  /**
   * Creates/saves a new Message
   * @param message message to be created
   * @return the newly saved Message
   */
  Message createMessage(Message message);

  /**
   * Determines if an instance of MessageService should be
   * configured to use test DB or not
   * @return true if test DB, false if not
   */
  boolean isTest();

  /**
   * Returns a List of all Messages sent to a User
   * @param username of the user whose messages are to be retrieved
   * @return A List of all messages sent to a User
   */
  List<Message> getUserMessages(String username);

  /**
   * Returns a List of all messages sent to a User from another User
   * @param user is the username of the User who is the recipient of messages
   * @param sender is the username of the User who is the sender of messages
   * @return a List of messages sent from sender to user if there are any
   */
  List<Message> getDirectMessages(String user, String sender);

  /**
   * Returns a List of all messages sent to a User from a Group
   * @param user is the username of the recipient
   * @param group is the name of the sending group
   * @return a List of messages if sent from a Group to a User
   */
  List<Message> getGroupMessages(String user, String group);

}
