package com.androidinnovations.photosview.util.binding

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
//import com.ozoneddigital.adamJee.utils.changeDateFormat
//import com.ozoneddigital.adamJee.utils.timeago

/**
 * Created by Wasabeef on 2017/10/24.
 */
object TextBindingAdapters {
    @JvmStatic
    @BindingAdapter("android:text")
    fun bindText(view: TextView, value: Int) {
        view.text = value.toString()
    }

    @JvmStatic
    @BindingAdapter("android:text", "android:text_format")
    fun bindTextWithFormat(view: TextView, value: Int, format: String) {
        view.text = String.format(format, value)
    }

    @JvmStatic
    @BindingAdapter("android:text", "android:text_format")
    fun bindTextWithFormat(view: TextView, value: Double, format: String) {
        view.text = String.format(format, value)
    }

    @JvmStatic
    @BindingAdapter(
        value = ["android:text", "is_date", "incoming_format", "output_format", "show_time_ago"],
        requireAll = false
    )
    fun TextView.bindServerDate(
        text: String?,
        isDate: Boolean?,
        incomingFormat: String? = "yyyy-MM-d HH:mm:ss",
        outFormat: String? = "dd-MMM-yyyy",
        isTimeAgo: Boolean? = false
    ) {
        /*Parse string data and set it in another format for your textView*/
        val incomingFormat1 = incomingFormat ?: "yyyy-MM-d HH:mm:ss"
        val outFormat1 = outFormat ?: "dd-MMM-yyyy"
        val isTimeAgo1 = isTimeAgo ?: false
        val isDate1 = isDate ?: false

        text?.let {
//            if(isDate1){
//                if (isTimeAgo1)
//                    this.text = text.timeago(incomingFormat1)
//                else {
//                    this.text = text.changeDateFormat(outFormat1)
//                }
//            }
//            else{
            this.text = text
            // }

        } ?: kotlin.run {
            this.visibility = View.GONE
        }

    }


}