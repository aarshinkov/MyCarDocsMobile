package bg.forcar.mobile.api;

import bg.forcar.mobile.responses.users.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @GET("api/users/password/forgot")
    Call<Boolean> forgotPassword(@Query("email") String email);

    @POST("api/users/password/reset")
    Call<Boolean> resetPassword(@Query("password") String password, @Query("code") String code);
}
