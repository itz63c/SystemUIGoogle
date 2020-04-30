package com.google.android.systemui.elmyra;

import android.content.Context;
import android.metrics.LogMaker;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dumpable;
import com.google.android.systemui.elmyra.actions.Action;
import com.google.android.systemui.elmyra.actions.Action.Listener;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ElmyraService implements Dumpable {
    private final Listener mActionListener = new Listener() {
        public void onActionAvailabilityChanged(Action action) {
            ElmyraService.this.updateSensorListener();
        }
    };
    private final List<Action> mActions;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final List<FeedbackEffect> mFeedbackEffects;
    private final Gate.Listener mGateListener = new Gate.Listener() {
        public void onGateChanged(Gate gate) {
            ElmyraService.this.updateSensorListener();
        }
    };
    private final List<Gate> mGates;
    private final GestureSensor.Listener mGestureListener = new GestureListener();
    private final GestureSensor mGestureSensor;
    private Action mLastActiveAction;
    /* access modifiers changed from: private */
    public long mLastPrimedGesture;
    /* access modifiers changed from: private */
    public int mLastStage;
    /* access modifiers changed from: private */
    public final MetricsLogger mLogger;
    /* access modifiers changed from: private */
    public final PowerManager mPowerManager;
    /* access modifiers changed from: private */
    public final WakeLock mWakeLock;

    private class GestureListener implements GestureSensor.Listener {
        private GestureListener() {
        }

        public void onGestureProgress(GestureSensor gestureSensor, float f, int i) {
            Action access$100 = ElmyraService.this.updateActiveAction();
            if (access$100 != null) {
                access$100.onProgress(f, i);
                for (int i2 = 0; i2 < ElmyraService.this.mFeedbackEffects.size(); i2++) {
                    ((FeedbackEffect) ElmyraService.this.mFeedbackEffects.get(i2)).onProgress(f, i);
                }
            }
            if (i != ElmyraService.this.mLastStage) {
                long uptimeMillis = SystemClock.uptimeMillis();
                if (i == 2) {
                    ElmyraService.this.mLogger.action(998);
                    ElmyraService.this.mLastPrimedGesture = uptimeMillis;
                } else if (i == 0 && ElmyraService.this.mLastPrimedGesture != 0) {
                    ElmyraService.this.mLogger.write(new LogMaker(997).setType(4).setLatency(uptimeMillis - ElmyraService.this.mLastPrimedGesture));
                }
                ElmyraService.this.mLastStage = i;
            }
        }

        public void onGestureDetected(GestureSensor gestureSensor, DetectionProperties detectionProperties) {
            ElmyraService.this.mWakeLock.acquire(2000);
            boolean isInteractive = ElmyraService.this.mPowerManager.isInteractive();
            int i = (detectionProperties == null || !detectionProperties.isHostSuspended()) ? !isInteractive ? 2 : 1 : 3;
            LogMaker latency = new LogMaker(999).setType(4).setSubtype(i).setLatency(isInteractive ? SystemClock.uptimeMillis() - ElmyraService.this.mLastPrimedGesture : 0);
            ElmyraService.this.mLastPrimedGesture = 0;
            Action access$100 = ElmyraService.this.updateActiveAction();
            if (access$100 != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Triggering ");
                sb.append(access$100);
                Log.i("Elmyra/ElmyraService", sb.toString());
                access$100.onTrigger(detectionProperties);
                for (int i2 = 0; i2 < ElmyraService.this.mFeedbackEffects.size(); i2++) {
                    ((FeedbackEffect) ElmyraService.this.mFeedbackEffects.get(i2)).onResolve(detectionProperties);
                }
                latency.setPackageName(access$100.getClass().getName());
            }
            ElmyraService.this.mLogger.write(latency);
        }
    }

    public ElmyraService(Context context, ServiceConfiguration serviceConfiguration) {
        this.mContext = context;
        this.mLogger = new MetricsLogger();
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService("power");
        this.mPowerManager = powerManager;
        this.mWakeLock = powerManager.newWakeLock(1, "Elmyra/ElmyraService");
        ArrayList arrayList = new ArrayList(serviceConfiguration.getActions());
        this.mActions = arrayList;
        arrayList.forEach(new Consumer() {
            public final void accept(Object obj) {
                ElmyraService.this.lambda$new$0$ElmyraService((Action) obj);
            }
        });
        this.mFeedbackEffects = new ArrayList(serviceConfiguration.getFeedbackEffects());
        ArrayList arrayList2 = new ArrayList(serviceConfiguration.getGates());
        this.mGates = arrayList2;
        arrayList2.forEach(new Consumer() {
            public final void accept(Object obj) {
                ElmyraService.this.lambda$new$1$ElmyraService((Gate) obj);
            }
        });
        GestureSensor gestureSensor = serviceConfiguration.getGestureSensor();
        this.mGestureSensor = gestureSensor;
        if (gestureSensor != null) {
            gestureSensor.setGestureListener(this.mGestureListener);
        }
        updateSensorListener();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ElmyraService(Action action) {
        action.setListener(this.mActionListener);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$ElmyraService(Gate gate) {
        gate.setListener(this.mGateListener);
    }

    private void activateGates() {
        for (int i = 0; i < this.mGates.size(); i++) {
            ((Gate) this.mGates.get(i)).activate();
        }
    }

    private void deactivateGates() {
        for (int i = 0; i < this.mGates.size(); i++) {
            ((Gate) this.mGates.get(i)).deactivate();
        }
    }

    private Gate blockingGate() {
        for (int i = 0; i < this.mGates.size(); i++) {
            if (((Gate) this.mGates.get(i)).isBlocking()) {
                return (Gate) this.mGates.get(i);
            }
        }
        return null;
    }

    private Action firstAvailableAction() {
        for (int i = 0; i < this.mActions.size(); i++) {
            if (((Action) this.mActions.get(i)).isAvailable()) {
                return (Action) this.mActions.get(i);
            }
        }
        return null;
    }

    private void startListening() {
        GestureSensor gestureSensor = this.mGestureSensor;
        if (gestureSensor != null && !gestureSensor.isListening()) {
            this.mGestureSensor.startListening();
        }
    }

    private void stopListening() {
        GestureSensor gestureSensor = this.mGestureSensor;
        if (gestureSensor != null && gestureSensor.isListening()) {
            this.mGestureSensor.stopListening();
            for (int i = 0; i < this.mFeedbackEffects.size(); i++) {
                ((FeedbackEffect) this.mFeedbackEffects.get(i)).onRelease();
            }
            Action updateActiveAction = updateActiveAction();
            if (updateActiveAction != null) {
                updateActiveAction.onProgress(0.0f, 0);
            }
        }
    }

    /* access modifiers changed from: private */
    public Action updateActiveAction() {
        Action firstAvailableAction = firstAvailableAction();
        Action action = this.mLastActiveAction;
        if (!(action == null || firstAvailableAction == action)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Switching action from ");
            sb.append(this.mLastActiveAction);
            sb.append(" to ");
            sb.append(firstAvailableAction);
            Log.i("Elmyra/ElmyraService", sb.toString());
            this.mLastActiveAction.onProgress(0.0f, 0);
        }
        this.mLastActiveAction = firstAvailableAction;
        return firstAvailableAction;
    }

    /* access modifiers changed from: protected */
    public void updateSensorListener() {
        Action updateActiveAction = updateActiveAction();
        String str = "Elmyra/ElmyraService";
        if (updateActiveAction == null) {
            Log.i(str, "No available actions");
            deactivateGates();
            stopListening();
            return;
        }
        activateGates();
        Gate blockingGate = blockingGate();
        if (blockingGate != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Gated by ");
            sb.append(blockingGate);
            Log.i(str, sb.toString());
            stopListening();
            return;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Unblocked; current action: ");
        sb2.append(updateActiveAction);
        Log.i(str, sb2.toString());
        startListening();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        String str2;
        String str3;
        StringBuilder sb = new StringBuilder();
        sb.append(ElmyraService.class.getSimpleName());
        sb.append(" state:");
        printWriter.println(sb.toString());
        printWriter.println("  Gates:");
        int i = 0;
        while (true) {
            str = "X ";
            str2 = "O ";
            str3 = "    ";
            if (i >= this.mGates.size()) {
                break;
            }
            printWriter.print(str3);
            if (((Gate) this.mGates.get(i)).isActive()) {
                if (!((Gate) this.mGates.get(i)).isBlocking()) {
                    str = str2;
                }
                printWriter.print(str);
            } else {
                printWriter.print("- ");
            }
            printWriter.println(((Gate) this.mGates.get(i)).toString());
            i++;
        }
        printWriter.println("  Actions:");
        for (int i2 = 0; i2 < this.mActions.size(); i2++) {
            printWriter.print(str3);
            printWriter.print(((Action) this.mActions.get(i2)).isAvailable() ? str2 : str);
            printWriter.println(((Action) this.mActions.get(i2)).toString());
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  Active: ");
        sb2.append(this.mLastActiveAction);
        printWriter.println(sb2.toString());
        printWriter.println("  Feedback Effects:");
        for (int i3 = 0; i3 < this.mFeedbackEffects.size(); i3++) {
            printWriter.print(str3);
            printWriter.println(((FeedbackEffect) this.mFeedbackEffects.get(i3)).toString());
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  Gesture Sensor: ");
        sb3.append(this.mGestureSensor.toString());
        printWriter.println(sb3.toString());
        GestureSensor gestureSensor = this.mGestureSensor;
        if (gestureSensor instanceof Dumpable) {
            ((Dumpable) gestureSensor).dump(fileDescriptor, printWriter, strArr);
        }
    }
}
