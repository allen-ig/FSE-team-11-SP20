package com.neu.prattle.controller;

import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestController {

    private UserService us;
    private UserServiceWithGroups ug;
    private UserController uc;
    private MessageService ms;
    private User newUser;

    @Before
    public void setUp() {
        System.setProperty("testing", "true");
        us = UserServiceImpl.getInstance();
        assertTrue(us.isTest());
        uc = new UserController();
        newUser = new User("TEST_USER_2");
        ms = MessageServiceImpl.getInstance();
        ug = UserServiceWithGroupsImpl.getInstance();
    }

    @After
    public void tearDown() {
        Optional<User> user = us.findUserByName("TEST_USER_2");
        if (user.isPresent()) {
            us.deleteUser(user.get());
        }
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
    public void basicControllerTest() {
        Response responce = uc.createUserAccount(newUser);
        Assert.assertEquals(responce.getStatus(), Response.ok().build().getStatus());
        Response responce2 = uc.createUserAccount(newUser);
        Assert.assertEquals(responce2.getStatus(), Response.status(409).build().getStatus());
    }

    @Test
    public void testFindUserEndpoint() {
        uc.createUserAccount(newUser);
        Response response = uc.findUserByName("TEST_USER_2");
        Assert.assertEquals(response.getStatus(), Response.status(200).build().getStatus());
    }

    @Test
    public void testFindUserEndpointDoesNotExist() {
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

    @Test
    public void testGetDirectMessage() {
        User tgdm1 = new User("tgdm1");
        User tgdm2 = new User("tgd2");
        User tgdm3 = new User("tgdm3");

        uc.createUserAccount(tgdm1);
        uc.createUserAccount(tgdm2);
        uc.createUserAccount(tgdm3);

        Message m1 = ms.createMessage(Message.messageBuilder().setTo(tgdm2.getName()).setFrom(tgdm1.getName()).setMessageContent("asdf").build());
        Message m2 = ms.createMessage(Message.messageBuilder().setTo(tgdm1.getName()).setFrom(tgdm2.getName()).setMessageContent("asdf2").build());

        Response res1 = uc.getDirectMessages(tgdm1.getName(), tgdm2.getName());
        Response res2 = uc.getDirectMessages(tgdm2.getName(), tgdm1.getName());
        Response res3 = uc.getDirectMessages(tgdm1.getName(), tgdm3.getName());
        Response res4 = uc.getDirectMessages(tgdm3.getName(), "rando");

        Assert.assertEquals(res1.getStatus(), 200);
        Assert.assertEquals(res2.getStatus(), 200);
        Assert.assertEquals(res3.getStatus(), 203);
        Assert.assertEquals(res4.getStatus(), 203);
    }

    @Test
    public void testGetGroupMessage() {
        User tggm1 = new User("tggm1");
        User tggm2 = new User("tggm2");
        User tggm3 = new User("tggm3");

        uc.createUserAccount(tggm1);
        uc.createUserAccount(tggm2);
        uc.createUserAccount(tggm3);

        Set<User> members = new HashSet<>();
        Set<User> moderators = new HashSet<>();

        members.add(tggm1);
        members.add(tggm2);

        moderators.add(tggm1);

        BasicGroup tggmg1 = BasicGroup.groupBuilder().setName("tggmg1").setMembers(members).setModerators(moderators).build();
        ug.addGroup(tggmg1);

        BasicGroup tggmg2 = BasicGroup.groupBuilder().setName("tggmg2").setMembers(members).setModerators(moderators).build();
        ug.addGroup(tggmg2);

        Message m1 = ms.createMessage(Message.messageBuilder().setTo("group: " + tggmg1).setFrom(tggm1.getName()).setMessageContent("asdf").build());
        Message m2 = ms.createMessage(Message.messageBuilder().setTo("group: " + tggmg1).setFrom(tggm2.getName()).setMessageContent("asdf2").build());
        Message m3 = ms.createMessage(Message.messageBuilder().setFrom(tggm1.getName() + ": " + tggm1.getName()).setTo(tggmg2.getName()).setMessageContent("fdsa").build());
        Message m4 = ms.createMessage(Message.messageBuilder().setFrom(tggm1.getName() + ": " + tggm2.getName()).setTo(tggmg1.getName()).setMessageContent("fdsa").build());

        Response res1 = uc.getGroupMessages(tggm1.getName(), tggmg1.getName());
        Response res2 = uc.getDirectMessages(tggmg1.getName(), tggm2.getName());
        Response res3 = uc.getDirectMessages(tggm1.getName(), tggmg2.getName());
        Response res4 = uc.getDirectMessages(tggm3.getName(), "rando");

        Assert.assertEquals(res1.getStatus(), 203);
        Assert.assertEquals(res2.getStatus(), 203);
        Assert.assertEquals(res3.getStatus(), 203);
        Assert.assertEquals(res4.getStatus(), 203);
    }


}
