package com.neu.prattle;

import com.neu.prattle.controller.GroupController;
import com.neu.prattle.controller.UserController;
import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.Group;
import com.neu.prattle.model.Request.GroupRequest;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import com.neu.prattle.service.UserServiceWithGroups;
import com.neu.prattle.service.UserServiceWithGroupsImpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestGroupController {

  private UserServiceWithGroups us;
  private UserController uc;
  private GroupController gc;
  private User newUser;
  private User newMod;
  private BasicGroup newGroupJustUsers;
  private BasicGroup newGroupMods;
  private Set<User> members;
  private Set<User> moderators;

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

    members = new HashSet<>();
    moderators = new HashSet<>();
    members.add(newUser);
    moderators.add(newMod);

    newGroupJustUsers = new BasicGroup.GroupBuilder().setName("newGroupJustUsers")
        .setMembers(members).build();
    newGroupMods = new BasicGroup.GroupBuilder().setName("newGroupMods").setMembers(members)
        .setModerators(moderators).build();
  }

  @AfterClass
  public static void tearDown() {
    System.setProperty("testing", "false");
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

    Assert.assertEquals(jufetch.get().getModerators(), newGroupJustUsers.getModerators());
    Assert.assertEquals(modfetch.get().getModerators(), newGroupMods.getModerators());

    BasicGroup noUsers = BasicGroup.groupBuilder().setName("no users").build();
    Response last = gc.createGroup(noUsers);
    Assert.assertEquals(last.getStatus(), 409);
  }

  @Test
  public void deleteGroup() {
    //existing group
    User da = new User("da");
    us.addUser(da);
    Set<User> mem = new HashSet<>();
    mem.add(da);
    BasicGroup dga = BasicGroup.groupBuilder().setMembers(mem).setName("dga").build();
    us.addGroup(dga);

    //make request
    String sender = da.getName();
    String user = da.getName();
    String gName = dga.getName();

    //no sender
    String send2 = "not there";
    GroupRequest req2 = GroupRequest.groupRequestBuilder().setSender(send2).setUser(user).setGroup(gName).build();
    Response res2 = gc.deleteGroup(req2);
    Assert.assertEquals(res2.getStatus(), 409);

    //no group
    String g2 = "not there";
    GroupRequest req4 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(g2).build();
    Response res4 = gc.deleteGroup(req4);
    Assert.assertEquals(res4.getStatus(), 409);

    //user not in mods
    User db = new User("db");
    us.addUser(db);
    GroupRequest req5 = GroupRequest.groupRequestBuilder().setSender(db.getName()).setUser(user).setGroup(gName).build();
    Response res5 = gc.deleteGroup(req5);
    Assert.assertEquals(res5.getStatus(), 409);

    //normal delete
    GroupRequest req6 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(gName).build();
    Response res6 = gc.deleteGroup(req6);
    Assert.assertEquals(res6.getStatus(), 200);
  }


  @Test
  public void addUser() {
    //existing group
    User a = new User("a");
    us.addUser(a);
    Set<User> mem = new HashSet<>();
    mem.add(a);
    BasicGroup ga = BasicGroup.groupBuilder().setMembers(mem).setName("ga").build();
    us.addGroup(ga);

    //new user
    User b = new User("b");
    us.addUser(b);

    //make request
    String sender = a.getName();
    String user = b.getName();
    String gName = ga.getName();

    GroupRequest request = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(gName).build();
    Response response = gc.extendGroup(request);
    Assert.assertEquals(response.getStatus(), 200);
    Assert.assertEquals(response.getEntity(), "members of ga appended: b");

    //no sender
    String send2 = "not there";
    GroupRequest req2 = GroupRequest.groupRequestBuilder().setSender(send2).setUser(user).setGroup(gName).build();
    Response res2 = gc.extendGroup(req2);
    Assert.assertEquals(res2.getStatus(), 409);

    //no user
    String u2 = "not there";
    GroupRequest req3 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(u2).setGroup(gName).build();
    Response res3 = gc.extendGroup(req3);
    Assert.assertEquals(res3.getStatus(), 409);

    //no group
    String g2 = "not there";
    GroupRequest req4 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(g2).build();
    Response res4 = gc.extendGroup(req4);
    Assert.assertEquals(res4.getStatus(), 409);

    //user already exists
    Response res5 = gc.extendGroup(request);
    Assert.assertEquals(res5.getStatus(), 409);

    //new non member
    User nm = new User("nm");
    us.addUser(nm);
    GroupRequest req6 = GroupRequest.groupRequestBuilder().setSender(nm.getName()).setUser(user).setGroup(gName).build();
    Response res6 = gc.extendGroup(req6);
    Assert.assertEquals(res6.getStatus(), 409);
  }

  @Test
  public void addModerator() {
    //existing group
    User ma = new User("ma");
    us.addUser(ma);
    Set<User> mem = new HashSet<>();
    mem.add(ma);
    BasicGroup mga = BasicGroup.groupBuilder().setMembers(mem).setName("mga").build();
    us.addGroup(mga);

    //new user
    User mb = new User("mb");
    us.addUser(mb);

    //make request
    String sender = ma.getName();
    String user = mb.getName();
    String gName = mga.getName();

    GroupRequest request = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(gName).build();
    Response response = gc.extendModerators(request);
    Assert.assertEquals(response.getStatus(), 200);
    Assert.assertEquals(response.getEntity(), "moderators of mga appended: mb");

    //no sender
    String send2 = "not there";
    GroupRequest req2 = GroupRequest.groupRequestBuilder().setSender(send2).setUser(user).setGroup(gName).build();
    Response res2 = gc.extendModerators(req2);
    Assert.assertEquals(res2.getStatus(), 409);

    //no user
    String u2 = "not there";
    GroupRequest req3 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(u2).setGroup(gName).build();
    Response res3 = gc.extendModerators(req3);
    Assert.assertEquals(res3.getStatus(), 409);

    //no group
    String g2 = "not there";
    GroupRequest req4 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(g2).build();
    Response res4 = gc.extendModerators(req4);
    Assert.assertEquals(res4.getStatus(), 409);

    //user already exists
    Response res5 = gc.extendModerators(request);
    Assert.assertEquals(res5.getStatus(), 409);
  }

  @Test
  public void removeUser() {
    //existing user
    User ra = new User("ra");
    us.addUser(ra);
    //new user
    User rb = new User("rb");
    us.addUser(rb);
    Set<User> mem = new HashSet<>();
    mem.add(ra);
    mem.add(rb);
    BasicGroup rga = BasicGroup.groupBuilder().setMembers(mem).setName("rga").build();
    us.addGroup(rga);

    //make request
    String sender = rga.getModerators().iterator().next().getName(); //make sure mod bc set
    String user = rb.getName();
    String gName = rga.getName();

    //no sender
    String send2 = "not there";
    GroupRequest req2 = GroupRequest.groupRequestBuilder().setSender(send2).setUser(user).setGroup(gName).build();
    Response res2 = gc.removeUser(req2);
    Assert.assertEquals(res2.getStatus(), 409);

    //no user
    String u2 = "not there";
    GroupRequest req3 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(u2).setGroup(gName).build();
    Response res3 = gc.removeUser(req3);
    Assert.assertEquals(res3.getStatus(), 409);

    //no group
    String g2 = "not there";
    GroupRequest req4 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(g2).build();
    Response res4 = gc.removeUser(req4);
    Assert.assertEquals(res4.getStatus(), 409);

    //not a moderator
    User r3 = new User("r3");
    us.addUser(r3);
    String nonMod = r3.getName();
    GroupRequest req5 = GroupRequest.groupRequestBuilder().setSender(nonMod).setUser(user).setGroup(gName).build();
    Response res5 = gc.removeUser(req5);
    Assert.assertEquals(res5.getStatus(), 409);

    //proper
    GroupRequest request = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(gName).build();
    Response response = gc.removeUser(request);
    Assert.assertEquals(response.getStatus(), 200);
    Assert.assertEquals(response.getEntity(), "member of rga removed: rb");

    Optional<BasicGroup> found = us.findGroupByName(sender, rga.getName());

    //remove last member and delete
    GroupRequest req6 = GroupRequest.groupRequestBuilder().setSender(found.get().getModerators().iterator().next().getName()).setUser(ra.getName()).setGroup(gName).build();
    Response res6 = gc.removeUser(req6);
    Assert.assertEquals(res6.getStatus(), 409);
  }

  @Test
  public void removeModerator() {
    //existing user
    User rma = new User("rma");
    us.addUser(rma);
    //new user
    User rmb = new User("rmb");
    us.addUser(rmb);
    Set<User> mem = new HashSet<>();
    mem.add(rma);
    mem.add(rmb);
    Set<User> mod = new HashSet<>();
    mod.add(rma);
    mod.add(rmb);

    BasicGroup rmga = BasicGroup.groupBuilder().setMembers(mem).setModerators(mod).setName("rmga").build();
    us.addGroup(rmga);

    //make request
    String sender = rma.getName();
    String user = rma.getName();
    String gName = rmga.getName();

    //no sender
    String send2 = "not there";
    GroupRequest req2 = GroupRequest.groupRequestBuilder().setSender(send2).setUser(user).setGroup(gName).build();
    Response res2 = gc.removeModerator(req2);
    Assert.assertEquals(res2.getStatus(), 409);

    //no user
    String u2 = "not there";
    GroupRequest req3 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(u2).setGroup(gName).build();
    Response res3 = gc.removeModerator(req3);
    Assert.assertEquals(res3.getStatus(), 409);

    //no group
    String g2 = "not there";
    GroupRequest req4 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(g2).build();
    Response res4 = gc.removeModerator(req4);
    Assert.assertEquals(res4.getStatus(), 409);


    //proper
    GroupRequest request = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(gName).build();
    Response response = gc.removeModerator(request);
    Assert.assertEquals(response.getStatus(), 200);
    Assert.assertEquals(response.getEntity(), "moderator of rmga removed: rma");

    //remove last member and delete
    GroupRequest req6 = GroupRequest.groupRequestBuilder().setSender("rmb").setUser("rmb").setGroup(gName).build();
    Response res6 = gc.removeUser(req6);
    Assert.assertEquals(res6.getStatus(), 409);
  }





}
