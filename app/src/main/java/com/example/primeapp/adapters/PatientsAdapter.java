package com.example.primeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.primeapp.MainActivity;
import com.example.primeapp.R;
import com.example.primeapp.models.Observation;
import com.example.primeapp.models.ObservationCollection;
import com.example.primeapp.models.Patient;
import com.example.primeapp.models.PatientCollection;

import java.text.DecimalFormat;



public class PatientsAdapter extends RecyclerView.Adapter<PatientsAdapter.ViewHolder> {

    private final Context context;
    private final PatientCollection courseModelArrayList;

    // Constructor
    public PatientsAdapter(Context context, PatientCollection courseModelArrayList) {
        this.context = context;
        this.courseModelArrayList = courseModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        Patient model = courseModelArrayList.get(position);
        holder.obsName.setText(model.getName());
        holder.obsValue.setText(model.getMRN());

        holder.SetHandler(new PatientClickHandler(context,model.getPatientId()));
    }

    public class PatientClickHandler
    {
        private final Context context;
        private final String patientId;
        PatientClickHandler(Context c,String p)
        {
            this.patientId=p;
            this.context=c;

        }

        public void Invoke(){

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("patientId", patientId);
            editor.apply();
            context.startActivity(new Intent(context, MainActivity.class));
        }

    }


    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return courseModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView obsName;
        private final TextView obsValue;
        private  PatientClickHandler clickHandler;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            obsName = itemView.findViewById(R.id.idPatientName);
            obsValue = itemView.findViewById(R.id.idPatientMRN);

            itemView.setOnClickListener(e->{

                if(clickHandler==null)
                    return;
                clickHandler.Invoke();

            });
        }

        public void SetHandler(PatientClickHandler patientClickHandler) {
            clickHandler=patientClickHandler;
        }
    }
}
