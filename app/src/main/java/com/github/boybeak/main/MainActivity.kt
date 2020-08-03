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
import com.github.boybeak.adapter.event.AbsOnItemClick
import com.github.boybeak.adapter.event.OnClick
import com.github.boybeak.adapter.event.OnItemClick
import com.github.boybeak.adapter.event.OnItemLongClick
import com.github.boybeak.adapter.ext.sortWith
import com.github.boybeak.adapter.ext.withOnClicks
import com.github.boybeak.adapter.ext.withOnLongClicks
import com.github.boybeak.adapter.footer.Footer
import com.github.boybeak.easypermission.Callback
import com.github.boybeak.easypermission.EasyPermission
import com.github.boybeak.easypermission.aspect.OnPermissionDenied
import com.github.boybeak.easypermission.aspect.RequestPermissions
import com.github.boybeak.main.adapter.item.SongItem
import com.github.boybeak.main.adapter.item.FooterItem
import com.github.boybeak.main.adapter.item.StringItem
import com.github.boybeak.main.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val selectCallback = object : com.github.boybeak.adapter.selector.Callback<SongItem> {
        override fun onSelected(t: SongItem, adapter: AnyAdapter) {
            Toast.makeText(this@MainActivity, "onSelected " + t.source().title, Toast.LENGTH_SHORT)
                .show()
        }

        override fun onUnselected(t: SongItem, adapter: AnyAdapter) {
            Toast.makeText(
                this@MainActivity,
                "onUnselected " + t.source().title,
                Toast.LENGTH_SHORT
            ).show()
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

    private val longClick: OnItemLongClick<SongItem> = object : OnItemLongClick<SongItem>() {
        override fun onLongClick(
            view: View,
            item: SongItem,
            position: Int,
            adapter: AnyAdapter
        ): Boolean {
            adapter.multipleSelectorFor(SongItem::class.java).run {
                registerCallback(selectCallback)
                begin(true)
                select(item)
            }
            return true
        }
    }
    private val click: OnItemClick<SongItem> = object : OnItemClick<SongItem>() {
        override fun onClick(view: View, item: SongItem, position: Int, adapter: AnyAdapter) {
            this@MainActivity.adapter.showFooter()
            val selection = adapter.multipleSelectorFor(SongItem::class.java)
            if (selection.isInSelectMode) {
                selection.select(item)
            }
        }
    }
    private val adapter = AutoFooterAdapter(FooterItem(Footer())).apply {
        setOnClickFor(SongItem::class.java, click)
        setOnLongClickFor(SongItem::class.java, longClick)
    }

    private lateinit var deleteBtn: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        recyclerView.adapter = adapter

        loadSongs()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        deleteBtn = menu.findItem(R.id.delete)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                adapter.multipleSelectorFor(SongItem::class.java).run {
                    remove()
                    end()
                }
                true
            }
            R.id.displayNameA_Z -> {
                adapter.sortWith(Comparator<SongItem> { o1, o2 ->
                    o1.source().title.compareTo(o2.source().title)
                })
                recyclerView.scrollToPosition(0)
                true
            }
            R.id.displayNameZ_A -> {
                adapter.sortWith(Comparator<SongItem> { o1, o2 ->
                    o2.source().title.compareTo(o1.source().title)
                })
                recyclerView.scrollToPosition(0)
                true
            }
            R.id.dateIncrease -> {
                adapter.sortWith(Comparator<SongItem> { o1, o2 ->
                    (o1.source().dateAdded - o2.source().dateAdded).toInt()
                })
                recyclerView.scrollToPosition(0)
                true
            }
            R.id.dateDecrease -> {
                adapter.sortWith(Comparator<SongItem> { o1, o2 ->
                    (o2.source().dateAdded - o1.source().dateAdded).toInt()
                })
                recyclerView.scrollToPosition(0)
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

    @RequestPermissions(
        permissions = [Manifest.permission.READ_EXTERNAL_STORAGE],
        requestCode = 100
    )
    private fun loadSongs() {
        ScanTask { songs ->
            adapter.addAll(songs) { s, position ->
                SongItem(s)
            }
            recyclerView.scrollToPosition(0)
            adapter.setFooterClick(object : OnClick<FooterItem> {
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

    @OnPermissionDenied
    fun onPermissionDenied(permission: String, requestCode: Int, neverAsk: Boolean) {
        when (permission) {
            Manifest.permission.READ_EXTERNAL_STORAGE -> {
                Toast.makeText(this, "No permission granted", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
