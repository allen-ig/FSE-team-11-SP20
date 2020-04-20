package com.neu.prattle.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UserTest {

    @Before
    public void setUp() {
        System.setProperty("testing", "true");
    }
    
    @After
    public void tearDown(){
        System.setProperty("testing", "false");
    }
    
    @Test
    public void testEmptyConstructor(){
        User user = new User();
        assertNull(user.getName());
    }

    @Test
    public void testConstructor(){
        User user = new User("alice");
        assertEquals("alice", user.getName());
    }

    @Test
    public void testGetId(){
        User user = new User("alice");
        assertEquals(0, user.getId());
    }

    @Test
    public void testSetName(){
        User user = new User("alice");
        assertEquals("alice", user.getName());
        user.setName("bob");
        assertEquals("bob", user.getName());
    }

    @Test
    public void testHashCode(){
        User user = new User("alice");
        assertEquals(Objects.hash("alice"), user.hashCode());
    }

    @Test
    public void testEqualsFalse(){
        User alice = new User("alice");
        User bob = new User("bob");
        assertFalse(alice.equals(bob));
    }

    @Test
    public void testEqualsTrue(){
        User alice = new User("alice");
        User alice2 = new User("alice");
        assertTrue(alice.equals(alice2));
    }

    @Test
    public void testEqualsNotUser(){
        User alice = new User("alice");
        assertFalse(alice.equals("alice"));
    }

    @Test
    public void testGetFriendList(){
        User alice = new User("alice");
        assertEquals(new ArrayList<>(), alice.getFriendList());
    }

    @Test
    public void testSetFriendList(){
        User alice = new User("alice");
        User bob = new User("bob");
        List<Friend> friendList = new ArrayList<>();
        Friend friend = new Friend(alice, bob);
        friendList.add(friend);
        alice.setFriendList(friendList);
        assertEquals(friendList, alice.getFriendList());
    }

    @Test
    public void testGetFriendByList(){
        User alice = new User("alice");
        assertEquals(new ArrayList<>(), alice.getFriendByList());
    }

    @Test
    public void testSetFriendByList(){
        User alice = new User("alice");
        User bob = new User("bob");
        List<Friend> friendList = new ArrayList<>();
        Friend friend = new Friend(bob, alice);
        friendList.add(friend);
        alice.setFriendByList(friendList);
        assertEquals(friendList, alice.getFriendByList());
    }
    
    @Test
    public void testGetAndSetUserStatus(){
        User alice = new User("alice");
        assertEquals("", alice.getStatus());
        alice.setStatus("Hello world");
        assertEquals("Hello world", alice.getStatus());
    }

    @Test
    public void testGetUserIsOnline(){
        User alice  = new User("alice");
        assertEquals("offline", alice.getIsOnline());
    }

    @Test
    public void testSetUserIsOnline(){
        User alice = new User("alice");
        alice.setIsOnline("online");
        assertEquals("online", alice.getIsOnline());
    }
    
    @Test
    public void testEquals() {
      User alice = new User("alice");
      assertEquals(alice, alice);
    }
    
    @Test
    public void testSetGroupsForAndSetModeratorsFor() {
      User alice = new User("alice");
      BasicGroup group = new BasicGroup();
      Set<BasicGroup> groups = new HashSet<>();
      groups.add(group);
      
      alice.setGroups(groups);
      alice.setModeratorFor(groups);
      assertEquals(1, alice.getGroups().size());
      assertEquals(1, alice.getModeratorFor().size());
    }
    
    @Test
    public void testPasswordSetterAndGetter() {
        User alice = new User("alice", "alice");
        assertEquals("alice", alice.getPassword());
        
        alice.setPassword("wonderland");
        assertEquals("wonderland", alice.getPassword());
    }
}
