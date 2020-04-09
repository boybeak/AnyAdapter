package com.github.boybeak.adapter.event;

import com.github.boybeak.adapter.ItemImpl;

public abstract class OnItemEvent<T extends ItemImpl> implements OnClick<T>, OnLongClick<T> {
    @Override
    public final int[] getClickableIds() {
        return new int[]{0};
    }

    @Override
    public final int[] getLongClickableIds() {
        return new int[]{0};
    }
}
