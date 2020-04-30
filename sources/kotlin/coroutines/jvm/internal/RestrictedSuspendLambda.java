package kotlin.coroutines.jvm.internal;

import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.FunctionBase;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;

/* compiled from: ContinuationImpl.kt */
public abstract class RestrictedSuspendLambda extends RestrictedContinuationImpl implements FunctionBase<Object> {
    private final int arity;

    public int getArity() {
        return this.arity;
    }

    public RestrictedSuspendLambda(int i, Continuation<Object> continuation) {
        super(continuation);
        this.arity = i;
    }

    public String toString() {
        if (getCompletion() != null) {
            return super.toString();
        }
        String renderLambdaToString = Reflection.renderLambdaToString((FunctionBase) this);
        Intrinsics.checkExpressionValueIsNotNull(renderLambdaToString, "Reflection.renderLambdaToString(this)");
        return renderLambdaToString;
    }
}
