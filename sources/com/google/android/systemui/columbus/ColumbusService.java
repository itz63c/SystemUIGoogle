package com.google.android.systemui.columbus;

import android.metrics.LogMaker;
import android.os.SystemClock;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dumpable;
import com.google.android.systemui.columbus.PowerManagerWrapper.WakeLockWrapper;
import com.google.android.systemui.columbus.actions.Action;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import com.google.android.systemui.columbus.gates.Gate;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import com.google.android.systemui.columbus.sensors.GestureSensor.Listener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ColumbusService.kt */
public class ColumbusService implements Dumpable {
    private final ColumbusService$actionListener$1 actionListener = new ColumbusService$actionListener$1(this);
    private final List<Action> actions;
    /* access modifiers changed from: private */
    public final Set<FeedbackEffect> effects;
    private final ColumbusService$gateListener$1 gateListener = new ColumbusService$gateListener$1(this);
    private final Set<Gate> gates;
    private final Listener gestureListener = new GestureListener();
    private final GestureSensor gestureSensor;
    private Action lastActiveAction;
    /* access modifiers changed from: private */
    public long lastProgressGesture;
    /* access modifiers changed from: private */
    public int lastStage;
    /* access modifiers changed from: private */
    public final MetricsLogger logger;
    /* access modifiers changed from: private */
    public final PowerManagerWrapper powerManager;
    /* access modifiers changed from: private */
    public final WakeLockWrapper wakeLock;

    /* compiled from: ColumbusService.kt */
    private final class GestureListener implements Listener {
        public GestureListener() {
        }

        public void onGestureProgress(GestureSensor gestureSensor, int i, DetectionProperties detectionProperties) {
            Intrinsics.checkParameterIsNotNull(gestureSensor, "sensor");
            if (i == 3) {
                onGestureDetected(detectionProperties);
                return;
            }
            Action access$updateActiveAction = ColumbusService.this.updateActiveAction();
            if (access$updateActiveAction != null) {
                access$updateActiveAction.onProgress(i, detectionProperties);
                for (FeedbackEffect onProgress : ColumbusService.this.effects) {
                    onProgress.onProgress(i, detectionProperties);
                }
            }
            if (i != ColumbusService.this.lastStage) {
                long uptimeMillis = SystemClock.uptimeMillis();
                if (i == 1) {
                    ColumbusService.this.logger.action(998);
                    ColumbusService.this.lastProgressGesture = uptimeMillis;
                } else if (i == 0 && ColumbusService.this.lastProgressGesture != 0) {
                    ColumbusService.this.logger.write(new LogMaker(997).setType(4).setLatency(uptimeMillis - ColumbusService.this.lastProgressGesture));
                }
                ColumbusService.this.lastStage = i;
            }
        }

        private final void onGestureDetected(DetectionProperties detectionProperties) {
            ColumbusService.this.wakeLock.acquire((long) 2000);
            boolean areEqual = Intrinsics.areEqual((Object) ColumbusService.this.powerManager.isInteractive(), (Object) Boolean.TRUE);
            int i = 1;
            if (detectionProperties != null && detectionProperties.isHostSuspended()) {
                i = 3;
            } else if (!areEqual) {
                i = 2;
            }
            LogMaker latency = new LogMaker(999).setType(4).setSubtype(i).setLatency(areEqual ? SystemClock.uptimeMillis() - ColumbusService.this.lastProgressGesture : 0);
            ColumbusService.this.lastProgressGesture = 0;
            Action access$updateActiveAction = ColumbusService.this.updateActiveAction();
            if (access$updateActiveAction != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Triggering ");
                sb.append(access$updateActiveAction);
                Log.i("Columbus/ColumbusService", sb.toString());
                access$updateActiveAction.onProgress(3, detectionProperties);
                for (FeedbackEffect onProgress : ColumbusService.this.effects) {
                    onProgress.onProgress(3, detectionProperties);
                }
                Intrinsics.checkExpressionValueIsNotNull(latency, "logEntry");
                latency.setPackageName(access$updateActiveAction.getClass().getName());
            }
            ColumbusService.this.logger.write(latency);
        }
    }

    public ColumbusService(List<Action> list, Set<FeedbackEffect> set, Set<Gate> set2, GestureSensor gestureSensor2, PowerManagerWrapper powerManagerWrapper, MetricsLogger metricsLogger) {
        Intrinsics.checkParameterIsNotNull(list, "actions");
        Intrinsics.checkParameterIsNotNull(set, "effects");
        Intrinsics.checkParameterIsNotNull(set2, "gates");
        Intrinsics.checkParameterIsNotNull(gestureSensor2, "gestureSensor");
        Intrinsics.checkParameterIsNotNull(powerManagerWrapper, "powerManager");
        Intrinsics.checkParameterIsNotNull(metricsLogger, "logger");
        this.actions = list;
        this.effects = set;
        this.gates = set2;
        this.gestureSensor = gestureSensor2;
        this.powerManager = powerManagerWrapper;
        this.logger = metricsLogger;
        this.wakeLock = powerManagerWrapper.newWakeLock(1, "Columbus/ColumbusService");
        for (Action listener : this.actions) {
            listener.setListener(this.actionListener);
        }
        for (Gate listener2 : this.gates) {
            listener2.setListener(this.gateListener);
        }
        this.gestureSensor.setGestureListener(this.gestureListener);
        updateSensorListener();
    }

    private final void activateGates() {
        for (Gate activate : this.gates) {
            activate.activate();
        }
    }

    private final void deactivateGates() {
        for (Gate deactivate : this.gates) {
            deactivate.deactivate();
        }
    }

    private final Gate blockingGate() {
        Object obj;
        Iterator it = this.gates.iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            if (((Gate) obj).isBlocking()) {
                break;
            }
        }
        return (Gate) obj;
    }

    private final Action firstAvailableAction() {
        Object obj;
        Iterator it = this.actions.iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            if (((Action) obj).isAvailable()) {
                break;
            }
        }
        return (Action) obj;
    }

    private final void startListening() {
        if (!this.gestureSensor.isListening()) {
            this.gestureSensor.startListening(true);
        }
    }

    private final void stopListening() {
        if (this.gestureSensor.isListening()) {
            this.gestureSensor.stopListening();
            for (FeedbackEffect onProgress : this.effects) {
                onProgress.onProgress(0, null);
            }
            Action updateActiveAction = updateActiveAction();
            if (updateActiveAction != null) {
                updateActiveAction.onProgress(0, null);
            }
        }
    }

    /* access modifiers changed from: private */
    public final Action updateActiveAction() {
        Action firstAvailableAction = firstAvailableAction();
        Action action = this.lastActiveAction;
        if (!(action == null || firstAvailableAction == action)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Switching action from ");
            sb.append(action);
            sb.append(" to ");
            sb.append(firstAvailableAction);
            Log.i("Columbus/ColumbusService", sb.toString());
            action.onProgress(0, null);
        }
        this.lastActiveAction = firstAvailableAction;
        return firstAvailableAction;
    }

    /* access modifiers changed from: private */
    public final void updateSensorListener() {
        Action updateActiveAction = updateActiveAction();
        String str = "Columbus/ColumbusService";
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
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(strArr, "args");
        StringBuilder sb = new StringBuilder();
        sb.append(ColumbusService.class.getSimpleName());
        sb.append(" state:");
        printWriter.println(sb.toString());
        printWriter.println("  Gates:");
        Iterator it = this.gates.iterator();
        while (true) {
            str = "X ";
            str2 = "O ";
            str3 = "    ";
            if (!it.hasNext()) {
                break;
            }
            Gate gate = (Gate) it.next();
            printWriter.print(str3);
            if (gate.getActive()) {
                if (!gate.isBlocking()) {
                    str = str2;
                }
                printWriter.print(str);
            } else {
                printWriter.print("- ");
            }
            printWriter.println(gate.toString());
        }
        printWriter.println("  Actions:");
        for (Action action : this.actions) {
            printWriter.print(str3);
            printWriter.print(action.isAvailable() ? str2 : str);
            printWriter.println(action.toString());
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  Active: ");
        sb2.append(this.lastActiveAction);
        printWriter.println(sb2.toString());
        printWriter.println("  Feedback Effects:");
        for (FeedbackEffect feedbackEffect : this.effects) {
            printWriter.print(str3);
            printWriter.println(feedbackEffect.toString());
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  Gesture Sensor: ");
        sb3.append(this.gestureSensor.toString());
        printWriter.println(sb3.toString());
        GestureSensor gestureSensor2 = this.gestureSensor;
        if (!(gestureSensor2 instanceof Dumpable)) {
            return;
        }
        if (gestureSensor2 != null) {
            ((Dumpable) gestureSensor2).dump(fileDescriptor, printWriter, strArr);
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type com.android.systemui.Dumpable");
    }
}
