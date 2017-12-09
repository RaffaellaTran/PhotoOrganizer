package com.example.raffy.photoorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raffy on 21/11/2017.
 */

public class LoginActivity extends AppCompatActivity implements TextWatcher {


    private EditText  inputPassword;
    private FirebaseAuth auth;
    private Button btnLogin,btnSignup ;
    private List<String> myList;
    private ArrayAdapter<String> myAutoCompleteAdapter;
    AutoCompleteTextView inputEmail;
    String item[]={"dd@dd.it"};


  //  TextView autoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.login_layout);
        inputEmail = (AutoCompleteTextView) findViewById(R.id.ins_mail);
        inputPassword = (EditText) findViewById(R.id.ins_pass);
        btnLogin = (Button) findViewById(R.id.login);
        btnSignup = (Button) findViewById(R.id.register);
       // autoList = (TextView)findViewById(R.id.autolist);
        //autocomplete
        prepareMyList();
        inputEmail.addTextChangedListener(this);
        myAutoCompleteAdapter= new ArrayAdapter<String>(
                LoginActivity.this, android.R.layout.simple_dropdown_item_1line,
                myList
        );
        inputEmail.setAdapter(myAutoCompleteAdapter);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Registration.class));
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO Auto-generated method stub
                //Get the bundle
                Bundle bundle = getIntent().getExtras();
                String newAdd = inputEmail.getText().toString();

                String ss=null;

                if (bundle!=null){ss=bundle.getString("email");}
                if(!myList.contains(newAdd)){
                    if(ss!=null){
                    myList.add(ss);}
                    myList.add(newAdd);

                    // I don't know why simple notifyDataSetChanged()
                    // cannot update the autocomplete words
                    //myAutoCompleteAdapter.notifyDataSetChanged();

                    //update the autocomplete words
                    myAutoCompleteAdapter = new ArrayAdapter<String>(
                            LoginActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            myList);

                    inputEmail.setAdapter(myAutoCompleteAdapter);
                }

                //display the words in myList for your reference
                String s = "";
                for(int i = 0; i < myList.size(); i++){
                    s += myList.get(i) + "\n";
                }
               // autoList.setText(s);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                                         }
                                    Toast.makeText(getApplicationContext(), "The password/email is incorrect or you are not register!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });}
    private void prepareMyList(){
        //prepare your list of words for AutoComplete
        myList = new ArrayList<String>();
        for(int i = 0; i < item.length; i++){
            myList.add(item[i]);
        }
            }
    @Override
    public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub

    }

}
