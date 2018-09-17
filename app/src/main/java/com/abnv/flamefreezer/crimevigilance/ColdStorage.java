package com.abnv.flamefreezer.crimevigilance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by Abhishek Abhinav on 13-07-2017.
 */

public class ColdStorage {
    Context context;
    SharedPreferences preferences;
    public ColdStorage(Context context){
        this.context= context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public void saveData(String email,String password){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Email",email);
        editor.commit();
        editor.putString("Password",password);
        editor.commit();
        editor.putBoolean("flag",true);
        editor.commit();
    }
    public String getEmail() {
        return preferences.getString("Email", "");
    }
    public String getPassword(){
        return preferences.getString("Password", "");
    }
    public boolean getFlag(){
        return preferences.getBoolean("flag", false);
    }
}
