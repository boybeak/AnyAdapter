package com.github.boybeak.main.adapter.item

import com.github.boybeak.adapter.footer.Footer
import com.github.boybeak.main.adapter.holder.FooterHolder
import com.github.boybeak.adapter.AbsItem
import com.github.boybeak.adapter.ItemImpl
import com.github.boybeak.main.R

class FooterItem(s: Footer) : AbsItem<Footer>(s) {

	override fun layoutId(): Int {
		return R.layout.item_footer
	}

	override fun holderClass(): Class<FooterHolder> {
		return FooterHolder::class.java
	}

	override fun areContentsSame(other: ItemImpl<*>): Boolean {
		return if (other is FooterItem) {
			val os = other.source()
			source().state == os.state && source().message == os.message
					&& source().summary == os.summary && source().icon == os.icon
		} else {
			false
		}
	}

}