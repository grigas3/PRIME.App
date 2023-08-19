package com.example.primeapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.primeapp.http_client.SSLHelper;
import com.example.primeapp.http_client.VolleySingleton;
import com.example.primeapp.login.SharedPrefManager;
import com.example.primeapp.login.User;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH_ADVERTISE;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;
import static android.Manifest.permission.CAMERA;
public  class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private TextInputLayout emailError, passError;
    private TextView pass_frg;
    private String login_url, register_patient_url, patient_id_url, register_sole_url, pair_patient_sole_url;

    private User user;
    public String savedAcessToken="";
    private SharedPreferences username;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //construct urls
        initUrls();



        // If user already logged in
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            // check if token has expired
            //            if (!(new UserHelper(this)).hasExpired()){
            //                finish();
            //                startActivity(new Intent(this, MainActivity.class));
            //            }
        }

        username = this.getPreferences(Context.MODE_PRIVATE);
        editor = username.edit();

        Button login;

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        emailError = findViewById(R.id.emailError);
        passError = findViewById(R.id.passError);


        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(email.getText().toString().length()>0 && password.getText().toString().length()>0)
                {

                    userLogin();
                }
                else
                {
                    SetValidation();
                }



            }
        });

        //Application Permissions check
        checkPermission();


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

    }





    private void initUrls() {

        login_url = "https://195.251.192.85:441/token";//getResources().getString(R.string.base_url) + getResources().getString(R.string.login_url);

    }

    /**
     * Email and Password Validator
     */
    public void SetValidation() {
        boolean isEmailValid, isPasswordValid;
        // Check for a valid email address.
        if (email.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
            //        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            //            emailError.setError(getResources().getString(R.string.error_invalid_email));
            //            isEmailValid = false;
        } else if (email.getText().length() < 4) {
            emailError.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (password.getText().toString().isEmpty()) {
            passError.setError(getResources().getString(R.string.password_error));
            isPasswordValid = false;
        } else if (password.getText().length() < 6) {
            passError.setError(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
        }


    }



    /**
     * Disables the SSL certificate checking for new instances of {@link HttpsURLConnection} This has been created to
     * aid testing on a local box, not for use on production.
     */

    public static StringRequest userLoginRequest(String url, String userName, String password, Response.Listener<String> responseListener,
                                                 Context context
    )
    {

        StringRequest request = new StringRequest(Request.Method.POST, url, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("LOGIN", "onErrorResponse: " +error.getMessage());
                if(context!=null)
                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "password");
                params.put("username", userName);
                params.put("password", password);
                return params;
            }
        };

        return request;

    }
    private void gotoPatientActivity(){
        finish();        startActivity(new Intent(this, PatientActivity.class));

    }
    private void gotoMainActivity(){
        finish();        startActivity(new Intent(this, MainActivity.class));

    }

    /**

     * Login and retrieve token
     * with Volley Android Library
     * after v19 (Android 5), TLS 1.2 is enabled by default.
     */
    public void userLogin() {
        RequestQueue mQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();

        SSLHelper.disableSSLCertificateChecking();
        Response.Listener<String> listener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Log.d("LOGIN", "RESPONSE : \"" + response + "\"");
                    JSONObject objson = new JSONObject(response);
                    savedAcessToken = objson.getString("access_token");
                    // get new response
                   // prefs = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                    if (objson.has("access_token")) {


                        //store the user in shared preferences
                      //  SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        String accessToken = objson.getString("access_token");
                        String  patientId="";
                        if(objson.has("id"))
                            patientId = objson.getString("id");
                        String  expires_in = objson.getString("expires_in");
                        String  role = objson.getString("roleName");

                        editor.putString("role",role);
                        editor.putString("token",accessToken);
                        editor.putString("patientId", patientId);
                        editor.putString("expires_in", expires_in);
                        editor.apply();

                        if(role.toLowerCase().equals("doctor"))
                        {

                            gotoPatientActivity();
                        }else{
                            gotoMainActivity();
                        }


                    } else {
                        Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                }
            }
        };

        Log.d("LOGIN","User name: "+this.email.getText().toString()+userNameSuffix);
        Log.d("LOGIN","Pass: "+this.password.getText().toString());
        Log.d("LOGIN","URL: "+login_url);
        StringRequest request=userLoginRequest(login_url,
                this.email.getText().toString()+userNameSuffix,
                this.password.getText().toString(),
                listener,
                getApplicationContext());
        mQueue.add(request);
    }

    private final String userNameSuffix="@PRIME";



    /**
     * Refresh token when expired
     * with Volley Android Library
     * after v19 (Android 5), TLS 1.2 is enabled by default.
     */
    private void refreshToken() {
    }

    /**
     * Check for all permission for the app
     */
    private void checkPermission2() {

        //Check for READ_EXTERNAL_STORAGE access, using ContextCompat.checkSelfPermission()//
        int resultGET_ACCOUNTS = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        int resultBLUETOOTH = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int resultBLUETOOTH_ADMIN = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        int resultACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int resultACCESS_NETWORK_STATE = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        int resultINTERNET = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);


        //If the app does have this permission, then return true//
        //If the app does not have this permission, then ask permission//
        ArrayList<String> missingPerms = new ArrayList<>();

        if (resultGET_ACCOUNTS == PackageManager.PERMISSION_DENIED)
            missingPerms.add(Manifest.permission.GET_ACCOUNTS);
        if (resultBLUETOOTH == PackageManager.PERMISSION_DENIED)
            missingPerms.add(Manifest.permission.BLUETOOTH);
        if (resultBLUETOOTH_ADMIN == PackageManager.PERMISSION_DENIED)
            missingPerms.add(Manifest.permission.BLUETOOTH_ADMIN);
        if (resultACCESS_COARSE_LOCATION == PackageManager.PERMISSION_DENIED)
            missingPerms.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (resultACCESS_NETWORK_STATE == PackageManager.PERMISSION_DENIED)
            missingPerms.add(Manifest.permission.ACCESS_NETWORK_STATE);
        if (resultINTERNET == PackageManager.PERMISSION_DENIED)
            missingPerms.add(Manifest.permission.INTERNET);

        if (!missingPerms.isEmpty()) {
            String[] missingPermsArray = missingPerms.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, (missingPermsArray), 1);
        }

        ActivityCompat.requestPermissions(this, new String[]{ BLUETOOTH_CONNECT, BLUETOOTH_SCAN}, PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE1);

    }


    private void checkPermission() {


        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("This app needs location access permission");
                builder.setMessage("Please grant location access permission so this app can detect peripherals.");
                builder.setPositiveButton(android.R.string.ok, (DialogInterface.OnClickListener) (d, e) ->
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION));
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                });
                builder.show();
            }

            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect peripherals.");
                builder.setPositiveButton(android.R.string.ok, (DialogInterface.OnClickListener) (d, e) ->
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION));
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                });
                builder.show();
            }
            if (this.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("This app needs Bluetooth Permission ");
                builder.setMessage("Please grant Bluetooth Permission access so this app can detect peripherals.");
                builder.setPositiveButton(android.R.string.ok, (DialogInterface.OnClickListener) (d, e) ->
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, MY_PERMISSIONS_REQUEST_SCAN));
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, MY_PERMISSIONS_REQUEST_SCAN);
                    }
                });
                builder.show();
            }
            if (this.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("This app needs Bluetooth access");
                builder.setMessage("Please grant Bluetooth access so this app can detect peripherals.");
                builder.setPositiveButton(android.R.string.ok, (DialogInterface.OnClickListener) (d, e) ->
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MY_PERMISSIONS_REQUEST_LOCATION));
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                });
                builder.show();
            }
        }

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_CONNECT = 100;
    public static final int MY_PERMISSIONS_REQUEST_SCAN= 101;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE1 = 201;

}