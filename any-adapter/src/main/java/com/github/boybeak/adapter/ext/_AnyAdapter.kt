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

fun <S> AnyAdapter.firstSourceAs(): S? {
    return first()?.source() as? S
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

fun <S> AnyAdapter.lastSourceAs(): S? {
    return last()?.source() as? S
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

fun AnyAdapter.isNotEmpty(): Boolean {
    return !isEmpty
}

fun AnyAdapter.forEach(block: (item: ItemImpl<*>) -> Unit) {
    repeat(itemCount) {
        block.invoke(getItem(it))
    }
}

fun AnyAdapter.forEachIndexed(block: (position: Int, item: ItemImpl<*>) -> Unit) {
    repeat(itemCount) {
        block.invoke(it, getItem(it))
    }
}

fun <T : ItemImpl<*>> AnyAdapter.forEachIndexed(clz: Class<T>, block: (position: Int, item: T) -> Unit) {
    repeat(itemCount) {
        val item = getItem(it)
        if (clz.isInstance(item))  {
            block.invoke(it, item as T)
        }
    }
}

fun AnyAdapter.countIgnore(vararg clz: Class<out ItemImpl<*>>): Int {
    if (clz.isEmpty()) {
        return itemCount
    }
    var count = 0
    for (i in 0 until itemCount) {
        if (isIn(getItem(i), *clz)) {
            continue
        }
        count++
    }
    return count
}

fun AnyAdapter.countOnly(vararg clz: Class<out ItemImpl<*>>): Int {
    if (clz.isEmpty()) {
        return 0
    }
    var count = 0
    for (i in 0 until itemCount) {
        if (isIn(getItem(i), *clz)) {
            count++
        }
    }
    return count
}

fun AnyAdapter.contains(block: (position: Int, item: ItemImpl<*>) -> Boolean): Boolean {
    var result = false
    forEachIndexed { position, item ->
        if (block.invoke(position, item)) {
            result = true
            return@forEachIndexed
        }
    }
    return result
}

fun <T : ItemImpl<*>> AnyAdapter.contains(clz: Class<T>, block: (position: Int, item: T) -> Boolean): Boolean {
    var result = false
    forEachIndexed { position, item ->
        if (clz.isInstance(item) && block.invoke(position, item as T)) {
            result = true
            return@forEachIndexed
        }
    }
    return result
}

private fun isIn(item: ItemImpl<*>, vararg clz: Class<out ItemImpl<*>>): Boolean {
    return clz.all {
        it.isInstance(item)
    }
}