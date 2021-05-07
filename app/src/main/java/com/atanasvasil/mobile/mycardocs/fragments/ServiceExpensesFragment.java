package com.atanasvasil.mobile.mycardocs.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.fuel.FuelExpenseCreateActivity;
import com.atanasvasil.mobile.mycardocs.activities.service.ServiceExpenseCreateActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ServiceExpensesFragment extends Fragment {

    private FloatingActionButton serviceExpenseCreateBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_service_expenses, container, false);

        serviceExpenseCreateBtn = root.findViewById(R.id.serviceExpenseCreateBtn);

        serviceExpenseCreateBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ServiceExpenseCreateActivity.class);
            startActivity(intent);
        });

        return root;
    }
}
