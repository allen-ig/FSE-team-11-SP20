package com.neu.prattle.testservice;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.neu.prattle.exceptions.GroupAlreadyPresentException;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserServiceWithGroups;
import com.neu.prattle.service.UserServiceWithGroupsImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestUserServiceWithGroups {

  private UserServiceWithGroups us;

  @Before
  public void setUp() {
    System.setProperty("testing", "true");
    us = UserServiceWithGroupsImpl.getInstance();
    assertTrue(us.isTest());
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
    List<User> members = new ArrayList<>();
    members.add(testU);
    List<User> moderators = new ArrayList<>();
    moderators.add(testU);

    BasicGroup testG = new BasicGroup.GroupBuilder().setName("Test").setMembers(members)
      .setModerators(moderators).build();
    us.addUser(testU);
    us.addGroup(testG);

    Optional<User> testGet = newUserService.findUserByName("TestGroups");
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

  @Test(expected = GroupAlreadyPresentException.class)
  public void testAddGroup(){
    User nU = new User("ThisIsANewUser");
    us.addUser(nU);

    List<User> mems = new ArrayList<>();
    mems.add(nU);

    us.addGroup(BasicGroup.groupBuilder().setName("ThisIsANewGroup").setMembers(mems).build());
    us.addGroup(BasicGroup.groupBuilder().setName("ThisIsANewGroup").setMembers(mems).build());
  }

  @Test
  public void testAddSecondGroup(){
    User secondTimeWeAreUsingThisUser = new User("ThisIsAnEvenNewerUser");
    us.addUser(secondTimeWeAreUsingThisUser);

    List<User> mems = new ArrayList<>();
    mems.add(secondTimeWeAreUsingThisUser);

    BasicGroup newGroup = BasicGroup.groupBuilder().setName("ThisIsASecondGroup").setMembers(mems).build();
    us.addGroup(newGroup);
    Optional<BasicGroup> found = us.findGroupByName(secondTimeWeAreUsingThisUser.getName(), newGroup.getName());
    assertTrue(found.isPresent());
    assertEquals(Optional.of(newGroup).get(), found.get());
  }


}
