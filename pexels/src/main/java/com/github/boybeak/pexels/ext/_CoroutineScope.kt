package com.github.boybeak.pexels.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun CoroutineScope.launchSafety(block: suspend CoroutineScope.() -> Unit,
                                error: ((e: Throwable) -> Unit)? = null,
                                finally: (() -> Unit)? = null) {
    launch {
        try {
            block.invoke(this)
        } catch (e: Throwable) {
            error?.invoke(e)
        } finally {
            finally?.invoke()
        }
    }
}