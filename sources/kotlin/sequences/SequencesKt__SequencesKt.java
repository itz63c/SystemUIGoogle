package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Sequences.kt */
class SequencesKt__SequencesKt extends SequencesKt__SequencesJVMKt {
    public static <T> Sequence<T> asSequence(Iterator<? extends T> it) {
        Intrinsics.checkParameterIsNotNull(it, "$this$asSequence");
        return constrainOnce(new SequencesKt__SequencesKt$asSequence$$inlined$Sequence$1(it));
    }

    public static final <T> Sequence<T> sequenceOf(T... tArr) {
        Intrinsics.checkParameterIsNotNull(tArr, "elements");
        return tArr.length == 0 ? emptySequence() : ArraysKt___ArraysKt.asSequence(tArr);
    }

    public static <T> Sequence<T> emptySequence() {
        return EmptySequence.INSTANCE;
    }

    public static final <T> Sequence<T> flatten(Sequence<? extends Sequence<? extends T>> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$flatten");
        return flatten$SequencesKt__SequencesKt(sequence, SequencesKt__SequencesKt$flatten$1.INSTANCE);
    }

    private static final <T, R> Sequence<R> flatten$SequencesKt__SequencesKt(Sequence<? extends T> sequence, Function1<? super T, ? extends Iterator<? extends R>> function1) {
        if (sequence instanceof TransformingSequence) {
            return ((TransformingSequence) sequence).flatten$kotlin_stdlib(function1);
        }
        return new FlatteningSequence(sequence, SequencesKt__SequencesKt$flatten$3.INSTANCE, function1);
    }

    public static final <T> Sequence<T> constrainOnce(Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$constrainOnce");
        return sequence instanceof ConstrainedOnceSequence ? sequence : new ConstrainedOnceSequence(sequence);
    }
}
