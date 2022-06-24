package com.androidinnovations.mehndidesigns.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidinnovations.mehndidesigns.InitApp
import com.androidinnovations.mehndidesigns.model.ImagesModel
import com.androidinnovations.mehndidesigns.retrofit.MainRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel constructor(private val repository: MainRepository)  : ViewModel() {

    val imagesList = MutableLiveData<ImagesModel>()
    val errorMessage = MutableLiveData<String>()


    fun getAllPictures(page: Int, clickCallback: ((List<ImagesModel.Hits>) -> Unit)? = null)
    {

        val paramsMap: MutableMap<String, String> = HashMap()
        paramsMap["key"] = InitApp.ApiKey
        paramsMap["q"] = "mehndi"
        paramsMap["image_type"] = "photo"
        paramsMap["page"] = page.toString()

        val response = repository.getAllPictures(paramsMap)
        response.enqueue(object : Callback<ImagesModel> {
            override fun onResponse(call: Call<ImagesModel>, response: Response<ImagesModel>) {
                imagesList.postValue(response.body())
                clickCallback?.invoke(response.body()?.hits!!)
            }

            override fun onFailure(call: Call<ImagesModel>, t: Throwable) {
                errorMessage.postValue(t.message)
            }

        })
    }
}