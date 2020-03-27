package com.neu.prattle.exceptions;

/**
 * Error when the sender for a REST request doesn't have proper clearance.
 */
public class SenderNotAuthorizedException extends RuntimeException {
  private static final long serialVersionUID = -4845176561270017894L;

  public SenderNotAuthorizedException(String message) {
    super(message);
  }
}
