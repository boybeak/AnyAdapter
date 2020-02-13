package com.github.boybeak.adapter.selection;

import com.github.boybeak.adapter.ItemImpl;

public class SelectionArgs {

    public static final int ACTION_MODE_CHANGE = 1, ACTION_STATE_CHANGE = 2;

    public static SelectionArgs modeChange(Class<? extends ItemImpl> clz, boolean value) {
        return new SelectionArgs(clz, ACTION_MODE_CHANGE, value);
    }

    public static SelectionArgs stateChange(Class<? extends ItemImpl> clz, boolean value) {
        return new SelectionArgs(clz, ACTION_STATE_CHANGE, value);
    }

    private Class<? extends ItemImpl> clz;
    private int action;
    private boolean value;

    private SelectionArgs(Class<? extends ItemImpl> clz, int action, boolean value) {
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
