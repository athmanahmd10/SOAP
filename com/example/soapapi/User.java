package com.example.soapapi;

public class User {
    public String username;
    public String password;
    public int admin;
    public User(String username, String password,int admin){
        this.username = username;
        this.password = password;
        this.admin = admin;
    }
}
