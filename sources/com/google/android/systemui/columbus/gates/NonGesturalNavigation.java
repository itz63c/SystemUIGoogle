package com.google.android.systemui.columbus.gates;

import android.content.Context;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener;
import dagger.Lazy;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NonGesturalNavigation.kt */
public final class NonGesturalNavigation extends Gate {
    /* access modifiers changed from: private */
    public boolean currentModeIsGestural;
    private final Lazy<NavigationModeController> modeController;
    private final ModeChangedListener modeListener = new NonGesturalNavigation$modeListener$1(this);

    public NonGesturalNavigation(Context context, Lazy<NavigationModeController> lazy) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(lazy, "modeController");
        super(context);
        this.modeController = lazy;
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.currentModeIsGestural = QuickStepContract.isGesturalMode(((NavigationModeController) this.modeController.get()).addListener(this.modeListener));
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        ((NavigationModeController) this.modeController.get()).removeListener(this.modeListener);
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return !isNavigationGestural();
    }

    public final boolean isNavigationGestural() {
        return this.currentModeIsGestural;
    }
}
