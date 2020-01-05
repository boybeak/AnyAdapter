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

import org.jetbrains.annotations.NotNull;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AnyAdapter extends RecyclerView.Adapter<AbsHolder> {

    private SparseArray<Class<? extends AbsHolder>> typeMap = new SparseArray<>();

    private List<ItemImpl> oldItems = null;
    private List<ItemImpl> currentItems = new ArrayList<>();

    private Map<Class<? extends ItemImpl>, OnClick> clickMap = new HashMap<>();
    private Map<Class<? extends ItemImpl>, OnLongClick> longClickMap = new HashMap<>();

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

    public ItemImpl getItem(int position) {
        return currentItems.get(position);
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

    public void add(int position, ItemImpl item) {
        add(position, item, null);
    }
    public void add(final int position, final ItemImpl item, FinishCallback finish) {
        add(false, 0, position, item, finish);
    }

    public void addAsync(long delayMS, final int position, final ItemImpl item, FinishCallback finish) {
        add(true, delayMS, position, item, finish);
    }

    public void add(boolean async, long delay, final int position, final ItemImpl item, FinishCallback finish) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                currentItems.add(position, item);
            }
        };
        if (async) {
            asyncNotify(delay, callback, finish);
        } else {
            syncNotify(callback, finish);
        }
    }

    public void add(ItemImpl item) {
        add(item, null);
    }
    public void add(final ItemImpl item, FinishCallback finish) {
        add(false, 0, item, finish);
    }
    public void addAsync(long delayMS, final ItemImpl item, FinishCallback finish) {
        add(true, delayMS, item, finish);
    }
    public void add(boolean async, long delayMS, final ItemImpl item, FinishCallback finish) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                currentItems.add(item);
            }
        };
        if (async) {
            asyncNotify(delayMS, callback, finish);
        } else {
            syncNotify(callback, finish);
        }
    }

    public void addAll(Collection<ItemImpl> items) {
        addAll(items, null);
    }
    public void addAll(final Collection<ItemImpl> items, FinishCallback finish) {
        addAll(false, 0, items, finish);
    }
    public void addAllAsync(long delayMS, final Collection<ItemImpl> items, FinishCallback finish) {
        addAll(true, delayMS, items, finish);
    }
    public void addAll(boolean async, long delayMS, final Collection<ItemImpl> items, FinishCallback finish) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                currentItems.addAll(items);
            }
        };
        if (async) {
            asyncNotify(delayMS, callback, finish);
        } else {
            syncNotify(callback, finish);
        }
    }

    public void remove(final ItemImpl item) {
        remove(item, null);
    }

    public void remove(final ItemImpl item, FinishCallback finish) {
        remove(item, false, finish);
    }

    public void remove(final ItemImpl item, boolean async, FinishCallback finish) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                currentItems.remove(item);
            }
        };
        if (async) {
            asyncNotify(0, callback, finish);
        } else {
            syncNotify(callback, finish);
        }
    }

    public void remove(final int position) {
        remove(position, null);
    }

    public void remove(final int position, FinishCallback finish) {
        remove(position, false, finish);
    }

    public void remove(final int position, boolean async, FinishCallback finish) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                currentItems.remove(position);
            }
        };
        if (async) {
            asyncNotify(0, callback, finish);
        } else {
            syncNotify(callback, finish);
        }
    }

    public void clear() {
        clear(false, null);
    }

    public void clear(boolean async) {
        clear(async, null);
    }

    public void clear(FinishCallback finish) {
        clear(false, finish);
    }

    public void clear(boolean async, FinishCallback finish) {
        Callback callback = new Callback() {
            @Override
            public void doChange() {
                currentItems.clear();
            }
        };
        if (async) {
            asyncNotify(0, callback, finish);
        } else {
            syncNotify(callback, finish);
        }

    }

    public void filterRun(IFilter filter, IRun run) {
        for (int i = 0; i < getItemCount(); i++) {
            ItemImpl item = getItem(i);
            if (filter.accept(item, i)) {
                if (run.run(item, i)) {
                    break;
                }
            }
        }
    }

    public <T extends ItemImpl> void filterRun(Class<T> clz, IFilter<T> filter, IRun<T> run) {
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

    private void syncNotify(final Callback change, final FinishCallback finish) {
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
        if (finish != null) {
            finish.onFinish();
        }
    }

    private synchronized  void asyncNotify(long delayMS, final Callback change, final FinishCallback finish) {
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
    }

    public void onAfterChanged(int oldSize, int newSize) {

    }

    private interface Callback {
        void doChange();
    }

    public interface NotifyCallback {
        void onNotifyStart();
        void onNotifyEnd();
    }

    public interface FinishCallback {
        void onFinish();
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
