package com.android.systemui;

/* compiled from: BootCompleteCache.kt */
public interface BootCompleteCache {

    /* compiled from: BootCompleteCache.kt */
    public interface BootCompleteListener {
        void onBootComplete();
    }

    boolean addListener(BootCompleteListener bootCompleteListener);

    boolean isBootComplete();

    void removeListener(BootCompleteListener bootCompleteListener);
}
