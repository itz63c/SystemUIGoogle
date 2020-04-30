package com.android.systemui.statusbar;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;

public interface SysuiStatusBarStateController extends StatusBarStateController {

    public static class RankedListener {
        final StateListener mListener;
        /* access modifiers changed from: 0000 */
        public final int mRank;

        RankedListener(StateListener stateListener, int i) {
            this.mListener = stateListener;
            this.mRank = i;
        }
    }

    @Deprecated
    void addCallback(StateListener stateListener, int i);

    boolean fromShadeLocked();

    float getInterpolatedDozeAmount();

    boolean goingToFullShade();

    boolean isKeyguardRequested();

    boolean leaveOpenOnKeyguardHide();

    void setDozeAmount(float f, boolean z);

    void setFullscreenState(boolean z, boolean z2);

    boolean setIsDozing(boolean z);

    void setKeyguardRequested(boolean z);

    void setLeaveOpenOnKeyguardHide(boolean z);

    void setPulsing(boolean z);

    boolean setState(int i);
}
