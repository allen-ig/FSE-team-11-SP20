package com.neu.prattle.model;

import javax.persistence.*;
import java.util.Objects;

/**
 * A Friend represents a potential relationship between two Users
 */
@Entity
@Table(name = "friend")
public class Friend {

    public Friend() {
    }

    public Friend(User sender, User recipient) {
        this.sender = sender;
        this.recipient = recipient;
    }

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

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

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

    @Override
    public int hashCode(){
        return Objects.hash(sender.getName(), recipient.getName());
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Friend))
            return false;

        Friend friend = (Friend) obj;
        return friend.getSender().getName().equals(this.sender.getName()) && friend.getRecipient().getName().equals(this.recipient.getName());
    }
}
