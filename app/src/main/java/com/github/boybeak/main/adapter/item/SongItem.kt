package com.github.boybeak.main.adapter.item

import com.github.boybeak.adapter.AbsItem
import com.github.boybeak.adapter.ItemImpl
import com.github.boybeak.main.R
import com.github.boybeak.main.Song
import com.github.boybeak.main.adapter.holder.SongHolder

class SongItem(s: Song) : AbsItem<Song>(s) {
    override fun layoutId(): Int {
        return R.layout.item_song
    }

    override fun holderClass(): Class<SongHolder> {
        return SongHolder::class.java
    }

    override fun areContentsSame(other: ItemImpl<*>): Boolean {
        return if (other is SongItem) {
            return other.source() == source()
        } else {
            false
        }
    }

    override fun supportSelect(): Boolean {
        return true
    }
}