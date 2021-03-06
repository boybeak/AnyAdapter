package com.github.boybeak.adapter.selector;

import com.github.boybeak.adapter.ItemImpl;

public interface Selector<T extends ItemImpl> {

    Selector<T> begin();
    Selector<T> reset();
    void end();
    boolean isInSelectMode();

    /**
     * @param t
     * @return true if result meet expectations, if you want to select item and result is selected,
     * or if you want to un-select and result is unselected
     */
    boolean select(T t);
    boolean select(int index);
    boolean isSelected(T t);

    int remove();
}
