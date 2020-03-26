package com.neu.prattle.testservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.neu.prattle.main.PrattleApplication;
import com.neu.prattle.model.Message;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.MessageServiceImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMessageService {

  private MessageService messageService;
  private Message.MessageBuilder builder;
  private String[] jane1 = {"john", "jane", "hello"};
  private String[] jane2 = {"jim", "jane", "yo"};
  private String[] jane3 = {"steve", "jane", "sup"};
  private String[] jim1 = {"john", "jim", "hello2"};
  private String[] jim2 = {"jane", "jim", "yo2"};
  private String[] jim3 = {"steve", "jim", "sup2"};
  private String[][] janeMessages = {jane1, jane2, jane3};
  private String[][] jimMessages = {jim1, jim2, jim3};
  private List<Message> expectedJaneMessages;
  private List<Message> expectedJimMessages;

  @Before
  public void setUp(){
    System.setProperty("testing", "true");
    messageService = MessageServiceImpl.getInstance();
    assertTrue(messageService.isTest());
  }

  @After
  public void tearDown(){
    System.setProperty("testing", "false");
  }

  @Test
  public void testGetUserMessages(){
    addMessages();
    List<Message> actualJaneMessage = messageService.getUserMessages("jane");
    for (Message m : expectedJaneMessages){
      assertTrue(actualJaneMessage.contains(m));
    }
    List<Message> actualJimMessage = messageService.getUserMessages("jim");
    for (Message m : expectedJimMessages){
      assertTrue(actualJimMessage.contains(m));
    }
  }

  public void addMessages(){
    expectedJaneMessages = new ArrayList<>();
    for (String[] msg : janeMessages){
      builder = Message.messageBuilder();
      builder.setFrom(msg[0]);
      builder.setTo(msg[1]);
      builder.setMessageContent(msg[2]);
      expectedJaneMessages.add(builder.build());
    }
    expectedJimMessages = new ArrayList<>();
    for (String[] msg : jimMessages){
      builder = Message.messageBuilder();
      builder.setFrom(msg[0]);
      builder.setTo(msg[1]);
      builder.setMessageContent(msg[2]);
      expectedJimMessages.add(builder.build());
    }
  }

}
