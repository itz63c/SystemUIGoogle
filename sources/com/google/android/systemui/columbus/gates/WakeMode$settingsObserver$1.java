package com.google.android.systemui.columbus.gates;

import android.net.Uri;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: WakeMode.kt */
final class WakeMode$settingsObserver$1 extends Lambda implements Function1<Uri, Unit> {
    final /* synthetic */ WakeMode this$0;

    WakeMode$settingsObserver$1(WakeMode wakeMode) {
        this.this$0 = wakeMode;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((Uri) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(Uri uri) {
        Intrinsics.checkParameterIsNotNull(uri, "it");
        this.this$0.updateWakeSetting();
    }
}
