package com.neu.prattle;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import com.neu.prattle.main.PrattleApplication;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;

import com.neu.prattle.model.User;

public class SimpleTest {

	private UserService as;
	private PrattleApplication prattleApplication;
	
	@Before
	public void setUp() {
		as = UserServiceImpl.getInstance();
		prattleApplication = new PrattleApplication();
	}
	
	
	// This method just tries to add 
	@Test
	public void setUserTest(){
	   as.addUser(new User("Mike"));
	}
	
	// This method just tries to add 
	@Test
	public void getUserTest(){
		Optional<User> user = as.findUserByName("Mike");
		assertTrue(user.isPresent());
	}
	
	// Performance testing to benchmark our number of users that can be added 
	// in 1 sec	
	
	@Test(timeout = 1000)
	public void checkPrefTest(){
		for(int i=0; i < 1000; i++) {
			as.addUser(new User("Mike"+i));
		}
	}
	
	@Test
	public void testPrattleApplication() {
		assertEquals(1, prattleApplication.getClasses().size());
	}
}
