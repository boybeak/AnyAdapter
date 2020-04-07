package com.github.boybeak.adapter.event;

import android.view.View;

import com.github.boybeak.adapter.AnyAdapter;
import com.github.boybeak.adapter.ItemImpl;

import org.jetbrains.annotations.NotNull;


public interface OnClick<Item extends ItemImpl> {
    int[] getClickableIds();
    void onClick(@NotNull View view, @NotNull Item item, int position, @NotNull AnyAdapter adapter);
}
