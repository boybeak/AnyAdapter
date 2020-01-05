package com.github.boybeak.adapter;

import androidx.annotation.Nullable;

public abstract class AbsItem<T> implements ItemImpl<T> {

    private T t;

    public AbsItem(T t) {
        this.t = t;
    }

    @Override
    public T source() {
        return t;
    }

    public void setSource(T t) {
        this.t = t;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ItemImpl) {
            return source().equals(((ItemImpl) obj).source());
        }
        return false;
    }
}
