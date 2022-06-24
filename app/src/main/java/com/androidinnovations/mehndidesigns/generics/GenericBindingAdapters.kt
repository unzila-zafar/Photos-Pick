package com.ozoneddigital.adamJee.generics

import android.app.Activity
import androidx.databinding.BindingAdapter
import androidx.databinding.Observable
import androidx.recyclerview.widget.RecyclerView
import com.androidinnovations.mehndidesigns.R

class GenericBindingAdapters<T> {
    @BindingAdapter("itemViewModels")
    fun bindItemViewModels(recyclerView: RecyclerView, items: io.reactivex.Observable<List<ListItemViewModel>>? ) {
        val adapter = getOrCreateAdapter(recyclerView, items)


    }

    private fun getOrCreateAdapter(recyclerView: RecyclerView,items:io.reactivex.Observable<List<ListItemViewModel>>?): GenericAdapter<ListItemViewModel> {
        return if (recyclerView.adapter != null && recyclerView.adapter is GenericAdapter<*>) {
            recyclerView.adapter as GenericAdapter<ListItemViewModel>
        } else {
            val bindableRecyclerAdapter = GenericAdapter<ListItemViewModel>(R.layout.loading_view)
            recyclerView.adapter = bindableRecyclerAdapter
            bindableRecyclerAdapter
        }
    }
}