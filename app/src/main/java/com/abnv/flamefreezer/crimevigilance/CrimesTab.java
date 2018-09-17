package com.abnv.flamefreezer.crimevigilance;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class CrimesTab extends Fragment  {
    private RecyclerView mBlogList;
    private Query mQueryDatabase;
    private DatabaseReference mDatabase;
    public CrimesTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.crimes_tab, container, false);
        final Toolbar tb = (Toolbar)getActivity().findViewById(R.id.appbar_loggedin);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mBlogList=(RecyclerView)view.findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(mLayoutManager);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Crime_Details");
        ViewPager mViewPager = (ViewPager)getActivity().findViewById(R.id.container);
        mQueryDatabase= mDatabase;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position!=0)
                    mBlogList.removeAllViewsInLayout();
                else{
                    mBlogList=(RecyclerView)getView().findViewById(R.id.blog_list);
                    mBlogList.setHasFixedSize(true);
                    populate();

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

     /*   mBlogList.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (IsRefreshing) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );*/
        final EditText zipcode= (EditText) tb.findViewById(R.id.editText6);
        final FloatingActionButton myFab= (FloatingActionButton)getActivity().findViewById(R.id.fab1);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().findViewById(R.id.tabs).setVisibility(View.VISIBLE);
                mBlogList.removeAllViewsInLayout();
                mBlogList.setLayoutManager(new LinearLayoutManager(getActivity()));
                mQueryDatabase= mDatabase;
                populate();
                myFab.setVisibility(View.GONE);
            }
        });
        tb.findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((TextView) tb.findViewById(R.id.toolbar_title)).getVisibility() == View.VISIBLE) {
                    ((TextView) tb.findViewById(R.id.toolbar_title)).setVisibility(View.INVISIBLE);
                    ((TextView) tb.findViewById(R.id.toolbar_extra)).setVisibility(View.INVISIBLE);
                    zipcode.setVisibility(View.VISIBLE);
                }else{
                    ((TextView) tb.findViewById(R.id.toolbar_title)).setVisibility(View.VISIBLE);
                    ((TextView) tb.findViewById(R.id.toolbar_extra)).setVisibility(View.VISIBLE);
                    String ZIPCode= zipcode.getText().toString().trim();
                    zipcode.setVisibility(View.GONE);
                    zipcode.setText("");
                    performSearch(ZIPCode);
                }
            }
        });
        return view;
    }
    private void performSearch(String zipCode) {
        if(!zipCode.matches("")) {
            if (zipCode.length() == 6) {
                getActivity().findViewById(R.id.tabs).setVisibility(View.GONE);
                mQueryDatabase = mDatabase.orderByChild("zipCode").equalTo(zipCode);
                populate();
                getActivity().findViewById(R.id.fab1).setVisibility(View.VISIBLE);
            }
            else
                Toast.makeText(getActivity(),"Invalid ZIPcode",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        populate();
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
                viewHolder.setImage(getActivity(), model.getImage());
                final Context context= getActivity();
                final LinearLayout contactme= (LinearLayout)viewHolder.mView.findViewById(R.id.linearlayoutcontact);
                final TextView tel=(TextView)viewHolder.mView.findViewById(R.id.contact);
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
}
