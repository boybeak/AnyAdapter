package com.github.boybeak.pexels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.github.boybeak.adapter.AnyAdapter
import com.github.boybeak.pexels.api.api
import com.github.boybeak.pexels.ext.obtain
import com.github.boybeak.pexels.vm.BaseViewModel
import com.github.boybeak.pexels.vm.SearchVM
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private val adapter = AnyAdapter()
    private val vm: SearchVM by lazy {
        obtain<SearchVM>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainRV.adapter = adapter

        vm.curatedPhotos {
            toast(it.message ?: "No message")
        }

    }
}
