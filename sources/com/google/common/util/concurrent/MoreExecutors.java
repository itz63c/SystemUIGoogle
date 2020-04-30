package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

public final class MoreExecutors {

    static class Application {
        Application() {
        }

        /* access modifiers changed from: 0000 */
        public void addShutdownHook(Thread thread) {
            Runtime.getRuntime().addShutdownHook(thread);
        }
    }

    public static Executor directExecutor() {
        return DirectExecutor.INSTANCE;
    }

    static Executor rejectionPropagatingExecutor(final Executor executor, final AbstractFuture<?> abstractFuture) {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(abstractFuture);
        if (executor == directExecutor()) {
            return executor;
        }
        return new Executor() {
            boolean thrownFromDelegate = true;

            public void execute(final Runnable runnable) {
                try {
                    executor.execute(new Runnable() {
                        public void run() {
                            C19945.this.thrownFromDelegate = false;
                            runnable.run();
                        }
                    });
                } catch (RejectedExecutionException e) {
                    if (this.thrownFromDelegate) {
                        abstractFuture.setException(e);
                    }
                }
            }
        };
    }
}
