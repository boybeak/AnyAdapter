package com.github.boybeak.pexels.adapter.holder

import android.util.Log
import android.view.View
import com.github.boybeak.adapter.AbsHolder
import com.github.boybeak.adapter.AnyAdapter
import com.github.boybeak.adapter.footer.Footer
import com.github.boybeak.pexels.R
import com.github.boybeak.pexels.adapter.item.FooterItem
import kotlinx.android.synthetic.main.item_footer.view.*
import org.jetbrains.anko.toast

class FooterHolder(v: View) : AbsHolder<FooterItem>(v) {

    override fun onBind(item: FooterItem, position: Int, absAdapter: AnyAdapter) {
        view<View>(R.id.waitingPB).visibility = if (item.source().state == Footer.LOADING)
            View.VISIBLE else View.INVISIBLE
    }

}