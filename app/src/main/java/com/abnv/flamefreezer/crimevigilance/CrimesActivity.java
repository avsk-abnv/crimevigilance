package com.abnv.flamefreezer.crimevigilance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import static android.view.View.GONE;

public class CrimesActivity extends AppCompatActivity {
    private RecyclerView mBlogList;
    private Query mQueryDatabase;
    private DatabaseReference mDatabase;
    Toolbar tb;
    ConnectionDetector cd= new ConnectionDetector(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crimes);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Crime_Details");
        mQueryDatabase= mDatabase;
        tb= (Toolbar)findViewById(R.id.crimes_bar);
        setSupportActionBar(tb);
        final ActionBar ab = getSupportActionBar();
        if(ab!=null){
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        if(!cd.isConnected())
            Toast.makeText(this,"Error! Check Network Connection",Toast.LENGTH_LONG).show();
        else{
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(CrimesActivity.this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            mBlogList=(RecyclerView)findViewById(R.id.blog_list);
            mBlogList.setHasFixedSize(true);
            mBlogList.setLayoutManager(mLayoutManager);
            ((ImageView)tb.findViewById(R.id.imageView2)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tb.findViewById(R.id.editText5).getVisibility()==GONE) {
                        tb.findViewById(R.id.editText5).setVisibility(View.VISIBLE);
                        tb.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
                    }
                    else{
                        final String zipcode= ((EditText)tb.findViewById(R.id.editText5)).getText().toString().trim();
                        performSearch(zipcode);
                    }
                }
            });
        }
    }
    private void performSearch(String zipCode) {
        if(!zipCode.matches("")) {
            if (zipCode.length() == 6) {
                mQueryDatabase = mDatabase.orderByChild("zipCode").equalTo(zipCode);
                mBlogList.removeAllViewsInLayout();
                mBlogList.setLayoutManager(new LinearLayoutManager(CrimesActivity.this));
                populate();
            }
            else
                Toast.makeText(CrimesActivity.this,"Invalid ZIPcode",Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(CrimesActivity.this, MainActivity.class);
        if(item.getItemId()== android.R.id.home) {
            if(tb.findViewById(R.id.editText5).getVisibility()==GONE) {
                startActivity(intent);
                finish();
            }
            else{
                ((EditText)tb.findViewById(R.id.editText5)).setText("");
                tb.findViewById(R.id.editText5).setVisibility(GONE);
                tb.findViewById(R.id.toolbar_title).setVisibility(View.VISIBLE);
                mBlogList.removeAllViewsInLayout();
                mBlogList.setLayoutManager(new LinearLayoutManager(CrimesActivity.this));
                mQueryDatabase= mDatabase;
                populate();
            }
        }
        if(item.getItemId()==R.id.menu_main_Exit)
            finishAffinity();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(CrimesActivity.this, MainActivity.class));
        finish();
    }

    private void populate() {
        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mQueryDatabase) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setZipcode(model.getZipCode());
                viewHolder.setCity(model.getCity());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setContact(model.getContact());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(CrimesActivity.this, model.getImage());
                final LinearLayout contactme= (LinearLayout)viewHolder.mView.findViewById(R.id.linearlayoutcontact);
                final TextView tel= (TextView)viewHolder.mView.findViewById(R.id.contact);
                final Context context= CrimesActivity.this;
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
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class BlogViewHolder extends  RecyclerView.ViewHolder
    {
        View mView;
        Button button;
        LinearLayout contactme;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            this.button= (Button)mView.findViewById(R.id.onDetails);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(((TextView)mView.findViewById(R.id.post_desc)).getVisibility()==View.GONE) {
                        ((TextView) mView.findViewById(R.id.post_desc)).setVisibility(View.VISIBLE);
                        ((TextView) mView.findViewById(R.id.nearest_police)).setVisibility(View.VISIBLE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayoutcontact)).setVisibility(View.VISIBLE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayoutzip)).setVisibility(View.VISIBLE);
                        button.setText("HIDE DETAILS");
                    }
                    else if(((TextView)mView.findViewById(R.id.post_desc)).getVisibility()==View.VISIBLE) {
                        ((TextView) mView.findViewById(R.id.post_desc)).setVisibility(View.GONE);
                        ((TextView) mView.findViewById(R.id.nearest_police)).setVisibility(View.GONE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayoutcontact)).setVisibility(View.GONE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayoutzip)).setVisibility(View.GONE);
                        button.setText("MORE DETAILS");
                    }
                }
            });
        }
        public void setTitle(String title)
        {
            TextView post_title=(TextView)mView.findViewById(R.id.post_title);
            post_title.setText("Crime  : "+title);
        }
        public void setDesc(String desc)
        {
            TextView post_desc=(TextView)mView.findViewById(R.id.post_desc);
            post_desc.setText("Description  :\n"+desc);
        }
        public void setImage(Context ctx, String image)
        {
            ImageView post_image=(ImageView)mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }

        public void setZipcode(String zipCode) {
            TextView ZIPCODE=(TextView)mView.findViewById(R.id.crime_zipcode);
            ZIPCODE.setText("ZIPCode  : "+zipCode);
        }

        public void setCity(String city) {
            TextView CITY=(TextView)mView.findViewById(R.id.crime_city);
            CITY.setText("City  : "+city);
        }

        public void setContact(String contact) {
            TextView CONTACT= (TextView)mView.findViewById(R.id.contact);
            CONTACT.setText(contact);
        }

        public void setStatus(String status) {
            TextView STATUS= (TextView)mView.findViewById(R.id.post_status);
            STATUS.setText(status);
            if(status.matches("Solved"))
                STATUS.setTextColor(Color.parseColor("#4E76BB"));
            else
                STATUS.setTextColor(Color.parseColor("#BB4E4E"));
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        populate();
    }
}

