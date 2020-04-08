package com.neu.prattle.service;

import com.neu.prattle.model.User;

import javax.swing.text.html.Option;
import java.util.Optional;

/***
 * Acts as an interface between the data layer and the
 * servlet controller.
 *
 * The controller is responsible for interfacing with this instance
 * to perform all the CRUD operations on user accounts.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 *
 */
public interface UserService {
    /***
     * Returns an optional object which might be empty or wraps an object
     * if the System contains a {@link User} object having the same name
     * as the parameter.
     *
     * @param name The name of the user
     * @return Optional object.
     */
    Optional<User> findUserByName(String name);

    /**
     * Returns an optional object that contains a User with related group objects. Only use if need access to groups.
     * @param name - the name of the user.
     * @return Optional object.
     */
    Optional<User> findUserByNameWithGroups(String name);

    /***
     * Tries to add a user in the system
     * @param user User object
     *
     */
    void addUser(User user);
  
  /**
   * Gets the status of a user
   * @param username the user's username
   * @return the user status
   */
  String getUserStatus(String username);
  
  /**
   * Sets the status of the user
   * @param username user's username
   * @param status status to be set
   */
    void setUserStatus(String username, String status);

    /***
     * Tries to delete a User in the system
     * @param user User object
     */
    void deleteUser(User user);

    /**
     * set user's online/offline status
     * @param username user's username
     * @param isOnline the user is online or not
     */
    void setUserIsOnline(String username, boolean isOnline);

    /**
     * Returns value indicating whether UserService is configured for testing
     * @return true if configured for testing else false
     */
    boolean isTest();
}
