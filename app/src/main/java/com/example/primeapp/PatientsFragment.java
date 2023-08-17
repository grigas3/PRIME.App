package com.example.primeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.primeapp.adapters.PatientsAdapter;
import com.example.primeapp.adapters.ResultsAdapter;
import com.example.primeapp.databinding.FragmentPatientsBinding;
import com.example.primeapp.databinding.FragmentResultsBinding;
import com.example.primeapp.http_client.SSLHelper;
import com.example.primeapp.http_client.VolleySingleton;
import com.example.primeapp.interfaces.ICredentialsProvider;
import com.example.primeapp.interfaces.IDataPostNotifier;
import com.example.primeapp.interfaces.IPatientDataNotifier;
import com.example.primeapp.models.ObservationCollection;
import com.example.primeapp.models.Patient;
import com.example.primeapp.models.PatientCollection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PatientsFragment extends Fragment implements IPatientDataNotifier, ICredentialsProvider {

    private FragmentPatientsBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentPatientsBinding.inflate(inflater, container, false);

        Button buttonReload = (Button) binding.buttonReload.findViewById(R.id.button_reload);
        if(buttonReload!=null) {
            buttonReload.setOnClickListener(e -> {

                postResults();
            });
        }

        return binding.getRoot();


    }

    private void createCardView(Context context, PatientCollection results) {


        if (results == null)
            return;

        // we are initializing our adapter class and passing our arraylist to it.
        PatientsAdapter courseAdapter = new PatientsAdapter(context, results);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        binding.idResultList.setLayoutManager(linearLayoutManager);
        binding.idResultList.setAdapter(courseAdapter);

      /*  binding.idResultList.addOnItemTouchListener(
                new RecyclerViewClickListener(context, binding.idResultList ,new RecyclerViewClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = preferences.edit();

                        var patientId=courseAdapter.
                        editor.putString("patientId", patientId);

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );*/
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postResults();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static final String get_url = "https://195.251.192.85:441/api/patient";

    public static void getPatients(RequestQueue mQueue,
                                   String getUrl,
                                   ICredentialsProvider credentialsProvider,
                                   IPatientDataNotifier dataPostNotifier) {
        Log.d("PRIME", "POST STARTED");

        if (dataPostNotifier != null)
            dataPostNotifier.onGetStart();
        try {
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            //Toast.makeText(thisActivity, "Success Post ", Toast.LENGTH_LONG).show();
                            Log.i("PRIME", "POST SUCCESS");
                            Log.d("POST", "Response->" + response);
                            dataPostNotifier.onGetEnd(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error.networkResponse != null)

                                Log.d("PRIME", "Failure: " + error.networkResponse.statusCode + error.getMessage());
                            else
                                Log.d("PRIME", "Failure: " + error.getMessage());

                            dataPostNotifier.onGEtError();
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

                    if (credentialsProvider != null) {

                        headers.put("Authorization", "Bearer " + credentialsProvider.getAccessToken());
                    }
                    return headers;
                }
            };
            mQueue.add(jsonObjectRequest);
            //    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        } catch (Exception e1) {
            Log.e("PRIME", "PRINT EXCEPTION HERE");
            Log.e("PRIME", e1.getMessage());
            e1.getMessage();
            e1.printStackTrace();
        }

    }




    private void postResults() {

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        Gson gson = new Gson();
        SSLHelper.disableSSLCertificateChecking();


        RequestQueue mQueue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        getPatients(mQueue, get_url, this, this);


    }


    @Override
    public String getAccessToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String accessToken = preferences.getString("token", null);
        return accessToken;
    }

    @Override
    public void onGetStart() {

    }

    @Override
    public void onGetEnd(JSONObject json) {
        binding.layoutActivityIndicator.setVisibility(View.VISIBLE);
        PatientCollection patientCollection = CreatePatientCollection(json);
        createCardView(this.getContext(), patientCollection);

    }

    private PatientCollection CreatePatientCollection(JSONObject json) {

        PatientCollection collection=new PatientCollection();
        try {
            JSONArray jsonArray=json.getJSONArray(("Data"));
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);

                Patient p=new Patient(jsonObject.getString("Key"),jsonObject.getString("FamilyName"),jsonObject.getString("GivenName"),jsonObject.getString("BornDate"),jsonObject.getString("MRN"));
                collection.add(p);
            }

        } catch (JSONException e) {
          //  throw new RuntimeException(e);
            //TODO: Handle
        }
        return collection;
    }

    @Override
    public void onGEtError() {

    }
}