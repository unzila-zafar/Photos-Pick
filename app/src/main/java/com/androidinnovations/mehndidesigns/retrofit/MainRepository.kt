package com.androidinnovations.mehndidesigns.retrofit

class MainRepository constructor(private val retrofitService: RetrofitService) {
    fun getAllPictures(query: Map<String, String>) = retrofitService.getAllPictures(query)
}