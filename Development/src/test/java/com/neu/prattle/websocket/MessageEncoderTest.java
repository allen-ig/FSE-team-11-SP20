package com.neu.prattle.websocket;

import com.neu.prattle.model.Message;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.websocket.EncodeException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MessageEncoderTest {
  @Spy
  @InjectMocks
  private MessageEncoder messageEncoder;

  @Mock
  ObjectMapper objectMapper;
  
  @Before
  public void setUp() {
    System.setProperty("testing", "true");
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown(){
    System.setProperty("testing", "false");
  }

  
  @Test
  public void testEncode() {
    try {
      Message newMessage = Message.messageBuilder().setFrom("User1").setTo("User2").setMessageContent("Hello world").build();
      String s = messageEncoder.encode(newMessage);
      assertTrue(s.contains("\"from\":\"User1\""));
      assertTrue(s.contains("\"to\":\"User2\""));
      assertTrue(s.contains("\"content\":\"Hello world\""));

    } catch (EncodeException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testEncodeNoMessage() {
    Message sampleMessage = new Message();
    try {
      String s = messageEncoder.encode(sampleMessage);
      assertTrue(s.contains("\"from\":null"));
      assertTrue(s.contains("\"to\":null"));
      assertTrue(s.contains("\"content\":null"));
    } catch (EncodeException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testEncodeThrowException() {
    objectMapper = Mockito.mock(ObjectMapper.class);
    ObjectMapper temp = new ObjectMapper();
    Field privateField = null;
    Message message = Message.messageBuilder().setFrom("User1").setTo("User2").setMessageContent("Hello world").build();
    try {
      privateField = MessageEncoder.class.getDeclaredField("objectMapper");
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    privateField.setAccessible(true);
    try {
      temp = (ObjectMapper) privateField.get(messageEncoder);
      privateField.set(messageEncoder, objectMapper);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    try {
      Mockito.when(objectMapper.writeValueAsString(message)).thenThrow(IOException.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      assertEquals("{}", messageEncoder.encode(message));
      privateField.set(messageEncoder, temp);

    } catch (EncodeException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testPrivateSetterObjectMapper() {
    Method privateMethod = null;
    try {
      privateMethod = MessageEncoder.class.getDeclaredMethod("setObjectMapper", ObjectMapper.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    privateMethod.setAccessible(true);
    try {
      privateMethod.invoke(messageEncoder, new ObjectMapper());
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testPrivateGetterObjectMapper() {
    Method privateMethod = null;
    try {
      privateMethod = MessageEncoder.class.getDeclaredMethod("getObjectMapper");
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    privateMethod.setAccessible(true);
    try {
      ObjectMapper obj = (ObjectMapper) privateMethod.invoke(messageEncoder);
      assertNotNull(obj);
    } catch (IllegalAccessException | InvocationTargetException e) {
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