package com.neu.prattle.websocket;

import com.neu.prattle.model.Message;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.IOException;

import javax.websocket.EncodeException;

import static org.junit.Assert.assertEquals;

public class MessageEncoderTest {
  @Spy
  @InjectMocks
  private MessageEncoder messageEncoder;
  
  private Message message;
  
  @Mock
  ObjectMapper obj;
  
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    message = Message.messageBuilder().setFrom("User1").setTo("User2").setMessageContent("Hello world").build();
  }
  
  @Test
  public void testEncode() {
    try {
      assertEquals("{\"from\":\"User1\",\"to\":\"User2\",\"content\":\"Hello world\"}", messageEncoder.encode(message));
    } catch (EncodeException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testEncodeNoMessage() {
    Message sampleMessage = new Message();
    try {
      assertEquals("{\"from\":null,\"to\":null,\"content\":null}", messageEncoder.encode(sampleMessage));
    } catch (EncodeException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testInit() {
    messageEncoder.init(null);
  }
  
  @Test
  public void testDestroy() {
    messageEncoder.destroy();
  }
}

/*
//    Field privateField = null;
//    try {
//      privateField = MessageEncoder.class.getDeclaredField("objectMapper");
//    } catch (NoSuchFieldException e) {
//      e.printStackTrace();
//    }
//    privateField.setAccessible(true);
//
//
//    try {
//      obj = (ObjectMapper) privateField.get(messageEncoder);
//      when(obj.writeValueAsString(null)).thenReturn("{}");
//    } catch (IllegalAccessException | IOException e) {
//      e.printStackTrace();
//    }


//    try {
//      System.out.println(obj.writeValueAsString(message));
//      when(obj.writeValueAsString(message)).thenReturn("{}");
//      when(this.mockSession.getId()).thenReturn("sessionId");
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
 

 */