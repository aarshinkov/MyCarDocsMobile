package com.atanasvasil.mobile.mycardocs.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.adapters.PolicyAdapter;
import com.atanasvasil.mobile.mycardocs.responses.policies.Policy;

import java.util.ArrayList;
import java.util.List;

public class PoliciesFragment extends Fragment {

    private RecyclerView recyclerView;
    private PolicyAdapter policyAdapter;
    private List<Policy> policies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_policies, container, false);

        recyclerView = root.findViewById(R.id.policies);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        policies = new ArrayList<>();
        policyAdapter = new PolicyAdapter(getContext(), policies);
        recyclerView.setAdapter(policyAdapter);
        Policy policy = new Policy();
        policy.setType(1);

        policies.add(policy);

        policy = new Policy();
        policy.setType(3);

        policies.add(policy);
        policyAdapter.notifyDataSetChanged();
        return root;


    }
}