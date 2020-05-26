package com.github.boybeak.pexels.adapter.holder

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.boybeak.adapter.AbsHolder
import com.github.boybeak.adapter.AnyAdapter
import com.github.boybeak.pexels.R
import com.github.boybeak.pexels.adapter.item.PhotoItem
import com.github.boybeak.pexels.ext.thumb
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoHolder(v: View) : AbsHolder<PhotoItem>(v) {

	override fun onBind(item: PhotoItem, position: Int, absAdapter: AnyAdapter) {
		view<ImageView>(R.id.photoIV).thumb(item.source().src.medium)
	}

}