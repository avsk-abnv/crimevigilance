package com.abnv.flamefreezer.crimevigilance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.abnv.flamefreezer.crimevigilance.R.id.container;
import static com.abnv.flamefreezer.crimevigilance.R.id.item;
import static com.abnv.flamefreezer.crimevigilance.R.menu.navigation_menu;

public class LoggedInActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "LoggedInActivity";
    private SelectionPageAdapter mSelectionPageAdapter;
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgress;
    static Button button1,button2;
    private ActionBarDrawerToggle mToggle;
    String image = "default";
    ConnectionDetector cd= new ConnectionDetector(this);
    private DatabaseReference mDatabaseUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        //  Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.navdrawer,this.getTheme());
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        mProgress = new ProgressDialog(this);
        setNavigationViewListner();
        final Toolbar tb = (Toolbar) findViewById(R.id.appbar_loggedin);
        setSupportActionBar(tb);
        final ActionBar actionBar = getSupportActionBar();
        final FloatingActionButton myFab= (FloatingActionButton)findViewById(R.id.fab);
        final FloatingActionButton myFab2= (FloatingActionButton)findViewById(R.id.fab1);
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
            ImageView imageButton = (ImageView) tb.findViewById(R.id.img_nav);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            });
        }
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("User_details").child(mAuth.getCurrentUser().getUid());
        getData();
        mSelectionPageAdapter = new SelectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    tb.findViewById(R.id.imageView3).setVisibility(View.VISIBLE);
                    myFab.setVisibility(View.GONE);
                }else if(position==1){
                    tb.findViewById(R.id.imageView3).setVisibility(View.GONE);
                    tb.findViewById(R.id.editText6).setVisibility(View.GONE);
                    myFab2.setVisibility(View.GONE);
                    myFab.setVisibility(View.GONE);
                }else{
                    tb.findViewById(R.id.editText6).setVisibility(View.GONE);
                    tb.findViewById(R.id.imageView3).setVisibility(View.GONE);
                    myFab2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        // Collect data from the intent and use it
        if(requestCode==1) {
            if (resultCode == RESULT_OK) {
                mProgress.setMessage("Wait for a moment...");
                mProgress.show();
                String email = getIntent().getStringExtra("Email");
                String password = getIntent().getStringExtra("Password");
                //   Toast.makeText(MainActivity.this,"falg: true ,email:"+email+" ,\npassword:"+password,Toast.LENGTH_LONG).show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getData();
                            mProgress.dismiss();
                        } else {
                            Toast.makeText(LoggedInActivity.this, "Sorry! You need to Login again", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoggedInActivity.this, LoginActivity.class));
                            finish();
                            mProgress.dismiss();
                        }
                    }
                });
            }
        }
        else
            getData();
    }
    private void setNavigationHeader(String Name,String image) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        ImageView Profile= (ImageView)header.findViewById(R.id.Image);
        TextView name = (TextView) header.findViewById(R.id.Name);
        TextView Email = (TextView) header.findViewById(R.id.email);
        name.setText(Name);
        Email.setText(mAuth.getCurrentUser().getEmail());
        if(!image.matches("default"))
            Picasso.with(LoggedInActivity.this).load(image).into(Profile);
        else
            Profile.setImageDrawable(getResources().getDrawable(R.drawable.bluehead));
    }

    private void setNavigationViewListner() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    /* @Override
     protected void onStart(){
         super.onStart();
         mAuth.addAuthStateListener(mAuthListener);
     }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item))
            return true;
        if (item.getItemId() == R.id.menu_main_Exit)
            finishAffinity();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_profile) {
            Intent profileIntent = new Intent(LoggedInActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        } else if (id == R.id.menu_addcrime) {
            Intent addcrimeIntent = new Intent(LoggedInActivity.this, AddCrimeActivity.class);
            startActivity(addcrimeIntent);

        } else if (id == R.id.menu_addmssingperson) {
            Intent addmissingIntent = new Intent(LoggedInActivity.this, AddMissingPersonActivity.class);
            startActivity(addmissingIntent);

        } else if (id == R.id.menu_logout) {
            if(!cd.isConnected())
                Toast.makeText(this,"Error! Check Network Connection",Toast.LENGTH_LONG).show();
            else {
                Intent logoutIntent = new Intent(LoggedInActivity.this, MainActivity.class);
                PreferenceManager.getDefaultSharedPreferences(LoggedInActivity.this).edit().clear().apply();
                mAuth.signOut();
                startActivity(logoutIntent);
                finish();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(Gravity.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        SelectionPageAdapter adapter = new SelectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new CrimesTab(), "Crimes");
        adapter.addFragment(new MissingPersonTab(), "Missing\nPersons");
        adapter.addFragment(new RecordsTab(), "Added By You");
        viewPager.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.exit_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }


    public void getData() {
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Name = dataSnapshot.child("Name").getValue(String.class);
                String Password = dataSnapshot.child("Password").getValue(String.class);
                String image= dataSnapshot.child("image").getValue(String.class);
                setNavigationHeader(Name,image);
                ColdStorage coldst = new ColdStorage(LoggedInActivity.this);
                coldst.saveData(mAuth.getCurrentUser().getEmail(), Password);
                //image= dataSnapshot.child("image").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
