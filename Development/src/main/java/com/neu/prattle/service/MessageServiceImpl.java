package com.neu.prattle.service;

import com.neu.prattle.main.HibernateUtil;
import com.neu.prattle.model.Message;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageServiceImpl implements MessageService {
  
  private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
  private boolean isTest;
  private Logger logger = LogManager.getLogger();

  /**
   * MessageServiceImpl is a "Singleton" class
   */
  private MessageServiceImpl(){}

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

  public static MessageService getInstance(){
    if (System.getProperty("testing") == null){
      return messageService;
    } else if (System.getProperty("testing").equals("true")){
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
      logger.info("Message " + message.getId() + " created.");
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
}
