package com.android.systemui.doze;

import android.app.AlarmManager;
import android.app.IWallpaperManager;
import android.content.Context;
import android.hardware.SensorManager;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Handler;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.C2017R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dock.DockManager;
import com.android.systemui.doze.DozeMachine.Part;
import com.android.systemui.doze.DozeMachine.Service;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.systemui.util.sensors.ProximitySensor;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import com.android.systemui.util.wakelock.DelayedWakeLock.Builder;
import com.android.systemui.util.wakelock.WakeLock;

public class DozeFactory {
    private final AlarmManager mAlarmManager;
    private final AsyncSensorManager mAsyncSensorManager;
    private final BatteryController mBatteryController;
    private final BiometricUnlockController mBiometricUnlockController;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final Builder mDelayedWakeLockBuilder;
    private final DockManager mDockManager;
    private final DozeLog mDozeLog;
    private final DozeParameters mDozeParameters;
    private final DozeServiceHost mDozeServiceHost;
    private final FalsingManager mFalsingManager;
    private final Handler mHandler;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final ProximitySensor mProximitySensor;
    private final WakefulnessLifecycle mWakefulnessLifecycle;
    private final IWallpaperManager mWallpaperManager;

    public DozeFactory(FalsingManager falsingManager, DozeLog dozeLog, DozeParameters dozeParameters, BatteryController batteryController, AsyncSensorManager asyncSensorManager, AlarmManager alarmManager, WakefulnessLifecycle wakefulnessLifecycle, KeyguardUpdateMonitor keyguardUpdateMonitor, DockManager dockManager, IWallpaperManager iWallpaperManager, ProximitySensor proximitySensor, Builder builder, Handler handler, BiometricUnlockController biometricUnlockController, BroadcastDispatcher broadcastDispatcher, DozeServiceHost dozeServiceHost) {
        this.mFalsingManager = falsingManager;
        this.mDozeLog = dozeLog;
        this.mDozeParameters = dozeParameters;
        this.mBatteryController = batteryController;
        this.mAsyncSensorManager = asyncSensorManager;
        this.mAlarmManager = alarmManager;
        this.mWakefulnessLifecycle = wakefulnessLifecycle;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mDockManager = dockManager;
        this.mWallpaperManager = iWallpaperManager;
        this.mProximitySensor = proximitySensor;
        this.mDelayedWakeLockBuilder = builder;
        this.mHandler = handler;
        this.mBiometricUnlockController = biometricUnlockController;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mDozeServiceHost = dozeServiceHost;
    }

    /* access modifiers changed from: 0000 */
    public DozeMachine assembleMachine(DozeService dozeService) {
        DozeService dozeService2 = dozeService;
        AmbientDisplayConfiguration ambientDisplayConfiguration = new AmbientDisplayConfiguration(dozeService2);
        Builder builder = this.mDelayedWakeLockBuilder;
        builder.setHandler(this.mHandler);
        builder.setTag("Doze");
        DelayedWakeLock build = builder.build();
        Service wrapIfNeeded = DozeSuspendScreenStatePreventingAdapter.wrapIfNeeded(DozeScreenStatePreventingAdapter.wrapIfNeeded(new DozeBrightnessHostForwarder(dozeService2, this.mDozeServiceHost), this.mDozeParameters), this.mDozeParameters);
        DozeMachine dozeMachine = new DozeMachine(wrapIfNeeded, ambientDisplayConfiguration, build, this.mWakefulnessLifecycle, this.mBatteryController, this.mDozeLog, this.mDockManager, this.mDozeServiceHost);
        Part[] partArr = new Part[9];
        partArr[0] = new DozePauser(this.mHandler, dozeMachine, this.mAlarmManager, this.mDozeParameters.getPolicy());
        partArr[1] = new DozeFalsingManagerAdapter(this.mFalsingManager);
        AsyncSensorManager asyncSensorManager = this.mAsyncSensorManager;
        DozeServiceHost dozeServiceHost = this.mDozeServiceHost;
        AlarmManager alarmManager = this.mAlarmManager;
        DozeParameters dozeParameters = this.mDozeParameters;
        Handler handler = this.mHandler;
        DockManager dockManager = this.mDockManager;
        Part[] partArr2 = partArr;
        DockManager dockManager2 = dockManager;
        DozeMachine dozeMachine2 = dozeMachine;
        partArr2[2] = createDozeTriggers(dozeService, asyncSensorManager, dozeServiceHost, alarmManager, ambientDisplayConfiguration, dozeParameters, handler, build, dozeMachine, dockManager2, this.mDozeLog);
        partArr2[3] = createDozeUi(dozeService, this.mDozeServiceHost, build, dozeMachine2, this.mHandler, this.mAlarmManager, this.mDozeParameters, this.mDozeLog);
        DozeScreenState dozeScreenState = new DozeScreenState(wrapIfNeeded, this.mHandler, this.mDozeServiceHost, this.mDozeParameters, build);
        partArr2[4] = dozeScreenState;
        partArr2[5] = createDozeScreenBrightness(dozeService, wrapIfNeeded, this.mAsyncSensorManager, this.mDozeServiceHost, this.mDozeParameters, this.mHandler);
        partArr2[6] = new DozeWallpaperState(this.mWallpaperManager, this.mBiometricUnlockController, this.mDozeParameters);
        DozeMachine dozeMachine3 = dozeMachine2;
        partArr2[7] = new DozeDockHandler(ambientDisplayConfiguration, dozeMachine3, this.mDockManager);
        Part[] partArr3 = partArr2;
        partArr3[8] = new DozeAuthRemover(dozeService);
        dozeMachine3.setParts(partArr3);
        return dozeMachine3;
    }

    private Part createDozeScreenBrightness(Context context, Service service, SensorManager sensorManager, DozeHost dozeHost, DozeParameters dozeParameters, Handler handler) {
        DozeScreenBrightness dozeScreenBrightness = new DozeScreenBrightness(context, service, sensorManager, DozeSensors.findSensorWithType(sensorManager, context.getString(C2017R$string.doze_brightness_sensor_type)), this.mBroadcastDispatcher, dozeHost, handler, dozeParameters.getPolicy());
        return dozeScreenBrightness;
    }

    private DozeTriggers createDozeTriggers(Context context, AsyncSensorManager asyncSensorManager, DozeHost dozeHost, AlarmManager alarmManager, AmbientDisplayConfiguration ambientDisplayConfiguration, DozeParameters dozeParameters, Handler handler, WakeLock wakeLock, DozeMachine dozeMachine, DockManager dockManager, DozeLog dozeLog) {
        DozeTriggers dozeTriggers = new DozeTriggers(context, dozeMachine, dozeHost, alarmManager, ambientDisplayConfiguration, dozeParameters, asyncSensorManager, handler, wakeLock, true, dockManager, this.mProximitySensor, dozeLog, this.mBroadcastDispatcher);
        return dozeTriggers;
    }

    private Part createDozeUi(Context context, DozeHost dozeHost, WakeLock wakeLock, DozeMachine dozeMachine, Handler handler, AlarmManager alarmManager, DozeParameters dozeParameters, DozeLog dozeLog) {
        DozeUi dozeUi = new DozeUi(context, alarmManager, dozeMachine, wakeLock, dozeHost, handler, dozeParameters, this.mKeyguardUpdateMonitor, dozeLog);
        return dozeUi;
    }
}
