package com.neu.prattle.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "friendRequest")
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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private FriendKey id;

    private String status = "pending";;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User recipient;
//
//    public FriendKey getFriendKey() {
//        return id;
//    }
//
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

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

//    @Embeddable
//    public class FriendKey implements Serializable {
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
