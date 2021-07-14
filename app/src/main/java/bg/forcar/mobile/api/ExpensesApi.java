package bg.forcar.mobile.api;

import bg.forcar.mobile.collections.FuelExpensesCollection;
import bg.forcar.mobile.collections.ServiceExpensesCollection;
import bg.forcar.mobile.requests.expenses.fuel.FuelExpenseCreateRequest;
import bg.forcar.mobile.requests.expenses.fuel.FuelExpenseUpdateRequest;
import bg.forcar.mobile.requests.expenses.service.ServiceExpenseCreateRequest;
import bg.forcar.mobile.requests.policies.PolicyUpdateRequest;
import bg.forcar.mobile.responses.expenses.ExpensesSummaryResponse;
import bg.forcar.mobile.responses.expenses.fuel.FuelExpense;
import bg.forcar.mobile.responses.expenses.service.ServiceExpense;

import bg.forcar.mobile.responses.policies.Policy;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @PUT("api/expenses/fuel/{fuelExpenseId}")
    Call<FuelExpense> updateFuelExpense(@Path("fuelExpenseId") String fuelExpenseId, @Body FuelExpenseUpdateRequest feur, @Header("Authorization") String authorization);

    @DELETE("api/expenses/fuel/{fuelExpenseId}")
    Call<Boolean> deleteFuelExpense(@Path("fuelExpenseId") String fuelExpenseId, @Header("Authorization") String authorization);

    // SERVICE
    @GET("api/expenses/service")
    Call<ServiceExpensesCollection> getServiceExpensesForUser(@Query("page") Integer page, @Query("limit") Integer limit, @Query("userId") String userId, @Header("Authorization") String authorization);

    @GET("api/expenses/service/{serviceExpenseId}")
    Call<ServiceExpense> getServiceExpense(@Path("serviceExpenseId") String serviceExpenseId, @Header("Authorization") String authorization);

    @POST("api/expenses/service")
    Call<ServiceExpense> createServiceExpense(@Body ServiceExpenseCreateRequest secr, @Query("userId") String userId, @Header("Authorization") String authorization);
}
