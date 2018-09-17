package com.abnv.flamefreezer.crimevigilance;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordsTab extends Fragment {
    private RecyclerView mBlogList,mPeopleList;
    private DatabaseReference mDatabase;
    Button Crimes,MissingPersons;
    FirebaseAuth mAuth;
    Query mQueryDatabase;
    ViewPager mViewPager;
    private static Context context = null;
    public RecordsTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.records_tab, container, false);
        Crimes= (Button)view.findViewById(R.id.button8);
        final FloatingActionButton myFab= (FloatingActionButton)getActivity().findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBack();
            }
        });
        MissingPersons= (Button)view.findViewById(R.id.button9);
        Crimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myFab.setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.tabs).setVisibility(View.GONE);
                getView().findViewById(R.id.blog_list).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.button8).setVisibility(View.GONE);
                getView().findViewById(R.id.button9).setVisibility(View.GONE);
                getView().findViewById(R.id.textView7).setVisibility(View.GONE);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                mLayoutManager.setReverseLayout(true);
                mLayoutManager.setStackFromEnd(true);
                mBlogList=(RecyclerView)getView().findViewById(R.id.blog_list);
                mBlogList.setHasFixedSize(true);
                mBlogList.setLayoutManager(mLayoutManager);
                mDatabase= FirebaseDatabase.getInstance().getReference().child("Crime_Details");
                mAuth= FirebaseAuth.getInstance();
                mQueryDatabase = mDatabase.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());
                onClickCrimes();
            }
        });
        MissingPersons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myFab.setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.tabs).setVisibility(View.GONE);
                getView().findViewById(R.id.people_list).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.button8).setVisibility(View.GONE);
                getView().findViewById(R.id.button9).setVisibility(View.GONE);
                getView().findViewById(R.id.textView7).setVisibility(View.GONE);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                mLayoutManager.setReverseLayout(true);
                mLayoutManager.setStackFromEnd(true);
                mPeopleList=(RecyclerView)getView().findViewById(R.id.people_list);
                mPeopleList.setHasFixedSize(true);
                mPeopleList.setLayoutManager(mLayoutManager);
                mDatabase= FirebaseDatabase.getInstance().getReference().child("Missing_Persons");
                mAuth= FirebaseAuth.getInstance();
                mQueryDatabase = mDatabase.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());
                onClickMissingPersons();
            }
        });
        context=getActivity();
        return view;
    }

    private void onBack() {
        getActivity().findViewById(R.id.tabs).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.blog_list).setVisibility(View.GONE);
        getView().findViewById(R.id.people_list).setVisibility(View.GONE);
        getView().findViewById(R.id.button8).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.button9).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.textView7).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
    }

    private void onClickMissingPersons() {
        getView().findViewById(R.id.people_list).setVisibility(View.VISIBLE);
        FirebaseRecyclerAdapter<People,PeopleViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<People, PeopleViewHolder>(
                People.class,
                R.layout.card_missing_person,
                PeopleViewHolder.class,
                mQueryDatabase) {
            @Override
            protected void populateViewHolder(PeopleViewHolder viewHolder, People model, int position) {
                final String post_key= getRef(position).getKey();
                viewHolder.setName(model.getName());
                viewHolder.setAge(model.getAge());
                viewHolder.setHeight(model.getHeight());
                viewHolder.setGender(model.getGender());
                viewHolder.setLast_Seen(model.getLast_seen());
                viewHolder.setContact_no(model.getContact_no());
                viewHolder.setAppearance(model.getAppearance());
                viewHolder.setImage(getActivity(),model.getImage());
                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        final AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete Item")
                                .setMessage("Are you sure")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        mDatabase.child(post_key).removeValue();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert).show();
                        return false;
                    }
                });
            }
        };
        mPeopleList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PeopleViewHolder extends  RecyclerView.ViewHolder
    {
        View mView;
        Button button,button2;
        RecordsTab tab= new RecordsTab();
        //String post_key= tab.getPost_key();
        DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference().child("Missing_Persons");
        public PeopleViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            this.button= (Button)mView.findViewById(R.id.onDetails);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(((TextView)mView.findViewById(R.id.appearance)).getVisibility()==View.GONE) {
                        ((LinearLayout) mView.findViewById(R.id.linearlayout1)).setVisibility(View.VISIBLE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayout2)).setVisibility(View.VISIBLE);
                        ((TextView) mView.findViewById(R.id.appearance)).setVisibility(View.VISIBLE);
                        ((TextView) mView.findViewById(R.id.concerning_person)).setVisibility(View.VISIBLE);
                        ((LinearLayout)mView.findViewById(R.id.linearlayoutcontact)).setVisibility(View.VISIBLE);
                        button.setText("HIDE DETAILS");
                    }
                    else if(((TextView)mView.findViewById(R.id.appearance)).getVisibility()==View.VISIBLE) {
                        ((TextView) mView.findViewById(R.id.appearance)).setVisibility(View.GONE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayout1)).setVisibility(View.GONE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayout2)).setVisibility(View.GONE);
                        ((TextView) mView.findViewById(R.id.concerning_person)).setVisibility(View.GONE);
                        ((LinearLayout)mView.findViewById(R.id.linearlayoutcontact)).setVisibility(View.GONE);
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
            TextView CONTACT=(TextView)mView.findViewById(R.id.contact);
            CONTACT.setText(contact_no);
        }
    }
    private void onClickCrimes() {
        getView().findViewById(R.id.blog_list).setVisibility(View.VISIBLE);
        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mQueryDatabase) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {
                final String post_key2= getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setZipcode(model.getZipCode());
                viewHolder.setCity(model.getCity());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setContact(model.getContact());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(getActivity(),model.getImage());
                TextView status= viewHolder.mView.findViewById(R.id.post_status);
                final String Status= status.getText().toString().trim();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context,"Status: "+Status,Toast.LENGTH_LONG).show();
                    }
                });
                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(Status.matches("Unsolved")) {
                            final AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(context);
                            builder.setTitle("Crime Solved !")
                                    .setMessage("Update Status to \"Solved\"")
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with update status

                                            mDatabase.child(post_key2).child("status").setValue("Solved");
                                        }
                                    })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert).show();
                        }else{
                            Toast.makeText(context,"Status already updated",Toast.LENGTH_LONG).show();
                        }
                        return false;
                    }
                });
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class BlogViewHolder extends  RecyclerView.ViewHolder
    {
        View mView;
        Button button,button2;
        DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference().child("Crimes_Details");
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
