package com.neu.prattle.service;

import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;
import java.util.Optional;

public interface UserServiceWithGroups extends UserService {
  Optional<BasicGroup> findGroupByName(String user, String name);

  void addGroup(User user, BasicGroup group);
}
