package com.neu.prattle.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.exceptions.UserNotFoundException;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;

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

    // Usually Dependency injection will be used to inject the service at run-time
    private UserService accountService = UserServiceImpl.getInstance();

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
    
    @POST
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserStatus(User user) {
        try {
            accountService.setUserStatus(user.getName(), user.getStatus());
        } catch (UserNotFoundException e) {
            return Response.status(404).build();
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
        return Response.status(404).build();
      }
      
      Gson gson = new Gson();
      JsonObject user = new JsonObject();
      user.addProperty("status", statusString);
      
      return Response.ok().type(MediaType.APPLICATION_JSON).entity(gson.toJson(user)).build();
    }
}
