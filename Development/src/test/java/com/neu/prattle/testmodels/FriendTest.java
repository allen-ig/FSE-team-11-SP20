package com.neu.prattle.testmodels;

import com.neu.prattle.model.Friend;
import com.neu.prattle.model.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FriendTest {

    Friend friend;

    @Test
    public void testEmptyConstructor(){
        friend = new Friend();
    }

    @Test
    public void testUserConstructor(){
        friend = new Friend(new User("user1"), new User("user2"));
        assertEquals("user1", friend.getSender().getName());
        assertEquals("user2", friend.getRecipient().getName());
    }

//    @Test
//    public void testStringConstructor(){
//        Friend.FriendKey friendKey = new Friend.FriendKey("user1", "user2");
//        friend = new Friend(friendKey);
//        assertEquals("user1", friend.getFriendKey().getSender());
//        assertEquals("user2", friend.getFriendKey().getRecipient());
//    }

    @Test
    public void testSetSender(){
        friend = new Friend(new User("user1"), new User("user2"));
        friend.setSender(new User("user3"));
        assertEquals("user3", friend.getSender().getName());
    }

    @Test
    public void testSetRecipient(){
        friend = new Friend(new User("user1"), new User("user2"));
        friend.setRecipient(new User("user3"));
        assertEquals("user3", friend.getRecipient().getName());
    }

    @Test
    public void testGetStatus(){
        friend = new Friend(new User("user1"), new User("user2"));
        assertEquals("pending", friend.getStatus());
    }

    @Test
    public void testSetStatus(){
        friend = new Friend(new User("user1"), new User("user2"));
        friend.setStatus("APPROVED");
        assertEquals("APPROVED", friend.getStatus());
    }

//    @Test
//    public void testFriendKeyConstructor(){
//        Friend.FriendKey friendKey = new Friend.FriendKey();
//        assertNull(friendKey.getRecipient());
//        assertNull(friendKey.getSender());
//    }
}
