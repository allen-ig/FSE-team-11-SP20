package com.neu.prattle.testservice;

import com.neu.prattle.model.Friend;
import com.neu.prattle.model.User;
import com.neu.prattle.service.FriendService;
import com.neu.prattle.service.FriendServiceImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestFriendService {

    private FriendService friendService;
    private UserService userService;

    @Before
    public void setUp() {
        System.setProperty("testing", "true");
        friendService = FriendServiceImpl.getInstance();
        userService = UserServiceImpl.getInstance();
        assertTrue(friendService.isTest());
    }

    @After
    public void tearDown() {
        System.setProperty("testing", "false");
    }

    @Test
    public void testSendFriendRequest(){
//        userService.addUser(new User("test2"));
//        userService.addUser(new User("test4"));
//        Friend friend = new Friend(new Friend.FriendKey("test2", "test4"));
        User test2 = userService.findUserByName("test2").get();
        User test4 = userService.findUserByName("test4").get();
        Friend friend = new Friend(test2, test4);
//        friend.setSender(test2);
//        friend.setRecipient(test4);
        friendService.sendFriendRequest(friend);
    }

//    @Test
//    public void testApproveFriendRequest(){
//        friendService.approveFriendRequest(1, true);
//    }

    @Test
    public void testFindAllFriends(){
        friendService.findAllFriends("tim3");
    }
}
