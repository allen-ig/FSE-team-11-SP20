package com.neu.prattle.service;

import com.neu.prattle.exceptions.GroupAlreadyPresentException;
import com.neu.prattle.exceptions.GroupDeletedException;
import com.neu.prattle.exceptions.SenderNotAuthorizedException;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

  @Test(expected = GroupDeletedException.class)
  public void testRemoveModerator() {
    User nU = new User("ThisIsANewUserRemoveModerator");
    userService.addUser(nU);
  
    Set<User> mems = new HashSet<>();
    mems.add(nU);
  
    BasicGroup newGroup = BasicGroup.groupBuilder().setName("ThisIsASecondGroupRemoveModerator").setMembers(mems).build();
    us.addGroup(newGroup);
    
    us.removeModerator(nU, nU, newGroup);
  }
  
  @Test(expected = SenderNotAuthorizedException.class)
  public void testExtendModerators() {
    User testU = new User("TestGroupsExtendModerators");
    User nU = new User("ThisIsANewUserExtendModerators");
    userService.addUser(testU);
    userService.addUser(nU);
  
    Set<User> members = new HashSet<>();
    members.add(nU);
    members.add(testU);
    Set<User> moderators = new HashSet<>();
    moderators.add(testU);
  
    BasicGroup newGroup = BasicGroup.groupBuilder().setName("ThisIsASecondGroupExtendModerators")
      .setMembers(members).setModerators(moderators).build();
    us.addGroup(newGroup);
    
    us.extendModerators(nU, nU, newGroup);
  }
  
  @Test
  public void testAddModeratorToMemberListGroup() {
    User testU = new User("TestGroups2");
    User nU = new User("ThisIsANewUserModeratorButNotMember");
    userService.addUser(testU);
    userService.addUser(nU);
  
    Set<User> members = new HashSet<>();
    members.add(nU);
    Set<User> moderators = new HashSet<>();
    moderators.add(testU);
  
    BasicGroup newGroup = BasicGroup.groupBuilder().setName("GroupName2")
      .setMembers(members).setModerators(moderators).build();
    us.addGroup(newGroup);
    
    Optional<BasicGroup> group = us.findGroupByName
      ("ThisIsANewUserModeratorButNotMember", "GroupName2");
    group.ifPresent(basicGroup -> assertTrue(basicGroup.hasMember(testU)));
    
    
  }
}
