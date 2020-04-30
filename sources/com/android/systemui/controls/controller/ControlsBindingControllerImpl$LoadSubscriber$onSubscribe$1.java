package com.android.systemui.controls.controller;

import android.service.controls.IControlsSubscription;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReference;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KDeclarationContainer;

/* compiled from: ControlsBindingControllerImpl.kt */
final /* synthetic */ class ControlsBindingControllerImpl$LoadSubscriber$onSubscribe$1 extends FunctionReference implements Function0<Unit> {
    ControlsBindingControllerImpl$LoadSubscriber$onSubscribe$1(IControlsSubscription iControlsSubscription) {
        super(0, iControlsSubscription);
    }

    public final String getName() {
        return "cancel";
    }

    public final KDeclarationContainer getOwner() {
        return Reflection.getOrCreateKotlinClass(IControlsSubscription.class);
    }

    public final String getSignature() {
        return "cancel()V";
    }

    public final void invoke() {
        ((IControlsSubscription) this.receiver).cancel();
    }
}
