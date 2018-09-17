package com.abnv.flamefreezer.crimevigilance;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewGroupCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {
    FloatingActionButton myfab;
    ProgressDialog mProgress;
    Uri imageUri = null;
    ImageView profile;
    private StorageReference mStorage;
    private EditText name;
    static int flag1=0,flag2=0;
    private static final int GALLERY_REQUEST = 1;
    private DatabaseReference mDatabase, mDatabaseUser;
    private static FirebaseAuth mAuth;
    private static FirebaseUser mCurrentUser;
    ConnectionDetector cd = new ConnectionDetector(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar tb = (Toolbar) findViewById(R.id.profile_bar);
        setSupportActionBar(tb);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mStorage = FirebaseStorage.getInstance().getReference();
        name = (EditText) findViewById(R.id.Name);
        profile = (ImageView) findViewById(R.id.ProfilePic);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User_details");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = mDatabase.child(mCurrentUser.getUid());
        myfab = (FloatingActionButton) findViewById(R.id.fab);
        myfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });
        getdata();
        mProgress = new ProgressDialog(this);
/*        if(!cd.isConnected())
            Toast.makeText(this,"Error! Check Network Connection",Toast.LENGTH_LONG).show();*/
    }

    private void getdata() {

        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Name = dataSnapshot.child("Name").getValue(String.class);
                String image = dataSnapshot.child("image").getValue(String.class);
                if (!image.matches("default"))
                    Picasso.with(ProfileActivity.this).load(image).into(profile);
                else
                    profile.setImageDrawable(getResources().getDrawable(R.drawable.bluehead));
                name.setText(Name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        ((TextView) findViewById(R.id.emailID)).setText(mCurrentUser.getEmail());
    }

    private void onSelectImageClick(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.exit_menu, menu);
        return true;
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            /*final Bundle extras = data.getExtras();
            if (extras != null) {
                //Get image
                Bitmap bitmap = extras.getParcelable("data");
                imageButton.setImageBitmap(bitmap);*/
            imageUri = data.getData();
            ((Button) findViewById(R.id.Crop)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.Upload)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.cancel)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.ProfilePic)).setImageURI(imageUri);

        }
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                ((Button) findViewById(R.id.Crop)).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.Upload)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.cancel)).setVisibility(View.VISIBLE);
                ((ImageView) findViewById(R.id.ProfilePic)).setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ProfileActivity.this, LoggedInActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_main_Exit:
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, LoggedInActivity.class));
        finish();
    }

    public void onClickCrop(View v) {
        startCropImageActivity(imageUri);
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(ProfileActivity.this);
    }

    public void onClick(View v) {
        startStoring();
        getdata();
    }

    private void startStoring() {

        if (imageUri != null) {
            mProgress.setMessage("Uploading Profile...");
            mProgress.show();

            StorageReference filePath = mStorage.child("Profile_images").child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();
                    mDatabase.child(mCurrentUser.getUid()).child("image").setValue(downloadUri.toString());
                    mProgress.dismiss();
                    ((Button) findViewById(R.id.Crop)).setVisibility(View.GONE);
                    ((Button) findViewById(R.id.Upload)).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.cancel)).setVisibility(View.GONE);
                    // startActivity(new Intent(AddCrimeActivity.this,PostPage.class));
                }
            });
        } else {
            Toast.makeText(ProfileActivity.this, "No Image Selected", Toast.LENGTH_LONG).show();
        }
    }

    public void onCancel(View v) {
        getdata();
        ((Button) findViewById(R.id.Crop)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.Upload)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.cancel)).setVisibility(View.GONE);
    }

    public void onChangeName(View v) {
        final String Name = name.getText().toString().trim();
        mProgress.setMessage("Updating Name...");
        mProgress.show();
        mDatabaseUser.child("Name").setValue(Name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mProgress.dismiss();
                    Toast.makeText(ProfileActivity.this, "Name Successfully Updated", Toast.LENGTH_LONG).show();
                } else {
                    mProgress.dismiss();
                    Toast.makeText(ProfileActivity.this, "Error! Failed to Update Name", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onChangePassword(View v) {
        final DatabaseReference current_user_db= FirebaseDatabase.getInstance().getReference().child("User_details").child(mCurrentUser.getUid());
        final ViewGroup nullParent = null;
        View mView= getLayoutInflater().inflate(R.layout.changepassword, nullParent);
        LinearLayout layout= (LinearLayout)mView.findViewById(R.id.myLinear);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
        alertDialog.setTitle("Update Password");
        alertDialog.setView(mView);
        final ProgressDialog mProgress= new ProgressDialog(ProfileActivity.this);
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dialog dialogObj =Dialog.class.cast(dialogInterface);
                        EditText old_pass= (EditText)dialogObj.findViewById(R.id.currpass);
                        final EditText new_pass= (EditText)dialogObj.findViewById(R.id.new_pass);
                        EditText cnf_pass= (EditText)dialogObj.findViewById(R.id.cnf_pass);
                        final String oldpass= old_pass.getText().toString();
                        final String newpass= new_pass.getText().toString();
                        final String cnfpass= cnf_pass.getText().toString();
                        if(!TextUtils.isEmpty(oldpass)&&!TextUtils.isEmpty(newpass)&&!TextUtils.isEmpty(cnfpass)) {
                            if (cnfpass.matches(newpass)) {
                                mProgress.setMessage("Authenticating...");
                                mProgress.show();
                                final String email = mCurrentUser.getEmail();
                                AuthCredential credential = EmailAuthProvider.getCredential(email, oldpass);
                                mCurrentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mProgress.setMessage("Updating Password...");
                                            mCurrentUser.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {
                                                        Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
			                        	                mProgress.dismiss();

                                                    } else {
                                                        Toast.makeText(ProfileActivity.this, "Password Updated Successfully", Toast.LENGTH_LONG).show();
                                                        mProgress.dismiss();
                                                        current_user_db.child("Password").setValue(newpass);

                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                                            mProgress.dismiss();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Passwords Don't match", Toast.LENGTH_LONG).show();
                                flag1=1;
                            }
                        }else{
                            Toast.makeText(ProfileActivity.this, "Some Fields are Empty", Toast.LENGTH_LONG).show();
                            flag1=1;
                        }
                    }
                });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog.setCancelable(false);
        alertDialog.create().show();
        if(flag1==1){

            ((TextView)findViewById(R.id.cancel)).performClick();
            flag1=0;
        }

    }
}
