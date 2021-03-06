package com.github.boybeak.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;

public abstract class AbsHolder<I extends ItemImpl> extends RecyclerView.ViewHolder {

    private SparseArray<View> viewSA = new SparseArray<>();

    public AbsHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void onAttached(@NonNull RecyclerView recyclerView) {
    }

    public void onDetached() {
    }

    public void onSelectionBegin() {

    }

    public void onSelectionEnd() {

    }

    public void onSelectedUpdate(@NonNull I item, @NonNull Boolean isSelected, @NonNull AnyAdapter adapter) {

    }

    public abstract void onBind(@NonNull I item, int position, @NonNull AnyAdapter absAdapter);

    public Context context() {
        return itemView.getContext();
    }

    public <T extends View> T view(@IdRes int id) {

        T t = (T)viewSA.get(id);

        if (t == null) {
            t = itemView.findViewById(id);
            viewSA.put(id, t);
        }
        return t;
    }

}
