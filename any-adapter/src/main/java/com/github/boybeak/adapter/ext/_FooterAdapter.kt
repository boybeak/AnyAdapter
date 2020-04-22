package com.github.boybeak.adapter.ext

import com.github.boybeak.adapter.FooterAdapter

fun FooterAdapter<*>.isEmptyIgnoreFooter(): Boolean {
    return itemCountIgnoreFooter == 0
}