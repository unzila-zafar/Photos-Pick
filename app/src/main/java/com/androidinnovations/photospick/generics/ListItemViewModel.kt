package com.ozoneddigital.adamJee.generics

import android.text.SpannableString
import androidx.annotation.LayoutRes
import com.androidinnovations.photosview.R


abstract class ListItemViewModel{
    @get:LayoutRes
    open val layoutId: Int = 0
    open val viewType: Int
        get() = GenericViewModelType.ACTUAL_DATA_VIEW.value
    var adapterPosition: Int = -1
    var onItemClickListener: GenericAdapter.OnItemClickListener? = null

}

class EmptyViewModel(var emptyDrawable: Int? = R.drawable.empty, var emptyLabel:String? = "No Item Found", var EmptyLabelSpanned: SpannableString? = null) : ListItemViewModel() {
    override val layoutId: Int = R.layout.empty_view
    override val viewType: Int = GenericViewModelType.EMPTY_VIEW.value
}

class LoadingViewModel (var orientation:ViewType = ViewType.VERTICAL) : ListItemViewModel() {
    override val layoutId: Int = R.layout.loading_view
    override val viewType: Int = GenericViewModelType.LOADING_VIEW.value

    enum class ViewType {
        VERTICAL, HORIZONTAL, HORIZONTAL_MULTIPLE
    }
}

enum class GenericViewModelType(val value: Int) {
    EMPTY_VIEW(0), LOADING_VIEW(1), ACTUAL_DATA_VIEW(2)
}