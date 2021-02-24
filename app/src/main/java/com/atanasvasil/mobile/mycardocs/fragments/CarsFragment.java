package com.atanasvasil.mobile.mycardocs.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.cars.CarCreateActivity;
import com.atanasvasil.mobile.mycardocs.adapters.CarAdapter;
import com.atanasvasil.mobile.mycardocs.api.Api;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_USER_ID;

public class CarsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CarAdapter carAdapter;
    private List<Car> cars;
    private ProgressDialog loadingDialog;

    private FloatingActionButton carCreateFBtn;
    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cars, container, false);

        recyclerView = root.findViewById(R.id.cars);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cars = new ArrayList<>();
        carAdapter = new CarAdapter(getContext(), cars);
        recyclerView.setAdapter(carAdapter);

        loadingDialog = new ProgressDialog(getContext());
        loadingDialog.setMessage("Loading cars...");
        loadingDialog.show();

        carCreateFBtn = root.findViewById(R.id.carCreateFBtn);
        carCreateFBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CarCreateActivity.class);
            startActivity(intent);
        });

        Retrofit retrofit = Api.getRetrofit();
        CarsApi carsApi = retrofit.create(CarsApi.class);

        pref = getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        Long userId = pref.getLong(SHARED_PREF_USER_ID, 0);

        carsApi.getUserCars(userId).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {

                if (response.code() == 400) {
                    //EMPTY car list
                    loadingDialog.hide();
                    return;
                }

                List<Car> storedCars = response.body();

                if (storedCars != null) {
                    cars.addAll(storedCars);
                    carAdapter.notifyDataSetChanged();
                    loadingDialog.hide();
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Toast.makeText(getContext(), "Error communicating with the server. Try again!", Toast.LENGTH_LONG).show();
                loadingDialog.hide();
            }
        });

        return root;
    }
}