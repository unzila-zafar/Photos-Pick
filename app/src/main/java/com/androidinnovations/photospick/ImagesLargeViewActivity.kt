package com.androidinnovations.photospick

import android.Manifest
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.androidinnovations.photospick.util.BackgroundNotificationService
import com.androidinnovations.photospick.util.Util
import com.androidinnovations.photosview.R
import com.androidinnovations.photosview.databinding.ActivityImagesViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.core.net.toUri


class ImagesLargeViewActivity : AppCompatActivity() {
    private var viewOfLayout: ActivityImagesViewBinding? = null
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

        viewOfLayout = DataBindingUtil.setContentView<ActivityImagesViewBinding>(
            this,
            R.layout.activity_images_view
        )
        imageUrl = intent.getStringExtra("url")

        loadInterstetialAds()
        loadBannerAd()


        checkPermission()
        Glide.with(this)
            .load(imageUrl)
            .placeholder(ContextCompat.getDrawable(this, R.drawable.empty))
            .thumbnail(1f)
            .into(viewOfLayout?.myZoomageView!!)

        viewOfLayout?.imageDownload!!.setOnClickListener {
            val intent =
                Intent(this@ImagesLargeViewActivity, BackgroundNotificationService::class.java)
            intent.putExtra("url", imageUrl)
            startService(intent)
        }

        viewOfLayout?.imageWallpaper!!.setOnClickListener {
            // Show the ad after 5 seconds (5000 milliseconds)
            Handler(Looper.getMainLooper()).postDelayed({
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(this@ImagesLargeViewActivity)
                } else {
                    Log.d("Ad", "Interstitial ad not ready yet.")
                }
            }, 5000)
            setWallpaper()

            // showPopupMenu(viewOfLayout?.imageMenu!!)
        }

        viewOfLayout?.imageBack!!.setOnClickListener {
            finish()
        }

        viewOfLayout?.imageSharePic!!.setOnClickListener {
            shareImage()
        }

    }

    fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }

            else -> {
                // You can directly ask for the permission.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), 1
                    )
                }
            }
        }
    }
//    fun showPopupMenu() {
//
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

                    val wallpaperManager =
                        WallpaperManager.getInstance(this@ImagesLargeViewActivity)
                    try {
                        resource?.let {
                            wallpaperManager.setBitmap(resource)
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
        val mAdView: AdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }


    private fun loadInterstetialAds() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            getString(R.string.interstetialAd_key),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError.toString().let { Log.d("interstetialAds", it) }
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

    private fun shareImage() {

        Glide.with(this@ImagesLargeViewActivity)
            .asBitmap().load(imageUrl)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    val path: String = MediaStore.Images.Media.insertImage(
                        contentResolver,
                        resource,
                        "Photo",
                        null
                    )

                    if (path != null) {
                        val share = Intent(Intent.ACTION_SEND)
                        share.type = "image/jpeg"
                        share.putExtra(Intent.EXTRA_STREAM, path.toUri())
                        startActivity(Intent.createChooser(share, "Share Image"))
                    }

                    return false
                }
            }).submit()
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap): String? {
        var cachePath: File? = null
        try {
            cachePath = File(getCacheDir(), "images")
            cachePath.mkdirs() // don't forget to make the directory
            val stream =
                FileOutputStream("$cachePath/image.png") // overwrites this image every time
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return cachePath!!.absolutePath
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}