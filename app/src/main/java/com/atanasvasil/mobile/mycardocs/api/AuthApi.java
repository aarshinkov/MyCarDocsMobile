package com.atanasvasil.mobile.mycardocs.api;

import com.atanasvasil.mobile.mycardocs.responses.users.User;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {

    @POST("api/login")
    Call<User> login(@Query("email") String email, @Query("password") String password);
}
