package com.neu.prattle.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.exceptions.UserNotFoundException;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.MessageServiceImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;

import jdk.nashorn.internal.objects.annotations.Getter;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/***
 * A Resource class responsible for handling CRUD operations
 * on User objects.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */
@Path(value = "/user")
public class UserController {
    
    private UserService accountService = UserServiceImpl.getInstance();

    private MessageService messageService = MessageServiceImpl.getInstance();

    private static Logger logger = Logger.getLogger(UserController.class.getName());

    /***
     * Handles a HTTP POST request for user creation
     * 
     * @param user -> The User object decoded from the payload of POST request.
     * @return -> A Response indicating the outcome of the requested operation.
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUserAccount(User user) {
        try {
            accountService.addUser(user);
        } catch (UserAlreadyPresentException e) {
            return Response.status(409).build();
        }

        return Response.ok().build();
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserByName(@PathParam("name") String name){
        Optional<User> res = accountService.findUserByName(name);
        ObjectMapper mapper = new ObjectMapper();
        if (!res.isPresent()) return Response.status(404).build();
        User user = res.get();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(user);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.ok().type(MediaType.APPLICATION_JSON).entity(jsonString).build();
    }
    
    @POST
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserStatus(User user) {
        try {
            accountService.setUserStatus(user.getName(), user.getStatus());
        } catch (UserNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            return Response.status(500).entity("An internal server error occurred").build();
        }
        
        return Response.ok().build();
    }
    
    @GET
    @Path("/{username}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserStatus(@PathParam("username") final String username) {
  
      String statusString;
      try {
        statusString = accountService.getUserStatus(username);
      } catch(UserNotFoundException e) {
        return Response.status(404).entity(e.getMessage()).build();
      }
      
      Gson gson = new Gson();
      JsonObject user = new JsonObject();
      user.addProperty("status", statusString);
      
      return Response.ok().type(MediaType.APPLICATION_JSON).entity(gson.toJson(user)).build();
    }

    @GET
    @Path("/getDirectMessages/{username}/{sender}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDirectMessages(
            @PathParam("username") String user,
            @PathParam("sender") String sender) {

        ObjectMapper mapper = new ObjectMapper();
        StringBuilder message = new StringBuilder();
        List<Message> messages = messageService.getDirectMessages(user, sender);
        if (messages.size() != 0) {
            try {
                String out = mapper.writeValueAsString(messages);
                return Response.ok().type(MediaType.APPLICATION_JSON).entity(out).build();
            } catch (IOException e) {
                message.append("could not get direct messages from database");
                return Response.status(409).type(MediaType.APPLICATION_JSON).entity(message.toString()).build();
            }
        } else {
            message.append("no conversation history with ").append(sender);
            return Response.status(203).type(MediaType.TEXT_PLAIN).entity(message.toString()).build();
        }
    }

    @GET
    @Path("/getGroupMessages/{username}/{group}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupMessages(
            @PathParam("username") String user,
            @PathParam("group") String group) {

        ObjectMapper mapper = new ObjectMapper();
        StringBuilder message = new StringBuilder();
        List<Message> messages = messageService.getGroupMessages(user, group);
        if (messages.size() != 0) {
            try {
                String out = mapper.writeValueAsString(messages);
                return Response.ok().type(MediaType.APPLICATION_JSON).entity(out).build();
            } catch (IOException e) {
                message.append("could not get group messages from database");
                return Response.status(409).type(MediaType.TEXT_PLAIN).entity(message.toString()).build();
            }
        } else {
            message.append("no conversation history with ").append(group);
            return Response.status(203).type(MediaType.TEXT_PLAIN).entity(message.toString()).build();
        }
    }
}
