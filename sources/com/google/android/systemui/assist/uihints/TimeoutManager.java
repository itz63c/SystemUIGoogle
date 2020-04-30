package com.google.android.systemui.assist.uihints;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.android.systemui.assist.AssistManager;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.KeepAliveListener;
import dagger.Lazy;
import java.util.concurrent.TimeUnit;

class TimeoutManager implements KeepAliveListener {
    private static final long SESSION_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(10);
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mOnTimeout;
    private TimeoutCallback mTimeoutCallback;

    interface TimeoutCallback {
        void onTimeout();
    }

    TimeoutManager(Lazy<AssistManager> lazy) {
        this.mOnTimeout = new Runnable(lazy) {
            public final /* synthetic */ Lazy f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TimeoutManager.this.lambda$new$0$TimeoutManager(this.f$1);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$TimeoutManager(Lazy lazy) {
        TimeoutCallback timeoutCallback = this.mTimeoutCallback;
        if (timeoutCallback != null) {
            timeoutCallback.onTimeout();
            return;
        }
        Log.e("TimeoutManager", "Timeout occurred, but there was no callback provided");
        ((AssistManager) lazy.get()).hideAssist();
    }

    public void onKeepAlive(String str) {
        resetTimeout();
    }

    /* access modifiers changed from: 0000 */
    public void resetTimeout() {
        this.mHandler.removeCallbacks(this.mOnTimeout);
        this.mHandler.postDelayed(this.mOnTimeout, SESSION_TIMEOUT_MS);
    }

    /* access modifiers changed from: 0000 */
    public void setTimeoutCallback(TimeoutCallback timeoutCallback) {
        this.mTimeoutCallback = timeoutCallback;
    }
}
