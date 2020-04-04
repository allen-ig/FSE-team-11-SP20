package com.neu.prattle.model;

import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;

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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class GroupTest {

  private BasicGroup.GroupBuilder builder;

  @Before
  public void setUp() {
    System.setProperty("testing","true");
    builder = BasicGroup.groupBuilder();
  }
  
  @After
  public void tearDown(){
    System.setProperty("testing", "false");
  }

  @Test
  public void testName() {
    builder.setName("tim");
    assertEquals("tim", builder.build().getName());
  }

  @Test
  public void testSetMembers() {
    Set<User> users= new HashSet<>();
    
    User user1 = new User("user1");
    User user2 = new User("user2");
    users.add(user1);
    users.add(user2);

    assertEquals(0, builder.build().getMembers().size());

    builder.setMembers(users);

    List<User> expected = new ArrayList<>();
    expected.add(user1);
    expected.add(user2);

    assertTrue(builder.build().getMembers().contains(expected.get(0)));
    assertTrue(builder.build().getMembers().contains(expected.get(1)));
    assertFalse(builder.build().getMembers().contains(new User("user3")));
  }

  @Test
  public void hasMember() {
    Set<User> users= new HashSet<>();
    
    User user1 = new User("user1");
    User user2 = new User("user2");
    users.add(user1);
    users.add(user2);
    
    builder.setMembers(users);

    assertTrue(builder.build().hasMember(user1));
    assertFalse(builder.build().hasMember(new User("thisUserIsNew")));
  }

  @Test
  public void testSetModerators() {
    Set<User> users= new HashSet<>();
    User user1 = new User("user1");
    User user2 = new User("user2");
    users.add(user1);
    users.add(user2);
    
    assertEquals(0, builder.build().getModerators().size());

    builder.setModerators(users);

    ArrayList<User> expected = new ArrayList<>();
    expected.add(user1);
    expected.add(user2);
    assertTrue(builder.build().getModerators().contains(expected.get(0)));
    assertTrue(builder.build().getModerators().contains(expected.get(1)));
    assertFalse(builder.build().getModerators().contains(new User("User not present")));
  }

  @Test
  public void testHasModerator() {
    Set<User> users= new HashSet<>();
    
    users.add(new User("user1"));
    users.add(new User("`user2"));
    builder.setModerators(users);

    assertTrue(builder.build().hasModerator(new User("user1")));
    assertFalse(builder.build().hasModerator(new User("thisUserDoesNotExit")));
  }

  @Test
  public void testSize() {
    Set<User> users= new HashSet<>();
    
    users.add(new User("user1"));
    users.add(new User("user2"));

    assertEquals(0, builder.build().size());

    builder.setMembers(users);

    assertEquals(2, builder.build().size());
  }

  @Test
  public void testHashcode() {
    BasicGroup group1 = BasicGroup.groupBuilder().setName("NotUser1").build();

    BasicGroup group2 = BasicGroup.groupBuilder().setName("user1").build();

    assertNotEquals(group1.getName(), group2.getName());
    assertNotEquals(Objects.hash(group1.getName()), Objects.hash(group2.getName()));

    assertNotEquals(group1.hashCode(), group2.hashCode());

    BasicGroup group3 = builder.setName("user1").build();

    assertEquals(group2.hashCode(), group3.hashCode());

  }

  @Test
  public void testEquals() {
    BasicGroup group1 = BasicGroup.groupBuilder().setName("user1").build();


    BasicGroup group2 = BasicGroup.groupBuilder().setName("user2").build();

    assertNotEquals(group1, group2);

    BasicGroup group3 = BasicGroup.groupBuilder().setName("user1").build();

    assertNotEquals(group2, group3);
    assertEquals(group1, group3);

    assertNotEquals(group1, new User("group1"));
  }

  @Test
  public void testCopy() {
    Set<User> users= new HashSet<>();
    
    users.add(new User("user1"));
    users.add(new User("user2"));
    BasicGroup group1 = builder.setName("group1").setMembers(users).setModerators(users).build();
    BasicGroup group2 = group1.copy();

    assertEquals(group1, group2);
    assertEquals(group1.getMembers(), group2.getMembers());
    assertEquals(group1.getModerators(), group2.getModerators());
    assertEquals(group1.getName(), group2.getName());

  }





}