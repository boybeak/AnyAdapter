package com.github.boybeak.adapter.event;


import com.github.boybeak.adapter.ItemImpl;

public abstract class OnItemClick<T extends ItemImpl> implements OnClick<T> {
    @Override
    public int[] getClickableIds() {
        return new int[]{0};
    }
}
