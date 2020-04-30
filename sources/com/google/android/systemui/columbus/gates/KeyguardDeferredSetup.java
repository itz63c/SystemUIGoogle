package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings.Secure;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.columbus.ColumbusContentObserver.Factory;
import com.google.android.systemui.columbus.actions.Action;
import com.google.android.systemui.columbus.gates.Gate.Listener;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: KeyguardDeferredSetup.kt */
public final class KeyguardDeferredSetup extends Gate {
    private boolean deferredSetupComplete;
    private final List<Action> exceptions;
    private final KeyguardVisibility keyguardGate;
    private final ColumbusContentObserver settingsObserver;

    public KeyguardDeferredSetup(Context context, List<Action> list, KeyguardVisibility keyguardVisibility, Factory factory) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(list, "exceptions");
        Intrinsics.checkParameterIsNotNull(keyguardVisibility, "keyguardGate");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context);
        this.exceptions = list;
        this.keyguardGate = keyguardVisibility;
        Uri uriFor = Secure.getUriFor("assist_gesture_setup_complete");
        Intrinsics.checkExpressionValueIsNotNull(uriFor, "Settings.Secure.getUriFo…T_GESTURE_SETUP_COMPLETE)");
        this.settingsObserver = factory.create(uriFor, new KeyguardDeferredSetup$settingsObserver$1(this));
        this.keyguardGate.setListener(new Listener(this) {
            final /* synthetic */ KeyguardDeferredSetup this$0;

            {
                this.this$0 = r1;
            }

            public void onGateChanged(Gate gate) {
                Intrinsics.checkParameterIsNotNull(gate, "gate");
                this.this$0.notifyListener();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.keyguardGate.activate();
        this.deferredSetupComplete = isDeferredSetupComplete();
        this.settingsObserver.activate();
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.keyguardGate.deactivate();
        this.settingsObserver.deactivate();
    }

    /* access modifiers changed from: private */
    public final void updateSetupComplete() {
        boolean isDeferredSetupComplete = isDeferredSetupComplete();
        if (this.deferredSetupComplete != isDeferredSetupComplete) {
            this.deferredSetupComplete = isDeferredSetupComplete;
            notifyListener();
        }
    }

    private final boolean isDeferredSetupComplete() {
        if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_setup_complete", 0, -2) != 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        Object obj;
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
        if (((Action) obj) != null || this.deferredSetupComplete || !this.keyguardGate.isBlocking()) {
            return false;
        }
        return true;
    }

    public final boolean isSuwComplete() {
        return this.deferredSetupComplete;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [deferredSetupComplete -> ");
        sb.append(this.deferredSetupComplete);
        sb.append("]");
        return sb.toString();
    }
}
