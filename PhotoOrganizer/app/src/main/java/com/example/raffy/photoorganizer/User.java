package com.example.raffy.photoorganizer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {

    private String name;
    private String email;
    private String group;

    public User() {
        // Empty constructor for Firebase
        // eg: User u = databaseSnapshot.getValue(User.class);
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        group = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String getUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }

}