package com.atanasvasil.mobile.mycardocs.fragments;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.ChartActivity;
import com.atanasvasil.mobile.mycardocs.activities.fuel.FuelExpenseCreateActivity;
import com.atanasvasil.mobile.mycardocs.activities.service.ServiceExpenseCreateActivity;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.api.PoliciesApi;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import static com.atanasvasil.mobile.mycardocs.utils.Utils.getWholeNumberFormatted;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout homeRefresh;

    private TextView welcomeTV;
    private CardView carsCountCV;
    private TextView carsCountTV;
    private CircularProgressIndicator carsCountProgress;

    private CardView policiesCountCV;
    private TextView policiesCountTV;
    private CircularProgressIndicator policiesCountProgress;

    private FloatingActionMenu menu;

    private FloatingActionButton fuelExpenseBtn;
    private FloatingActionButton serviceExpenseBtn;

    private LoggedUser loggedUser;
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

        menu = root.findViewById(R.id.menu);

        fuelExpenseBtn = root.findViewById(R.id.fuelExpenseBtn);
        serviceExpenseBtn = root.findViewById(R.id.serviceExpenseBtn);

        pref = requireContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        welcomeTV.setText(Html.fromHtml(getString(R.string.home_welcome, getString(R.string.app_name), loggedUser.getFullName()), 0));

        loadData();

        carsCountCV.setOnClickListener(v -> requireActivity().findViewById(R.id.nav_view).findViewById(R.id.nav_cars).performClick());
        policiesCountCV.setOnClickListener(v -> requireActivity().findViewById(R.id.nav_view).findViewById(R.id.nav_policies).performClick());

        homeRefresh.setOnRefreshListener(() -> {
            loadData();
            homeRefresh.setRefreshing(false);
        });

        Retrofit retrofit = getRetrofit();
        CarsApi carsApi = retrofit.create(CarsApi.class);

        carsApi.hasUserCars(loggedUser.getUserId(), loggedUser.getAuthorization()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NotNull Call<Boolean> call, @NotNull Response<Boolean> response) {

                if (!response.isSuccessful()) {
                    return;
                }

                Boolean hasCars = response.body();

                if (hasCars == null) {
                    return;
                }

                if (hasCars) {
                    menu.setVisibility(View.VISIBLE);
                } else {
                    menu.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Boolean> call, @NotNull Throwable t) {

            }
        });

        fuelExpenseBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FuelExpenseCreateActivity.class);
            startActivity(intent);
        });

        serviceExpenseBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ServiceExpenseCreateActivity.class);
            startActivity(intent);
        });

        return root;
    }

    private final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Toast.makeText(getContext(), "Helllowww", Toast.LENGTH_SHORT).show();
        }
    };

    private void loadData() {

        carsCountProgress.setVisibility(View.VISIBLE);
        carsCountTV.setVisibility(View.INVISIBLE);

        policiesCountProgress.setVisibility(View.VISIBLE);
        policiesCountTV.setVisibility(View.INVISIBLE);

        Retrofit retrofit = getRetrofit();
        CarsApi carsApi = retrofit.create(CarsApi.class);
        PoliciesApi policiesApi = retrofit.create(PoliciesApi.class);

        carsApi.getCarsCountByUserId(loggedUser.getUserId(), loggedUser.getAuthorization()).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NotNull Call<Long> call, @NotNull Response<Long> response) {

                if (response.code() == 404) {
                    carsCountTV.setVisibility(View.VISIBLE);
                    return;
                }

                carsCountProgress.setVisibility(View.GONE);
                carsCountTV.setVisibility(View.VISIBLE);
                carsCountTV.setText(getWholeNumberFormatted(response.body()));
            }

            @Override
            public void onFailure(@NotNull Call<Long> call, @NotNull Throwable t) {

            }
        });

        policiesApi.getPoliciesCountByUserId(loggedUser.getUserId(), loggedUser.getAuthorization()).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NotNull Call<Long> call, @NotNull Response<Long> response) {

                if (response.code() == 404) {
                    policiesCountTV.setVisibility(View.VISIBLE);
                    return;
                }

                policiesCountProgress.setVisibility(View.GONE);
                policiesCountTV.setVisibility(View.VISIBLE);
                policiesCountTV.setText(getWholeNumberFormatted(response.body()));
            }

            @Override
            public void onFailure(@NotNull Call<Long> call, @NotNull Throwable t) {

            }
        });
    }
}