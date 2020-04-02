package com.neu.prattle.exceptions;

/**
 * Error when the sender for a REST request doesn't have proper clearance.
 */
public class GroupDeletedException extends RuntimeException {
  private static final long serialVersionUID = -4845176561270017893L;

  public GroupDeletedException(String message) {
    super(message);
  }
}