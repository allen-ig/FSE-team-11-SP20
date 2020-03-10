package com.neu.prattle.main;
import com.neu.prattle.managers.UserManager;

public class TestMain {

  public static void main(String[] args){
    UserManager.addUser("Thomas");
    UserManager.addUser("Sarah");
    UserManager.findUser("Thomas");
    UserManager.findUser("Sarah");
    UserManager.findUser("Amy");
  }
}
