package com.github.boybeak.pexels.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

inline fun <reified T : ViewModel> ViewModelStoreOwner.obtain(): T {
    return ViewModelProvider(this).get(T::class.java)
}

inline fun <reified T : ViewModel> ViewModelStoreOwner.obtain(key: String): T {
    return ViewModelProvider(this).get(key, T::class.java)
}