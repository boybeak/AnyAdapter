package com.github.boybeak.main.adapter.item

import com.github.boybeak.main.adapter.holder.StringHolder
import com.github.boybeak.adapter.AbsItem
import com.github.boybeak.adapter.ItemImpl
import com.github.boybeak.main.R

class StringItem(s: String) : AbsItem<String>(s) {

	override fun layoutId(): Int {
		return R.layout.item_song
	}

	override fun holderClass(): Class<StringHolder> {
		return StringHolder::class.java
	}

	override fun areContentsSame(other: ItemImpl<*>): Boolean {
		return if (other is StringItem) {
			val os = other.source()
			source() == os
		} else {
			false
		}
	}

}