package com.github.boybeak.pexels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.boybeak.adapter.AnyAdapter
import com.github.boybeak.pexels.adapter.item.PhotoItem
import com.github.boybeak.pexels.api.api
import com.github.boybeak.pexels.ext.obtain
import com.github.boybeak.pexels.vm.BaseViewModel
import com.github.boybeak.pexels.vm.MainVM
import com.github.boybeak.pexels.vm.SearchVM
import com.github.boybeak.pexels.widget.OnScrollBottomListener
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private val adapter = AnyAdapter()
    private val vm: MainVM by lazy {
        obtain<MainVM>()
    }

    private val onBottom = object : OnScrollBottomListener() {
        override fun onScrollBottom(recyclerView: RecyclerView, newState: Int) {
            vm.nextPage {
                adapter.addAll(it) { s, _ ->
                    PhotoItem(s)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainRV.adapter = adapter
        mainRV.addOnScrollListener(onBottom)

        vm.getCuratedPhotos (
            {
                adapter.addAll(it) { s, _ ->
                    PhotoItem(s)
                }
            },
            {
                toast(it.message!!)
            }
        )
    }

    override fun onDestroy() {
        mainRV.removeOnScrollListener(onBottom)
        super.onDestroy()
    }
}
