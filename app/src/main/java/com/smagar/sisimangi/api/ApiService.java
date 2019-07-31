package com.smagar.sisimangi.api;

import com.smagar.sisimangi.model.ResponseData;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Gustiawan on 1/20/2019.
 */

public interface ApiService {
    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseData> login(@Field("username") String username,
                             @Field("password") String password);
    @FormUrlEncoded
    @POST("absensi.php")
    Call<ResponseData> absen(@Field("absent") String absent,
                             @Field("chiper") String chiper,
                             @Field("nip") String nip,
                             @Field("status") String status);
}
