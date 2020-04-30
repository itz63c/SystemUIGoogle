package com.android.systemui.statusbar.notification.row;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

/* compiled from: ChannelEditorDialogController.kt */
public final class ChannelEditorDialogController$initDialog$$inlined$apply$lambda$1 implements OnDismissListener {
    final /* synthetic */ ChannelEditorDialogController this$0;

    ChannelEditorDialogController$initDialog$$inlined$apply$lambda$1(ChannelEditorDialogController channelEditorDialogController) {
        this.this$0 = channelEditorDialogController;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        OnChannelEditorDialogFinishedListener onFinishListener = this.this$0.getOnFinishListener();
        if (onFinishListener != null) {
            onFinishListener.onChannelEditorDialogFinished();
        }
    }
}
