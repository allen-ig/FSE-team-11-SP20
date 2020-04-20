package com.neu.prattle.service;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.exceptions.UserNotFoundException;
import com.neu.prattle.main.HibernateUtil;
import com.neu.prattle.model.User;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.NoResultException;

/***
 * Implementation of {@link UserService}
 *
 * It stores the userStr accounts in a MYSQL database
 * It is capable of storing data in an actual database or a temporary,
 * im-memory database that will be deleted upon completion of code execution
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */
public class UserServiceImpl implements UserService {
  
  private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
  private boolean isTest;
  private Logger logger = LogManager.getLogger();
  private String userStr = "User ";
  private String onlineStr = "online";

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
    testingUserService.sessionFactory = HibernateUtil.getTestSessionFactory();
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
   * @param name -> The name of the userStr.
   * @return An optional wrapper supplying the User if it exists empty if it does not.
   */
  @Override
  public Optional<User> findUserByName(String name) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    try {
      User result = (User) findUserByNameQuery(name, session);
      return Optional.of(result);
    } catch (NoResultException ex) {
      logger.warn(userStr + name + " was searched for, but does not exist");
      return Optional.empty();
    } finally {
      session.disconnect();
      session.close();
    }
  }

  @Override
  public Optional<User> findUserByNameWithGroups(String name) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    try {
      User result = (User) findUserByNameQuery(name, session);
      Hibernate.initialize(result.getGroups());
      return Optional.of(result);
    } catch (NoResultException ex) {
      return Optional.empty();
    } finally {
      session.disconnect();
      session.close();
    }
  }

  /**
   * A helper method to generate a Query to find a User by username
   * @param name is the username of the user
   * @param session is a Session
   * @return the query result
   */
  static Object findUserByNameQuery(String name, Session session) {
    String strQuery = "SELECT u FROM User u WHERE u.name = :name";
    Query query = session.createQuery(strQuery);
    query.setParameter("name", name);
    return query.getSingleResult();
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
      logger.info(user + user.getName() + " was added");
    } catch (Exception e) {
      logger.error(e.getMessage());
    } finally {
      session.disconnect();
      session.close();
    }
  }

   @Override
    public synchronized void deleteUser(User user){
      Session session = sessionFactory.openSession();
      session.beginTransaction();
      try{
        session.delete(user);
        session.getTransaction().commit();
        logger.info(user + user.getName() + " was deleted.");
      } catch(Exception e){
        logger.error(e.getMessage());
      } finally{
        session.disconnect();
        session.close();
      }
    }

  @Override
  public String getUserStatus(String username) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();

    try {
      User result = (User) findUserByNameQuery(username, session);
      return result.getStatus();
    } catch (NoResultException e) {
      throw new UserNotFoundException("User not found");
    } finally {
      session.disconnect();
      session.close();
    }
  }

  @Override
  public void setUserStatus(String username, String status) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();

    try {
      User user = (User) findUserByNameQuery(username, session);
      user.setStatus(status);
      session.saveOrUpdate(user);
      session.getTransaction().commit();
      logger.info(user + username + " updated their status to " + status);
    } catch (NoResultException e) {
      throw new UserNotFoundException("User could not be found");
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw e;
    } finally {
      session.disconnect();
      session.close();
    }
  }

  /**
   * Returns first 500 users onlineStr.
   * @return
   */
  @Override
  public List<User> getAllUsersOnline(int maxResults) {
    List<User> online;
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    String strQuery = "SELECT u FROM User u  WHERE u.isOnline = :" + this.onlineStr;
    Query query = session.createQuery(strQuery).setFirstResult(0).setMaxResults(maxResults);
    query.setParameter(this.onlineStr, this.onlineStr);
    online = query.getResultList();
    session.close();
    return online;
  }

  @Override
  public void setUserIsOnline(String username, boolean isOnline){
    Session session = sessionFactory.openSession();
    session.beginTransaction();

    try {
      User user = (User) findUserByNameQuery(username, session);
      user.setIsOnline(isOnline ? this.onlineStr : "offline");
      session.saveOrUpdate(user);
      session.getTransaction().commit();
    } catch (NoResultException e) {
      throw new UserNotFoundException("User not found");
    } catch (Exception e) {
      throw e;
    } finally {
      session.disconnect();
      session.close();
    }
  }

  @Override
  public boolean isTest() {
    return isTest;
  }
}
