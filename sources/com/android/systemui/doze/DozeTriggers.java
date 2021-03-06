package com.android.systemui.doze;

import android.app.AlarmManager;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.AmbientDisplayConfiguration;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dock.DockManager;
import com.android.systemui.doze.DozeHost.Callback;
import com.android.systemui.doze.DozeMachine.Part;
import com.android.systemui.doze.DozeMachine.State;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.util.Assert;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.systemui.util.sensors.ProximitySensor;
import com.android.systemui.util.sensors.ProximitySensor.ProximityCheck;
import com.android.systemui.util.wakelock.WakeLock;
import java.io.PrintWriter;
import java.util.function.Consumer;

public class DozeTriggers implements Part {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = DozeService.DEBUG;
    private static boolean sWakeDisplaySensorState = true;
    private final boolean mAllowPulseTriggers;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final TriggerReceiver mBroadcastReceiver = new TriggerReceiver();
    /* access modifiers changed from: private */
    public final AmbientDisplayConfiguration mConfig;
    private final Context mContext;
    private final DockEventListener mDockEventListener = new DockEventListener();
    private final DockManager mDockManager;
    /* access modifiers changed from: private */
    public final DozeHost mDozeHost;
    private final DozeLog mDozeLog;
    private final DozeParameters mDozeParameters;
    /* access modifiers changed from: private */
    public final DozeSensors mDozeSensors;
    private Callback mHostCallback = new Callback() {
        public void onNotificationAlerted(Runnable runnable) {
            DozeTriggers.this.onNotification(runnable);
        }

        public void onPowerSaveChanged(boolean z) {
            if (DozeTriggers.this.mDozeHost.isPowerSaveActive()) {
                DozeTriggers.this.mMachine.requestState(State.DOZE);
            }
        }

        public void onDozeSuppressedChanged(boolean z) {
            State state;
            if (!DozeTriggers.this.mConfig.alwaysOnEnabled(-2) || z) {
                state = State.DOZE;
            } else {
                state = State.DOZE_AOD;
            }
            DozeTriggers.this.mMachine.requestState(state);
        }
    };
    /* access modifiers changed from: private */
    public final DozeMachine mMachine;
    private final MetricsLogger mMetricsLogger = ((MetricsLogger) Dependency.get(MetricsLogger.class));
    private long mNotificationPulseTime;
    private final ProximityCheck mProxCheck;
    private boolean mPulsePending;
    private final AsyncSensorManager mSensorManager;
    private final UiModeManager mUiModeManager;
    private final WakeLock mWakeLock;

    /* renamed from: com.android.systemui.doze.DozeTriggers$2 */
    static /* synthetic */ class C08302 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$doze$DozeMachine$State;

        /* JADX WARNING: Can't wrap try/catch for region: R(20:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|(3:19|20|22)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(22:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|22) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0060 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x006c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.systemui.doze.DozeMachine$State[] r0 = com.android.systemui.doze.DozeMachine.State.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$systemui$doze$DozeMachine$State = r0
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.INITIALIZED     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD_PAUSED     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD_PAUSING     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_PULSING     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_PULSING_BRIGHT     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD_DOCKED     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x006c }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_PULSE_DONE     // Catch:{ NoSuchFieldError -> 0x006c }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x006c }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x006c }
            L_0x006c:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0078 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.FINISH     // Catch:{ NoSuchFieldError -> 0x0078 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0078 }
                r2 = 10
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0078 }
            L_0x0078:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.doze.DozeTriggers.C08302.<clinit>():void");
        }
    }

    private class DockEventListener implements com.android.systemui.dock.DockManager.DockEventListener {
        private DockEventListener() {
        }

        public void onEvent(int i) {
            if (DozeTriggers.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("dock event = ");
                sb.append(i);
                Log.d("DozeTriggers", sb.toString());
            }
            if (i == 0) {
                DozeTriggers.this.mDozeSensors.ignoreTouchScreenSensorsSettingInterferingWithDocking(false);
            } else if (i == 1 || i == 2) {
                DozeTriggers.this.mDozeSensors.ignoreTouchScreenSensorsSettingInterferingWithDocking(true);
            }
        }
    }

    private class TriggerReceiver extends BroadcastReceiver {
        private boolean mRegistered;

        private TriggerReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("com.android.systemui.doze.pulse".equals(intent.getAction())) {
                if (DozeMachine.DEBUG) {
                    Log.d("DozeTriggers", "Received pulse intent");
                }
                DozeTriggers.this.requestPulse(0, false, null);
            }
            if (UiModeManager.ACTION_ENTER_CAR_MODE.equals(intent.getAction())) {
                DozeTriggers.this.mMachine.requestState(State.FINISH);
            }
            if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                DozeTriggers.this.mDozeSensors.onUserSwitched();
            }
        }

        public void register(BroadcastDispatcher broadcastDispatcher) {
            if (!this.mRegistered) {
                IntentFilter intentFilter = new IntentFilter("com.android.systemui.doze.pulse");
                intentFilter.addAction(UiModeManager.ACTION_ENTER_CAR_MODE);
                intentFilter.addAction("android.intent.action.USER_SWITCHED");
                broadcastDispatcher.registerReceiver(this, intentFilter);
                this.mRegistered = true;
            }
        }

        public void unregister(BroadcastDispatcher broadcastDispatcher) {
            if (this.mRegistered) {
                broadcastDispatcher.unregisterReceiver(this);
                this.mRegistered = false;
            }
        }
    }

    public DozeTriggers(Context context, DozeMachine dozeMachine, DozeHost dozeHost, AlarmManager alarmManager, AmbientDisplayConfiguration ambientDisplayConfiguration, DozeParameters dozeParameters, AsyncSensorManager asyncSensorManager, Handler handler, WakeLock wakeLock, boolean z, DockManager dockManager, ProximitySensor proximitySensor, DozeLog dozeLog, BroadcastDispatcher broadcastDispatcher) {
        this.mContext = context;
        this.mMachine = dozeMachine;
        this.mDozeHost = dozeHost;
        AmbientDisplayConfiguration ambientDisplayConfiguration2 = ambientDisplayConfiguration;
        this.mConfig = ambientDisplayConfiguration2;
        DozeParameters dozeParameters2 = dozeParameters;
        this.mDozeParameters = dozeParameters2;
        this.mSensorManager = asyncSensorManager;
        WakeLock wakeLock2 = wakeLock;
        this.mWakeLock = wakeLock2;
        this.mAllowPulseTriggers = z;
        DozeSensors dozeSensors = new DozeSensors(context, alarmManager, this.mSensorManager, dozeParameters2, ambientDisplayConfiguration2, wakeLock2, new DozeSensors.Callback() {
            public final void onSensorPulse(int i, float f, float f2, float[] fArr) {
                DozeTriggers.this.onSensor(i, f, f2, fArr);
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                DozeTriggers.this.onProximityFar(((Boolean) obj).booleanValue());
            }
        }, dozeLog);
        this.mDozeSensors = dozeSensors;
        this.mUiModeManager = (UiModeManager) this.mContext.getSystemService(UiModeManager.class);
        this.mDockManager = dockManager;
        this.mProxCheck = new ProximityCheck(proximitySensor, handler);
        this.mDozeLog = dozeLog;
        this.mBroadcastDispatcher = broadcastDispatcher;
    }

    /* access modifiers changed from: private */
    public void onNotification(Runnable runnable) {
        String str = "DozeTriggers";
        if (DozeMachine.DEBUG) {
            Log.d(str, "requestNotificationPulse");
        }
        if (!sWakeDisplaySensorState) {
            Log.d(str, "Wake display false. Pulse denied.");
            runIfNotNull(runnable);
            this.mDozeLog.tracePulseDropped("wakeDisplaySensor");
            return;
        }
        this.mNotificationPulseTime = SystemClock.elapsedRealtime();
        if (!this.mConfig.pulseOnNotificationEnabled(-2)) {
            runIfNotNull(runnable);
            this.mDozeLog.tracePulseDropped("pulseOnNotificationsDisabled");
        } else if (this.mDozeHost.isDozeSuppressed()) {
            runIfNotNull(runnable);
            this.mDozeLog.tracePulseDropped("dozeSuppressed");
        } else {
            requestPulse(1, false, runnable);
            this.mDozeLog.traceNotificationPulse();
        }
    }

    private static void runIfNotNull(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    private void proximityCheckThenCall(Consumer<Boolean> consumer, boolean z, int i) {
        Boolean isProximityCurrentlyNear = this.mDozeSensors.isProximityCurrentlyNear();
        if (z) {
            consumer.accept(null);
        } else if (isProximityCurrentlyNear != null) {
            consumer.accept(isProximityCurrentlyNear);
        } else {
            long uptimeMillis = SystemClock.uptimeMillis();
            ProximityCheck proximityCheck = this.mProxCheck;
            $$Lambda$DozeTriggers$7dHaL16QO2EYQ_3R1TKZzEi3lA r0 = new Consumer(uptimeMillis, i, consumer) {
                public final /* synthetic */ long f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ Consumer f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                }

                public final void accept(Object obj) {
                    DozeTriggers.this.lambda$proximityCheckThenCall$0$DozeTriggers(this.f$1, this.f$2, this.f$3, (Boolean) obj);
                }
            };
            proximityCheck.check(500, r0);
            this.mWakeLock.acquire("DozeTriggers");
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$proximityCheckThenCall$0 */
    public /* synthetic */ void lambda$proximityCheckThenCall$0$DozeTriggers(long j, int i, Consumer consumer, Boolean bool) {
        boolean z;
        long uptimeMillis = SystemClock.uptimeMillis();
        DozeLog dozeLog = this.mDozeLog;
        if (bool == null) {
            z = false;
        } else {
            z = bool.booleanValue();
        }
        dozeLog.traceProximityResult(z, uptimeMillis - j, i);
        consumer.accept(bool);
        this.mWakeLock.release("DozeTriggers");
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void onSensor(int i, float f, float f2, float[] fArr) {
        boolean z = false;
        boolean z2 = i == 4;
        boolean z3 = i == 9;
        boolean z4 = i == 3;
        boolean z5 = i == 5;
        boolean z6 = i == 7;
        boolean z7 = i == 8;
        boolean z8 = (fArr == null || fArr.length <= 0 || fArr[0] == 0.0f) ? false : true;
        State state = null;
        if (z6) {
            if (!this.mMachine.isExecutingTransition()) {
                state = this.mMachine.getState();
            }
            onWakeScreen(z8, state);
        } else if (z5) {
            requestPulse(i, true, null);
        } else if (!z7) {
            $$Lambda$DozeTriggers$_9uGVeOllRSk5IFkZMhDAbIz6Gw r3 = new Consumer(z2, z3, f, f2, i, z4) {
                public final /* synthetic */ boolean f$1;
                public final /* synthetic */ boolean f$2;
                public final /* synthetic */ float f$3;
                public final /* synthetic */ float f$4;
                public final /* synthetic */ int f$5;
                public final /* synthetic */ boolean f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void accept(Object obj) {
                    DozeTriggers.this.lambda$onSensor$1$DozeTriggers(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, (Boolean) obj);
                }
            };
            proximityCheckThenCall(r3, true, i);
        } else if (z8) {
            requestPulse(i, true, null);
        }
        if (z4) {
            if (SystemClock.elapsedRealtime() - this.mNotificationPulseTime < ((long) this.mDozeParameters.getPickupVibrationThreshold())) {
                z = true;
            }
            this.mDozeLog.tracePickupWakeUp(z);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onSensor$1 */
    public /* synthetic */ void lambda$onSensor$1$DozeTriggers(boolean z, boolean z2, float f, float f2, int i, boolean z3, Boolean bool) {
        if (bool == null || !bool.booleanValue()) {
            if (z || z2) {
                if (!(f == -1.0f || f2 == -1.0f)) {
                    this.mDozeHost.onSlpiTap(f, f2);
                }
                gentleWakeUp(i);
            } else if (z3) {
                gentleWakeUp(i);
            } else {
                this.mDozeHost.extendPulse(i);
            }
        }
    }

    private void gentleWakeUp(int i) {
        this.mMetricsLogger.write(new LogMaker(223).setType(6).setSubtype(i));
        if (this.mDozeParameters.getDisplayNeedsBlanking()) {
            this.mDozeHost.setAodDimmingScrim(1.0f);
        }
        this.mMachine.wakeUp();
    }

    /* access modifiers changed from: private */
    public void onProximityFar(boolean z) {
        String str = "DozeTriggers";
        if (this.mMachine.isExecutingTransition()) {
            Log.w(str, "onProximityFar called during transition. Ignoring sensor response.");
            return;
        }
        boolean z2 = !z;
        State state = this.mMachine.getState();
        boolean z3 = false;
        boolean z4 = state == State.DOZE_AOD_PAUSED;
        boolean z5 = state == State.DOZE_AOD_PAUSING;
        if (state == State.DOZE_AOD) {
            z3 = true;
        }
        if (state == State.DOZE_PULSING || state == State.DOZE_PULSING_BRIGHT) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Prox changed, ignore touch = ");
                sb.append(z2);
                Log.i(str, sb.toString());
            }
            this.mDozeHost.onIgnoreTouchWhilePulsing(z2);
        }
        if (z && (z4 || z5)) {
            if (DEBUG) {
                Log.i(str, "Prox FAR, unpausing AOD");
            }
            this.mMachine.requestState(State.DOZE_AOD);
        } else if (z2 && z3) {
            if (DEBUG) {
                Log.i(str, "Prox NEAR, pausing AOD");
            }
            this.mMachine.requestState(State.DOZE_AOD_PAUSING);
        }
    }

    private void onWakeScreen(boolean z, State state) {
        this.mDozeLog.traceWakeDisplay(z);
        sWakeDisplaySensorState = z;
        boolean z2 = true;
        if (z) {
            proximityCheckThenCall(new Consumer(state) {
                public final /* synthetic */ State f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    DozeTriggers.this.lambda$onWakeScreen$2$DozeTriggers(this.f$1, (Boolean) obj);
                }
            }, true, 7);
            return;
        }
        boolean z3 = state == State.DOZE_AOD_PAUSED;
        if (state != State.DOZE_AOD_PAUSING) {
            z2 = false;
        }
        if (!z2 && !z3) {
            this.mMachine.requestState(State.DOZE);
            this.mMetricsLogger.write(new LogMaker(223).setType(2).setSubtype(7));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onWakeScreen$2 */
    public /* synthetic */ void lambda$onWakeScreen$2$DozeTriggers(State state, Boolean bool) {
        if ((bool == null || !bool.booleanValue()) && state == State.DOZE) {
            this.mMachine.requestState(State.DOZE_AOD);
            this.mMetricsLogger.write(new LogMaker(223).setType(1).setSubtype(7));
        }
    }

    public void transitionTo(State state, State state2) {
        switch (C08302.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()]) {
            case 1:
                this.mBroadcastReceiver.register(this.mBroadcastDispatcher);
                this.mDozeHost.addCallback(this.mHostCallback);
                this.mDockManager.addListener(this.mDockEventListener);
                this.mDozeSensors.requestTemporaryDisable();
                checkTriggersAtInit();
                return;
            case 2:
            case 3:
                this.mDozeSensors.setProxListening(state2 != State.DOZE);
                this.mDozeSensors.setListening(true);
                this.mDozeSensors.setPaused(false);
                if (state2 == State.DOZE_AOD && !sWakeDisplaySensorState) {
                    onWakeScreen(false, state2);
                    return;
                }
                return;
            case 4:
            case 5:
                this.mDozeSensors.setProxListening(true);
                this.mDozeSensors.setPaused(true);
                return;
            case 6:
            case 7:
            case 8:
                this.mDozeSensors.setTouchscreenSensorsListening(false);
                this.mDozeSensors.setProxListening(true);
                this.mDozeSensors.setPaused(false);
                return;
            case 9:
                this.mDozeSensors.requestTemporaryDisable();
                this.mDozeSensors.updateListening();
                return;
            case 10:
                this.mBroadcastReceiver.unregister(this.mBroadcastDispatcher);
                this.mDozeHost.removeCallback(this.mHostCallback);
                this.mDockManager.removeListener(this.mDockEventListener);
                this.mDozeSensors.setListening(false);
                this.mDozeSensors.setProxListening(false);
                return;
            default:
                return;
        }
    }

    private void checkTriggersAtInit() {
        if (this.mUiModeManager.getCurrentModeType() == 3 || this.mDozeHost.isBlockingDoze() || !this.mDozeHost.isProvisioned()) {
            this.mMachine.requestState(State.FINISH);
        }
    }

    /* access modifiers changed from: private */
    public void requestPulse(int i, boolean z, Runnable runnable) {
        Assert.isMainThread();
        this.mDozeHost.extendPulse(i);
        if (this.mMachine.getState() == State.DOZE_PULSING && i == 8) {
            this.mMachine.requestState(State.DOZE_PULSING_BRIGHT);
        } else if (this.mPulsePending || !this.mAllowPulseTriggers || !canPulse()) {
            if (this.mAllowPulseTriggers) {
                this.mDozeLog.tracePulseDropped(this.mPulsePending, this.mMachine.getState(), this.mDozeHost.isPulsingBlocked());
            }
            runIfNotNull(runnable);
        } else {
            boolean z2 = true;
            this.mPulsePending = true;
            $$Lambda$DozeTriggers$7efrn9gYOB_Pbk9skV2oR0AOE r1 = new Consumer(runnable, i) {
                public final /* synthetic */ Runnable f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void accept(Object obj) {
                    DozeTriggers.this.lambda$requestPulse$3$DozeTriggers(this.f$1, this.f$2, (Boolean) obj);
                }
            };
            if (this.mDozeParameters.getProxCheckBeforePulse() && !z) {
                z2 = false;
            }
            proximityCheckThenCall(r1, z2, i);
            this.mMetricsLogger.write(new LogMaker(223).setType(6).setSubtype(i));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$requestPulse$3 */
    public /* synthetic */ void lambda$requestPulse$3$DozeTriggers(Runnable runnable, int i, Boolean bool) {
        if (bool == null || !bool.booleanValue()) {
            continuePulseRequest(i);
            return;
        }
        this.mDozeLog.tracePulseDropped("inPocket");
        this.mPulsePending = false;
        runIfNotNull(runnable);
    }

    private boolean canPulse() {
        return this.mMachine.getState() == State.DOZE || this.mMachine.getState() == State.DOZE_AOD || this.mMachine.getState() == State.DOZE_AOD_DOCKED;
    }

    private void continuePulseRequest(int i) {
        this.mPulsePending = false;
        if (this.mDozeHost.isPulsingBlocked() || !canPulse()) {
            this.mDozeLog.tracePulseDropped(this.mPulsePending, this.mMachine.getState(), this.mDozeHost.isPulsingBlocked());
        } else {
            this.mMachine.requestPulse(i);
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.print(" notificationPulseTime=");
        printWriter.println(Formatter.formatShortElapsedTime(this.mContext, this.mNotificationPulseTime));
        StringBuilder sb = new StringBuilder();
        sb.append(" pulsePending=");
        sb.append(this.mPulsePending);
        printWriter.println(sb.toString());
        printWriter.println("DozeSensors:");
        this.mDozeSensors.dump(printWriter);
    }
}
