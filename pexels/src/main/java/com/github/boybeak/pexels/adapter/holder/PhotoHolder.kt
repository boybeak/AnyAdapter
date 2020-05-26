package com.github.boybeak.pexels.adapter.holder

import android.view.View
import com.bumptech.glide.Glide
import com.github.boybeak.adapter.AbsHolder
import com.github.boybeak.adapter.AnyAdapter
import com.github.boybeak.pexels.R
import com.github.boybeak.pexels.adapter.item.PhotoItem
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoHolder(v: View) : AbsHolder<PhotoItem>(v) {

	override fun onBind(item: PhotoItem, position: Int, absAdapter: AnyAdapter) {
		Glide.with(context()).asBitmap().load(item.source().src.medium)
			.into(view(R.id.photoIV))
	}

}