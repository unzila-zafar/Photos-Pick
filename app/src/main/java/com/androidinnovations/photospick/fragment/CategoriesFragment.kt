package com.androidinnovations.photospick.fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.androidinnovations.photospick.MainActivity
import com.androidinnovations.photospick.generics.GenericAdapter
import com.androidinnovations.photospick.model.CategoriesModel
import com.androidinnovations.photospick.util.GridSpacingItemDecoration
import com.androidinnovations.photosview.R
import com.androidinnovations.photosview.databinding.FragmentCategoriesBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

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
        (requireActivity() as MainActivity).changeTopBarText(requireActivity().getString(R.string.categories))
        (requireActivity() as MainActivity).showTopView(true)

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

                var bundle = Bundle()
                bundle.putString("category", categoriesAdapter[position]!!.name.lowercase())
                var fragment = ImagesFragment()
                fragment.arguments = bundle
                (requireContext() as MainActivity).changeFragment(fragment, false)


            }

            override fun onMenuClick(view: View, position: Int) {


            }

        })

        viewOfLayout!!.categoriesListing.layoutManager = mlayoutManager

        viewOfLayout!!.categoriesListing.addItemDecoration(
            GridSpacingItemDecoration(
                2,
                0,
                true
            )
        )
        viewOfLayout!!.categoriesListing.adapter = categoriesAdapter


    }


    private fun loadRecyclerData() {
        var modelData1 = CategoriesModel(1, requireContext().getString(R.string.nature), R.drawable.ic_nature)
        var modelData2 = CategoriesModel(2, requireContext().getString(R.string.food), R.drawable.ic_food)
        var modelData3 = CategoriesModel(3, requireContext().getString(R.string.science), R.drawable.ic_science)
        var modelData4 = CategoriesModel(4, requireContext().getString(R.string.music), R.drawable.ic_music)
        var modelData5 = CategoriesModel(4, requireContext().getString(R.string.education), R.drawable.ic_education)
        var modelData6 = CategoriesModel(5, requireContext().getString(R.string.mehndi), R.drawable.ic_mehndi)
        var modelData7 = CategoriesModel(6, requireContext().getString(R.string.animals), R.drawable.ic_animal)
        var modelData8 = CategoriesModel(6, requireContext().getString(R.string.computer), R.drawable.ic_computer)
        var listData: ArrayList<CategoriesModel> = ArrayList()
        listData.add(modelData1)
        listData.add(modelData2)
        listData.add(modelData3)
        listData.add(modelData4)
        listData.add(modelData5)
        listData.add(modelData6)
        listData.add(modelData7)
        listData.add(modelData8)

        categoriesAdapter.addAll(listData)

    }
}