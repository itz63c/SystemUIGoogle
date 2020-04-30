package com.android.systemui.statusbar.notification.people;

import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.SequenceScope;

@DebugMetadata(mo22270c = "com.android.systemui.statusbar.notification.people.PeopleHubViewControllerKt$repeated$1", mo22271f = "PeopleHubViewController.kt", mo22272l = {191}, mo22273m = "invokeSuspend")
/* compiled from: PeopleHubViewController.kt */
final class PeopleHubViewControllerKt$repeated$1 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super T>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Object $value;
    Object L$0;
    int label;

    /* renamed from: p$ */
    private SequenceScope f72p$;

    PeopleHubViewControllerKt$repeated$1(Object obj, Continuation continuation) {
        this.$value = obj;
        super(2, continuation);
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        Intrinsics.checkParameterIsNotNull(continuation, "completion");
        PeopleHubViewControllerKt$repeated$1 peopleHubViewControllerKt$repeated$1 = new PeopleHubViewControllerKt$repeated$1(this.$value, continuation);
        peopleHubViewControllerKt$repeated$1.f72p$ = (SequenceScope) obj;
        return peopleHubViewControllerKt$repeated$1;
    }

    public final Object invoke(Object obj, Object obj2) {
        return ((PeopleHubViewControllerKt$repeated$1) create(obj, (Continuation) obj2)).invokeSuspend(Unit.INSTANCE);
    }

    public final Object invokeSuspend(Object obj) {
        SequenceScope sequenceScope;
        Object obj2;
        Object coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure(obj);
            sequenceScope = this.f72p$;
        } else if (i == 1) {
            sequenceScope = (SequenceScope) this.L$0;
            ResultKt.throwOnFailure(obj);
        } else {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        do {
            obj2 = this.$value;
            this.L$0 = sequenceScope;
            this.label = 1;
        } while (sequenceScope.yield(obj2, this) != coroutine_suspended);
        return coroutine_suspended;
    }
}
