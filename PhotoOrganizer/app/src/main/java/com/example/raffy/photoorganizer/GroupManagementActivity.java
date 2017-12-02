package com.example.raffy.photoorganizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroupManagementActivity extends AppCompatActivity implements View.OnClickListener {

    @Nullable Group mGroup;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_layout);

        final ConstraintLayout myGroupPanel = findViewById(R.id.myGroupInfo);

        final Button button_create = findViewById(R.id.create_group);
        button_create.setOnClickListener(this);
        final Button button_join = findViewById(R.id.join_group);
        button_join.setOnClickListener(this);
        final Button button_delete = findViewById(R.id.delete_group);
        button_delete.setOnClickListener(this);
        final Button button_add = findViewById(R.id.add_member);
        button_add.setOnClickListener(this);

        final TextView group_name_field = findViewById(R.id.name);
        final TextView group_expires_field = findViewById(R.id.expires);
        final TextView group_members_field = findViewById(R.id.members);

        final ProgressDialog progress = new ProgressDialog(this);
        progress.show();
        Group.getMyGroup(new Group.GetMyGroupResult() {
            @Override
            public void react(@Nullable Group group) {
                progress.dismiss();
                if (group == null) {
                    setTitle(getString(R.string.create_join));
                    // hide group panel
                    myGroupPanel.setVisibility(View.GONE);
                } else {
                    mGroup = group;
                    setTitle(getString(R.string.group_information));
                    // hide create/join buttons
                    button_create.setVisibility(View.GONE);
                    button_join.setVisibility(View.GONE);
                    // set delete/leave button
                    if (group.getOwner().equals(User.getUid())) {
                        button_delete.setText(getString(R.string.delete_a_group));
                    }
                    // fill group panel
                    group_name_field.setText(group.getName());
                    group_expires_field.setText(Group.getDateFormat().format(group.getExpires().getTime()));
                    StringBuilder membersString = new StringBuilder();
                    for (String member : group.getUsers()) {
                        membersString.append(member);
                        membersString.append(" ");
                    }
                    group_members_field.setText(membersString.toString());
                }
            }
        });

    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case  R.id.create_group:
                intent = new Intent(GroupManagementActivity.this, CreateGroupActivity.class);
                startActivity(intent); // start Intent
                break;
            case R.id.join_group:
                intent = new Intent(GroupManagementActivity.this, JoinActivity.class);
                startActivity(intent); // start Intent
                break;
            case R.id.delete_group:
                // delete group
                final boolean delete = mGroup != null && mGroup.getOwner().equals(User.getUid());
                new AlertDialog.Builder(this)
                        .setTitle(getString(delete ? R.string.delete_a_group : R.string.leave_group))
                        .setMessage(getString(delete ? R.string.delete_group_confirmation : R.string.leave_group_confirmation))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startDeleteGroupAction(GroupManagementActivity.this, mGroup, !delete);
                            }
                        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //
                    }
                }).show();
                break;
            case R.id.add_member:
                // TODO
                break;
        }
    }

    private static void startDeleteGroupAction(final Activity context, final Group group, final boolean justLeave) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        final ProgressDialog progress = new ProgressDialog(context);
        progress.show();
        // get token and start progress
        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                String token = task.getResult().getToken();
                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("token", token)
                        .addFormDataPart("group_name", group.getName())
                        .build();
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:5000/" + (justLeave ? "leave_group" : "delete_group"))  // TODO
                        .delete(body)
                        .build();
                new Http(context, token, progress).execute(request);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("!!!", e.getMessage());
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class Http extends AsyncTask<Request, Void, String> {

        private WeakReference<Activity> context;
        private String token;
        private ProgressDialog progress;

        Http(Activity context, String token, ProgressDialog progress) {
            this.context = new WeakReference<>(context);
            this.token = token;
            this.progress = progress;
        }

        @Override
        protected String doInBackground(Request... requests) {
            OkHttpClient client = new OkHttpClient();
            for (Request request : requests) {
                try {
                    Response response = client.newCall(request).execute();
                    context.get().finish();
                    return "Success! " + response.toString();
                } catch (IOException e) {
                    return "Network error: " + e.getMessage();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            if (message != null) {
                Log.i("GROUPS", message);
                Toast.makeText(context.get(), message, Toast.LENGTH_LONG).show();
            }
            progress.dismiss();
        }
    }

}
