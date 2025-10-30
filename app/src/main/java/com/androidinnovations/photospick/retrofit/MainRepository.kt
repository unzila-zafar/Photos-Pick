package com.androidinnovations.photospick.retrofit

class MainRepository constructor(private val retrofitService: RetrofitService) {
    fun getAllPictures(query: Map<String, String>) = retrofitService.getAllPictures(query)
}