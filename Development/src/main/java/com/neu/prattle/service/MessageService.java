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

  List<Message> getDirectMessages(String user, String sender);

  List<Message> getGroupMessages(String user, String group);

}
