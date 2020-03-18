package com.neu.prattle;

import com.neu.prattle.controller.UserController;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import javax.ws.rs.core.Response;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.map.util.JSONPObject;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.Optional;
@FixMethodOrder(MethodSorters.JVM)
public class TestController {
  private UserService us;
  private UserController uc;
  private User newUser;

  @Before
  public void setUp() {
    us = UserServiceImpl.getInstance();
    uc = new UserController();
    newUser = new User("TEST_USER_2");
  }

  @After
  public void tearDown(){
    Optional<User> user = us.findUserByName("TEST_USER_2");
    user.ifPresent(value -> us.deleteUser(value));
  }

  @Test
  public void basicControllerTest(){
     Response responce = uc.createUserAccount(newUser);
     Assert.assertEquals(responce.getStatus(), Response.ok().build().getStatus());
     Response responce2 = uc.createUserAccount(newUser);
     Assert.assertEquals(responce2.getStatus(), Response.status(409).build().getStatus());
  }

}
