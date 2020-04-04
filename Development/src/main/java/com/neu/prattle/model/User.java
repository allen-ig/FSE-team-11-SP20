package com.neu.prattle.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.*;

/***
 * A User object represents a basic account information for a user.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */
@Entity
@Table(name="nuslack_user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

  @Column(name = "name", unique = true)
	private String name;

	private String status;

  @JsonIgnore
	@ManyToMany(mappedBy = "members", cascade = {CascadeType.ALL})
  private Set<BasicGroup> groups;
  @JsonIgnore
  @ManyToMany(mappedBy = "moderators", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  private Set<BasicGroup> moderatorFor;
  @JsonIgnore
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Friend> friendList = new ArrayList<>();
  @JsonIgnore
	@OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private List<Friend> friendByList = new ArrayList<>();
  
  public User() {
    groups = new HashSet<>();
    moderatorFor = new HashSet<>();
    this.status = "";
  }
  
  public User(String name) {
    this.name = name;
    groups = new HashSet<>();
    moderatorFor = new HashSet<>();
    this.status = "";
  }
  @JsonIgnore
    public List<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }
  @JsonIgnore
    public List<Friend> getFriendByList() {
        return friendByList;
    }

    public void setFriendByList(List<Friend> friendByList) {
        this.friendByList = friendByList;
    }

	public int getId() {
        return this.id;
    }
  
  /***
   * Returns the hashCode of this object.
   *
   * As name can be treated as a sort of identifier for
   * this instance, we can use the hashCode of "name"
   * for the complete object.
   *
   *
   * @return hashCode of "this"
   */
  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  /***
   * Makes comparison between two user accounts.
   *
   * Two user objects are equal if their name are equal ( names are case-sensitive )
   *
   * @param obj Object to compare
   * @return a predicate value for the comparison.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this){
      return true;
    }
    if (!(obj instanceof User)) {
      return false;
    }
    User user = (User) obj;
    return user.name.equals(this.name) && user.getId()==this.getId();
  }
  @JsonIgnore
  public Set<BasicGroup> getGroups() {
    return groups;
  }
  
  public void setGroups(Set<BasicGroup> groups) {
    this.groups = groups;
  }
  @JsonIgnore
  public Set<BasicGroup> getModeratorFor() {
    return moderatorFor;
  }
  
  public void setModeratorFor(Set<BasicGroup> moderatorFor) {
    this.moderatorFor = moderatorFor;
  }
  
  public String getStatus() {
    return status;
  }
  
  public void setStatus(String status) {
    this.status = status;
  }
}
