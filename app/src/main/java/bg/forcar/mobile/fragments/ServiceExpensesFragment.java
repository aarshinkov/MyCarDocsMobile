package bg.forcar.mobile.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import bg.forcar.mobile.R;

import bg.forcar.mobile.activities.service.ServiceExpenseCreateActivity;
import bg.forcar.mobile.collections.ServiceExpensesCollection;
import bg.forcar.mobile.adapters.ServiceExpenseAdapter;
import bg.forcar.mobile.api.CarsApi;
import bg.forcar.mobile.api.ExpensesApi;
import bg.forcar.mobile.responses.expenses.service.ServiceExpense;
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
import static bg.forcar.mobile.api.Api.getRetrofit;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_NAME;
import static bg.forcar.mobile.utils.Utils.getLoggedUser;

public class ServiceExpensesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ServiceExpenseAdapter serviceExpensesAdapter;
    private List<ServiceExpense> serviceExpenses;

    private SwipeRefreshLayout serviceExpensesNoItemsRefresh;
    private SwipeRefreshLayout serviceExpensesRefresh;
    private CircularProgressIndicator serviceExpensesScrollProgress;

    private FloatingActionButton serviceExpenseCreateBtn;

    private SharedPreferences pref;
    private LoggedUser loggedUser;

    private Long currentPage;
    private Long totalPages;

    private CircularProgressIndicator serviceExpensesProgress;

    private Integer page = 1;
    private final Integer startLimit = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_service_expenses, container, false);

        pref = getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        recyclerView = root.findViewById(R.id.serviceExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        serviceExpenses = new ArrayList<>();
        serviceExpensesAdapter = new ServiceExpenseAdapter(requireContext(), serviceExpenses);
        recyclerView.setAdapter(serviceExpensesAdapter);

        serviceExpensesNoItemsRefresh = root.findViewById(R.id.serviceExpensesNoItemsRefresh);
        serviceExpensesRefresh = root.findViewById(R.id.serviceExpensesRefresh);

        serviceExpensesScrollProgress = root.findViewById(R.id.serviceExpensesScrollProgress);

        serviceExpenseCreateBtn = root.findViewById(R.id.serviceExpenseCreateBtn);

        serviceExpensesProgress = root.findViewById(R.id.serviceExpensesProgress);
        serviceExpensesProgress.setVisibility(View.VISIBLE);

        getServiceExpenses(page, startLimit);

        serviceExpensesNoItemsRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getServiceExpenses(page, startLimit);
                serviceExpensesNoItemsRefresh.setRefreshing(false);
            }
        });

        serviceExpensesRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getServiceExpenses(page, startLimit);
                serviceExpensesRefresh.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (page < totalPages) {
                        serviceExpensesScrollProgress.setVisibility(View.VISIBLE);
                        page++;
                        getServiceExpenses(page, startLimit);
                    }
                }
            }
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
                    serviceExpenseCreateBtn.setVisibility(View.VISIBLE);
                } else {
                    serviceExpenseCreateBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Boolean> call, @NotNull Throwable t) {

            }
        });

        serviceExpenseCreateBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ServiceExpenseCreateActivity.class);
            startActivity(intent);
        });

        return root;
    }

    private void getServiceExpenses(Integer page, Integer limit) {
        Retrofit retrofit = getRetrofit();
        ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);

        expensesApi.getServiceExpensesForUser(page, limit, loggedUser.getUserId(), loggedUser.getAuthorization()).enqueue(new Callback<ServiceExpensesCollection>() {
            @Override
            public void onResponse(@NotNull Call<ServiceExpensesCollection> call, @NotNull Response<ServiceExpensesCollection> response) {

                if (!response.isSuccessful()) {
                    serviceExpensesProgress.setVisibility(View.GONE);
                    return;
                }

                ServiceExpensesCollection storedServiceExpenses = response.body();

                if (page == 1) {
                    serviceExpenses.clear();
                }

                if (storedServiceExpenses != null) {
                    serviceExpenses.addAll(storedServiceExpenses.getData());

                    Long serviceExpensesCount = storedServiceExpenses.getPage().getGlobalTotalElements();
                    if (serviceExpensesCount <= 0) {
                        serviceExpensesNoItemsRefresh.setVisibility(View.VISIBLE);
                        serviceExpensesRefresh.setVisibility(View.INVISIBLE);
                    } else {
                        serviceExpensesNoItemsRefresh.setVisibility(View.INVISIBLE);
                        serviceExpensesRefresh.setVisibility(View.VISIBLE);
                    }
                }

                totalPages = storedServiceExpenses.getPage().getTotalPages();

                serviceExpensesAdapter.notifyDataSetChanged();
                serviceExpensesProgress.setVisibility(View.GONE);
                serviceExpensesScrollProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<ServiceExpensesCollection> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getServiceExpenses(1, startLimit);
    }
}
