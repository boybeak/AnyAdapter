package com.github.boybeak.pexels.api.model

data class PhotoPage(
    val total_results: Int, val page: Int,
    val per_page: Int, val next_page: String, val photos: List<Photo>
) {
}