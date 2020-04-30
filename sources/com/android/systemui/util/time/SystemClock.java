package com.android.systemui.util.time;

public interface SystemClock {
    long elapsedRealtime();

    long uptimeMillis();
}
