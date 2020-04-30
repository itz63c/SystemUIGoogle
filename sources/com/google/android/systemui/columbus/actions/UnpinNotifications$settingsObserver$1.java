package com.google.android.systemui.columbus.actions;

import android.net.Uri;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: UnpinNotifications.kt */
final class UnpinNotifications$settingsObserver$1 extends Lambda implements Function1<Uri, Unit> {
    final /* synthetic */ UnpinNotifications this$0;

    UnpinNotifications$settingsObserver$1(UnpinNotifications unpinNotifications) {
        this.this$0 = unpinNotifications;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((Uri) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(Uri uri) {
        Intrinsics.checkParameterIsNotNull(uri, "it");
        this.this$0.updateHeadsUpListener();
    }
}
