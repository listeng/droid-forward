package com.ester.remotetrigger.service

import com.ester.remotetrigger.config.BarkNotifyResult
import com.ester.remotetrigger.config.NotifyResult
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by tls on 2018/1/13.
 */
interface BarkApiService {

    @GET("{key}/{text}/{desp}")
    fun sendNotify(
            @Path("key") key:String,
            @Path("text") text: String,
            @Path("desp") desp: String
    ): Call<BarkNotifyResult>

    companion object Factory {
        fun create(): BarkApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://api.day.app/")
                    .build()


            return retrofit.create(BarkApiService::class.java);
        }
    }
}