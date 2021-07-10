package bg.forcar.mobile.api;

import bg.forcar.mobile.requests.cars.CarCreateRequest;
import bg.forcar.mobile.requests.cars.CarUpdateRequest;
import bg.forcar.mobile.responses.cars.Car;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CarsApi {

    @GET("api/cars/{carId}")
    Call<Car> getCar(@Path("carId") String carId, @Header("Authorization") String authorization);

    @GET("api/cars/license/{licensePlate}")
    Call<Car> getCarByLicensePlate(@Path("licensePlate") String licensePlate, @Header("Authorization") String authorization);

    @GET("api/cars/user/{userId}")
    Call<List<Car>> getUserCars(@Path("userId") String userId, @Header("Authorization") String authorization);

    @POST("api/cars")
    Call<Car> createCar(@Body CarCreateRequest ccr, @Header("Authorization") String authorization);

    @PUT("api/cars/{carId}")
    Call<Car> updateCar(@Path("carId") String carId, @Body CarUpdateRequest cur, @Header("Authorization") String authorization);

    @DELETE("api/cars/{carId}")
    Call<Boolean> deleteCar(@Path("carId") String carId, @Header("Authorization") String authorization);

    @GET("api/cars/count/{userId}")
    Call<Long> getCarsCountByUserId(@Path("userId") String userId, @Header("Authorization") String authorization);

    @GET("api/cars/{userId}/has")
    Call<Boolean> hasUserCars(@Path("userId") String userId, @Header("Authorization") String authorization);
}
