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

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Comparator;
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

  /**
   * Returns a User object given the User's username
   * @param name is the username of a User
   * @return a Response indicating the success of finding the User
   */
    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserByName(@PathParam("name") String name) {
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

  /**
   * Updates the status of a User
   * @param user is a User whose status is to updated
   * @return a Response indicating the success of updating the User's status
   */
    @POST
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserStatus(User user) {
        try {
            accountService.setUserStatus(user.getName(), user.getStatus());
        } catch (UserNotFoundException e) {
            return Response.status(404).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            return Response.status(500).type(MediaType.TEXT_PLAIN).entity("Could not reach server to update your status").build();
        }

        return Response.ok().type(MediaType.TEXT_PLAIN).entity("Status updated").build();
    }

  /**
   * Returns a User's status
   * @param username is the username of the User whose status should be retrieved
   * @return a Response indicating the success of the operation
   */
    @GET
    @Path("/{username}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserStatus(@PathParam("username") final String username) {

        String statusString;
        try {
            statusString = accountService.getUserStatus(username);
        } catch (UserNotFoundException e) {
            return Response.status(404).type(MediaType.APPLICATION_JSON).entity(e.getMessage()).build();
        }

        Gson gson = new Gson();
        JsonObject user = new JsonObject();
        user.addProperty("status", statusString);

        return Response.ok().type(MediaType.APPLICATION_JSON).entity(gson.toJson(user)).build();
    }

  /**
   * Returns a User's direct messages
   * @param user is the User for which to retrieve the direct messages
   * @param sender is the User who sent the direct messages to the User
   * @return
   */
    @GET
    @Path("/getDirectMessages/{username}/{sender}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDirectMessages(
            @PathParam("username") String user,
            @PathParam("sender") String sender) {

        ObjectMapper mapper = new ObjectMapper();
        StringBuilder message = new StringBuilder();
        List<Message> messages = messageService.getDirectMessages(user, sender);
        messages.addAll(messageService.getDirectMessages(sender, user));
        //Timsort should work well enough.
        messages.sort(new MessageSorter());

        if (! messages.isEmpty()) {
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

  /**
   * Returns a User's Group messages
   * @param user is the User for which to retrieve the Group messages
   * @param group is the Group that sent the messages to retrieve
   * @return a Response indicating the success of the operation
   */
    @GET
    @Path("/getGroupMessages/{username}/{group}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupMessages(
            @PathParam("username") String user,
            @PathParam("group") String group) {

        ObjectMapper mapper = new ObjectMapper();
        StringBuilder message = new StringBuilder();
        List<Message> messages = messageService.getGroupMessages(user, group);
        messages.sort(new MessageSorter());
        if (! messages.isEmpty()) {
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

    /**
     * Gets all users that are currently online in JSON format.
     * @return - Response with JSON body on success, text of error on failure.
     */
    @GET
    @Path("/getAllUsersOnline/{maxResults}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsersOnline(@PathParam("maxResults") int maxResults) {
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder message = new StringBuilder();
        List<User> online = accountService.getAllUsersOnline(maxResults);
        if (! online.isEmpty()) {
            try {
                String out = mapper.writeValueAsString(online);
                return Response.ok().type(MediaType.APPLICATION_JSON).entity(out).build();
            } catch (IOException e) {
                message.append("could not get online users from database");
                return Response.status(409).type(MediaType.TEXT_PLAIN).entity(message.toString()).build();
            }
        } else {
            message.append("no users currently online");
            return Response.status(203).type(MediaType.TEXT_PLAIN).entity(message.toString()).build();
        }
    }
}

/**
 * Simple comparator for messages.
 */
class MessageSorter implements Comparator<Message> {
    @Override
    public int compare(Message m1, Message m2) {
        return m1.getTimestamp().compareTo(m2.getTimestamp());
    }
}

