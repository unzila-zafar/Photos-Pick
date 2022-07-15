package com.androidinnovations.photosview

import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.androidinnovations.photosview.databinding.ActivityImagesViewBinding
import com.androidinnovations.photosview.model.ImagesModel
import com.androidinnovations.photosview.util.BackgroundNotificationService
import com.androidinnovations.photosview.util.Util
import com.androidinnovations.photosview.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ozoneddigital.adamJee.generics.GenericAdapter

class ImagesLargeViewActivity: AppCompatActivity() {

    lateinit var viewModel: MainViewModel
    private var viewOfLayout: ActivityImagesViewBinding? = null
    lateinit var imagesAdapter: GenericAdapter<ImagesModel.Hits>
    private var page: Int = 1
    private var mInterstitialAd: InterstitialAd? = null
    var imageUrl: String? = null


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.parseColor("#AD1457"))
        }
        setContentView(R.layout.activity_images_view)

        viewOfLayout = DataBindingUtil.setContentView<ActivityImagesViewBinding>(this, R.layout.activity_images_view)
        imageUrl = intent.getStringExtra("url")

        loadInterstetialAds()
        loadBannerAd()





         Glide.with(this)
            .load(imageUrl)
            .placeholder(ContextCompat.getDrawable(this, R.drawable.empty))
            .thumbnail(1f)
            .into(viewOfLayout?.myZoomageView!!)

        viewOfLayout?.imageDownload!!.setOnClickListener {
            val intent = Intent(this@ImagesLargeViewActivity, BackgroundNotificationService::class.java)
            intent.putExtra("url", imageUrl)
            startService(intent)
        }

        viewOfLayout?.imageWallpaper!!.setOnClickListener {
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this@ImagesLargeViewActivity)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }

            setWallpaper()

           // showPopupMenu(viewOfLayout?.imageMenu!!)
        }

        viewOfLayout?.imageBack!!.setOnClickListener {
            finish()
        }

    }


//    fun showPopupMenu(view: View) {
//        PopupMenu(view.context, view).apply {
//            menuInflater.inflate(R.menu.image_menu, menu)
//            setOnMenuItemClickListener { item ->
//                when(item.title)
//                {
//                    "Download" ->
//                    {
//
//                    }
//                    "Set as Wallpaper" ->
//                    {
//
//                    }
//                }
//                true
//            }
//        }.show()
//    }

    fun setWallpaper() {

        Glide.with(this@ImagesLargeViewActivity)
            .asBitmap().load(imageUrl)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    runOnUiThread {
                        Util.showToast(this@ImagesLargeViewActivity, "Setting Wallpaper Failed")
                    }

                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    val wallpaperManager = WallpaperManager.getInstance(this@ImagesLargeViewActivity)
                    try {
                        resource?.let {
                            wallpaperManager.setBitmap(it)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    runOnUiThread {
                        Util.showToast(this@ImagesLargeViewActivity, "Wallpaper set Successfully")

                    }
                    return false
                }
            }).submit()


    }

    private fun loadBannerAd() {
        var mAdView: AdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }



    private fun loadInterstetialAds() {
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            getString(R.string.interstetialAd_key),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("interstetialAds", adError?.toString())
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("interstetialAds", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }

            })


        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.

                Log.d("Ads", "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d("Ads", "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }


            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d("Ads", "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d("Ads", "Ad showed fullscreen content.")
                mInterstitialAd = null

            }
        }

    }

    override fun onResume() {
        super.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}