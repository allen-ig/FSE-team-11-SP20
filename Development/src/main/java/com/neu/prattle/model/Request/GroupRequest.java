package com.neu.prattle.model.request;


/**
 * POJO for communications from client to controller. String holder for @Consumes JSON.
 */
public class GroupRequest {
  private String sender;
  private String user;
  private String group;

  public String getSender() {
    return sender;
  }

  public String getUser() {
    return user;
  }

  public String getGroup() {
    return group;
  }

  private void setUser(String user) {
    this.user = user;
  }

  private void setGroup(String group) {
    this.group = group;
  }

  private void setSender(String sender) {
    this.sender = sender;
  }

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
