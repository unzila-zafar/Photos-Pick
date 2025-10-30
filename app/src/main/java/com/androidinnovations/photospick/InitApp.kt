package com.androidinnovations.photospick

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.androidinnovations.photospick.viewmodel.MainViewModel
import com.google.android.gms.ads.MobileAds
import java.security.Security
import com.pixplicity.easyprefs.library.Prefs
import org.bouncycastle.jce.provider.BouncyCastleProvider


class InitApp : Application() {
    companion object {
        const val ApiKey = "28200790-994ee58bd6dd493ead5874abc" // api key for getting images
        lateinit var viewModel: MainViewModel
        const val PROGRESS_UPDATE = "progress_update"

        @JvmStatic
        fun get(context: Context): InitApp {
            return context.applicationContext as InitApp
        }
    }
    override fun onCreate() {
        super.onCreate()
        Security.addProvider(BouncyCastleProvider())

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        adjustFontScale(resources.configuration)
        MobileAds.initialize(this) {} // initialize mobile ads

        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

    }
    fun adjustFontScale(configuration: Configuration) {
        if (configuration.fontScale != 1f) {
            Log.d("fontScale=", "" + configuration.fontScale)
            configuration.fontScale = 1f
            Log.d("fontScale=", "" + configuration.fontScale)
            val metrics = resources.displayMetrics
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            metrics.scaledDensity = configuration.fontScale * metrics.density
            baseContext.resources.updateConfiguration(configuration, metrics)
        }
    }

}

