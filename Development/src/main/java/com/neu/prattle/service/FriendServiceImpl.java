package com.neu.prattle.service;

import com.neu.prattle.model.Friend;
import com.neu.prattle.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FriendServiceImpl implements FriendService{

    private Configuration config = new Configuration().configure("hibernate.cfg.xml")
            .addAnnotatedClass(User.class).addAnnotatedClass(Friend.class);
    private ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
    private SessionFactory sessionFactory = config.buildSessionFactory(registry);
    private boolean isTest;

    private FriendServiceImpl() { }

    private static FriendServiceImpl friendService;
    private static FriendServiceImpl testingFriendService;
    static {
        friendService = new FriendServiceImpl();
        friendService.isTest = false;
    }

    static {
        testingFriendService = new FriendServiceImpl();
        Configuration testingConfig = new Configuration().configure("testing-hibernate.cfg.xml")
                .addAnnotatedClass(User.class).addAnnotatedClass(Friend.class);
        testingFriendService.config = testingConfig;
        ServiceRegistry testingRegistry = new StandardServiceRegistryBuilder().applySettings(testingConfig.getProperties()).build();
        testingFriendService.registry = testingRegistry;
        testingFriendService.sessionFactory = testingConfig.buildSessionFactory(testingRegistry);
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
    public void sendFriendRequest(Friend friend) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try{
            session.save(friend);
            session.getTransaction().commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            session.disconnect();
            session.close();
        }
    }

    @Override
    public void approveFriendRequest(User sender, User recipient, boolean isApproved) {
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
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            session.disconnect();
            session.close();
        }
    }

    @Override
    public Collection<Friend> findAllFriends(String username) {
        List<Friend> friends = new ArrayList<>();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        String strQuery = "SELECT f FROM Friend f WHERE f.status = :status and (f.recipient.name = :username or f.sender.name = :username)";
        Query query = session.createQuery(strQuery);
        query.setParameter("status", "APPROVED");
        query.setParameter("username", username);
        try{
            friends = query.getResultList();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            session.disconnect();
            session.close();
        }
        return friends;
    }

    @Override
    public boolean isTest() {
        return isTest;
    }
}