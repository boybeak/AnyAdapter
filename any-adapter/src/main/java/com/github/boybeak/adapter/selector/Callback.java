package com.github.boybeak.adapter.selector;

import com.github.boybeak.adapter.AnyAdapter;
import com.github.boybeak.adapter.ItemImpl;

import androidx.annotation.NonNull;

public interface Callback<T extends ItemImpl> {
    void onBegin();
    void onEnd();
    void onReset();
    void onSelected(@NonNull T t, @NonNull AnyAdapter adapter);
    void onUnselected(@NonNull T t, @NonNull AnyAdapter adapter);
}