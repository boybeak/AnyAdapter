package com.github.boybeak.adapter.event;

import android.view.View;

import com.github.boybeak.adapter.AnyAdapter;
import com.github.boybeak.adapter.ItemImpl;

import org.jetbrains.annotations.NotNull;

public interface OnLongClick<Item extends ItemImpl> {
    int[] getLongClickableIds();
    boolean onLongClick(@NotNull View view, @NotNull Item item, int position, @NotNull AnyAdapter adapter);
}
