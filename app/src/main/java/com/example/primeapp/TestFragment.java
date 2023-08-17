package com.example.primeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.primeapp.databinding.FragmentTestBinding;
import com.example.primeapp.models.TestCodes;


public class TestFragment extends Fragment {

    private FragmentTestBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentTestBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       binding.walkingTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("test", TestCodes.Walking);
                NavHostFragment.findNavController(TestFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);
            }
        });


        binding.restingTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("test", TestCodes.Resting);
                NavHostFragment.findNavController(TestFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);
            }
        });


        binding.pronSup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("test", TestCodes.PronationSup);
                NavHostFragment.findNavController(TestFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}