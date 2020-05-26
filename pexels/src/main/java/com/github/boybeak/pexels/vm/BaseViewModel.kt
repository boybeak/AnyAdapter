package com.github.boybeak.pexels.vm

import androidx.lifecycle.ViewModel
import com.github.boybeak.pexels.ext.launchSafety
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {
    companion object {

    }
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun searchPhotos() {
        /*launch {
            api.searchPhotos()
        }*/
    }
    
    fun curatedPhotos(error: (e: Throwable) -> Unit) {
        launchSafety(
            {
                throw IllegalStateException("My exception")
            }, error
        )
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}