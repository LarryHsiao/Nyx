package com.larryhsiao.nyx.account.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

/**
 * The Nyx service apis.
 */
public interface NyxApi {
    static NyxApi client() {
        return new Retrofit.Builder()
            .baseUrl(" https://us-central1-elizabeth-7687c.cloudfunctions.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(NyxApi.class);
    }

    /**
     * @param subReq Subscription data for confirm the subscription.
     */
    @POST("/subscription")
    Call<Void> subscription(@Body SubReq subReq);

    /**
     * @param uid The user UID.
     * @return Current encrypt key MD5 hash of the user.
     */
    @GET("/encryptKey")
    Call<GetEncryptKeyRes> encryptKey(@Query("uid") String uid);

    /**
     * Change current user's encrypt key on Firestore.
     */
    @PUT("/encryptKey")
    Call<Void> changeEncryptKey(@Body ChangeEncryptKeyReq req);
}
