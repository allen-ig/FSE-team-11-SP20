package com.neu.prattle;


import com.neu.prattle.main.PrattleApplication;
import com.neu.prattle.model.User;
import com.neu.prattle.service.FriendService;
import com.neu.prattle.service.FriendServiceImpl;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.MessageServiceImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import com.neu.prattle.service.UserServiceWithGroups;
import com.neu.prattle.service.UserServiceWithGroupsImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TestSimple {

	private UserService as;
	private MessageService msgs;
	private FriendService frs;
	private UserServiceWithGroups uswgs;
	private PrattleApplication prattleApplication;
	
	@Before
	public void setUp() {
		System.setProperty("testing", "true");
		as = UserServiceImpl.getInstance();
		msgs = MessageServiceImpl.getInstance();
		frs = FriendServiceImpl.getInstance();
		uswgs = UserServiceWithGroupsImpl.getInstance();
		prattleApplication = new PrattleApplication();
		assertTrue(as.isTest());
	}

	@After
	public void tearDown(){
		System.setProperty("testing", "false");
	}

	// This method just tries to add 
	@Test(timeout=40000)
	public void setUserTest(){
		assertEquals(Optional.empty(), as.findUserByName("Mike"));
		as.addUser(new User("Mike"));
		User mike = as.findUserByName("Mike").get();
		assertEquals("Mike", mike.getName());
		as.deleteUser(mike);
	}


	// Performance testing to benchmark our number of users that can be added 
	// in 2 sec
	@Test(timeout = 30000)
	public void checkPrefTest(){
		for(int i=0; i < 5; i++) {
			as.addUser(new User("TEST_NAME"+i));
		}
		for (int i=0; i< 5; i++){
			as.deleteUser(as.findUserByName("TEST_NAME"+i).get());
		}
	}

	@Test
	public void prattleApplicationTest() {
		assertEquals(3, prattleApplication.getClasses().size());
	}
	
	@Test
  public void testServiceInstances() {
    System.setProperty("testing", "false");
    
    UserService us = UserServiceImpl.getInstance();
    UserServiceWithGroups ugs = UserServiceWithGroupsImpl.getInstance();
    FriendService fs = FriendServiceImpl.getInstance();
    MessageService ms = MessageServiceImpl.getInstance();
    
    assertNotEquals(as, us);
    assertNotEquals(frs, fs);
    assertNotEquals(msgs, ms);
    assertNotEquals(uswgs, ugs);
    
	  System.setProperty("testing", "true");
   
  }
}
