package com.neu.prattle.exceptions;

/***
 * An representation of an error which is thrown where a request has been made
 * for retrieval of a user object that does not exist in the system.
 */
public class UserNotFoundException extends RuntimeException {
  private static final long serialVersionUID = -4845176561270017895L;
  
  public UserNotFoundException(String message)  {
    super(message);
  }
}
