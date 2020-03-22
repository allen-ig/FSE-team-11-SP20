package com.neu.prattle.main;

import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;

public class TestMain {
  public static void main(String[] args){
    UserService service = UserServiceImpl.getInstance();
    service.addUser(new User("Carl"));
    //service.addUser(new User("Josh"));
    //service.addUser(new User("Shelly"));
    //service.addUser(new User("Alice"));


  }
}
