package com.neu.prattle;

import com.neu.prattle.controller.GroupController;
import com.neu.prattle.controller.UserController;
import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.Group;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import com.neu.prattle.service.UserServiceWithGroups;
import com.neu.prattle.service.UserServiceWithGroupsImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestGroupController {

  private UserServiceWithGroups us;
  private UserController uc;
  private GroupController gc;
  private User newUser;
  private User newMod;
  private BasicGroup newGroupJustUsers;
  private BasicGroup newGroupMods;
  private List<User> members;
  private List<User> moderators;

  @Before
  public void setUp() {
    //set testing
    System.setProperty("testing", "true");

    us = UserServiceWithGroupsImpl.getInstance();
    assertTrue(us.isTest());

    uc = new UserController();
    gc = new GroupController();

    newUser = new User("TEST_USER_3");
    newMod = new User("TEST_MOD_GROUP_CONTROLLER");
    members = new ArrayList<>();
    moderators = new ArrayList<>();
    members.add(newUser);
    moderators.add(newMod);

    newGroupJustUsers = new BasicGroup.GroupBuilder().setName("newGroupJustUsers")
        .setMembers(members).build();
    newGroupMods = new BasicGroup.GroupBuilder().setName("newGroupMods").setMembers(members)
        .setModerators(moderators).build();
  }

  @After
  public void tearDown() {
    /*
    BasicGroup group1 = us.findGroupByName("TEST_USER_3", "newGroupJustUsers").get();
    BasicGroup group2 = us.findGroupByName("TEST_USER_3", "newGroupMods").get();

    //any user should be able to delete groups with no mods
    us.deleteGroup(newMod, group2);
    us.deleteGroup(newMod, group1);


    User user = us.findUserByName("TEST_USER_3").get();
    us.deleteUser(user);
    User mod = us.findUserByName("TEST_MOD_GROUP_CONTROLLER").get();
    us.deleteUser(mod);



     */
    System.setProperty("testing", "false");
  }

  @Test
  public void basicControllerTest() {
    Response responce = uc.createUserAccount(newUser);
    Assert.assertEquals(responce.getStatus(), Response.ok().build().getStatus());
    Response responce2 = uc.createUserAccount(newUser);
    Assert.assertEquals(responce2.getStatus(), Response.status(409).build().getStatus());

    uc.createUserAccount(newMod);

    Response responce3 = gc.createGroup(newGroupJustUsers);
    Assert.assertEquals(responce.getStatus(), Response.ok().build().getStatus());
    Response responce4 = gc.createGroup(newGroupJustUsers);
    Assert.assertEquals(responce2.getStatus(), Response.status(409).build().getStatus());

    Response responce5 = gc.createGroup(newGroupMods);
    Assert.assertEquals(responce.getStatus(), Response.ok().build().getStatus());
    Response responce6 = gc.createGroup(newGroupMods);
    Assert.assertEquals(responce2.getStatus(), Response.status(409).build().getStatus());

    Optional<BasicGroup> jufetch = us.findGroupByName(newMod.getName(), newGroupJustUsers.getName());
    Optional<BasicGroup> modfetch = us.findGroupByName(newUser.getName(), newGroupMods.getName());

    Assert.assertEquals(jufetch.get(), newGroupJustUsers);
    Assert.assertEquals(modfetch.get(), newGroupMods);


    //Assert.assertEquals(jufetch.get().getModerators(), newGroupJustUsers.getModerators());
    Assert.assertEquals(modfetch.get().getModerators().get(0), newGroupMods.getModerators().get(0));

  }
}
