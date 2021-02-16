package com.ester.remotetrigger.service

import com.ester.remotetrigger.config.NotifyResult
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by tls on 2018/1/13.
 */
interface FTQQApiService {

    @FormUrlEncoded
    @POST("{key}.send")
    fun sendNotify(
            @Path("key") key:String,
            @Field("text") text: String,
            @Field("desp") desp: String
    ): Call<NotifyResult>

    companion object Factory {
        fun create(): FTQQApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://sc.ftqq.com/")
                    .build()


            return retrofit.create(FTQQApiService::class.java);
        }
    }
}