package com.neu.prattle.managers;


import com.neu.prattle.model.User;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class UserManager {
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory("prattle");

    public static void findUser(String username){
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        String strQuery = "SELECT u FROM User u  WHERE u.name = :name";
        TypedQuery<User> tq = em.createQuery(strQuery, User.class);
        tq.setParameter("name", username);
        User user;
        try {
            // Get matching User object and output
            user = tq.getSingleResult();
            System.out.println(user.getName());
        }
        catch(NoResultException ex) {
            System.out.println("Could not find user");
        }
    }

    public static void addUser(String username){
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;

        try {
            et = em.getTransaction();
            et.begin();
            User testUser = new User(username);
            em.persist(testUser);
            et.commit();;
        } catch (Exception e){
            if (et != null){
                et.rollback();
            }
            e.printStackTrace();
        } finally{
            em.close();
        }

    }

}
