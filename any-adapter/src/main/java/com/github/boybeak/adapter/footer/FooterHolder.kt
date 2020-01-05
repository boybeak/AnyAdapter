package com.github.boybeak.adapter.footer

import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import art.musemore.adapter.R
import com.github.boybeak.adapter.AbsHolder
import com.github.boybeak.adapter.AnyAdapter

class FooterHolder(v: View) : AbsHolder<FooterItem>(v) {

    private val loadingBar = view<ProgressBar>(R.id.footerLoadingBar)
    private val msg = view<AppCompatTextView>(R.id.footerMsg)

    private val noOnePart = view<View>(R.id.footerNoOne)
    private val noOneIcon = view<AppCompatImageView>(R.id.footerNoOneIcon)
    private val noOneMsg = view<AppCompatTextView>(R.id.footerNoOneMsg)
    private val noOneSummary = view<AppCompatTextView>(R.id.footerNoOneSummary)


    override fun onBind(item: FooterItem, position: Int, absAdapter: AnyAdapter) {
        val footer = item.source()
        when(footer.state) {
            Footer.NO_ONE -> {
                loadingBar.visibility = View.GONE
                msg.visibility = View.GONE
                noOnePart.visibility = View.VISIBLE

                if (footer.icon != 0) {
                    noOneIcon.visibility = View.VISIBLE
                    noOneIcon.setImageResource(footer.icon)
                } else {
                    noOneIcon.visibility = View.GONE
                    noOneIcon.setImageDrawable(null)
                }

                noOneMsg.text = footer.message
                noOneSummary.text = footer.summary
            }
            Footer.SUCCESS, Footer.FAILED, Footer.NO_MORE -> {
                loadingBar.visibility = View.GONE
                msg.visibility = View.VISIBLE
                noOnePart.visibility = View.GONE

                msg.text = footer.message
            }
            Footer.LOADING -> {
                loadingBar.visibility = View.VISIBLE
                msg.visibility = View.GONE
                noOnePart.visibility = View.GONE
            }
        }
    }
}