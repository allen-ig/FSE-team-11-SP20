package com.neu.prattle.managers;


import com.neu.prattle.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class UserManager {
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory("prattle");

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
