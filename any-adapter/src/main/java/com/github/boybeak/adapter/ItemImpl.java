package com.github.boybeak.adapter;

import androidx.annotation.LayoutRes;
import org.jetbrains.annotations.NotNull;

public interface ItemImpl<T> {
    T source();
    @LayoutRes int layoutId();
    @NotNull
    Class<? extends AbsHolder> holderClass();
    boolean areContentsSame(@NotNull ItemImpl other);
}
