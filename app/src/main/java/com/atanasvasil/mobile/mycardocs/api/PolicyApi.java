package com.atanasvasil.mobile.mycardocs.api;

import com.atanasvasil.mobile.mycardocs.responses.policies.Policy;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PolicyApi {

    @GET("api/policies")
    Call<List<Policy>> getPolicies();

    @GET("api/policies/user/id/{userId}")
    Call<List<Policy>> getPoliciesByUserId(@Path("userId") Long userId);
}
