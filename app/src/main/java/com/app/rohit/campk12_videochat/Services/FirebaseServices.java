package com.app.rohit.campk12_videochat.Services;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by rohit on 11/1/18.
 */

public interface FirebaseServices {


    /*User Otp REQUEST*/
    @FormUrlEncoded
    @POST("kbuzz/")
    Call<ResponseBody> sendnotification(@Field("gcm") String gcm, @Field("name") String name);

}
