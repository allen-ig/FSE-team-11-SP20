package com.neu.prattle.service;

import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;

import java.util.Optional;

public interface UserServiceWithGroups {
  Optional<BasicGroup> findGroupByName(String user, String groupName);

  void addGroup(BasicGroup group);

  //if moderator
  void deleteGroup(User sender, BasicGroup group);

  //if member
  void extendUsers(User sender, User user, BasicGroup group);

  //if moderator
  void extendModerators(User sender, User user, BasicGroup group);

  //if user in request or moderator
  void removeUser(User sender, User user, BasicGroup group);

  //if user in request and moderator
  void removeModerator(User sender, User user, BasicGroup group);
  
  /**
   * Returns value indicating whether UserService is configured for testing
   * @return true if configured for testing else false
   */
  boolean isTest();
}
