package com.neu.prattle.testservice;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.User;
import java.util.Optional;

import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import javax.swing.text.html.Option;
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


}
