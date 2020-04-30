package com.google.android.systemui.elmyra;

import android.app.StatsManager;
import android.app.StatsManager.StatsPullAtomCallback;
import android.content.Context;
import android.util.Log;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.google.android.systemui.elmyra.proto.nano.ChassisProtos$Chassis;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshot;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import com.google.android.systemui.elmyra.sensors.GestureSensor.Listener;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class WestworldLogger implements Listener {
    private ChassisProtos$Chassis mChassis = null;
    private CountDownLatch mCountDownLatch;
    private GestureConfiguration mGestureConfiguration;
    private Object mMutex;
    private SnapshotProtos$Snapshot mSnapshot;
    private SnapshotController mSnapshotController;
    private final StatsPullAtomCallback mWestworldCallback = new StatsPullAtomCallback() {
        public final int onPullAtom(int i, List list) {
            return WestworldLogger.this.lambda$new$0$WestworldLogger(i, list);
        }
    };

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ int lambda$new$0$WestworldLogger(int i, List list) {
        Log.d("Elmyra/Logger", "Receiving pull request from statsd.");
        return pull(i, list);
    }

    public WestworldLogger(Context context, GestureConfiguration gestureConfiguration, SnapshotController snapshotController) {
        this.mGestureConfiguration = gestureConfiguration;
        this.mSnapshotController = snapshotController;
        this.mSnapshot = null;
        this.mMutex = new Object();
        registerWithWestworld(context);
    }

    public void registerWithWestworld(Context context) {
        StatsManager statsManager = (StatsManager) context.getSystemService("stats");
        String str = "Elmyra/Logger";
        if (statsManager == null) {
            Log.d(str, "Failed to get StatsManager");
        }
        try {
            statsManager.setPullAtomCallback(150000, null, Executors.newSingleThreadExecutor(), this.mWestworldCallback);
        } catch (RuntimeException e) {
            Log.d(str, "Failed to register callback with StatsManager");
            e.printStackTrace();
        }
    }

    public void didReceiveChassis(ChassisProtos$Chassis chassisProtos$Chassis) {
        this.mChassis = chassisProtos$Chassis;
    }

    public void onGestureProgress(GestureSensor gestureSensor, float f, int i) {
        SysUiStatsLog.write(176, (int) (f * 100.0f));
        SysUiStatsLog.write(174, i);
    }

    public void onGestureDetected(GestureSensor gestureSensor, DetectionProperties detectionProperties) {
        SysUiStatsLog.write(174, 3);
    }

    public void querySubmitted() {
        SysUiStatsLog.write(175, 2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
        r8.mSnapshotController.onWestworldPull();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r2 = java.lang.System.currentTimeMillis();
        r8.mCountDownLatch.await(50, java.util.concurrent.TimeUnit.MILLISECONDS);
        r5 = new java.lang.StringBuilder();
        r5.append("Snapshot took ");
        r5.append(java.lang.Long.toString(java.lang.System.currentTimeMillis() - r2));
        r5.append(" milliseconds.");
        android.util.Log.d("Elmyra/Logger", r5.toString());
        r2 = r8.mMutex;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0057, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x005a, code lost:
        if (r8.mSnapshot == null) goto L_0x009f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005e, code lost:
        if (r8.mChassis != null) goto L_0x0061;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0061, code lost:
        r1 = new com.google.android.systemui.elmyra.proto.nano.ElmyraAtoms$ElmyraSnapshot();
        r8.mSnapshot.sensitivitySetting = r8.mGestureConfiguration.getSensitivity();
        r1.snapshot = r8.mSnapshot;
        r1.chassis = r8.mChassis;
        r10.add(android.util.StatsEvent.newBuilder().setAtomId(r9).writeByteArray(com.google.protobuf.nano.MessageNano.toByteArray(r1.snapshot)).writeByteArray(com.google.protobuf.nano.MessageNano.toByteArray(r1.chassis)).build());
        r8.mSnapshot = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x009d, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x009f, code lost:
        r8.mCountDownLatch = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00a1, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a2, code lost:
        return 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00a6, code lost:
        r9 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00a7, code lost:
        android.util.Log.d("Elmyra/Logger", r9.getMessage());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00b1, code lost:
        r9 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00b2, code lost:
        android.util.Log.d("Elmyra/Logger", r9.getMessage());
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int pull(int r9, java.util.List<android.util.StatsEvent> r10) {
        /*
            r8 = this;
            com.google.android.systemui.elmyra.SnapshotController r0 = r8.mSnapshotController
            r1 = 1
            if (r0 != 0) goto L_0x000d
            java.lang.String r8 = "Elmyra/Logger"
            java.lang.String r9 = "Snapshot Controller is null, returning."
            android.util.Log.d(r8, r9)
            return r1
        L_0x000d:
            java.lang.Object r0 = r8.mMutex
            monitor-enter(r0)
            java.util.concurrent.CountDownLatch r2 = r8.mCountDownLatch     // Catch:{ all -> 0x00c8 }
            if (r2 == 0) goto L_0x0016
            monitor-exit(r0)     // Catch:{ all -> 0x00c8 }
            return r1
        L_0x0016:
            java.util.concurrent.CountDownLatch r2 = new java.util.concurrent.CountDownLatch     // Catch:{ all -> 0x00c8 }
            r2.<init>(r1)     // Catch:{ all -> 0x00c8 }
            r8.mCountDownLatch = r2     // Catch:{ all -> 0x00c8 }
            monitor-exit(r0)     // Catch:{ all -> 0x00c8 }
            com.google.android.systemui.elmyra.SnapshotController r0 = r8.mSnapshotController
            r0.onWestworldPull()
            r0 = 0
            long r2 = java.lang.System.currentTimeMillis()     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            java.util.concurrent.CountDownLatch r4 = r8.mCountDownLatch     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            r5 = 50
            java.util.concurrent.TimeUnit r7 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            r4.await(r5, r7)     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            java.lang.String r4 = "Elmyra/Logger"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            r5.<init>()     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            java.lang.String r6 = "Snapshot took "
            r5.append(r6)     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            long r6 = java.lang.System.currentTimeMillis()     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            long r6 = r6 - r2
            java.lang.String r2 = java.lang.Long.toString(r6)     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            r5.append(r2)     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            java.lang.String r2 = " milliseconds."
            r5.append(r2)     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            java.lang.String r2 = r5.toString()     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            android.util.Log.d(r4, r2)     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            java.lang.Object r2 = r8.mMutex     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            monitor-enter(r2)     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
            com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshot r3 = r8.mSnapshot     // Catch:{ all -> 0x00a3 }
            if (r3 == 0) goto L_0x009f
            com.google.android.systemui.elmyra.proto.nano.ChassisProtos$Chassis r3 = r8.mChassis     // Catch:{ all -> 0x00a3 }
            if (r3 != 0) goto L_0x0061
            goto L_0x009f
        L_0x0061:
            com.google.android.systemui.elmyra.proto.nano.ElmyraAtoms$ElmyraSnapshot r1 = new com.google.android.systemui.elmyra.proto.nano.ElmyraAtoms$ElmyraSnapshot     // Catch:{ all -> 0x00a3 }
            r1.<init>()     // Catch:{ all -> 0x00a3 }
            com.google.android.systemui.elmyra.sensors.config.GestureConfiguration r3 = r8.mGestureConfiguration     // Catch:{ all -> 0x00a3 }
            float r3 = r3.getSensitivity()     // Catch:{ all -> 0x00a3 }
            com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshot r4 = r8.mSnapshot     // Catch:{ all -> 0x00a3 }
            r4.sensitivitySetting = r3     // Catch:{ all -> 0x00a3 }
            com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshot r3 = r8.mSnapshot     // Catch:{ all -> 0x00a3 }
            r1.snapshot = r3     // Catch:{ all -> 0x00a3 }
            com.google.android.systemui.elmyra.proto.nano.ChassisProtos$Chassis r3 = r8.mChassis     // Catch:{ all -> 0x00a3 }
            r1.chassis = r3     // Catch:{ all -> 0x00a3 }
            android.util.StatsEvent$Builder r3 = android.util.StatsEvent.newBuilder()     // Catch:{ all -> 0x00a3 }
            android.util.StatsEvent$Builder r9 = r3.setAtomId(r9)     // Catch:{ all -> 0x00a3 }
            com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshot r3 = r1.snapshot     // Catch:{ all -> 0x00a3 }
            byte[] r3 = com.google.protobuf.nano.MessageNano.toByteArray(r3)     // Catch:{ all -> 0x00a3 }
            android.util.StatsEvent$Builder r9 = r9.writeByteArray(r3)     // Catch:{ all -> 0x00a3 }
            com.google.android.systemui.elmyra.proto.nano.ChassisProtos$Chassis r1 = r1.chassis     // Catch:{ all -> 0x00a3 }
            byte[] r1 = com.google.protobuf.nano.MessageNano.toByteArray(r1)     // Catch:{ all -> 0x00a3 }
            android.util.StatsEvent$Builder r9 = r9.writeByteArray(r1)     // Catch:{ all -> 0x00a3 }
            android.util.StatsEvent r9 = r9.build()     // Catch:{ all -> 0x00a3 }
            r10.add(r9)     // Catch:{ all -> 0x00a3 }
            r8.mSnapshot = r0     // Catch:{ all -> 0x00a3 }
            monitor-exit(r2)     // Catch:{ all -> 0x00a3 }
            goto L_0x00bb
        L_0x009f:
            r8.mCountDownLatch = r0     // Catch:{ all -> 0x00a3 }
            monitor-exit(r2)     // Catch:{ all -> 0x00a3 }
            return r1
        L_0x00a3:
            r9 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00a3 }
            throw r9     // Catch:{ InterruptedException -> 0x00b1, IllegalMonitorStateException -> 0x00a6 }
        L_0x00a6:
            r9 = move-exception
            java.lang.String r9 = r9.getMessage()
            java.lang.String r10 = "Elmyra/Logger"
            android.util.Log.d(r10, r9)
            goto L_0x00bb
        L_0x00b1:
            r9 = move-exception
            java.lang.String r9 = r9.getMessage()
            java.lang.String r10 = "Elmyra/Logger"
            android.util.Log.d(r10, r9)
        L_0x00bb:
            java.lang.Object r9 = r8.mMutex
            monitor-enter(r9)
            r8.mCountDownLatch = r0     // Catch:{ all -> 0x00c5 }
            r8.mSnapshot = r0     // Catch:{ all -> 0x00c5 }
            monitor-exit(r9)     // Catch:{ all -> 0x00c5 }
            r8 = 0
            return r8
        L_0x00c5:
            r8 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x00c5 }
            throw r8
        L_0x00c8:
            r8 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00c8 }
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.elmyra.WestworldLogger.pull(int, java.util.List):int");
    }

    public void didReceiveSnapshot(SnapshotProtos$Snapshot snapshotProtos$Snapshot) {
        synchronized (this.mMutex) {
            this.mSnapshot = snapshotProtos$Snapshot;
            if (this.mCountDownLatch != null) {
                this.mCountDownLatch.countDown();
            }
        }
    }
}
