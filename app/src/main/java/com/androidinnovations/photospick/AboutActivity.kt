package com.androidinnovations.photospick

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.androidinnovations.photosview.R
import com.androidinnovations.photosview.databinding.ActivityAboutBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

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

}