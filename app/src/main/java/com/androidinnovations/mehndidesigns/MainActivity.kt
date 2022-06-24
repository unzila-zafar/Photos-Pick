package com.androidinnovations.mehndidesigns

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.androidinnovations.mehndidesigns.databinding.ActivityMainBinding
import com.androidinnovations.mehndidesigns.model.ImagesModel
import com.androidinnovations.mehndidesigns.retrofit.MainRepository
import com.androidinnovations.mehndidesigns.retrofit.RetrofitService
import com.androidinnovations.mehndidesigns.util.GridSpacingItemDecoration
import com.androidinnovations.mehndidesigns.util.Util
import com.androidinnovations.mehndidesigns.viewmodel.MainViewModel
import com.androidinnovations.mehndidesigns.viewmodel.MyViewModelFactory
import com.ozoneddigital.adamJee.generics.GenericAdapter
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {


    lateinit var viewModel: MainViewModel
    lateinit var imagesAdapter: GenericAdapter<ImagesModel.Hits>
    private var viewOfLayout: ActivityMainBinding? = null
    private var page: Int = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_main)
        viewOfLayout =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        viewModel = ViewModelProvider(
            this, MyViewModelFactory(
                MainRepository(
                    RetrofitService.getInstance()
                )
            )
        ).get(MainViewModel::class.java)

        initRecycler()

        doApiCall(page)

    }

    private fun doApiCall(page: Int) {
        if (Util.isNetworkConnected(this)) {
            viewModel.getAllPictures(1)
            {
                if (it.size != 0) {
                    imagesAdapter.addAll(it!!)
                    viewOfLayout!!.imagesListing?.adapter = imagesAdapter
                    // imagesAdapter.notifyDataSetChanged()
                }
            }

        }
    }

    val mlayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    var loading: Boolean? = false
    var previousTotal = 0
    val visibleThreshold = 10
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initRecycler() {
        imagesAdapter = GenericAdapter(R.layout.item_images)

        imagesAdapter.setOnClickListener(object : GenericAdapter.OnItemClickListener {

            override fun onClick(view: View, position: Int) {

            }

        })

        // imagesAdapter.addQueryArgs("claim.claim_no")
        // imagesAdapter.addQueryArgs("claim.quotation.ticket_no")


        viewOfLayout!!.imagesListing?.layoutManager = mlayoutManager

        viewOfLayout!!.imagesListing?.addItemDecoration(
            GridSpacingItemDecoration(
                2,
                resources.getDimension(com.intuit.sdp.R.dimen._10sdp).roundToInt(),
                true
            )
        )

        viewOfLayout!!.imagesListing?.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItemCount = recyclerView.childCount
                totalItemCount = mlayoutManager.itemCount
                val lastVisibleItemPositions: IntArray = mlayoutManager.findLastVisibleItemPositions(null)
                firstVisibleItem = getLastVisibleItem(lastVisibleItemPositions)

                val totalItemCount: Int = mlayoutManager.getItemCount()
                if (!loading!! && totalItemCount <= firstVisibleItem + visibleThreshold) {
                    page++
                    doApiCall(page)
                    mlayoutManager.invalidateSpanAssignments()

                    loading = true
                }
            }
        })
    }


    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }
}