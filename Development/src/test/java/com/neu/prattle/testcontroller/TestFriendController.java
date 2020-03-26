package com.neu.prattle.testcontroller;

import com.neu.prattle.controller.FriendController;
import com.neu.prattle.controller.UserController;
import com.neu.prattle.model.Friend;
import com.neu.prattle.model.User;
import com.neu.prattle.service.FriendService;
import com.neu.prattle.service.FriendServiceImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestFriendController {
    private FriendService friendService;
    private UserService userService;
    private FriendController friendController;
    private User test1;
    private User test2;

    @Before
    public void setUp() {
        System.setProperty("testing", "true");
        friendService = FriendServiceImpl.getInstance();
        userService = UserServiceImpl.getInstance();
        assertTrue(friendService.isTest());
        friendController = new FriendController();
        test1 = new User("test1");
        test2 = new User("test2");
        userService.addUser(test1);
        userService.addUser(test2);
    }

    @After
    public void tearDown(){
        User user1 = userService.findUserByName("test1").get();
        User user2 = userService.findUserByName("test2").get();
        userService.deleteUser(user1);
        userService.deleteUser(user2);
        System.setProperty("testing", "false");
    }

    @Test
    public void testSendFriendRequest(){
        Friend friend = new Friend(test1, test2);
        Response response = friendController.sendFriendRequest(friend);
        assertEquals(response.getStatus(), Response.status(200).build().getStatus());
        friendService.deleteFriend(friend);
    }

    @Test
    public void testApproveFriendRequest(){
        Friend friend = new Friend(test1, test2);
        friendController.sendFriendRequest(friend);
        Response response = friendController.respondToFriendRequest(test1.getName(), test2.getName(), "approve");
        assertEquals(response.getStatus(), Response.status(200).build().getStatus());
        friendService.deleteFriend(friend);
    }

    @Test
    public void testDenyFriendRequest(){
        Friend friend = new Friend(test1, test2);
        friendController.sendFriendRequest(friend);
        Response response = friendController.respondToFriendRequest(test1.getName(), test2.getName(), "deny");
        assertEquals(response.getStatus(), Response.status(200).build().getStatus());
        friendService.deleteFriend(friend);
    }

    @Test
    public void testFindAllFriends(){
        friendController.findAllFriends(test1.getName());
    }

    @Test
    public void testRespondToFriendRequest(){
        User test3 = new User("test3");
        Friend friend = new Friend(test1, test3);
        friendController.sendFriendRequest(friend);
        Response response = friendController.respondToFriendRequest(test1.getName(), test3.getName(), "approve");
        assertEquals(response.getStatus(), Response.status(404).build().getStatus());
        friendService.deleteFriend(friend);
    }
}
