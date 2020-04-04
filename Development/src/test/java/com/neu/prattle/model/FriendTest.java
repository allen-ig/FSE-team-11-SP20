package com.neu.prattle.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class FriendTest {

    Friend friend;
    
    @Before
    public void setUp(){
        System.setProperty("testing", "true");
    }
    
    @After
    public void tearDown() {
        System.setProperty("testing", "false");
    }

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

    @Test
    public void testGetId(){
        friend = new Friend(new User("user1"), new User("user2"));
        assertEquals(0, friend.getId());
    }

    @Test
    public void testSetId(){
        friend = new Friend(new User("user1"), new User("user2"));
        friend.setId(9);
        assertEquals(9, friend.getId());
    }

    @Test
    public void testGetSender(){
        friend = new Friend(new User("user1"), new User("user2"));
        assertEquals("user1", friend.getSender().getName());
    }

    @Test
    public void testSetSender(){
        friend = new Friend(new User("user1"), new User("user2"));
        friend.setSender(new User("user3"));
        assertEquals("user3", friend.getSender().getName());
    }

    @Test
    public void testGetRecipient(){
        friend = new Friend(new User("user1"), new User("user2"));
        assertEquals("user2", friend.getRecipient().getName());
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

    @Test
    public void testHashCode(){
        Friend friend = new Friend(new User("test1"), new User("test2"));
        assertEquals(Objects.hash("test1", "test2"), friend.hashCode());
    }

    @Test
    public void testEqualsTrue(){
        User test1 = new User("test1");
        User test2 = new User("test2");
        Friend friend1 = new Friend(test1, test2);
        Friend friend2 = new Friend(test1, test2);
        assertEquals(friend1, friend2);
    }

    @Test
    public void testEqualsFalse(){
        User test1 = new User("test1");
        User test2 = new User("test2");
        Friend friend1 = new Friend(test1, test2);
        Friend friend2 = new Friend(test2, test1);
        assertNotEquals(friend1, friend2);
    }

    @Test
    public void testEqualsNotAFriend(){
        User test1 = new User("test1");
        User test2 = new User("test2");
        Friend friend1 = new Friend(test1, test2);
        assertNotEquals(friend1, test1);
    }
}
