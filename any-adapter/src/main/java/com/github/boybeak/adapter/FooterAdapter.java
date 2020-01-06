package com.github.boybeak.adapter;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.github.boybeak.adapter.footer.Footer;

import org.jetbrains.annotations.NotNull;

public class FooterAdapter extends AnyAdapter {

    private Footer footer = null;
    private ItemImpl<Footer> footerItem;

    private View.OnClickListener footerClick;

    public FooterAdapter(@NotNull AbsItem<Footer> item, @NotNull NotifyCallback callback) {
        super(callback);
        init(item);
    }

    public FooterAdapter(@NotNull AbsItem<Footer> item) {
        super();
        init(item);
    }

    private void init(AbsItem<Footer> footerItem) {
        footer = footerItem.source();
        this.footerItem = footerItem;
        putType(footerItem.layoutId(), footerItem.holderClass());
    }

    @Override
    public void onBindViewHolder(@NonNull AbsHolder holder, int position) {
        if (position < getItemCountIgnoreFooter()) {
            super.onBindViewHolder(holder, position);
        } else {
            holder.onBind(footerItem, position, this);
            holder.itemView.setOnClickListener(footerClick);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getItemCountIgnoreFooter()) {
            return super.getItemViewType(position);
        } else {
            return footerItem.layoutId();
        }
    }

    @Override
    public ItemImpl getItem(int position) {
        if (position < getItemCountIgnoreFooter()) {
            return super.getItem(position);
        } else {
            return footerItem;
        }
    }

    public boolean isItemsEmpty() {
        return getItemCountIgnoreFooter() == 0;
    }

    public int getItemCountIgnoreFooter() {
        return super.getItemCount();
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    public void notifyFooter(int state, int icon, String msg) {
        footer.setState(state);
        footer.setIcon(icon);
        footer.setMessage(msg);
        notifyItemChanged(getItemCount() - 1);
    }

    public void replaceFooterItem(ItemImpl<Footer> newFooterItem) {
        newFooterItem.source().setState(footer.getState());
        footer = newFooterItem.source();
        footerItem = newFooterItem;
        putType(newFooterItem.layoutId(), newFooterItem.holderClass());
        notifyFooter();
    }

    public void notifyFooter() {
        notifyFooter(footer.getState(), footer.getIcon(), footer.getMessage());
    }

    public void notifyLoading() {
        notifyFooter(Footer.LOADING, 0, "");
    }

    public void notifyLoading(Context context, @StringRes int textRes) {
        notifyFooter(Footer.LOADING, 0, context.getString(textRes));
    }

    public void notifySuccess(String msg) {
        notifyFooter(Footer.SUCCESS, 0, msg);
    }

    public void notifyNoOne(String msg) {
        notifyFooter(Footer.NO_ONE, 0, msg);
    }

    public void notifyNoMore(String msg) {
        notifyFooter(Footer.NO_MORE, 0, msg);
    }

    public void notifyEmpty(String msg) {
        if (isItemsEmpty()) {
            notifyNoOne(msg);
        } else {
            notifyNoMore(msg);
        }
    }

    public void notifyFailed(String msg) {
        notifyFooter(Footer.FAILED, 0, msg);
    }

    public void setFooterSummary(String summary) {
        footer.setSummary(summary);
        notifyFooter();
    }

    public void setFooterClick(View.OnClickListener footerClick) {
        this.footerClick = footerClick;
        notifyFooter();
    }

    public boolean isLoading() {
        return footer.getState() == Footer.LOADING;
    }

}
