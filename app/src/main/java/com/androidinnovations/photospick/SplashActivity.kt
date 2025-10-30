package com.androidinnovations.photospick

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.androidinnovations.photosview.R
import com.pixplicity.easyprefs.library.Prefs
import java.util.*
import androidx.core.graphics.toColorInt


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = "#AD1457".toColorInt()
        }
        setContentView(R.layout.activity_splash)

        //loadNativeAds()

        val locale = Locale(Prefs.getString("language", ""))
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )

        Handler().postDelayed(Runnable {

            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            // close splash activity

            finish()

        }, 3000)


    }

    /*  fun updateApp(): Boolean? {
          var checkUpdate: Boolean? = false

          val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(this)
          val appUpdateInfoTask: com.google.android.play.core.tasks.Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo
          var mAlertDialog: Dialog?

          // Checks that the platform will allow the specified type of update.
          appUpdateInfoTask.addOnSuccessListener { result ->
              if (result.updateAvailability() === UpdateAvailability.UPDATE_AVAILABLE) {
    //                requestUpdate(result);
                  checkUpdate = true
                  val binding = DataBindingUtil.inflate<UpdateDialogLayoutBinding>(
                      LayoutInflater.from(this@SplashActivity),
                      com.androidinnovations.photosview.R.layout.update_dialog_layout,
                      null,
                      false
                  )
                  val mDialogView = binding.root

                  val mBuilder = this@SplashActivity.let {
                      androidx.appcompat.app.AlertDialog.Builder(it).setView(mDialogView)
                  }

                  mAlertDialog = mBuilder!!.show()
                  mAlertDialog?.setCancelable(false)
                  mAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                  binding.infoUpdate.setOnClickListener {
                      try {
                          startActivity(
                              Intent(
                                  "android.intent.action.VIEW",
                                  Uri.parse("market://details?id=$packageName")
                              )
                          )
                      } catch (e: ActivityNotFoundException) {
                          startActivity(
                              Intent(
                                  "android.intent.action.VIEW",
                                  Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                              )
                          )
                      }
                  }

                  binding.infoCancel.setOnClickListener {
                      mAlertDialog!!.dismiss()
                  }


              } else {

                  checkUpdate = false
              }
          }

          return checkUpdate!!
      }*/

}