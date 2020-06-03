package com.github.boybeak.adapter.event;


import com.github.boybeak.adapter.ItemImpl;
import com.github.boybeak.adapter.R;

public abstract class OnItemClick<T extends ItemImpl> implements OnClick<T> {
    @Override
    public final int[] getClickableIds() {
        return new int[]{R.id.any_adapter_holder_root};
    }
}
