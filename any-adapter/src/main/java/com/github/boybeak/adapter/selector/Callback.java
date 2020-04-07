package com.github.boybeak.adapter.selector;

import com.github.boybeak.adapter.AnyAdapter;
import com.github.boybeak.adapter.ItemImpl;

import org.jetbrains.annotations.NotNull;

public interface Callback<T extends ItemImpl> {
    void onBegin();
    void onEnd();
    void onReset();
    void onSelected(@NotNull T t, @NotNull AnyAdapter adapter);
    void onUnselected(@NotNull T t, @NotNull AnyAdapter adapter);
}