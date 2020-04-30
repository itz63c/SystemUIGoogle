package com.google.android.systemui.columbus.gates;

import android.content.Context;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.google.android.systemui.columbus.actions.Action;
import dagger.Lazy;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SetupWizard.kt */
public final class SetupWizard extends Gate {
    private final List<Action> exceptions;
    private final Lazy<DeviceProvisionedController> provisionedController;
    private final SetupWizard$provisionedListener$1 provisionedListener = new SetupWizard$provisionedListener$1(this);
    /* access modifiers changed from: private */
    public boolean setupComplete;

    public SetupWizard(Context context, Set<Action> set, Lazy<DeviceProvisionedController> lazy) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(set, "setupWizardExceptions");
        Intrinsics.checkParameterIsNotNull(lazy, "provisionedController");
        super(context);
        this.provisionedController = lazy;
        this.exceptions = CollectionsKt___CollectionsKt.toList(set);
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.setupComplete = isSetupComplete();
        ((DeviceProvisionedController) this.provisionedController.get()).addCallback(this.provisionedListener);
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        ((DeviceProvisionedController) this.provisionedController.get()).removeCallback(this.provisionedListener);
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
        if (((Action) obj) != null || this.setupComplete) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public final boolean isSetupComplete() {
        Object obj = this.provisionedController.get();
        String str = "provisionedController.get()";
        Intrinsics.checkExpressionValueIsNotNull(obj, str);
        if (((DeviceProvisionedController) obj).isDeviceProvisioned()) {
            Object obj2 = this.provisionedController.get();
            Intrinsics.checkExpressionValueIsNotNull(obj2, str);
            if (((DeviceProvisionedController) obj2).isCurrentUserSetup()) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [isDeviceProvisioned -> ");
        Object obj = this.provisionedController.get();
        String str = "provisionedController.get()";
        Intrinsics.checkExpressionValueIsNotNull(obj, str);
        sb.append(((DeviceProvisionedController) obj).isDeviceProvisioned());
        sb.append("; isCurrentUserSetup -> ");
        Object obj2 = this.provisionedController.get();
        Intrinsics.checkExpressionValueIsNotNull(obj2, str);
        sb.append(((DeviceProvisionedController) obj2).isCurrentUserSetup());
        sb.append("]");
        return sb.toString();
    }
}
