package com.neu.prattle.websocket;

import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ChatEndpointTest {
  
  private static final String username = "User1";
  
  @InjectMocks
  private ChatEndpoint chatEndpoint;
  private Message message;
  
  @Mock
  private Session mockSession;
  
  @Mock
  private RemoteEndpoint.Basic mockBasic;
  
  @Mock
  private UserService userService;
  
  @Mock
  private Set<ChatEndpoint> chatEndpoints;
  
  private User user;
  
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    chatEndpoint = new ChatEndpoint();
    message = Message.messageBuilder().setFrom("User1").setTo("User2").setMessageContent("Hello World").build();
    user = new User();
    user.setName("User1");
  }
  
  @Test
  public void testPrivateCreateConnectedMessage() {
    Method privateMethod = null;
    Class[] cArg = new Class[1];
    cArg[0] = String.class;
    
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("createConnectedMessage", cArg);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    
    privateMethod.setAccessible(true);
    
    Message message = null;
    try {
      message = (Message) privateMethod.invoke(chatEndpoint, new String(username));
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
    
    assertEquals("Connected!", message.getContent());
  }
  
  @Test
  public void testOnOpenUserNotFound(){
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    Mockito.when(this.mockSession.getBasicRemote()).thenReturn(this.mockBasic);
    
    try {
      chatEndpoint.onOpen(mockSession, username);
    } catch (IOException | EncodeException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testOnOpenUserFound(){
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    Mockito.when(this.mockSession.getBasicRemote()).thenReturn(this.mockBasic);
    Mockito.when(userService.findUserByName(username)).thenReturn(Optional.of(new User(username)));
    
    try {
      chatEndpoint.onOpen(mockSession, username);
    } catch (IOException | EncodeException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testPrivateAddEndpoint(){
    this.mockSession = Mockito.mock(Session.class);
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    
    Method privateMethod = null;
    
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("addEndpoint", Session.class, String.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    
    privateMethod.setAccessible(true);
    
    try {
      privateMethod.invoke(chatEndpoint, mockSession, username);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
    
    verify(mockSession, times(1)).getId();
  }
  
  @Test
  public void testOnMessage(){
    this.mockSession = Mockito.mock(Session.class);
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    
    chatEndpoint.onMessage(mockSession, message);
    verify(mockSession, times(1)).getId();
  }
  
  @Test
  public void testOnClose(){
    this.mockSession = Mockito.mock(Session.class);
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    
    chatEndpoint.onClose(mockSession);
    verify(mockSession, times(1)).getId();
  }
  
  @Test
  public void testOnError(){
    this.mockSession = Mockito.mock(Session.class);
    chatEndpoint.onError(mockSession, new Throwable());
  }
  
  @Test
  public void testPrivateBroadcast(){
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    Mockito.when(this.mockSession.getBasicRemote()).thenReturn(this.mockBasic);
    
    Method privateMethod = null;
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("broadcast", Message.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    privateMethod.setAccessible(true);
    try {
      privateMethod.invoke(chatEndpoint, message);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testPrivateBroadcastException(){
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    
    Method privateMethod = null;
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("broadcast", Message.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    privateMethod.setAccessible(true);
    try {
      privateMethod.invoke(chatEndpoint, message);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}

