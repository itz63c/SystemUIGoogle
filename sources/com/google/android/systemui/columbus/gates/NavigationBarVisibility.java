package com.google.android.systemui.columbus.gates;

import android.content.Context;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.google.android.systemui.assist.AssistManagerGoogle;
import com.google.android.systemui.columbus.actions.Action;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NavigationBarVisibility.kt */
public final class NavigationBarVisibility extends Gate {
    private final AssistManagerGoogle assistManager;
    private final NavigationBarVisibility$commandQueueCallbacks$1 commandQueueCallbacks = new NavigationBarVisibility$commandQueueCallbacks$1(this);
    /* access modifiers changed from: private */
    public final int displayId;
    private final List<Action> exceptions;
    private final NavigationBarVisibility$gateListener$1 gateListener;
    private boolean isKeyguardShowing;
    private boolean isNavigationGestural;
    /* access modifiers changed from: private */
    public boolean isNavigationHidden;
    /* access modifiers changed from: private */
    public final KeyguardVisibility keyguardGate;
    /* access modifiers changed from: private */
    public final NonGesturalNavigation navigationModeGate;

    public NavigationBarVisibility(Context context, List<Action> list, AssistManager assistManager2, KeyguardVisibility keyguardVisibility, NonGesturalNavigation nonGesturalNavigation, CommandQueue commandQueue) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(list, "exceptions");
        Intrinsics.checkParameterIsNotNull(assistManager2, "assistManager");
        Intrinsics.checkParameterIsNotNull(keyguardVisibility, "keyguardGate");
        Intrinsics.checkParameterIsNotNull(nonGesturalNavigation, "navigationModeGate");
        Intrinsics.checkParameterIsNotNull(commandQueue, "commandQueue");
        super(context);
        this.exceptions = list;
        this.keyguardGate = keyguardVisibility;
        this.navigationModeGate = nonGesturalNavigation;
        this.displayId = context.getDisplayId();
        if (!(assistManager2 instanceof AssistManagerGoogle)) {
            assistManager2 = null;
        }
        this.assistManager = (AssistManagerGoogle) assistManager2;
        this.gateListener = new NavigationBarVisibility$gateListener$1(this);
        commandQueue.addCallback((Callbacks) this.commandQueueCallbacks);
        this.keyguardGate.setListener(this.gateListener);
        this.navigationModeGate.setListener(this.gateListener);
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.keyguardGate.activate();
        updateKeyguardState();
        this.navigationModeGate.activate();
        updateNavigationModeState();
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.navigationModeGate.deactivate();
        updateNavigationModeState();
        this.keyguardGate.deactivate();
        updateKeyguardState();
    }

    /* access modifiers changed from: private */
    public final void updateKeyguardState() {
        this.isKeyguardShowing = this.keyguardGate.isKeyguardShowing();
    }

    /* access modifiers changed from: private */
    public final void updateNavigationModeState() {
        this.isNavigationGestural = this.navigationModeGate.isNavigationGestural();
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        Object obj;
        boolean z = false;
        if (this.isKeyguardShowing) {
            return false;
        }
        if (this.isNavigationGestural && isActiveAssistantNga()) {
            return false;
        }
        Iterator it = this.exceptions.iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            if (((Action) obj).isAvailable()) {
                break;
            }
        }
        if (((Action) obj) == null) {
            z = this.isNavigationHidden;
        }
        return z;
    }

    private final boolean isActiveAssistantNga() {
        AssistManagerGoogle assistManagerGoogle = this.assistManager;
        if (assistManagerGoogle != null) {
            return assistManagerGoogle.isActiveAssistantNga();
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [isNavigationHidden -> ");
        sb.append(this.isNavigationHidden);
        sb.append("; exceptions -> ");
        sb.append(this.exceptions);
        sb.append("; isNavigationGestural -> ");
        sb.append(this.isNavigationGestural);
        sb.append("; isActiveAssistantNga() -> ");
        sb.append(isActiveAssistantNga());
        sb.append("]");
        return sb.toString();
    }
}
