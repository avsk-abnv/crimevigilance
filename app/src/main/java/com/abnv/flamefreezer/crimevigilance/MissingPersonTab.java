package com.abnv.flamefreezer.crimevigilance;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class MissingPersonTab extends Fragment {

    private RecyclerView mPeopleList;
    private DatabaseReference mDatabase;
    public MissingPersonTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.missingperson_tab, container, false);
        mPeopleList = (RecyclerView) view.findViewById(R.id.blog_list);
        mPeopleList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mPeopleList.setLayoutManager(mLayoutManager);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Missing_Persons");
        final ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.container);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position!=1)
                    mPeopleList.removeAllViewsInLayout();
                else{
                    mPeopleList=(RecyclerView)getView().findViewById(R.id.blog_list);
                    mPeopleList.setHasFixedSize(true);
                    populate();

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
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
                viewHolder.setImage(getActivity(),model.getImage());
                LinearLayout contactme= (LinearLayout)viewHolder.mView.findViewById(R.id.linearlayoutcontact);
                final TextView tel= (TextView)viewHolder.mView.findViewById(R.id.contact);
                final Context context= getActivity();
                contactme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(context);
                        builder.setTitle("Call now")
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
                        ((TextView) mView.findViewById(R.id.concerning_person)).setVisibility(View.GONE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayout1)).setVisibility(View.GONE);
                        ((LinearLayout) mView.findViewById(R.id.linearlayout2)).setVisibility(View.GONE);
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
