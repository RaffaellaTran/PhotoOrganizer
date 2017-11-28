package com.example.raffy.photoorganizer;

import java.util.Calendar;

public class Group {

    private String name;
    private Calendar expires;

    public Group(String name, Calendar expires) {
        this.name = name;
        this.expires = expires;
    }

    public String getName() {
        return this.name;
    }

    public Calendar getExpires() {
        return this.expires;
    }

}
