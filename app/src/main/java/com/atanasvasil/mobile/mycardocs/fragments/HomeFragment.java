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

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.api.PoliciesApi;
import com.atanasvasil.mobile.mycardocs.responses.users.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;
import static com.atanasvasil.mobile.mycardocs.api.Api.getRetrofit;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.Utils.getLoggedUser;

public class HomeFragment extends Fragment {

    private TextView welcomeTV;
    private CardView carsCountCV;
    private CardView policiesCountCV;
    private TextView carsCountTV;
    private TextView policiesCountTV;

    private SharedPreferences pref;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        welcomeTV = root.findViewById(R.id.welcomeTV);
        carsCountCV = root.findViewById(R.id.carsCountCV);
        policiesCountCV = root.findViewById(R.id.policiesCountCV);
        carsCountTV = root.findViewById(R.id.carsCountTV);
        policiesCountTV = root.findViewById(R.id.policiesCountTV);

        pref = getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        User user = getLoggedUser(pref);

        welcomeTV.setText(getString(R.string.home_welcome, getString(R.string.app_name), user.getFullName()));

        Retrofit retrofit = getRetrofit();
        CarsApi carsApi = retrofit.create(CarsApi.class);
        PoliciesApi policiesApi = retrofit.create(PoliciesApi.class);

        carsApi.getCarsCountByUserId(user.getUserId()).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {

                if (response.code() == 404) {
                    return;
                }

                carsCountTV.setText(Long.toString(response.body()));
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {

            }
        });

        policiesApi.getPoliciesCountByUserId(user.getUserId()).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {

                if (response.code() == 404) {
                    return;
                }

                policiesCountTV.setText(Long.toString(response.body()));
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {

            }
        });

        carsCountCV.setOnClickListener(v -> getActivity().findViewById(R.id.nav_view).findViewById(R.id.nav_cars).performClick());
        policiesCountCV.setOnClickListener(v -> getActivity().findViewById(R.id.nav_view).findViewById(R.id.nav_policies).performClick());

        return root;
    }
}