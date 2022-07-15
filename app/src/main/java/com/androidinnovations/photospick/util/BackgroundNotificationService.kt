package com.androidinnovations.photosview.util

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.androidinnovations.photosview.BuildConfig
import com.androidinnovations.photosview.InitApp
import com.androidinnovations.photosview.R
import java.io.File
import java.io.IOException

class BackgroundNotificationService : IntentService("Service") {
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    var isPdf = false
    override fun onHandleIntent(intent: Intent?) {
        val url = intent?.getStringExtra("url")
        isPdf = (url?.contains("pdf") == true)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW)
            notificationChannel.description = "no sound"
            notificationChannel.setSound(null, null)
            notificationChannel.enableLights(false)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(false)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
        notificationBuilder = NotificationCompat.Builder(this, "id")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Downloading Image")
            .setDefaults(0)
            .setAutoCancel(true)
        notificationManager!!.notify(0, notificationBuilder!!.build())
        try {
            downloadImage(url!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun downloadImage(url: String) {
        updateNotification(url)
        applicationContext.downloadFile(url) {
            onDownloadComplete(true, it)
        }

    }

    private fun updateNotification(file: String) {
        val name = file.substring(file.lastIndexOf("/") + 1)
        notificationBuilder?.setProgress(100, 50, true);
        notificationBuilder!!.setContentText("Downloading: $name")
        notificationManager!!.notify(0, notificationBuilder!!.build())
    }

    private fun sendProgressUpdate(downloadComplete: Boolean) {
        val intent: Intent = Intent(InitApp.PROGRESS_UPDATE)
        intent.putExtra("downloadComplete", downloadComplete)
        LocalBroadcastManager.getInstance(this@BackgroundNotificationService).sendBroadcast(intent)
    }

    private fun onDownloadComplete(downloadComplete: Boolean, file: File?) {
        sendProgressUpdate(downloadComplete)
        val intent = Intent(Intent.ACTION_VIEW)
        file?.let {
            val data: Uri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
            intent.setDataAndType(
                data,
                if (isPdf) "application/pdf" else "application/vnd.ms-excel"
            )
        }
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.getActivity(
                this,
                0 /* Request code */,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE
            )
        else
            PendingIntent.getActivity(
                this,
                0 /* Request code */,
                intent,
                PendingIntent.FLAG_ONE_SHOT)
        notificationManager!!.cancel(0)
        notificationBuilder!!.setSmallIcon(R.mipmap.ic_launcher)
        notificationBuilder!!.setProgress(0, 0, false)
        notificationBuilder!!.setContentText("File Download Complete")
        notificationBuilder!!.setContentIntent(pendingIntent)
        notificationManager!!.notify(0, notificationBuilder!!.build())
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        notificationManager!!.cancel(0)
    }
}