package com.github.boybeak.adapter;

import androidx.annotation.NonNull;

import com.github.boybeak.adapter.footer.Footer;

public class AutoFooterAdapter extends FooterAdapter {

    private FooterMsgCallback msgCallback = null;

    public AutoFooterAdapter() {
        super();
    }

    public AutoFooterAdapter(NotifyCallback callback) {
        super(callback);
    }

    public AutoFooterAdapter(AbsItem<Footer> item, NotifyCallback callback) {
        super(item, callback);
    }

    public AutoFooterAdapter(AbsItem<Footer> item) {
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

    public void setFooterMsgCallback(FooterMsgCallback callback) {
        msgCallback = callback;
    }

    public interface FooterMsgCallback {
        String msgForNoOne(@NonNull AutoFooterAdapter adapter);
        String msgForNoMore(@NonNull AutoFooterAdapter adapter);
        String msgForSuccess(@NonNull AutoFooterAdapter adapter);
        String msgForOthers(@NonNull AutoFooterAdapter adapter, int state);
    }

}
