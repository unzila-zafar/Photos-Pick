package com.androidinnovations.photospick

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import com.androidinnovations.photosview.R
import com.androidinnovations.photosview.databinding.ActivitySettingsBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.pixplicity.easyprefs.library.Prefs
import java.util.*
import androidx.core.graphics.toColorInt
import com.androidinnovations.photosview.BuildConfig
class SettingsActivity : AppCompatActivity() {

    private var viewOfLayout: ActivitySettingsBinding? = null
    private var mInterstitialAd: InterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor("#AD1457".toColorInt())
        }
        setContentView(R.layout.activity_settings)
        viewOfLayout = DataBindingUtil.setContentView<ActivitySettingsBinding>(
            this,
            R.layout.activity_settings
        )

        loadBannerAd()
        loadInterstetialAds()

        viewOfLayout?.settingLanguageBtn!!.setOnClickListener {
            // Show the ad after 5 seconds (5000 milliseconds)
            Handler(Looper.getMainLooper()).postDelayed({
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(this@SettingsActivity)
                } else {
                    Log.d("Ad", "Interstitial ad not ready yet.")
                }
            }, 5000)

            showPopupMenu(viewOfLayout?.settingLanguageBtn!!)
        }

        viewOfLayout?.settingRateBtn!!.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.androidinnovations.photospick")
                )
            )
        }

        viewOfLayout?.settingShareBtn!!.setOnClickListener {
            
            // Show the ad after 5 seconds (5000 milliseconds)
            Handler(Looper.getMainLooper()).postDelayed({
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(this@SettingsActivity)
                } else {
                    Log.d("Ad", "Interstitial ad not ready yet.")
                }
            }, 5000)

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            val sendMessage =
                "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Hey Check out this Great HD Pictures Collection: ${sendMessage} "
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }

        viewOfLayout?.settingAboutBtn?.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))

        }

        viewOfLayout?.imageBack!!.setOnClickListener {
            finish()
        }


    }

    fun showPopupMenu(view: View) {
        PopupMenu(view.context, view).apply {
            menuInflater.inflate(R.menu.image_menu, menu)
            setOnMenuItemClickListener { item ->
                when(item.title)
                {
                    "English" ->
                    {
                        val languageToLoad = "en" // your language

                        val locale = Locale(languageToLoad)
                        Locale.setDefault(locale)
                        val config = Configuration()
                        config.locale = locale
                        baseContext.resources.updateConfiguration(
                            config,
                            baseContext.resources.displayMetrics
                        )
                        Prefs.edit().putString("language", languageToLoad).apply()

                        val refresh = Intent(this@SettingsActivity, MainActivity::class.java)
                        refresh.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(refresh)
                        finish()

                    }
                    "Arabic" ->
                    {
                        val languageToLoad = "ar" // your language

                        val locale = Locale(languageToLoad)
                        Locale.setDefault(locale)
                        val config = Configuration()
                        config.locale = locale
                        baseContext.resources.updateConfiguration(
                            config,
                            baseContext.resources.displayMetrics
                        )
                        Prefs.edit().putString("language", languageToLoad).apply()

                        val refresh = Intent(this@SettingsActivity, MainActivity::class.java)
                        refresh.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(refresh)
                        finish()
                    }
                    "Urdu" ->
                    {
                        val languageToLoad = "ur" // your language

                        val locale = Locale(languageToLoad)
                        Locale.setDefault(locale)
                        val config = Configuration()
                        config.locale = locale
                        baseContext.resources.updateConfiguration(
                            config,
                            baseContext.resources.displayMetrics
                        )
                        Prefs.edit().putString("language", languageToLoad).apply()

                        val refresh = Intent(this@SettingsActivity, MainActivity::class.java)
                        refresh.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(refresh)
                        finish()
                    }
                    "German" ->
                    {
                        val languageToLoad = "gmh" // your language

                        val locale = Locale(languageToLoad)
                        Locale.setDefault(locale)
                        val config = Configuration()
                        config.locale = locale
                        baseContext.resources.updateConfiguration(
                            config,
                            baseContext.resources.displayMetrics
                        )
                        Prefs.edit().putString("language", languageToLoad).apply()

                        val refresh = Intent(this@SettingsActivity, MainActivity::class.java)
                        refresh.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(refresh)
                        finish()
                    }
                }
                true
            }
        }.show()
    }



    private fun loadBannerAd() {
        val mAdView: AdView = findViewById(R.id.adView)
        val mAdView1: AdView = findViewById(R.id.adView1)

        val adRequest = AdRequest.Builder().build()
        mAdView1.loadAd(adRequest)
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
                    mInterstitialAd!!.show(this@SettingsActivity)
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