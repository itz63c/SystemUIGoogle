package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfo;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfoListener;
import java.util.Optional;
import java.util.function.Consumer;

final class KeyboardMonitor implements Callbacks, ConfigInfoListener {
    private final Context mContext;
    private PendingIntent mOnKeyboardShowingChanged;
    private boolean mShowing;

    KeyboardMonitor(Context context, Optional<CommandQueue> optional) {
        this.mContext = context;
        optional.ifPresent(new Consumer() {
            public final void accept(Object obj) {
                KeyboardMonitor.this.lambda$new$0$KeyboardMonitor((CommandQueue) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$KeyboardMonitor(CommandQueue commandQueue) {
        commandQueue.addCallback((Callbacks) this);
    }

    public void setImeWindowStatus(int i, IBinder iBinder, int i2, int i3, boolean z) {
        boolean z2 = this.mShowing;
        boolean z3 = (i2 & 2) != 0;
        this.mShowing = z3;
        if (z3 != z2) {
            trySendKeyboardShowing();
        }
    }

    public void onConfigInfo(ConfigInfo configInfo) {
        PendingIntent pendingIntent = this.mOnKeyboardShowingChanged;
        PendingIntent pendingIntent2 = configInfo.onKeyboardShowingChange;
        if (pendingIntent != pendingIntent2) {
            this.mOnKeyboardShowingChanged = pendingIntent2;
            trySendKeyboardShowing();
        }
    }

    private void trySendKeyboardShowing() {
        if (this.mOnKeyboardShowingChanged != null) {
            Intent intent = new Intent();
            intent.putExtra("is_keyboard_showing", this.mShowing);
            try {
                this.mOnKeyboardShowingChanged.send(this.mContext, 0, intent);
            } catch (CanceledException e) {
                Log.e("KeyboardMonitor", "onKeyboardShowingChanged pending intent cancelled", e);
            }
        }
    }
}
