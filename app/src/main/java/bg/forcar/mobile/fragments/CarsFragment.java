package bg.forcar.mobile.fragments;

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

import bg.forcar.mobile.R;

import bg.forcar.mobile.activities.cars.CarCreateActivity;
import bg.forcar.mobile.adapters.CarAdapter;
import bg.forcar.mobile.api.Api;
import bg.forcar.mobile.api.CarsApi;
import bg.forcar.mobile.responses.cars.Car;
import bg.forcar.mobile.utils.LoggedUser;
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
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_NAME;
import static bg.forcar.mobile.utils.Utils.getLoggedUser;

public class CarsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CarAdapter carAdapter;
    private List<Car> cars;
    private SwipeRefreshLayout carsNoItemsRefresh;
    private SwipeRefreshLayout carsRefresh;

    private CircularProgressIndicator progress;

    private FloatingActionButton carCreateFBtn;
    private SharedPreferences pref;
    private LoggedUser loggedUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cars, container, false);

        carsNoItemsRefresh = root.findViewById(R.id.carsNoItemsRefresh);
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
        loggedUser = getLoggedUser(pref);

        getUserCars();

        carsNoItemsRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getUserCars();
                carsNoItemsRefresh.setRefreshing(false);
            }
        });

        carsRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getUserCars();
                carsRefresh.setRefreshing(false);
            }
        });

        return root;
    }

    private void getUserCars() {

        Retrofit retrofit = Api.getRetrofit();
        CarsApi carsApi = retrofit.create(CarsApi.class);

        carsApi.getUserCars(loggedUser.getUserId(), loggedUser.getAuthorization()).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(@NotNull Call<List<Car>> call, @NotNull Response<List<Car>> response) {

                if (response.code() == 400) {
                    carsNoItemsRefresh.setVisibility(View.VISIBLE);
                    carsRefresh.setVisibility(View.INVISIBLE);
                    progress.setVisibility(View.GONE);
                    return;
                }

                List<Car> storedCars = response.body();

                cars.clear();

                if (storedCars != null) {
                    cars.addAll(storedCars);

                    final int size = storedCars.size();

                    if (size <= 0) {
                        carsNoItemsRefresh.setVisibility(View.VISIBLE);
                        carsRefresh.setVisibility(View.INVISIBLE);
                    } else {
                        carsNoItemsRefresh.setVisibility(View.INVISIBLE);
                        carsRefresh.setVisibility(View.VISIBLE);
                    }
                }

                carAdapter.notifyDataSetChanged();
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<List<Car>> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), R.string.error_server, Toast.LENGTH_LONG).show();
                progress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        getUserCars();
        super.onResume();
    }
}