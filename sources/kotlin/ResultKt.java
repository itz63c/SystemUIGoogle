package kotlin;

import kotlin.Result.Failure;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Result.kt */
public final class ResultKt {
    public static final Object createFailure(Throwable th) {
        Intrinsics.checkParameterIsNotNull(th, "exception");
        return new Failure(th);
    }

    public static final void throwOnFailure(Object obj) {
        if (obj instanceof Failure) {
            throw ((Failure) obj).exception;
        }
    }
}
