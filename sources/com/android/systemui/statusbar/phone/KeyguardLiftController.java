package com.android.systemui.statusbar.phone;

import android.hardware.Sensor;
import android.hardware.TriggerEventListener;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.util.sensors.AsyncSensorManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: KeyguardLiftController.kt */
public final class KeyguardLiftController extends KeyguardUpdateMonitorCallback implements StateListener, Dumpable {
    private final AsyncSensorManager asyncSensorManager;
    private boolean bouncerVisible;
    /* access modifiers changed from: private */
    public boolean isListening;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor keyguardUpdateMonitor;
    private final TriggerEventListener listener = new KeyguardLiftController$listener$1(this);
    private final Sensor pickupSensor;
    private final StatusBarStateController statusBarStateController;

    public KeyguardLiftController(StatusBarStateController statusBarStateController2, AsyncSensorManager asyncSensorManager2, KeyguardUpdateMonitor keyguardUpdateMonitor2, DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(statusBarStateController2, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(asyncSensorManager2, "asyncSensorManager");
        Intrinsics.checkParameterIsNotNull(keyguardUpdateMonitor2, "keyguardUpdateMonitor");
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        this.statusBarStateController = statusBarStateController2;
        this.asyncSensorManager = asyncSensorManager2;
        this.keyguardUpdateMonitor = keyguardUpdateMonitor2;
        this.pickupSensor = asyncSensorManager2.getDefaultSensor(25);
        String name = KeyguardLiftController.class.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, "javaClass.name");
        dumpManager.registerDumpable(name, this);
        this.statusBarStateController.addCallback(this);
        this.keyguardUpdateMonitor.registerCallback(this);
        updateListeningState();
    }

    public void onDozingChanged(boolean z) {
        updateListeningState();
    }

    public void onKeyguardBouncerChanged(boolean z) {
        this.bouncerVisible = z;
        updateListeningState();
    }

    public void onKeyguardVisibilityChanged(boolean z) {
        updateListeningState();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(strArr, "args");
        printWriter.println("KeyguardLiftController:");
        StringBuilder sb = new StringBuilder();
        sb.append("  pickupSensor: ");
        sb.append(this.pickupSensor);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  isListening: ");
        sb2.append(this.isListening);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  bouncerVisible: ");
        sb3.append(this.bouncerVisible);
        printWriter.println(sb3.toString());
    }

    /* access modifiers changed from: private */
    public final void updateListeningState() {
        if (this.pickupSensor != null) {
            boolean z = true;
            if (!(this.keyguardUpdateMonitor.isKeyguardVisible() && !this.statusBarStateController.isDozing()) && !this.bouncerVisible) {
                z = false;
            }
            if (z != this.isListening) {
                this.isListening = z;
                if (z) {
                    this.asyncSensorManager.requestTriggerSensor(this.listener, this.pickupSensor);
                } else {
                    this.asyncSensorManager.cancelTriggerSensor(this.listener, this.pickupSensor);
                }
            }
        }
    }
}
