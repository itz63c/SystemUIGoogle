package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.AccessibilityServicesStateChangeListener;

public class AccessibilityManagerWrapper implements CallbackController<AccessibilityServicesStateChangeListener> {
    private final AccessibilityManager mAccessibilityManager;

    public AccessibilityManagerWrapper(Context context) {
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
    }

    public void addCallback(AccessibilityServicesStateChangeListener accessibilityServicesStateChangeListener) {
        this.mAccessibilityManager.addAccessibilityServicesStateChangeListener(accessibilityServicesStateChangeListener, null);
    }

    public void removeCallback(AccessibilityServicesStateChangeListener accessibilityServicesStateChangeListener) {
        this.mAccessibilityManager.removeAccessibilityServicesStateChangeListener(accessibilityServicesStateChangeListener);
    }

    public int getRecommendedTimeoutMillis(int i, int i2) {
        return this.mAccessibilityManager.getRecommendedTimeoutMillis(i, i2);
    }
}
