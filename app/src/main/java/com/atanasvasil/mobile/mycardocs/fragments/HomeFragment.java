package com.atanasvasil.mobile.mycardocs.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.api.PoliciesApi;
import com.atanasvasil.mobile.mycardocs.responses.users.User;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;
import static com.atanasvasil.mobile.mycardocs.api.Api.getRetrofit;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.Utils.getLoggedUser;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout homeRefresh;

    private TextView welcomeTV;
    private CardView carsCountCV;
    private TextView carsCountTV;
    private CircularProgressIndicator carsCountProgress;

    private CardView policiesCountCV;
    private TextView policiesCountTV;
    private CircularProgressIndicator policiesCountProgress;

    private SharedPreferences pref;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        homeRefresh = root.findViewById(R.id.homeRefresh);

        welcomeTV = root.findViewById(R.id.welcomeTV);
        carsCountCV = root.findViewById(R.id.carsCountCV);
        carsCountTV = root.findViewById(R.id.carsCountTV);
        carsCountProgress = root.findViewById(R.id.carsCountProgress);

        policiesCountCV = root.findViewById(R.id.policiesCountCV);
        policiesCountTV = root.findViewById(R.id.policiesCountTV);
        policiesCountProgress = root.findViewById(R.id.policiesCountProgress);

        pref = getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        LoggedUser loggedUser = getLoggedUser(pref);

        welcomeTV.setText(getString(R.string.home_welcome, getString(R.string.app_name), loggedUser.getFullName()));

        loadData(loggedUser.getUserId());

        carsCountCV.setOnClickListener(v -> getActivity().findViewById(R.id.nav_view).findViewById(R.id.nav_cars).performClick());
        policiesCountCV.setOnClickListener(v -> getActivity().findViewById(R.id.nav_view).findViewById(R.id.nav_policies).performClick());

        homeRefresh.setOnRefreshListener(() -> {
            loadData(loggedUser.getUserId());
            homeRefresh.setRefreshing(false);
        });

        return root;
    }

    private void loadData(String userId) {

        carsCountProgress.setVisibility(View.VISIBLE);
        carsCountTV.setVisibility(View.INVISIBLE);

        policiesCountProgress.setVisibility(View.VISIBLE);
        policiesCountTV.setVisibility(View.INVISIBLE);

        Retrofit retrofit = getRetrofit();
        CarsApi carsApi = retrofit.create(CarsApi.class);
        PoliciesApi policiesApi = retrofit.create(PoliciesApi.class);

        carsApi.getCarsCountByUserId(userId).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NotNull Call<Long> call, @NotNull Response<Long> response) {

                if (response.code() == 404) {
                    carsCountTV.setVisibility(View.VISIBLE);
                    return;
                }

                carsCountProgress.setVisibility(View.GONE);
                carsCountTV.setVisibility(View.VISIBLE);
                carsCountTV.setText(Long.toString(response.body()));
            }

            @Override
            public void onFailure(@NotNull Call<Long> call, @NotNull Throwable t) {

            }
        });

        policiesApi.getPoliciesCountByUserId(userId).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NotNull Call<Long> call, @NotNull Response<Long> response) {

                if (response.code() == 404) {
                    policiesCountTV.setVisibility(View.VISIBLE);
                    return;
                }

                policiesCountProgress.setVisibility(View.GONE);
                policiesCountTV.setVisibility(View.VISIBLE);
                policiesCountTV.setText(Long.toString(response.body()));
            }

            @Override
            public void onFailure(@NotNull Call<Long> call, @NotNull Throwable t) {

            }
        });
    }
}