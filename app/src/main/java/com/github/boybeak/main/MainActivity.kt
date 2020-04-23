package com.github.boybeak.main

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.boybeak.adapter.AnyAdapter
import com.github.boybeak.adapter.AutoFooterAdapter
import com.github.boybeak.adapter.FooterAdapter
import com.github.boybeak.adapter.event.OnClick
import com.github.boybeak.adapter.event.OnItemClick
import com.github.boybeak.adapter.event.OnItemLongClick
import com.github.boybeak.adapter.ext.isEmptyIgnoreFooter
import com.github.boybeak.adapter.footer.Footer
import com.github.boybeak.easypermission.Callback
import com.github.boybeak.easypermission.EasyPermission
import com.github.boybeak.main.adapter.SongItem
import com.github.boybeak.main.adapter.item.FooterItem
import com.github.boybeak.main.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val selectCallback = object : com.github.boybeak.adapter.selector.Callback<SongItem> {
        override fun onSelected(t: SongItem, adapter: AnyAdapter) {
            Toast.makeText(this@MainActivity, "onSelected " + t.source().title, Toast.LENGTH_SHORT).show()
        }

        override fun onUnselected(t: SongItem, adapter: AnyAdapter) {
            Toast.makeText(this@MainActivity, "onUnselected " + t.source().title, Toast.LENGTH_SHORT).show()
        }

        override fun onBegin() {
            deleteBtn.isVisible = true
        }

        override fun onEnd() {
            deleteBtn.isVisible = false
        }

        override fun onReset() {
        }
    }

    private val longClick: OnItemLongClick<SongItem> = object : OnItemLongClick<SongItem>(){
        override fun onLongClick(view: View, item: SongItem, position: Int, adapter: AnyAdapter): Boolean {
            adapter.multipleSelectorFor(SongItem::class.java).run {
                registerCallback(selectCallback)
                begin(true)
                select(item)
            }
            return true
        }
    }
    private val click: OnItemClick<SongItem> = object : OnItemClick<SongItem>(){
        override fun onClick(view: View, item: SongItem, position: Int, adapter: AnyAdapter) {
            this@MainActivity.adapter.showFooter()
            val selection = adapter.multipleSelectorFor(SongItem::class.java)
            if (selection.isInSelectMode) {
                selection.select(item)
            }
        }
    }
    private val adapter = AutoFooterAdapter<FooterItem>(FooterItem(Footer())).apply {
        setOnClickFor(SongItem::class.java, click)
        setOnLongClickFor(SongItem::class.java, longClick)
    }

    private lateinit var deleteBtn: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        recyclerView.adapter = adapter

        /*adapter.add(0, object : AnyAdapter.IConverter<Int, SongItem>{
            override fun convert(s: Int): MutableList<SongItem> {

            }
        })*/

        EasyPermission.ask(Manifest.permission.READ_EXTERNAL_STORAGE)
            .go(this, object : Callback {
                override fun onGranted(permissions: MutableList<String>) {
                    loadSongs()
                }

                override fun onDenied(
                    permission: String,
                    shouldShowRequestPermissionRationale: Boolean
                ) {
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        deleteBtn = menu.findItem(R.id.delete)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.delete -> {
                adapter.multipleSelectorFor(SongItem::class.java).run {
                    remove()
                    end()
                }
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onBackPressed() {
        val selection = adapter.multipleSelectorFor(SongItem::class.java)
        if (selection.isInSelectMode) {
            val orderedList = selection.selectedItemsOrderByAdapter
            AlertDialog.Builder(this)
                .setItems(Array(orderedList.size) {
                    orderedList[it].source().title
                }, null)
                .show()
            selection.reset().end()
            return
        }
        super.onBackPressed()
    }

    private fun loadSongs() {
        ScanTask { songs ->
            adapter.addAll(songs) { s, position -> SongItem(s) }
            adapter.setFooterClick(object : OnClick<FooterItem>{
                override fun getClickableIds(): IntArray {
                    return intArrayOf(R.id.hideBtn)
                }

                override fun onClick(
                    view: View,
                    item: FooterItem,
                    position: Int,
                    adapter: AnyAdapter
                ) {
                    this@MainActivity.adapter.hideFooter()
                }
            })
        }.execute(this)
    }



}
