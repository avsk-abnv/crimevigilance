package com.abnv.flamefreezer.crimevigilance;

import android.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

public class AddCrimeActivity extends AppCompatActivity {
    ImageView imageView;
    ProgressDialog mProgress;
    Uri imageUri = null;
    RadioGroup rg;  RadioButton Solved,Unsolved;
    private StorageReference mStorage;
    private EditText title, description,zipCode,city,contact;
    private static final int GALLERY_REQUEST = 1;
    private DatabaseReference mDatabase, mDatabaseUser;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crime);
        Toolbar tb = (Toolbar) findViewById(R.id.add_a_crime_bar);
        mStorage = FirebaseStorage.getInstance().getReference();
        setSupportActionBar(tb);
        title = (EditText) findViewById(R.id.editText);
        description = (EditText) findViewById(R.id.editText2);
        zipCode = (EditText) findViewById(R.id.editText3);
        city = (EditText) findViewById(R.id.editText4);
        rg = (RadioGroup)findViewById(R.id.RadioStatus);
        Solved= (RadioButton)findViewById(R.id.Solved);
        Unsolved= (RadioButton)findViewById(R.id.Unsolved);
        contact= (EditText)findViewById(R.id.contact);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Crime_Details");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        imageView = (ImageView) findViewById(R.id.imageButton);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });
        mProgress = new ProgressDialog(this);
    }

    public void onChangeClick(View v) {
        onSelectImageClick(v);
    }

    private void onSelectImageClick(View v) {
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_REQUEST);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK) {
            /*final Bundle extras = data.getExtras();
            if (extras != null) {
                //Get image
                Bitmap bitmap = extras.getParcelable("data");
                imageButton.setImageBitmap(bitmap);*/
            imageUri = data.getData();
            imageView.setVisibility(View.GONE);
            ((Button) findViewById(R.id.button2)).setVisibility(View.VISIBLE);
            ((Button)findViewById(R.id.button5)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.imageView)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.imageView)).setImageURI(imageUri);

        }
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageView.setVisibility(View.GONE);
                imageUri = result.getUri();
                ((Button) findViewById(R.id.button2)).setVisibility(View.VISIBLE);
                ((Button)findViewById(R.id.button5)).setVisibility(View.VISIBLE);
                ((ImageView) findViewById(R.id.imageView)).setVisibility(View.VISIBLE);
                ((ImageView) findViewById(R.id.imageView)).setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onClickCrop(View v){
        startCropImageActivity(imageUri);
    }
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(AddCrimeActivity.this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.exit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(AddCrimeActivity.this, LoggedInActivity.class);
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
        startActivity(new Intent(AddCrimeActivity.this, LoggedInActivity.class));
        finish();
    }

    public void onClick(View v) {
        startStoring();
    }

    private void startStoring() {
        final String title_val = title.getText().toString().trim();
        final String desc = description.getText().toString().trim();
        final String zipcode= zipCode.getText().toString().trim();
        final String City= city.getText().toString().trim();
        final String contact_no= contact.getText().toString().trim();
        final String Status = ((RadioButton)findViewById(rg.getCheckedRadioButtonId())).getText().toString();
        if (!TextUtils.isEmpty(zipcode)&&!TextUtils.isEmpty(City)&&!TextUtils.isEmpty(title_val)&&!TextUtils.isEmpty(desc)
                &&imageUri!=null&&(Solved.isChecked()||Unsolved.isChecked())&&!TextUtils.isEmpty(contact_no) ) {
            if(zipcode.length()==6 && contact_no.length()==10) {
                mProgress.setMessage("Uploading Data...");
                mProgress.show();

                StorageReference filePath = mStorage.child("Blog_images").child(imageUri.getLastPathSegment());
                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();
                        DatabaseReference newpost = mDatabase.push();
                        newpost.child("title").setValue(title_val);
                        newpost.child("zipCode").setValue(zipcode);
                        newpost.child("city").setValue(City);
                        newpost.child("desc").setValue(desc);
                        newpost.child("contact").setValue(contact_no);
                        newpost.child("status").setValue(Status);
                        newpost.child("image").setValue(downloadUri.toString());
                        newpost.child("uid").setValue(mCurrentUser.getUid());
                        mProgress.dismiss();
                        ((EditText) findViewById(R.id.editText)).setText("");
                        ((EditText) findViewById(R.id.editText2)).setText("");
                        zipCode.setText("");
                        city.setText("");
                        contact.setText("");
                        ((ImageView) findViewById(R.id.imageView)).setImageDrawable(null);
                        ((ImageView) findViewById(R.id.imageView)).setVisibility(View.INVISIBLE);
                        ((ImageView) findViewById(R.id.imageButton)).setVisibility(View.VISIBLE);
                        ((RadioGroup) findViewById(R.id.RadioStatus)).clearCheck();
                        ((Button) findViewById(R.id.button2)).setVisibility(View.GONE);
                        ((Button) findViewById(R.id.button5)).setVisibility(View.GONE);
                        ((Button) findViewById(R.id.button4)).performClick();
                        // startActivity(new Intent(AddCrimeActivity.this,PostPage.class));
                    }
                });
            }else{
                Toast.makeText(AddCrimeActivity.this,"Invalid ZipCode or Contact no.",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(AddCrimeActivity.this,"Some Fields are Empty",Toast.LENGTH_LONG).show();
        }
    }
    public void onSnack(View v){
        Snackbar.make(v, "Successfully Uploaded..", Snackbar.LENGTH_LONG)
                .show();
    }

}
