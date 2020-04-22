package com.github.boybeak.main.adapter.holder

import android.view.View
import com.github.boybeak.adapter.AbsHolder
import com.github.boybeak.adapter.AnyAdapter
import com.github.boybeak.main.adapter.item.FooterItem
import com.github.boybeak.main.databinding.ItemFooterBinding

class FooterHolder(v: View) : AbsHolder<FooterItem>(v) {

	private val binding = ItemFooterBinding.bind(v)
	override fun onBind(item: FooterItem, position: Int, absAdapter: AnyAdapter) {
	}

}