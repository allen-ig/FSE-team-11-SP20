package com.neu.prattle.model;

import java.util.ArrayList;
import java.util.Collection;

public interface Group {

  public String getName();

  public void setName(String name);

  public boolean hasMember(User user);

  public void addMember(User user);

  public ArrayList<String> getMembers();

  public void setMembers(Collection<User> members);

  public boolean isModerator(User user);

  public void addModerator(User user);

  public ArrayList<String> getModerators();

  public void setModerators(Collection<User> moderators);

  public int size();

  public int hashCode();

  public boolean equals(Object obj);

  public Group copy();

}
