package com.abnv.flamefreezer.crimevigilance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MissingPersonsActivity extends AppCompatActivity {
    private RecyclerView mPeopleList;
    private DatabaseReference mDatabase;
    private  static Context context;
    ConnectionDetector cd= new ConnectionDetector(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_persons);
        Toolbar tb= (Toolbar)findViewById(R.id.missingpersons_bar);
        setSupportActionBar(tb);

        final ActionBar ab = getSupportActionBar();
        if(ab!=null){
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        if(!cd.isConnected())
            Toast.makeText(this,"Error! Check Network Connection",Toast.LENGTH_LONG).show();
        else{
            mPeopleList = (RecyclerView)findViewById(R.id.blog_list);
            mPeopleList.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(MissingPersonsActivity.this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            mPeopleList.setLayoutManager(mLayoutManager);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Missing_Persons");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.exit_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MissingPersonsActivity.this, MainActivity.class);
        if(item.getItemId()== android.R.id.home)
            startActivity(intent);
        if(item.getItemId()==R.id.menu_main_Exit)
            finishAffinity();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(MissingPersonsActivity.this, MainActivity.class));
        finish();
    }
    private void populate(){
        FirebaseRecyclerAdapter<People,PeopleViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<People, PeopleViewHolder>(
                People.class,
                R.layout.card_missing_person,
                PeopleViewHolder.class,
                mDatabase) {
            @Override
            protected void populateViewHolder(PeopleViewHolder viewHolder, People model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setAge(model.getAge());
                viewHolder.setHeight(model.getHeight());
                viewHolder.setGender(model.getGender());
                viewHolder.setLast_Seen(model.getLast_seen());
                viewHolder.setContact_no(model.getContact_no());
                viewHolder.setAppearance(model.getAppearance());
                viewHolder.setImage(MissingPersonsActivity.this,model.getImage());
                final TextView tel=(TextView)viewHolder.mView.findViewById(R.id.contact);
                LinearLayout contactme=(LinearLayout)viewHolder.mView.findViewById(R.id.linearlayoutcontact);
                context= MissingPersonsActivity.this;
                contactme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(context);
                        builder.setTitle("Call now")
                                .setIcon(R.drawable.callme)
                                .setMessage("Want to Call : "+tel.getText().toString()+" ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent callIntent= new Intent(Intent.ACTION_DIAL);
                                        callIntent.setData(Uri.parse("tel:"+tel.getText().toString().trim()));
                                        context.startActivity(callIntent);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert).show();
                    }
                });
            }
        };
        mPeopleList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PeopleViewHolder extends  RecyclerView.ViewHolder
    {
        View mView;
        Button button;
        LinearLayout contactme;
        public PeopleViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            this.button = (Button) mView.findViewById(R.id.onDetails);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((TextView) mView.findViewById(R.id.appearance)).getVisibility() == View.GONE) {
                        ((LinearLayout) mView.findViewById(R.id.linearlayout1)).setVisibility(View.VISIBLE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayout2)).setVisibility(View.VISIBLE);
                        ((TextView) mView.findViewById(R.id.appearance)).setVisibility(View.VISIBLE);
                        ((TextView) mView.findViewById(R.id.concerning_person)).setVisibility(View.VISIBLE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayoutcontact)).setVisibility(View.VISIBLE);
                        button.setText("HIDE DETAILS");
                    } else if (((TextView) mView.findViewById(R.id.appearance)).getVisibility() == View.VISIBLE) {
                        ((TextView) mView.findViewById(R.id.appearance)).setVisibility(View.GONE);
                        ((TextView) mView.findViewById(R.id.concerning_person)).setVisibility(View.GONE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayout1)).setVisibility(View.GONE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayout2)).setVisibility(View.GONE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayoutcontact)).setVisibility(View.GONE);
                        button.setText("MORE DETAILS");
                    }
                }
            });
        }

        public void setName(String name)
        {
            TextView NAME=(TextView)mView.findViewById(R.id.name);
            NAME.setText("Name  : "+name);
        }
        public void setAge(String age)
        {
            TextView AGE=(TextView)mView.findViewById(R.id.age);
            AGE.setText("Age  : "+age);
        }
        public void setImage(Context ctx, String image)
        {
            ImageView post_image=(ImageView)mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }

        public void setHeight(String height) {
            TextView HEIGHT=(TextView)mView.findViewById(R.id.height);
            HEIGHT.setText("Height  : "+height);
        }

        public void setLast_Seen(String lastseen) {
            TextView LAST_SEEN=(TextView)mView.findViewById(R.id.lastseen);
            LAST_SEEN.setText("Last Seen  : "+lastseen);
        }

        public void setAppearance(String appearance) {
            TextView APPEARANCE=(TextView)mView.findViewById(R.id.appearance);
            APPEARANCE.setText("Appearance  : "+appearance);
        }

        public void setGender(String gender) {
            TextView GENDER=(TextView)mView.findViewById(R.id.gender);
            GENDER.setText("Gender  : "+gender);
        }

        public void setContact_no(String contact_no) {
            TextView CONTACT= (TextView)mView.findViewById(R.id.contact);
            CONTACT.setText(contact_no);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        populate();
    }
}
