package com.neu.prattle.service;

import com.neu.prattle.model.User;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/***
 * Implementation of {@link UserService}
 *
 * It stores the user accounts in-memory, which means any user accounts
 * created will be deleted once the application has been restarted.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */
public class UserServiceImpl implements UserService {

    /***
     * UserServiceImpl is a Singleton class.
     */
    private UserServiceImpl() { }
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory("prattle");
    private static UserService accountService;

    static {
        accountService = new UserServiceImpl();
    }

    /**
     * Call this method to return an instance of this service.
     * @return this
     */
    public static UserService getInstance() {
        return accountService;
    }

    /***
     *
     * @param name -> The name of the user.
     * @return An optional wrapper supplying the user.
     */
    @Override
    public Optional<User>  findUserByName(String name){
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        String strQuery = "SELECT u FROM User u  WHERE u.name = :name";
        TypedQuery<User> tq = em.createQuery(strQuery, User.class);
        tq.setParameter("name", name);
        User user;
        try {
            // Get matching User object and output
            user = tq.getSingleResult();
            return Optional.of(user);
        }
        catch(NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public synchronized void addUser(User user){
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;

        try {
            et = em.getTransaction();
            et.begin();
            em.persist(user);
            et.commit();
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
