package com.github.boybeak.adapter.event;

import android.view.View;

import com.github.boybeak.adapter.AnyAdapter;
import com.github.boybeak.adapter.ItemImpl;

import androidx.annotation.NonNull;


public interface OnClick<Item extends ItemImpl<?>> {
    int[] getClickableIds();
    void onClick(@NonNull View view, @NonNull Item item, int position, @NonNull AnyAdapter adapter);
}
