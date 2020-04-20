package com.neu.prattle.controller;

import com.neu.prattle.exceptions.GroupAlreadyPresentException;
import com.neu.prattle.exceptions.GroupDeletedException;
import com.neu.prattle.exceptions.SenderNotAuthorizedException;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.BasicGroup;

import com.neu.prattle.model.request.GroupRequest;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import com.neu.prattle.service.UserServiceWithGroups;
import com.neu.prattle.service.UserServiceWithGroupsImpl;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Handles all HTTP Requests involving the Group model and UserServiceWithGroupsImpl service
 */
@Path(value = "/group")
public class GroupController {
    private String databaseConnectionErrorMsg = "Failed connecting to Database. Try again later.";
    private String lostMsg = "you have been lost";
    private String notFoundMsg = " not found.\n";
    // Usually Dependency injection will be used to inject the service at run-time
    private UserServiceWithGroups accountService = UserServiceWithGroupsImpl.getInstance();
    private UserService userService = UserServiceImpl.getInstance();
    private Logger logger = LogManager.getLogger();

    /***
     * Handles a HTTP POST request for group creation
     *
     * @param group -> A Group object decoded from JSON Post.
     * @return -> A Response indicating the outcome of the requested operation.
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createGroup(BasicGroup group) {
        StringBuilder message = new StringBuilder();
        try {
            accountService.addGroup(group);
        } catch (GroupAlreadyPresentException | IllegalArgumentException e) {
            message.append(e.getMessage());
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            message.append(databaseConnectionErrorMsg);
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        }
        message.append("Group Created: ");
        message.append(group.getName());
        return Response.ok().type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
    }

    /***
     * Handles an HTTP DELETE request to remove group.
     *
     * @param request -> A Group object decoded from JSON Post. Has two strings.
     * @return -> A Response indicating the outcome of the requested operation.
     */
    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteGroup(GroupRequest request) {
        StringBuilder message = new StringBuilder();

        //query records
        Optional<BasicGroup> foundGroup = accountService
                .findGroupByName(request.getUser(), request.getGroup());
        Optional<User> sender = userService.findUserByName(request.getSender());

        //check if found, send error if not
        boolean allClear = true;
        if (!sender.isPresent()) {
            message.append(lostMsg);
            allClear = false;
        }
        if (!foundGroup.isPresent()) {
            message.append("group ").append(request.getGroup()).append(notFoundMsg);
            allClear = false;
        }
        if (!allClear) {
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        }

        //try to delete, return result
        try {
            accountService.deleteGroup(sender.get(), foundGroup.get());
        } catch (SenderNotAuthorizedException e) {
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            message.append(databaseConnectionErrorMsg);
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        }
        message.append("Group Deleted: ");
        message.append(foundGroup.get().getName());
        return Response.ok().type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
    }

    /***
     * Handles a HTTP PUT for adding a user.
     *
     * @param request -> A GroupRequest object decoded from JSON Post. Has three strings.
     * @return -> A Response indicating the outcome of the requested operation.
     */
    @PUT
    @Path("/addUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response extendGroup(GroupRequest request) {

        ProcessedRequest pr = new ProcessedRequest(request);

        StringBuilder message = pr.getMessage();

        if (!pr.getAllClear()) {
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        }

        try {
            accountService.extendUsers(pr.getSender(), pr.getUser(), pr.getGroup());
        } catch (SenderNotAuthorizedException | UserAlreadyPresentException e) {
            message.append(e.getMessage());
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            message.append(databaseConnectionErrorMsg);
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        }

        message.append("members of ").append(pr.getGroup().getName());
        message.append(" appended: ").append(pr.getUser().getName());
        // !!! Issue - no way to send a message to everyone except from JS or calling ChatEndPoint
        return Response.ok().type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
    }

    /***
     * Handles a HTTP PUT for adding a Moderator.
     *
     * @param request -> A GroupRequest object decoded from JSON Post. Has three strings.
     * @return -> A Response indicating the outcome of the requested operation.
     */
    @PUT
    @Path("/addModerator")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response extendModerators(GroupRequest request) {

        ProcessedRequest pr = new ProcessedRequest(request);

        StringBuilder message = pr.getMessage();

        if (!pr.getAllClear()) {
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        }

        try {
            accountService.extendModerators(pr.getSender(), pr.getUser(), pr.getGroup());
        } catch (SenderNotAuthorizedException | UserAlreadyPresentException e) {
            message.append(e.getMessage());
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            message.append(databaseConnectionErrorMsg);
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        }

        message.append("moderators of ");
        message.append(pr.getGroup().getName());
        message.append(" appended: ");
        message.append(pr.getUser().getName());
        // !!! Issue - no way to send a message to everyone except from JS or calling ChatEndPoint
        return Response.ok().type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
    }

    /***
     * Handles a HTTP PUT for removing a user.
     *
     * @param request -> A GroupRequest object decoded from JSON Post. Has three strings.
     * @return -> A Response indicating the outcome of the requested operation.
     */
    @DELETE
    @Path("/removeUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeUser(GroupRequest request) {

        ProcessedRequest pr = new ProcessedRequest(request);

        StringBuilder message = pr.getMessage();

        if (!pr.getAllClear()) {
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        }

        try {
            accountService.removeUser(pr.getSender(), pr.getUser(), pr.getGroup());
        } catch (SenderNotAuthorizedException | GroupDeletedException e) {
            message.append(e.getMessage());
            return Response.status(410).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            message.append(databaseConnectionErrorMsg);
            return Response.status(411).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        }

        message.append("member of ");
        message.append(pr.getGroup().getName());
        message.append(" removed: ");
        message.append(pr.getUser().getName());

        return Response.ok().type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
    }

    /***
     * Handles a HTTP PUT for removing a moderator.
     *
     * @param request -> A GroupRequest object decoded from JSON Post. Has three strings.
     * @return -> A Response indicating the outcome of the requested operation.
     */
    @DELETE
    @Path("/removeModerator")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeModerator(GroupRequest request) {

        ProcessedRequest pr = new ProcessedRequest(request);

        StringBuilder message = pr.getMessage();

        if (!pr.getAllClear()) {
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        }

        try {
            accountService.removeModerator(pr.getSender(), pr.getUser(), pr.getGroup());
        } catch (SenderNotAuthorizedException | GroupDeletedException e) {
            message.append(e.getMessage());
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            message.append(databaseConnectionErrorMsg);
            return Response.status(409).type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
        }

        message.append("moderator of ");
        message.append(pr.getGroup().getName());
        message.append(" removed: ").append(pr.getUser().getName());

        return Response.ok().type(MediaType.TEXT_PLAIN_TYPE).entity(message.toString()).build();
    }


    /**
     * Returns all the Groups that a User belongs to
     * @param request is a GroupRequest
     * @return the Groups that a User belongs to, if there are any
     */
    @PUT
    @Path("/getGroups")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGroups(GroupRequest request) {
        StringBuilder message = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();

        Optional<User> sender = userService.findUserByNameWithGroups(request.getSender());

        //check if found, send error if not
        boolean allClear = true;
        if (!sender.isPresent()) {
            message.append(lostMsg);
            allClear = false;
        }
        if (!allClear) {
            return Response.status(409).type(MediaType.APPLICATION_JSON).entity(message.toString()).build();
        }

        Collection<BasicGroup> groups = sender.get().getGroups();
        List<BasicGroup> out = new ArrayList<>();

        if (! groups.isEmpty()) {
            for (BasicGroup group : groups) {
                    Optional<BasicGroup> groupE = accountService.findGroupByName(sender.get().getName(), group.getName());
                groupE.ifPresent(out::add);
            }
            try {
                String response = mapper.writeValueAsString(out);
                return Response.status(200).type(MediaType.APPLICATION_JSON).entity(response).build();
            } catch (IOException e) {
                message.append("could not send you groups");
                return Response.status(409).type(MediaType.APPLICATION_JSON).entity(message.toString()).build();
            }
        } else {
            message.append("No groups yet");
        }
        return Response.status(201).type(MediaType.APPLICATION_JSON).entity(message.toString()).build();
    }

    /**
     * Processes request, stores Sender, User, Group, if clear, and message.
     */
    private class ProcessedRequest {

        private boolean allClear;
        private Optional<User> sender;
        private Optional<User> user;
        private Optional<BasicGroup> group;
        private StringBuilder message;

        /**
         * Creates a new ProcessedRequest
         * @param request is a GroupRequest
         */
        ProcessedRequest(GroupRequest request) {
            message = new StringBuilder();

            //query records
            sender = userService.findUserByNameWithGroups(request.getSender());
            user = userService.findUserByNameWithGroups(request.getUser());
            group = accountService.findGroupByName(request.getUser(), request.getGroup());

            //check if found
            allClear = true;
            if (!user.isPresent()) {
                message.append("user ").append(request.getUser()).append(notFoundMsg);
                allClear = false;
            }
            if (!group.isPresent()) {
                message.append("group ").append(request.getGroup()).append(notFoundMsg);
                allClear = false;
            }
            if (!sender.isPresent()) {
                message.append(lostMsg);
                allClear = false;
            }
        }

        /**
         * Returns the value of allClear
         * @return the value of allClear
         */
        boolean getAllClear() {
            return allClear;
        }

        /**
         * Returns the value of message
         * @return the value of message
         */
        StringBuilder getMessage() {
            return message;
        }

        /**
         * Returns the value of sender if it exists, otherwise returns null
         * @return the value of sender
         */
        User getSender() {
            return sender.orElse(null);
        }

        /**
         * Returns the value of user if it exists, otherwise returns null
         * @return the value of user
         */
        User getUser() {
            return user.orElse(null);
        }

        /**
         * Returns the group if it exists, otherwise null
         * @return the value of group
         */
        BasicGroup getGroup() {
            return group.orElse(null);
        }
    }
}