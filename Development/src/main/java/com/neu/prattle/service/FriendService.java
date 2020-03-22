package com.neu.prattle.service;

import com.neu.prattle.model.Friend;
import com.neu.prattle.model.User;

import java.util.Collection;

public interface FriendService {

    void sendFriendRequest(Friend friend);

    void approveFriendRequest(int friendId, boolean isApproved);

    Collection<Friend> findAllFriends(User user);
}
