package com.neu.prattle.testmodels;

import com.neu.prattle.model.User;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;

public class UserTest {

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
}
