package com.androidinnovations.photosview

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.androidinnovations.photosview.databinding.ActivityImagesViewBinding
import com.androidinnovations.photosview.model.ImagesModel
import com.androidinnovations.photosview.viewmodel.MainViewModel
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.ozoneddigital.adamJee.generics.GenericAdapter

class ImagesLargeViewActivity: AppCompatActivity() {

    lateinit var viewModel: MainViewModel
    private var viewOfLayout: ActivityImagesViewBinding? = null
    lateinit var imagesAdapter: GenericAdapter<ImagesModel.Hits>
    private var page: Int = 1
    private var mInterstitialAd: InterstitialAd? = null
    var selectedCategory: String? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_view)

        viewOfLayout = DataBindingUtil.setContentView<ActivityImagesViewBinding>(this, R.layout.activity_images_view)

    }




    override fun onResume() {
        super.onResume()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStop() {
        super.onStop()
        mInterstitialAd = null
        imagesAdapter.clear()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}