package kotlin.coroutines.intrinsics;

import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.coroutines.jvm.internal.BaseContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: IntrinsicsJvm.kt */
class IntrinsicsKt__IntrinsicsJvmKt {
    public static <R, T> Continuation<Unit> createCoroutineUnintercepted(Function2<? super R, ? super Continuation<? super T>, ? extends Object> function2, R r, Continuation<? super T> continuation) {
        Intrinsics.checkParameterIsNotNull(function2, "$this$createCoroutineUnintercepted");
        Intrinsics.checkParameterIsNotNull(continuation, "completion");
        DebugProbesKt.probeCoroutineCreated(continuation);
        if (function2 instanceof BaseContinuationImpl) {
            return ((BaseContinuationImpl) function2).create(r, continuation);
        }
        CoroutineContext context = continuation.getContext();
        String str = "null cannot be cast to non-null type kotlin.coroutines.Continuation<kotlin.Any?>";
        if (context == EmptyCoroutineContext.INSTANCE) {
            if (continuation != null) {
                return new C1998xa50de662(continuation, continuation, function2, r);
            }
            throw new TypeCastException(str);
        } else if (continuation != null) {
            C1999xa50de663 intrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$4 = new C1999xa50de663(continuation, context, continuation, context, function2, r);
            return intrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$4;
        } else {
            throw new TypeCastException(str);
        }
    }
}
