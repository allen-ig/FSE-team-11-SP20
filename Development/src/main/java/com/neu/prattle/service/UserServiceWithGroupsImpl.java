package com.neu.prattle.service;

import com.neu.prattle.exceptions.GroupAlreadyPresentException;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;

import java.util.Optional;

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
    String strQuery = "SELECT u FROM User u  WHERE u.name = :name";
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
    String strQuery = "SELECT g FROM BasicGroup g join fetch g.members join fetch g.moderators WHERE g.name = :name";
    Query query = session.createQuery(strQuery);
    query.setParameter("name", groupName);
  
    try {
      BasicGroup result = (BasicGroup) query.getSingleResult();
      
//      check if user is part of the group
      return Optional.of(result);
    } catch (NoResultException ex) {
      return Optional.empty();
    } finally {
      session.disconnect();
      session.close();
    }
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
    
    for(User member : group.getMembers()) {
      member.getGroups().add(group);
    }
    
    if(group.getModerators().isEmpty()) {
      group.getModerators().add(group.getMembers().iterator().next());
    }
    
    for(User moderator : group.getModerators()) {
      moderator.getModeratorFor().add(group);
    }
    
    // delete after discussing expected behavior
  
//    Set<User> members = new HashSet<>();
//
//    for(User member : group.getMembers()) {
//      Optional<User> op = findUserByName(member.getName());
//      if(op.isPresent()) {
//        User user = op.get();
//        user.getGroups().add(group);
//        members.add(op.get());
//      }
//    }
//
//    group.setMembers(members);
//
//    Set<User> moderators = new HashSet<>();
//    if(group.getModerators().isEmpty()) {
//      Optional<User> op = findUserByName(group.getMembers().iterator().next().getName());
//      if(op.isPresent()) {
//        User user = op.get();
//        user.getModeratorFor().add(group);
//        moderators.add(user);
//      }
//    } else {
//      for(User moderator : group.getModerators()) {
//        Optional<User> op = findUserByName(moderator.getName());
//        if(op.isPresent()) {
//          User user = op.get();
//          Set<BasicGroup> groups = user.getGroups();
//          groups.add(group);
//          user.setModeratorFor(groups);
//        }
//      }
//    }
//    group.setModerators(moderators);
    
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
}

