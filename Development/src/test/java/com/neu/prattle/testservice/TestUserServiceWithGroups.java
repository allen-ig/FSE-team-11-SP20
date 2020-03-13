package com.neu.prattle.testservice;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserServiceWithGroups;
import com.neu.prattle.service.UserServiceWithGroupsImpl;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class TestUserServiceWithGroups {

  private UserServiceWithGroups us;

  @Before
  public void setUp(){
    us = UserServiceWithGroupsImpl.getInstance();
  }

  @Test
  public void testSingleton(){
    UserServiceWithGroups newUserService;
    newUserService = UserServiceWithGroupsImpl.getInstance();

    User testU = new User("Test");
    BasicGroup testG = new BasicGroup.GroupBuilder().setName("Test").build();
    us.addUser(testU);
    us.addGroup(testU, testG);

    Optional<User> testGet = newUserService.findUserByName("Test");
    Optional<BasicGroup> testGetG = newUserService.findGroupByName(testU.getName(), testG.getName());
    assertTrue(testGet.isPresent());
    assertTrue(testGetG.isPresent());
    assertEquals(Optional.of(testU), testGet);
    assertEquals(Optional.of(testG), testGetG);
  }

  @Test
  public void testGetNoneUser(){
    Optional<User>noneUser = us.findUserByName("ThisUserDoesntExist");
    assertEquals(noneUser, Optional.empty());
  }

  @Test
  public void testGetNoneGroup(){
    Optional<BasicGroup>noneGroup = us.findGroupByName("ThisUserDoesntExist", "ThisGroupDoersNotExist");
    assertEquals(noneGroup, Optional.empty());
    us.addUser(new User("ThisUserDoesntExist"));
    noneGroup = us.findGroupByName("ThisUserDoesntExist", "ThisGroupDoersNotExist");
    assertEquals(noneGroup, Optional.empty());

  }

  @Test (expected = UserAlreadyPresentException.class)
  public void testAddUser(){
    us.addUser(new User("TestAddUser"));
    us.addUser(new User("TestAddUser"));
  }

  @Test(expected = UserAlreadyPresentException.class)
  public void testAddGroup(){
    us.addGroup(new User("ThisIsANewUser"), BasicGroup.groupBuilder().setName("ThisIsANewGroup").build());
    us.addGroup(new User("ThisIsANewUser"), BasicGroup.groupBuilder().setName("ThisIsANewGroup").build());
  }

  @Test
  public void testAddSecondGroup(){
    User secondTimeWereUsingthisUser = new User("ThisIsANewUser");
    BasicGroup newGroup = BasicGroup.groupBuilder().setName("ThisIsASecondGroup").build();
    us.addGroup(secondTimeWereUsingthisUser, newGroup);
    Optional<BasicGroup> found = us.findGroupByName(secondTimeWereUsingthisUser.getName(), newGroup.getName());
    assertTrue(found.isPresent());
    assertEquals(Optional.of(newGroup).get(), found.get());
  }


}
