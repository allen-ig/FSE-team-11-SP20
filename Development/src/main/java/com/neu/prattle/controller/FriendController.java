package com.neu.prattle.controller;

import com.neu.prattle.exceptions.FriendAlreadyPresentException;
import com.neu.prattle.model.Friend;
import com.neu.prattle.model.User;
import com.neu.prattle.service.FriendService;
import com.neu.prattle.service.FriendServiceImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path(value = "/friend")
public class FriendController {

    private FriendService friendService = FriendServiceImpl.getInstance();
    private UserService userService = UserServiceImpl.getInstance();

    private static Logger logger = LogManager.getLogger();

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendFriendRequest(Friend friend){
        if (friend.getSender().getName().equals(friend.getRecipient().getName())) {
            return Response.status(405).entity("You can't add yourself as a friend!").build();
        }
        try {
            friendService.sendFriendRequest(friend);
        }catch (FriendAlreadyPresentException e){
            logger.error("Tried to create a Friend that already exists from "
                    + friend.getSender().getName() + " to " + friend.getRecipient().getName());
            return Response.status(409).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/{username}/friends")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllFriends(@PathParam("username") String username){
        Optional<User> user = userService.findUserByName(username);
        String resString = "";
        if (user.isPresent()) {
            Collection<Friend> friendList = friendService.findAllFriends(user.get());
            ObjectMapper mapper = new ObjectMapper();
            List<User> userList = new ArrayList<>();
            for (Friend friend : friendList){
                if (friend.getSender().getName().equals(username))
                    userList.add(friend.getRecipient());
                else if (friend.getRecipient().getName().equals(username))
                    userList.add(friend.getSender());
            }

            try {
                resString = mapper.writeValueAsString(userList);
                return Response.ok().type(MediaType.APPLICATION_JSON).entity(resString).build();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }else {
            resString = "Could not find the target user " + username;
        }
        return Response.status(404).type(MediaType.APPLICATION_JSON).entity(resString).build();
    }

    @PATCH
    @Path("/{sender}/{recipient}/{isApproved}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response respondToFriendRequest(
            @PathParam("sender") String sender,
            @PathParam("recipient") String recipient,
            @PathParam("isApproved") String isApproved){
        Optional<User> senderOp = userService.findUserByName(sender);
        Optional<User> recipientOp = userService.findUserByName(recipient);
        StringBuilder message = new StringBuilder();
        if (senderOp.isPresent() && recipientOp.isPresent()){
            friendService.approveFriendRequest(senderOp.get(), recipientOp.get(), isApproved.equals("approve"));
            return Response.ok().build();
        }
        if (!senderOp.isPresent()) message.append("Could not find sender!\n");
        if (!recipientOp.isPresent()) message.append("Could not find recipient!");
        return Response.status(404).entity(message.toString()).build();
    }

    @DELETE
    @Path("/{sender}/{recipient}/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeFriend(
            @PathParam("sender") String sender,
            @PathParam("recipient") String recipient){
        Optional<User> optionalSender = userService.findUserByName(sender);
        Optional<User> optionalRecipient = userService.findUserByName(recipient);
        if (!optionalRecipient.isPresent() || !optionalSender.isPresent())
            return Response.status(404).entity("The friend you requested does not exist!").build();
        Optional<Friend> optionalFriend = friendService.findFriendByUsers(optionalSender.get(), optionalRecipient.get());
        Optional<Friend> optionalFriendReverse = friendService.findFriendByUsers(optionalRecipient.get(), optionalSender.get());
        if (!optionalFriend.isPresent() && !optionalFriendReverse.isPresent())
            return Response.status(404).entity("The friend you requested does not exist!").build();
        friendService.deleteFriend(optionalFriend.orElseGet(optionalFriendReverse::get));
        return Response.ok().build();
    }
}
