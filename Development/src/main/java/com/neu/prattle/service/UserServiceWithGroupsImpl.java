package com.neu.prattle.service;

import com.neu.prattle.exceptions.GroupAlreadyPresentException;
import com.neu.prattle.exceptions.GroupDeletedException;
import com.neu.prattle.exceptions.SenderNotAuthorizedException;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.main.HibernateUtil;
import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class UserServiceWithGroupsImpl implements UserServiceWithGroups {
  
  private Logger logger = Logger.getLogger(this.getClass().getName());
  private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
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
  
  private Object findUserByNameQuery(String name, Session session) {
    String strQuery = "SELECT u FROM User u WHERE u.name = :name";
    Query query = session.createQuery(strQuery);
    query.setParameter("name", name);
    
    return query.getSingleResult();
  }
  
  public Optional<BasicGroup> findGroupByName(String username, String groupName) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    
    try {
      BasicGroup result = (BasicGroup) findGroupByNameQuery(groupName, session);

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
      if(!group.getMembers().contains(moderator)) {
        group.getMembers().add(moderator);
        moderator.getGroups().add(group);
      }
    }
    
    try {
      session.saveOrUpdate(group);
      session.getTransaction().commit();
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage());
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
        logger.log(Level.SEVERE, ex.getMessage());
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
  public synchronized void deleteGroup(User sender, BasicGroup group) throws SenderNotAuthorizedException {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    
    Set<User> mods = group.getModerators();
    if (!mods.contains(sender)) {
      throw new SenderNotAuthorizedException("You are not authorized to delete this message");
    }
    
    group.getMembers().clear();
    group.getModerators().clear();
    
    try {
      session.remove(group);
      session.getTransaction().commit();
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage());
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
  public synchronized void extendUsers(User sender, User user, BasicGroup group) throws SenderNotAuthorizedException, UserAlreadyPresentException {
    //check if sender is member
    Set<User> members = group.getMembers();
    if (!members.contains(sender)) {
      throw new SenderNotAuthorizedException("Not allowed to extend the users of this group");
    }
    //check if user already member
    if (members.contains(user)) {
      throw new UserAlreadyPresentException("Group already has this user");
    }
    
    members.add(user);
    group.setMembers(members);
    
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    try {
      session.saveOrUpdate(group);
      session.getTransaction().commit();
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage());
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
  public synchronized void extendModerators(User sender, User user, BasicGroup group) throws SenderNotAuthorizedException, UserAlreadyPresentException {
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
    
    //no need to check if already added for set.
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
      logger.log(Level.SEVERE, e.getMessage());
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
  public synchronized void removeUser(User sender, User user, BasicGroup group) throws SenderNotAuthorizedException {
    //check if sender is member
    Set<User> members = group.getMembers();
    Set<User> moderators = group.getModerators();

    if (!moderators.contains(sender) && !sender.getName().equals(user.getName())) {
      throw new SenderNotAuthorizedException("Not allowed to delete this user");
    }
    
    //ok if not there
    members.remove(user);
    moderators.remove(user);
    
    //check if deleting the last member
    if (members.isEmpty() || moderators.isEmpty()) {
      //already checked if moderator, so no errors
      deleteGroup(sender, group);
      throw new GroupDeletedException(group.getName());
    }
    
    group.setMembers(members);
    group.setModerators(moderators);
    
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    try {
      session.saveOrUpdate(group);
      session.getTransaction().commit();
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage());
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
  public synchronized void removeModerator(User sender, User user, BasicGroup group) throws SenderNotAuthorizedException {
    //check if sender is member
    Set<User> moderators = group.getModerators();
    if (!(moderators.contains(sender) || sender == user)) {
      throw new SenderNotAuthorizedException("Not allowed to delete this user");
    }
    
    //ok if not there
    moderators.remove(user);
    
    //check if deleting the last member
    if (moderators.isEmpty()) {
      //already checked if moderator, so no errors
      deleteGroup(sender, group);
      return;
    }
    
    group.setModerators(moderators);
    
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    try {
      session.saveOrUpdate(group);
      session.getTransaction().commit();
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage());
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