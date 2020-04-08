package com.neu.prattle.controller;

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
        Friend friend = new Friend(test1, test2);
        friendController.sendFriendRequest(friend);
        friendController.respondToFriendRequest(test1.getName(),test2.getName(),"approve");
        assertEquals(200, friendController.findAllFriends(test1.getName()).getStatus());
        friendService.deleteFriend(friend);
    }

    @Test
    public void testFindAllFriendsForRecipient(){
        Friend friend = new Friend(test1, test2);
        friendController.sendFriendRequest(friend);
        friendController.respondToFriendRequest(test1.getName(),test2.getName(),"approve");
        assertEquals(200, friendController.findAllFriends(test2.getName()).getStatus());
        friendService.deleteFriend(friend);
    }

    @Test
    public void testRespondToFriendRequestWithoutSender(){
        Friend friend = new Friend(test1, test2);
        friendController.sendFriendRequest(friend);
        Response response = friendController.respondToFriendRequest("noSender", test2.getName(), "deny");
        assertEquals(response.getStatus(), Response.status(404).build().getStatus());
        assertEquals("Could not find sender!\n", response.getEntity());
        friendService.deleteFriend(friend);
    }

    @Test
    public void testRespondToFriendRequestWithoutRecipient(){
        Friend friend = new Friend(test1, test2);
        friendController.sendFriendRequest(friend);
        Response response = friendController.respondToFriendRequest(test1.getName(), "noRecipient", "deny");
        assertEquals(response.getStatus(), Response.status(404).build().getStatus());
        assertEquals("Could not find recipient!", response.getEntity());
        friendService.deleteFriend(friend);
    }

    @Test
    public void testFriendAlreadyExist(){
        Friend friend = new Friend(test1, test2);
        friendController.sendFriendRequest(friend);
        assertEquals(409, friendController.sendFriendRequest(friend).getStatus());
    }

    @Test
    public void testFindAllFriendsForNoneExistingUser(){
        Response response = friendController.findAllFriends("ghost");
        assertEquals(404, response.getStatus());
        assertEquals("Could not find the target user ghost", response.getEntity());
    }

    @Test
    public void testDeleteFriend(){
        Friend friend = new Friend(test1, test2);
        friendController.sendFriendRequest(friend);
        Response response = friendController.respondToFriendRequest(test1.getName(), test2.getName(), "approve");
        assertEquals(response.getStatus(), Response.status(200).build().getStatus());
        Response response1 = friendController.removeFriend(friend);
        assertEquals(200, response1.getStatus());
    }

    @Test
    public void testDeleteFriendDoesNotExist(){
        Friend friend = new Friend(test1, test2);
        assertEquals(404, friendController.removeFriend(friend).getStatus());
    }
}
