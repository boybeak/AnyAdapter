package com.github.boybeak.adapter.event;

import android.view.View;

import androidx.annotation.NonNull;

import com.github.boybeak.adapter.AnyAdapter;
import com.github.boybeak.adapter.ItemImpl;
import com.github.boybeak.adapter.R;

public abstract class AbsOnItemClick<T extends ItemImpl<?>> extends AbsOnClick<T> {

    public AbsOnItemClick(Class<T> clz) {
        super(clz);
    }

    @Override
    public final int[] getClickableIds() {
        return new int[]{R.id.any_adapter_holder_root};
    }

    @Override
    public final void onClick(@NonNull View view, @NonNull T item, int position, @NonNull AnyAdapter adapter) {
        if (view.getId() == R.id.any_adapter_holder_root) {
            onItemClick(view, item, position, adapter);
        }
    }

    public abstract void onItemClick(@NonNull View view, @NonNull T item, int position, @NonNull AnyAdapter adapter);
}
