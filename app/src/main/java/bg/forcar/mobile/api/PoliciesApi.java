package bg.forcar.mobile.api;

import bg.forcar.mobile.requests.policies.PolicyCreateRequest;
import bg.forcar.mobile.requests.policies.PolicyUpdateRequest;
import bg.forcar.mobile.responses.policies.Policy;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PoliciesApi {

    @GET("api/policies")
    Call<List<Policy>> getPolicies(@Header("Authorization") String authorization);

    @GET("api/policies/{policyId}")
    Call<Policy> getPolicy(@Path("policyId") String policyId, @Header("Authorization") String authorization);

    @GET("api/policies/user/id/{userId}")
    Call<List<Policy>> getPoliciesByUserId(@Path("userId") String userId, @Header("Authorization") String authorization);

    @GET("api/policies/type")
    Call<List<Policy>> getPoliciesByType(@Query("type") Integer type, @Query("userId") String userId, @Header("Authorization") String authorization);

    @GET("api/policies/status")
    Call<List<Policy>> getPoliciesByStatus(@Query("status") Integer status, @Query("userId") String userId, @Header("Authorization") String authorization);

    @GET("api/policies/filter")
    Call<List<Policy>> getPoliciesByCriteria(@Query("type") Integer type, @Query("status") Integer status, @Query("carId") String carId, @Query("userId") String userId, @Header("Authorization") String authorization);

    @POST("api/policies")
    Call<Policy> createPolicy(@Body PolicyCreateRequest pcr, @Header("Authorization") String authorization);

    @PUT("api/policies/{policyId}")
    Call<Policy> updatePolicy(@Path("policyId") String policyId, @Body PolicyUpdateRequest pur, @Header("Authorization") String authorization);

    @DELETE("api/policies/{policyId}")
    Call<Boolean> deletePolicy(@Path("policyId") String policyId, @Header("Authorization") String authorization);

    @GET("api/policies/count/{userId}")
    Call<Long> getPoliciesCountByUserId(@Path("userId") String userId, @Header("Authorization") String authorization);
}
