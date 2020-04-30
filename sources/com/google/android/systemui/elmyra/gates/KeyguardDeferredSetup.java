package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings.Secure;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.actions.Action;
import com.google.android.systemui.elmyra.gates.Gate.Listener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KeyguardDeferredSetup extends Gate {
    private boolean mDeferredSetupComplete;
    private final List<Action> mExceptions;
    private final KeyguardVisibility mKeyguardGate;
    private final Listener mKeyguardGateListener = new Listener() {
        public void onGateChanged(Gate gate) {
            KeyguardDeferredSetup.this.notifyListener();
        }
    };
    private final UserContentObserver mSettingsObserver;

    public KeyguardDeferredSetup(Context context, List<Action> list) {
        super(context);
        this.mExceptions = new ArrayList(list);
        KeyguardVisibility keyguardVisibility = new KeyguardVisibility(context);
        this.mKeyguardGate = keyguardVisibility;
        keyguardVisibility.setListener(this.mKeyguardGateListener);
        this.mSettingsObserver = new UserContentObserver(context, Secure.getUriFor("assist_gesture_setup_complete"), new Consumer() {
            public final void accept(Object obj) {
                KeyguardDeferredSetup.this.lambda$new$0$KeyguardDeferredSetup((Uri) obj);
            }
        }, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$KeyguardDeferredSetup(Uri uri) {
        updateSetupComplete();
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.mKeyguardGate.activate();
        this.mDeferredSetupComplete = isDeferredSetupComplete();
        this.mSettingsObserver.activate();
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.mKeyguardGate.deactivate();
        this.mSettingsObserver.deactivate();
    }

    private void updateSetupComplete() {
        boolean isDeferredSetupComplete = isDeferredSetupComplete();
        if (this.mDeferredSetupComplete != isDeferredSetupComplete) {
            this.mDeferredSetupComplete = isDeferredSetupComplete;
            notifyListener();
        }
    }

    private boolean isDeferredSetupComplete() {
        if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_setup_complete", 0, -2) != 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        boolean z = false;
        for (int i = 0; i < this.mExceptions.size(); i++) {
            if (((Action) this.mExceptions.get(i)).isAvailable()) {
                return false;
            }
        }
        if (!this.mDeferredSetupComplete && this.mKeyguardGate.isBlocking()) {
            z = true;
        }
        return z;
    }

    public boolean isSuwComplete() {
        return this.mDeferredSetupComplete;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mDeferredSetupComplete -> ");
        sb.append(this.mDeferredSetupComplete);
        sb.append("]");
        return sb.toString();
    }
}
