package com.neu.prattle.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

	@ManyToMany(mappedBy = "members")
  private List<BasicGroup> groups = new ArrayList<>();

  @ManyToMany(mappedBy = "moderators")
  private List<BasicGroup> moderatorFor = new ArrayList<>();
	
	public User() {

	}

    public User(String name) {
        this.name = name;
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
  
  public List<BasicGroup> getGroups() {
    return groups;
  }
  
  public void setGroups(List<BasicGroup> groups) {
    this.groups = groups;
  }
  
  public List<BasicGroup> getModeratorFor() {
    return moderatorFor;
  }
  
  public void setModeratorFor(List<BasicGroup> moderatorFor) {
    this.moderatorFor = moderatorFor;
  }
}
