package com.example.raffy.photoorganizer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
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
                Calendar now = Calendar.getInstance();
                now.add(Calendar.MINUTE, Integer.parseInt(durationField.getText().toString()));
                Group group = new Group(nameField.getText().toString(), now);
                new Http(this).execute(group);
                break;
        }
    }

    private static class Http extends AsyncTask<Group, Void, String> {

        private WeakReference<Activity> context;

        Http(Activity context) {
            this.context = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Group... groups) {
            // TODO needs to be tested + handled
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            OkHttpClient client = new OkHttpClient();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    new Locale("fi", "FI"));
            for (Group group : groups) {
                try {
                    String expiration = format.format(group.getExpires().getTime());

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("group_name", group.getName());
                    jsonObject.put("expiration_time", expiration);
                    jsonObject.put("owner", user.getUid());

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:5000/create_group")  // TODO
                            .post(body)
                            .build();

                    Response response = client.newCall(request).execute();
                    context.get().finish();
                    return "Group created! " + response.toString();
                } catch (IOException e) {
                    return "Network error: " + e.getMessage();
                } catch (JSONException e) {
                    return "JSON error: " + e.getMessage();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            Log.i("GROUPS", message);
            Toast.makeText(context.get(), message, Toast.LENGTH_LONG).show();
        }
    }
}
