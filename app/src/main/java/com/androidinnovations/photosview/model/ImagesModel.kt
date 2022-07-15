package com.androidinnovations.photosview.model

import com.ozoneddigital.adamJee.generics.ListItemViewModel

data class ImagesModel(
    val total: Int,
    val totalHits: Int,
    val hits: List<Hits>
) {
    data class Hits(
        val collections: Int,
        val comments: Int,
        val downloads: Int,
        val id: Int,
        val imageHeight: Int,
        val imageSize: Int,
        val imageWidth: Int,
        val largeImageURL: String,
        val likes: Int,
        val pageURL: String,
        val previewHeight: Int,
        val previewURL: String? = null,
        val previewWidth: Int,
        val tags: String,
        val type: String,
        val user: String,
        val userImageURL: String,
        val user_id: Int,
        val views: Int,
        val webformatHeight: Int,
        val webformatURL: String,
        val webformatWidth: Int
    ): ListItemViewModel()
}