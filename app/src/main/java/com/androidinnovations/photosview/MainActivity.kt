package com.androidinnovations.photosview

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.androidinnovations.photosview.databinding.ActivityMainBinding
import com.androidinnovations.photosview.fragment.CategoriesFragment
import com.androidinnovations.photosview.fragment.ImagesFragment
import com.androidinnovations.photosview.model.CategoriesModel
import com.androidinnovations.photosview.retrofit.MainRepository
import com.androidinnovations.photosview.retrofit.RetrofitService
import com.androidinnovations.photosview.viewmodel.MainViewModel
import com.androidinnovations.photosview.viewmodel.MyViewModelFactory
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.ozoneddigital.adamJee.generics.GenericAdapter

//unzila
class MainActivity : AppCompatActivity() {


    lateinit var categoriesAdapter: GenericAdapter<CategoriesModel>
    private var viewOfLayout: ActivityMainBinding? = null
    private var mInterstitialAd: InterstitialAd? = null


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_main)


//        // Set up an OnPreDrawListener to the root view.
//        val content: View = findViewById(android.R.id.content)
//        content.viewTreeObserver.addOnPreDrawListener(
//            object : ViewTreeObserver.OnPreDrawListener {
//                override fun onPreDraw(): Boolean {
//                    // Check if the initial data is ready.
//                    return if (viewModel) {
//                        // The content is ready; start drawing.
//                        content.viewTreeObserver.removeOnPreDrawListener(this)
//                        true
//                    } else {
//                        // The content is not ready; suspend.
//                        false
//                    }
//                }
//            }
//        )

        viewOfLayout =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        InitApp.viewModel = ViewModelProvider(
            this, MyViewModelFactory(
                MainRepository(
                    RetrofitService.getInstance()
                )
            )
        ).get(MainViewModel::class.java)

        loadNativeAds()
        loadBannerAd()
        loadInterstetialAds()


        changeFragment(CategoriesFragment(), false)

    }

    fun changeFragment(nextFragment: Fragment, removeBackStack: Boolean) {
        if (removeBackStack) {
            val fm = supportFragmentManager
            for (i in 0 until fm.backStackEntryCount) {
                fm.popBackStack()
            }
        }


        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            .replace(R.id.container_mainScreen, nextFragment)
            .addToBackStack(null)
            .commit()
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
            "ca-app-pub-3940256099942544/1033173712",
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

    lateinit var adLoader: AdLoader
    private fun loadNativeAds() {
        adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                // Show the ad.
                if (isDestroyed) {
                    ad.destroy()
                    return@forNativeAd
                }

            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()

        adLoader.loadAds(AdRequest.Builder().build(), 3)
    }


    override fun onBackPressed() {

        val fragments: List<Fragment> = supportFragmentManager.fragments
        for (f: Fragment in fragments) {

            if (supportFragmentManager.backStackEntryCount > 1) {
                supportFragmentManager.popBackStack()
                if(f is ImagesFragment)
                    changeFragment(CategoriesFragment(), false)

            } else {
                super.onBackPressed()

            }

        }
    }

}