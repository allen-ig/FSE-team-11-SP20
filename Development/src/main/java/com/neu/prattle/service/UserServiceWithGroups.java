package com.neu.prattle.service;

import com.neu.prattle.model.BasicGroup;
import java.util.Optional;

public interface UserServiceWithGroups extends UserService {
  Optional<BasicGroup> findGroupByName(String user, String groupName);

  void addGroup(BasicGroup group);
  
  void addMembersToGroup(BasicGroup group);
}
