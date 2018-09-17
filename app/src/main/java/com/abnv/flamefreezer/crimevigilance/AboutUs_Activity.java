package com.abnv.flamefreezer.crimevigilance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AboutUs_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }
    public void onClickBack(View view){
        Intent intent= new Intent(AboutUs_Activity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(AboutUs_Activity.this, MainActivity.class));
        finish();
    }
}
