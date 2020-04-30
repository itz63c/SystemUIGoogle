package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfo;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfoListener;
import dagger.Lazy;

public class TouchInsideHandler implements ConfigInfoListener, OnClickListener, OnTouchListener {
    private Runnable mFallback;
    private boolean mGuardLocked;
    private boolean mGuarded;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mInGesturalMode;
    private PendingIntent mTouchInside;

    TouchInsideHandler(Lazy<AssistManager> lazy, NavigationModeController navigationModeController) {
        this.mFallback = new Runnable() {
            public final void run() {
                ((AssistManager) Lazy.this.get()).hideAssist();
            }
        };
        onNavigationModeChange(navigationModeController.addListener(new ModeChangedListener() {
            public final void onNavigationModeChanged(int i) {
                TouchInsideHandler.this.onNavigationModeChange(i);
            }
        }));
    }

    public void onConfigInfo(ConfigInfo configInfo) {
        this.mTouchInside = configInfo.onTouchInside;
    }

    public void onClick(View view) {
        onTouchInside();
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (this.mInGesturalMode) {
            gestureModeOnTouch(view, motionEvent);
        } else {
            nonGestureModeOnTouch(view, motionEvent);
        }
        return true;
    }

    public void onTouchInside() {
        PendingIntent pendingIntent = this.mTouchInside;
        if (pendingIntent != null) {
            try {
                pendingIntent.send();
            } catch (CanceledException unused) {
                Log.w("TouchInsideHandler", "Touch outside PendingIntent canceled");
                this.mFallback.run();
            }
        } else {
            this.mFallback.run();
        }
        MetricsLogger.action(new LogMaker(1716).setType(5).setSubtype(2));
    }

    private void gestureModeOnTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            onTouchInside();
        }
    }

    private void nonGestureModeOnTouch(View view, MotionEvent motionEvent) {
        if (this.mGuarded && !this.mGuardLocked && motionEvent.getAction() == 0) {
            this.mGuarded = false;
        } else if (!this.mGuarded && motionEvent.getAction() == 1) {
            onTouchInside();
        }
    }

    /* access modifiers changed from: 0000 */
    public void maybeSetGuarded() {
        if (!this.mInGesturalMode) {
            this.mGuardLocked = true;
            this.mGuarded = true;
            this.mHandler.postDelayed(new Runnable() {
                public final void run() {
                    TouchInsideHandler.this.unlockGuard();
                }
            }, 500);
        }
    }

    /* access modifiers changed from: private */
    public void unlockGuard() {
        this.mGuardLocked = false;
    }

    /* access modifiers changed from: 0000 */
    public void setFallback(Runnable runnable) {
        this.mFallback = runnable;
    }

    /* access modifiers changed from: private */
    public void onNavigationModeChange(int i) {
        boolean isGesturalMode = QuickStepContract.isGesturalMode(i);
        this.mInGesturalMode = isGesturalMode;
        if (isGesturalMode) {
            this.mGuardLocked = false;
            this.mGuarded = false;
        }
    }
}
