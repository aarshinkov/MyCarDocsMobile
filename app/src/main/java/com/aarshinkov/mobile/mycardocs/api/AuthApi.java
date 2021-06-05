package com.aarshinkov.mobile.mycardocs.api;

import com.aarshinkov.mobile.mycardocs.responses.AuthenticationResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {

    @POST("api/login")
    Call<AuthenticationResponse> login(@Query("email") String email, @Query("password") String password);
}
