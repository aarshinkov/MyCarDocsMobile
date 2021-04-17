package com.atanasvasil.mobile.mycardocs.api;

import com.atanasvasil.mobile.mycardocs.responses.users.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UsersApi {

    @GET("api/users")
    Call<List<User>> getUsers();

    /**
     * @param identifier userId or email
     * @return the user if it exists; null - if not
     */
    @GET("api/users/{identifier}")
    Call<User> getUser(@Path("identifier") String identifier);

    @GET("api/users/exists/{email}")
    Call<Boolean> isUserExistByEmail(@Path("email") String email);

    @POST("api/users")
    Call<User> createUser(@Body User user);

    @GET("api/users/{userId}/has/cars")
    Call<Boolean> hasUserCars(@Path("userId") String userId);
}
