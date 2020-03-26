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
//        userService.addUser(test1);
//        userService.addUser(test2);
    }

    @After
    public void tearDown(){
//        userService.deleteUser(test1);
//        userService.deleteUser(test2);
        System.setProperty("testing", "false");
    }

    @Test
    public void testSendFriendRequest(){
        friendController.sendFriendRequest(new Friend(test1, test2));
    }

    @Test
    public void testApproveFriendRequest(){
        friendController.sendFriendRequest(new Friend(test1, test2));
        friendController.respondToFriendRequest(test1.getName(), test2.getName(), "approve");
    }

    @Test
    public void testDenyFriendRequest(){
        friendController.sendFriendRequest(new Friend(test1, test2));
        friendController.respondToFriendRequest(test1.getName(), test2.getName(), "deny");
    }

    @Test
    public void testFindAllFriends(){
        friendController.findAllFriends(test1.getName());
    }
}
