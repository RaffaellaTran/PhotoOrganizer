package com.example.raffy.photoorganizer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GroupManagementActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_layout);

        final Button button_create = findViewById(R.id.create_group);
        button_create.setOnClickListener(this);
        Button button_join = findViewById(R.id.join_group);
        button_join.setOnClickListener(this);
        final Button button_delete = findViewById(R.id.delete_group);
        button_delete.setOnClickListener(this);

        final ProgressDialog progress = new ProgressDialog(this);
        progress.show();
        Group.getMyGroup(new Group.GetMyGroupResult() {
            @Override
            public void react(@Nullable Group group) {
                progress.dismiss();
                if (group == null) {
                    setTitle("No group");
                    button_delete.setVisibility(View.GONE);
                } else {
                    setTitle("My group: " + group.getName());
                    if (!group.getUser().equals(User.getUid())) {
                        button_delete.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.create_group: {
                intent = new Intent(GroupManagementActivity.this, CreateGroupActivity.class);
                startActivity(intent); // start Intent
                break;
            }

            case R.id.join_group: {
                intent = new Intent(GroupManagementActivity.this, JoinActivity.class);
                startActivity(intent); // start Intent
                break;
            }

            case R.id.delete_group: {
                intent = new Intent(GroupManagementActivity.this, DeleteActivity.class);
                startActivity(intent); // start Intent
                break;
            }
        }
    }



    }
