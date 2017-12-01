package com.example.raffy.photoorganizer;

import android.support.annotation.Nullable;
import android.util.Log;

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
import java.util.Locale;

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
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.getKey();
                    String owner = snapshot.child("owner").getValue().toString();
                    if (owner.equals(user.getUid())) {
                        try {
                            Date date = getDateFormat().parse(snapshot.child("expiration_time").getValue().toString());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            Group group = new Group(name, calendar, user.getUid());
                            result.react(group);
                            return;
                        } catch (ParseException e) {
                            // TODO
                        }
                    }
                }
                result.react(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO
            }
        });
    }

    public static abstract class GetMyGroupResult {
        public abstract void react(@Nullable Group group);
    }

}
