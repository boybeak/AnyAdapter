package com.github.boybeak.adapter.ext

import com.github.boybeak.adapter.AnyAdapter
import com.github.boybeak.adapter.ItemImpl
import com.github.boybeak.adapter.event.AbsOnClick
import com.github.boybeak.adapter.event.AbsOnLongClick
import com.github.boybeak.adapter.event.OnClick
import com.github.boybeak.adapter.event.OnLongClick
import java.util.Comparator

fun <S, T : ItemImpl<S>> AnyAdapter.getSource(position: Int): S {
    return getItemAs<T>(position).source()
}

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

operator fun <T : ItemImpl<*>> AnyAdapter.contains(item: T) = this.contains(item)

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

fun <T : ItemImpl<*>> AnyAdapter.getAll(clz: Class<T>): List<T> {
    val list = ArrayList<T>()
    forEach {
        if (clz.isInstance(it)) {
            list.add(it as T)
        }
    }
    return list
}

fun <S, T : ItemImpl<S>> AnyAdapter.getAllSource(clz: Class<T>): List<S> {
    val sources = ArrayList<S>()
    forEach {
        if (clz.isInstance(it)) {
            val item = it as T
            sources.add(item.source())
        }
    }
    return sources
}

fun <A : AnyAdapter> A.withOnClicks(vararg onClicks: AbsOnClick<out ItemImpl<*>>): A {
    onClicks.forEach {
        setOnClickFor(it)
    }
    return this
}
fun <A : AnyAdapter> A.withOnLongClicks(vararg onClicks: AbsOnLongClick<out ItemImpl<*>>): A {
    onClicks.forEach {
        setOnLongClickFor(it)
    }
    return this
}

inline fun <reified T : ItemImpl<*>> AnyAdapter.sortWith(comparator: Comparator<T>) {
    sortBy(T::class.java, comparator)
    /*if (!isDataSingleType) {
        throw IllegalStateException("This adapter contains more than one data type that can not compare")
    }
    val list = getItemsAs(T::class.java)
    list.sortWith(comparator)
    mergeFrom(list as List<ItemImpl<Any>>?)*/
}

operator fun AnyAdapter.get(position: Int): ItemImpl<*> = getItem(position)