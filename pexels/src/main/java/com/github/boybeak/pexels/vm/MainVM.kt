package com.github.boybeak.pexels.vm

import androidx.lifecycle.MutableLiveData
import com.github.boybeak.pexels.api.api
import com.github.boybeak.pexels.api.model.PhotoPage
import com.github.boybeak.pexels.ext.launchSafety

class MainVM : BaseViewModel() {

    private val photos = MutableLiveData<ArrayList<PhotoPage>>().apply {
        value = ArrayList()
    }

    fun getCuratedPhotos() {
        launchSafety({
            api.curatedPhotos()
        }, {

        })
    }
}