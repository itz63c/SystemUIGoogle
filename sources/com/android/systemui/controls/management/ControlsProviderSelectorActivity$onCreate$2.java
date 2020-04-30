package com.android.systemui.controls.management;

import android.content.ComponentName;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReference;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KDeclarationContainer;

/* compiled from: ControlsProviderSelectorActivity.kt */
final /* synthetic */ class ControlsProviderSelectorActivity$onCreate$2 extends FunctionReference implements Function1<ComponentName, Unit> {
    ControlsProviderSelectorActivity$onCreate$2(ControlsProviderSelectorActivity controlsProviderSelectorActivity) {
        super(1, controlsProviderSelectorActivity);
    }

    public final String getName() {
        return "launchFavoritingActivity";
    }

    public final KDeclarationContainer getOwner() {
        return Reflection.getOrCreateKotlinClass(ControlsProviderSelectorActivity.class);
    }

    public final String getSignature() {
        return "launchFavoritingActivity(Landroid/content/ComponentName;)V";
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((ComponentName) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(ComponentName componentName) {
        ((ControlsProviderSelectorActivity) this.receiver).launchFavoritingActivity(componentName);
    }
}
