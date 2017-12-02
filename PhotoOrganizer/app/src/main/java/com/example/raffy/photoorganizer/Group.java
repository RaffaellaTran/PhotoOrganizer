package com.example.raffy.photoorganizer;

import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class Group {

    private String name;
    private Calendar expires;
    private String owner;
    private String[] users;
    private String joinCode;

    public Group(String name, Calendar expires, String owner, String[] users) {
        this.name = name;
        this.expires = expires;
        this.owner = owner;
        this.users = users;
    }

    public String getName() {
        return this.name;
    }

    public Calendar getExpires() {
        return this.expires;
    }

    public String getOwner() {
        return this.owner;
    }

    public String[] getUsers() {
        return this.users;
    }

    @Nullable
    public String getJoinCode() {
        return this.joinCode;
    }

    static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                new Locale("fi", "FI"));
    }

    static void getMyGroup(final GetMyGroupResult result) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) result.react(null);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference groups = databaseRef.child("groups");
        groups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String name = snapshot.getKey();
                        String owner = snapshot.child("owner").getValue().toString();
                        String joinCode = snapshot.child("join_token").getValue().toString();
                        int childrenCount = (int) snapshot.child("users").getChildrenCount();
                        Iterator<DataSnapshot> children = snapshot.child("users").getChildren().iterator();
                        String[] users = new String[childrenCount];
                        for (int i = 0; i < childrenCount; i++) {
                            users[i] = children.next().getValue().toString();
                        }
                        if (Arrays.asList(users).contains(user.getUid())) {
                            Date date = getDateFormat().parse(snapshot.child("expiration_time").getValue().toString());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            Group group = new Group(name, calendar, owner, users);
                            group.joinCode = joinCode;
                            result.react(group);
                            return;
                        }
                    }
                } catch (ParseException|NullPointerException e) {
                    result.react(null);
                }
                result.react(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.react(null);
            }
        });
    }

    public static abstract class GetMyGroupResult {
        public abstract void react(@Nullable Group group);
    }

}
