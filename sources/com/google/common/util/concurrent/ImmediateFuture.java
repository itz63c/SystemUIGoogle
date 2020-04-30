package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class ImmediateFuture<V> implements ListenableFuture<V> {
    private static final Logger log = Logger.getLogger(ImmediateFuture.class.getName());

    static class ImmediateSuccessfulFuture<V> extends ImmediateFuture<V> {
        static final ImmediateSuccessfulFuture<Object> NULL = new ImmediateSuccessfulFuture<>(null);
        private final V value;

        ImmediateSuccessfulFuture(V v) {
            this.value = v;
        }

        public V get() {
            return this.value;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append("[status=SUCCESS, result=[");
            sb.append(this.value);
            sb.append("]]");
            return sb.toString();
        }
    }

    public boolean cancel(boolean z) {
        return false;
    }

    public abstract V get() throws ExecutionException;

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return true;
    }

    ImmediateFuture() {
    }

    public void addListener(Runnable runnable, Executor executor) {
        Preconditions.checkNotNull(runnable, "Runnable was null.");
        Preconditions.checkNotNull(executor, "Executor was null.");
        try {
            executor.execute(runnable);
        } catch (RuntimeException e) {
            Logger logger = log;
            Level level = Level.SEVERE;
            StringBuilder sb = new StringBuilder();
            sb.append("RuntimeException while executing runnable ");
            sb.append(runnable);
            sb.append(" with executor ");
            sb.append(executor);
            logger.log(level, sb.toString(), e);
        }
    }

    public V get(long j, TimeUnit timeUnit) throws ExecutionException {
        Preconditions.checkNotNull(timeUnit);
        return get();
    }
}
