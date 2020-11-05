package com.github.boybeak.adapter.event;

import com.github.boybeak.adapter.ItemImpl;

public abstract class AbsOnLongClick<T extends ItemImpl<?>> implements OnLongClick<T> {
    private Class<T> tClass;
    public AbsOnLongClick(Class<T> clz) {
        tClass = clz;
    }

    public Class<T> getItemClass() {
        return tClass;
    }
}
