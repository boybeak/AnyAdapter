package com.github.boybeak.adapter.ext

import com.github.boybeak.adapter.AnyAdapter
import com.github.boybeak.adapter.ItemImpl

fun AnyAdapter.first(): ItemImpl<*>? {
    if (isEmpty) {
        return null
    }
    return getItem(0)
}

fun <T : ItemImpl<*>> AnyAdapter.firstAs(): T? {
    return first() as? T
}

fun AnyAdapter.firstSource(): Any? {
    return first()?.source()
}

fun <S, T : ItemImpl<S>> AnyAdapter.firstSourceAs(): S? {
    return firstAs<T>()?.source()
}

fun <T : ItemImpl<*>> AnyAdapter.firstFor(clz: Class<T>): T? {
    if (isEmpty) {
        return null
    }
    for (i in 0 until itemCount) {
        val item = getItem(i)
        if (clz.isInstance(item)) {
            return item as T
        }
    }
    return null
}

fun AnyAdapter.last(): ItemImpl<*>? {
    if (isEmpty) {
        return null
    }
    return getItem(itemCount -1)
}

fun <T : ItemImpl<*>> AnyAdapter.lastAs(): T? {
    return last() as? T
}

fun AnyAdapter.lastSource(): Any? {
    return last()?.source()
}

fun <S, T : ItemImpl<S>> AnyAdapter.lastSourceAs(): S? {
    return lastAs<T>()?.source()
}

fun <T : ItemImpl<*>> AnyAdapter.lastFor(clz: Class<T>): T? {
    if (isEmpty) {
        return null
    }
    for (i in itemCount -1 downTo 0) {
        val item = getItem(i)
        if (clz.isInstance(item)) {
            return item as T
        }
    }
    return null
}

fun <T : ItemImpl<*>> AnyAdapter.contains(clz: Class<T>): Boolean {
    for (i in 0 until itemCount) {
        if (clz.isInstance(getItem(i))) {
            return true
        }
    }
    return false
}