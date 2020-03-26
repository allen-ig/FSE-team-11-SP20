package com.neu.prattle.controller;

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
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path(value = "/friend")
public class FriendController {

    private FriendService friendService = FriendServiceImpl.getInstance();
    private UserService userService = UserServiceImpl.getInstance();

    private static Logger logger = Logger.getLogger(FriendController.class.getName());

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendFriendRequest(Friend friend){
//        Optional<User> sender = userService.findUserByName(friendKey.getSender());
//        Optional<User> recipient = userService.findUserByName(friendKey.getRecipient());
//        if (sender.isPresent() && recipient.isPresent())
//        {
//            Friend friend = new Friend(sender.get(), recipient.get());
//            friend.setId(friendKey);
//            friendService.sendFriendRequest(friend);
//            return Response.ok().build();
//        }
        friendService.sendFriendRequest(friend);
        return Response.ok().build();
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllFriends(@PathParam("username") String username){
        Collection<Friend> friendList = friendService.findAllFriends(username);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(friendList);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.ok().type(MediaType.APPLICATION_JSON).entity(jsonString).build();
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
        if (senderOp.isPresent() && recipientOp.isPresent()){
            friendService.approveFriendRequest(senderOp.get(), recipientOp.get(), isApproved.equals("approve"));
            return Response.ok().build();
        }
//        friendService.approveFriendRequest(new Friend.FriendKey(sender, recipient), isApproved.equals("approve"));
        return Response.status(404).build();
    }
}
