package com.abnv.flamefreezer.crimevigilance;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ImageView;
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

public class AddMissingPersonActivity extends AppCompatActivity {
    ImageView imageView;
    ProgressDialog mProgress;
    Uri imageUri = null;
    private StorageReference mStorage;
    private EditText name,age,last_seen,height,appearance,contact;
    private static final int GALLERY_REQUEST = 1;
    private DatabaseReference mDatabase, mDatabaseUser;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_missing_person);
        Toolbar tb= (Toolbar)findViewById(R.id.add_a_missingperson_bar);
        setSupportActionBar(tb);
        mStorage = FirebaseStorage.getInstance().getReference();
        name = (EditText) findViewById(R.id.Name);
        age = (EditText) findViewById(R.id.Age);
        last_seen = (EditText) findViewById(R.id.LastSeen);
        height = (EditText) findViewById(R.id.Height);
        contact= (EditText)findViewById(R.id.contact_no);
        appearance = (EditText) findViewById(R.id.Appearance);
        final ActionBar ab = getSupportActionBar();
        if(ab!=null){
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Missing_Persons");
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

    private void onSelectImageClick(View v) {
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_REQUEST);
    }

    public void onChangeClick(View v) {
        onSelectImageClick(v);
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
            ((Button) findViewById(R.id.Crop)).setVisibility(View.VISIBLE);
            ((Button)findViewById(R.id.Change)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.imageView)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.imageView)).setImageURI(imageUri);

        }
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageView.setVisibility(View.GONE);
                imageUri = result.getUri();
                ((Button) findViewById(R.id.Change)).setVisibility(View.VISIBLE);
                ((Button)findViewById(R.id.Crop)).setVisibility(View.VISIBLE);
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
                .start(AddMissingPersonActivity.this);
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
                Intent intent = new Intent(AddMissingPersonActivity.this, LoggedInActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menu_main_Exit:
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(AddMissingPersonActivity.this, LoggedInActivity.class));
        finish();
    }
    public void onClick(View v) {
        startStoring();
    }

    private void startStoring() {
        final String Name = name.getText().toString().trim();
        final String Age = age.getText().toString().trim();
        final String Height= height.getText().toString().trim();
        final String Last_seen= last_seen.getText().toString().trim();
        final String Appearance= appearance.getText().toString().trim();
        final String contact_no= contact.getText().toString().trim();
        RadioGroup rg = (RadioGroup)findViewById(R.id.RadioGender);
        RadioButton Male= (RadioButton)findViewById(R.id.Male);
        RadioButton Female= (RadioButton)findViewById(R.id.Female);
        final String Gender = ((RadioButton)findViewById(rg.getCheckedRadioButtonId())).getText().toString();
        if (!TextUtils.isEmpty(Name)&&!TextUtils.isEmpty(Age)&&!TextUtils.isEmpty(Height)&&!TextUtils.isEmpty(Last_seen)
                &&imageUri!=null&&!TextUtils.isEmpty(Appearance)&&(Male.isChecked()||Female.isChecked())&&!TextUtils.isEmpty(contact_no) ){
            if(contact_no.length()==10) {
                mProgress.setMessage("Uploading Data...");
                mProgress.show();

                StorageReference filePath = mStorage.child("Missing_Persons_pics").child(imageUri.getLastPathSegment());
                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();
                        DatabaseReference newpost = mDatabase.push();
                        newpost.child("name").setValue(Name);
                        newpost.child("age").setValue(Age);
                        newpost.child("height").setValue(Height);
                        newpost.child("last_seen").setValue(Last_seen);
                        newpost.child("contact_no").setValue(contact_no);
                        newpost.child("appearance").setValue(Appearance);
                        newpost.child("gender").setValue(Gender);
                        newpost.child("image").setValue(downloadUri.toString());
                        newpost.child("uid").setValue(mCurrentUser.getUid());
                        mProgress.dismiss();
                        name.setText("");
                        age.setText("");
                        height.setText("");
                        appearance.setText("");
                        last_seen.setText("");
                        contact.setText("");
                        ((ImageView) findViewById(R.id.imageView)).setImageDrawable(null);
                        ((ImageView) findViewById(R.id.imageView)).setVisibility(View.INVISIBLE);
                        ((ImageView) findViewById(R.id.imageButton)).setVisibility(View.VISIBLE);
                        ((RadioGroup) findViewById(R.id.RadioGender)).clearCheck();
                        ((Button) findViewById(R.id.Change)).setVisibility(View.GONE);
                        ((Button) findViewById(R.id.Crop)).setVisibility(View.GONE);
                        ((Button) findViewById(R.id.Snackbar)).performClick();
                        // startActivity(new Intent(AddCrimeActivity.this,PostPage.class));
                    }
                });
            }else{
                Toast.makeText(AddMissingPersonActivity.this,"Invalid Contact No.",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(AddMissingPersonActivity.this,"Some Fields are Empty",Toast.LENGTH_LONG).show();
        }
    }
    public void onSnack(View v){
        Snackbar.make(v, "Successfully Uploaded..", Snackbar.LENGTH_LONG)
                .show();
    }
}
