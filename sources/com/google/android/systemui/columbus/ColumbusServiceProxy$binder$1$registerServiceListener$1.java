package com.google.android.systemui.columbus;

import android.os.IBinder;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: ColumbusServiceProxy.kt */
final class ColumbusServiceProxy$binder$1$registerServiceListener$1 extends Lambda implements Function1<ColumbusServiceListener, Boolean> {
    final /* synthetic */ IBinder $token;

    ColumbusServiceProxy$binder$1$registerServiceListener$1(IBinder iBinder) {
        this.$token = iBinder;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Boolean.valueOf(invoke((ColumbusServiceListener) obj));
    }

    public final boolean invoke(ColumbusServiceListener columbusServiceListener) {
        Intrinsics.checkParameterIsNotNull(columbusServiceListener, "it");
        if (!Intrinsics.areEqual((Object) this.$token, (Object) columbusServiceListener.getToken())) {
            return false;
        }
        columbusServiceListener.unlinkToDeath();
        return true;
    }
}
