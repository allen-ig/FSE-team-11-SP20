package com.neu.prattle.service;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.exceptions.UserNotFoundException;
import com.neu.prattle.main.HibernateUtil;
import com.neu.prattle.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

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
  public String getUserStatus(String username) {
    for(User u : userSet) {
      if(u.getName().equals(username)) {
        return u.getStatus();
      }
    }
    
    throw new UserNotFoundException("User not found");
  }
  
  @Override
  public void setUserStatus(String username, String status) {
    for(User user : userSet) {
      if(user.getName().equals(username)) {
        user.setStatus(status);
        return;
      }
    }
    
    throw new UserNotFoundException("User not found");
  }

  @Override
  public boolean isTest() {
    return isTest;
  }
}
