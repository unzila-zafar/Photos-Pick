package com.androidinnovations.mehndidesigns

//import org.conscrypt.Conscrypt
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.androidinnovations.mehndidesigns.retrofit.MainRepository
import com.androidinnovations.mehndidesigns.retrofit.RetrofitService
import com.androidinnovations.mehndidesigns.viewmodel.MainViewModel
import com.androidinnovations.mehndidesigns.viewmodel.MyViewModelFactory


class InitApp : Application() {


    companion object {

        final val ApiKey = "28200790-994ee58bd6dd493ead5874abc"


        @JvmStatic
        fun get(context: Context): InitApp {
            return context.applicationContext as InitApp
        }
    }


    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        adjustFontScale(resources.configuration)
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

