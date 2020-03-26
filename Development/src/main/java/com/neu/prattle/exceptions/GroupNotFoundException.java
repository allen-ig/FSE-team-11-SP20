package com.neu.prattle.exceptions;

/***
 * An representation of an error which is thrown where a request has been made
 * for creation of a group object that already exists in the system.
 */
public class GroupNotFoundException extends RuntimeException {
  private static final long serialVersionUID = -4845176561270017894L;
  
  public GroupNotFoundException(String message)  {
    super(message);
  }
}
