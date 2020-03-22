package com.neu.prattle.service;

import com.neu.prattle.model.Friend;
import com.neu.prattle.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

public class FriendServiceImpl implements FriendService{

    private FriendServiceImpl() { }
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory("prattle");
    private static FriendService friendService;

    static {
        friendService = new FriendServiceImpl();
    }

    public static FriendService getInstance() {
        return friendService;
    }

    @Override
    public void sendFriendRequest(Friend friend) {
//        Friend friend = new Friend(sender, recipient);
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;
        try{
            et = em.getTransaction();
            et.begin();
            em.persist(friend);
            et.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            em.close();
        }
    }

    @Override
    public void approveFriendRequest(int friendId, boolean isApproved) {
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;
        try{
            et = em.getTransaction();
            et.begin();
            Friend friend = em.find(Friend.class, friendId);
            friend.setStatus(isApproved ? "APPROVED" : "DENIED");
            et.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            em.close();
        }
    }

    @Override
    public Collection<Friend> findAllFriends(User user) {
        Collection<Friend> friends = new ArrayList<>();
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        String strQuery = "SELECT f FROM Friend f WHERE f.status = :status";
        TypedQuery<Friend> tq = em.createQuery(strQuery, Friend.class);
        tq.setParameter("status", true);
        try{
            friends = tq.getResultList();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            em.close();
        }
        return friends;
    }
}
