package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Gate.kt */
public abstract class Gate {
    private boolean active;
    private final Context context;
    private Listener listener;
    private final Handler notifyHandler = new Handler(Looper.getMainLooper());

    /* compiled from: Gate.kt */
    public interface Listener {
        void onGateChanged(Gate gate);
    }

    /* access modifiers changed from: protected */
    public abstract boolean isBlocked();

    /* access modifiers changed from: protected */
    public abstract void onActivate();

    /* access modifiers changed from: protected */
    public abstract void onDeactivate();

    public Gate(Context context2) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        this.context = context2;
    }

    public final Context getContext() {
        return this.context;
    }

    public final boolean getActive() {
        return this.active;
    }

    public Listener getListener() {
        return this.listener;
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }

    public void activate() {
        if (!this.active) {
            this.active = true;
            onActivate();
        }
    }

    public void deactivate() {
        if (this.active) {
            this.active = false;
            onDeactivate();
        }
    }

    public boolean isBlocking() {
        return this.active && isBlocked();
    }

    /* access modifiers changed from: protected */
    public final void notifyListener() {
        if (this.active && getListener() != null) {
            this.notifyHandler.post(new Gate$notifyListener$1(this));
        }
    }

    public String toString() {
        String simpleName = getClass().getSimpleName();
        Intrinsics.checkExpressionValueIsNotNull(simpleName, "javaClass.simpleName");
        return simpleName;
    }
}
