package com.google.android.systemui.elmyra.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Binder;
import android.os.SystemClock;
import android.util.Slog;
import android.util.TypedValue;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2012R$integer;
import com.android.systemui.Dumpable;
import com.google.android.systemui.elmyra.SnapshotConfiguration;
import com.google.android.systemui.elmyra.SnapshotController;
import com.google.android.systemui.elmyra.SnapshotLogger;
import com.google.android.systemui.elmyra.SnapshotLogger.Snapshot;
import com.google.android.systemui.elmyra.WestworldLogger;
import com.google.android.systemui.elmyra.proto.nano.ChassisProtos$Chassis;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshot;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshots;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import com.google.android.systemui.elmyra.sensors.GestureSensor.Listener;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import com.google.protobuf.nano.MessageNano;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

class AssistGestureController implements Dumpable {
    private ChassisProtos$Chassis mChassis;
    /* access modifiers changed from: private */
    public SnapshotLogger mCompleteGestures;
    private final long mFalsePrimeWindow;
    private final GestureConfiguration mGestureConfiguration;
    private final long mGestureCooldownTime;
    private Listener mGestureListener;
    private float mGestureProgress;
    private final GestureSensor mGestureSensor;
    private SnapshotLogger mIncompleteGestures;
    private boolean mIsFalsePrimed;
    private long mLastDetectionTime;
    private OPAQueryReceiver mOpaQueryReceiver;
    private final float mProgressAlpha;
    private final float mProgressReportThreshold;
    private final SnapshotController mSnapshotController;
    /* access modifiers changed from: private */
    public WestworldLogger mWestworldLogger;

    private class OPAQueryReceiver extends BroadcastReceiver {
        private OPAQueryReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.google.android.systemui.OPA_ELMYRA_QUERY_SUBMITTED")) {
                AssistGestureController.this.mCompleteGestures.didReceiveQuery();
                AssistGestureController.this.mWestworldLogger.querySubmitted();
            }
        }
    }

    AssistGestureController(Context context, GestureSensor gestureSensor, GestureConfiguration gestureConfiguration) {
        this(context, gestureSensor, gestureConfiguration, null);
    }

    AssistGestureController(Context context, GestureSensor gestureSensor, GestureConfiguration gestureConfiguration, SnapshotConfiguration snapshotConfiguration) {
        this.mOpaQueryReceiver = new OPAQueryReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.google.android.systemui.OPA_ELMYRA_QUERY_SUBMITTED");
        context.registerReceiver(this.mOpaQueryReceiver, intentFilter);
        this.mGestureSensor = gestureSensor;
        this.mGestureConfiguration = gestureConfiguration;
        Resources resources = context.getResources();
        TypedValue typedValue = new TypedValue();
        int i = 0;
        this.mCompleteGestures = new SnapshotLogger(snapshotConfiguration != null ? snapshotConfiguration.getCompleteGestures() : 0);
        if (snapshotConfiguration != null) {
            i = snapshotConfiguration.getIncompleteGestures();
        }
        this.mIncompleteGestures = new SnapshotLogger(i);
        resources.getValue(C2009R$dimen.elmyra_progress_alpha, typedValue, true);
        this.mProgressAlpha = typedValue.getFloat();
        resources.getValue(C2009R$dimen.elmyra_progress_report_threshold, typedValue, true);
        this.mProgressReportThreshold = typedValue.getFloat();
        long integer = (long) resources.getInteger(C2012R$integer.elmyra_gesture_cooldown_time);
        this.mGestureCooldownTime = integer;
        this.mFalsePrimeWindow = integer + ((long) resources.getInteger(C2012R$integer.elmyra_false_prime_window));
        if (snapshotConfiguration != null) {
            this.mSnapshotController = new SnapshotController(snapshotConfiguration);
        } else {
            this.mSnapshotController = null;
        }
        this.mWestworldLogger = new WestworldLogger(context, this.mGestureConfiguration, this.mSnapshotController);
    }

    public void setGestureListener(Listener listener) {
        this.mGestureListener = listener;
    }

    public void setSnapshotListener(SnapshotController.Listener listener) {
        SnapshotController snapshotController = this.mSnapshotController;
        if (snapshotController != null) {
            snapshotController.setListener(listener);
        }
    }

    public void storeChassisConfiguration(ChassisProtos$Chassis chassisProtos$Chassis) {
        this.mChassis = chassisProtos$Chassis;
        this.mWestworldLogger.didReceiveChassis(chassisProtos$Chassis);
    }

    public ChassisProtos$Chassis getChassisConfiguration() {
        return this.mChassis;
    }

    public void onGestureProgress(float f) {
        if (f == 0.0f) {
            this.mGestureProgress = 0.0f;
            this.mIsFalsePrimed = false;
        } else {
            float f2 = this.mProgressAlpha;
            this.mGestureProgress = (f2 * f) + ((1.0f - f2) * this.mGestureProgress);
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        long j = this.mLastDetectionTime;
        if (uptimeMillis - j >= this.mGestureCooldownTime && !this.mIsFalsePrimed) {
            int i = ((uptimeMillis - j) > this.mFalsePrimeWindow ? 1 : ((uptimeMillis - j) == this.mFalsePrimeWindow ? 0 : -1));
            int i2 = 1;
            if (i >= 0 || f != 1.0f) {
                float f3 = this.mGestureProgress;
                float f4 = this.mProgressReportThreshold;
                if (f3 < f4) {
                    sendGestureProgress(this.mGestureSensor, 0.0f, 0);
                    this.mWestworldLogger.onGestureProgress(this.mGestureSensor, 0.0f, 0);
                } else {
                    float f5 = (f3 - f4) / (1.0f - f4);
                    if (f == 1.0f) {
                        i2 = 2;
                    }
                    sendGestureProgress(this.mGestureSensor, f5, i2);
                    this.mWestworldLogger.onGestureProgress(this.mGestureSensor, f5, i2);
                }
            } else {
                this.mIsFalsePrimed = true;
            }
        }
    }

    public void onGestureDetected(DetectionProperties detectionProperties) {
        long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis - this.mLastDetectionTime >= this.mGestureCooldownTime && !this.mIsFalsePrimed) {
            Listener listener = this.mGestureListener;
            if (listener != null) {
                listener.onGestureDetected(this.mGestureSensor, detectionProperties);
            }
            SnapshotController snapshotController = this.mSnapshotController;
            if (snapshotController != null) {
                snapshotController.onGestureDetected(this.mGestureSensor, detectionProperties);
            }
            this.mWestworldLogger.onGestureDetected(this.mGestureSensor, detectionProperties);
            this.mLastDetectionTime = uptimeMillis;
        }
    }

    public void onSnapshotReceived(SnapshotProtos$Snapshot snapshotProtos$Snapshot) {
        int i = snapshotProtos$Snapshot.header.gestureType;
        if (i == 4) {
            this.mWestworldLogger.didReceiveSnapshot(snapshotProtos$Snapshot);
        } else if (i == 1) {
            this.mCompleteGestures.addSnapshot(snapshotProtos$Snapshot, System.currentTimeMillis());
        } else {
            this.mIncompleteGestures.addSnapshot(snapshotProtos$Snapshot, System.currentTimeMillis());
        }
    }

    private void sendGestureProgress(GestureSensor gestureSensor, float f, int i) {
        Listener listener = this.mGestureListener;
        if (listener != null) {
            listener.onGestureProgress(gestureSensor, f, i);
        }
        SnapshotController snapshotController = this.mSnapshotController;
        if (snapshotController != null) {
            snapshotController.onGestureProgress(gestureSensor, f, i);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (this.mChassis != null) {
            for (int i = 0; i < this.mChassis.sensors.length; i++) {
                printWriter.print("sensors {");
                StringBuilder sb = new StringBuilder();
                sb.append("  source: ");
                sb.append(this.mChassis.sensors[i].source);
                printWriter.print(sb.toString());
                StringBuilder sb2 = new StringBuilder();
                sb2.append("  gain: ");
                sb2.append(this.mChassis.sensors[i].gain);
                printWriter.print(sb2.toString());
                StringBuilder sb3 = new StringBuilder();
                sb3.append("  sensitivity: ");
                sb3.append(this.mChassis.sensors[i].sensitivity);
                printWriter.print(sb3.toString());
                printWriter.print("}");
            }
            printWriter.println();
        }
        boolean z = false;
        boolean z2 = false;
        for (String str : strArr) {
            if (str.equals("GoogleServices")) {
                z = true;
            } else if (str.equals("proto")) {
                z2 = true;
            }
        }
        if (!z || !z2) {
            this.mCompleteGestures.dump(fileDescriptor, printWriter, strArr);
            this.mIncompleteGestures.dump(fileDescriptor, printWriter, strArr);
        } else {
            dumpProto(fileDescriptor);
        }
        StringBuilder sb4 = new StringBuilder();
        sb4.append("user_sensitivity: ");
        sb4.append(this.mGestureConfiguration.getSensitivity());
        printWriter.println(sb4.toString());
    }

    private void dumpProto(FileDescriptor fileDescriptor) {
        List snapshots = this.mIncompleteGestures.getSnapshots();
        List snapshots2 = this.mCompleteGestures.getSnapshots();
        if (snapshots.size() + snapshots2.size() != 0) {
            SnapshotProtos$Snapshots snapshotProtos$Snapshots = new SnapshotProtos$Snapshots();
            snapshotProtos$Snapshots.snapshots = new SnapshotProtos$Snapshot[(snapshots.size() + snapshots2.size())];
            int i = 0;
            while (i < snapshots.size()) {
                snapshotProtos$Snapshots.snapshots[i] = ((Snapshot) snapshots.get(i)).getSnapshot();
                i++;
            }
            for (int i2 = 0; i2 < snapshots2.size(); i2++) {
                snapshotProtos$Snapshots.snapshots[i + i2] = ((Snapshot) snapshots2.get(i2)).getSnapshot();
            }
            byte[] byteArray = MessageNano.toByteArray(snapshotProtos$Snapshots);
            FileOutputStream fileOutputStream = new FileOutputStream(fileDescriptor);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                fileOutputStream.write(byteArray);
                fileOutputStream.flush();
            } catch (IOException unused) {
                Slog.e("Elmyra/AssistGestureController", "Error writing to output stream");
            } catch (Throwable th) {
                this.mCompleteGestures.getSnapshots().clear();
                this.mIncompleteGestures.getSnapshots().clear();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th;
            }
            this.mCompleteGestures.getSnapshots().clear();
            this.mIncompleteGestures.getSnapshots().clear();
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
}
