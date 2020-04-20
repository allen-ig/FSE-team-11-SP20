package com.neu.prattle.service;

import com.neu.prattle.exceptions.GroupAlreadyPresentException;
import com.neu.prattle.exceptions.GroupDeletedException;
import com.neu.prattle.exceptions.SenderNotAuthorizedException;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.main.HibernateUtil;
import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.NoResultException;

public class UserServiceWithGroupsImpl implements UserServiceWithGroups {
  
  private Logger logger = LogManager.getLogger();
  private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
  private boolean isTest;
  
  /***
   * UserServiceImpl is a Singleton class.
   */
  private UserServiceWithGroupsImpl() {  }
  
  private static UserServiceWithGroupsImpl accountService;
  private static UserServiceWithGroupsImpl testingUserService;
  private String group = "Group ";
  
  static {
    accountService = new UserServiceWithGroupsImpl();
    accountService.isTest = false;
  }
  
  static {
    testingUserService = new UserServiceWithGroupsImpl();
    testingUserService.sessionFactory =
        HibernateUtil.getTestSessionFactory();
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

  
  public Optional<BasicGroup> findGroupByName(String username, String groupName) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    
    try {
      BasicGroup result = (BasicGroup) findGroupByNameQuery(groupName, session);
      logger.info(group + groupName + " found");
      return Optional.of(result);
    } catch (NoResultException ex) {
      logger.info("No Group called " + groupName + " found");
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
   * @param group - Group.class
   */
  @Override
  public void addGroup(BasicGroup group) {
    if(group.getMembers().isEmpty()) {
      logger.error("A Group called " + group.getName()
              + " could not be created without any members");
      throw new IllegalArgumentException(
        "The group must contain at least one member.");
    }
    
    if (findGroupByName(group.getMembers().iterator().next().getName(), group.getName()).isPresent()) {
      String msg = String.format("Group already present with name: %s", group.getName());
      logger.error(msg);
      throw new GroupAlreadyPresentException(msg);
    }
    
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    
    Set<User> updatedMembers = getUsersInDatabase(group.getMembers(), session);
    Set<User> updatedModerators = getUsersInDatabase(group.getModerators(), session);
    updatedMembers.addAll(updatedModerators); //make sure mods are users as well

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
      if(!group.getMembers().contains(moderator)) {
        group.getMembers().add(moderator);
        moderator.getGroups().add(group);
      }
    }
    
    try {
      session.saveOrUpdate(group);
      session.getTransaction().commit();
      logger.info(group + group.getName()
              + " created with " + updatedMembers.size() + " members");
    } catch (Exception e) {
      logger.error(e.getMessage());
    } finally {
      session.disconnect();
      session.close();
    }
  }
  
  private Set<User> getUsersInDatabase(Set<User> usersToBeValidated, Session session) {
    Set<User> updatedUsers = new HashSet<>();
    for (User user : usersToBeValidated) {
      try {
        User userInDb = (User) UserServiceImpl.findUserByNameQuery(user.getName(), session);
        updatedUsers.add(userInDb);
      } catch (NoResultException ex) {
        logger.error(ex.getMessage());
      }
    }
    
    return updatedUsers;
  }
  
  /**
   * Deletes a group. Checks if the sender is a moderator in the group.
   * @param sender - User sender, stored at user login.
   * @param group - BasicGroup group to be removed.
   * @throws SenderNotAuthorizedException - If sender is not a moderator.
   */
  public synchronized void deleteGroup(User sender, BasicGroup group){
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    
    Set<User> mods = group.getModerators();
    if (!mods.contains(sender)) {
      throw new SenderNotAuthorizedException("You are not authorized to delete this group");
    }
    
    group.getMembers().clear();
    group.getModerators().clear();
    
    try {
      session.remove(group);
      session.getTransaction().commit();
      logger.info(group + group.getName() + " deleted");
    } catch (Exception e) {
      logger.error(e.getMessage());
    } finally {
      session.disconnect();
      session.close();
    }
  }
  
  /**
   * Adds a user.
   * Checks for existence have been performed by the controller. Checking if allowed, and persisting
   * change. Any member can add.
   * @param sender - User sender of message, stored at user login.
   * @param user - User to be added.
   * @param group - Basic group to be changed.
   * @throws SenderNotAuthorizedException - if sender is not a member.
   * @throws UserAlreadyPresentException - if user is already in the group.
   */
  public synchronized void extendUsers(User sender, User user, BasicGroup group) {
    //check if sender is member
    Set<User> members = group.getMembers();
    if (!members.contains(sender)) {
      throw new SenderNotAuthorizedException("Not allowed to extend the users of this group");
    }
    //check if user already member
    if (members.contains(user)) {
      throw new UserAlreadyPresentException("Group already has this user");
    }
    
    Session session = sessionFactory.openSession();
    session.beginTransaction();

    members.add(user);
    group.setMembers(members);

    try {
      session.saveOrUpdate(group);
      session.getTransaction().commit();
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new IllegalStateException("failed connecting to database");
    } finally {
      session.disconnect();
      session.close();
    }
  }
  
  /**
   * Adds a user.
   * Checks for existence have been performed by the controller. Checking if allowed, and persisting
   * change. Any Moderator can add. Adds user as member if not already.
   * @param sender - User sender of message, stored at user login.
   * @param user - User to be added.
   * @param group - Basic group to be changed.
   * @throws SenderNotAuthorizedException - if sender is not a moderator.
   * @throws UserAlreadyPresentException - if user is already a moderator.
   */
  public synchronized void extendModerators(User sender, User user, BasicGroup group) {
    //check if sender is member
    Set<User> members = group.getMembers();
    Set<User> moderators = group.getModerators();
    if (!moderators.contains(sender)) {
      throw new SenderNotAuthorizedException("Not allowed to extend the moderators of this group");
    }
    
    //check if user already member
    if (moderators.contains(user)) {
      throw new UserAlreadyPresentException("Group already has this moderator");
    }

    members.add(user);
    moderators.add(user);
    group.setMembers(members);
    group.setModerators(moderators);
    
    Session session = sessionFactory.openSession();
    session.beginTransaction();

    try {
      session.saveOrUpdate(group);
      session.getTransaction().commit();
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new UserAlreadyPresentException("Failed updating database");
    } finally {
      session.disconnect();
      session.close();
    }
  }
  
  /**
   * Removes a user.
   * Checks for existence have been performed by the controller. Checking if allowed, and persisting
   * change. Any Moderator can remove. Users can remove themselves.
   * @param sender - User sender of message, stored at user login.
   * @param user - User to be added.
   * @param group - Basic group to be changed.
   * @throws SenderNotAuthorizedException - if sender is not a moderator or deleted user.
   */
  public synchronized void removeUser(User sender, User user, BasicGroup group) {
    //check if sender is member
    Set<User> members = group.getMembers();
    Set<User> moderators = group.getModerators();

    if (!moderators.contains(sender) && !sender.getName().equals(user.getName())) {
      throw new SenderNotAuthorizedException("Not allowed to delete this user");
    }

    //check if deleting the last member
    if (members.size() == 1) {
      deleteGroup(sender, group);
      throw new GroupDeletedException("Last member removed, deleting");
    }
    
    //ok if not there
    members.remove(user);
    moderators.remove(user);
    
    group.setMembers(members);
    group.setModerators(moderators);
    
    Session session = sessionFactory.openSession();
    session.beginTransaction();

    try {
      session.saveOrUpdate(group);
      session.getTransaction().commit();
      logger.info("User " + user.getName() + " removed from group " + group.getName());
    } catch (Exception e) {
      logger.error(e.getMessage());
    } finally {
      session.disconnect();
      session.close();
    }
  }
  
  /**
   * Removes a moderator.
   * Checks for existence have been performed by the controller. Checking if allowed, and persisting
   * change. Can only remove if sender and moderator. If last moderator removed, group it deleted even if users are present.
   * @param sender - User sender of message, stored at user login.
   * @param user - User to be added.
   * @param group - Basic group to be changed.
   * @throws SenderNotAuthorizedException - if sender is not a moderator.
   */
  public synchronized void removeModerator(User sender, User user, BasicGroup group) {

    Set<User> moderators = group.getModerators();
    if ( !(moderators.contains(sender)) || !(moderators.contains(user)) ) {
      throw new SenderNotAuthorizedException("Not allowed to delete this moderator");
    }
    
    //check if deleting the last member
    if (moderators.size() == 1) {
      //already checked if moderator, so no errors
      deleteGroup(sender, group);
      throw new GroupDeletedException("Last moderator removed, deleting");
    }


    moderators.remove(user);
    group.setModerators(moderators);
    
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    try {
      session.saveOrUpdate(group);
      session.getTransaction().commit();
      logger.info("Moderator " + user.getName() + " removed from " + group.getName());
    } catch (Exception e) {
      logger.error(e.getMessage());
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