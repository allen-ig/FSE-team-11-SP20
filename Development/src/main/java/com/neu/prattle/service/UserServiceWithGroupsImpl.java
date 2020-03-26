package com.neu.prattle.service;

import com.neu.prattle.exceptions.GroupAlreadyPresentException;
import com.neu.prattle.exceptions.GroupNotFoundException;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

public class UserServiceWithGroupsImpl implements UserServiceWithGroups {
  
  private Configuration config = new Configuration().configure("hibernate.cfg.xml")
    .addAnnotatedClass(User.class).addAnnotatedClass(BasicGroup.class);
  private ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
  private SessionFactory sessionFactory = config.buildSessionFactory(registry);
  private boolean isTest;
  
  /***
   * UserServiceImpl is a Singleton class.
   */
  private UserServiceWithGroupsImpl() {  }
  
  private static UserServiceWithGroupsImpl accountService;
  private static UserServiceWithGroupsImpl testingUserService;
  
  static {
    accountService = new UserServiceWithGroupsImpl();
    accountService.isTest = false;
  }
  
  static {
    testingUserService = new UserServiceWithGroupsImpl();
    Configuration testingConfig = new Configuration().configure("testing-hibernate.cfg.xml").addAnnotatedClass(User.class).addAnnotatedClass(BasicGroup.class);
    testingUserService.config = testingConfig;
    ServiceRegistry testingRegistry = new StandardServiceRegistryBuilder().applySettings(testingConfig.getProperties()).build();
    testingUserService.registry = testingRegistry;
    testingUserService.sessionFactory = testingConfig.buildSessionFactory(testingRegistry);
    testingUserService.isTest = true;
  }
  
  /**
   * Call this method to return an instance of this service.
   *
   * @return this
   */
  public static UserServiceWithGroups getInstance() {
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
    }
    
    finally {
      session.disconnect();
      session.close();
    }
  }
  
  private Object findUserByNameQuery(String name, Session session) {
    String strQuery = "SELECT u FROM User u  WHERE u.name = :name";
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
      System.out.println(e.getMessage());
    } finally {
      session.disconnect();
      session.close();
    }
  }
  
  public synchronized void deleteUser(User user) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    try {
      session.delete(user);
      session.getTransaction().commit();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      session.disconnect();
      session.close();
    }
  }
  
  @Override
  public boolean isTest() {
    return isTest;
  }
  
  public Optional<BasicGroup> findGroupByName(String username, String groupName) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    
    try {
      BasicGroup result = (BasicGroup) findGroupByNameQuery(groupName, session);
//      check if user is part of the group
      return Optional.of(result);
    } catch (NoResultException ex) {
      return Optional.empty();
    } finally {
      session.disconnect();
      session.close();
    }
  }
  
  private Object findGroupByNameQuery(String groupName, Session session) {
    String strQuery = "SELECT g FROM BasicGroup g join fetch g.members join fetch g.moderators WHERE g.name = :name";
    Query query = session.createQuery(strQuery);
    query.setParameter("name", groupName);
    
    return query.getSingleResult();
  }
  
  /**
   * Adds a group to the system. If users in group do not exist, simply does not send to them.
   * If there are no moderators set, the user creating the group becomes the moderator.
   * Needs Doing: Check user existence. Add moderator support. Add other users in the group.
   * @param group - Group.class
   */
  @Override
  public void addGroup(BasicGroup group) {
    if(group.getMembers().isEmpty()) {
      throw new IllegalArgumentException(
        "The group must contain at least one member.");
    }
    
    if (findGroupByName(group.getMembers().iterator().next().getName(), group.getName()).isPresent()) {
      throw new GroupAlreadyPresentException(
        String.format("Group already present with name: %s", group.getName()));
    }
    
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    
    Set<User> updatedMembers = getUsersInDatabase(group.getMembers(), session);
    Set<User> updatedModerators = getUsersInDatabase(group.getModerators(), session);
    group.setMembers(updatedMembers);
    group.setModerators(updatedModerators);
    
    for(User member : group.getMembers()) {
      member.getGroups().add(group);
    }
    
    if(group.getModerators().isEmpty()) {
      group.getModerators().add(group.getMembers().iterator().next());
    }
    
    for(User moderator : group.getModerators()) {
      moderator.getModeratorFor().add(group);
    }
    
    try {
      session.saveOrUpdate(group);
      session.getTransaction().commit();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      session.disconnect();
      session.close();
    }
  }
  
  private Set<User> getUsersInDatabase(Set<User> usersToBeValidated, Session session) {
    Set<User> updatedUsers = new HashSet<>();
    for (User user : usersToBeValidated) {
      try {
        User userInDb = (User) findUserByNameQuery(user.getName(), session);
        updatedUsers.add(userInDb);
      } catch (NoResultException ex) {
        // log that the user did not exist in database
      }
    }
    
    return updatedUsers;
  }
  
  @Override
  public void addMembersToGroup(BasicGroup group) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    
    // check if group exists - how to pass caller?
    String strQuery = "SELECT g FROM BasicGroup g join fetch g.members join fetch g.moderators WHERE g.name = :name";
    Query query = session.createQuery(strQuery);
    query.setParameter("name", group.getName());
    
    BasicGroup result = (BasicGroup) query.getSingleResult();
    Optional<BasicGroup> op = Optional.of(result);
    
    if (!op.isPresent()) {
      throw new GroupNotFoundException("A group called " + group.getName() + " does not exist!");
    }
    
    BasicGroup groupInDatabase = op.get();
  
    Set<User> updatedMembers = getUsersInDatabase(group.getMembers(), session);
    Set<User> updatedModerators = getUsersInDatabase(group.getModerators(), session);
    group.setMembers(updatedMembers);
    group.setModerators(updatedModerators);
    
    if(!group.getMembers().isEmpty()) {
      groupInDatabase.getMembers().addAll(group.getMembers());
      for(User newMember : groupInDatabase.getMembers()) {
        newMember.getGroups().add(groupInDatabase);
      }
    }
    
    if(!group.getModerators().isEmpty()) {
      groupInDatabase.getModerators().addAll(group.getModerators());
      
      for(User newModerator : groupInDatabase.getModerators()) {
        newModerator.getModeratorFor().add(groupInDatabase);
      }
    }
    
    try {
      session.saveOrUpdate(groupInDatabase);
      session.getTransaction().commit();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      session.disconnect();
      session.close();
    }
    
    // update group?
  }
}

