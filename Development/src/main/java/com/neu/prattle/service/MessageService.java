package com.neu.prattle.service;

import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;

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
   * @param message
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
   * @param username
   * @return A List of all messages sent to a User
   */
  List<Message> getUserMessages(String username);

}
