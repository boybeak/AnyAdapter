package com.github.boybeak.pexels.api.model

data class Photo(val id: Long, val width: Int, val height: Int, val url: String, val photographer: String,
                 val photographer_url: String, val photographer_id: Int, val src: Src, val liked: Boolean) {
}