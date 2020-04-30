package com.android.systemui.broadcast;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.UserHandle;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: BroadcastDispatcher.kt */
public final class ReceiverData {
    private final Executor executor;
    private final IntentFilter filter;
    private final BroadcastReceiver receiver;
    private final UserHandle user;

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002e, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2.user, (java.lang.Object) r3.user) != false) goto L_0x0033;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x0033
            boolean r0 = r3 instanceof com.android.systemui.broadcast.ReceiverData
            if (r0 == 0) goto L_0x0031
            com.android.systemui.broadcast.ReceiverData r3 = (com.android.systemui.broadcast.ReceiverData) r3
            android.content.BroadcastReceiver r0 = r2.receiver
            android.content.BroadcastReceiver r1 = r3.receiver
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0031
            android.content.IntentFilter r0 = r2.filter
            android.content.IntentFilter r1 = r3.filter
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0031
            java.util.concurrent.Executor r0 = r2.executor
            java.util.concurrent.Executor r1 = r3.executor
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0031
            android.os.UserHandle r2 = r2.user
            android.os.UserHandle r3 = r3.user
            boolean r2 = kotlin.jvm.internal.Intrinsics.areEqual(r2, r3)
            if (r2 == 0) goto L_0x0031
            goto L_0x0033
        L_0x0031:
            r2 = 0
            return r2
        L_0x0033:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.broadcast.ReceiverData.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        BroadcastReceiver broadcastReceiver = this.receiver;
        int i = 0;
        int hashCode = (broadcastReceiver != null ? broadcastReceiver.hashCode() : 0) * 31;
        IntentFilter intentFilter = this.filter;
        int hashCode2 = (hashCode + (intentFilter != null ? intentFilter.hashCode() : 0)) * 31;
        Executor executor2 = this.executor;
        int hashCode3 = (hashCode2 + (executor2 != null ? executor2.hashCode() : 0)) * 31;
        UserHandle userHandle = this.user;
        if (userHandle != null) {
            i = userHandle.hashCode();
        }
        return hashCode3 + i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ReceiverData(receiver=");
        sb.append(this.receiver);
        sb.append(", filter=");
        sb.append(this.filter);
        sb.append(", executor=");
        sb.append(this.executor);
        sb.append(", user=");
        sb.append(this.user);
        sb.append(")");
        return sb.toString();
    }

    public ReceiverData(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, Executor executor2, UserHandle userHandle) {
        Intrinsics.checkParameterIsNotNull(broadcastReceiver, "receiver");
        Intrinsics.checkParameterIsNotNull(intentFilter, "filter");
        Intrinsics.checkParameterIsNotNull(executor2, "executor");
        Intrinsics.checkParameterIsNotNull(userHandle, "user");
        this.receiver = broadcastReceiver;
        this.filter = intentFilter;
        this.executor = executor2;
        this.user = userHandle;
    }

    public final BroadcastReceiver getReceiver() {
        return this.receiver;
    }

    public final IntentFilter getFilter() {
        return this.filter;
    }

    public final Executor getExecutor() {
        return this.executor;
    }

    public final UserHandle getUser() {
        return this.user;
    }
}
