package com.android.systemui.broadcast;

import android.content.BroadcastReceiver;
import java.util.function.Predicate;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$BooleanRef;

/* renamed from: com.android.systemui.broadcast.UserBroadcastDispatcher$handleUnregisterReceiver$$inlined$forEach$lambda$1 */
/* compiled from: UserBroadcastDispatcher.kt */
final class C0756xa20bc321<T> implements Predicate<ReceiverData> {
    final /* synthetic */ BroadcastReceiver $receiver$inlined;

    C0756xa20bc321(UserBroadcastDispatcher userBroadcastDispatcher, BroadcastReceiver broadcastReceiver, Ref$BooleanRef ref$BooleanRef) {
        this.$receiver$inlined = broadcastReceiver;
    }

    public final boolean test(ReceiverData receiverData) {
        Intrinsics.checkParameterIsNotNull(receiverData, "it");
        return Intrinsics.areEqual((Object) receiverData.getReceiver(), (Object) this.$receiver$inlined);
    }
}
