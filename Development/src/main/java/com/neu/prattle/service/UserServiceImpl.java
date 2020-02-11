package com.neu.prattle.service;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
    private UserServiceImpl() {

    }

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

    private Set<User> userSet = new HashSet<>();

    /***
     *
     * @param name -> The name of the user.
     * @return An optional wrapper supplying the user.
     */
    @Override
    public Optional<User> findUserByName(String name) {
        final User user = new User(name);
        if (userSet.contains(user))
            return Optional.of(user);
        else
            return Optional.empty();
    }

    @Override
    public synchronized void addUser(User user) {
        if (userSet.contains(user))
            throw new UserAlreadyPresentException(String.format("User already present with name: %s", user.getName()));

        userSet.add(user);
    }
}
