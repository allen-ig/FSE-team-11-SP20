package com.neu.prattle.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * POJO for a Group of users. Could fairly easily be used instead of User. Has Builder and
 * classically named getters/setters if we need a convenient way to create them from js.
 * <p>
 * Needs Doing: (1) Currently hashing by name so will have collisions with users/sameNamedGroups if
 * not careful. (2) I'm not sure how to place the groupBuilder() function in the interface without
 * dependency to this concrete class, so currently the interface is purely ornamental.
 */
@Entity
@Table(name="nuslack_group")
public class BasicGroup {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  
  /**
   * Name of the group
   */
  @Column(name = "name", unique = true)
  private String name;
  /**
   * Hashset of User for the members of the group.
   */
  @ManyToMany
  @JoinTable(
    name = "user_group",
    joinColumns = {@JoinColumn(name = "group_id")},
    inverseJoinColumns = {@JoinColumn(name = "user_id")}
  )
  private Set<User> members;

  /**
   * Moderators of the group.
   */
  @ManyToMany
  @JoinTable(
    name = "moderator_group",
    joinColumns = {@JoinColumn(name = "group_id")},
    inverseJoinColumns = {@JoinColumn(name = "user_id")}
  )
  private Set<User> moderators;

  private BasicGroup () {
    name = "not set";
    members = new HashSet<>();
    moderators = new HashSet<>();
  }
  
  public String getName() {
    return this.name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public boolean hasMember(User user) {
    return members.contains(user);
  }


  public List<String> getMembers() {
    ArrayList<String> names = new ArrayList<>();
    for (User m : members) {
      names.add(m.getName());
    }
    return names;
  }


  public void setMembers(Collection<User> newMembers) {
    members.addAll(newMembers);
  }


  public boolean hasModerator(User user) {
    return moderators.contains(user);
  }


  public List<String> getModerators() {
    ArrayList<String> names = new ArrayList<>();
    for (User m : moderators) {
      names.add(m.getName());
    }
    return names;
  }


  public void setModerators(Collection<User> mods) {
    moderators.addAll(mods);
  }


  public int size() {
    return members.size();
  }


  public int hashCode() {
    return Objects.hash(name);
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof BasicGroup)) {
      return false;
    } else {
      BasicGroup gObj = (BasicGroup) obj;
      return gObj.name.equals(this.name);
    }
  }

  public BasicGroup copy(){
    return groupBuilder().setName(name).setMembers(members).setModerators(moderators).build();
  }

  public static GroupBuilder groupBuilder() {
    return new GroupBuilder();
  }

  /**
   * Builder so we can conveniently create instances with @Link.
   */
  public static class GroupBuilder {

    BasicGroup group;

    public GroupBuilder() {
      group = new BasicGroup();
    }

    public GroupBuilder setName(String givenName) {
      group.setName(givenName);
      return this;
    }

    public GroupBuilder setModerators(Collection<User> users) {
      group.setModerators(users);
      return this;
    }

    public GroupBuilder setMembers(Collection<User> users) {
      group.setMembers(users);
      return this;
    }

    public BasicGroup build() {
      return group;
    }

  }

}
