package kotlin.coroutines.intrinsics;

import kotlin.ResultKt;
import kotlin.TypeCastException;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.TypeIntrinsics;

/* renamed from: kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$4 */
/* compiled from: IntrinsicsJvm.kt */
public final class C1999xa50de663 extends ContinuationImpl {
    final /* synthetic */ Continuation $completion;
    final /* synthetic */ CoroutineContext $context;
    final /* synthetic */ Object $receiver$inlined;
    final /* synthetic */ Function2 $this_createCoroutineUnintercepted$inlined;
    private int label;

    public C1999xa50de663(Continuation continuation, CoroutineContext coroutineContext, Continuation continuation2, CoroutineContext coroutineContext2, Function2 function2, Object obj) {
        this.$completion = continuation;
        this.$context = coroutineContext;
        this.$this_createCoroutineUnintercepted$inlined = function2;
        this.$receiver$inlined = obj;
        super(continuation2, coroutineContext2);
    }

    /* access modifiers changed from: protected */
    public Object invokeSuspend(Object obj) {
        int i = this.label;
        if (i == 0) {
            this.label = 1;
            ResultKt.throwOnFailure(obj);
            Function2 function2 = this.$this_createCoroutineUnintercepted$inlined;
            if (function2 != null) {
                TypeIntrinsics.beforeCheckcastToFunctionOfArity(function2, 2);
                return function2.invoke(this.$receiver$inlined, this);
            }
            throw new TypeCastException("null cannot be cast to non-null type (R, kotlin.coroutines.Continuation<T>) -> kotlin.Any?");
        } else if (i == 1) {
            this.label = 2;
            ResultKt.throwOnFailure(obj);
            return obj;
        } else {
            throw new IllegalStateException("This coroutine had already completed".toString());
        }
    }
}
