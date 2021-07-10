package bg.forcar.mobile.api;

import bg.forcar.mobile.responses.AuthenticationResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {

    @POST("api/login")
    Call<AuthenticationResponse> login(@Query("email") String email, @Query("password") String password);
}
