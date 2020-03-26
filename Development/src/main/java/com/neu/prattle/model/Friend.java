package com.neu.prattle.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "friend")
//@IdClass(Friend.FriendKey.class)
public class Friend {

    public Friend() {
    }

//    public Friend(FriendKey id){
//        this.id = id;
//    }

    public Friend(User sender, User recipient) {
        this.sender = sender;
        this.recipient = recipient;
    }

//    @EmbeddedId
//
//    private FriendKey id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String status = "pending";

    @ManyToOne(targetEntity = User.class)
    private User sender;

    @ManyToOne(targetEntity = User.class)
    private User recipient;

//    public FriendKey getFriendKey() {
//        return id;
//    }
//
//    public FriendKey getId() {
//        return id;
//    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

//    public void setId(FriendKey id) {
//        this.id = id;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getSender() {
        return sender;
    }

    public User getRecipient() {
        return recipient;
    }

//    @Embeddable
//    public static class FriendKey implements Serializable {
//        private String sender;
//        private String recipient;
//
//        public String getSender() {
//            return sender;
//        }
//
//        public void setSender(String sender) {
//            this.sender = sender;
//        }
//
//        public String getRecipient() {
//            return recipient;
//        }
//
//        public void setRecipient(String recipient) {
//            this.recipient = recipient;
//        }
//
//        public FriendKey(String sender, String recipient){
//            this.sender = sender;
//            this.recipient = recipient;
//        }
//
//        public FriendKey() {}
//    }
}
