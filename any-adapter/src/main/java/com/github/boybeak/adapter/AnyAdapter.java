package com.github.boybeak.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;

import com.github.boybeak.adapter.event.OnClick;
import com.github.boybeak.adapter.event.OnLongClick;
import com.github.boybeak.adapter.selection.MultipleSelection;
import com.github.boybeak.adapter.selection.SelectionArgs;
import com.github.boybeak.adapter.selection.SingleSelection;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnyAdapter extends RecyclerView.Adapter<AbsHolder> {

    private static final String TAG = AnyAdapter.class.getSimpleName();

    private SparseArray<Class<? extends AbsHolder>> typeMap = new SparseArray<>();

    private List<ItemImpl> oldItems = null;
    private List<ItemImpl> currentItems = new ArrayList<>();

    private Map<Class<? extends ItemImpl>, OnClick> clickMap = new HashMap<>();
    private Map<Class<? extends ItemImpl>, OnLongClick> longClickMap = new HashMap<>();
    private Map<Class<? extends ItemImpl>, SingleSelection> singleSelectionMap = new HashMap<>();
    private Map<Class<? extends ItemImpl>, MultipleSelection> multipleSelectionMap = new HashMap<>();

    private DiffUtil.Callback diffCallback = new DiffUtil.Callback() {
        @Override
        public int getOldListSize() {
            return oldItems == null ? 0 : oldItems.size();
        }

        @Override
        public int getNewListSize() {
            return currentItems.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldItems.get(oldItemPosition).equals(currentItems.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldItems.get(oldItemPosition).areContentsSame(currentItems.get(newItemPosition));
        }

    };

    private NotifyCallback notifyCallback = null;

    public AnyAdapter() {
    }

    public AnyAdapter(NotifyCallback callback) {
        setNotifyCallback(callback);
    }

    public void setNotifyCallback(NotifyCallback callback) {
        notifyCallback = callback;
    }

    @NonNull
    @Override
    public AbsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Class<? extends AbsHolder> clz = typeMap.get(viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        try {
            return clz.getDeclaredConstructor(View.class).newInstance(view);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Unknown layoutId=(0x" + Integer.toHexString(viewType) + ") or constructor AbsHolder(view) not exist");
    }

    @Override
    public void onBindViewHolder(@NonNull AbsHolder holder, final int position) {
        final ItemImpl item = getItem(position);
        holder.onBind(item, position, this);
        if (item.supportSelect()) {
            if (item.isSelectable()) {
                holder.onSelectionBegin();
            } else {
                holder.onSelectionEnd();
            }
            holder.onSelectedUpdate(item, item.isSelected(), this);
        }
        final OnClick click = clickMap.get(item.getClass());
        if (click != null) {
            int[] ids = click.getClickableIds();
            if (ids == null || ids.length == 0) {
                return;
            }
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click.onClick(v, item, position);
                }
            };
            for (int id : ids) {
                if (id == 0) {
                    holder.itemView.setOnClickListener(clickListener);
                } else {
                    holder.itemView.findViewById(id).setOnClickListener(clickListener);
                }
            }
        }
        final OnLongClick longClick = longClickMap.get(item.getClass());
        if (longClick != null) {
            int[] ids = longClick.getLongClickableIds();
            if (ids == null || ids.length == 0) {
                return;
            }
            View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return longClick.onLongClick(v, item, position);
                }
            };
            for (int id : ids) {
                if (id == 0) {
                    holder.itemView.setOnLongClickListener(longClickListener);
                } else {
                    holder.itemView.findViewById(id).setOnLongClickListener(longClickListener);
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AbsHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {

            for (Object o : payloads) {
                if (o instanceof SelectionArgs) {
                    ItemImpl item = getItem(position);
                    if (!item.supportSelect()) {
                        continue;
                    }
                    SelectionArgs args = (SelectionArgs)o;
                    if (!args.getClz().isInstance(item)) {
                        continue;
                    }
                    switch (args.action()) {
                        case SelectionArgs.ACTION_STATE_CHANGE:
                            holder.onSelectedUpdate(item, args.value(), this);
                            break;
                        case SelectionArgs.ACTION_MODE_CHANGE:
                            if (args.value()) {
                                holder.onSelectionBegin();
                            } else {
                                holder.onSelectionEnd();
                            }
                            break;

                    }
                }
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull AbsHolder holder) {
        ViewParent vp = holder.itemView.getParent();
        holder.onAttached((RecyclerView)vp);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull AbsHolder holder) {
        ViewParent vp = holder.itemView.getParent();
        holder.onDetached((RecyclerView)vp);
    }

    @Override
    public int getItemViewType(int position) {
        ItemImpl item = getItem(position);
        int layoutId = item.layoutId();
        Class<AbsHolder> clz = item.holderClass();
        putType(layoutId, clz);
        return layoutId;
    }

    public void putType(int type, Class<? extends AbsHolder> clz) {
        typeMap.put(type, clz);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    @Override
    public int getItemCount() {
        return currentItems.size();
    }

    public int countOf(Class<? extends ItemImpl> clz) {
        final int size = currentItems.size();
        int sizeOfClz = 0;
        for (int i = 0; i < size; i++) {
            if (clz.isInstance(currentItems.get(i))) {
                sizeOfClz++;
            }
        }
        return sizeOfClz;
    }

    public ItemImpl getItem(int position) {
        return currentItems.get(position);
    }

    public int indexOf(ItemImpl item) {
        return currentItems.indexOf(item);
    }

    public int firstIndexOf(Class<? extends ItemImpl> clz) {
        final int size = currentItems.size();
        for (int i = 0; i < size; i++) {
            if (clz.isInstance(currentItems.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(Class<? extends ItemImpl> clz) {
        final int size = currentItems.size();
        for (int i = size - 1; i >= 0; i--) {
            if (clz.isInstance(currentItems.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public <T extends ItemImpl> boolean contains(T t) {
        return currentItems.contains(t);
    }

    public <T extends ItemImpl> List<T> getItemsAs(Class<T> clz) {
        final int size = currentItems.size();
        List<T> ts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ItemImpl item = currentItems.get(i);
            if (clz.isInstance(item)) {
                ts.add((T)item);
            }
        }
        return ts;
    }

    public <T extends ItemImpl> T getItemAs(int position) {
        return (T)getItem(position);
    }

    public <T extends ItemImpl> void setOnClickFor(Class<T> clz, OnClick<T> click) {
        clickMap.put(clz, click);
    }

    public <T extends ItemImpl> void setOnLongClickFor(Class<T> clz, OnLongClick<T> click) {
        longClickMap.put(clz, click);
    }

    private <T extends ItemImpl> boolean isSelectableFor(Class<T> clz) {
        SingleSelection ss = singleSelectionMap.get(clz);
        if (ss != null) {
            return ss.isInSelectMode();
        }
        MultipleSelection ms = multipleSelectionMap.get(clz);
        if (ms != null) {
            return ms.isInSelectMode();
        }
        return false;
    }

    private <T extends ItemImpl> void handleAddOperation(T item) {
        if (!item.supportSelect()) {
            return;
        }
        item.setSelectable(isSelectableFor(item.getClass()));
    }

    public <T extends ItemImpl> void add(final int position, final T item) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                handleAddOperation(item);
                currentItems.add(position, item);
            }
        };
        syncNotify(callback);
    }

    public <T extends ItemImpl> void add(final T item) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                handleAddOperation(item);
                currentItems.add(item);
            }
        };
        syncNotify(callback);
    }

    public <T extends ItemImpl> void replace(final int position, final T item) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                handleAddOperation(item);
                currentItems.set(position, item);
            }
        };
        syncNotify(callback);
    }

    public <T extends ItemImpl> void addAll(final Collection<T> items) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                for (T t : items) {
                    handleAddOperation(t);
                }
                currentItems.addAll(items);
            }
        };
        syncNotify(callback);
    }

    public <T extends ItemImpl> void addAll(final int position, final Collection<T> items) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                for (T t : items) {
                    handleAddOperation(t);
                }
                currentItems.addAll(position, items);
            }
        };
        syncNotify(callback);
    }

    private <S, T extends ItemImpl<S>> List<T> doConvert(List<S> sources, IConverter<S, T> converter) {
        List<T> items = new ArrayList<>(sources.size());
        for (int i = 0; i < sources.size(); i++) {
            S s = sources.get(i);
            T t = converter.convert(s, i);
            handleAddOperation(t);
            items.add(t);
        }
        return items;
    }

    public <S, T extends ItemImpl<S>> void addAll(int position, List<S> sources, IConverter<S, T> converter) {
        addAll(position, doConvert(sources, converter));
    }

    public <S, T extends ItemImpl<S>> void addAll(List<S> sources, IConverter<S, T> converter) {
        addAll(doConvert(sources, converter));
    }

    private <T extends ItemImpl> void handleRemoveOperation(T t) {
        if (!t.supportSelect()) {
            return;
        }
        SingleSelection ss = singleSelectionMap.get(t.getClass());
        if (ss != null) {
            if (t.equals(ss.getSelectedItem())) {
                ss.select(t);
            }
            return;
        }
        MultipleSelection ms = multipleSelectionMap.get(t.getClass());
        if (ms != null) {
            if (ms.isSelected(t)) {
                ms.select(t);
            }
        }
    }

    public <T extends ItemImpl> void remove(final T item) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                handleRemoveOperation(item);
                currentItems.remove(item);
            }
        };
        syncNotify(callback);
    }

    public void remove(final int position) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                handleRemoveOperation(getItem(position));
                currentItems.remove(position);
            }
        };
        syncNotify(callback);
    }

    public <T extends ItemImpl> void removeAll(final Collection<T> items) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                for (T t : items) {
                    handleAddOperation(t);
                }
                currentItems.removeAll(items);
            }
        };
        syncNotify(callback);
    }

    public void removeBy(@NonNull IFilter<ItemImpl> filter) {
        List<ItemImpl> removeList = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            ItemImpl item = getItem(i);
            handleAddOperation(item);
            if (filter.accept(item, i)) {
                removeList.add(item);
            }
        }
        removeAll(removeList);
    }

    public <T extends ItemImpl> void removeBy(@NonNull Class<T> clz,
                                              @NonNull IFilter<T> filter) {
        List<ItemImpl> removeList = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            ItemImpl item = getItem(i);
            handleAddOperation(item);
            if (clz.isInstance(item) && filter.accept((T) item, i)) {
                removeList.add(item);
            }
        }
        removeAll(removeList);
    }

    /*private <T extends ItemImpl> void handleRemoveOperation(@NonNull Class<T> clz) {
        SingleSelection ss = singleSelectionMap.get(clz);
        if (ss != null) {
            ss.reset();
            return;
        }
        MultipleSelection ms = multipleSelectionMap.get(clz);
        if (ms != null) {
            ms.reset();
        }
    }*/
    public <T extends ItemImpl> void removeAll(@NonNull Class<T> clz) {

        removeBy(clz, new IFilter<T>() {
            @Override
            public boolean accept(@NotNull T t, int position) {
                return true;
            }
        });
    }

    public void clear() {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                for (SingleSelection ss : singleSelectionMap.values()) {
                    ss.reset();
                }
                for (MultipleSelection ms : multipleSelectionMap.values()) {
                    ms.reset();
                }
                currentItems.clear();
            }
        };
        syncNotify(callback);
    }

    public void filterRun(@NonNull IFilter filter, @NonNull IRun run) {
        for (int i = 0; i < getItemCount(); i++) {
            ItemImpl item = getItem(i);
            if (filter.accept(item, i)) {
                if (run.run(item, i)) {
                    break;
                }
            }
        }
    }

    public <T extends ItemImpl> void filterRun(@NonNull Class<T> clz, @NonNull IFilter<T> filter, @NonNull IRun<T> run) {
        for (int i = 0; i < getItemCount(); i++) {
            ItemImpl item = getItem(i);
            if (!clz.isInstance(item)) {
                continue;
            }
            T t = (T)item;
            if (filter.accept(t, i)) {
                if (run.run(t, i)) {
                    break;
                }
            }
        }
    }

    private void syncNotify(final Callback change) {
        if (notifyCallback != null) {
            notifyCallback.onNotifyStart();
        }
        oldItems = new ArrayList<>(currentItems);
        change.doChange();
        int oldSize = oldItems.size();
        int newSize = currentItems.size();
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        diffResult.dispatchUpdatesTo(this);
        onAfterChanged(oldSize, newSize);
        if (notifyCallback != null) {
            notifyCallback.onNotifyEnd();
        }
    }

    /*private synchronized  void asyncNotify(long delayMS, final Callback change, final FinishCallback finish) {
        if (notifyCallback != null) {
            notifyCallback.onNotifyStart();
        }
        Single.just(this)
                .subscribeOn(Schedulers.computation())
                .delay(delayMS, TimeUnit.MILLISECONDS)
                .map(new Function<AnyAdapter, Change>() {
                    @Override
                    public Change apply(AnyAdapter adapter) throws Exception {
                        oldItems = new ArrayList<>(currentItems);
                        change.doChange();
                        int oldSize = oldItems.size();
                        int newSize = currentItems.size();
                        return new Change(oldSize, newSize, DiffUtil.calculateDiff(diffCallback));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(new Consumer<Change>() {
                    @Override
                    public void accept(Change changeResult) throws Exception {
                        changeResult.diffResult.dispatchUpdatesTo(AnyAdapter.this);
                        onAfterChanged(changeResult.oldSize, changeResult.newSize);
                        if (notifyCallback != null) {
                            notifyCallback.onNotifyEnd();
                        }
                        if (finish != null) {
                            finish.onFinish();
                        }
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                })
                .subscribe();
    }*/

    public void onAfterChanged(int oldSize, int newSize) {

    }

    public <T extends ItemImpl> SingleSelection<T> singleSelectionFor(Class<T> clz) {
        SingleSelection ss;
        if (singleSelectionMap.containsKey(clz)) {
            ss = singleSelectionMap.get(clz);
        } else {
            ss = new SingleSelection<>(this, clz);
            singleSelectionMap.put(clz, ss);
        }
        //Remove multipleSelection, can only have one selection for one type data
        multipleSelectionMap.remove(clz);
        return ss;
    }

    public <T extends ItemImpl> MultipleSelection<T> multipleSelectionFor(Class<T> clz) {
        MultipleSelection ms;
        if (multipleSelectionMap.containsKey(clz)) {
            ms = multipleSelectionMap.get(clz);
        } else {
            ms = new MultipleSelection<>(this, clz);
            multipleSelectionMap.put(clz, ms);
        }
        //Remove singleSelection, can only have one selection for one type data
        singleSelectionMap.remove(clz);
        return ms;
    }

    private interface Callback {
        void doChange();
    }

    public interface NotifyCallback {
        void onNotifyStart();
        void onNotifyEnd();
    }

    private class Change {

        public int oldSize, newSize;
        public DiffUtil.DiffResult diffResult;

        public Change(int oldSize, int newSize, DiffUtil.DiffResult diffResult) {
            this.oldSize = oldSize;
            this.newSize = newSize;
            this.diffResult = diffResult;
        }
    }

    public interface IConverter<S, I extends ItemImpl<S>> {
        I convert(S s, int position);
    }

    public interface IFilter<T extends ItemImpl> {
        boolean accept(@NotNull T t, int position);
    }

    public interface IRun<T extends ItemImpl> {
        /**
         * @param t
         * @param position
         * @return break if after this method. true -> break; false -> not
         */
        boolean run(@NotNull T t, int position);
    }

}
