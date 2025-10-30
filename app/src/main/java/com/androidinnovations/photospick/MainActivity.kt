package com.androidinnovations.photospick

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.androidinnovations.photospick.fragment.CategoriesFragment
import com.androidinnovations.photospick.fragment.ImagesFragment
import com.androidinnovations.photospick.retrofit.MainRepository
import com.androidinnovations.photospick.retrofit.RetrofitService
import com.androidinnovations.photospick.viewmodel.MainViewModel
import com.androidinnovations.photospick.viewmodel.MyViewModelFactory
import com.androidinnovations.photosview.databinding.ActivityMainBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.androidinnovations.photosview.R
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView

//unzila
class MainActivity : AppCompatActivity() {
    private var viewOfLayout: ActivityMainBinding? = null
    private var mInterstitialAd: InterstitialAd? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.parseColor("#AD1457"))
        }
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

        viewOfLayout?.imageSetting!!.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))

        }
        viewOfLayout?.imageBack!!.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val fragments: List<Fragment> = supportFragmentManager.fragments
                for (f: Fragment in fragments) {

                    if (supportFragmentManager.backStackEntryCount > 1) {
                        supportFragmentManager.popBackStack()
                        if (f is ImagesFragment)
                            changeFragment(CategoriesFragment(), false)

                    } else {
                        moveTaskToBack(true)
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }

                }
            }
        })

    }

    fun changeTopBarText(value: String) {
        viewOfLayout?.textViewCategory!!.setText(value)
    }

    fun showTopView(canShow: Boolean) {
        if (canShow.not()) {
            viewOfLayout?.imageSetting!!.visibility = View.GONE
            viewOfLayout?.imageBack!!.visibility = View.VISIBLE
        } else {
            viewOfLayout?.imageSetting!!.visibility = View.VISIBLE
            viewOfLayout?.imageBack!!.visibility = View.GONE
        }
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
            getString(R.string.interstetialAd_key),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError.toString()?.let { Log.d("interstetialAds", it) }
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
        adLoader = AdLoader.Builder(
            this,
            getString(R.string.nativeAd_key)
        ) //"ca-app-pub-3940256099942544/2247696110"
            .forNativeAd { ad: NativeAd ->
                // Show the ad.
                Log.v("native ", "loaded")

                if (isDestroyed) {
                    ad.destroy()
                    return@forNativeAd
                }

                val adView = layoutInflater.inflate(R.layout.native_ad_layout, null) as NativeAdView
                val parent = findViewById<FrameLayout>(R.id.container_nativead)

                displayNativeAd(adView, ad)

                // Ensure that the parent view doesn't already contain an ad view.
                parent.removeAllViews()

                // Place the AdView into the parent.
                parent.addView(adView)
                parent.visibility = View.VISIBLE

            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                    Log.v("native ", "loading error")
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun displayNativeAd(adView: NativeAdView, ad: NativeAd) {
        // Locate the view that will hold the headline, set its text, and use the
        // NativeAdView's headlineView property to register it.
        val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
        headlineView.text = ad.headline
        adView.headlineView = headlineView

        // Repeat the above process for the other assets in the NativeAd using
        // additional view objects (Buttons, ImageViews, etc).

        val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
        adView.mediaView = mediaView

        val cancelAd = adView.findViewById<ImageView>(R.id.ad_cancel_icon)
        cancelAd.setOnClickListener {
            val parent = findViewById<FrameLayout>(R.id.container_nativead)
            parent.visibility = View.GONE

        }

        // Call the NativeAdView's setNativeAd method to register the
        // NativeAdObject.
        adView.setNativeAd(ad)
    }
}