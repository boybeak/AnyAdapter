package com.github.boybeak.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

public interface ItemImpl<T> {
    T source();
    @LayoutRes int layoutId();
    @NonNull
    Class<? extends AbsHolder> holderClass();
    boolean areContentsSame(@NonNull ItemImpl other);

    boolean supportSelect();

    boolean isSelectable();
    void setSelectable(boolean isSupportSelect);

    boolean isSelected();
    void setSelected(boolean isSelected);

}
