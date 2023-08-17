package com.example.primeapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.primeapp.adapters.ResultsAdapter;
import com.example.primeapp.databinding.FragmentResultsBinding;
import com.example.primeapp.http_client.SSLHelper;
import com.example.primeapp.http_client.VolleySingleton;
import com.example.primeapp.interfaces.ICredentialsProvider;
import com.example.primeapp.interfaces.IDataPostNotifier;
import com.example.primeapp.login.SharedPrefManager;
import com.example.primeapp.login.User;
import com.example.primeapp.models.Observation;
import com.example.primeapp.models.ObservationCollection;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ResultsFragment extends Fragment implements IDataPostNotifier, ICredentialsProvider{
    private ObservationCollection results;
    private FragmentResultsBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

         results=(ObservationCollection)(getArguments().getSerializable("results"));



        binding = FragmentResultsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    private void createCardView(Context context,ObservationCollection results){


        if(results==null)
            return;

        // we are initializing our adapter class and passing our arraylist to it.
        ResultsAdapter courseAdapter = new ResultsAdapter(context, results);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        binding.idResultList.setLayoutManager(linearLayoutManager);
        binding.idResultList.setAdapter(courseAdapter);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createCardView(this.getContext(),results);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try{
                    postResults();
                }
                catch (Exception ex)
                {
                    Toast.makeText(getContext(),"An error occurred",Toast.LENGTH_SHORT);
                }


            }
        });

     /*   binding.buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ResultsFragment.this)
                        .navigate(R.id.action_ThirdFragment_to_FirstFragment);

            }
        });

      */
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void postResults(RequestQueue mQueue,
                                       String postUrl,
                                       JSONObject json,
                                       ICredentialsProvider credentialsProvider,
                                       IDataPostNotifier dataPostNotifier) {
        Log.d("PRIME","POST STARTED");

        if(dataPostNotifier!=null)
            dataPostNotifier.onPostStart();
        try {
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, json,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            //Toast.makeText(thisActivity, "Success Post ", Toast.LENGTH_LONG).show();
                            Log.i("PRIME","POST SUCCESS");Log.d("POST","Response->"+response);
                            dataPostNotifier.onPostEnd();

                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(error.networkResponse!=null)

                                Log.d("PRIME", "Failure: "+error.networkResponse.statusCode + error.getMessage());
                            else
                                Log.d("PRIME", "Failure: "+ error.getMessage());

                            dataPostNotifier.onPostError();
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
            ) {
                @Override
                public Priority getPriority() {
                    return Priority.HIGH;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();



                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=UTF-8");

                    if(credentialsProvider!=null) {

                        headers.put("Authorization","Bearer "+credentialsProvider.getAccessToken());
                    }
                    return headers;
                }
            };
            mQueue.add(jsonObjectRequest);
            //    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        } catch (Exception e1) {
            Log.e("PRIME","PRINT EXCEPTION HERE");
            Log.e("PRIME",e1.getMessage());
            e1.getMessage();
            e1.printStackTrace();
        }

    }


    private  ObservationCollection createObservations(){


        if(results==null)
            return null;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String patientId= preferences.getString("patientId",null);
        results.forEach((e)->{
            e.setPatientId(patientId);
        });



        return results;


    }

    private static final String post_url="https://195.251.192.85:441/api/measurement/post";
    private void postResults() throws JSONException {

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        Gson gson = new Gson();
        SSLHelper.disableSSLCertificateChecking();
        ObservationCollection results=createObservations();
        results.forEach((e)-> {


            String json = gson.toJson(e);
            Log.d("PRIME", "Send request: " + json);
            JSONObject object = null;
            try {
                object = new JSONObject(json);
                RequestQueue mQueue = VolleySingleton.getInstance(getContext()).getRequestQueue();
                postResults(mQueue, post_url, object, this, this);
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }


        });


    }




    @Override
    public void onPostStart() {

        binding.layoutActivityIndicator.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPostEnd() {
        NavHostFragment.findNavController(ResultsFragment.this)
                .navigate(R.id.action_ThirdFragment_to_FirstFragment);
    }

    @Override
    public void onPostError() {

        binding.layoutActivityIndicator.setVisibility(View.GONE);
        Toast.makeText(getContext(),"An error occurred",Toast.LENGTH_SHORT);


    }

    @Override
    public String getAccessToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String accessToken= preferences.getString("token",null);
        return accessToken;
    }
}