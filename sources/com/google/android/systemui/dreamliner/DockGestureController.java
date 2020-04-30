package com.google.android.systemui.dreamliner;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.Interpolators;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.statusbar.FlingAnimationUtils.Builder;
import java.util.concurrent.TimeUnit;

public class DockGestureController extends SimpleOnGestureListener implements OnTouchListener, StateListener {
    private static final long GEAR_VISIBLE_TIME_MILLIS = TimeUnit.SECONDS.toMillis(15);
    private final AccessibilityManager mAccessibilityManager;
    private final Context mContext;
    private final FlingAnimationUtils mFlingAnimationUtils;
    @VisibleForTesting
    int mFlingDiffThreshold;
    @VisibleForTesting
    GestureDetector mGestureDetector;
    private final Runnable mHideGearRunnable = new Runnable() {
        public final void run() {
            DockGestureController.this.hideGear();
        }
    };
    private final ImageView mSettingsGear;
    private final StatusBarStateController mStatusBarStateController;
    private final View mTouchDelegateView;

    DockGestureController(Context context, ImageView imageView, View view, StatusBarStateController statusBarStateController) {
        this.mContext = context;
        this.mGestureDetector = new GestureDetector(context, this);
        this.mTouchDelegateView = view;
        this.mSettingsGear = imageView;
        imageView.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                DockGestureController.this.lambda$new$0$DockGestureController(view);
            }
        });
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        this.mFlingDiffThreshold = this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.dock_fling_diff);
        this.mFlingAnimationUtils = new Builder(this.mContext.getResources().getDisplayMetrics()).build();
        this.mStatusBarStateController = statusBarStateController;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$DockGestureController(View view) {
        hideGear();
        sendProtectedBroadcast(new Intent("com.google.android.apps.dreamliner.SETTINGS"));
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        this.mGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        showGear();
        return false;
    }

    public boolean onDown(MotionEvent motionEvent) {
        sendProtectedBroadcast(new Intent("com.google.android.systemui.dreamliner.TOUCH_EVENT"));
        return false;
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        float x = motionEvent2.getX() - motionEvent.getX();
        if (Math.abs(x) <= Math.abs(motionEvent2.getY() - motionEvent.getY()) || Math.abs(x) <= ((float) this.mFlingDiffThreshold) || (Math.abs(f) >= this.mFlingAnimationUtils.getMinVelocityPxPerSecond() && Math.signum(x) != Math.signum(f))) {
            return false;
        }
        sendProtectedBroadcast(new Intent("com.google.android.systemui.dreamliner.FLING_EVENT").putExtra("direction", x < 0.0f ? 1 : 2));
        return true;
    }

    public void onDozingChanged(boolean z) {
        if (z) {
            this.mTouchDelegateView.setOnTouchListener(this);
            showGear();
            return;
        }
        this.mTouchDelegateView.setOnTouchListener(null);
        hideGear();
    }

    /* access modifiers changed from: 0000 */
    public void startMonitoring() {
        this.mSettingsGear.setVisibility(4);
        onDozingChanged(this.mStatusBarStateController.isDozing());
        this.mStatusBarStateController.addCallback(this);
    }

    /* access modifiers changed from: 0000 */
    public void stopMonitoring() {
        this.mStatusBarStateController.removeCallback(this);
        onDozingChanged(false);
        this.mSettingsGear.setVisibility(8);
    }

    private void showGear() {
        if (!this.mSettingsGear.isVisibleToUser()) {
            this.mSettingsGear.setVisibility(0);
            this.mSettingsGear.animate().setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).alpha(1.0f).start();
        }
        this.mSettingsGear.removeCallbacks(this.mHideGearRunnable);
        this.mSettingsGear.postDelayed(this.mHideGearRunnable, getRecommendedTimeoutMillis());
    }

    /* access modifiers changed from: private */
    public void hideGear() {
        if (this.mSettingsGear.isVisibleToUser()) {
            this.mSettingsGear.removeCallbacks(this.mHideGearRunnable);
            this.mSettingsGear.animate().setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).alpha(0.0f).withEndAction(new Runnable() {
                public final void run() {
                    DockGestureController.this.lambda$hideGear$1$DockGestureController();
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hideGear$1 */
    public /* synthetic */ void lambda$hideGear$1$DockGestureController() {
        this.mSettingsGear.setVisibility(4);
    }

    private void sendProtectedBroadcast(Intent intent) {
        try {
            this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        } catch (SecurityException e) {
            Log.w("DLGestureController", "Cannot send event", e);
        }
    }

    private long getRecommendedTimeoutMillis() {
        AccessibilityManager accessibilityManager = this.mAccessibilityManager;
        if (accessibilityManager == null) {
            return GEAR_VISIBLE_TIME_MILLIS;
        }
        return (long) accessibilityManager.getRecommendedTimeoutMillis(Math.toIntExact(GEAR_VISIBLE_TIME_MILLIS), 5);
    }
}
