package com.github.boybeak.adapter.selector;

import com.github.boybeak.adapter.AnyAdapter;
import com.github.boybeak.adapter.ItemImpl;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsSelector<T extends ItemImpl> implements Selector<T> {
    private AnyAdapter adapter;
    private Class<T> itemClass;

    private boolean isInSelectMode = false;

    List<Callback<T>> callbacks = new ArrayList<>();

    public AbsSelector(AnyAdapter adapter, Class<T> tClass) {
        this.adapter = adapter;
        itemClass = tClass;
    }

    public AnyAdapter adapter() {
        return adapter;
    }
    public Class<T> itemClz() {
        return itemClass;
    }

    @Override
    public AbsSelector<T> begin() {
        if (isInSelectMode) {
            return this;
        }
        isInSelectMode = true;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            ItemImpl item = adapter.getItem(i);
            if (!item.supportSelect()) {
                continue;
            }
            adapter.getItem(i).setSelectable(isInSelectMode);
        }
        final int firstIndex = adapter.firstIndexOf(itemClass);
        final int lastIndex = adapter.lastIndexOf(itemClass);
        adapter.notifyItemRangeChanged(firstIndex, lastIndex - firstIndex, SelectorArgs.modeChange(itemClass, isInSelectMode));
        for (Callback c : callbacks) {
            c.onBegin();
        }
        return this;
    }

    @Override
    final public void end() {
        if (!isInSelectMode) {
            return;
        }
        isInSelectMode = false;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            ItemImpl item = adapter.getItem(i);
            if (!item.supportSelect()) {
                continue;
            }
            adapter.getItem(i).setSelectable(isInSelectMode);
        }
        final int firstIndex = adapter.firstIndexOf(itemClass);
        final int lastIndex = adapter.lastIndexOf(itemClass);
        adapter.notifyItemRangeChanged(firstIndex, lastIndex - firstIndex, SelectorArgs.modeChange(itemClass, isInSelectMode));
        for (Callback c : callbacks) {
            c.onEnd();
        }
        callbacks.clear();
    }

    @Override
    public boolean isInSelectMode() {
        return isInSelectMode;
    }

    @Override
    final public boolean select(int index) {
        if (index < 0 || index >= adapter().getItemCount()) {
            return false;
        }
        ItemImpl item = adapter().getItem(index);
        if (itemClz().isInstance(item)) {
            return select((T)item);
        }
        return false;
    }

    public void registerCallback(Callback callback) {
        if (callbacks.contains(callback)) {
            return;
        }
        callbacks.add(callback);
    }

    public void unregisterCallback(Callback callback) {
        callbacks.remove(callback);
    }

}
