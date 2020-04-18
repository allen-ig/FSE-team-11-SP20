package com.neu.prattle.model.request;


/**
 * POJO for communications from client to controller. String holder for @Consumes JSON.
 */
public class GroupRequest {
  private String sender;
  private String user;
  private String group;

  /**
   * Returns the sender
   * @return sender
   */
  public String getSender() {
    return sender;
  }

  /**
   * Returns the user
   * @return user
   */
  public String getUser() {
    return user;
  }

  /**
   * Returns the group
   * @return group
   */
  public String getGroup() {
    return group;
  }

  /**
   * Updates the user
   * @param user is the username of the user to update with
   */
  private void setUser(String user) {
    this.user = user;
  }

  /**
   * Updates the group
   * @param group is the name of the group to update with
   */
  private void setGroup(String group) {
    this.group = group;
  }

  /**
   * Update the sender
   * @param sender is the username of the sender to update with
   */
  private void setSender(String sender) {
    this.sender = sender;
  }

  /**
   * Returns a GroupRequestBuilder
   * @return an instance of GroupRequestBuilder
   */
  public static GroupRequestBuilder groupRequestBuilder() {
    return new GroupRequestBuilder();
  }

  public static class GroupRequestBuilder {
    GroupRequest request;

    public GroupRequestBuilder() {
      request = new GroupRequest();
    }

    public GroupRequestBuilder setSender(String sender) {
      request.setSender(sender);
      return this;
    }

    public GroupRequestBuilder setUser(String userName) {
      request.setUser(userName);
      return this;
    }

    public GroupRequestBuilder setGroup(String groupName) {
      request.setGroup(groupName);
      return this;
    }

    public GroupRequest build() {
      return request;
    }
  }


}
