package com.example.raffy.photoorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Raffy on 15/11/2017.
 */

public class GroupManagementActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_layout);

        Button button_create = findViewById(R.id.create_group);
        button_create.setOnClickListener(this);
        Button button_join = findViewById(R.id.join_group);
        button_join.setOnClickListener(this);
        Button button_delete = findViewById(R.id.delete_group);
        button_delete.setOnClickListener(this);



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
