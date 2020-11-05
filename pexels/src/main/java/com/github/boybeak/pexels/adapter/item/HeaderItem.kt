package com.github.boybeak.pexels.adapter.item

import com.github.boybeak.pexels.adapter.holder.HeaderHolder
import com.github.boybeak.adapter.AbsItem
import com.github.boybeak.adapter.ItemImpl
import com.github.boybeak.pexels.R

class HeaderItem(s: Int) : AbsItem<Int>(s) {

	override fun layoutId(): Int {
		return R.layout.item_header
	}

	override fun holderClass(): Class<HeaderHolder> {
		return HeaderHolder::class.java
	}

	override fun areContentsSame(other: ItemImpl<*>): Boolean {
		return if (other is HeaderItem) {
			val os = other.source()
			source().toInt() == os.toInt()
		} else {
			false
		}
	}

}