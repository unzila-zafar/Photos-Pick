package com.androidinnovations.mehndidesigns.util

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.androidinnovations.mehndidesigns.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Util {
    companion object {

        fun isNetworkConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
        }

        fun ImageView.fetchImage(
            url: String?, showFullView: Boolean = false,
            placHolder: Drawable? = null, thumbnail: Float? = null,
            requestOptions: RequestOptions? = null, fitCenter: Boolean = false, file: File? = null
        ) {
            val context = this.context

            val reqOp = requestOptions ?: RequestOptions()
            if (fitCenter)
                reqOp.fitCenter()
            Glide.with(context)
                .load(Uri.decode(url) ?: file)
                .apply(reqOp)

                .thumbnail(thumbnail ?: 1f)
                .into(this)
            if (showFullView) {
                this.setOnClickListener {
                    openLargeScreenView(this.context, this.drawable)
                }
            }
        }

        fun ImageView.fetchImage(file: Uri, showFullView: Boolean = false) {
            val context = this.context

            Glide.with(context)
                .load(file)
//            .apply(reqOp)
                .placeholder(ContextCompat.getDrawable(context, R.drawable.empty))
                .thumbnail(1f)
                .into(this)
            if (showFullView) {
                this.setOnClickListener {
                    openLargeScreenView(this.context, this.drawable)
                }
            }
        }

        fun openLargeScreenView(
            context: Context,
            drawable: Drawable?,
            list: ArrayList<String>? = null,
            position: Int = 0
        ) {
            val dialoge = Dialog(context)
            val imageView = ImageView(context)
            var selectedIndex = position
            if (drawable != null) {
                imageView.setImageDrawable(drawable)
            } else {
                Glide.with(context).load(list?.get(selectedIndex)).into(imageView)
            }
            dialoge.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialoge.setContentView(imageView)
            dialoge.show()
            dialoge.window?.decorView?.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            dialoge.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            OnSwipeTouchListener(context, imageView, object : OnSwipeTouchListener.onSwipeListener {
                override fun swipeTop() {
                    dialoge.dismiss()
                }

                override fun swipeBottom() {
                    dialoge.dismiss()
                }

                override fun swipeLeft() {
                    if (list?.size!! > selectedIndex) {
                        selectedIndex++
                        Glide.with(context).load(list.get(selectedIndex)).into(imageView)
                    }

                }

                override fun swipeRight() {
                    if (selectedIndex > 0) {
                        selectedIndex--
                        Glide.with(context).load(list?.get(selectedIndex)).into(imageView)
                    }
                }
            })

        }

    }

}