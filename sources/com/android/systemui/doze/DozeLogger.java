package com.android.systemui.doze;

import com.android.systemui.doze.DozeMachine.State;
import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogLevel;
import com.android.systemui.log.LogMessageImpl;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DozeLogger.kt */
public final class DozeLogger {
    private final LogBuffer buffer;

    public DozeLogger(LogBuffer logBuffer) {
        Intrinsics.checkParameterIsNotNull(logBuffer, "buffer");
        this.buffer = logBuffer;
    }

    public final void logPickupWakeup(boolean z) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.DEBUG, DozeLogger$logPickupWakeup$2.INSTANCE);
        obtain.setBool1(z);
        logBuffer.push(obtain);
    }

    public final void logPulseStart(int i) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.INFO, DozeLogger$logPulseStart$2.INSTANCE);
        obtain.setInt1(i);
        logBuffer.push(obtain);
    }

    public final void logPulseFinish() {
        LogBuffer logBuffer = this.buffer;
        logBuffer.push(logBuffer.obtain("DozeLog", LogLevel.INFO, DozeLogger$logPulseFinish$2.INSTANCE));
    }

    public final void logNotificationPulse() {
        LogBuffer logBuffer = this.buffer;
        logBuffer.push(logBuffer.obtain("DozeLog", LogLevel.INFO, DozeLogger$logNotificationPulse$2.INSTANCE));
    }

    public final void logDozing(boolean z) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.INFO, DozeLogger$logDozing$2.INSTANCE);
        obtain.setBool1(z);
        logBuffer.push(obtain);
    }

    public final void logFling(boolean z, boolean z2, boolean z3, boolean z4) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.DEBUG, DozeLogger$logFling$2.INSTANCE);
        obtain.setBool1(z);
        obtain.setBool2(z2);
        obtain.setBool3(z3);
        obtain.setBool4(z4);
        logBuffer.push(obtain);
    }

    public final void logEmergencyCall() {
        LogBuffer logBuffer = this.buffer;
        logBuffer.push(logBuffer.obtain("DozeLog", LogLevel.INFO, DozeLogger$logEmergencyCall$2.INSTANCE));
    }

    public final void logKeyguardBouncerChanged(boolean z) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.INFO, DozeLogger$logKeyguardBouncerChanged$2.INSTANCE);
        obtain.setBool1(z);
        logBuffer.push(obtain);
    }

    public final void logScreenOn(boolean z) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.INFO, DozeLogger$logScreenOn$2.INSTANCE);
        obtain.setBool1(z);
        logBuffer.push(obtain);
    }

    public final void logScreenOff(int i) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.INFO, DozeLogger$logScreenOff$2.INSTANCE);
        obtain.setInt1(i);
        logBuffer.push(obtain);
    }

    public final void logMissedTick(String str) {
        Intrinsics.checkParameterIsNotNull(str, "delay");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.ERROR, DozeLogger$logMissedTick$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logTimeTickScheduled(long j, long j2) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.DEBUG, DozeLogger$logTimeTickScheduled$2.INSTANCE);
        obtain.setLong1(j);
        obtain.setLong2(j2);
        logBuffer.push(obtain);
    }

    public final void logKeyguardVisibilityChange(boolean z) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.INFO, DozeLogger$logKeyguardVisibilityChange$2.INSTANCE);
        obtain.setBool1(z);
        logBuffer.push(obtain);
    }

    public final void logDozeStateChanged(State state) {
        Intrinsics.checkParameterIsNotNull(state, "state");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.INFO, DozeLogger$logDozeStateChanged$2.INSTANCE);
        obtain.setStr1(state.name());
        logBuffer.push(obtain);
    }

    public final void logWakeDisplay(boolean z) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.DEBUG, DozeLogger$logWakeDisplay$2.INSTANCE);
        obtain.setBool1(z);
        logBuffer.push(obtain);
    }

    public final void logProximityResult(boolean z, long j, int i) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.DEBUG, DozeLogger$logProximityResult$2.INSTANCE);
        obtain.setBool1(z);
        obtain.setLong1(j);
        obtain.setInt1(i);
        logBuffer.push(obtain);
    }

    public final void logPulseDropped(boolean z, State state, boolean z2) {
        Intrinsics.checkParameterIsNotNull(state, "state");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.INFO, DozeLogger$logPulseDropped$2.INSTANCE);
        obtain.setBool1(z);
        obtain.setStr1(state.name());
        obtain.setBool2(z2);
        logBuffer.push(obtain);
    }

    public final void logPulseDropped(String str) {
        Intrinsics.checkParameterIsNotNull(str, "reason");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.INFO, DozeLogger$logPulseDropped$4.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logPulseTouchDisabledByProx(boolean z) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.DEBUG, DozeLogger$logPulseTouchDisabledByProx$2.INSTANCE);
        obtain.setBool1(z);
        logBuffer.push(obtain);
    }

    public final void logSensorTriggered(int i) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.DEBUG, DozeLogger$logSensorTriggered$2.INSTANCE);
        obtain.setInt1(i);
        logBuffer.push(obtain);
    }

    public final void logDozeSuppressed(State state) {
        Intrinsics.checkParameterIsNotNull(state, "state");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("DozeLog", LogLevel.INFO, DozeLogger$logDozeSuppressed$2.INSTANCE);
        obtain.setStr1(state.name());
        logBuffer.push(obtain);
    }
}
