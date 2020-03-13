package com.neu.prattle.controller;

import com.neu.prattle.model.User;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestController {
  private UserController uc;

  @Before
  public void setUp() {
    uc = new UserController();
  }

  @Test
  public void basicControllerTest(){
     Response response = uc.createUserAccount(new User("test4"));
     assertEquals(response.getStatus(), Response.ok().build().getStatus());
     Response response2 = uc.createUserAccount(new User("test4"));
     assertEquals(response2.getStatus(), Response.status(409).build().getStatus());
  }
  
  @Test
  public void testUserStatusGetAndUpdate() {
    User user = new User("test");
    uc.createUserAccount(user);
    Response response = uc.getUserStatus("test");
    assertEquals(response.getStatus(), Response.ok().build().getStatus());
    assertEquals("{\"status\":\"\"}", response.readEntity(String.class));
    
    user.setStatus("Hello World");
    response = uc.updateUserStatus(user);
    assertEquals(response.getStatus(), Response.ok().build().getStatus());
  
    response = uc.getUserStatus("test");
    assertEquals(response.getStatus(), Response.ok().build().getStatus());
    assertEquals("{\"status\":\"Hello World\"}", response.readEntity(String.class));
  }
  
  @Test
  public void testUserStatusUpdateError() {
    Response response = uc.createUserAccount(new User("test2"));
    assertEquals(response.getStatus(), Response.ok().build().getStatus());
    
    User user = new User("test3");
    user.setStatus("Hello");
  
    response = uc.updateUserStatus(user);
    assertEquals(404, response.getStatus());
  }
  
  @Test
  public void testGetUserStatusError() {
    Response response = uc.createUserAccount(new User("test"));
    assertEquals(response.getStatus(), Response.ok().build().getStatus());
    
    response = uc.getUserStatus("test2");
    assertEquals(404, response.getStatus());
  }
}
