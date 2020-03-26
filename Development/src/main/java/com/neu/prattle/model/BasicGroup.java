package com.neu.prattle.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
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
   * ArrayList of User for the members of the group.
   */
  @ManyToMany(cascade = {CascadeType.ALL})
  @JoinTable(
    name = "user_group",
    joinColumns = {@JoinColumn(name = "group_id"), },
    inverseJoinColumns = {@JoinColumn(name = "user_id")}
  )
  private List<User> members = new ArrayList<>();

  /**
   * Moderators of the group.
   */
  @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
  @JoinTable(
    name = "moderator_group",
    joinColumns = {@JoinColumn(name = "group_id")},
    inverseJoinColumns = {@JoinColumn(name = "user_id")}
  )
  private List<User> moderators = new ArrayList<>();

  private BasicGroup () {
    name = "not set";
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
  
  public void setMembers(List<User> members) {
    this.members = members;
  }
  
  public void setModerators(List<User> moderators) {
    this.moderators = moderators;
  }
  
  public List<User> getMembers() {
    return members;
  }
  
  public List<User> getModerators() {
    return moderators;
  }
  
  public boolean hasModerator(User user) {
    return moderators.contains(user);
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
   * Builder so we can conveniently create instances with @Consumes.
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

    public GroupBuilder setModerators(List<User> users) {
      group.setModerators(users);
      return this;
    }

    public GroupBuilder setMembers(List<User> users) {
      group.setMembers(users);
      return this;
    }

    public BasicGroup build() {
      return group;
    }

  }

}
