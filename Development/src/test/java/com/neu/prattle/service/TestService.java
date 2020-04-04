package com.neu.prattle.service;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.exceptions.UserNotFoundException;
import com.neu.prattle.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestService {
  
  private UserService us;
  
  @Before
  public void setUp() {
    System.setProperty("testing", "true");
    us = UserServiceImpl.getInstance();
    assertTrue(us.isTest());
  }
  
  @After
  public void tearDown() {
    System.setProperty("testing", "false");
  }
  
  @Test
  public void testGetNoneUser() {
    Optional<User> noneUser = us.findUserByName("ThisUserDoesntExist2");
    assertEquals(noneUser, Optional.empty());
  }
  
  @Test(expected = UserAlreadyPresentException.class)
  public void testAddUser() {
    us.addUser(new User("TestAddUser"));
    us.addUser(new User("TestAddUser"));
  }
  
  /**
   * Ensure that a non-testing UserServiceImpl that is configured to use
   * the actual DB is returned when System.getProperty("testing") = "false"
   */
  @Test
  public void testNonTestUserServiceImpl(){
    System.setProperty("testing", "false");
    UserService service = UserServiceImpl.getInstance();
    assertFalse(service.isTest());
  }
  
  @Test
  public void testSingleton(){
    UserService newUserService;
    newUserService = UserServiceImpl.getInstance();

    User test = new User("TestUser123");
    us.addUser(test);
    Optional<User> testGet = newUserService.findUserByName("TestUser123");
    assertTrue(testGet.isPresent());
    assertEquals(Optional.of(test), testGet);
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
