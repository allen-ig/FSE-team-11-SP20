package com.neu.prattle.service;

import com.neu.prattle.main.HibernateUtil;
import com.neu.prattle.model.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class MessageServiceImpl implements MessageService {

  private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
  private boolean isTest;
  private Logger logger = LogManager.getLogger();

  /**
   * MessageServiceImpl is a "Singleton" class
   */
  private MessageServiceImpl() {
  }

  private static MessageServiceImpl messageService;
  private static MessageServiceImpl testingMessageService;

  static {
    messageService = new MessageServiceImpl();
    messageService.isTest = false;
  }

  static {
    testingMessageService = new MessageServiceImpl();
    testingMessageService.sessionFactory =
        HibernateUtil.getTestSessionFactory();
    testingMessageService.isTest = true;
  }

  public static MessageService getInstance() {
    if (System.getProperty("testing") == null) {
      return messageService;
    } else if (System.getProperty("testing").equals("true")) {
      return testingMessageService;
    }
    return messageService;
  }

  @Override
  public void deleteMessage(Message message) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    try {
      session.delete(message);
      session.getTransaction().commit();
      logger.info("Message " + message.getId() + " deleted.");
    } catch (Exception e){
     logger.error(e.getMessage());
    } finally{
      session.close();
    }
  }

  @Override
  public Message createMessage(Message message) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    try {
      session.save(message);
      session.getTransaction().commit();
      logger.info("Message created: " + message.toString());
    } catch (Exception e){
      logger.error(e.getMessage());
    } finally{
      session.close();
    }
    return message;
  }

  @Override
  public boolean isTest() {
    return isTest;
  }

  @Override
  public List<Message> getUserMessages(String username) {
    List<Message> userMessages;
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    String strQuery = "SELECT m FROM Message m  WHERE m.toUser = :username";
    Query query = session.createQuery(strQuery);
    query.setParameter("username", username);
    userMessages = query.getResultList();
    session.close();
    logger.info(userMessages.size() + " messages for User " +username + " found.");
    return userMessages;
  }

  /**
   * Gets all direct messages from sender to user.
   *
   * @param user   - receiver of messages
   * @param sender - sender of messages
   * @return - list of Message objects
   */
  @Override
  public List<Message> getDirectMessages(String user, String sender) {
    List<Message> userMessages;
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    String strQuery = "SELECT m FROM Message m WHERE m.toUser = :inq AND m.fromUser = :sender";
    Query query = session.createQuery(strQuery);
    query.setParameter("inq", user);
    query.setParameter("sender", sender);
    userMessages = query.getResultList();
    session.close();
    return userMessages;
  }

  /**
   * Gets all messages to a group.
   *
   * @param user  - receiver of messages
   * @param group - the group they are a part of
   * @return - list of Message objects
   */
  @Override
  public List<Message> getGroupMessages(String user, String group) {
    List<Message> userMessages;
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    String strQuery = "SELECT m FROM Message m WHERE m.toUser = :inq AND m.fromUser LIKE ?1";
    Query query = session.createQuery(strQuery);
    query.setParameter("inq", user);
    query.setParameter(1, group + ":%");
    userMessages = query.getResultList();
    session.close();
    logger.info(userMessages.size() + " messages found for " + user + " from group "+ group);
    return userMessages;
  }

  /**
   * Gets all outgoing messages. This includes the messages sent as an alias and to groups.
   *
   * @param username - sender of messages
   * @return - list of messages
   */
  @Override
  public List<Message> getOutgoingMessages(String username) {
    List<Message> userMessages;
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    String strQuery = "SELECT m FROM Message m  WHERE m.fromUser = :username";
    Query query = session.createQuery(strQuery);
    query.setParameter("username", username);
    userMessages = query.getResultList();
    session.close();
    logger.info(userMessages.size() + " messages sent from " + username + " found.");
    return userMessages;
  }
}
