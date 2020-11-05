package com.github.boybeak.adapter.event;

import com.github.boybeak.adapter.ItemImpl;

public abstract class AbsOnClick<T extends ItemImpl<?>> implements OnClick<T> {

    private Class<T> tClass;
    public AbsOnClick(Class<T> clz) {
        tClass = clz;
    }

    public Class<T> getItemClass() {
        return tClass;
    }
}
