package com.github.boybeak.pexels.adapter

import com.github.boybeak.adapter.HeaderFooterAdapter
import com.github.boybeak.adapter.footer.Footer
import com.github.boybeak.pexels.adapter.item.FooterItem
import com.github.boybeak.pexels.adapter.item.HeaderItem

class OneAdapter : HeaderFooterAdapter<HeaderItem, FooterItem>(HeaderItem(0), FooterItem(Footer())) {

}