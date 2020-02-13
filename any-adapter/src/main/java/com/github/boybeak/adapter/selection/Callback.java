package com.github.boybeak.adapter.selection;

import com.github.boybeak.adapter.ItemImpl;

import org.jetbrains.annotations.NotNull;

public interface Callback<T extends ItemImpl> {
    void onBegin();
    void onEnd();
    void onReset();
    void onSelected(@NotNull T t);
    void onUnselected(@NotNull T t);
}