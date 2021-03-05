package com.atanasvasil.mobile.mycardocs.api;

import com.atanasvasil.mobile.mycardocs.requests.policies.PolicyCreateRequest;
import com.atanasvasil.mobile.mycardocs.requests.policies.PolicyUpdateRequest;
import com.atanasvasil.mobile.mycardocs.responses.policies.Policy;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PolicyApi {

    @GET("api/policies")
    Call<List<Policy>> getPolicies();

    @GET("api/policies/{policyId}")
    Call<Policy> getPolicy(@Path("policyId") String policyId);

    @GET("api/policies/user/id/{userId}")
    Call<List<Policy>> getPoliciesByUserId(@Path("userId") Long userId);

    @POST("api/policies")
    Call<Policy> createPolicy(@Body PolicyCreateRequest pcr);

    @PUT("api/policies")
    Call<Policy> updatePolicy(@Body PolicyUpdateRequest pur);

    @DELETE("api/policies/{policyId}")
    Call<Boolean> deletePolicy(@Path("policyId") String policyId);
}
