package com.neu.prattle.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="test_user")

public class TestUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int pk;

    private String username;
    private String password;

    public TestUser (String u, String p){
        this.username = u;
        this.password = p;
    }
}
