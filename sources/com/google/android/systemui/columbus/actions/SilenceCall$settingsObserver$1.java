package com.google.android.systemui.columbus.actions;

import android.net.Uri;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: SilenceCall.kt */
final class SilenceCall$settingsObserver$1 extends Lambda implements Function1<Uri, Unit> {
    final /* synthetic */ SilenceCall this$0;

    SilenceCall$settingsObserver$1(SilenceCall silenceCall) {
        this.this$0 = silenceCall;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((Uri) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(Uri uri) {
        Intrinsics.checkParameterIsNotNull(uri, "it");
        this.this$0.updatePhoneStateListener();
    }
}
