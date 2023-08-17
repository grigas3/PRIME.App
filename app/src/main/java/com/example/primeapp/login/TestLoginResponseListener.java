package com.example.primeapp.login;

import android.util.Log;

import com.android.volley.Response;
import com.example.primeapp.interfaces.ICredentialsProvider;
import com.example.primeapp.interfaces.IDataPostNotifier;

import org.json.JSONException;
import org.json.JSONObject;

public class TestLoginResponseListener implements Response.Listener<String>, ICredentialsProvider {
    boolean responseOk = false;
    String accessToken;
    String patientId;

    public String getPatientId() {
        return patientId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean isResponseOk() {
        return responseOk;
    }

    @Override
    public void onResponse(String response) {

        JSONObject objson = null;
        try {
            objson = new JSONObject(response);
            Log.d("TEST",response);
            accessToken = objson.getString("access_token");
            patientId = objson.getString("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        responseOk = true;

    }



}
