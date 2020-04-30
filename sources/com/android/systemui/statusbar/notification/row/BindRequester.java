package com.android.systemui.statusbar.notification.row;

import androidx.core.p002os.CancellationSignal;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline.BindCallback;

public abstract class BindRequester {
    private BindRequestListener mBindRequestListener;

    public interface BindRequestListener {
        void onBindRequest(NotificationEntry notificationEntry, CancellationSignal cancellationSignal, BindCallback bindCallback);
    }

    public final CancellationSignal requestRebind(NotificationEntry notificationEntry, BindCallback bindCallback) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        BindRequestListener bindRequestListener = this.mBindRequestListener;
        if (bindRequestListener != null) {
            bindRequestListener.onBindRequest(notificationEntry, cancellationSignal, bindCallback);
        }
        return cancellationSignal;
    }

    /* access modifiers changed from: 0000 */
    public final void setBindRequestListener(BindRequestListener bindRequestListener) {
        this.mBindRequestListener = bindRequestListener;
    }
}
