package com.app.rohit.campk12_videochat.Services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by rohit on 10/1/18.
 */

public interface ApiServices {



    /*User Otp REQUEST*/
    @FormUrlEncoded
    @POST("create")
    Call<ResponseBody> getsessionid(@Field("p2p.preference") String p2p, @Field("archiveMode") String archive, @Header("Accept") String accept, @Header("X-OPENTOK-AUTH") String auth);

}
