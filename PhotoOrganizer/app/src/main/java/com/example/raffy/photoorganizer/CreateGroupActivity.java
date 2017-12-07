package com.example.raffy.photoorganizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nameField;
    private EditText durationField;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_layout);
        setTitle(R.string.create_group);

        Button button_cancel = findViewById(R.id.cancel);
        button_cancel.setOnClickListener(this);
        Button button_create = findViewById(R.id.create_group_btn);
        button_create.setOnClickListener(this);

        nameField = findViewById(R.id.ins_group_name);
        durationField = findViewById(R.id.ins_group_duration);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.create_group_btn:
                // check that fields are not empty
                if (nameField.getText().toString().equals("") || durationField.getText().toString().equals(""))
                    return;
                // get instances
                final Calendar now = Calendar.getInstance();
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) break;
                // start progress
                now.add(Calendar.MINUTE, Integer.parseInt(durationField.getText().toString()));
                HashMap<String, String> users = new HashMap<>();
                users.put(user.getUid(), user.getEmail());
                Group group = new Group(nameField.getText().toString(), now, user.getUid(), users);
                startCreateGroupAction(CreateGroupActivity.this, group);
                break;
        }
    }

    private static void startCreateGroupAction(final Activity context, final Group group) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        final ProgressDialog progress = ApiHttp.getProgressDialog(context);
        // get token and start progress
        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                String token = task.getResult().getToken();
                String expiration = Group.getDateFormat().format(group.getExpires().getTime());
                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("token", token)
                        .addFormDataPart("group_name", group.getName())
                        .addFormDataPart("expiration_time", expiration)
                        .addFormDataPart("user", user.getEmail())
                        .build();
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:5000/create_group")  // TODO
                        .post(body)
                        .build();
                new ApiHttp(context, progress).execute(request);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("!!!", e.getMessage());
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }
}
