package com.atanasvasil.mobile.mycardocs.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.API_URL;

public final class Api {

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }
}
