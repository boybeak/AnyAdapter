package com.github.boybeak.pexels

import android.app.Application
import com.github.boybeak.pexels.api.Api

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Api.install(this)
    }
}