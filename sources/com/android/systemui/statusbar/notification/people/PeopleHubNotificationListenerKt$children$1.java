package com.android.systemui.statusbar.notification.people;

import android.view.View;
import android.view.ViewGroup;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.SequenceScope;

@DebugMetadata(mo22270c = "com.android.systemui.statusbar.notification.people.PeopleHubNotificationListenerKt$children$1", mo22271f = "PeopleHubNotificationListener.kt", mo22272l = {302}, mo22273m = "invokeSuspend")
/* compiled from: PeopleHubNotificationListener.kt */
final class PeopleHubNotificationListenerKt$children$1 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super View>, Continuation<? super Unit>, Object> {
    final /* synthetic */ ViewGroup $this_children;
    int I$0;
    int I$1;
    Object L$0;
    int label;

    /* renamed from: p$ */
    private SequenceScope f71p$;

    PeopleHubNotificationListenerKt$children$1(ViewGroup viewGroup, Continuation continuation) {
        this.$this_children = viewGroup;
        super(2, continuation);
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        Intrinsics.checkParameterIsNotNull(continuation, "completion");
        PeopleHubNotificationListenerKt$children$1 peopleHubNotificationListenerKt$children$1 = new PeopleHubNotificationListenerKt$children$1(this.$this_children, continuation);
        peopleHubNotificationListenerKt$children$1.f71p$ = (SequenceScope) obj;
        return peopleHubNotificationListenerKt$children$1;
    }

    public final Object invoke(Object obj, Object obj2) {
        return ((PeopleHubNotificationListenerKt$children$1) create(obj, (Continuation) obj2)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARNING: Removed duplicated region for block: B:8:0x0031  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object invokeSuspend(java.lang.Object r7) {
        /*
            r6 = this;
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r1 = r6.label
            r2 = 1
            if (r1 == 0) goto L_0x001f
            if (r1 != r2) goto L_0x0017
            int r1 = r6.I$1
            int r3 = r6.I$0
            java.lang.Object r4 = r6.L$0
            kotlin.sequences.SequenceScope r4 = (kotlin.sequences.SequenceScope) r4
            kotlin.ResultKt.throwOnFailure(r7)
            goto L_0x0046
        L_0x0017:
            java.lang.IllegalStateException r6 = new java.lang.IllegalStateException
            java.lang.String r7 = "call to 'resume' before 'invoke' with coroutine"
            r6.<init>(r7)
            throw r6
        L_0x001f:
            kotlin.ResultKt.throwOnFailure(r7)
            kotlin.sequences.SequenceScope r7 = r6.f71p$
            r1 = 0
            android.view.ViewGroup r3 = r6.$this_children
            int r3 = r3.getChildCount()
            r4 = r7
            r5 = r3
            r3 = r1
            r1 = r5
        L_0x002f:
            if (r3 >= r1) goto L_0x0048
            android.view.ViewGroup r7 = r6.$this_children
            android.view.View r7 = r7.getChildAt(r3)
            r6.L$0 = r4
            r6.I$0 = r3
            r6.I$1 = r1
            r6.label = r2
            java.lang.Object r7 = r4.yield(r7, r6)
            if (r7 != r0) goto L_0x0046
            return r0
        L_0x0046:
            int r3 = r3 + r2
            goto L_0x002f
        L_0x0048:
            kotlin.Unit r6 = kotlin.Unit.INSTANCE
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.people.PeopleHubNotificationListenerKt$children$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }
}
