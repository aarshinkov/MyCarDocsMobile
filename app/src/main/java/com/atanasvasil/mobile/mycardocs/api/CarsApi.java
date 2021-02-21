package com.atanasvasil.mobile.mycardocs.api;

import com.atanasvasil.mobile.mycardocs.requests.CarCreateRequest;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CarsApi {

    @GET("api/cars/user/{userId}")
    Call<Car> getUserCars(@Path("userId") Long userId);

    @POST("api/cars")
    Call<Car> createCar(@Body CarCreateRequest ccr);

}
