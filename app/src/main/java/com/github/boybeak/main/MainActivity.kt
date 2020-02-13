package com.github.boybeak.main

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.boybeak.adapter.AnyAdapter
import com.github.boybeak.adapter.event.OnItemClick
import com.github.boybeak.adapter.event.OnItemLongClick
import com.github.boybeak.adapter.selection.MultipleSelection
import com.github.boybeak.adapter.selection.SingleSelection
import com.github.boybeak.easypermission.Callback
import com.github.boybeak.easypermission.EasyPermission
import com.github.boybeak.main.adapter.SongItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val selectCallback = object : com.github.boybeak.adapter.selection.Callback<SongItem> {
        override fun onSelected(t: SongItem) {
            Toast.makeText(this@MainActivity, "onSelected " + t.source().title, Toast.LENGTH_SHORT).show()
        }

        override fun onUnselected(t: SongItem) {
            Toast.makeText(this@MainActivity, "onUnselected " + t.source().title, Toast.LENGTH_SHORT).show()
        }

        override fun onBegin() {
        }

        override fun onEnd() {
        }

        override fun onReset() {
        }
    }

    private val longClick: OnItemLongClick<SongItem> = object : OnItemLongClick<SongItem>(){
        override fun onLongClick(view: View, item: SongItem, position: Int): Boolean {
            adapter.multipleSelectionFor(SongItem::class.java).run {
                registerCallback(selectCallback)
                begin(true)
                select(item)
            }
            return true
        }
    }
    private val click: OnItemClick<SongItem> = object : OnItemClick<SongItem>(){
        override fun onClick(view: View, item: SongItem, position: Int) {
            val selection = adapter.multipleSelectionFor(SongItem::class.java)
            if (selection.isInSelectMode) {
                selection.select(item)
            }
        }
    }
    private val adapter = AnyAdapter().apply {
        setOnClickFor(SongItem::class.java, click)
        setOnLongClickFor(SongItem::class.java, longClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.adapter = adapter

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

    override fun onBackPressed() {
        val selection = adapter.multipleSelectionFor(SongItem::class.java)
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
            adapter.addAll(List(songs.size) {
                SongItem(songs[it])
            })
        }.execute(this)
    }

}
