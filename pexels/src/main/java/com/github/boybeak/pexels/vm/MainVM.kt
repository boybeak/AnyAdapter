package com.github.boybeak.pexels.vm

import androidx.lifecycle.MutableLiveData
import com.github.boybeak.pexels.api.api
import com.github.boybeak.pexels.api.model.Photo
import com.github.boybeak.pexels.api.model.PhotoPage
import com.github.boybeak.pexels.ext.launchSafety

class MainVM : BaseViewModel() {

    private val photos = MutableLiveData<ArrayList<Photo>>().apply {
        value = ArrayList()
    }

    private val lastPage = MutableLiveData<PhotoPage>()

    fun getCuratedPhotos(callback: (newPage: List<Photo>) -> Unit,
                         error: (e: Throwable) -> Unit) {
        launchSafety({
            val page = api.curatedPhotos()
            lastPage.value = page
            callback.invoke(page.photos)
        }, error)
    }
    fun nextPage(callback: (newPage: List<Photo>) -> Unit) {
        lastPage.value ?: return
        launchSafety(
            {
                val page = api.curatedPhotos(lastPage.value!!.page + 1)
                lastPage.value = page
                callback.invoke(page.photos)
            },
            {

            }
        )
    }
    private fun append(page: PhotoPage) {

    }
}