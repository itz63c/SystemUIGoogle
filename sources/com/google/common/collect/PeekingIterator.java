package com.google.common.collect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;

public interface PeekingIterator<E> extends Iterator<E> {
    @CanIgnoreReturnValue
    E next();

    E peek();
}
