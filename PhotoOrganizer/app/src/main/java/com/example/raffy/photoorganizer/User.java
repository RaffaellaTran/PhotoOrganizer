package com.example.raffy.photoorganizer;

/**
 * Created by Raffy on 14/11/2017.
 */
public class User {

    private String name;
    private String password;
    private String email;

    public User(String name, String pass, String email) {
        this.name = name;
        this.password=pass;
        this.email=email;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pass) {
        this.password = pass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}