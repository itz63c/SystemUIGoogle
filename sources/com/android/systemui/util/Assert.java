package com.android.systemui.util;

import android.os.Looper;

public class Assert {
    private static final Looper sMainLooper = Looper.getMainLooper();
    private static Looper sTestLooper = null;

    public static void setTestableLooper(Looper looper) {
        sTestLooper = looper;
    }

    public static void isMainThread() {
        if (!sMainLooper.isCurrentThread()) {
            Looper looper = sTestLooper;
            if (looper == null || !looper.isCurrentThread()) {
                StringBuilder sb = new StringBuilder();
                sb.append("should be called from the main thread. sMainLooper.threadName=");
                sb.append(sMainLooper.getThread().getName());
                sb.append(" Thread.currentThread()=");
                sb.append(Thread.currentThread().getName());
                throw new IllegalStateException(sb.toString());
            }
        }
    }
}
