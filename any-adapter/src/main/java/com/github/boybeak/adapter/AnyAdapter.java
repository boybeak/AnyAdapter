package com.github.boybeak.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;

import com.github.boybeak.adapter.event.AbsOnClick;
import com.github.boybeak.adapter.event.AbsOnLongClick;
import com.github.boybeak.adapter.event.OnClick;
import com.github.boybeak.adapter.event.OnLongClick;
import com.github.boybeak.adapter.selector.MultipleSelector;
import com.github.boybeak.adapter.selector.SelectorArgs;
import com.github.boybeak.adapter.selector.SingleSelector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
    private Map<Class<? extends ItemImpl>, SingleSelector> singleSelectorMap = new HashMap<>();
    private Map<Class<? extends ItemImpl>, MultipleSelector> multipleSelectorMap = new HashMap<>();

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
        view.setId(R.id.any_adapter_holder_root);
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
                    click.onClick(v, item, position, AnyAdapter.this);
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
                    return longClick.onLongClick(v, item, position, AnyAdapter.this);
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
                if (o instanceof SelectorArgs) {
                    ItemImpl item = getItem(position);
                    if (!item.supportSelect()) {
                        continue;
                    }
                    SelectorArgs args = (SelectorArgs)o;
                    if (!args.getClz().isInstance(item)) {
                        continue;
                    }
                    switch (args.action()) {
                        case SelectorArgs.ACTION_STATE_CHANGE:
                            holder.onSelectedUpdate(item, args.value(), this);
                            break;
                        case SelectorArgs.ACTION_MODE_CHANGE:
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

    public int getItemViewTypeCount() {
        return typeMap.size();
    }

    public boolean isDataSingleType() {
        return getItemViewTypeCount() <= 1;
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

    public int countOf(@NonNull Class<? extends ItemImpl> clz) {
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

    public <T extends ItemImpl> void notifyItemChanged(T t) {
        notifyItemChanged(t, null);
    }

    public <T extends ItemImpl> void notifyItemChanged(T t, @NonNull Object payloads) {
        int index = indexOf(t);
        if (index >= 0 && index <= getItemCount()) {
            notifyItemChanged(index, payloads);
        }
    }

    public int indexOf(@NonNull ItemImpl item) {
        return currentItems.indexOf(item);
    }

    public int firstIndexOf(@NonNull Class<? extends ItemImpl> clz) {
        final int size = currentItems.size();
        for (int i = 0; i < size; i++) {
            if (clz.isInstance(currentItems.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public int firstIndexBy(@NonNull IFilter<ItemImpl> filter) {
        final int size = currentItems.size();
        for (int i = 0; i < size; i++) {
            if (filter.accept(getItem(i), i)) {
                return i;
            }
        }
        return -1;
    }

    public <T extends ItemImpl> int firstIndexBy(Class<T> clz, @NonNull IFilter<T> filter) {
        final int size = currentItems.size();
        for (int i = 0; i < size; i++) {
            ItemImpl item = getItem(i);
            if (clz.isInstance(item) && filter.accept((T)item, i)) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(@NonNull Class<? extends ItemImpl> clz) {
        final int size = currentItems.size();
        for (int i = size - 1; i >= 0; i--) {
            if (clz.isInstance(currentItems.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexBy(@NonNull IFilter<ItemImpl> filter) {
        final int size = currentItems.size();
        for (int i = size - 1; i >= 0; i--) {
            if (filter.accept(getItem(i), i)) {
                return i;
            }
        }
        return -1;
    }

    public <T extends ItemImpl> int lastIndexBy(Class<T> clz, @NonNull IFilter<T> filter) {
        final int size = currentItems.size();
        for (int i = size - 1; i >= 0; i--) {
            ItemImpl item = getItem(i);
            if (clz.isInstance(item) && filter.accept((T)item, i)) {
                return i;
            }
        }
        return -1;
    }

    public <T extends ItemImpl> boolean contains(T t) {
        return currentItems.contains(t);
    }

    public <T extends ItemImpl> List<T> getItemsAs(@NonNull Class<T> clz) {
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

    public <T extends ItemImpl> void setOnClickFor(@NonNull Class<T> clz, @NonNull OnClick<T> click) {
        clickMap.put(clz, click);
    }

    public <T extends ItemImpl> void setOnClickFor(@NonNull AbsOnClick<T> absClick) {
        setOnClickFor(absClick.getItemClass(), absClick);
    }

    public <T extends ItemImpl> void setOnLongClickFor(@NonNull Class<T> clz, @NonNull OnLongClick<T> click) {
        longClickMap.put(clz, click);
    }

    public <T extends ItemImpl> void setOnLongClickFor(@NonNull AbsOnLongClick<T> absLongClick) {
        setOnLongClickFor(absLongClick.getItemClass(), absLongClick);
    }

    private <T extends ItemImpl> boolean isSelectableFor(Class<T> clz) {
        SingleSelector ss = singleSelectorMap.get(clz);
        if (ss != null) {
            return ss.isInSelectMode();
        }
        MultipleSelector ms = multipleSelectorMap.get(clz);
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

    public <T extends ItemImpl> void add(final int position, @NonNull final T item) {
        syncNotify(new Callback() {
            @Override
            public void doChange() {
                handleAddOperation(item);
                currentItems.add(position, item);
            }
        });
    }

    public <T extends ItemImpl> void add(@NonNull final T item) {
        syncNotify(new Callback() {
            @Override
            public void doChange() {
                handleAddOperation(item);
                currentItems.add(item);
            }
        });
    }

    public <S, T extends ItemImpl> void add(@NonNull S s, @NonNull IConverter<S, T> converter) {
        addAll(converter.convert(s));
    }

    public <S, T extends ItemImpl> void add(int position, @NonNull S s, @NonNull IConverter<S, T> converter) {
        addAll(position, converter.convert(s));
    }

    public <T extends ItemImpl> void replace(final int position, @NonNull final T item) {
        syncNotify(new Callback() {
            @Override
            public void doChange() {
                handleAddOperation(item);
                currentItems.set(position, item);
            }
        });
    }

    public <T extends ItemImpl> void addAll(@NonNull final Collection<T> items) {
        syncNotify(new Callback() {
            @Override
            public void doChange() {
                for (T t : items) {
                    handleAddOperation(t);
                }
                currentItems.addAll(items);
            }
        });
    }

    public <T extends ItemImpl> void addAll(final int position, @NonNull final Collection<T> items) {
        syncNotify(new Callback() {
            @Override
            public void doChange() {
                for (T t : items) {
                    handleAddOperation(t);
                }
                currentItems.addAll(position, items);
            }
        });
    }

    private <S, T extends ItemImpl<S>> List<T> doConvert(@NonNull List<S> sources, @NonNull IEachConverter<S, T> converter) {
        List<T> items = new ArrayList<>(sources.size());
        for (int i = 0; i < sources.size(); i++) {
            S s = sources.get(i);
            T t = converter.convert(s, i);
            handleAddOperation(t);
            items.add(t);
        }
        return items;
    }

    public <S, T extends ItemImpl<S>> void addAll(int position, @NonNull List<S> sources, @NonNull IEachConverter<S, T> converter) {
        addAll(position, doConvert(sources, converter));
    }

    public <S, T extends ItemImpl<S>> void addAll(@NonNull List<S> sources, @NonNull IEachConverter<S, T> converter) {
        addAll(doConvert(sources, converter));
    }

    private <T extends ItemImpl> void handleRemoveOperation(T t) {
        if (!t.supportSelect()) {
            return;
        }
        SingleSelector ss = singleSelectorMap.get(t.getClass());
        if (ss != null) {
            if (t.equals(ss.getSelectedItem())) {
                ss.select(t);
            }
            return;
        }
        MultipleSelector ms = multipleSelectorMap.get(t.getClass());
        if (ms != null) {
            if (ms.isSelected(t)) {
                ms.select(t);
            }
        }
    }

    public <T extends ItemImpl> void remove(final T item) {
        syncNotify(new Callback() {
            @Override
            public void doChange() {
                handleRemoveOperation(item);
                currentItems.remove(item);
            }
        });
    }

    public void remove(final int position) {
        syncNotify(new Callback() {
            @Override
            public void doChange() {
                handleRemoveOperation(getItem(position));
                currentItems.remove(position);
            }
        });
    }

    public <T extends ItemImpl> void removeAll(@NonNull final Collection<T> items) {
        syncNotify(new Callback() {
            @Override
            public void doChange() {
                for (T t : items) {
                    handleAddOperation(t);
                }
                currentItems.removeAll(items);
            }
        });
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
            public boolean accept(@NonNull T t, int position) {
                return true;
            }
        });
    }

    public void clear() {
        syncNotify(new Callback() {
            @Override
            public void doChange() {
                for (SingleSelector ss : singleSelectorMap.values()) {
                    ss.reset();
                }
                for (MultipleSelector ms : multipleSelectorMap.values()) {
                    ms.reset();
                }
                currentItems.clear();
            }
        });
    }

    public void sortBy(final Comparator<ItemImpl> comparator) {
        syncNotify(new Callback() {
            @Override
            public void doChange() {
                Collections.sort(currentItems, comparator);
            }
        });
    }

    public <T extends ItemImpl> boolean contains(@NonNull Class<T> clz) {
        for (int i = 0; i < getItemCount(); i++) {
            if (clz.isInstance(getItem(i))) {
                return true;
            }
        }
        return false;
    }

    public <T extends ItemImpl> void sortBy(final Class<T> clz, final Comparator<T> comparator) {
        if (!isDataSingleType()) {
            throw new IllegalStateException("This adapter contains more than one data type that can not compare, typeMap=" + typeMap);
        }
        if (!this.contains(clz)) {
            return;
        }
        List<T> ts = getItemsAs(clz);
        Collections.sort(ts, comparator);
        mergeFrom((List<ItemImpl>) ts);
    }

    private void mergeFrom(final List<ItemImpl> mergeList) {
        syncNotify(new Callback() {
            @Override
            public void doChange() {
                currentItems.clear();
                currentItems.addAll(mergeList);
            }
        });
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

    /**
     * Only one selector can be assigned for @param clz, obtain SingleSelector will remove MultipleSelector
     * @param clz
     * @param <T>
     * @return
     */
    public <T extends ItemImpl> SingleSelector<T> singleSelectorFor(@NonNull Class<T> clz) {
        SingleSelector ss;
        if (singleSelectorMap.containsKey(clz)) {
            ss = singleSelectorMap.get(clz);
        } else {
            ss = new SingleSelector<>(this, clz);
            singleSelectorMap.put(clz, ss);
        }
        //Remove multipleSelection, can only have one selection for one type data
        multipleSelectorMap.remove(clz);
        return ss;
    }

    /**
     * Only one selector can be assigned  for @param clz, obtain MultipleSelector will remove SingleSelector
     * @param clz
     * @param <T>
     * @return
     */
    public <T extends ItemImpl> MultipleSelector<T> multipleSelectorFor(@NonNull Class<T> clz) {
        MultipleSelector ms;
        if (multipleSelectorMap.containsKey(clz)) {
            ms = multipleSelectorMap.get(clz);
        } else {
            ms = new MultipleSelector<>(this, clz);
            multipleSelectorMap.put(clz, ms);
        }
        //Remove singleSelection, can only have one selection for one type data
        singleSelectorMap.remove(clz);
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

    public interface IEachConverter<S, I extends ItemImpl<S>> {
        I convert(@NonNull S s, int position);
    }

    public interface IConverter<S, I extends ItemImpl> {
        @NonNull
        List<I> convert(@NonNull S s);
    }

    public interface IFilter<T extends ItemImpl> {
        boolean accept(@NonNull T t, int position);
    }

    public interface IRun<T extends ItemImpl> {
        /**
         * @param t
         * @param position
         * @return break if after this method. true -> break; false -> not
         */
        boolean run(@NonNull T t, int position);
    }

}
