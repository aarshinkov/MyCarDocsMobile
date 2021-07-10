package bg.forcar.mobile.api;

import bg.forcar.mobile.utils.AppConstants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class Api {

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder().baseUrl(AppConstants.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }
}
