package com.neu.prattle.managers;


import com.neu.prattle.model.TestUser;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class TestUserManager {
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory("prattle");

    public static void addTestUser(String username, String password){
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;

        try {
            et = em.getTransaction();
            et.begin();

            TestUser testUser = new TestUser(username, password);

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
