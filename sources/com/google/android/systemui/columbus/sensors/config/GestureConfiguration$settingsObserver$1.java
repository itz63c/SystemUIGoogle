package com.google.android.systemui.columbus.sensors.config;

import android.net.Uri;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: GestureConfiguration.kt */
final class GestureConfiguration$settingsObserver$1 extends Lambda implements Function1<Uri, Unit> {
    final /* synthetic */ GestureConfiguration this$0;

    GestureConfiguration$settingsObserver$1(GestureConfiguration gestureConfiguration) {
        this.this$0 = gestureConfiguration;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((Uri) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(Uri uri) {
        Intrinsics.checkParameterIsNotNull(uri, "it");
        this.this$0.onSensitivityChanged();
    }
}
