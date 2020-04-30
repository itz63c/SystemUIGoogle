package com.android.systemui.util.leak;

import android.os.SystemClock;
import android.util.ArrayMap;
import java.io.PrintWriter;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class TrackedGarbage {
    private final HashSet<LeakReference> mGarbage = new HashSet<>();
    private final ReferenceQueue<Object> mRefQueue = new ReferenceQueue<>();
    private final TrackedCollections mTrackedCollections;

    private static class LeakReference extends WeakReference<Object> {
        /* access modifiers changed from: private */
        public final Class<?> clazz;
        /* access modifiers changed from: private */
        public final long createdUptimeMillis = SystemClock.uptimeMillis();

        LeakReference(Object obj, ReferenceQueue<Object> referenceQueue) {
            super(obj, referenceQueue);
            this.clazz = obj.getClass();
        }
    }

    private boolean isOld(long j, long j2) {
        return j + 60000 < j2;
    }

    public TrackedGarbage(TrackedCollections trackedCollections) {
        this.mTrackedCollections = trackedCollections;
    }

    public synchronized void track(Object obj) {
        cleanUp();
        this.mGarbage.add(new LeakReference(obj, this.mRefQueue));
        this.mTrackedCollections.track(this.mGarbage, "Garbage");
    }

    private void cleanUp() {
        while (true) {
            Reference poll = this.mRefQueue.poll();
            if (poll != null) {
                this.mGarbage.remove(poll);
            } else {
                return;
            }
        }
    }

    public synchronized void dump(PrintWriter printWriter) {
        cleanUp();
        long uptimeMillis = SystemClock.uptimeMillis();
        ArrayMap arrayMap = new ArrayMap();
        ArrayMap arrayMap2 = new ArrayMap();
        Iterator it = this.mGarbage.iterator();
        while (it.hasNext()) {
            LeakReference leakReference = (LeakReference) it.next();
            arrayMap.put(leakReference.clazz, Integer.valueOf(((Integer) arrayMap.getOrDefault(leakReference.clazz, Integer.valueOf(0))).intValue() + 1));
            if (isOld(leakReference.createdUptimeMillis, uptimeMillis)) {
                arrayMap2.put(leakReference.clazz, Integer.valueOf(((Integer) arrayMap2.getOrDefault(leakReference.clazz, Integer.valueOf(0))).intValue() + 1));
            }
        }
        for (Entry entry : arrayMap.entrySet()) {
            printWriter.print(((Class) entry.getKey()).getName());
            printWriter.print(": ");
            printWriter.print(entry.getValue());
            printWriter.print(" total, ");
            printWriter.print(arrayMap2.getOrDefault(entry.getKey(), Integer.valueOf(0)));
            printWriter.print(" old");
            printWriter.println();
        }
    }

    public synchronized int countOldGarbage() {
        int i;
        cleanUp();
        long uptimeMillis = SystemClock.uptimeMillis();
        i = 0;
        Iterator it = this.mGarbage.iterator();
        while (it.hasNext()) {
            if (isOld(((LeakReference) it.next()).createdUptimeMillis, uptimeMillis)) {
                i++;
            }
        }
        return i;
    }
}
