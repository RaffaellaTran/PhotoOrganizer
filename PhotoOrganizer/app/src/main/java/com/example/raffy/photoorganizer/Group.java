package com.example.raffy.photoorganizer;

import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class Group {

    private String name;
    private Calendar expires;
    private String owner;
    private HashMap<String, String> users;
    private String joinCode;

    Group(String name, Calendar expires, String owner, HashMap<String, String> users) {
        this.name = name;
        this.expires = expires;
        this.owner = owner;
        this.users = users;
    }

    public String getName() {
        return this.name;
    }

    Calendar getExpires() {
        return this.expires;
    }

    String getOwner() {
        return this.owner;
    }

    HashMap<String, String> getUsers() {
        return this.users;
    }

    @Nullable
    String getJoinCode() {
        return this.joinCode;
    }

    static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                new Locale("fi", "FI"));
    }

    static void getMyGroup(final GetMyGroupResult result) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            result.react(null);
            return;
        }

        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference groupNameRef = databaseRef.child("users").child(user.getUid()).child("group");

        if (groupNameRef == null) {
            result.react(null);
            return;
        }

        groupNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getValue() == null) {
                    result.react(null);
                    return;
                }
                String groupName = dataSnapshot.getValue().toString();
                DatabaseReference groupRef = databaseRef.child("groups").child(groupName);
                groupRef.addValueEventListener(new ValueEventListener() {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            String name = dataSnapshot.getKey();
                            String owner = dataSnapshot.child("owner").getValue().toString();
                            String joinCode = dataSnapshot.child("join_token").getValue().toString();
                            int childrenCount = (int) dataSnapshot.child("users").getChildrenCount();
                            Iterator<DataSnapshot> children = dataSnapshot.child("users").getChildren().iterator();
                            HashMap<String, String> users = new HashMap<>();
                            for (int i = 0; i < childrenCount; i++) {
                                DataSnapshot next = children.next();
                                users.put(next.getKey(), next.getValue().toString());
                            }
                            if (users.get(user.getUid()) != null) {
                                Date date = getDateFormat().parse(dataSnapshot.child("expiration_time").getValue().toString());
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                Group group = new Group(name, calendar, owner, users);
                                group.joinCode = joinCode;
                                result.react(group);
                                return;
                            }
                            result.react(null);
                        } catch (ParseException|NullPointerException e) {
                            result.react(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Goes here if there is no such item in the database
                        result.react(null);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Goes here if there is no such item in the database
                result.react(null);
            }
        });
    }

    public static abstract class GetMyGroupResult {
        public abstract void react(@Nullable Group group);
    }

}
