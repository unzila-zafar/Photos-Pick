package com.androidinnovations.photospick.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.androidinnovations.photospick.ImagesLargeViewActivity
import com.androidinnovations.photospick.InitApp
import com.androidinnovations.photospick.MainActivity
import com.androidinnovations.photospick.generics.GenericAdapter
import com.androidinnovations.photospick.model.ImagesModel
import com.androidinnovations.photospick.util.GridSpacingItemDecoration
import com.androidinnovations.photospick.util.Util
import com.androidinnovations.photospick.util.Util.Companion.openLargeScreenView
import com.androidinnovations.photospick.util.Util.Companion.showProgressDialog
import com.androidinnovations.photosview.R
import com.androidinnovations.photosview.databinding.FragmentImagesBinding
import com.google.android.gms.ads.interstitial.InterstitialAd
import java.util.Locale

class ImagesFragment : Fragment() {

    private var viewOfLayout: FragmentImagesBinding? = null
    lateinit var imagesAdapter: GenericAdapter<ImagesModel.Hits>
    private var page: Int = 1
    private var mInterstitialAd: InterstitialAd? = null
    var selectedCategory: String? = null
    var running = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOfLayout = DataBindingUtil.inflate(inflater, R.layout.fragment_images, container, false)

        selectedCategory = requireArguments().getString("category").toString()

        //loadInterstetialAds()

        (activity as MainActivity).changeTopBarText(
            selectedCategory!!.split(" ").joinToString(" ") {
                it.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                }
            })
        (activity as MainActivity).showTopView(false)

        page = 1
        initRecycler()

        doApiCall(page, false, selectedCategory!!)

        return viewOfLayout?.root
    }


    private fun doApiCall(page: Int, fromScroll: Boolean, category: String) {
        if (Util.isNetworkConnected(requireContext())) {
            requireContext().showProgressDialog()
            loading = false
            InitApp.viewModel.getAllPictures(page, category)
            { it ->
                Util.dismissProgressDialog()
                if (it.size != 0) {
                    if (fromScroll) {
                        imagesAdapter.appendAll(it, true)
                    } else {
                        imagesAdapter.addAll(it)
                    }

                }
            }

        }
    }


    var loading: Boolean? = false

    val visibleThreshold = 10
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initRecycler() {

        val mlayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mlayoutManager.removeAllViews()
        imagesAdapter = GenericAdapter(R.layout.item_images)

        imagesAdapter.setOnClickListener(object : GenericAdapter.OnItemClickListener {

            override fun onClick(view: View, position: Int) {

                // val filePaths = java.util.ArrayList<String>()
                // filePaths.add(imagesAdapter.get(position)!!.largeImageURL)
                val intent = Intent(requireContext(), ImagesLargeViewActivity::class.java)
                intent.putExtra("url", imagesAdapter.get(position)!!.largeImageURL)
                startActivity(intent)

                //  openLargeScreenView(requireContext(),null, filePaths)
            }

            override fun onMenuClick(view: View, position: Int) {

                val filePaths = java.util.ArrayList<String>()
                filePaths.add(imagesAdapter.get(position)!!.largeImageURL)
                openLargeScreenView(requireContext(), null, filePaths)
            }

        })

        viewOfLayout!!.imagesListing.layoutManager = mlayoutManager

        viewOfLayout!!.imagesListing.addItemDecoration(
            GridSpacingItemDecoration(
                2,
                0,
                true
            )
        )
        viewOfLayout!!.imagesListing.adapter = imagesAdapter


        viewOfLayout!!.imagesListing.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = mlayoutManager.getChildCount()
                totalItemCount = mlayoutManager.getItemCount()
                val lastVisibleItemPositions: IntArray =
                    mlayoutManager.findLastVisibleItemPositions(null)
                firstVisibleItem = getLastVisibleItem(lastVisibleItemPositions)

                if (!loading!! && totalItemCount <= firstVisibleItem + visibleThreshold) {
                    if ((visibleItemCount + firstVisibleItem) >=
                        totalItemCount && firstVisibleItem >= 0
                    ) {
                        if (page < 17) { //check total page size
                            page++
                            doApiCall(page, true, selectedCategory!!)
                        }
                    }
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

    override fun onStart() {
        super.onStart()
        running = true
    }

    override fun onStop() {
        super.onStop()
        running = false
        mInterstitialAd = null
    }


}