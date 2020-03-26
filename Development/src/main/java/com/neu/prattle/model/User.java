package com.neu.prattle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;


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
	public int getId(){return id;}

	public void setName(String name) {
		this.name = name;
	}

  @Column(name = "name", unique = true)
	private String name;

	@OneToMany(mappedBy = "sender")
    private List<Friend> friendList = new ArrayList<>();

	@OneToMany(mappedBy = "recipient")
    private List<Friend> friendByList = new ArrayList<>();

    public List<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }

    public List<Friend> getFriendByList() {
        return friendByList;
    }

    public void setFriendByList(List<Friend> friendByList) {
        this.friendByList = friendByList;
    }

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
        if (!(obj instanceof User))
            return false;

        User user = (User) obj;
        return user.name.equals(this.name);
    }
}
