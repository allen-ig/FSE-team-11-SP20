package com.neu.prattle.service;

import com.neu.prattle.model.Friend;
import com.neu.prattle.model.User;

import java.util.Collection;
import java.util.Optional;

public interface FriendService {

    void sendFriendRequest(Friend friend);

    void approveFriendRequest(User sender, User recipient, boolean isApproved);

    Collection<Friend> findAllFriends(String username);

    Optional<Friend> findFriendByUsers(User sender, User recipient);

    void deleteFriend(Friend friend);

    boolean isTest();
}
