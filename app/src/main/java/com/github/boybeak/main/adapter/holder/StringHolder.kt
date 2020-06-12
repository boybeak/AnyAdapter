package com.github.boybeak.main.adapter.holder

import android.view.View
import com.github.boybeak.adapter.AbsHolder
import com.github.boybeak.adapter.AnyAdapter
import com.github.boybeak.main.adapter.item.StringItem
import com.github.boybeak.main.databinding.ItemSongBinding

class StringHolder(v: View) : AbsHolder<StringItem>(v) {

	private val binding = ItemSongBinding.bind(v)
	override fun onBind(item: StringItem, position: Int, absAdapter: AnyAdapter) {
		binding.songDir.text = item.source()
	}

}