package com.atanasvasil.mobile.mycardocs.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atanasvasil.mobile.mycardocs.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ShelvesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton shelvesCreateFBtn;
    private SharedPreferences pref;
    private ProgressDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shelves, container, false);

        recyclerView = root.findViewById(R.id.shelves);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        loadingDialog = new ProgressDialog(getContext());
        loadingDialog.setMessage("Loading cars...");
        loadingDialog.show();

    }
}