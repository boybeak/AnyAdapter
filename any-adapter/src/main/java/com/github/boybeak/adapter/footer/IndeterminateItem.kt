package com.github.boybeak.adapter.footer

import art.musemore.adapter.R
import com.github.boybeak.adapter.AbsItem
import com.github.boybeak.adapter.ItemImpl

class IndeterminateItem(footer: Footer) : AbsItem<Footer>(footer) {

    constructor(): this(Footer())

    override fun layoutId(): Int {
        return R.layout.item_indeterminate
    }

    override fun holderClass(): Class<IndeterminateHolder> {
        return IndeterminateHolder::class.java
    }

    override fun areContentsSame(other: ItemImpl<*>): Boolean {
        val any = other.source()
        if (any is Footer) {
            return source().state == any.state && source().icon == any.icon && source().message == any.message
        }
        return false
    }
}