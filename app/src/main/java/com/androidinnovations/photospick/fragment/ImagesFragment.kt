package com.androidinnovations.photosview.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.androidinnovations.photosview.ImagesLargeViewActivity
import com.androidinnovations.photosview.InitApp
import com.androidinnovations.photosview.MainActivity
import com.androidinnovations.photosview.R
import com.androidinnovations.photosview.databinding.FragmentImagesBinding
import com.androidinnovations.photosview.model.ImagesModel
import com.androidinnovations.photosview.util.GridSpacingItemDecoration
import com.androidinnovations.photosview.util.Util
import com.androidinnovations.photosview.util.Util.Companion.showProgressDialog
import com.androidinnovations.photosview.viewmodel.MainViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ozoneddigital.adamJee.generics.GenericAdapter
import kotlin.math.roundToInt

class ImagesFragment : Fragment() {

    lateinit var viewModel: MainViewModel
    private var viewOfLayout: FragmentImagesBinding? = null
    lateinit var imagesAdapter: GenericAdapter<ImagesModel.Hits>
    private var page: Int = 1
    private var mInterstitialAd: InterstitialAd? = null
    var selectedCategory: String? = null
    var fullscreenAdShowing = false
    var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOfLayout = DataBindingUtil.inflate(inflater, R.layout.fragment_images, container,false)

        selectedCategory = requireArguments()!!.get("category").toString()

        loadInterstetialAds()

        (activity as MainActivity).changeTopBarText(selectedCategory!!.split(" ").map { it.capitalize() }.joinToString(" "))

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
            {
                Util.dismissProgressDialog()
                if (it.size != 0) {
                    if(fromScroll)
                    {
                        imagesAdapter.appendAll(it!!, true)
                    }else {
                        imagesAdapter.addAll(it!!)
                    }

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

        mlayoutManager.removeAllViews()
        imagesAdapter = GenericAdapter(R.layout.item_images)

        imagesAdapter.setOnClickListener(object : GenericAdapter.OnItemClickListener {

            override fun onClick(view: View, position: Int) {
                if (running && mInterstitialAd != null) {
                    mInterstitialAd?.show(requireActivity())
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }

               // val filePaths = java.util.ArrayList<String>()
               // filePaths.add(imagesAdapter.get(position)!!.largeImageURL)
                var intent = Intent(requireContext(), ImagesLargeViewActivity::class.java)
                intent.putExtra("url", imagesAdapter.get(position)!!.largeImageURL)
                startActivity(intent)

              //  openLargeScreenView(requireContext(),null, filePaths)
            }

            override fun onMenuClick(view: View, position: Int) {
               // showPopupMenu(view)
            }

        })

        viewOfLayout!!.imagesListing?.layoutManager = mlayoutManager

        viewOfLayout!!.imagesListing?.addItemDecoration(
            GridSpacingItemDecoration(
                2,
                0,
                true
            )
        )
        viewOfLayout!!.imagesListing?.adapter = imagesAdapter


        viewOfLayout!!.imagesListing?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = mlayoutManager.getChildCount();
                totalItemCount = mlayoutManager.getItemCount();
                val lastVisibleItemPositions: IntArray = mlayoutManager.findLastVisibleItemPositions(null)
                firstVisibleItem = getLastVisibleItem(lastVisibleItemPositions)

                if (!loading!! && totalItemCount <= firstVisibleItem + visibleThreshold) {
                    if ((visibleItemCount + firstVisibleItem) >=
                        totalItemCount && firstVisibleItem >= 0
                    ) {
                        if(page < 17) { //check total page size
                            page++
                            doApiCall(page, true, selectedCategory!!)
                        }
                    }
                }


            }
        })
    }

    fun showPopupMenu(view: View) {
        PopupMenu(view.context, view).apply {
            menuInflater.inflate(R.menu.image_menu, menu)
            setOnMenuItemClickListener { item ->
                when(item.title)
                {
                    "Download" ->
                    {

                    }
                    "set_wallpaper" ->
                    {

                    }
                }
                true
            }
        }.show()
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

    override fun onPause() {
        super.onPause()

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

    private fun loadInterstetialAds() {
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireContext(),
            requireContext().getString(R.string.interstetialAd_key),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("interstetialAds", adError?.toString())
                    mInterstitialAd = null
                    fullscreenAdShowing = false

                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("interstetialAds", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    fullscreenAdShowing = true
                }

            })



        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.

                fullscreenAdShowing = false
                Log.d("Ads", "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d("Ads", "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }


            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d("Ads", "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d("Ads", "Ad showed fullscreen content.")
                mInterstitialAd = null

            }
        }

    }


}