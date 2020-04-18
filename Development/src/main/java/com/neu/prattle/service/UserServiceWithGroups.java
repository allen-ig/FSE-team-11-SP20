package com.neu.prattle.service;

import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;

import java.util.Optional;

/**
 * Handles CRUD DB operations for Group objects
 */
public interface UserServiceWithGroups {
  /**
   * Returns a Group that a User is a member of
   * @param user is the username of the User
   * @param groupName is the name of the Group
   * @return a BasicGroup if one is found, else Optional.Empty()
   */
  Optional<BasicGroup> findGroupByName(String user, String groupName);

  /**
   * Creates a new persistent Basicgroup
   * @param group is the BasicGroup
   */
  void addGroup(BasicGroup group);

  /**
   * Deletes a group is the sender is a moderator of the Group
   * @param sender is the User requesting to delete the Group
   * @param group is the Group to be deleted
   */
  void deleteGroup(User sender, BasicGroup group);

  /**
   * Adds a User to a Group
   * @param sender is the User adding a new member to the Group
   * @param user is the User to be added to the Group
   * @param group is the Group gaining a new member
   */
  void extendUsers(User sender, User user, BasicGroup group);

  /**
   * Adds a moderator to a Group
   * @param sender is the User adding a moderator to the group
   * @param user is the User being added as a moderator
   * @param group is the Group that is gaining a moderator
   */
  void extendModerators(User sender, User user, BasicGroup group);

  /**
   * Removes a User from a Group
   * @param sender is the User removing a member from the Group
   * @param user is the User to remove from the Group
   * @param group is the Group to remove the User from
   */
  void removeUser(User sender, User user, BasicGroup group);

  /**
   * Removes a moderator from a Group
   * @param sender is the User removing a moderator
   * @param user is the moderator to remove
   * @param group is the Group to remove a moderator from
   */
  void removeModerator(User sender, User user, BasicGroup group);
  
  /**
   * Returns value indicating whether UserService is configured for testing
   * @return true if configured for testing else false
   */
  boolean isTest();
}
