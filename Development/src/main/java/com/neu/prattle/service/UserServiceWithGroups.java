package com.neu.prattle.service;

import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;
import java.util.Optional;

public interface UserServiceWithGroups extends UserService {
  Optional<BasicGroup> findGroupByName(String user, String name);

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


}
