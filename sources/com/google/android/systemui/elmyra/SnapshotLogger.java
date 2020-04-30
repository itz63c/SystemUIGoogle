package com.google.android.systemui.elmyra;

import android.os.Binder;
import com.android.systemui.Dumpable;
import com.google.android.systemui.elmyra.proto.nano.ChassisProtos$SensorEvent;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshot;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SnapshotLogger implements Dumpable {
    private final int mSnapshotCapacity;
    private List<Snapshot> mSnapshots;

    public class Snapshot {
        final SnapshotProtos$Snapshot mSnapshot;
        final long mTimestamp;

        Snapshot(SnapshotLogger snapshotLogger, SnapshotProtos$Snapshot snapshotProtos$Snapshot, long j) {
            this.mSnapshot = snapshotProtos$Snapshot;
            this.mTimestamp = j;
        }

        public SnapshotProtos$Snapshot getSnapshot() {
            return this.mSnapshot;
        }

        /* access modifiers changed from: 0000 */
        public long getTimestamp() {
            return this.mTimestamp;
        }
    }

    public SnapshotLogger(int i) {
        this.mSnapshotCapacity = i;
        this.mSnapshots = new ArrayList(i);
    }

    public void addSnapshot(SnapshotProtos$Snapshot snapshotProtos$Snapshot, long j) {
        if (this.mSnapshots.size() == this.mSnapshotCapacity) {
            this.mSnapshots.remove(0);
        }
        this.mSnapshots.add(new Snapshot(this, snapshotProtos$Snapshot, j));
    }

    public void didReceiveQuery() {
        if (this.mSnapshots.size() > 0) {
            List<Snapshot> list = this.mSnapshots;
            ((Snapshot) list.get(list.size() - 1)).getSnapshot().header.feedback = 1;
        }
    }

    public List<Snapshot> getSnapshots() {
        return this.mSnapshots;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            dumpInternal(printWriter);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    private void dumpInternal(PrintWriter printWriter) {
        printWriter.println("Dumping Elmyra Snapshots");
        for (int i = 0; i < this.mSnapshots.size(); i++) {
            SnapshotProtos$Snapshot snapshot = ((Snapshot) this.mSnapshots.get(i)).getSnapshot();
            StringBuilder sb = new StringBuilder();
            sb.append("SystemTime: ");
            sb.append(((Snapshot) this.mSnapshots.get(i)).getTimestamp());
            printWriter.println(sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Snapshot: ");
            sb2.append(i);
            printWriter.println(sb2.toString());
            printWriter.print("header {");
            StringBuilder sb3 = new StringBuilder();
            sb3.append("  identifier: ");
            sb3.append(snapshot.header.identifier);
            printWriter.print(sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append("  gesture_type: ");
            sb4.append(snapshot.header.gestureType);
            printWriter.print(sb4.toString());
            StringBuilder sb5 = new StringBuilder();
            sb5.append("  feedback: ");
            sb5.append(snapshot.header.feedback);
            printWriter.print(sb5.toString());
            String str = "}";
            printWriter.print(str);
            for (int i2 = 0; i2 < snapshot.events.length; i2++) {
                printWriter.print("events {");
                if (snapshot.events[i2].hasGestureStage()) {
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append("  gesture_stage: ");
                    sb6.append(snapshot.events[i2].getGestureStage());
                    printWriter.print(sb6.toString());
                } else if (snapshot.events[i2].hasSensorEvent()) {
                    ChassisProtos$SensorEvent sensorEvent = snapshot.events[i2].getSensorEvent();
                    printWriter.print("  sensor_event {");
                    StringBuilder sb7 = new StringBuilder();
                    sb7.append("    timestamp: ");
                    sb7.append(sensorEvent.timestamp);
                    printWriter.print(sb7.toString());
                    for (float append : sensorEvent.values) {
                        StringBuilder sb8 = new StringBuilder();
                        sb8.append("    values: ");
                        sb8.append(append);
                        printWriter.print(sb8.toString());
                    }
                    printWriter.print("  }");
                }
                printWriter.print(str);
            }
            StringBuilder sb9 = new StringBuilder();
            sb9.append("sensitivity_setting: ");
            sb9.append(snapshot.sensitivitySetting);
            printWriter.println(sb9.toString());
            printWriter.println();
        }
        this.mSnapshots.clear();
        printWriter.println("Finished Dumping Elmyra Snapshots");
    }
}
