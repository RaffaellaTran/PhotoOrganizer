package com.example.raffy.photoorganizer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.SharedPreferences;
/**
 * Created by Raffy on 10/11/2017.
 */

public class Registration extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName;
    private Button btnSignUp, btnSignIn;
  //  private ProgressBar progressBar;
    private TextView txtSignIn;
    private FirebaseAuth auth;

    String email;
    String password;
    String name;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String Password = "pass";
    public static final String Email = "emailKey";

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.sign_up);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            // User is logged in
            startActivity(new Intent(Registration.this, MainActivity.class));
            finish();
        } else{

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        txtSignIn = (TextView) findViewById(R.id.name);
        btnSignUp = (Button) findViewById(R.id.register);
        inputName= (EditText) findViewById(R.id.ins_name);
        inputEmail = (EditText) findViewById(R.id.ins_mail);
        inputPassword = (EditText) findViewById(R.id.ins_pass);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
      //  progressBar = (ProgressBar) findViewById(R.id.progressBar);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

            btnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Registration.this, LoginActivity.class));
                    finish();
                }
            });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name= inputName.getText().toString().trim();
                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

               // progressBar.setVisibility(View.VISIBLE);
                //create user

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Toast.makeText(Registration.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                          //      progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Registration.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

                                    String userId = FirebaseAuth.getInstance().getUid();

                                    User user = new User(name, email);

                                    mDatabase.child(userId).setValue(user);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(Name, name);
                                    editor.putString(Password, password);
                                    editor.putString(Email, email);
                                    editor.commit();
                                    startActivity(new Intent(Registration.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });}
    }

}
