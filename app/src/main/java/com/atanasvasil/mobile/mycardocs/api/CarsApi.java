package com.atanasvasil.mobile.mycardocs.api;

import com.atanasvasil.mobile.mycardocs.requests.cars.CarCreateRequest;
import com.atanasvasil.mobile.mycardocs.requests.cars.CarUpdateRequest;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CarsApi {

    @GET("api/cars/{carId}")
    Call<Car> getCar(@Path("carId") String carId);

    @GET("api/cars/license/{licensePlate}")
    Call<Car> getCarByLicensePlate(@Path("licensePlate") String licensePlate);

    @GET("api/cars/user/{userId}")
    Call<List<Car>> getUserCars(@Path("userId") Long userId);

    @POST("api/cars")
    Call<Car> createCar(@Body CarCreateRequest ccr);

    @PUT("api/cars")
    Call<Car> updateCar(@Body CarUpdateRequest ccr);

    @DELETE("api/cars/{carId}")
    Call<Boolean> deleteCar(@Path("carId") String carId);

    @GET("api/cars/count/{userId}")
    Call<Long> getCarsCountByUserId(@Path("userId") Long userId);
}
