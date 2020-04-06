package com.neu.prattle.model;

import java.util.Collection;
import java.util.List;

public interface Group {

  String getName();

  void setName(String name);

  boolean hasMember(User user);

  void addMember(User user);

  List<String> getMembers();

  void setMembers(Collection<User> members);

  boolean isModerator(User user);

  void addModerator(User user);

  List<String> getModerators();

  void setModerators(Collection<User> moderators);

  int size();

  int hashCode();

  boolean equals(Object obj);

  Group copy();

}
