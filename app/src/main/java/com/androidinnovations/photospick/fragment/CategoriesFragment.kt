package com.androidinnovations.photosview.fragment

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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.androidinnovations.photosview.MainActivity
import com.androidinnovations.photosview.R
import com.androidinnovations.photosview.databinding.FragmentCategoriesBinding
import com.androidinnovations.photosview.model.CategoriesModel
import com.androidinnovations.photosview.util.GridSpacingItemDecoration
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ozoneddigital.adamJee.generics.GenericAdapter
import kotlin.math.roundToInt

class CategoriesFragment: Fragment() {


    lateinit var categoriesAdapter: GenericAdapter<CategoriesModel>
    private var viewOfLayout: FragmentCategoriesBinding? = null
    private var mInterstitialAd: InterstitialAd? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOfLayout = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container,false)

        loadInterstetialAds()

        initRecycler()
        loadRecyclerData() // show categories

        return viewOfLayout?.root
    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun initRecycler() {
        val mlayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        categoriesAdapter = GenericAdapter(R.layout.item_categories)

        categoriesAdapter.setOnClickListener(object : GenericAdapter.OnItemClickListener {

            override fun onClick(view: View, position: Int) {
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(requireActivity())
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }

                var bundle = Bundle()
                bundle.putString("category", categoriesAdapter.get(position)!!.name.lowercase())
                var fragment = ImagesFragment()
                fragment.arguments = bundle
                (requireContext() as MainActivity).changeFragment(fragment, false)


            }

            override fun onMenuClick(view: View, position: Int) {


            }

        })

        viewOfLayout!!.categoriesListing?.layoutManager = mlayoutManager

        viewOfLayout!!.categoriesListing?.addItemDecoration(
            GridSpacingItemDecoration(
                2,
                0,
                true
            )
        )
        viewOfLayout!!.categoriesListing?.adapter = categoriesAdapter


    }


    private fun loadRecyclerData() {
        var modelData1 = CategoriesModel(1, "Nature", R.drawable.ic_nature)
        var modelData2 = CategoriesModel(2, "Food", R.drawable.ic_food)
        var modelData3 = CategoriesModel(3, "Science", R.drawable.ic_science)
        var modelData4 = CategoriesModel(4, "Education", R.drawable.ic_education)
        var modelData5 = CategoriesModel(5, "Mehndi", R.drawable.ic_mehndi)
        var modelData6 = CategoriesModel(6, "Computer", R.drawable.ic_computer)
        var listData: ArrayList<CategoriesModel> = ArrayList()
        listData.add(modelData1)
        listData.add(modelData2)
        listData.add(modelData3)
        listData.add(modelData4)
        listData.add(modelData5)
        listData.add(modelData6)

        categoriesAdapter.addAll(listData!!)

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
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("interstetialAds", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })


        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.


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