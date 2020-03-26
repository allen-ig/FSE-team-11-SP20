package com.neu.prattle.service;

import com.neu.prattle.model.Friend;
import com.neu.prattle.model.User;

import java.util.Collection;

public interface FriendService {

    void sendFriendRequest(Friend friend);

    void approveFriendRequest(User sender, User recipient, boolean isApproved);

    Collection<Friend> findAllFriends(String username);

    boolean isTest();
}
