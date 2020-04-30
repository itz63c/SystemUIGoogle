package com.google.common.util.concurrent;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

public final class SettableFuture<V> extends TrustedFuture<V> {
    public static <V> SettableFuture<V> create() {
        return new SettableFuture<>();
    }

    @CanIgnoreReturnValue
    public boolean set(V v) {
        return super.set(v);
    }

    @CanIgnoreReturnValue
    public boolean setException(Throwable th) {
        return super.setException(th);
    }

    @CanIgnoreReturnValue
    public boolean setFuture(ListenableFuture<? extends V> listenableFuture) {
        return super.setFuture(listenableFuture);
    }

    private SettableFuture() {
    }
}
