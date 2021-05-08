package com.atanasvasil.mobile.mycardocs.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.fuel.FuelExpenseCreateActivity;
import com.atanasvasil.mobile.mycardocs.adapters.FuelExpenseAdapter;
import com.atanasvasil.mobile.mycardocs.api.ExpensesApi;
import com.atanasvasil.mobile.mycardocs.collections.FuelExpensesCollection;
import com.atanasvasil.mobile.mycardocs.responses.expenses.fuel.FuelExpense;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;
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

public class FuelExpensesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FuelExpenseAdapter fuelExpensesAdapter;
    private List<FuelExpense> fuelExpenses;

    private SwipeRefreshLayout fuelExpensesNoItemsRefresh;
    private SwipeRefreshLayout fuelExpensesRefresh;

    private FloatingActionButton fuelExpenseCreateBtn;

    private SharedPreferences pref;
    private LoggedUser loggedUser;

    private Long currentPage;
    private Long totalPages;

    private CircularProgressIndicator fuelExpensesProgress;

    private Integer page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_fuel_expenses, container, false);

        pref = getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        recyclerView = root.findViewById(R.id.fuelExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuelExpenses = new ArrayList<>();
        fuelExpensesAdapter = new FuelExpenseAdapter(getContext(), fuelExpenses);
        recyclerView.setAdapter(fuelExpensesAdapter);

        fuelExpensesNoItemsRefresh = root.findViewById(R.id.fuelExpensesNoItemsRefresh);
        fuelExpensesRefresh = root.findViewById(R.id.fuelExpensesRefresh);

        fuelExpenseCreateBtn = root.findViewById(R.id.fuelExpenseCreateBtn);

        fuelExpensesProgress = root.findViewById(R.id.fuelExpensesProgress);
        fuelExpensesProgress.setVisibility(View.VISIBLE);

        getFuelExpenses(page, 50);

        fuelExpensesNoItemsRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getFuelExpenses(page, 50);
                fuelExpensesNoItemsRefresh.setRefreshing(false);
            }
        });

        fuelExpensesRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getFuelExpenses(page, 50);
                fuelExpensesRefresh.setRefreshing(false);
            }
        });

        fuelExpenseCreateBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FuelExpenseCreateActivity.class);
            startActivity(intent);
        });

        return root;
    }

    private void getFuelExpenses(Integer page, Integer limit) {
        Retrofit retrofit = getRetrofit();
        ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);

        expensesApi.getFuelExpensesForUser(page, limit, loggedUser.getUserId(), loggedUser.getAuthorization()).enqueue(new Callback<FuelExpensesCollection>() {
            @Override
            public void onResponse(@NotNull Call<FuelExpensesCollection> call, @NotNull Response<FuelExpensesCollection> response) {

                if (!response.isSuccessful()) {
                    fuelExpensesProgress.setVisibility(View.GONE);
                    return;
                }

                FuelExpensesCollection storedFuelExpenses = response.body();

                if (page == 1) {
                    fuelExpenses.clear();
                }


                if (storedFuelExpenses != null) {
                    fuelExpenses.addAll(storedFuelExpenses.getData());

                    Long sightsCount = storedFuelExpenses.getPage().getGlobalTotalElements();
                    if (sightsCount <= 0) {
                        fuelExpensesNoItemsRefresh.setVisibility(View.VISIBLE);
                        fuelExpensesRefresh.setVisibility(View.INVISIBLE);
                    } else {
                        fuelExpensesNoItemsRefresh.setVisibility(View.INVISIBLE);
                        fuelExpensesRefresh.setVisibility(View.VISIBLE);
                    }
                }

                totalPages = storedFuelExpenses.getPage().getTotalPages();

                fuelExpensesAdapter.notifyDataSetChanged();
                fuelExpensesProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<FuelExpensesCollection> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getFuelExpenses(1, 6);
    }
}