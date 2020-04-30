package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.os.Handler;

abstract class TransientGate extends Gate {
    private final long mBlockDuration;
    /* access modifiers changed from: private */
    public boolean mIsBlocking;
    private final Runnable mResetGate = new Runnable() {
        public void run() {
            TransientGate.this.mIsBlocking = false;
            TransientGate.this.notifyListener();
        }
    };
    private final Handler mResetGateHandler;

    TransientGate(Context context, long j) {
        super(context);
        this.mBlockDuration = j;
        this.mResetGateHandler = new Handler(context.getMainLooper());
    }

    /* access modifiers changed from: protected */
    public void block() {
        this.mIsBlocking = true;
        notifyListener();
        this.mResetGateHandler.removeCallbacks(this.mResetGate);
        this.mResetGateHandler.postDelayed(this.mResetGate, this.mBlockDuration);
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return this.mIsBlocking;
    }
}
