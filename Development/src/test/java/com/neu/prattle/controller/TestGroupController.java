package com.neu.prattle.controller;

import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.request.GroupRequest;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import com.neu.prattle.service.UserServiceWithGroups;
import com.neu.prattle.service.UserServiceWithGroupsImpl;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestGroupController {

  private UserServiceWithGroups us;
  private UserService userService;
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

    userService = UserServiceImpl.getInstance();
    assertTrue(userService.isTest());
    
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
  public void deleteGroupTest() {
    //existing group
    User da = new User("da");
    userService.addUser(da);
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
    Assert.assertEquals(409, res2.getStatus());

    //no group
    String g2 = "not there";
    GroupRequest req4 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(g2).build();
    Response res4 = gc.deleteGroup(req4);
    Assert.assertEquals(409, res4.getStatus());

    //user not in mods
    User db = new User("db");
    userService.addUser(db);
    GroupRequest req5 = GroupRequest.groupRequestBuilder().setSender(db.getName()).setUser(user).setGroup(gName).build();
    Response res5 = gc.deleteGroup(req5);
    Assert.assertEquals(409, res5.getStatus());

    //normal delete
    GroupRequest req6 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(gName).build();
    Response res6 = gc.deleteGroup(req6);
    Assert.assertEquals(200, res6.getStatus());
  }


  @Test
  public void addUserTest() {
    //existing group
    User a = new User("a");
    userService.addUser(a);
    Set<User> mem = new HashSet<>();
    mem.add(a);
    BasicGroup ga = BasicGroup.groupBuilder().setMembers(mem).setName("ga").build();
    us.addGroup(ga);

    //new user
    User b = new User("b");
    userService.addUser(b);

    //make request
    String sender = a.getName();
    String user = b.getName();
    String gName = ga.getName();

    GroupRequest request = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(gName).build();
    Response response = gc.extendGroup(request);
    Assert.assertEquals(200, response.getStatus());
    Assert.assertEquals("members of ga appended: b", response.getEntity());

    //no sender
    String send2 = "not there";
    GroupRequest req2 = GroupRequest.groupRequestBuilder().setSender(send2).setUser(user).setGroup(gName).build();
    Response res2 = gc.extendGroup(req2);
    Assert.assertEquals(409, res2.getStatus());

    //no user
    String u2 = "not there";
    GroupRequest req3 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(u2).setGroup(gName).build();
    Response res3 = gc.extendGroup(req3);
    Assert.assertEquals(409, res3.getStatus());

    //no group
    String g2 = "not there";
    GroupRequest req4 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(g2).build();
    Response res4 = gc.extendGroup(req4);
    Assert.assertEquals(409, res4.getStatus());

    //user already exists
    Response res5 = gc.extendGroup(request);
    Assert.assertEquals(409, res5.getStatus());

    //new non member
    User nm = new User("nm");
    userService.addUser(nm);
    GroupRequest req6 = GroupRequest.groupRequestBuilder().setSender(nm.getName()).setUser(user).setGroup(gName).build();
    Response res6 = gc.extendGroup(req6);
    Assert.assertEquals(409, res6.getStatus());
  }

  @Test
  public void addModeratorTest() {
    //existing group
    User ma = new User("ma");
    userService.addUser(ma);
    Set<User> mem = new HashSet<>();
    mem.add(ma);
    BasicGroup mga = BasicGroup.groupBuilder().setMembers(mem).setName("mga").build();
    us.addGroup(mga);

    //new user
    User mb = new User("mb");
    userService.addUser(mb);

    //make request
    String sender = ma.getName();
    String user = mb.getName();
    String gName = mga.getName();

    GroupRequest request = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(gName).build();
    Response response = gc.extendModerators(request);
    Assert.assertEquals(200, response.getStatus());
    Assert.assertEquals("moderators of mga appended: mb", response.getEntity());

    //no sender
    String send2 = "not there";
    GroupRequest req2 = GroupRequest.groupRequestBuilder().setSender(send2).setUser(user).setGroup(gName).build();
    Response res2 = gc.extendModerators(req2);
    Assert.assertEquals(409, res2.getStatus());

    //no user
    String u2 = "not there";
    GroupRequest req3 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(u2).setGroup(gName).build();
    Response res3 = gc.extendModerators(req3);
    Assert.assertEquals(409, res3.getStatus());

    //no group
    String g2 = "not there";
    GroupRequest req4 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(g2).build();
    Response res4 = gc.extendModerators(req4);
    Assert.assertEquals(409, res4.getStatus());

    //user already exists
    Response res5 = gc.extendModerators(request);
    Assert.assertEquals( 409, res5.getStatus());
    Assert.assertEquals("Group already has this moderator", res5.getEntity());
  }

  @Test
  public void removeUserTest() {
    //existing user
    User ra = new User("ra");
    userService.addUser(ra);
    //new user
    User rb = new User("rb");
    userService.addUser(rb);
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
    Assert.assertEquals( 409, res2.getStatus());

    //no user
    String u2 = "not there";
    GroupRequest req3 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(u2).setGroup(gName).build();
    Response res3 = gc.removeUser(req3);
    Assert.assertEquals(409, res3.getStatus());

    //no group
    String g2 = "not there";
    GroupRequest req4 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(g2).build();
    Response res4 = gc.removeUser(req4);
    Assert.assertEquals(409, res4.getStatus());

    //not a moderator
    User r3 = new User("r3");
    userService.addUser(r3);
    String nonMod = r3.getName();
    GroupRequest req5 = GroupRequest.groupRequestBuilder().setSender(nonMod).setUser(user).setGroup(gName).build();
    Response res5 = gc.removeUser(req5);
    Assert.assertEquals(410, res5.getStatus());

    //proper
    GroupRequest request = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(gName).build();
    Response response = gc.removeUser(request);
    Assert.assertEquals(200, response.getStatus());
    Assert.assertEquals("member of rga removed: rb", response.getEntity());

    Optional<BasicGroup> found = us.findGroupByName(sender, rga.getName());

    //remove last member and delete
    GroupRequest req6 = GroupRequest.groupRequestBuilder().setSender(found.get().getModerators().iterator().next().getName()).setUser(ra.getName()).setGroup(gName).build();
    Response res6 = gc.removeUser(req6);
    Assert.assertEquals(410, res6.getStatus());
  }

  @Test
  public void removeModeratorTest() {
    //existing user
    User rma = new User("rma");
    userService.addUser(rma);
    //new user
    User rmb = new User("rmb");
    userService.addUser(rmb);

    User rmc = new User("rmc");
    userService.addUser(rmc);

    Set<User> mem = new HashSet<>();
    mem.add(rma);
    mem.add(rmb);
    mem.add(rmc);
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
    Assert.assertEquals(409, res2.getStatus());

    //no user
    String u2 = "not there";
    GroupRequest req3 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(u2).setGroup(gName).build();
    Response res3 = gc.removeModerator(req3);
    Assert.assertEquals(409, res3.getStatus());

    //no group
    String g2 = "not there";
    GroupRequest req4 = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(g2).build();
    Response res4 = gc.removeModerator(req4);
    Assert.assertEquals(409, res4.getStatus());

    //wrong user
    GroupRequest wrUserReq = GroupRequest.groupRequestBuilder().setSender(rmc.getName()).setUser(rmb.getName()).setGroup(gName).build();
    Response wrUser = gc.removeModerator(wrUserReq);
    Assert.assertEquals(409, wrUser.getStatus());

    //proper
    GroupRequest request = GroupRequest.groupRequestBuilder().setSender(sender).setUser(user).setGroup(gName).build();
    Response response = gc.removeModerator(request);
    Assert.assertEquals(response.getStatus(), 200);
    Assert.assertEquals("moderator of rmga removed: rma", response.getEntity());

    //remove last member and delete
    GroupRequest req6 = GroupRequest.groupRequestBuilder().setSender("rmb").setUser("rmb").setGroup(gName).build();
    Response res6 = gc.removeUser(req6);
    Assert.assertEquals(200, res6.getStatus());

    //remove last member and delete
    GroupRequest req7 = GroupRequest.groupRequestBuilder().setSender("rmb").setUser("rmb").setGroup(gName).build();
    Response res7 = gc.removeUser(req7);
    Assert.assertEquals(409, res7.getStatus());
  }

  @Test
  public void getGroupsTest() {
    //existing user
    User gga = new User("gga");
    userService.addUser(gga);
    //new user
    User ggb = new User("ggb");
    userService.addUser(ggb);

    User ggc = new User("ggc");
    userService.addUser(ggc);

    Set<User> mem = new HashSet<>();
    mem.add(gga);
    mem.add(ggb);

    Set<User> mod = new HashSet<>();
    mod.add(gga);

    BasicGroup ggg = BasicGroup.groupBuilder().setMembers(mem).setModerators(mod).setName("ggg").build();
    us.addGroup(ggg);

    //not allClear
    GroupRequest req1 = GroupRequest.groupRequestBuilder().setSender("randoo").build();
    Response res1 = gc.getGroups(req1);
    Assert.assertEquals(409, res1.getStatus());

    //zero size
    GroupRequest req2 = GroupRequest.groupRequestBuilder().setSender(ggc.getName()).build();
    Response res2 = gc.getGroups(req2);
    Assert.assertEquals(201, res2.getStatus());

    //proper
    GroupRequest req = GroupRequest.groupRequestBuilder().setSender(gga.getName()).build();
    Response res3 = gc.getGroups(req);
    Assert.assertEquals(200, res3.getStatus());
  }

}
