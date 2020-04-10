package com.neu.prattle.service;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.exceptions.UserNotFoundException;
import com.neu.prattle.main.HibernateUtil;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.NoResultException;

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
  
  private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
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
   * @param name -> The name of the user.
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
    } catch (NoResultException e) {
      throw new UserNotFoundException("User not found");
    } catch (Exception e) {
      throw e;
    } finally {
      session.disconnect();
      session.close();
    }
  }

  /**
   * Returns first 500 users online.
   * @return
   */
  @Override
  public List<User> getAllUsersOnline(int maxResults) {
    List<User> online;
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    String strQuery = "SELECT u FROM User u  WHERE u.isOnline = :online";
    Query query = session.createQuery(strQuery).setFirstResult(0).setMaxResults(maxResults);
    query.setParameter("online", "online");
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
      user.setIsOnline(isOnline ? "online" : "offline");
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
