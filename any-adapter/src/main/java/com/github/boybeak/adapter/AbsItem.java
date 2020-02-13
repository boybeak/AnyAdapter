package com.github.boybeak.adapter;

import androidx.annotation.Nullable;

public abstract class AbsItem<T> implements ItemImpl<T> {

    private T t;
    private boolean isSelectable = false, isSelected = false;

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

    @Override
    public boolean supportSelect() {
        return false;
    }

    @Override
    final public boolean isSelectable() {
        return isSelectable;
    }

    @Override
    final public void setSelectable(boolean isSupportSelect) {
        this.isSelectable = isSupportSelect;
    }

    @Override
    final public boolean isSelected() {
        return isSelected;
    }

    @Override
    final public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
