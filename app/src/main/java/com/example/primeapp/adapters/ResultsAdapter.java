package com.example.primeapp.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.primeapp.R;
import com.example.primeapp.ResultsFragment;
import com.example.primeapp.models.Observation;
import com.example.primeapp.models.ObservationCollection;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {

    private final Context context;
    private final ObservationCollection courseModelArrayList;

    // Constructor
    public ResultsAdapter(Context context, ObservationCollection courseModelArrayList) {
        this.context = context;
        this.courseModelArrayList = courseModelArrayList;
    }

    @NonNull
    @Override
    public ResultsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsAdapter.ViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        Observation model = courseModelArrayList.get(position);
        holder.obsName.setText(model.getCode());
        holder.obsValue.setText("" +df.format(model.getValue()));

    }
    private static final DecimalFormat df = new DecimalFormat("0.00");
    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return courseModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView obsName;
        private final TextView obsValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            obsName = itemView.findViewById(R.id.idObsName);
            obsValue = itemView.findViewById(R.id.idObsValue);
        }
    }
}