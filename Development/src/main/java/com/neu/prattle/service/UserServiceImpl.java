package com.neu.prattle.service;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.Friend;
import com.neu.prattle.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.NoResultException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/***
 * Implementation of {@link UserService}
 *
 * It stores the user accounts in a MYSQL database
 * It is capable of storing data in an actual database or a temporary,
 * im-memory database that will be deleted upon completion of code execution
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */
public class UserServiceImpl implements UserService {
  
  
  private Configuration config = new Configuration().configure("hibernate.cfg.xml")
    .addAnnotatedClass(User.class).addAnnotatedClass(BasicGroup.class).addAnnotatedClass(Friend.class);
  private ServiceRegistry registry = new StandardServiceRegistryBuilder()
      .applySettings(config.getProperties()).build();

  private SessionFactory sessionFactory = config.buildSessionFactory(registry);
  private boolean isTest;
  private Logger logger = Logger.getLogger(this.getClass().getName());

  /***
   * UserServiceImpl is a Singleton class.
   */
  private UserServiceImpl() {
  }


  private static UserServiceImpl accountService;
  private static UserServiceImpl testingUserService;

  static {
    accountService = new UserServiceImpl();
    accountService.isTest = false;
  }

  static {
    testingUserService = new UserServiceImpl();
    Configuration testingConfig = new Configuration().configure("testing-hibernate.cfg.xml")
        .addAnnotatedClass(User.class).addAnnotatedClass(BasicGroup.class).addAnnotatedClass(Friend.class);
    testingUserService.config = testingConfig;
    ServiceRegistry testingRegistry = new StandardServiceRegistryBuilder()
        .applySettings(testingConfig.getProperties()).build();
    testingUserService.registry = testingRegistry;
    testingUserService.sessionFactory = testingConfig.buildSessionFactory(testingRegistry);
    testingUserService.isTest = true;
  }

  /**
   * Call this method to return an instance of this service.
   *
   * @return this
   */
  public static UserService getInstance() {
    try {
      if (System.getProperty("testing").equals("true")) {
        return testingUserService;
      }
    } catch (NullPointerException e) {
      return accountService;
    }
    return accountService;
  }

  /***
   *
   * @param name -> The name of the user.
   * @return An optional wrapper supplying the User if it exists empty if it does not.
   */
  @Override
  public Optional<User> findUserByName(String name) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    String strQuery = "SELECT u FROM User u WHERE u.name = :name";
    Query query = session.createQuery(strQuery);
    query.setParameter("name", name);
    try {
      User result = (User) query.getSingleResult();
      return Optional.of(result);
    } catch (NoResultException ex) {
      return Optional.empty();
    } finally {
      session.disconnect();
      session.close();
    }
  }

  @Override
  public synchronized void addUser(User user) {
    if (findUserByName(user.getName()).isPresent()) {
      throw new UserAlreadyPresentException(
          String.format("User already present with name: %s", user.getName()));
    }
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    try {
      session.save(user);
      session.getTransaction().commit();
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage());
    } finally {
      session.disconnect();
      session.close();
    }
  }

    public synchronized void deleteUser(User user){
      Session session = sessionFactory.openSession();
      session.beginTransaction();
      try{
        session.delete(user);
        session.getTransaction().commit();
      } catch(Exception e){
        logger.log(Level.SEVERE,(e.getMessage()));
      } finally{
        session.disconnect();
        session.close();
      }
    }


  @Override
  public boolean isTest() {
    return isTest;
  }
}
