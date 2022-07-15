package com.androidinnovations.photosview.retrofit

import com.androidinnovations.photosview.model.ImagesModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap
import java.util.concurrent.TimeUnit

//unzila
interface RetrofitService {


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