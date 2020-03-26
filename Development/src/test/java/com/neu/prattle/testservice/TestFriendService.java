package com.neu.prattle.testservice;

import com.neu.prattle.controller.FriendController;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
@FixMethodOrder(MethodSorters.JVM)
public class TestFriendService {

    private FriendService friendService;
    private UserService userService;
//    private User test1;
//    private User test2;

    @Before
    public void setUp() {
        System.setProperty("testing", "true");
        friendService = FriendServiceImpl.getInstance();
        userService = UserServiceImpl.getInstance();
        assertTrue(friendService.isTest());
    }

    @After
    public void tearDown(){
//        userService.deleteUser(test1);
//        userService.deleteUser(test2);
        System.setProperty("testing", "false");
    }

    @Test
    public void testSendFriendRequest(){
//        userService.addUser(test1);
//        userService.addUser(test2);
        User test1 = userService.findUserByName("test1").get();
        User test2 = userService.findUserByName("test2").get();
        Friend friend = new Friend(test1, test2);
        friendService.sendFriendRequest(friend);
    }

    @Test
    public void testApproveFriendRequest(){
        User test1 = userService.findUserByName("test1").get();
        User test2 = userService.findUserByName("test2").get();
        Friend friend = new Friend(test1, test2);
        friendService.sendFriendRequest(friend);
        friendService.approveFriendRequest(test1, test2, true);
    }

    @Test
    public void testDenyFriendRequest(){
        userService.addUser(new User("test3"));
        User test1 = userService.findUserByName("test1").get();
        User test3 = userService.findUserByName("test3").get();
        Friend friend = new Friend(test1, test3);
        friendService.sendFriendRequest(friend);
        friendService.approveFriendRequest(test1, test3, false);
    }

    @Test
    public void testFindAllFriends(){
        User test1 = userService.findUserByName("test1").get();
        User test2 = userService.findUserByName("test2").get();
        Friend friend = new Friend(test1, test2);
        friendService.sendFriendRequest(friend);
        friendService.approveFriendRequest(test1, test2, true);
//        assertEquals(1, friendService.findAllFriends("tim3").size());
    }

    @Test
    public void testNonTestUserServiceImpl(){
        System.setProperty("testing", "false");
        FriendService service = FriendServiceImpl.getInstance();
        assertFalse(service.isTest());
    }
}
