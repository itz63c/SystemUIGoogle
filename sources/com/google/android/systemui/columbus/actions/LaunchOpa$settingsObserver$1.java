package com.google.android.systemui.columbus.actions;

import android.net.Uri;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: LaunchOpa.kt */
final class LaunchOpa$settingsObserver$1 extends Lambda implements Function1<Uri, Unit> {
    final /* synthetic */ LaunchOpa this$0;

    LaunchOpa$settingsObserver$1(LaunchOpa launchOpa) {
        this.this$0 = launchOpa;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((Uri) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(Uri uri) {
        Intrinsics.checkParameterIsNotNull(uri, "it");
        this.this$0.updateGestureEnabled();
    }
}
