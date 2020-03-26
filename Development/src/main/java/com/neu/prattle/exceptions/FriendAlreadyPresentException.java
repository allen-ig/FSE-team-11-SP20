package com.neu.prattle.exceptions;

public class FriendAlreadyPresentException extends RuntimeException{

    private static final long serialVersionUID = -4845176561270017896L;

    public FriendAlreadyPresentException(String message)  {
            super(message);
        }

}
