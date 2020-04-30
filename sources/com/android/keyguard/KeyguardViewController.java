package com.android.keyguard;

import android.os.Bundle;
import android.view.ViewRootImpl;

public interface KeyguardViewController {
    void dismissAndCollapse();

    ViewRootImpl getViewRootImpl();

    void hide(long j, long j2);

    boolean isGoingToNotificationShade();

    boolean isShowing();

    boolean isUnlockWithWallpaper();

    void keyguardGoingAway();

    void onCancelClicked();

    void onFinishedGoingToSleep();

    void onScreenTurnedOn();

    void onScreenTurningOn();

    void onStartedGoingToSleep();

    void onStartedWakingUp();

    void reset(boolean z);

    void setNeedsInput(boolean z);

    void setOccluded(boolean z, boolean z2);

    boolean shouldDisableWindowAnimationsForUnlock();

    boolean shouldSubtleWindowAnimationsForUnlock();

    void show(Bundle bundle);

    void startPreHideAnimation(Runnable runnable);
}
