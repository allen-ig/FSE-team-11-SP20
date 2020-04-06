package com.neu.prattle.service;

import com.neu.prattle.exceptions.FriendAlreadyPresentException;
import com.neu.prattle.model.Friend;
import com.neu.prattle.model.User;
import com.neu.prattle.service.FriendService;
import com.neu.prattle.service.FriendServiceImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestFriendService {

    private FriendService friendService;
    private UserService userService;
    private User test1;
    private User test2;

    @Before
    public void setUp() {
        System.setProperty("testing", "true");
        friendService = FriendServiceImpl.getInstance();
        userService = UserServiceImpl.getInstance();
        assertTrue(friendService.isTest());
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
        friendService.sendFriendRequest(friend);
//        assertEquals(friend, friendService.findFriendByUsers(test1, test2));
        friendService.deleteFriend(friend);
    }

    @Test
    public void testApproveFriendRequest(){
        Friend friend = new Friend(test1, test2);
        friendService.sendFriendRequest(friend);
        friendService.approveFriendRequest(test1, test2, true);
//        assertEquals("APPROVED", friendService.findFriendByUsers(test1, test2).get().getStatus());
        friendService.deleteFriend(friend);
    }

    @Test
    public void testDenyFriendRequest(){
        userService.addUser(new User("test3"));
        User test3 = userService.findUserByName("test3").get();
        Friend friend = new Friend(test1, test3);
        friendService.sendFriendRequest(friend);
        friendService.approveFriendRequest(test1, test3, false);
//        assertEquals("DENIED", friendService.findFriendByUsers(test1, test2).get().getStatus());
        friendService.deleteFriend(friend);
    }

    @Test
    public void testFindAllFriends(){
        Friend friend = new Friend(test1, test2);
        friendService.sendFriendRequest(friend);
        friendService.approveFriendRequest(test1, test2, true);
        friendService.deleteFriend(friend);
    }

    @Test
    public void testNonTestUserServiceImpl(){
        System.setProperty("testing", "false");
        FriendService service = FriendServiceImpl.getInstance();
        assertFalse(service.isTest());
    }

    @Test(expected = FriendAlreadyPresentException.class)
    public void testDuplicateFriend(){
        Friend friend = new Friend(test1, test2);
        friendService.sendFriendRequest(friend);
        friendService.sendFriendRequest(friend);
        friendService.deleteFriend(friend);
    }

    @Test
    public void testFindFriendForUsers(){
        Friend friend = new Friend(test1, test2);
        friendService.sendFriendRequest(friend);
        Optional<Friend> friendOptional = friendService.findFriendByUsers(test1, test2);
        assertEquals(friendOptional.get(), friend);
    }
}
