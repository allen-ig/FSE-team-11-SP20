package com.neu.prattle.service;

import com.neu.prattle.model.Friend;
import com.neu.prattle.model.User;

import java.util.Collection;
import java.util.Optional;

/**
 * A class to handle CRUD DB operations for persistent Friend objects
 */
public interface FriendService {

    /**
     * Sends a friend request
     * @param friend is a Friend object representing the potential friendship
     */
    void sendFriendRequest(Friend friend);

    /**
     * Approves or denies a friend request
     * @param sender is the User sending the request
     * @param recipient is the User receiving the request
     * @param isApproved is the answer of the recipient
     */
    void approveFriendRequest(User sender, User recipient, boolean isApproved);

    /**
     * Returns all Friends for a User
     * @param user is a User
     * @return all Friends that a User has if they have any
     */
    Collection<Friend> findAllFriends(User user);

    /**
     * Returns the Friend object representing the friendship between two Users
     * @param sender is the sender of the friend request
     * @param recipient is the recipient of the friend request
     * @return the Friend object if it exists, else Optional.Empty()
     */
    Optional<Friend> findFriendByUsers(User sender, User recipient);

    /**
     * Removes a Friend
     * @param friend is the Friend to remove
     */
    void deleteFriend(Friend friend);

    /**
     * Specifies if the instance of this is configured to use the test databse
     * @return true if test database, else false
     */
    boolean isTest();
}
