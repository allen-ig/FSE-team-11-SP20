package com.neu.prattle.service;

import com.neu.prattle.exceptions.FriendAlreadyPresentException;
import com.neu.prattle.main.HibernateUtil;
import com.neu.prattle.model.Friend;
import com.neu.prattle.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FriendServiceImpl implements FriendService{
  
    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private boolean isTest;

    private Logger logger = LogManager.getLogger();

    private FriendServiceImpl() { }

    private static FriendServiceImpl friendService;
    private static FriendServiceImpl testingFriendService;
    
    
    static {
        friendService = new FriendServiceImpl();
        friendService.isTest = false;
    }

    static {
        testingFriendService = new FriendServiceImpl();
        testingFriendService.sessionFactory = HibernateUtil.getTestSessionFactory();
        testingFriendService.isTest = true;
    }
    
    public static FriendService getInstance() {
        try{
            if (System.getProperty("testing").equals("true")){
                return testingFriendService;
            }
        } catch (NullPointerException e){
            return friendService;
        }
        return friendService;
    }
    @Override
    public synchronized void sendFriendRequest(Friend friend) {
        if (findFriendByUsers(friend.getSender(), friend.getRecipient()).isPresent() ||
                findFriendByUsers(friend.getRecipient(), friend.getSender()).isPresent()){
            throw new FriendAlreadyPresentException(
                    String.format("Friend relationship between %s and %s already present!",
                            friend.getSender().getName(), friend.getRecipient().getName()));
        }
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.save(friend);
            session.getTransaction().commit();
            logger.info("Friend request sent from "
                    + friend.getSender() + " to " + friend.getRecipient());
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            session.disconnect();
            session.close();
        }
    }

    @Override
    public synchronized void approveFriendRequest(User sender, User recipient, boolean isApproved) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        String strQuery = "SELECT f FROM Friend f WHERE f.sender=:sender AND f.recipient=:recipient";
        Query query = session.createQuery(strQuery);
        query.setParameter("sender", sender);
        query.setParameter("recipient", recipient);
        try{
            Friend friend = (Friend) query.getSingleResult();
            friend.setStatus(isApproved ? "APPROVED" : "DENIED");
            session.getTransaction().commit();
            logger.info("Friend request from " + sender + " to "
                    + recipient + " was responded to with " + isApproved);
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            session.disconnect();
            session.close();
        }
    }

    @Override
    public Collection<Friend> findAllFriends(User user) {
        List<Friend> friends = new ArrayList<>();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        String strQuery = "SELECT f FROM Friend f WHERE f.status = :status and (f.recipient = :user or f.sender = :user)";
        Query query = session.createQuery(strQuery);
        query.setParameter("status", "APPROVED");
        query.setParameter("user", user);
        try{
            friends = query.getResultList();
            logger.info(friends.size() + " found for " + user.getName());
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            session.disconnect();
            session.close();
        }
        return friends;
    }

    @Override
    public Optional<Friend> findFriendByUsers(User sender, User recipient){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        String strQuery = "SELECT f FROM Friend f WHERE f.sender = :sender AND f.recipient = :recipient";
        Query query = session.createQuery(strQuery);
        query.setParameter("sender", sender);
        query.setParameter("recipient", recipient);
        try{
            Friend friend = (Friend) query.getSingleResult();
            logger.info("Friend found for " + sender + " and " + recipient);
            return Optional.of(friend);
        }catch (NoResultException e){
            logger.info("No friend found for " + sender + " and " + recipient);
            return Optional.empty();
        }finally {
            session.disconnect();
            session.close();
        }
    }

    @Override
    public synchronized void deleteFriend(Friend friend){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.delete(friend);
            session.getTransaction().commit();
            logger.info("Friend for " + friend.getSender()
                    + " and " + friend.getRecipient() + " deleted");
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            session.disconnect();
            session.close();
        }
    }

    @Override
    public boolean isTest() {
        return isTest;
    }
}
