package com.androidinnovations.photosview

//import org.conscrypt.Conscrypt
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.androidinnovations.photosview.viewmodel.MainViewModel
import com.google.android.gms.ads.MobileAds
import org.conscrypt.Conscrypt
import java.security.Security


class InitApp : Application() {


    companion object {

        final val ApiKey = "28200790-994ee58bd6dd493ead5874abc" // api key for getting images

        lateinit var viewModel: MainViewModel

        @JvmStatic
        fun get(context: Context): InitApp {
            return context.applicationContext as InitApp
        }
    }


    override fun onCreate() {
        super.onCreate()
        Security.insertProviderAt(Conscrypt.newProvider(), 1);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        adjustFontScale(resources.configuration)

        MobileAds.initialize(this) {} // initialize mobile ads

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

