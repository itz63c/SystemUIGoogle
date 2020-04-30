package com.android.settingslib.graph;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* compiled from: ThemedBatteryDrawable.kt */
final class ThemedBatteryDrawable$invalidateRunnable$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ ThemedBatteryDrawable this$0;

    ThemedBatteryDrawable$invalidateRunnable$1(ThemedBatteryDrawable themedBatteryDrawable) {
        this.this$0 = themedBatteryDrawable;
        super(0);
    }

    public final void invoke() {
        this.this$0.invalidateSelf();
    }
}
