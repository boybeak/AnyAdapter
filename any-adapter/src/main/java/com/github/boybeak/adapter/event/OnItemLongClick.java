package com.github.boybeak.adapter.event;

import com.github.boybeak.adapter.ItemImpl;
import com.github.boybeak.adapter.R;

public abstract class OnItemLongClick<T extends ItemImpl<?>> implements OnLongClick<T> {
    @Override
    public final int[] getLongClickableIds() {
        return new int[]{R.id.any_adapter_holder_root};
    }
}
