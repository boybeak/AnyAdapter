package com.github.boybeak.main.adapter
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.github.boybeak.adapter.AbsHolder
import com.github.boybeak.adapter.AnyAdapter
import com.github.boybeak.main.R

class SongHolder(v: View) : AbsHolder<SongItem>(v) {
    companion object {
        private val TAG = SongHolder::class.java.simpleName
    }

    private val checkFlag = view<AppCompatTextView>(R.id.songCheck)

    override fun onBind(item: SongItem, position: Int, absAdapter: AnyAdapter) {
        view<TextView>(R.id.songDir).text = item.source().data
    }

    override fun onSelectionBegin() {
        checkFlag.visibility = View.VISIBLE
    }

    override fun onSelectionEnd() {
        checkFlag.visibility = View.GONE
    }

    override fun onSelectedUpdate(item: SongItem, isSelected: Boolean, adapter: AnyAdapter) {
        Log.v(TAG, "onSelectedUpdate isSelected=$isSelected")
        checkFlag.isSelected = isSelected
        checkFlag.text = if (isSelected) {
            (adapter.multipleSelectionFor(SongItem::class.java).indexOf(item) + 1).toString()
        } else {
            ""
        }
    }

}
