package kotlin.sequences;

/* compiled from: Sequences.kt */
public interface DropTakeSequence<T> extends Sequence<T> {
    Sequence<T> take(int i);
}
