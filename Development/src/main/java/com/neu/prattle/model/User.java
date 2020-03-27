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
import javax.persistence.ManyToMany;
import javax.persistence.Table;


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

	@ManyToMany(mappedBy = "members", cascade = {CascadeType.ALL})
  private Set<BasicGroup> groups;

  @ManyToMany(mappedBy = "moderators", cascade = {CascadeType.ALL})
  private Set<BasicGroup> moderatorFor;
	
	public User() {
    groups = new HashSet<>();
    moderatorFor = new HashSet<>();
	}

	public User(String name) {
	  this.name = name;
    groups = new HashSet<>();
    moderatorFor = new HashSet<>();
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
    if (!(obj instanceof User)) {
      return false;
    }
    User user = (User) obj;
    return user.name.equals(this.name);
  }
  
  public Set<BasicGroup> getGroups() {
    return groups;
  }
  
  public void setGroups(Set<BasicGroup> groups) {
    this.groups = groups;
  }
  
  public Set<BasicGroup> getModeratorFor() {
    return moderatorFor;
  }
  
  public void setModeratorFor(Set<BasicGroup> moderatorFor) {
    this.moderatorFor = moderatorFor;
  }
}
