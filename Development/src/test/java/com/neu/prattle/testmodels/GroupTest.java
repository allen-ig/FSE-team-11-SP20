package com.neu.prattle.testmodels;

import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GroupTest {

  BasicGroup.GroupBuilder builder;

  @Before
  public void setUp() {
    builder = BasicGroup.groupBuilder();
  }

  @Test
  public void testName() {
    builder.setName("tim");
    assertEquals("tim", builder.build().getName());
  }

  @Test
  public void testSetMembers() {

    ArrayList<User> users= new ArrayList<>();
    users.add(new User("user1"));
    users.add(new User("user2"));

    assertEquals(0, builder.build().getMembers().size());

    builder.setMembers(users);

    ArrayList<String> expected = new ArrayList<>();
    expected.add("user1");
    expected.add("user2");

    assertTrue(builder.build().getMembers().contains(expected.get(0)));
    assertTrue(builder.build().getMembers().contains(expected.get(1)));
    assertFalse(builder.build().getMembers().contains("this shouldnt be here"));
  }

  @Test
  public void hasMember() {
    ArrayList<User> users= new ArrayList<>();
    users.add(new User("user1"));
    users.add(new User("user2"));
    builder.setMembers(users);

    assertTrue(builder.build().hasMember(new User("user1")));
    assertFalse(builder.build().hasMember(new User("thisUserIsNew")));
  }

  @Test
  public void testSetModerators() {
    ArrayList<User> users= new ArrayList<>();
    users.add(new User("user1"));
    users.add(new User("user2"));

    assertEquals(0, builder.build().getModerators().size());

    builder.setModerators(users);

    ArrayList<String> expected = new ArrayList<>();
    expected.add("user1");
    expected.add("user2");
    assertTrue(builder.build().getModerators().contains(expected.get(0)));
    assertTrue(builder.build().getModerators().contains(expected.get(1)));
    assertFalse(builder.build().getModerators().contains("this shouldnt be here"));
  }

  @Test
  public void testHasModerator() {
    ArrayList<User> users= new ArrayList<>();
    users.add(new User("user1"));
    users.add(new User("user2"));
    builder.setModerators(users);

    assertTrue(builder.build().hasModerator(new User("user1")));
    assertFalse(builder.build().hasModerator(new User("thisUserDoesNotExit")));
  }

  @Test
  public void testSize() {
    ArrayList<User> users= new ArrayList<>();
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
    ArrayList<User> users= new ArrayList<>();
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