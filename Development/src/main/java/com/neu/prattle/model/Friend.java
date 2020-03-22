package com.neu.prattle.model;

import javax.persistence.*;

@Entity
@Table(name = "friend")
public class Friend {

    public Friend() {}

    public Friend(User sender, User recipient) {
        this.sender = sender;
        this.recipient = recipient;
        this.status = "pending";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String status;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User recipient;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }
}
