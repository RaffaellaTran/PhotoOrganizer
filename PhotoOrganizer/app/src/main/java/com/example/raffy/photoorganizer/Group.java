package com.example.raffy.photoorganizer;

import java.util.Calendar;

public class Group {

    private String name;
    private Calendar expires;
    private String user;

    public Group(String name, Calendar expires, String user) {
        this.name = name;
        this.expires = expires;
        this.user = user;
    }

    public String getName() {
        return this.name;
    }

    public Calendar getExpires() {
        return this.expires;
    }

    public String getUser() {
        return this.user;
    }

}
