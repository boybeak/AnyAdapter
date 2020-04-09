package com.github.boybeak.adapter.event;

import android.view.View;

import com.github.boybeak.adapter.AnyAdapter;
import com.github.boybeak.adapter.ItemImpl;

import androidx.annotation.NonNull;

public interface OnLongClick<Item extends ItemImpl> {
    int[] getLongClickableIds();
    boolean onLongClick(@NonNull View view, @NonNull Item item, int position, @NonNull AnyAdapter adapter);
}
