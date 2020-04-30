package com.google.android.systemui.elmyra;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$SnapshotHeader;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.util.Random;

public final class SnapshotController implements com.google.android.systemui.elmyra.sensors.GestureSensor.Listener {
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                SnapshotController.this.requestSnapshot((SnapshotProtos$SnapshotHeader) message.obj);
            }
        }
    };
    private int mLastGestureStage = 0;
    private final int mSnapshotDelayAfterGesture;
    private Listener mSnapshotListener;

    public interface Listener {
        void onSnapshotRequested(SnapshotProtos$SnapshotHeader snapshotProtos$SnapshotHeader);
    }

    public SnapshotController(SnapshotConfiguration snapshotConfiguration) {
        this.mSnapshotDelayAfterGesture = snapshotConfiguration.getSnapshotDelayAfterGesture();
    }

    public void onGestureProgress(GestureSensor gestureSensor, float f, int i) {
        if (this.mLastGestureStage == 2 && i != 2) {
            SnapshotProtos$SnapshotHeader snapshotProtos$SnapshotHeader = new SnapshotProtos$SnapshotHeader();
            snapshotProtos$SnapshotHeader.identifier = new Random().nextLong();
            snapshotProtos$SnapshotHeader.gestureType = 2;
            requestSnapshot(snapshotProtos$SnapshotHeader);
        }
        this.mLastGestureStage = i;
    }

    public void onGestureDetected(GestureSensor gestureSensor, DetectionProperties detectionProperties) {
        SnapshotProtos$SnapshotHeader snapshotProtos$SnapshotHeader = new SnapshotProtos$SnapshotHeader();
        snapshotProtos$SnapshotHeader.gestureType = 1;
        snapshotProtos$SnapshotHeader.identifier = detectionProperties != null ? detectionProperties.getActionId() : 0;
        this.mLastGestureStage = 0;
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(1, snapshotProtos$SnapshotHeader), (long) this.mSnapshotDelayAfterGesture);
    }

    public void onWestworldPull() {
        SnapshotProtos$SnapshotHeader snapshotProtos$SnapshotHeader = new SnapshotProtos$SnapshotHeader();
        snapshotProtos$SnapshotHeader.gestureType = 4;
        snapshotProtos$SnapshotHeader.identifier = 0;
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(1, snapshotProtos$SnapshotHeader));
    }

    public void setListener(Listener listener) {
        this.mSnapshotListener = listener;
    }

    /* access modifiers changed from: private */
    public void requestSnapshot(SnapshotProtos$SnapshotHeader snapshotProtos$SnapshotHeader) {
        Listener listener = this.mSnapshotListener;
        if (listener != null) {
            listener.onSnapshotRequested(snapshotProtos$SnapshotHeader);
        }
    }
}
