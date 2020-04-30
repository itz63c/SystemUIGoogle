package com.android.systemui.util.concurrency;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class ExecutorImpl implements DelayableExecutor {
    /* access modifiers changed from: private */
    public final Handler mHandler;

    private class ExecutionToken implements Runnable {
        public final Runnable runnable;

        private ExecutionToken(Runnable runnable2) {
            this.runnable = runnable2;
        }

        public void run() {
            ExecutorImpl.this.mHandler.removeCallbacksAndMessages(this);
        }
    }

    ExecutorImpl(Looper looper) {
        this.mHandler = new Handler(looper, new Callback() {
            public final boolean handleMessage(Message message) {
                return ExecutorImpl.this.onHandleMessage(message);
            }
        });
    }

    public void execute(Runnable runnable) {
        if (!this.mHandler.post(runnable)) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mHandler);
            sb.append(" is shutting down");
            throw new RejectedExecutionException(sb.toString());
        }
    }

    public Runnable executeDelayed(Runnable runnable, long j, TimeUnit timeUnit) {
        ExecutionToken executionToken = new ExecutionToken(runnable);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0, executionToken), timeUnit.toMillis(j));
        return executionToken;
    }

    /* access modifiers changed from: private */
    public boolean onHandleMessage(Message message) {
        if (message.what == 0) {
            ((ExecutionToken) message.obj).runnable.run();
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unrecognized message: ");
        sb.append(message.what);
        throw new IllegalStateException(sb.toString());
    }
}
