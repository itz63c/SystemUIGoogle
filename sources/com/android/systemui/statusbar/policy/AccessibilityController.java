package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener;
import android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener;
import java.util.ArrayList;

public class AccessibilityController implements AccessibilityStateChangeListener, TouchExplorationStateChangeListener {
    private boolean mAccessibilityEnabled;
    private final ArrayList<AccessibilityStateChangedCallback> mChangeCallbacks = new ArrayList<>();
    private boolean mTouchExplorationEnabled;

    public interface AccessibilityStateChangedCallback {
        void onStateChanged(boolean z, boolean z2);
    }

    public AccessibilityController(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        accessibilityManager.addTouchExplorationStateChangeListener(this);
        accessibilityManager.addAccessibilityStateChangeListener(this);
        this.mAccessibilityEnabled = accessibilityManager.isEnabled();
        this.mTouchExplorationEnabled = accessibilityManager.isTouchExplorationEnabled();
    }

    public boolean isAccessibilityEnabled() {
        return this.mAccessibilityEnabled;
    }

    public void addStateChangedCallback(AccessibilityStateChangedCallback accessibilityStateChangedCallback) {
        this.mChangeCallbacks.add(accessibilityStateChangedCallback);
        accessibilityStateChangedCallback.onStateChanged(this.mAccessibilityEnabled, this.mTouchExplorationEnabled);
    }

    public void removeStateChangedCallback(AccessibilityStateChangedCallback accessibilityStateChangedCallback) {
        this.mChangeCallbacks.remove(accessibilityStateChangedCallback);
    }

    private void fireChanged() {
        int size = this.mChangeCallbacks.size();
        for (int i = 0; i < size; i++) {
            ((AccessibilityStateChangedCallback) this.mChangeCallbacks.get(i)).onStateChanged(this.mAccessibilityEnabled, this.mTouchExplorationEnabled);
        }
    }

    public void onAccessibilityStateChanged(boolean z) {
        this.mAccessibilityEnabled = z;
        fireChanged();
    }

    public void onTouchExplorationStateChanged(boolean z) {
        this.mTouchExplorationEnabled = z;
        fireChanged();
    }
}
