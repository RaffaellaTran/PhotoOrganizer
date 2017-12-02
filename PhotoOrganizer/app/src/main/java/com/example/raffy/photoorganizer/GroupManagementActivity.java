package com.example.raffy.photoorganizer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
                    if (group.getUser().equals(User.getUid())) {
                        button_delete.setText(getString(R.string.delete_a_group));
                    }
                    // fill group panel
                    group_name_field.setText(group.getName());
                    group_expires_field.setText(Group.getDateFormat().format(group.getExpires().getTime()));
                    group_members_field.setText("TODO");
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
                if (mGroup != null && mGroup.getUser().equals(User.getUid())) {
                    // delete group
                    new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.delete_a_group))
                        .setMessage(getString(R.string.delete_group_confirmation))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // TODO
                            }
                        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //
                            }
                        }).show();
                } else {
                    // leave group
                    // TODO
                }
                break;
            case R.id.add_member:
                // TODO
                break;
        }
    }



}
