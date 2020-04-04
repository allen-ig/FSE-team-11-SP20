package com.neu.prattle.model;

import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.MessageServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MessageTest {

    private Message.MessageBuilder messageBuilder;
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

    @Test
    public void testTimestamp(){
        Timestamp now = Timestamp.from(Instant.now());
        messageBuilder.setTo("you");
        messageBuilder.setFrom("me");
        messageBuilder.setMessageContent("hello");
        Message savedMessage = messageBuilder.build();
        assertTrue(savedMessage.getTimestamp().getTime()-now.getTime()<3000);
        assertTrue(savedMessage.getId()>0);
    }

    @Test
    public void testSetTimestamp(){
        Message newMessage = new Message();
        newMessage.setTimestamp();
        Timestamp now = Timestamp.from(Instant.now());
        assertTrue(newMessage.getTimestamp().getTime()-now.getTime()<3000);
    }
}