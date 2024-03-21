package com.ashcollege.entities;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String username;
    private String password;

    private String email;
    private float balance;

    public User(int id, String username, String password, String email, float balance) {
        this(username, password, email, balance);
        this.id = id;
    }

    public User(String username, String password, String email, float balance) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.balance = 1000;
    }

    public User() {

    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSameUsername (String username) {
        return this.username.equals(username);
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public boolean isSameCreds (String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
