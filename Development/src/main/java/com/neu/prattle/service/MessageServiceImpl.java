package com.neu.prattle.service;

import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import java.util.List;

import javax.persistence.NoResultException;

public class MessageServiceImpl implements MessageService {

  private Configuration config = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Message.class);
  private ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
  private SessionFactory sessionFactory = config.buildSessionFactory(registry);
  private boolean isTest;

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
    Configuration testingConfig = new Configuration().configure("testing-hibernate.cfg.xml").addAnnotatedClass(Message.class);
    testingMessageService.config = testingConfig;
    ServiceRegistry testingRegistry = new StandardServiceRegistryBuilder().applySettings(testingConfig.getProperties()).build();
    testingMessageService.registry = testingRegistry;
    testingMessageService.sessionFactory = testingConfig.buildSessionFactory(testingRegistry);
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
  public List<Message> getUserMessages(User user) {
    return null;
  }

  @Override
  public void deleteMessage(Message message) {

  }

  @Override
  public Message createMessage(Message message) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    try {
      session.save(message);
      session.getTransaction().commit();
    } catch (Exception e){
      System.out.println(e.getMessage());
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
    return userMessages;
  }
}
