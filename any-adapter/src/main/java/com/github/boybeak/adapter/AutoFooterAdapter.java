package com.github.boybeak.adapter;

import androidx.annotation.NonNull;

import com.github.boybeak.adapter.footer.Footer;

import androidx.annotation.NonNull;

public class AutoFooterAdapter<FooterItem extends ItemImpl<Footer>> extends FooterAdapter<FooterItem> {

    private FooterMsgCallback<FooterItem> msgCallback = null;

    public AutoFooterAdapter(@NonNull FooterItem item, @NonNull NotifyCallback callback) {
        super(item, callback);
    }

    public AutoFooterAdapter(@NonNull FooterItem item) {
        super(item);
    }

    /**
     * @param oldSize size ignore footer
     * @param newSize size ignore footer
     */
    @Override
    public void onAfterChanged(int oldSize, int newSize) {
        if (newSize == 0) {
            notifyNoOne(onGetFooterMsg(Footer.NO_ONE));
            return;
        }
        if (newSize == oldSize) {
            notifyNoMore(onGetFooterMsg(Footer.NO_MORE));
        } else {
            notifySuccess(onGetFooterMsg(Footer.SUCCESS));
        }
    }

    private String onGetFooterMsg(@Footer.State int state) {
        if (msgCallback == null) {
            return "";
        }
        switch (state) {
            case Footer.NO_ONE:
                return msgCallback.msgForNoOne(this);
            case Footer.NO_MORE:
                return msgCallback.msgForNoMore(this);
            case Footer.SUCCESS:
                return msgCallback.msgForSuccess(this);
        }
        return msgCallback.msgForOthers(this, state);
    }

    public void setFooterMsgCallback(FooterMsgCallback<FooterItem> callback) {
        msgCallback = callback;
        notifyFooter();
    }

    public interface FooterMsgCallback<FooterItem extends ItemImpl<Footer>> {
        String msgForNoOne(@NonNull AutoFooterAdapter<FooterItem> adapter);
        String msgForNoMore(@NonNull AutoFooterAdapter<FooterItem> adapter);
        String msgForSuccess(@NonNull AutoFooterAdapter<FooterItem> adapter);
        String msgForOthers(@NonNull AutoFooterAdapter<FooterItem> adapter, int state);
    }

}
