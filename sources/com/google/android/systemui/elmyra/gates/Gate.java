package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.os.Handler;

public abstract class Gate {
    private boolean mActive = false;
    private final Context mContext;
    private Listener mListener;
    private final Handler mNotifyHandler;

    public interface Listener {
        void onGateChanged(Gate gate);
    }

    /* access modifiers changed from: protected */
    public abstract boolean isBlocked();

    /* access modifiers changed from: protected */
    public abstract void onActivate();

    /* access modifiers changed from: protected */
    public abstract void onDeactivate();

    public Gate(Context context) {
        this.mContext = context;
        this.mNotifyHandler = new Handler(context.getMainLooper());
    }

    public void activate() {
        if (!isActive()) {
            this.mActive = true;
            onActivate();
        }
    }

    public void deactivate() {
        if (isActive()) {
            this.mActive = false;
            onDeactivate();
        }
    }

    public final boolean isActive() {
        return this.mActive;
    }

    public final boolean isBlocking() {
        return isActive() && isBlocked();
    }

    /* access modifiers changed from: protected */
    public void notifyListener() {
        if (isActive() && this.mListener != null) {
            this.mNotifyHandler.post(new Runnable() {
                public final void run() {
                    Gate.this.lambda$notifyListener$0$Gate();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$notifyListener$0 */
    public /* synthetic */ void lambda$notifyListener$0$Gate() {
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onGateChanged(this);
        }
    }

    /* access modifiers changed from: protected */
    public Context getContext() {
        return this.mContext;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}
