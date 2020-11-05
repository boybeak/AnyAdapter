package com.github.boybeak.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.github.boybeak.adapter.event.OnClick;
import com.github.boybeak.adapter.event.OnLongClick;
import com.github.boybeak.adapter.footer.Footer;

public class FooterAdapter<FooterItem extends ItemImpl<Footer>> extends AnyAdapter {

    private Footer footer = null;
    private FooterItem footerItem;

    private OnClick<FooterItem> footerClick;
    private OnLongClick<FooterItem> footerLongClick;
    private boolean hideFooter = false;

    public FooterAdapter(@NonNull FooterItem item, @NonNull NotifyCallback callback) {
        super(callback);
        init(item);
    }

    public FooterAdapter(@NonNull FooterItem item) {
        super();
        init(item);
    }

    private void init(FooterItem footerItem) {
        footer = footerItem.source();
        this.footerItem = footerItem;
        putType(footerItem.layoutId(), footerItem.holderClass());
    }

    @Override
    public void onBindViewHolder(@NonNull AbsHolder holder, final int position) {
        if (!hideFooter && position == getItemCount() - 1) {
            holder.onBind(footerItem, position, this);

            if (footerClick != null) {
                int[] ids = footerClick.getClickableIds();
                if (ids == null || ids.length == 0) {
                    return;
                }
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        footerClick.onClick(v, footerItem, position, FooterAdapter.this);
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
            if (footerLongClick != null) {
                int[] ids = footerLongClick.getLongClickableIds();
                if (ids == null || ids.length == 0) {
                    return;
                }
                View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return footerLongClick.onLongClick(v, footerItem, position, FooterAdapter.this);
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

            holder.itemView.setVisibility(hideFooter ? View.GONE : View.VISIBLE);
        } else {
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!hideFooter && position == getItemCount() - 1) {
            return footerItem.layoutId();
        } else {
            return super.getItemViewType(position);
        }
        /*if (position < getItemCountIgnoreFooter()) {

        } else if (!hideFooter) {

        } else {

        }*/
//        throw new IllegalStateException("Unreachable position(" + position + ")");
    }

    public int getItemViewTypeCountIgnoreFooter() {
        return super.getItemViewTypeCount() - 1;
    }

    @Override
    public boolean isDataSingleType() {
        return getItemViewTypeCountIgnoreFooter() <= 1;
    }

    @Override
    public ItemImpl<?> getItem(int position) {
        Log.v(FooterAdapter.class.getSimpleName(), "itemCount=" + getItemCount());
        if (!hideFooter && position == getItemCount() - 1) {
            return footerItem;
        } else {
            Log.v(FooterAdapter.class.getSimpleName(), "position=" + position);
            return super.getItem(position);
        }
        /*if (position < getItemCountIgnoreFooter()) {

        } else if (!hideFooter) {

        }*/
//        throw new IllegalStateException("Unreachable position(" + position + ")");
    }

    public boolean isItemsEmpty() {
        return getItemCountIgnoreFooter() == 0;
    }

    public int getItemCountIgnoreFooter() {
        return super.getItemCount();
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        if (!hideFooter) {
            itemCount += 1;
        }
        return itemCount;
    }

    public void notifyFooter(int state, int icon, String msg) {
        if (hideFooter) {
            return;
        }
        footer.setState(state);
        footer.setIcon(icon);
        footer.setMessage(msg);
        notifyItemChanged(getItemCount() - 1);
    }

    /*public void replaceFooterItem(ItemImpl<Footer> newFooterItem) {
        newFooterItem.source().setState(footer.getState());
        footer = newFooterItem.source();
        footerItem = newFooterItem;
        putType(newFooterItem.layoutId(), newFooterItem.holderClass());
        notifyFooter();
    }*/

    public void notifyFooter() {
        notifyFooter(footer.getState(), footer.getIcon(), footer.getMessage());
    }

    public void notifyLoading() {
        notifyFooter(Footer.LOADING, footer.getIcon(), "");
    }

    public void notifyLoading(Context context, @StringRes int textRes) {
        notifyFooter(Footer.LOADING, footer.getIcon(), context.getString(textRes));
    }

    public void notifySuccess(String msg) {
        notifyFooter(Footer.SUCCESS, footer.getIcon(), msg);
    }

    public void notifyNoOne(String msg) {
        notifyFooter(Footer.NO_ONE, footer.getIcon(), msg);
    }

    public void notifyNoMore(String msg) {
        notifyFooter(Footer.NO_MORE, footer.getIcon(), msg);
    }

    public void notifyEmpty(String msg) {
        if (isItemsEmpty()) {
            notifyNoOne(msg);
        } else {
            notifyNoMore(msg);
        }
    }

    public void notifyFailed(String msg) {
        notifyFooter(Footer.FAILED, footer.getIcon(), msg);
    }

    public void setFooterSummary(String summary) {
        footer.setSummary(summary);
        notifyFooter();
    }

    /*public void setFooterClick(View.OnClickListener footerClick) {
        this.footerClick = footerClick;
        notifyFooter();
    }*/

    public void setFooterClick(OnClick<FooterItem> click) {
        footerClick = click;
        notifyFooter();
    }

    public void setFooterLongClick(OnLongClick<FooterItem> longClick) {
        footerLongClick = longClick;
        notifyFooter();
    }

    public boolean isLoading() {
        return footer.getState() == Footer.LOADING;
    }

    public Footer getFooter() {
        return footer;
    }

    public FooterItem getFooterItem() {
        return footerItem;
    }

    public void hideFooter() {
        if (hideFooter) {
            return;
        }
        hideFooter = true;
        notifyItemRemoved(getItemCount() - 1);
    }

    public void showFooter() {
        if (!hideFooter) {
            return;
        }
        hideFooter = false;
        notifyItemInserted(getItemCount());
    }

    public boolean isFooterHidden() {
        return hideFooter;
    }

}
