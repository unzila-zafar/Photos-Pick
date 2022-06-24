package com.androidinnovations.mehndidesigns.retrofit

import com.androidinnovations.mehndidesigns.model.ImagesModel
import com.androidinnovations.mehndidesigns.viewmodel.Images
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.util.concurrent.TimeUnit

interface RetrofitService {

//    @GET(".")
//    fun getAllPictures(@Query("key") key: String) : Call<List<ImagesModel>>
    @GET(".")
    fun getAllPictures(@QueryMap paramsMap: Map<String, String> ) : Call<ImagesModel>

    companion object {
        var retrofitService: RetrofitService? = null

        fun getInstance() : RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://pixabay.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = OkHttpClient.Builder().apply {

            this.addInterceptor { chain ->
                return@addInterceptor chain.proceed(chain.request().newBuilder().build())
            }

            this.addInterceptor(interceptor)
            this.connectTimeout(30, TimeUnit.SECONDS)
            this.readTimeout(120, TimeUnit.SECONDS)

        }.build()

    }



}