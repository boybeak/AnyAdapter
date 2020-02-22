package com.github.boybeak.adapter.selection;

import com.github.boybeak.adapter.AnyAdapter;
import com.github.boybeak.adapter.ItemImpl;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SingleSelection<T extends ItemImpl> extends AbsSelection<T> {

    private static final String TAG = SingleSelection.class.getSimpleName();

    private T lastT = null;

    public SingleSelection(AnyAdapter adapter, Class<T> tClass) {
        super(adapter, tClass);
    }

    @Override
    final public SingleSelection<T> begin() {
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
                adapter().notifyItemChanged(adapter().indexOf(lastT), SelectionArgs.stateChange(itemClz(), lastT.isSelected()));
                for (Callback c : callbacks) {
                    c.onUnselected(lastT);
                }
            }
            lastT = t;
            lastT.setSelected(true);
            adapter().notifyItemChanged(adapter().indexOf(lastT), SelectionArgs.stateChange(itemClz(), lastT.isSelected()));
            for (Callback c : callbacks) {
                c.onSelected(lastT);
            }
        }
        return true;
    }

    @Override
    public boolean isSelected(T t) {
        return t.equals(lastT);
    }

    @Override
    public SingleSelection<T> reset() {
        if (lastT != null) {
            lastT.setSelected(false);
            adapter().notifyItemChanged(adapter().indexOf(lastT), SelectionArgs.stateChange(itemClz(), lastT.isSelected()));
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
