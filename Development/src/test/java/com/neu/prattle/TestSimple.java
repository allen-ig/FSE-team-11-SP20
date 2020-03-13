package com.neu.prattle;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import com.neu.prattle.main.PrattleApplication;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neu.prattle.model.User;

public class TestSimple {

	private UserService as;
	private PrattleApplication prattleApplication;
	
	@Before
	public void setUp() {
		as = UserServiceImpl.getInstance();
		prattleApplication = new PrattleApplication();
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

	public void testPrattleApplication() {
		assertEquals(1, prattleApplication.getClasses().size());
	}
}
