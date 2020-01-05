package com.github.boybeak.adapter.footer

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import art.musemore.adapter.R
import com.github.boybeak.adapter.AbsHolder
import com.github.boybeak.adapter.AnyAdapter

class IndeterminateHolder(v: View) : AbsHolder<IndeterminateItem>(v) {

    private val loadingPart = view<FrameLayout>(R.id.footerLoadingPart)
    private val loadingMsg = view<AppCompatTextView>(R.id.footerLoadingMsg)
    private val msgPart = view<LinearLayoutCompat>(R.id.footerMsgPart)
    private val msgTv = view<AppCompatTextView>(R.id.footerMsg)

    override fun onBind(item: IndeterminateItem, position: Int, absAdapter: AnyAdapter) {
        val footer = item.source()
        msgTv.text = footer.message
        loadingMsg.text = footer.message
        when(footer.state) {
            Footer.LOADING -> {
                loadingPart.visibility = View.VISIBLE
                msgPart.visibility = View.GONE
            }
            Footer.NO_ONE -> {
                loadingPart.visibility = View.GONE
                msgPart.visibility = View.VISIBLE
            }
            else -> {
                loadingPart.visibility = View.GONE
                msgPart.visibility = View.GONE
            }
        }
    }
}