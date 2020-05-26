package com.github.boybeak.pexels.adapter.item

import com.github.boybeak.pexels.api.model.Photo
import com.github.boybeak.pexels.adapter.holder.PhotoHolder
import com.github.boybeak.adapter.AbsItem
import com.github.boybeak.adapter.ItemImpl
import com.github.boybeak.pexels.R

class PhotoItem(s: Photo) : AbsItem<Photo>(s) {

	override fun layoutId(): Int {
		return R.layout.item_photo
	}

	override fun holderClass(): Class<PhotoHolder> {
		return PhotoHolder::class.java
	}

	override fun areContentsSame(other: ItemImpl<*>): Boolean {
		return if (other is PhotoItem) {
			val os = other.source()
			source().id == os.id && source().width == os.width && source().height == os.height
				 && source().url == os.url && source().photographer == os.photographer && source().photographer_url == os.photographer_url
				 && source().photographer_id == os.photographer_id && source().src == os.src
		} else {
			false
		}
	}

}