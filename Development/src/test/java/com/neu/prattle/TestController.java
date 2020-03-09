package com.neu.prattle;

import com.google.gson.JsonObject;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestController {
  private UserService us;
  private UserController uc;

  @Before
  public void setUp() {
    us = UserServiceImpl.getInstance();
    uc = new UserController();
  }

  @Test
  public void basicControllerTest(){
     Response responce = uc.createUserAccount(new User("test"));
     Assert.assertEquals(responce.getStatus(), Response.ok().build().getStatus());
     Response responce2 = uc.createUserAccount(new User("test"));
     Assert.assertEquals(responce2.getStatus(), Response.status(409).build().getStatus());
  }

  /**
   * Integration test. Works with Tomcat running. This should be commented out if it throws errors.
   */
  @Test
  public void testResponce() {
    /*
    HttpUriRequest request = new HttpGet("http://localhost:8080/prattle/rest/user/create/" + "testController");
    */
    JsonObject name = new JsonObject();
    name.addProperty("name", "tim");

    try {
      StringEntity womboCombo = new StringEntity(name.toString(), ContentType.APPLICATION_JSON);

      HttpClient client = HttpClientBuilder.create().build();
      HttpPost request = new HttpPost("http://localhost:8080/prattle/rest/user/create");
      request.setEntity(womboCombo);
      HttpResponse response = client.execute(request);
      HttpResponse response2 = client.execute(request);

      Assert.assertEquals(response2.getStatusLine().getStatusCode(), HttpStatus.SC_CONFLICT);
    } catch (java.io.IOException exception) {
      Assert.fail();
    }

  }

}
