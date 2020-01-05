package com.github.boybeak.adapter.footer

import androidx.annotation.IntDef

class Footer {

    @State
    @get:State
    var state = NO_ONE
    var message = ""
    var summary = ""
    var icon = 0

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LOADING, SUCCESS, FAILED, NO_ONE, NO_MORE)
    annotation class State

    companion object {
        const val LOADING = 1
        const val SUCCESS = 2
        const val FAILED = 3
        const val NO_ONE = 4
        const val NO_MORE = 5
    }
}
