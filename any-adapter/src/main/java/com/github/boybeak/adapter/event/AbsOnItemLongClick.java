package com.github.boybeak.adapter.event;

import android.view.View;

import androidx.annotation.NonNull;

import com.github.boybeak.adapter.AnyAdapter;
import com.github.boybeak.adapter.ItemImpl;
import com.github.boybeak.adapter.R;

public abstract class AbsOnItemLongClick<T extends ItemImpl<?>> extends AbsOnLongClick<T> {

    public AbsOnItemLongClick(Class<T> clz) {
        super(clz);
    }

    @Override
    public final int[] getLongClickableIds() {
        return new int[]{R.id.any_adapter_holder_root};
    }

    @Override
    public final boolean onLongClick(@NonNull View view, @NonNull T item, int position, @NonNull AnyAdapter adapter) {
        if (view.getId() == R.id.any_adapter_holder_root) {
            return onItemLongClick(view, item, position, adapter);
        }
        return false;
    }

    public abstract boolean onItemLongClick(@NonNull View view, @NonNull T item, int position, @NonNull AnyAdapter adapter);

}
