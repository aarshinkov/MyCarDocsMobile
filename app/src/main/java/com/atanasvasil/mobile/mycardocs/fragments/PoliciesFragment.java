package com.atanasvasil.mobile.mycardocs.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.policies.PolicyCreateActivity;
import com.atanasvasil.mobile.mycardocs.adapters.PolicyAdapter;
import com.atanasvasil.mobile.mycardocs.api.PoliciesApi;
import com.atanasvasil.mobile.mycardocs.api.UsersApi;
import com.atanasvasil.mobile.mycardocs.responses.policies.Policy;
import com.atanasvasil.mobile.mycardocs.responses.users.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;
import static com.atanasvasil.mobile.mycardocs.api.Api.getRetrofit;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.Utils.getLoggedUser;

public class PoliciesFragment extends Fragment {

    private RecyclerView recyclerView;
    private PolicyAdapter policyAdapter;
    private List<Policy> policies;
    private SwipeRefreshLayout policiesRefresh;
    private FloatingActionButton policyCreateFBtn;

    private CircularProgressIndicator progress;

    private TextView noPoliciesFoundTV;
    private TextView noActivePoliciesTV;

    private SharedPreferences pref;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_policies, container, false);

        policiesRefresh = root.findViewById(R.id.policiesRefresh);
        policyCreateFBtn = root.findViewById(R.id.policyCreateFBtn);

        recyclerView = root.findViewById(R.id.policies);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noPoliciesFoundTV = root.findViewById(R.id.noPoliciesFoundTV);
        noActivePoliciesTV = root.findViewById(R.id.noActivePoliciesTV);

        policies = new ArrayList<>();
        policyAdapter = new PolicyAdapter(getContext(), policies);
        recyclerView.setAdapter(policyAdapter);

        progress = root.findViewById(R.id.policiesProgress);
        progress.setVisibility(View.VISIBLE);

        pref = getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        user = getLoggedUser(pref);

        getPoliciesByUserId(user.getUserId());

        policiesRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPoliciesByUserId(user.getUserId());
                policiesRefresh.setRefreshing(false);
            }
        });

        policyCreateFBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PolicyCreateActivity.class);
            startActivity(intent);
        });

        Retrofit retrofit = getRetrofit();
        UsersApi usersApi = retrofit.create(UsersApi.class);

        usersApi.hasUserCars(user.getUserId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                if (response.body()) {
                    policyCreateFBtn.setVisibility(View.VISIBLE);
                } else {
                    noActivePoliciesTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });

        return root;
    }

    public void getPoliciesByUserId(Long userId) {

        Retrofit retrofit = getRetrofit();
        PoliciesApi policiesApi = retrofit.create(PoliciesApi.class);

        policiesApi.getPoliciesByUserId(userId).enqueue(new Callback<List<Policy>>() {
            @Override
            public void onResponse(Call<List<Policy>> call, Response<List<Policy>> response) {

                if (response.code() == 400) {
//                    Toast.makeText(getContext(), "No policies found", Toast.LENGTH_LONG).show();
                    noPoliciesFoundTV.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    return;
                }

                noPoliciesFoundTV.setVisibility(View.GONE);

                List<Policy> storedPolicies = response.body();

                policies.clear();

                if (storedPolicies != null) {
                    policies.addAll(storedPolicies);
                }

                policyAdapter.notifyDataSetChanged();
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Policy>> call, Throwable t) {
                Toast.makeText(getContext(), R.string.error_server, Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });

        policyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        getPoliciesByUserId(user.getUserId());
        super.onResume();
    }
}