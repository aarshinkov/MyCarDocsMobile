package com.atanasvasil.mobile.mycardocs.api;

import com.atanasvasil.mobile.mycardocs.requests.policies.PolicyCreateRequest;
import com.atanasvasil.mobile.mycardocs.requests.policies.PolicyUpdateRequest;
import com.atanasvasil.mobile.mycardocs.responses.policies.Policy;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PoliciesApi {

    @GET("api/policies")
    Call<List<Policy>> getPolicies(@Header("Authorization") String authorization);

    @GET("api/policies/{policyId}")
    Call<Policy> getPolicy(@Path("policyId") String policyId, @Header("Authorization") String authorization);

    @GET("api/policies/user/id/{userId}")
    Call<List<Policy>> getPoliciesByUserId(@Path("userId") String userId, @Header("Authorization") String authorization);

    @POST("api/policies")
    Call<Policy> createPolicy(@Body PolicyCreateRequest pcr, @Header("Authorization") String authorization);

    @PUT("api/policies/{policyId}")
    Call<Policy> updatePolicy(@Path("policyId") String policyId, @Body PolicyUpdateRequest pur, @Header("Authorization") String authorization);

    @DELETE("api/policies/{policyId}")
    Call<Boolean> deletePolicy(@Path("policyId") String policyId, @Header("Authorization") String authorization);

    @GET("api/policies/count/{userId}")
    Call<Long> getPoliciesCountByUserId(@Path("userId") String userId, @Header("Authorization") String authorization);
}
