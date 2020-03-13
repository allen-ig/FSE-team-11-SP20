package com.neu.prattle.service;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.exceptions.UserNotFoundException;
import com.neu.prattle.model.User;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class TestService {

  private UserService us;

  @Before
  public void setUp(){
    us = UserServiceImpl.getInstance();
  }

  @Test
  public void testSingleton(){
    UserService newUserService;
    newUserService = UserServiceImpl.getInstance();

    User test = new User("Test");
    us.addUser(test);
    Optional<User> testGet = newUserService.findUserByName("Test");
    assertTrue(testGet.isPresent());
    assertEquals(Optional.of(test), testGet);
  }

  @Test
  public void testGetNoneUser(){
    Optional<User>noneUser = us.findUserByName("ThisUserDoesntExist");
    assertEquals(noneUser, Optional.empty());
  }

  @Test (expected = UserAlreadyPresentException.class)
  public void testAddUser(){
    us.addUser(new User("TestAddUser"));
    us.addUser(new User("TestAddUser"));
  }
  
  @Test
  public void testSetUserStatus() {
    User test = new User("User2");
    us.addUser(test);
    
    us.setUserStatus("User2", "Hello world");
    assertEquals("Hello world", us.getUserStatus("User2"));
  }
  
  @Test(expected = UserNotFoundException.class)
  public void testSetUserStatusUserDoesNotExist() {
    us.setUserStatus("User3", "Hello world");
  }
  
  @Test
  public void testGetUserStatus() {
    User test = new User("User4");
    us.addUser(test);
    
    assertEquals("", us.getUserStatus("User4"));
    us.setUserStatus("User4", "Hello world");
    assertEquals("Hello world", us.getUserStatus("User4"));
  }
  
  @Test(expected = UserNotFoundException.class)
  public void testGetUserStatusUserDoesNotExist() {
    us.getUserStatus("Test2");
  }
}
