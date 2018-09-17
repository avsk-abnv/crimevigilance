package com.abnv.flamefreezer.crimevigilance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ListView list;
    String[] itemname ={
            "Crimes Around You",
            "Missing Persons",
            "Login",
    };
    Integer[] ext={
            R.color.transparent,
            R.color.transparent,
            R.drawable.pic4
    };
    Integer[] imgid={
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic3,
    };
    //private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth= FirebaseAuth.getInstance();
        checkForLogin();
        Toolbar tb= (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(tb);
        final ActionBar ab = getSupportActionBar();
        if(ab!=null){
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayShowCustomEnabled(true);
        }
        final Intent loginIntent= new Intent(MainActivity.this,LoggedInActivity.class);
      //  mAuth= FirebaseAuth.getInstance();
        /*mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)

                    startActivity(loginIntent);
            }
        };*/
      //  ab.setDisplayShowTitleEnabled(false);
        //ab.setDisplayHomeAsUpEnabled(true);
        CustomListAdapter adapter=new CustomListAdapter(this, itemname, imgid, ext);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String Slecteditem= itemname[+position];
                if(position==0) {
                    startActivity(new Intent(MainActivity.this, CrimesActivity.class));
                    finish();
                }
                else if(position==1) {
                    startActivity(new Intent(MainActivity.this, MissingPersonsActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }

    private void checkForLogin() {
        ColdStorage cd= new ColdStorage(getApplicationContext());
        boolean flag= false;
        flag= cd.getFlag();
        if(flag){
            String email= cd.getEmail();    String password= cd.getPassword();
            Intent intent = new Intent(MainActivity.this, LoggedInActivity.class);
            intent.putExtra("Email",email);
            intent.putExtra("Password",password);
            setResult(RESULT_OK);
            int requestCode=1;
            startActivityForResult(intent,requestCode);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent2 = new Intent(MainActivity.this, AboutUs_Activity.class);
        if(item.getItemId()==R.id.menu_main_about)
            startActivity(intent2);
        if(item.getItemId()==R.id.menu_main_Exit)
            finishAffinity();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
    public void onClickTextView(View view){
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        finish();
    }
}


