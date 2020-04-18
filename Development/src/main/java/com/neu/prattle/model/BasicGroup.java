package com.neu.prattle.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
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
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JoinTable(
    name = "user_group",
    joinColumns = {@JoinColumn(name = "group_id", referencedColumnName = "id")},
    inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
  )
  private Set<User> members;

  /**
   * Moderators of the group.
   */
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JoinTable(
    name = "moderator_group",
    joinColumns = {@JoinColumn(name = "group_id", referencedColumnName = "id")},
    inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
  )
  private Set<User> moderators;

  private BasicGroup () {
    name = "not set";
    moderators = new HashSet<>();
    members = new HashSet<>();
  }
  
  public String getName() {
    return this.name;
  }


  public void setName(String name) {
    this.name = name;
  }


  /**
   * Checks if a BasicGroup has a User as a member
   * @param user is the User to check
   * @return a boolean representing the User's membership
   */
  public boolean hasMember(User user) {
    return members.contains(user);
  }

  /**
   * Checks if a User is a moderator of a BasicGroup
   * @param user is a User to check
   * @return a boolean representing a User's being a moderator
   */
  public boolean hasModerator(User user) {
    return moderators.contains(user);
  }

  /**
   * Returns the number of members of the BasicGroup
   * @return size of membership
   */
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
  
  public Set<User> getMembers() {
    return members;
  }
  
  public void setMembers(Set<User> members) {
    this.members = members;
  }
  
  public Set<User> getModerators() {
    return moderators;
  }
  
  public void setModerators(Set<User> moderators) {
    this.moderators = moderators;
  }

  /**
   * Returns a copy of a BasicGroup
   * @return the copy
   */
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

    public GroupBuilder setModerators(Set<User> users) {
      group.setModerators(users);
      return this;
    }

    public GroupBuilder setMembers(Set<User> users) {
      group.setMembers(users);
      return this;
    }

    public BasicGroup build() {
      return group;
    }

  }

}
