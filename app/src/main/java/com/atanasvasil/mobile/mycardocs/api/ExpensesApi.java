package com.atanasvasil.mobile.mycardocs.api;

import com.atanasvasil.mobile.mycardocs.requests.expenses.fuel.FuelExpenseCreateRequest;
import com.atanasvasil.mobile.mycardocs.responses.expenses.fuel.FuelExpense;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ExpensesApi {

    @POST("api/expenses/fuel")
    Call<FuelExpense> createFuelExpense(@Body FuelExpenseCreateRequest fecr, @Header("Authorization") String authorization);
}
