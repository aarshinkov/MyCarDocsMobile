package com.atanasvasil.mobile.mycardocs.api;

import com.atanasvasil.mobile.mycardocs.responses.users.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UsersApi {

    @GET("api/users")
    Call<List<User>> getUsers();

    @GET("api/users/{userId}")
    Call<User> getUser(@Path("userId") Long userId);
}
