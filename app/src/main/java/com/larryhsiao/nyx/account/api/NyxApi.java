package com.larryhsiao.nyx.account.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

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
}
