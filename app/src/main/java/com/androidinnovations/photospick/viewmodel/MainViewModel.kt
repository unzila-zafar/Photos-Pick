package com.androidinnovations.photosview.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidinnovations.photosview.InitApp
import com.androidinnovations.photosview.model.ImagesModel
import com.androidinnovations.photosview.retrofit.MainRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//unzila
class MainViewModel constructor(private val repository: MainRepository)  : ViewModel() {

    val imagesList = MutableLiveData<ImagesModel>()
    val errorMessage = MutableLiveData<String>()


    fun getAllPictures(page: Int, category: String, clickCallback: ((List<ImagesModel.Hits>) -> Unit)? = null)
    {

        val paramsMap: MutableMap<String, String> = HashMap()
        paramsMap["key"] = InitApp.ApiKey
        if(category.equals("mehndi"))
            paramsMap["q"] = "mehndi"
        paramsMap["image_type"] = "photo"
        paramsMap["page"] = page.toString()
        paramsMap["category"] = category
        paramsMap["orientation"] = "horizontal"

        val response = repository.getAllPictures(paramsMap)
        response.enqueue(object : Callback<ImagesModel> {
            override fun onResponse(call: Call<ImagesModel>, response: Response<ImagesModel>) {
                imagesList.postValue(response.body())
                response.body()?.hits?.let {
                    clickCallback?.invoke(response.body()?.hits!!)
                }

            }

            override fun onFailure(call: Call<ImagesModel>, t: Throwable) {
                errorMessage.postValue(t.message)
            }

        })
    }
}