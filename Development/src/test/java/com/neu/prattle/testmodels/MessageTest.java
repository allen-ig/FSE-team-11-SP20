package com.neu.prattle.testmodels;

import com.neu.prattle.model.Message;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.MessageServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    Message.MessageBuilder messageBuilder;
    private MessageService messageService;

    @Before
    public void setUp(){
        System.setProperty("testing", "true");
        messageService = MessageServiceImpl.getInstance();
        messageBuilder = Message.messageBuilder();
        assertTrue(messageService.isTest());
    }

    @After
    public void tearDown(){
        System.setProperty("testing", "false");
    }

    @Test
    public void testFrom() {
        messageBuilder.setFrom("user1");
        Message msg = messageBuilder.build();
        assertEquals("user1", msg.getFrom());
        assertTrue(msg.getId()>0);
    }

    @Test
    public void testTo() {
        messageBuilder.setTo("user2");
        Message msg = messageBuilder.build();
        assertEquals("user2", msg.getTo());
    }

    @Test
    public void testContent() {
        messageBuilder.setMessageContent("hello");
        Message msg = messageBuilder.build();
        assertEquals("hello", msg.getContent());
    }

    @Test
    public void testToString() {
        messageBuilder.setFrom("user1");
        messageBuilder.setTo("user2");
        messageBuilder.setMessageContent("hello");
        Message msg = messageBuilder.build();
        assertEquals("From: user1To: user2Content: hello", msg.toString());
    }

    @Test
    public void testProductionMessageService(){
      System.setProperty("testing", "false");
      messageService = MessageServiceImpl.getInstance();
      assertFalse(messageService.isTest());
      System.setProperty("testing", "true");
    }
}