package com.github.boybeak.adapter.selector;

import android.util.Log;

import com.github.boybeak.adapter.AnyAdapter;
import com.github.boybeak.adapter.ItemImpl;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class MultipleSelector<T extends ItemImpl<?>> extends AbsSelector<T> {

    private static final String TAG = MultipleSelector.class.getSimpleName();

    private List<T> selectedItems = new LinkedList<T>();
//    private List<T> selectedItemsAdapterCopy = new LinkedList<T>();

    private boolean isOrderSensitive = false;

    public MultipleSelector(AnyAdapter adapter, Class<T> tClass) {
        super(adapter, tClass);
    }

    @Override
    final public MultipleSelector<T> begin() {
        super.begin();
        return this;
    }

    public MultipleSelector<T> begin(boolean isOrderSensitive) {
        this.isOrderSensitive = isOrderSensitive;
        return this.begin();
    }

    @Override
    public boolean select(T t) {
        if (!isInSelectMode()) {
            throw new IllegalStateException("Call begin() before doing select");
        }
        if (!adapter().contains(t)) {
            return false;
        }
        Log.v(TAG, "select t.supportSelect=" + t.supportSelect() + " t.isSelectable=" + t.isSelectable());
        if (!t.supportSelect() || !t.isSelectable()) {
            return false;
        }
        if (selectedItems.contains(t)) {

            final int index = selectedItems.indexOf(t);

            selectedItems.remove(t);
//            selectedItemsAdapterCopy.remove(t);
            t.setSelected(false);
            adapter().notifyItemChanged(adapter().indexOf(t), SelectorArgs.stateChange(itemClz(),
                    t.isSelected()));
            if (isOrderSensitive) {
                final int size = selectedItems.size();
                for (int i = index; i < size; i++) {
                    ItemImpl item = selectedItems.get(i);
                    adapter().notifyItemChanged(adapter().indexOf(item));
                }
            }
            for (Callback c : callbacks) {
                c.onUnselected(t, adapter());
            }
        } else {
            selectedItems.add(t);
//            addAtCopyList(t);
            t.setSelected(true);
            adapter().notifyItemChanged(adapter().indexOf(t), SelectorArgs.stateChange(itemClz(),
                    t.isSelected()));
            for (Callback c : callbacks) {
                c.onSelected(t, adapter());
            }
        }
        return true;
    }

    /*private void addAtCopyList(T t) {
        final int index = getIndexInCopyList(t);
        if (index == -1) {
            selectedItemsAdapterCopy.add(t);
        } else {
            selectedItemsAdapterCopy.add(index, t);
        }
    }

    private int getIndexInCopyList(T t) {
        if (selectedItemsAdapterCopy.isEmpty()) {
            return -1;
        }
        int index = adapter().indexOf(t);
        if (selectedItemsAdapterCopy.size() == 1) {
            T theOne = selectedItemsAdapterCopy.get(0);
            int theOneIndex = adapter().indexOf(theOne);

            if (index > theOneIndex) {
                return -1;
            } else {
                return 0;
            }
        }
        final int size = selectedItemsAdapterCopy.size();
        for (int i = 0; i < size - 1; i++) {
            T t1 = selectedItemsAdapterCopy.get(i);
            T t2 = selectedItemsAdapterCopy.get(i+1);
            final int idx1 = adapter().indexOf(t1);
            final int idx2 = adapter().indexOf(t2);
            if (index > idx1 && index < idx2) {
                return i+1;
            }
        }
        return -1;
    }*/

    @Override
    public boolean isSelected(T t) {
        return selectedItems.contains(t);
    }

    public boolean isAllSelected() {
        return !selectedItems.isEmpty() && adapter().countOf(itemClz()) == selectedItems.size();
    }

    @Override
    public MultipleSelector<T> reset() {
        if (selectedItems.isEmpty()) {
            return this;
        }
        List<T> copy = new LinkedList<>(selectedItems);
        selectedItems.clear();
//        selectedItemsAdapterCopy.clear();
        for (T t : copy) {
            t.setSelected(false);
            adapter().notifyItemChanged(adapter().indexOf(t), SelectorArgs.stateChange(itemClz(),
                    t.isSelected()));
        }
        for (Callback c : callbacks) {
            c.onReset();
        }
        return this;
    }

    public boolean selectAllOrNot() {
        if (isAllSelected()) {
            List<T> copy = new LinkedList<>(selectedItems);
            selectedItems.clear();
//            selectedItemsAdapterCopy.clear();
            for (T t : copy) {
                select(t);
                /*t.setSelected(false);
                adapter().notifyItemChanged(adapter().indexOf(t), SelectionArgs.stateChange(itemClz(),
                        t.isSelected()));*/
            }
        } else {
            List<T> ts = adapter().getItemsAs(itemClz());
            final int size = ts.size();
            for (int i = 0; i < size; i++) {
                T t = ts.get(i);
                if (t.isSelected()) {
                    continue;
                }
                select(t);
            }
        }
        return true;
    }

    public List<T> getSelectedItemsOrderByAdapter() {
        List<T> copy = new LinkedList<>(selectedItems);
        Collections.sort(copy, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return adapter().indexOf(o1) - adapter().indexOf(o2);
            }
        });
        return copy;
    }

    /*public int indexOfOrderByAdapter(T t) {
        return selectedItemsAdapterCopy.indexOf(t);
    }*/

    public int indexOf(T t) {
        return selectedItems.indexOf(t);
    }

    @Override
    public int remove() {
        if (selectedItems.isEmpty()) {
            return 0;
        }
        final int size = selectedItems.size();
        adapter().removeAll(selectedItems);
        selectedItems.clear();
        return size;
    }
}
