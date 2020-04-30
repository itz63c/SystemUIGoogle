package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import com.android.systemui.Dependency;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener;

public class NonGesturalNavigation extends Gate {
    /* access modifiers changed from: private */
    public boolean mCurrentModeIsGestural;
    private final NavigationModeController mModeController = ((NavigationModeController) Dependency.get(NavigationModeController.class));
    private final ModeChangedListener mModeListener = new ModeChangedListener() {
        public void onNavigationModeChanged(int i) {
            NonGesturalNavigation.this.mCurrentModeIsGestural = QuickStepContract.isGesturalMode(i);
            NonGesturalNavigation.this.notifyListener();
        }
    };

    public NonGesturalNavigation(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.mCurrentModeIsGestural = QuickStepContract.isGesturalMode(this.mModeController.addListener(this.mModeListener));
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.mModeController.removeListener(this.mModeListener);
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return !isNavigationGestural();
    }

    public boolean isNavigationGestural() {
        return this.mCurrentModeIsGestural;
    }
}
