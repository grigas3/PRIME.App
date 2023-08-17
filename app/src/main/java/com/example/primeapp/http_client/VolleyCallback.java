package com.example.primeapp.http_client;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interface to get response from Request
 */
public interface VolleyCallback {
    void onSuccess(JSONObject result) throws JSONException;
    void onError(String result) throws Exception;
}
