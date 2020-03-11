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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class MessageDecoderTest {
  @Spy
  @InjectMocks
  private MessageDecoder messageDecoder;
  
  @Mock
  private ObjectMapper objectMapper;
  
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void testDecode() {
      Message message = messageDecoder.decode("{\"from\":\"User1\",\"to\":\"User2\",\"content\":\"Hello world\"}");
  
      assertEquals("User1", message.getFrom());
      assertEquals("User2", message.getTo());
      assertEquals("Hello world", message.getContent());
  }
  
  @Test
  public void testDecodeInvalid() {
    assertNull(messageDecoder.decode("test"));
  }

  @Test
  public void testWillDecode() {
    assertTrue(messageDecoder.willDecode("ABCD"));
  }
  
  @Test
  public void testWillDecodeFalse() {
    assertFalse(messageDecoder.willDecode(null));
  }
  
  @Test
  public void testDecoderInit() {
    messageDecoder.init(null);
  }
  
  @Test
  public void testDecoderDestroy() {
    messageDecoder.destroy();
  }
}
