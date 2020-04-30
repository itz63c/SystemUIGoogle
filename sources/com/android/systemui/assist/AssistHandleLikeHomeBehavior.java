package com.android.systemui.assist;

import android.content.Context;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle.Observer;
import com.android.systemui.model.SysUiState;
import com.android.systemui.model.SysUiState.SysUiStateCallback;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import dagger.Lazy;
import java.io.PrintWriter;

final class AssistHandleLikeHomeBehavior implements BehaviorController {
    private AssistHandleCallbacks mAssistHandleCallbacks;
    private boolean mIsAwake;
    private boolean mIsDozing;
    private boolean mIsHomeHandleHiding;
    private final Lazy<StatusBarStateController> mStatusBarStateController;
    private final StateListener mStatusBarStateListener = new StateListener() {
        public void onDozingChanged(boolean z) {
            AssistHandleLikeHomeBehavior.this.handleDozingChanged(z);
        }
    };
    private final Lazy<SysUiState> mSysUiFlagContainer;
    private final SysUiStateCallback mSysUiStateCallback = new SysUiStateCallback() {
        public final void onSystemUiStateChanged(int i) {
            AssistHandleLikeHomeBehavior.this.handleSystemUiStateChange(i);
        }
    };
    private final Lazy<WakefulnessLifecycle> mWakefulnessLifecycle;
    private final Observer mWakefulnessLifecycleObserver = new Observer() {
        public void onStartedWakingUp() {
            AssistHandleLikeHomeBehavior.this.handleWakefullnessChanged(false);
        }

        public void onFinishedWakingUp() {
            AssistHandleLikeHomeBehavior.this.handleWakefullnessChanged(true);
        }

        public void onStartedGoingToSleep() {
            AssistHandleLikeHomeBehavior.this.handleWakefullnessChanged(false);
        }

        public void onFinishedGoingToSleep() {
            AssistHandleLikeHomeBehavior.this.handleWakefullnessChanged(false);
        }
    };

    private static boolean isHomeHandleHiding(int i) {
        return (i & 2) != 0;
    }

    AssistHandleLikeHomeBehavior(Lazy<StatusBarStateController> lazy, Lazy<WakefulnessLifecycle> lazy2, Lazy<SysUiState> lazy3) {
        this.mStatusBarStateController = lazy;
        this.mWakefulnessLifecycle = lazy2;
        this.mSysUiFlagContainer = lazy3;
    }

    public void onModeActivated(Context context, AssistHandleCallbacks assistHandleCallbacks) {
        this.mAssistHandleCallbacks = assistHandleCallbacks;
        this.mIsDozing = ((StatusBarStateController) this.mStatusBarStateController.get()).isDozing();
        ((StatusBarStateController) this.mStatusBarStateController.get()).addCallback(this.mStatusBarStateListener);
        this.mIsAwake = ((WakefulnessLifecycle) this.mWakefulnessLifecycle.get()).getWakefulness() == 2;
        ((WakefulnessLifecycle) this.mWakefulnessLifecycle.get()).addObserver(this.mWakefulnessLifecycleObserver);
        ((SysUiState) this.mSysUiFlagContainer.get()).addCallback(this.mSysUiStateCallback);
        callbackForCurrentState();
    }

    public void onModeDeactivated() {
        this.mAssistHandleCallbacks = null;
        ((StatusBarStateController) this.mStatusBarStateController.get()).removeCallback(this.mStatusBarStateListener);
        ((WakefulnessLifecycle) this.mWakefulnessLifecycle.get()).removeObserver(this.mWakefulnessLifecycleObserver);
        ((SysUiState) this.mSysUiFlagContainer.get()).removeCallback(this.mSysUiStateCallback);
    }

    /* access modifiers changed from: private */
    public void handleDozingChanged(boolean z) {
        if (this.mIsDozing != z) {
            this.mIsDozing = z;
            callbackForCurrentState();
        }
    }

    /* access modifiers changed from: private */
    public void handleWakefullnessChanged(boolean z) {
        if (this.mIsAwake != z) {
            this.mIsAwake = z;
            callbackForCurrentState();
        }
    }

    /* access modifiers changed from: private */
    public void handleSystemUiStateChange(int i) {
        boolean isHomeHandleHiding = isHomeHandleHiding(i);
        if (this.mIsHomeHandleHiding != isHomeHandleHiding) {
            this.mIsHomeHandleHiding = isHomeHandleHiding;
            callbackForCurrentState();
        }
    }

    private void callbackForCurrentState() {
        if (this.mAssistHandleCallbacks != null) {
            if (this.mIsHomeHandleHiding || !isFullyAwake()) {
                this.mAssistHandleCallbacks.hide();
            } else {
                this.mAssistHandleCallbacks.showAndStay();
            }
        }
    }

    private boolean isFullyAwake() {
        return this.mIsAwake && !this.mIsDozing;
    }

    public void dump(PrintWriter printWriter, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("Current AssistHandleLikeHomeBehavior State:");
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append("   mIsDozing=");
        sb2.append(this.mIsDozing);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append("   mIsAwake=");
        sb3.append(this.mIsAwake);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str);
        sb4.append("   mIsHomeHandleHiding=");
        sb4.append(this.mIsHomeHandleHiding);
        printWriter.println(sb4.toString());
    }
}
