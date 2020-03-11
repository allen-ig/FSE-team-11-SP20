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
  public void testEncodeThrowException() {
    obj = Mockito.mock(ObjectMapper.class);
    try {
      Mockito.when(obj.writeValueAsString(message)).thenThrow(IOException.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      assertEquals("{}", messageEncoder.encode(message));
    } catch (EncodeException e) {
      e.printStackTrace();
    }
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