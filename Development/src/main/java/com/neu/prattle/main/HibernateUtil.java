package com.neu.prattle.main;

import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.Friend;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
  private static SessionFactory sessionFactory;
  private static SessionFactory testSessionFactory;
  private static final String TEST_HIBERNATE_CONFIG = "testing-hibernate.cfg.xml";
  private static final String HIBERNATE_CONFIG = "hibernate.cfg.xml";
  
  private HibernateUtil() {
  
  }
  
  public static SessionFactory getSessionFactory() {
    if (sessionFactory == null) {
      // loads configuration and mappings
      Configuration configuration = new Configuration().configure(HIBERNATE_CONFIG)
        .addAnnotatedClass(User.class).addAnnotatedClass(BasicGroup.class)
        .addAnnotatedClass(Friend.class).addAnnotatedClass(Message.class);
      ServiceRegistry serviceRegistry
        = new StandardServiceRegistryBuilder()
        .applySettings(configuration.getProperties()).build();
      
      // builds a session factory from the service registry
      sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }
    
    return sessionFactory;
  }
  
  public static SessionFactory getTestSessionFactory() {
    if (testSessionFactory == null) {
      // loads configuration and mappings
      Configuration configuration = new Configuration().configure(TEST_HIBERNATE_CONFIG)
        .addAnnotatedClass(User.class).addAnnotatedClass(BasicGroup.class)
        .addAnnotatedClass(Friend.class).addAnnotatedClass(Message.class);
      ServiceRegistry serviceRegistry
        = new StandardServiceRegistryBuilder()
        .applySettings(configuration.getProperties()).build();
      
      // builds a session factory from the service registry
      testSessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }
    
    return testSessionFactory;
  }
}
