package com.android.systemui;

import com.android.internal.annotations.GuardedBy;
import com.android.systemui.BootCompleteCache.BootCompleteListener;
import com.android.systemui.dump.DumpManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: BootCompleteCacheImpl.kt */
public final class BootCompleteCacheImpl implements BootCompleteCache, Dumpable {
    private final AtomicBoolean bootComplete = new AtomicBoolean(false);
    @GuardedBy({"listeners"})
    private final List<WeakReference<BootCompleteListener>> listeners = new ArrayList();

    public BootCompleteCacheImpl(DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        dumpManager.registerDumpable("BootCompleteCacheImpl", this);
    }

    public boolean isBootComplete() {
        return this.bootComplete.get();
    }

    public final void setBootComplete() {
        if (this.bootComplete.compareAndSet(false, true)) {
            synchronized (this.listeners) {
                for (WeakReference weakReference : this.listeners) {
                    BootCompleteListener bootCompleteListener = (BootCompleteListener) weakReference.get();
                    if (bootCompleteListener != null) {
                        bootCompleteListener.onBootComplete();
                    }
                }
                this.listeners.clear();
                Unit unit = Unit.INSTANCE;
            }
        }
    }

    public boolean addListener(BootCompleteListener bootCompleteListener) {
        Intrinsics.checkParameterIsNotNull(bootCompleteListener, "listener");
        if (this.bootComplete.get()) {
            return true;
        }
        synchronized (this.listeners) {
            if (this.bootComplete.get()) {
                return true;
            }
            this.listeners.add(new WeakReference(bootCompleteListener));
            return false;
        }
    }

    public void removeListener(BootCompleteListener bootCompleteListener) {
        Intrinsics.checkParameterIsNotNull(bootCompleteListener, "listener");
        if (!this.bootComplete.get()) {
            synchronized (this.listeners) {
                this.listeners.removeIf(new C0668x854f7506(this, bootCompleteListener));
                Unit unit = Unit.INSTANCE;
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(strArr, "args");
        printWriter.println("BootCompleteCache state:");
        StringBuilder sb = new StringBuilder();
        sb.append("  boot complete: ");
        sb.append(isBootComplete());
        printWriter.println(sb.toString());
        if (!isBootComplete()) {
            printWriter.println("  listeners:");
            synchronized (this.listeners) {
                for (WeakReference weakReference : this.listeners) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("    ");
                    sb2.append(weakReference);
                    printWriter.println(sb2.toString());
                }
                Unit unit = Unit.INSTANCE;
            }
        }
    }
}
