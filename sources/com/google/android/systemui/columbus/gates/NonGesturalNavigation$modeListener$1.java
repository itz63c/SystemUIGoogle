package com.google.android.systemui.columbus.gates;

import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener;

/* compiled from: NonGesturalNavigation.kt */
final class NonGesturalNavigation$modeListener$1 implements ModeChangedListener {
    final /* synthetic */ NonGesturalNavigation this$0;

    NonGesturalNavigation$modeListener$1(NonGesturalNavigation nonGesturalNavigation) {
        this.this$0 = nonGesturalNavigation;
    }

    public final void onNavigationModeChanged(int i) {
        this.this$0.currentModeIsGestural = QuickStepContract.isGesturalMode(i);
        this.this$0.notifyListener();
    }
}
