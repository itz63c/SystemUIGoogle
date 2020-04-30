package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import com.android.systemui.Dependency;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.google.android.systemui.assist.AssistManagerGoogle;
import com.google.android.systemui.elmyra.actions.Action;
import com.google.android.systemui.elmyra.gates.Gate.Listener;
import java.util.ArrayList;
import java.util.List;

public class NavigationBarVisibility extends Gate {
    private final AssistManagerGoogle mAssistManager;
    private final CommandQueue mCommandQueue;
    private final Callbacks mCommandQueueCallbacks = new Callbacks() {
        public void setWindowState(int i, int i2, int i3) {
            if (NavigationBarVisibility.this.mDisplayId == i && i2 == 2) {
                boolean z = i3 != 0;
                if (z != NavigationBarVisibility.this.mIsNavigationHidden) {
                    NavigationBarVisibility.this.mIsNavigationHidden = z;
                    NavigationBarVisibility.this.notifyListener();
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public final int mDisplayId;
    private final List<Action> mExceptions;
    private final Listener mGateListener = new Listener() {
        public void onGateChanged(Gate gate) {
            if (gate.equals(NavigationBarVisibility.this.mKeyguardGate)) {
                NavigationBarVisibility.this.updateKeyguardState();
            } else if (gate.equals(NavigationBarVisibility.this.mNavigationModeGate)) {
                NavigationBarVisibility.this.updateNavigationModeState();
            }
        }
    };
    private boolean mIsKeyguardShowing;
    private boolean mIsNavigationGestural;
    /* access modifiers changed from: private */
    public boolean mIsNavigationHidden;
    /* access modifiers changed from: private */
    public final KeyguardVisibility mKeyguardGate;
    /* access modifiers changed from: private */
    public final NonGesturalNavigation mNavigationModeGate;

    public NavigationBarVisibility(Context context, List<Action> list) {
        super(context);
        this.mExceptions = new ArrayList(list);
        this.mIsNavigationHidden = false;
        CommandQueue commandQueue = (CommandQueue) Dependency.get(CommandQueue.class);
        this.mCommandQueue = commandQueue;
        commandQueue.addCallback(this.mCommandQueueCallbacks);
        this.mDisplayId = context.getDisplayId();
        this.mAssistManager = (AssistManagerGoogle) Dependency.get(AssistManager.class);
        KeyguardVisibility keyguardVisibility = new KeyguardVisibility(context);
        this.mKeyguardGate = keyguardVisibility;
        keyguardVisibility.setListener(this.mGateListener);
        NonGesturalNavigation nonGesturalNavigation = new NonGesturalNavigation(context);
        this.mNavigationModeGate = nonGesturalNavigation;
        nonGesturalNavigation.setListener(this.mGateListener);
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.mKeyguardGate.activate();
        updateKeyguardState();
        this.mNavigationModeGate.activate();
        updateNavigationModeState();
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.mNavigationModeGate.deactivate();
        updateNavigationModeState();
        this.mKeyguardGate.deactivate();
        updateKeyguardState();
    }

    /* access modifiers changed from: private */
    public void updateKeyguardState() {
        this.mIsKeyguardShowing = this.mKeyguardGate.isKeyguardShowing();
    }

    /* access modifiers changed from: private */
    public void updateNavigationModeState() {
        this.mIsNavigationGestural = this.mNavigationModeGate.isNavigationGestural();
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        if (this.mIsKeyguardShowing) {
            return false;
        }
        if (this.mIsNavigationGestural && isActiveAssistantNga()) {
            return false;
        }
        for (int i = 0; i < this.mExceptions.size(); i++) {
            if (((Action) this.mExceptions.get(i)).isAvailable()) {
                return false;
            }
        }
        return this.mIsNavigationHidden;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mIsNavigationHidden -> ");
        sb.append(this.mIsNavigationHidden);
        sb.append("; mExceptions -> ");
        sb.append(this.mExceptions);
        sb.append("; mIsNavigationGestural -> ");
        sb.append(this.mIsNavigationGestural);
        sb.append("; isActiveAssistantNga() -> ");
        sb.append(isActiveAssistantNga());
        sb.append("]");
        return sb.toString();
    }

    private boolean isActiveAssistantNga() {
        return this.mAssistManager.isActiveAssistantNga();
    }
}
