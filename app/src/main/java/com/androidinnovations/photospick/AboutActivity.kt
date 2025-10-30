package com.androidinnovations.photospick

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.androidinnovations.photosview.R
import com.androidinnovations.photosview.databinding.ActivityAboutBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AboutActivity : AppCompatActivity() {


    private var viewOfLayout: ActivityAboutBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#AD1457")
        }
        setContentView(R.layout.activity_about)

        viewOfLayout = DataBindingUtil.setContentView<ActivityAboutBinding>(
            this,
            R.layout.activity_about
        )

        loadBannerAd()
        loadInterstetialAds()

        // Show the ad after 5 seconds (5000 milliseconds)
        Handler(Looper.getMainLooper()).postDelayed({
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this@AboutActivity)
            } else {
                Log.d("Ad", "Interstitial ad not ready yet.")
            }
        }, 5000)

        viewOfLayout?.imageBack!!.setOnClickListener {
            finish()
        }


    }


    private fun loadBannerAd() {
        var mAdView: AdView = findViewById(R.id.adView)
        var mAdView1: AdView = findViewById(R.id.adView1)

        val adRequest = AdRequest.Builder().build()
        mAdView1.loadAd(adRequest)
        mAdView.loadAd(adRequest)
    }

    private var mInterstitialAd: InterstitialAd? = null
    private fun loadInterstetialAds() {
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            getString(R.string.interstetialAd_key),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError?.toString()?.let { Log.d("interstetialAds", it) }
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("interstetialAds", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mInterstitialAd!!.show(this@AboutActivity)
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


}