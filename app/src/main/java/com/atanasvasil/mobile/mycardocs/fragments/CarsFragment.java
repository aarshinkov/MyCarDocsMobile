package com.atanasvasil.mobile.mycardocs.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.cars.CarCreateActivity;
import com.atanasvasil.mobile.mycardocs.adapters.CarAdapter;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CarsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CarAdapter carAdapter;
    private List<Car> cars;

    private FloatingActionButton carCreateFBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cars, container, false);

        recyclerView = root.findViewById(R.id.cars);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cars = new ArrayList<>();
        carAdapter = new CarAdapter(getContext(), cars);
        recyclerView.setAdapter(carAdapter);

        carCreateFBtn = root.findViewById(R.id.carCreateFBtn);
        carCreateFBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CarCreateActivity.class);
            startActivity(intent);
        });

        return root;
    }
}