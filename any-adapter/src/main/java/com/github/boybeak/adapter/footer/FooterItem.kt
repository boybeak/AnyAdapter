package com.github.boybeak.adapter.footer

import art.musemore.adapter.R
import com.github.boybeak.adapter.AbsItem
import com.github.boybeak.adapter.ItemImpl

class FooterItem(footer: Footer) : AbsItem<Footer>(footer) {
    override fun areContentsSame(other: ItemImpl<*>): Boolean {
        val any = other.source()
        if (any is Footer) {
            return source().state == any.state && source().icon == any.icon && source().message == any.message
        }
        return false
    }

    override fun layoutId(): Int {
        return R.layout.item_footer
    }

    override fun holderClass(): Class<FooterHolder> {
        return FooterHolder::class.java
    }
}