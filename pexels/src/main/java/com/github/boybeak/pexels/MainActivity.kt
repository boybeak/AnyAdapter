package com.github.boybeak.pexels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.boybeak.adapter.AnyAdapter
import com.github.boybeak.adapter.AutoFooterAdapter
import com.github.boybeak.adapter.event.OnItemClick
import com.github.boybeak.adapter.footer.Footer
import com.github.boybeak.pexels.adapter.item.FooterItem
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

    private val adapter = AutoFooterAdapter(FooterItem(Footer())).apply {
        setOnClickFor(PhotoItem::class.java, object : OnItemClick<PhotoItem>(){
            override fun onClick(view: View, item: PhotoItem, position: Int, adapter: AnyAdapter) {
                toast(item.source().photographer)
            }
        })
    }
    private val vm: MainVM by lazy {
        obtain<MainVM>()
    }

    private val onBottom = object : OnScrollBottomListener() {
        override fun onScrollBottom(recyclerView: RecyclerView, newState: Int) {
            if (adapter.isLoading) {
                return
            }
            adapter.notifyLoading()
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
        (mainRV.layoutManager as GridLayoutManager).let { glm ->
            glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (position == adapter.itemCount - 1) {
                        return glm.spanCount
                    }
                    return 1
                }
            }
        }

        adapter.notifyLoading()
        vm.getCuratedPhotos (
            {
                adapter.addAll(it) { s, _ ->
                    PhotoItem(s)
                }
                mainRV.scrollToPosition(0)
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
