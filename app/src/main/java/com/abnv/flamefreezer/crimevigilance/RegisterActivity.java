package com.abnv.flamefreezer.crimevigilance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static com.abnv.flamefreezer.crimevigilance.R.id.item;

public class RegisterActivity extends AppCompatActivity {
    public DatabaseReference mDatabase;
    EditText editText,editText2,editText3,editText4;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    ConnectionDetector cd= new ConnectionDetector(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar tb= (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(tb);
        final ActionBar ab = getSupportActionBar();
        if(ab!=null){
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayShowCustomEnabled(true);
        }

        editText= (EditText)findViewById(R.id.editText);
        editText2= (EditText)findViewById(R.id.editText2);
        editText3= (EditText)findViewById(R.id.editText3);
        editText4= (EditText)findViewById(R.id.editText4);
        mProgress= new ProgressDialog(this);
        mAuth= FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("User_details");
        mDatabase.keepSynced(true);
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
    public void onClickTextView(View view){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
    public void onClickButton(View view){
        String Name= editText.getText().toString().trim();
        String Username= editText2.getText().toString().trim();
        String Password= editText3.getText().toString().trim();
        String CnfPass= editText4.getText().toString().trim();
        if(TextUtils.isEmpty(Name) || TextUtils.isEmpty(Username) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(CnfPass))
            Toast.makeText(RegisterActivity.this,"Some Fields are Empty",Toast.LENGTH_LONG).show();
        else
            if(!Password.matches(CnfPass))
                Toast.makeText(RegisterActivity.this,"Passwords Don't Match",Toast.LENGTH_LONG).show();
            else {
                if (!isValidEmail(Username) || !isAlpha(Name))
                    Toast.makeText(RegisterActivity.this, "Name or Email ID Invalid", Toast.LENGTH_SHORT).show();
                if (isValidEmail(Username) && isAlpha(Name)){
                    startRegister();
                     }
            }
    }

    private void startRegister() {
        final String Name = editText.getText().toString().trim();
        String Username = editText2.getText().toString().trim();
        final String Password = editText3.getText().toString().trim();
        if (!cd.isConnected())
            Toast.makeText(this, "Error! Check Network Connection", Toast.LENGTH_LONG).show();
        else {
            mProgress.setMessage("Creating Account...");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(Username, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("Name").setValue(Name);
                        current_user_db.child("image").setValue("default");
                        current_user_db.child("Password").setValue(Password);
                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this, "Account Successfully Created", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    } else {
                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error! Unable to save information", Toast.LENGTH_LONG).show();
                    }


                }
            });
        }
    }




    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.exit_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        if(item.getItemId()==R.id.menu_main_Exit)
            finishAffinity();
        return super.onOptionsItemSelected(item);
    }
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    public boolean isAlpha(String name) {
        if(name.matches("[a-zA-Z]+")||name.contains(" "))
            return true;
        else
            return false;
    }
}
