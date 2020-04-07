package com.github.boybeak.adapter.selector;

import com.github.boybeak.adapter.ItemImpl;

public class SelectorArgs {

    public static final int ACTION_MODE_CHANGE = 1, ACTION_STATE_CHANGE = 2;

    public static SelectorArgs modeChange(Class<? extends ItemImpl> clz, boolean value) {
        return new SelectorArgs(clz, ACTION_MODE_CHANGE, value);
    }

    public static SelectorArgs stateChange(Class<? extends ItemImpl> clz, boolean value) {
        return new SelectorArgs(clz, ACTION_STATE_CHANGE, value);
    }

    private Class<? extends ItemImpl> clz;
    private int action;
    private boolean value;

    private SelectorArgs(Class<? extends ItemImpl> clz, int action, boolean value) {
        this.clz = clz;
        this.action = action;
        this.value = value;
    }

    public Class<? extends ItemImpl> getClz() {
        return clz;
    }

    public int action() {
        return action;
    }

    public boolean value() {
        return value;
    }
}
