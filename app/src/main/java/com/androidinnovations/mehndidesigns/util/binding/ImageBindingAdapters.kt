package com.ozoneddigital.adamJee.ui.binding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.androidinnovations.mehndidesigns.R
import com.androidinnovations.mehndidesigns.util.Util.Companion.fetchImage

/**
 * Created by Wasabeef on 2017/10/24.
 */
object ImageBindingAdapters {


    @JvmStatic
    @BindingAdapter(value = ["android:src", "showLargeOnClick", "placeHolder"],
        requireAll = false)
    fun ImageView.loadImage(imagePath: String, showLargeOnClick: Boolean?, placeHolder: Drawable?) {
        val showLargeOnClick1 = showLargeOnClick ?: false
        this.fetchImage(imagePath, showLargeOnClick1, placeHolder)
    }

//    @JvmStatic
//    @BindingAdapter( value = ["android:src", "showLargeOnClick", "placeHolder"],
//        requireAll = false)
//    fun CircleImageView.loadImage(
//        imagePath: String,
//        showLargeOnClick: Boolean?,
//        placeHolder: Drawable?
//    ) {
//        val showLargeOnClick1 = showLargeOnClick ?: false
//        this.fetchImage(imagePath, showLargeOnClick1, placeHolder)
//    }
//
//    @JvmStatic
//    @BindingAdapter(
//        value = ["notification_type"],
//        requireAll = false
//    )
//    fun ImageView.loadImage(notification_type: String?) {
//
//        val notification_type1 = notification_type ?: ""
//        when (notification_type1) {
//
//            "new_cover_note" -> {
//                this.setImageResource(R.drawable._new_cover_note)
//            }
//            "surveyor_assigned_quotation" -> {
//                this.setImageResource(R.drawable._policy_survey_icon)
//            }
//            "new_policy" -> {
//                this.setImageResource(R.drawable._new_policy)
//            }
//            "new_claim" -> {
//                this.setImageResource(R.drawable._new_claim)
//            }
//            "claim_cancelled" -> {
//                this.setImageResource(R.drawable._claim_rejected)
//            }
//            "repair_status" -> {
//                this.setImageResource(R.drawable._repair_in_process)
//            }
//            "verify_claim" -> {
//                this.setImageResource(R.drawable._claim_verified)
//            }
//            "surveyor_assigned_claim" -> {
//                this.setImageResource(R.drawable._claim_survey_icon)
//            }
//            "complains" -> {
//                this.setImageResource(R.drawable._complaint_resolved)
//            }
//            "update_registration_requests" -> {
//                this.setImageResource(R.drawable.registration_accept)
//            }
//            "update_contact_detail_request" -> {
//                this.setImageResource(R.drawable.registration_accept)
//            }
//            "policy_servicing" -> {
//                this.setImageResource(R.drawable._policy_survey_icon)
//            }
//            "dispatch_policy" -> {
//                this.setImageResource(R.drawable._policy_survey_icon)
//            }
//            "policy_activation" -> {
//                this.setImageResource(R.drawable._policy_survey_icon)
//            }
//            "policy_issue" -> {
//                this.setImageResource(R.drawable._policy_survey_icon)
//            }
//
//        }
//
//    }


}