package com.atanasvasil.mobile.mycardocs.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.policies.PolicyCreateActivity;
import com.atanasvasil.mobile.mycardocs.adapters.PolicyAdapter;
import com.atanasvasil.mobile.mycardocs.api.PoliciesApi;
import com.atanasvasil.mobile.mycardocs.api.UsersApi;
import com.atanasvasil.mobile.mycardocs.responses.policies.Policy;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.jetbrains.annotations.NotNull;

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
import static com.atanasvasil.mobile.mycardocs.utils.Utils.getStringResource;

public class PoliciesFragment extends Fragment {

    private MaterialButton policiesFilterBtn;
    private TextView policiesSelectedTypeTV;
    private RecyclerView recyclerView;
    private PolicyAdapter policyAdapter;
    private List<Policy> policies;
    private FloatingActionButton policyCreateFBtn;

    private Spinner pfTypeSP;
    private MaterialButton pfApplyBtn;

    private SwipeRefreshLayout policiesNoItemsRefresh;
    private SwipeRefreshLayout policiesRefresh;

    private CircularProgressIndicator progress;

    private TextView noCarsForPolicyTV;

    private SharedPreferences pref;
    private LoggedUser loggedUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_policies, container, false);

        policiesNoItemsRefresh = root.findViewById(R.id.policiesNoItemsRefresh);
        policiesRefresh = root.findViewById(R.id.policiesRefresh);
        policyCreateFBtn = root.findViewById(R.id.policyCreateFBtn);
        policiesFilterBtn = root.findViewById(R.id.policiesFilterBtn);
        policiesSelectedTypeTV = root.findViewById(R.id.policiesSelectedTypeTV);

        recyclerView = root.findViewById(R.id.policies);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noCarsForPolicyTV = root.findViewById(R.id.noCarsForPolicyTV);

        policies = new ArrayList<>();
        policyAdapter = new PolicyAdapter(getContext(), policies);
        recyclerView.setAdapter(policyAdapter);

        progress = root.findViewById(R.id.policiesProgress);
        progress.setVisibility(View.VISIBLE);

        pref = getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        getPoliciesByUserId();
        hasUserCars();

        policiesNoItemsRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPoliciesByUserId();
                hasUserCars();
                policiesNoItemsRefresh.setRefreshing(false);
            }
        });

        policiesRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPoliciesByUserId();
                hasUserCars();
                policiesRefresh.setRefreshing(false);
            }
        });

        policyCreateFBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PolicyCreateActivity.class);
            startActivity(intent);
        });

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);

        final View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_policies_filter, root.findViewById(R.id.policiesFilterContainer));

        pfTypeSP = bottomSheetView.findViewById(R.id.pfTypeSP);
        pfApplyBtn = bottomSheetView.findViewById(R.id.pfApplyBtn);

        bottomSheetDialog.setContentView(bottomSheetView);

        policiesFilterBtn.setOnClickListener(v -> {
            bottomSheetDialog.show();
        });

        pfApplyBtn.setOnClickListener(v -> {
            final int type = pfTypeSP.getSelectedItemPosition();

            if (type == 0) {
                policiesSelectedTypeTV.setVisibility(View.GONE);
                getPoliciesByUserId();
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
                return;
            }

            policiesSelectedTypeTV.setVisibility(View.VISIBLE);
            policiesSelectedTypeTV.setText(requireContext().getString(R.string.policies_selected_type, getStringResource(requireContext(), "policy_type_" + type)));

            Retrofit retrofit = getRetrofit();
            PoliciesApi policiesApi = retrofit.create(PoliciesApi.class);

            policiesApi.getPoliciesByType(type, loggedUser.getUserId(), loggedUser.getAuthorization()).enqueue(new Callback<List<Policy>>() {
                @Override
                public void onResponse(@NotNull Call<List<Policy>> call, @NotNull Response<List<Policy>> response) {

                    if (response.code() == 400) {
                        policies.clear();
                        policyAdapter.notifyDataSetChanged();
                        policiesNoItemsRefresh.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                        return;
                    }

                    List<Policy> storedPolicies = response.body();

                    updatePolicies(storedPolicies);
                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NotNull Call<List<Policy>> call, @NotNull Throwable t) {
                    Toast.makeText(getContext(), R.string.error_server, Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                }
            });

            bottomSheetDialog.setDismissWithAnimation(true);
            bottomSheetDialog.dismiss();
        });

        return root;
    }

    private void updatePolicies(List<Policy> storedPolicies) {
        policies.clear();
        policyAdapter.notifyDataSetChanged();

        if (storedPolicies != null) {
            policies.addAll(storedPolicies);

            final int size = storedPolicies.size();

            if (size <= 0) {
                policiesNoItemsRefresh.setVisibility(View.VISIBLE);
                policiesRefresh.setVisibility(View.INVISIBLE);
            } else {
                policiesNoItemsRefresh.setVisibility(View.INVISIBLE);
                policiesRefresh.setVisibility(View.VISIBLE);
            }
        }

        policyAdapter.notifyDataSetChanged();
    }

    private void getPoliciesByUserId() {

        Retrofit retrofit = getRetrofit();
        PoliciesApi policiesApi = retrofit.create(PoliciesApi.class);

        policiesApi.getPoliciesByUserId(loggedUser.getUserId(), loggedUser.getAuthorization()).enqueue(new Callback<List<Policy>>() {
            @Override
            public void onResponse(@NotNull Call<List<Policy>> call, @NotNull Response<List<Policy>> response) {

                if (response.code() == 400) {
                    policies.clear();
                    policyAdapter.notifyDataSetChanged();
                    policiesNoItemsRefresh.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    return;
                }

                List<Policy> storedPolicies = response.body();

                updatePolicies(storedPolicies);
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<List<Policy>> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), R.string.error_server, Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });

        policyAdapter.notifyDataSetChanged();
    }

    private void hasUserCars() {
        Retrofit retrofit = getRetrofit();
        UsersApi usersApi = retrofit.create(UsersApi.class);

        usersApi.hasUserCars(loggedUser.getUserId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NotNull Call<Boolean> call, @NotNull Response<Boolean> response) {

                if (response.body() == null) {
                    return;
                }

                if (response.body()) {
                    noCarsForPolicyTV.setVisibility(View.GONE);
                    policyCreateFBtn.setVisibility(View.VISIBLE);
                } else {
                    noCarsForPolicyTV.setVisibility(View.VISIBLE);
                    policyCreateFBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Boolean> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        getPoliciesByUserId();
        super.onResume();
    }
}