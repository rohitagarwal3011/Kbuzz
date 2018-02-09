package com.app.rohit.campk12_videochat.Services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rohit on 11/1/18.
 */

public class FirebaseClient {

    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl("http://plethron.pythonanywhere.com/eagle/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    public static FirebaseServices getApiService() {
        return getRetrofitInstance().create(FirebaseServices.class);
    }
}
