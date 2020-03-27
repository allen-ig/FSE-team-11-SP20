package com.neu.prattle.testservice;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.neu.prattle.exceptions.GroupAlreadyPresentException;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import com.neu.prattle.service.UserServiceWithGroups;
import com.neu.prattle.service.UserServiceWithGroupsImpl;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestUserServiceWithGroups {

  private UserServiceWithGroups us;
  private UserService userService;

  @Before
  public void setUp() {
    System.setProperty("testing", "true");
    us = UserServiceWithGroupsImpl.getInstance();
    userService = UserServiceImpl.getInstance();
    assertTrue(us.isTest());
    assertTrue(userService.isTest());
  }

  @After
  public void tearDown() {
    System.setProperty("testing", "false");
  }

  @Test
  public void testSingleton(){
    UserServiceWithGroups newUserService;
    newUserService = UserServiceWithGroupsImpl.getInstance();

    User testU = new User("TestGroups");
    Set<User> members = new HashSet<>();
    members.add(testU);
    Set<User> moderators = new HashSet<>();
    moderators.add(testU);

    BasicGroup testG = new BasicGroup.GroupBuilder().setName("Test").setMembers(members)
      .setModerators(moderators).build();
    userService.addUser(testU);
    us.addGroup(testG);

    Optional<User> testGet = userService.findUserByName("TestGroups");
    Optional<BasicGroup> testGetG = newUserService.findGroupByName(testU.getName(), testG.getName());
    assertTrue(testGet.isPresent());
    assertTrue(testGetG.isPresent());
    assertEquals(Optional.of(testU), testGet);
    assertEquals(Optional.of(testG), testGetG);
  }

  @Test
  public void testGetNoneUser(){
    Optional<User>noneUser = userService.findUserByName("ThisUserDoesntExist");
    assertEquals(noneUser, Optional.empty());
  }

  @Test
  public void testGetNoneGroup(){
    Optional<BasicGroup>noneGroup = us.findGroupByName("ThisUserDoesntExist", "ThisGroupDoersNotExist");
    assertEquals(noneGroup, Optional.empty());
    userService.addUser(new User("ThisUserDoesntExist"));
    noneGroup = us.findGroupByName("ThisUserDoesntExist", "ThisGroupDoersNotExist");
    assertEquals(noneGroup, Optional.empty());

  }

  @Test (expected = UserAlreadyPresentException.class)
  public void testAddUser(){
    userService.addUser(new User("TestAddUser"));
    userService.addUser(new User("TestAddUser"));
  }

  @Test
  public void deleteUser() {
    userService.addUser(new User("testDelete"));
    Optional<User> found = userService.findUserByName("testDelete");
    userService.deleteUser(found.get());
    userService.deleteUser(new User("newUserForDelete"));
  }


  @Test(expected = GroupAlreadyPresentException.class)
  public void testAddGroup(){
    User nU = new User("ThisIsANewUser");
    userService.addUser(nU);
  
    Set<User> mems = new HashSet<>();
    mems.add(nU);

    us.addGroup(BasicGroup.groupBuilder().setName("ThisIsANewGroup").setMembers(mems).build());
    us.addGroup(BasicGroup.groupBuilder().setName("ThisIsANewGroup").setMembers(mems).build());
  }

  @Test
  public void testAddSecondGroup(){
    User secondTimeWeAreUsingThisUser = new User("ThisIsAnEvenNewerUser");
    userService.addUser(secondTimeWeAreUsingThisUser);
  
    Set<User> mems = new HashSet<>();
    mems.add(secondTimeWeAreUsingThisUser);

    BasicGroup newGroup = BasicGroup.groupBuilder().setName("ThisIsASecondGroup").setMembers(mems).build();
    us.addGroup(newGroup);
    Optional<BasicGroup> found = us.findGroupByName(secondTimeWeAreUsingThisUser.getName(), newGroup.getName());
    assertTrue(found.isPresent());
    assertEquals(Optional.of(newGroup).get(), found.get());
  }


}
