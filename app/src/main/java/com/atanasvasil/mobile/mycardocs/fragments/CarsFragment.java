package com.atanasvasil.mobile.mycardocs.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.cars.CarCreateActivity;
import com.atanasvasil.mobile.mycardocs.adapters.CarAdapter;
import com.atanasvasil.mobile.mycardocs.api.Api;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
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
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.Utils.getLoggedUser;

public class CarsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CarAdapter carAdapter;
    private List<Car> cars;
    private SwipeRefreshLayout carsRefresh;

    private CircularProgressIndicator progress;

    private FloatingActionButton carCreateFBtn;
    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cars, container, false);

        carsRefresh = root.findViewById(R.id.carsRefresh);

        recyclerView = root.findViewById(R.id.cars);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cars = new ArrayList<>();
        carAdapter = new CarAdapter(getContext(), cars);
        recyclerView.setAdapter(carAdapter);

        progress = root.findViewById(R.id.carsProgress);
        progress.setVisibility(View.VISIBLE);

        carCreateFBtn = root.findViewById(R.id.carCreateFBtn);
        carCreateFBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CarCreateActivity.class);
            startActivity(intent);
        });

        pref = getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        User user = getLoggedUser(pref);

        getUserCars(user.getUserId());

        carsRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getUserCars(user.getUserId());
                carsRefresh.setRefreshing(false);
            }
        });

        return root;
    }

    private void getUserCars(Long userId) {

        Retrofit retrofit = Api.getRetrofit();
        CarsApi carsApi = retrofit.create(CarsApi.class);

        carsApi.getUserCars(userId).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {

                if (response.code() == 400) {
                    progress.setVisibility(View.GONE);
                    return;
                }

                List<Car> storedCars = response.body();

                cars.clear();

                if (storedCars != null) {
                    cars.addAll(storedCars);
                }

                carAdapter.notifyDataSetChanged();
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Toast.makeText(getContext(), R.string.error_server, Toast.LENGTH_LONG).show();
                progress.setVisibility(View.GONE);
            }
        });
    }
}