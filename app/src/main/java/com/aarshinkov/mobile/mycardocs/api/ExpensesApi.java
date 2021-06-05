package com.aarshinkov.mobile.mycardocs.api;

import com.aarshinkov.mobile.mycardocs.collections.FuelExpensesCollection;
import com.aarshinkov.mobile.mycardocs.collections.ServiceExpensesCollection;
import com.aarshinkov.mobile.mycardocs.requests.expenses.fuel.FuelExpenseCreateRequest;
import com.aarshinkov.mobile.mycardocs.requests.expenses.service.ServiceExpenseCreateRequest;
import com.aarshinkov.mobile.mycardocs.responses.expenses.ExpensesSummaryResponse;
import com.aarshinkov.mobile.mycardocs.responses.expenses.fuel.FuelExpense;
import com.aarshinkov.mobile.mycardocs.responses.expenses.service.ServiceExpense;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ExpensesApi {

    @GET("api/expenses/summary")
    Call<ExpensesSummaryResponse> getExpensesSummary(@Query("userId") String userId, @Query("carId") String carId, @Query("year") Integer year, @Header("Authorization") String authorization);

    // FUEL
    @GET("api/expenses/fuel")
    Call<FuelExpensesCollection> getFuelExpensesForUser(@Query("page") Integer page, @Query("limit") Integer limit, @Query("userId") String userId, @Header("Authorization") String authorization);

    @GET("api/expenses/fuel/{fuelExpenseId}")
    Call<FuelExpense> getFuelExpense(@Path("fuelExpenseId") String fuelExpenseId, @Header("Authorization") String authorization);

    @POST("api/expenses/fuel")
    Call<FuelExpense> createFuelExpense(@Body FuelExpenseCreateRequest fecr, @Query("userId") String userId, @Header("Authorization") String authorization);

    // SERVICE
    @GET("api/expenses/service")
    Call<ServiceExpensesCollection> getServiceExpensesForUser(@Query("page") Integer page, @Query("limit") Integer limit, @Query("userId") String userId, @Header("Authorization") String authorization);

    @GET("api/expenses/service/{serviceExpenseId}")
    Call<ServiceExpense> getServiceExpense(@Path("serviceExpenseId") String serviceExpenseId, @Header("Authorization") String authorization);

    @POST("api/expenses/service")
    Call<ServiceExpense> createServiceExpense(@Body ServiceExpenseCreateRequest secr, @Query("userId") String userId, @Header("Authorization") String authorization);
}
