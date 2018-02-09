package com.app.rohit.campk12_videochat.Services;




import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {


    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl("https://api.opentok.com/session/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    public static ApiServices getApiService() {
        return getRetrofitInstance().create(ApiServices.class);
    }
}
