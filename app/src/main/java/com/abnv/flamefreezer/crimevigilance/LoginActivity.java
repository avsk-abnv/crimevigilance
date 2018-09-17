package com.abnv.flamefreezer.crimevigilance;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText emailfield,password;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    ConnectionDetector cd= new ConnectionDetector(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar tb= (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(tb);
        final ActionBar ab = getSupportActionBar();
        if(ab!=null){
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayShowCustomEnabled(true);
        }
        mAuth= FirebaseAuth.getInstance();
        emailfield= (EditText)findViewById(R.id.editText);
        password= (EditText)findViewById(R.id.editText2);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("User_details");
        mProgress= new ProgressDialog(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.exit_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            if(item.getItemId()==R.id.menu_main_Exit)
                finishAffinity();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
    public void onClickLogin(View view) {
        String email = emailfield.getText().toString().trim();
        String pass_word = password.getText().toString().trim();
        if (!cd.isConnected())
            Toast.makeText(this, "Error! Check Network Connection", Toast.LENGTH_SHORT).show();
        else {
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass_word)) {
                mProgress.setMessage("Attempting Login...");
                mProgress.show();
                mAuth.signInWithEmailAndPassword(email, pass_word).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkUserExist();
                            mProgress.dismiss();
                            Toast.makeText(LoginActivity.this, "Successfully Logged in..", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error! Incorrect Username or Password", Toast.LENGTH_LONG).show();
                            mProgress.dismiss();
                            password.setText("");
                        }
                    }
                });
            } else
                Toast.makeText(LoginActivity.this, "Some Fields are Empty", Toast.LENGTH_LONG).show();
        }
    }

    private void checkUserExist() {
        final String user_id= mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){
                    startActivity(new Intent(LoginActivity.this,LoggedInActivity.class));
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,"Error! You need to setup your Account",Toast.LENGTH_LONG).show();
                    emailfield.setText("");
                    password.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onClickTextView(View view){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }

}
