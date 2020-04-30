package kotlin.sequences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import kotlin.Pair;
import kotlin.TypeCastException;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: _Sequences.kt */
class SequencesKt___SequencesKt extends SequencesKt___SequencesJvmKt {
    public static <T> T firstOrNull(Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$firstOrNull");
        Iterator it = sequence.iterator();
        if (!it.hasNext()) {
            return null;
        }
        return it.next();
    }

    public static <T> T lastOrNull(Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$lastOrNull");
        Iterator it = sequence.iterator();
        if (!it.hasNext()) {
            return null;
        }
        T next = it.next();
        while (it.hasNext()) {
            next = it.next();
        }
        return next;
    }

    public static <T> Sequence<T> filter(Sequence<? extends T> sequence, Function1<? super T, Boolean> function1) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$filter");
        Intrinsics.checkParameterIsNotNull(function1, "predicate");
        return new FilteringSequence(sequence, true, function1);
    }

    public static <T> Sequence<T> filterNot(Sequence<? extends T> sequence, Function1<? super T, Boolean> function1) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$filterNot");
        Intrinsics.checkParameterIsNotNull(function1, "predicate");
        return new FilteringSequence(sequence, false, function1);
    }

    public static final <T> Sequence<T> filterNotNull(Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$filterNotNull");
        Sequence<T> filterNot = filterNot(sequence, SequencesKt___SequencesKt$filterNotNull$1.INSTANCE);
        if (filterNot != null) {
            return filterNot;
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.sequences.Sequence<T>");
    }

    public static <T> Sequence<T> take(Sequence<? extends T> sequence, int i) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$take");
        if (!(i >= 0)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Requested element count ");
            sb.append(i);
            sb.append(" is less than zero.");
            throw new IllegalArgumentException(sb.toString().toString());
        } else if (i == 0) {
            return SequencesKt__SequencesKt.emptySequence();
        } else {
            if (sequence instanceof DropTakeSequence) {
                return ((DropTakeSequence) sequence).take(i);
            }
            return new TakeSequence(sequence, i);
        }
    }

    public static <T> Sequence<T> sortedWith(Sequence<? extends T> sequence, Comparator<? super T> comparator) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$sortedWith");
        Intrinsics.checkParameterIsNotNull(comparator, "comparator");
        return new SequencesKt___SequencesKt$sortedWith$1(sequence, comparator);
    }

    public static final <T, C extends Collection<? super T>> C toCollection(Sequence<? extends T> sequence, C c) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$toCollection");
        Intrinsics.checkParameterIsNotNull(c, "destination");
        for (Object add : sequence) {
            c.add(add);
        }
        return c;
    }

    public static <T> List<T> toList(Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$toList");
        return CollectionsKt__CollectionsKt.optimizeReadOnlyList(toMutableList(sequence));
    }

    public static final <T> List<T> toMutableList(Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$toMutableList");
        ArrayList arrayList = new ArrayList();
        toCollection(sequence, arrayList);
        return arrayList;
    }

    public static <T, R> Sequence<R> flatMap(Sequence<? extends T> sequence, Function1<? super T, ? extends Sequence<? extends R>> function1) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$flatMap");
        Intrinsics.checkParameterIsNotNull(function1, "transform");
        return new FlatteningSequence(sequence, function1, SequencesKt___SequencesKt$flatMap$1.INSTANCE);
    }

    public static <T, R> Sequence<R> map(Sequence<? extends T> sequence, Function1<? super T, ? extends R> function1) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$map");
        Intrinsics.checkParameterIsNotNull(function1, "transform");
        return new TransformingSequence(sequence, function1);
    }

    public static <T, R> Sequence<R> mapNotNull(Sequence<? extends T> sequence, Function1<? super T, ? extends R> function1) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$mapNotNull");
        Intrinsics.checkParameterIsNotNull(function1, "transform");
        return filterNotNull(new TransformingSequence(sequence, function1));
    }

    public static <T> Sequence<T> distinct(Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$distinct");
        return distinctBy(sequence, SequencesKt___SequencesKt$distinct$1.INSTANCE);
    }

    public static final <T, K> Sequence<T> distinctBy(Sequence<? extends T> sequence, Function1<? super T, ? extends K> function1) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$distinctBy");
        Intrinsics.checkParameterIsNotNull(function1, "selector");
        return new DistinctSequence(sequence, function1);
    }

    public static <T> Sequence<T> plus(Sequence<? extends T> sequence, Sequence<? extends T> sequence2) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$plus");
        Intrinsics.checkParameterIsNotNull(sequence2, "elements");
        return SequencesKt__SequencesKt.flatten(SequencesKt__SequencesKt.sequenceOf(sequence, sequence2));
    }

    public static <T, R> Sequence<Pair<T, R>> zip(Sequence<? extends T> sequence, Sequence<? extends R> sequence2) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$zip");
        Intrinsics.checkParameterIsNotNull(sequence2, "other");
        return new MergingSequence(sequence, sequence2, SequencesKt___SequencesKt$zip$1.INSTANCE);
    }

    public static <T> Iterable<T> asIterable(Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$asIterable");
        return new SequencesKt___SequencesKt$asIterable$$inlined$Iterable$1(sequence);
    }
}
