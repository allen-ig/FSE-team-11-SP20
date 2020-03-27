package com.neu.prattle.websocket;

import com.neu.prattle.model.BasicGroup;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;

import com.neu.prattle.service.UserServiceImpl;
import com.neu.prattle.service.UserServiceWithGroups;
import com.neu.prattle.service.UserServiceWithGroupsImpl;
import java.util.HashSet;

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
import java.util.Set;
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
    message = Message.messageBuilder().setFrom("User1").setTo("User2")
        .setMessageContent("Hello World").build();
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

    assert privateMethod != null;
    privateMethod.setAccessible(true);

    Message message = null;
    try {
      message = (Message) privateMethod.invoke(chatEndpoint, username);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

    assert message != null;
    assertEquals("Connected!", message.getContent());
  }

  @Test
  public void testOnOpenUserNotFound() {
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    Mockito.when(this.mockSession.getBasicRemote()).thenReturn(this.mockBasic);

    try {
      chatEndpoint.onOpen(mockSession, username);
    } catch (IOException | EncodeException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testOnOpen() {
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    Mockito.when(this.mockSession.getBasicRemote()).thenReturn(this.mockBasic);

    UserServiceWithGroups u = UserServiceWithGroupsImpl.getInstance();
    User user = new User("onOpenUser");
    UserServiceImpl.getInstance().addUser(user);


    try {
      chatEndpoint.onOpen(mockSession, "onOpenUser");
    } catch (IOException | EncodeException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testPrivateAddEndpoint() {
    this.mockSession = Mockito.mock(Session.class);
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");

    Method privateMethod = null;

    try {
      privateMethod = ChatEndpoint.class
          .getDeclaredMethod("addEndpoint", Session.class, String.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    assert privateMethod != null;
    privateMethod.setAccessible(true);

    try {
      privateMethod.invoke(chatEndpoint, mockSession, username);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

    verify(mockSession, times(1)).getId();
  }

  @Test
  public void testOnMessage() {
    this.mockSession = Mockito.mock(Session.class);
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    Mockito.when(this.mockSession.getBasicRemote()).thenReturn(this.mockBasic);

    Method privateMethod = null;

    try {
      privateMethod = ChatEndpoint.class
          .getDeclaredMethod("addEndpoint", Session.class, String.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    assert privateMethod != null;
    privateMethod.setAccessible(true);

    try {
      privateMethod.invoke(chatEndpoint, mockSession, username);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

    chatEndpoint.onMessage(mockSession, message);
    verify(mockSession, times(2)).getId();
  }

  @Test
  public void testOnMessageExtendedForGroups() throws IOException, EncodeException {
    this.mockSession = Mockito.mock(Session.class);
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    Mockito.when(this.mockSession.getBasicRemote()).thenReturn(this.mockBasic);

    Method privateMethod = null;

    try {
      privateMethod = ChatEndpoint.class
          .getDeclaredMethod("addEndpoint", Session.class, String.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    assert privateMethod != null;
    privateMethod.setAccessible(true);

    UserServiceWithGroups us = UserServiceWithGroupsImpl.getInstance();
    User omu = new User("onMessageUser");
    User omu2 = new User("onMessageUser2");
    UserServiceImpl.getInstance().addUser(omu);
    UserServiceImpl.getInstance().addUser(omu2);
    Set<User> members = new HashSet<>();
    members.add(omu);
    members.add(omu2);

    try {
      privateMethod.invoke(chatEndpoint, mockSession, omu.getName());
      privateMethod.invoke(chatEndpoint, mockSession, omu2.getName());
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

    BasicGroup existingGroup = BasicGroup.groupBuilder().setName("eg").setMembers(members).build();
    us.addGroup(existingGroup);

    Message message2 = Message.messageBuilder().setFrom(omu.getName()).setTo("neWgRoup")
        .setMessageContent("ng1 onMessageUser onMessageUser2").build();
    chatEndpoint.onMessage(mockSession, message2);
    verify(mockSession, times(3)).getId();

    Message message3 = Message.messageBuilder().setFrom(omu.getName()).setTo("newgroup ")
        .setMessageContent("ng2").build();
    chatEndpoint.onMessage(mockSession, message3);
    verify(mockSession, times(4)).getId();

    Message message4 = Message.messageBuilder().setFrom(omu.getName()).setTo(" NEWGROUP")
        .setMessageContent("ng3 onMessageUser3 onMessageUser4").build();
    chatEndpoint.onMessage(mockSession, message4);
    verify(mockSession, times(5)).getId();

    Message message5 = Message.messageBuilder().setFrom(omu.getName()).setTo(" group existingGroup")
        .setMessageContent("content").build();
    chatEndpoint.onMessage(mockSession, message5);
    verify(mockSession, times(6)).getId();

    try {
      Message message6 = Message.messageBuilder().setFrom(omu.getName())
          .setTo("")
          .setMessageContent("content").build();
      chatEndpoint.onMessage(mockSession, message5);
    } catch (Error e) {
      e.printStackTrace();
    }

    try {
      Message message6 = Message.messageBuilder().setFrom(omu.getName())
          .setMessageContent("content").build();
      chatEndpoint.onMessage(mockSession, message5);
    } catch (Error e) {
      e.printStackTrace();
    }

  }

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
  public void testOnClose() {
    this.mockSession = Mockito.mock(Session.class);

    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");

    Method privateMethod = null;

    try {
      privateMethod = ChatEndpoint.class
          .getDeclaredMethod("addEndpoint", Session.class, String.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    privateMethod.setAccessible(true);

    try {
      privateMethod.invoke(chatEndpoint, mockSession, username);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

    chatEndpoint.onClose(mockSession);
    verify(mockSession, times(3)).getId();
  }

  @Test
  public void testOnError() {
    this.mockSession = Mockito.mock(Session.class);
    chatEndpoint.onError(mockSession, new Throwable());
  }

  @Test
  public void testPrivateBroadcast() {
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    Mockito.when(this.mockSession.getBasicRemote()).thenReturn(this.mockBasic);

    Method privateMethod = null;
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("broadcast", Message.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    assert privateMethod != null;
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
  public void testPrivateBroadcastException() {
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");

    Method privateMethod = null;
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("broadcast", Message.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    assert privateMethod != null;
    privateMethod.setAccessible(true);
    try {
      privateMethod.invoke(chatEndpoint, message);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testSendMessage() {
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");

    Method privateMethod = null;
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("sendMessage", Message.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    assert privateMethod != null;
    privateMethod.setAccessible(true);
    try {
      privateMethod.invoke(chatEndpoint, message);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

    UserServiceWithGroups u = UserServiceWithGroupsImpl.getInstance();
    User user = new User(username);
    UserServiceImpl.getInstance().addUser(user);

    try {
      privateMethod.invoke(chatEndpoint, message);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }


  @Test
  public void testPrivateSendGroupMessage() {
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");
    Mockito.when(this.mockSession.getBasicRemote()).thenReturn(this.mockBasic);

    Method privateMethod = null;
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("sendGroupMessage", Message.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    assert privateMethod != null;
    privateMethod.setAccessible(true);
    try {
      privateMethod.invoke(chatEndpoint, message);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

    User omu = new User("OnMessageUser");
    User omu2 = new User("OnMessageUser2");
    Set<User> mems = new HashSet<>();
    mems.add(omu);
    mems.add(omu2);

    BasicGroup existingGroup = BasicGroup.groupBuilder().setName("existingGroup").setMembers(mems).build();
    UserServiceWithGroups us = UserServiceWithGroupsImpl.getInstance();
    UserServiceImpl.getInstance().addUser(omu);
    UserServiceImpl.getInstance().addUser(omu2);
    us.addGroup(existingGroup);

    Message message5 = Message.messageBuilder().setFrom(omu.getName()).setTo(" group existingGroup")
        .setMessageContent("content").build();
    //chatEndpoint.onMessage(mockSession, message5);

    Message message6 = Message.messageBuilder().setFrom(omu.getName()).setTo("group groupname")
        .setMessageContent("content").build();
    //chatEndpoint.onMessage(mockSession, message6);

    Message message7 = Message.messageBuilder().setFrom(omu.getName()).setTo("group ")
        .setMessageContent("content").build();
    //chatEndpoint.onMessage(mockSession, message7);

    try {
      privateMethod.invoke(chatEndpoint, message5);
      privateMethod.invoke(chatEndpoint, message6);
      privateMethod.invoke(chatEndpoint, message7);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  //
  @Test
  public void testPrivateAddGroup() {
    Mockito.when(this.mockSession.getId()).thenReturn("sessionId");

    Method privateMethod = null;
    try {
      privateMethod = ChatEndpoint.class.getDeclaredMethod("addGroup", Message.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    assert privateMethod != null;
    privateMethod.setAccessible(true);
    try {
      privateMethod.invoke(chatEndpoint, message);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

    User omu = new User("OnMessageUser");
    Message message2 = Message.messageBuilder().setFrom(omu.getName()).setTo(" neWgrOup")
        .setMessageContent("groupname OnMessageUser user2").build();

    Message message3 = Message.messageBuilder().setFrom(omu.getName()).setTo("newgroup ")
        .setMessageContent("GROUPNAME").build();

    Message message4 = Message.messageBuilder().setFrom(omu.getName()).setTo(" NEWGROUP ")
        .setMessageContent("ug1 OnMessageUser OnMessageUser2").build();

    try {
      privateMethod.invoke(chatEndpoint, message2);
      privateMethod.invoke(chatEndpoint, message3);
      privateMethod.invoke(chatEndpoint, message4);
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
      privateMethod = ChatEndpoint.class
          .getDeclaredMethod("setAccountService", UserServiceWithGroups.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    assert privateMethod != null;
    privateMethod.setAccessible(true);
    try {
      privateMethod.invoke(chatEndpoint, UserServiceWithGroupsImpl.getInstance());
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

