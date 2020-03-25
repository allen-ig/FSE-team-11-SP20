package com.neu.prattle.websocket;

import com.neu.prattle.model.Message;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

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

  @Before
  public void setUp() {
    System.setProperty("testing", "true");
    MockitoAnnotations.initMocks(this);
    chatEndpoint = new ChatEndpoint();
    message = Message.messageBuilder().setFrom("User1").setTo("User2").setMessageContent("Hello World").build();
  }

  @After
  public void tearDown(){
    chatEndpoint = null;
    System.setProperty("testing", "false");
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
      message = (Message) privateMethod.invoke(chatEndpoint, username);
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
  public void testOnMessageBroadcast(){
    Message broadcastMessage = Message.messageBuilder().setFrom("User1").setMessageContent("Hello World").build();

    this.mockSession = Mockito.mock(Session.class);
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");

    chatEndpoint.onMessage(mockSession, broadcastMessage);
    verify(mockSession, times(1)).getId();
  }

  @Test
  public void testOnMessageBroadcast2(){
    Message broadcastMessage = Message.messageBuilder().setFrom("User1").setTo("").setMessageContent("Hello World").build();

    this.mockSession = Mockito.mock(Session.class);
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");

    chatEndpoint.onMessage(mockSession, broadcastMessage);
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
  public void testPrivateSendMessage(){
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    Mockito.when(this.mockSession.getBasicRemote()).thenReturn(this.mockBasic);

    ConcurrentHashMap<String, ChatEndpoint> chatEndpoints = new ConcurrentHashMap<>();
    chatEndpoints.put("User2", chatEndpoint);
    chatEndpoints.put("User1", chatEndpoint);

    Field privateEndpoints = null;
    Field privateSession = null;
    try{
      privateEndpoints = this.chatEndpoint.getClass().getDeclaredField("chatEndpoints");
      privateSession = this.chatEndpoint.getClass().getDeclaredField("session");
    }catch (NoSuchFieldException e){
      e.printStackTrace();
    }
    privateEndpoints.setAccessible(true);
    privateSession.setAccessible(true);
    try{
      privateEndpoints.set(chatEndpoint, chatEndpoints);
      privateSession.set(chatEndpoint, this.mockSession);
    }catch (IllegalArgumentException | IllegalAccessException e){
      e.printStackTrace();
    }

    Method privateMethod = null;
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("sendMessage", Message.class);
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
  public void testPrivateSendMessageUserDoesNotExist(){
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    Mockito.when(this.mockSession.getBasicRemote()).thenReturn(this.mockBasic);

    ConcurrentHashMap<String, ChatEndpoint> chatEndpoints = new ConcurrentHashMap<>();
    chatEndpoints.put("User1", chatEndpoint);

    Field privateEndpoints = null;
    Field privateSession = null;
    try{
      privateEndpoints = this.chatEndpoint.getClass().getDeclaredField("chatEndpoints");
      privateSession = this.chatEndpoint.getClass().getDeclaredField("session");
    }catch (NoSuchFieldException e){
      e.printStackTrace();
    }
    privateEndpoints.setAccessible(true);
    privateSession.setAccessible(true);
    try{
      privateEndpoints.set(chatEndpoint, chatEndpoints);
      privateSession.set(chatEndpoint, this.mockSession);
    }catch (IllegalArgumentException | IllegalAccessException e){
      e.printStackTrace();
    }

    Method privateMethod = null;
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("sendMessage", Message.class);
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

  @Test
  public void testPrivateSendMessageException(){
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    Message messageWithWrongToAndFrom = Message.messageBuilder().setFrom("ghost").setTo("phantom").setMessageContent("Hello World").build();

    Method privateMethod = null;
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("sendMessage", Message.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    privateMethod.setAccessible(true);
    try {
      privateMethod.invoke(chatEndpoint, messageWithWrongToAndFrom);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testPrivateSendMessageException2(){
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    Message messageWithWrongFrom = Message.messageBuilder().setFrom("phantom").setTo("User2").setMessageContent("Hello World").build();

    Method privateMethod = null;
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("sendMessage", Message.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    privateMethod.setAccessible(true);
    try {
      privateMethod.invoke(chatEndpoint, messageWithWrongFrom);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testPrivateSetterUserService() {
    Method privateMethod = null;
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("setAccountService", UserService.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    privateMethod.setAccessible(true);
    try {
      privateMethod.invoke(chatEndpoint, UserServiceImpl.getInstance());
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testPrivateSetterSession() {
    Method privateMethod = null;
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("setSession", Session.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    privateMethod.setAccessible(true);
    try {
      privateMethod.invoke(chatEndpoint, mockSession);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}

