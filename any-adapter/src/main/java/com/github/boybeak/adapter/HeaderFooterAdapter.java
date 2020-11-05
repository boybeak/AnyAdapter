package com.github.boybeak.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.github.boybeak.adapter.event.OnClick;
import com.github.boybeak.adapter.event.OnLongClick;
import com.github.boybeak.adapter.footer.Footer;

public class HeaderFooterAdapter<H extends ItemImpl<?>, F extends ItemImpl<Footer>> extends FooterAdapter<F> {

    private H headerItem;
    private OnClick<H> headerClick;
    private OnLongClick<H> headerLongClick;
    private boolean hideHeader = false;

    public HeaderFooterAdapter(@NonNull H headerItem, @NonNull F item, @NonNull NotifyCallback callback) {
        super(item, callback);
        this.headerItem = headerItem;
        initHeader(headerItem);
    }

    public HeaderFooterAdapter(@NonNull H headerItem, @NonNull F item) {
        super(item);
        this.headerItem = headerItem;
        initHeader(headerItem);
    }

    private void initHeader(H headerItem) {
        putType(headerItem.layoutId(), headerItem.holderClass());
    }

    @Override
    public void onBindViewHolder(@NonNull AbsHolder holder, final int position) {
        if (!hideHeader && position == 0) {
            holder.onBind(headerItem, position, this);

            if (headerClick != null) {
                int[] ids = headerClick.getClickableIds();
                if (ids == null || ids.length == 0) {
                    return;
                }
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        headerClick.onClick(v, headerItem, position, HeaderFooterAdapter.this);
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
            if (headerLongClick != null) {
                int[] ids = headerLongClick.getLongClickableIds();
                if (ids == null || ids.length == 0) {
                    return;
                }
                View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return headerLongClick.onLongClick(v, headerItem, position, HeaderFooterAdapter.this);
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

            holder.itemView.setVisibility(hideHeader ? View.GONE : View.VISIBLE);
        } else {
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        if (!hideHeader) {
            itemCount += 1;
        }
        return itemCount;
    }

    @Override
    public ItemImpl<?> getItem(int position) {
        if (!hideHeader && position == 0) {
            return headerItem;
        }

        return super.getItem(position - (hideHeader ? 0 : 1));
    }

    @Override
    public int getItemViewType(int position) {
        if (!hideHeader && position == 0) {
            return headerItem.layoutId();
        }
        return super.getItemViewType(position);
    }

    public int getItemViewTypeCountIgnoreHeaderAndFooter() {
        return super.getItemCountIgnoreFooter();
    }

    @Override
    public boolean isDataSingleType() {
        return getItemViewTypeCountIgnoreHeaderAndFooter() <= 1;
    }

    @Override
    public boolean isItemsEmpty() {
        return getItemViewTypeCountIgnoreHeaderAndFooter() == 0;
    }

    public void setHeaderClick(OnClick<H> click) {
        headerClick = click;
        notifyHeader();
    }

    public void setHeaderLongClick(OnLongClick<H> longClick) {
        headerLongClick = longClick;
        notifyHeader();
    }

    public void notifyHeader() {
        if (hideHeader) {
            return;
        }
        notifyItemChanged(0);
    }

    public void hideHeader() {
        if (hideHeader) {
            return;
        }
        hideHeader = true;
        notifyItemRemoved(0);
    }

    public void showHeader() {
        if (!hideHeader) {
            return;
        }
        hideHeader = false;
        notifyItemInserted(0);
    }

    public H getHeaderItem() {
        return headerItem;
    }

    public boolean isHeaderHidden() {
        return hideHeader;
    }
}
