package com.neu.prattle.exceptions;

/**
 * An Exception representing the case when a User tries to add a Friend that they already have
 */
public class FriendAlreadyPresentException extends RuntimeException{

    private static final long serialVersionUID = -4845176561270017896L;

    public FriendAlreadyPresentException(String message)  {
            super(message);
        }

}
