package com.neu.prattle.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    private Message.MessageBuilder messageBuilder;

    @Before
    public void setUp(){
        messageBuilder = Message.messageBuilder();
    }

    @Test
    public void testFrom() {
        messageBuilder.setFrom("user1");
        Message msg = messageBuilder.build();
        assertEquals("user1", msg.getFrom());
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
}