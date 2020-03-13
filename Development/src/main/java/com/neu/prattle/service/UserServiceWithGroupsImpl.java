package com.neu.prattle.service;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class UserServiceWithGroupsImpl implements UserServiceWithGroups {
  /***
   * UserServiceImpl is a Singleton class.
   */
  private UserServiceWithGroupsImpl() {

  }

  private static UserServiceWithGroups accountService;

  static {
    accountService = new UserServiceWithGroupsImpl();
  }

  /**
   * Call this method to return an instance of this service.
   * @return this
   */
  public static UserServiceWithGroups getInstance() {
    return accountService;
  }

  private Set<User> userSet = new HashSet<>();
  private Map<User, Map<String, BasicGroup>> groupSet = new HashMap<>();

  /***
   *
   * @param name -> The name of the user.
   * @return An optional wrapper supplying the user.
   */
  @Override
  public Optional<User> findUserByName(String name) {
    final User user = new User(name);
    if (userSet.contains(user))
      return Optional.of(user);
    else
      return Optional.empty();
  }

  @Override
  public synchronized void addUser(User user) {
    if (userSet.contains(user))
      throw new UserAlreadyPresentException(String.format("User already present with name: %s", user.getName()));

    userSet.add(user);
  }

  @Override
  public Optional<BasicGroup> findGroupByName(String name, String group) {
    User user = new User(name);
    if (groupSet.containsKey(user) && groupSet.get(user).containsKey(group)) {
      return Optional.of(groupSet.get(user).get(group).copy());
    } else {
      return Optional.empty();
    }
  }

  /**
   * Adds a group to a user. If users in group do not exist, simply does not send to them.
   * Needs Doing: Check user existence. Add moderator support. Add other users in the group.
   * @param user - User.class
   * @param group - Group.class
   */
  @Override
  public void addGroup(User user, BasicGroup group) {
    if (groupSet.containsKey(user)) {
      if (groupSet.get(user).containsKey(group.getName())) {
        throw new UserAlreadyPresentException(String.format("Group already present with name: %s", group.getName()));
      } else {
        groupSet.get(user).put(group.getName(), group.copy());
      }
    } else {
      groupSet.put(user, new HashMap<>());
      groupSet.get(user).put(group.getName(), group.copy());
      //add something that communicates to other users.
    }
  }
}
