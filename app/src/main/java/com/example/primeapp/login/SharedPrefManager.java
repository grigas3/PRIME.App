package com.example.primeapp.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.primeapp.LoginActivity;
import com.example.primeapp.ResultsFragment;


/**
 * We use singleton pattern
 * SharedPrefManager for storing User information and Token
 */
public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "user_info";
    private static final String KEY_USERNAME = "keyusername";

    private static final String KEY_EXPIRES_AT = "keyexpires";
    private static final String KEY_ACCESS_TOKEN = "accesstoken";
    private static final String KEY_REFRESH_TOKEN = "refreshtoken";
    private static final String KEY_PATIENT_ID = "patientid";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    /**
     * method to let the user login
     * this method will store the user data in shared preferences
     */
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EXPIRES_AT, user.getExpiresAt());
        editor.putString(KEY_REFRESH_TOKEN, user.getRefreshtoken());
        editor.putString(KEY_PATIENT_ID, user.getPatientID());

        editor.apply();
    }



    /**
     * this method will checker whether user is already logged in or not
     */
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null) != null;
    }

    /**
     * this method will give the logged in user
     */
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(

                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_EXPIRES_AT, null),
                sharedPreferences.getString(KEY_ACCESS_TOKEN, null),
                sharedPreferences.getString(KEY_PATIENT_ID, null)


        );
    }




    /**
     * this method will logout the user
     */
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }
}
