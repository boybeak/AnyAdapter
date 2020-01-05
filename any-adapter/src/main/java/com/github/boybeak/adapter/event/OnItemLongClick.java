package com.github.boybeak.adapter.event;

import com.github.boybeak.adapter.ItemImpl;

public abstract class OnItemLongClick<T extends ItemImpl> implements OnLongClick<T> {
    @Override
    public int[] getLongClickableIds() {
        return new int[]{0};
    }
}
