package com.github.boybeak.adapter.selector;

import com.github.boybeak.adapter.AnyAdapter;
import com.github.boybeak.adapter.ItemImpl;

public class SingleSelector<T extends ItemImpl<?>> extends AbsSelector<T> {

    private static final String TAG = SingleSelector.class.getSimpleName();

    private T lastT = null;

    public SingleSelector(AnyAdapter adapter, Class<T> tClass) {
        super(adapter, tClass);
    }

    @Override
    final public SingleSelector<T> begin() {
        super.begin();
        return this;
    }

    @Override
    public boolean select(T t) {
        if (!isInSelectMode()) {
            throw new IllegalStateException("Call begin() before doing select");
        }
        if (!adapter().contains(t)) {
            return false;
        }
        if (!t.supportSelect() || !t.isSelectable()) {
            return false;
        }
        if (!t.equals(lastT)) {
            if (lastT != null) {
                lastT.setSelected(false);
                adapter().notifyItemChanged(adapter().indexOf(lastT), SelectorArgs.stateChange(itemClz(), lastT.isSelected()));
                for (Callback c : callbacks) {
                    c.onUnselected(lastT, adapter());
                }
            }
            lastT = t;
            lastT.setSelected(true);
            adapter().notifyItemChanged(adapter().indexOf(lastT), SelectorArgs.stateChange(itemClz(), lastT.isSelected()));
            for (Callback c : callbacks) {
                c.onSelected(lastT, adapter());
            }
        }
        return true;
    }

    @Override
    public boolean isSelected(T t) {
        return t.equals(lastT);
    }

    @Override
    public SingleSelector<T> reset() {
        if (lastT != null) {
            lastT.setSelected(false);
            adapter().notifyItemChanged(adapter().indexOf(lastT), SelectorArgs.stateChange(itemClz(), lastT.isSelected()));
            lastT = null;
        }
        for (Callback c : callbacks) {
            c.onReset();
        }
        return this;
    }

    public T getSelectedItem() {
        return lastT;
    }

    @Override
    public int remove() {
        if (lastT != null) {
            adapter().remove(lastT);
            lastT = null;
            return 1;
        }
        return 0;
    }
}
