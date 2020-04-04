package com.neu.prattle.controller;

import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestController {
  
  private UserService us;
  private UserController uc;
  private User newUser;
  
  @Before
  public void setUp() {
    System.setProperty("testing", "true");
    us = UserServiceImpl.getInstance();
    assertTrue(us.isTest());
    uc = new UserController();
    newUser = new User("TEST_USER_2");
  }
  
  @After
  public void tearDown(){
    User user = us.findUserByName("TEST_USER_2").get();
    us.deleteUser(user);
    System.setProperty("testing", "false");
  }
  
  @Test
  public void testUserStatusGetAndUpdate() {
    uc.createUserAccount(newUser);
    
    User user = new User("User_Status_Update_Success");
    uc.createUserAccount(user);
    Response response = uc.getUserStatus("User_Status_Update_Success");
    assertEquals(response.getStatus(), Response.ok().build().getStatus());
    assertEquals("{\"status\":\"\"}", response.readEntity(String.class));
    
    user.setStatus("Hello World");
    response = uc.updateUserStatus(user);
    assertEquals(response.getStatus(), Response.ok().build().getStatus());
  
    response = uc.getUserStatus("User_Status_Update_Success");
    assertEquals(response.getStatus(), Response.ok().build().getStatus());
    assertEquals("{\"status\":\"Hello World\"}", response.readEntity(String.class));
  }
  
  @Test
  public void testUserStatusUpdateError() {
    uc.createUserAccount(newUser);
    
    Response response = uc.createUserAccount(new User("test2_user"));
    assertEquals(response.getStatus(), Response.ok().build().getStatus());
    
    User user = new User("test3_user");
    user.setStatus("Hello");
  
    response = uc.updateUserStatus(user);
    assertEquals(404, response.getStatus());
  }
  
//  @Test
//  public void testGetUserStatusError() {
//    Response response = uc.createUserAccount(new User("New_User"));
//    assertEquals(response.getStatus(), Response.ok().build().getStatus());
//
//    response = uc.getUserStatus("Non_Existent_User");
//    assertEquals(404, response.getStatus());
//  }
  
  @Test
  public void basicControllerTest(){
    Response responce = uc.createUserAccount(newUser);
    Assert.assertEquals(responce.getStatus(), Response.ok().build().getStatus());
    Response responce2 = uc.createUserAccount(newUser);
    Assert.assertEquals(responce2.getStatus(), Response.status(409).build().getStatus());
  }
  
  @Test
  public void testFindUserEndpoint(){
    uc.createUserAccount(newUser);
    Response response = uc.findUserByName("TEST_USER_2");
    Assert.assertEquals(response.getStatus(), Response.status(200).build().getStatus());
  }
  
  @Test
  public void testFindUserEndpointDoesNotExist(){
    uc.createUserAccount(newUser);
    Response response = uc.findUserByName("TEST_USER_DOES_NOT_EXIST");
    Assert.assertEquals(response.getStatus(), Response.status(404).build().getStatus());
  }
  
  @Test
  public void testGetUserStatus() {
    uc.createUserAccount(newUser);
    Response response = uc.getUserStatus(newUser.getName());
    
    Assert.assertEquals(response.getStatus(), Response.status(200).build().getStatus());
  }
  
  @Test
  public void testSetAndGetUserStatus() {
    uc.createUserAccount(newUser);
    newUser.setStatus("Hello world");
    Response setStatusResponse = uc.updateUserStatus(newUser);
    Assert.assertEquals(setStatusResponse.getStatus(), Response.status(200).build().getStatus());
    
    Response response = uc.getUserStatus(newUser.getName());
    Assert.assertEquals(response.getStatus(), Response.status(200).build().getStatus());
  }
  
  @Test
  public void testGetUserStatusError() {
    uc.createUserAccount(newUser);
    User newUser2 = new User("TEST_USER_NEW");
    
    Response response = uc.getUserStatus(newUser2.getName());
    Assert.assertEquals(response.getStatus(), Response.status(404).build().getStatus());
  }
  
  @Test
  public void testSetUserStatusError() {
    uc.createUserAccount(newUser);
    User newUser2 = new User("TEST_USER_NEW");
    newUser.setStatus("Hello world");
    
    Response setStatusResponse = uc.updateUserStatus(newUser2);
    Assert.assertEquals(setStatusResponse.getStatus(), Response.status(404).build().getStatus());
  }
}
