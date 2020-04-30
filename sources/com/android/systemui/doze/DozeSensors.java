package com.android.systemui.doze;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.hardware.display.AmbientDisplayConfiguration;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.plugins.SensorManagerPlugin.Sensor;
import com.android.systemui.plugins.SensorManagerPlugin.SensorEvent;
import com.android.systemui.plugins.SensorManagerPlugin.SensorEventListener;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.systemui.util.sensors.ProximitySensor;
import com.android.systemui.util.sensors.ProximitySensor.ProximityEvent;
import com.android.systemui.util.sensors.ProximitySensor.ProximitySensorListener;
import com.android.systemui.util.wakelock.WakeLock;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.function.Consumer;

public class DozeSensors {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = DozeService.DEBUG;
    /* access modifiers changed from: private */
    public final Callback mCallback;
    /* access modifiers changed from: private */
    public final AmbientDisplayConfiguration mConfig;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public long mDebounceFrom;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    private boolean mListening;
    private boolean mPaused;
    private final Consumer<Boolean> mProxCallback;
    private final ProximitySensor mProximitySensor;
    /* access modifiers changed from: private */
    public final ContentResolver mResolver;
    /* access modifiers changed from: private */
    public final AsyncSensorManager mSensorManager;
    protected TriggerSensor[] mSensors;
    private boolean mSettingRegistered;
    /* access modifiers changed from: private */
    public final ContentObserver mSettingsObserver = new ContentObserver(this.mHandler) {
        public void onChange(boolean z, Collection<Uri> collection, int i, int i2) {
            if (i2 == ActivityManager.getCurrentUser()) {
                for (TriggerSensor updateListening : DozeSensors.this.mSensors) {
                    updateListening.updateListening();
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public final WakeLock mWakeLock;

    public interface Callback {
        void onSensorPulse(int i, float f, float f2, float[] fArr);
    }

    class PluginSensor extends TriggerSensor implements SensorEventListener {
        private long mDebounce;
        final Sensor mPluginSensor;
        final /* synthetic */ DozeSensors this$0;

        PluginSensor(DozeSensors dozeSensors, Sensor sensor, String str, boolean z, int i, boolean z2, boolean z3, DozeLog dozeLog) {
            this(dozeSensors, sensor, str, z, i, z2, z3, 0, dozeLog);
        }

        PluginSensor(DozeSensors dozeSensors, Sensor sensor, String str, boolean z, int i, boolean z2, boolean z3, long j, DozeLog dozeLog) {
            DozeSensors dozeSensors2 = dozeSensors;
            this.this$0 = dozeSensors2;
            super(dozeSensors2, null, str, z, i, z2, z3, dozeLog);
            this.mPluginSensor = sensor;
            this.mDebounce = j;
        }

        public void updateListening() {
            if (this.mConfigured) {
                AsyncSensorManager access$200 = this.this$0.mSensorManager;
                String str = "DozeSensors";
                if (this.mRequested && !this.mDisabled && ((enabledBySetting() || this.mIgnoresSetting) && !this.mRegistered)) {
                    access$200.registerPluginListener(this.mPluginSensor, this);
                    this.mRegistered = true;
                    if (DozeSensors.DEBUG) {
                        Log.d(str, "registerPluginListener");
                    }
                } else if (this.mRegistered) {
                    access$200.unregisterPluginListener(this.mPluginSensor, this);
                    this.mRegistered = false;
                    if (DozeSensors.DEBUG) {
                        Log.d(str, "unregisterPluginListener");
                    }
                }
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("{mRegistered=");
            sb.append(this.mRegistered);
            sb.append(", mRequested=");
            sb.append(this.mRequested);
            sb.append(", mDisabled=");
            sb.append(this.mDisabled);
            sb.append(", mConfigured=");
            sb.append(this.mConfigured);
            sb.append(", mIgnoresSetting=");
            sb.append(this.mIgnoresSetting);
            sb.append(", mSensor=");
            sb.append(this.mPluginSensor);
            sb.append("}");
            return sb.toString();
        }

        private String triggerEventToString(SensorEvent sensorEvent) {
            if (sensorEvent == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder("PluginTriggerEvent[");
            sb.append(sensorEvent.getSensor());
            sb.append(',');
            sb.append(sensorEvent.getVendorType());
            if (sensorEvent.getValues() != null) {
                for (float append : sensorEvent.getValues()) {
                    sb.append(',');
                    sb.append(append);
                }
            }
            sb.append(']');
            return sb.toString();
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            this.mDozeLog.traceSensor(this.mPulseReason);
            this.this$0.mHandler.post(this.this$0.mWakeLock.wrap(new Runnable(sensorEvent) {
                public final /* synthetic */ SensorEvent f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PluginSensor.this.lambda$onSensorChanged$0$DozeSensors$PluginSensor(this.f$1);
                }
            }));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onSensorChanged$0 */
        public /* synthetic */ void lambda$onSensorChanged$0$DozeSensors$PluginSensor(SensorEvent sensorEvent) {
            String str = "DozeSensors";
            if (SystemClock.uptimeMillis() < this.this$0.mDebounceFrom + this.mDebounce) {
                StringBuilder sb = new StringBuilder();
                sb.append("onSensorEvent dropped: ");
                sb.append(triggerEventToString(sensorEvent));
                Log.d(str, sb.toString());
                return;
            }
            if (DozeSensors.DEBUG) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("onSensorEvent: ");
                sb2.append(triggerEventToString(sensorEvent));
                Log.d(str, sb2.toString());
            }
            this.this$0.mCallback.onSensorPulse(this.mPulseReason, -1.0f, -1.0f, sensorEvent.getValues());
        }
    }

    class TriggerSensor extends TriggerEventListener {
        final boolean mConfigured;
        protected boolean mDisabled;
        protected final DozeLog mDozeLog;
        protected boolean mIgnoresSetting;
        final int mPulseReason;
        protected boolean mRegistered;
        private final boolean mReportsTouchCoordinates;
        protected boolean mRequested;
        /* access modifiers changed from: private */
        public final boolean mRequiresTouchscreen;
        final android.hardware.Sensor mSensor;
        private final String mSetting;
        private final boolean mSettingDefault;

        public TriggerSensor(DozeSensors dozeSensors, android.hardware.Sensor sensor, String str, boolean z, int i, boolean z2, boolean z3, DozeLog dozeLog) {
            this(dozeSensors, sensor, str, true, z, i, z2, z3, dozeLog);
        }

        public TriggerSensor(DozeSensors dozeSensors, android.hardware.Sensor sensor, String str, boolean z, boolean z2, int i, boolean z3, boolean z4, DozeLog dozeLog) {
            this(sensor, str, z, z2, i, z3, z4, false, dozeLog);
        }

        private TriggerSensor(android.hardware.Sensor sensor, String str, boolean z, boolean z2, int i, boolean z3, boolean z4, boolean z5, DozeLog dozeLog) {
            this.mSensor = sensor;
            this.mSetting = str;
            this.mSettingDefault = z;
            this.mConfigured = z2;
            this.mPulseReason = i;
            this.mReportsTouchCoordinates = z3;
            this.mRequiresTouchscreen = z4;
            this.mIgnoresSetting = z5;
            this.mDozeLog = dozeLog;
        }

        public void setListening(boolean z) {
            if (this.mRequested != z) {
                this.mRequested = z;
                updateListening();
            }
        }

        public void ignoreSetting(boolean z) {
            if (this.mIgnoresSetting != z) {
                this.mIgnoresSetting = z;
                updateListening();
            }
        }

        public void updateListening() {
            if (this.mConfigured && this.mSensor != null) {
                String str = "DozeSensors";
                if (this.mRequested && !this.mDisabled && ((enabledBySetting() || this.mIgnoresSetting) && !this.mRegistered)) {
                    this.mRegistered = DozeSensors.this.mSensorManager.requestTriggerSensor(this, this.mSensor);
                    if (DozeSensors.DEBUG) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("requestTriggerSensor ");
                        sb.append(this.mRegistered);
                        Log.d(str, sb.toString());
                    }
                } else if (this.mRegistered) {
                    boolean cancelTriggerSensor = DozeSensors.this.mSensorManager.cancelTriggerSensor(this, this.mSensor);
                    if (DozeSensors.DEBUG) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("cancelTriggerSensor ");
                        sb2.append(cancelTriggerSensor);
                        Log.d(str, sb2.toString());
                    }
                    this.mRegistered = false;
                }
            }
        }

        /* access modifiers changed from: protected */
        public boolean enabledBySetting() {
            boolean z = false;
            if (!DozeSensors.this.mConfig.enabled(-2)) {
                return false;
            }
            if (TextUtils.isEmpty(this.mSetting)) {
                return true;
            }
            if (Secure.getIntForUser(DozeSensors.this.mResolver, this.mSetting, this.mSettingDefault ? 1 : 0, -2) != 0) {
                z = true;
            }
            return z;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("{mRegistered=");
            sb.append(this.mRegistered);
            sb.append(", mRequested=");
            sb.append(this.mRequested);
            sb.append(", mDisabled=");
            sb.append(this.mDisabled);
            sb.append(", mConfigured=");
            sb.append(this.mConfigured);
            sb.append(", mIgnoresSetting=");
            sb.append(this.mIgnoresSetting);
            sb.append(", mSensor=");
            sb.append(this.mSensor);
            sb.append("}");
            return sb.toString();
        }

        public void onTrigger(TriggerEvent triggerEvent) {
            this.mDozeLog.traceSensor(this.mPulseReason);
            DozeSensors.this.mHandler.post(DozeSensors.this.mWakeLock.wrap(new Runnable(triggerEvent) {
                public final /* synthetic */ TriggerEvent f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TriggerSensor.this.lambda$onTrigger$0$DozeSensors$TriggerSensor(this.f$1);
                }
            }));
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:16:0x0063  */
        /* JADX WARNING: Removed duplicated region for block: B:18:? A[RETURN, SYNTHETIC] */
        /* renamed from: lambda$onTrigger$0 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$onTrigger$0$DozeSensors$TriggerSensor(android.hardware.TriggerEvent r6) {
            /*
                r5 = this;
                boolean r0 = com.android.systemui.doze.DozeSensors.DEBUG
                if (r0 == 0) goto L_0x0020
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "onTrigger: "
                r0.append(r1)
                java.lang.String r1 = r5.triggerEventToString(r6)
                r0.append(r1)
                java.lang.String r0 = r0.toString()
                java.lang.String r1 = "DozeSensors"
                android.util.Log.d(r1, r0)
            L_0x0020:
                android.hardware.Sensor r0 = r5.mSensor
                r1 = 0
                if (r0 == 0) goto L_0x003d
                int r0 = r0.getType()
                r2 = 25
                if (r0 != r2) goto L_0x003d
                float[] r0 = r6.values
                r0 = r0[r1]
                int r0 = (int) r0
                com.android.systemui.doze.DozeSensors r2 = com.android.systemui.doze.DozeSensors.this
                android.content.Context r2 = r2.mContext
                r3 = 411(0x19b, float:5.76E-43)
                com.android.internal.logging.MetricsLogger.action(r2, r3, r0)
            L_0x003d:
                r5.mRegistered = r1
                boolean r0 = r5.mReportsTouchCoordinates
                r2 = -1082130432(0xffffffffbf800000, float:-1.0)
                if (r0 == 0) goto L_0x0051
                float[] r0 = r6.values
                int r3 = r0.length
                r4 = 2
                if (r3 < r4) goto L_0x0051
                r2 = r0[r1]
                r1 = 1
                r0 = r0[r1]
                goto L_0x0052
            L_0x0051:
                r0 = r2
            L_0x0052:
                com.android.systemui.doze.DozeSensors r1 = com.android.systemui.doze.DozeSensors.this
                com.android.systemui.doze.DozeSensors$Callback r1 = r1.mCallback
                int r3 = r5.mPulseReason
                float[] r6 = r6.values
                r1.onSensorPulse(r3, r2, r0, r6)
                boolean r6 = r5.mRegistered
                if (r6 != 0) goto L_0x0066
                r5.updateListening()
            L_0x0066:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.doze.DozeSensors.TriggerSensor.lambda$onTrigger$0$DozeSensors$TriggerSensor(android.hardware.TriggerEvent):void");
        }

        public void registerSettingsObserver(ContentObserver contentObserver) {
            if (this.mConfigured && !TextUtils.isEmpty(this.mSetting)) {
                DozeSensors.this.mResolver.registerContentObserver(Secure.getUriFor(this.mSetting), false, DozeSensors.this.mSettingsObserver, -1);
            }
        }

        /* access modifiers changed from: protected */
        public String triggerEventToString(TriggerEvent triggerEvent) {
            if (triggerEvent == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder("SensorEvent[");
            sb.append(triggerEvent.timestamp);
            sb.append(',');
            sb.append(triggerEvent.sensor.getName());
            if (triggerEvent.values != null) {
                for (float append : triggerEvent.values) {
                    sb.append(',');
                    sb.append(append);
                }
            }
            sb.append(']');
            return sb.toString();
        }
    }

    public DozeSensors(Context context, AlarmManager alarmManager, AsyncSensorManager asyncSensorManager, DozeParameters dozeParameters, AmbientDisplayConfiguration ambientDisplayConfiguration, WakeLock wakeLock, Callback callback, Consumer<Boolean> consumer, DozeLog dozeLog) {
        AsyncSensorManager asyncSensorManager2 = asyncSensorManager;
        this.mContext = context;
        this.mSensorManager = asyncSensorManager2;
        this.mConfig = ambientDisplayConfiguration;
        this.mWakeLock = wakeLock;
        this.mProxCallback = consumer;
        this.mResolver = context.getContentResolver();
        this.mCallback = callback;
        boolean alwaysOnEnabled = this.mConfig.alwaysOnEnabled(-2);
        TriggerSensor[] triggerSensorArr = new TriggerSensor[7];
        TriggerSensor triggerSensor = new TriggerSensor(this, this.mSensorManager.getDefaultSensor(17), null, dozeParameters.getPulseOnSigMotion(), 2, false, false, dozeLog);
        triggerSensorArr[0] = triggerSensor;
        TriggerSensor[] triggerSensorArr2 = triggerSensorArr;
        TriggerSensor triggerSensor2 = new TriggerSensor(this.mSensorManager.getDefaultSensor(25), "doze_pulse_on_pick_up", true, ambientDisplayConfiguration.dozePickupSensorAvailable(), 3, false, false, false, dozeLog);
        triggerSensorArr2[1] = triggerSensor2;
        DozeLog dozeLog2 = dozeLog;
        TriggerSensor triggerSensor3 = new TriggerSensor(this, findSensorWithType(ambientDisplayConfiguration.doubleTapSensorType()), "doze_pulse_on_double_tap", true, 4, dozeParameters.doubleTapReportsTouchCoordinates(), true, dozeLog2);
        triggerSensorArr2[2] = triggerSensor3;
        TriggerSensor triggerSensor4 = new TriggerSensor(this, findSensorWithType(ambientDisplayConfiguration.tapSensorType()), "doze_tap_gesture", true, 9, false, true, dozeLog2);
        triggerSensorArr2[3] = triggerSensor4;
        TriggerSensor triggerSensor5 = new TriggerSensor(this, findSensorWithType(ambientDisplayConfiguration.longPressSensorType()), "doze_pulse_on_long_press", false, true, 5, true, true, dozeLog);
        triggerSensorArr2[4] = triggerSensor5;
        PluginSensor pluginSensor = new PluginSensor(this, new Sensor(2), "doze_wake_display_gesture", this.mConfig.wakeScreenGestureAvailable() && alwaysOnEnabled, 7, false, false, dozeLog);
        triggerSensorArr2[5] = pluginSensor;
        PluginSensor pluginSensor2 = new PluginSensor(this, new Sensor(1), "doze_wake_screen_gesture", this.mConfig.wakeScreenGestureAvailable(), 8, false, false, this.mConfig.getWakeLockScreenDebounce(), dozeLog);
        triggerSensorArr2[6] = pluginSensor2;
        this.mSensors = triggerSensorArr2;
        this.mProximitySensor = new ProximitySensor(context.getResources(), asyncSensorManager2);
        setProxListening(false);
        this.mProximitySensor.register(new ProximitySensorListener() {
            public final void onSensorEvent(ProximityEvent proximityEvent) {
                DozeSensors.this.lambda$new$0$DozeSensors(proximityEvent);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$DozeSensors(ProximityEvent proximityEvent) {
        if (proximityEvent != null) {
            this.mProxCallback.accept(Boolean.valueOf(!proximityEvent.getNear()));
        }
    }

    public void requestTemporaryDisable() {
        this.mDebounceFrom = SystemClock.uptimeMillis();
    }

    private android.hardware.Sensor findSensorWithType(String str) {
        return findSensorWithType(this.mSensorManager, str);
    }

    static android.hardware.Sensor findSensorWithType(SensorManager sensorManager, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        for (android.hardware.Sensor sensor : sensorManager.getSensorList(-1)) {
            if (str.equals(sensor.getStringType())) {
                return sensor;
            }
        }
        return null;
    }

    public void setListening(boolean z) {
        if (this.mListening != z) {
            this.mListening = z;
            updateListening();
        }
    }

    public void setPaused(boolean z) {
        if (this.mPaused != z) {
            this.mPaused = z;
            updateListening();
        }
    }

    public void updateListening() {
        boolean z = false;
        for (TriggerSensor listening : this.mSensors) {
            listening.setListening(this.mListening);
            if (this.mListening) {
                z = true;
            }
        }
        if (!z) {
            this.mResolver.unregisterContentObserver(this.mSettingsObserver);
        } else if (!this.mSettingRegistered) {
            for (TriggerSensor registerSettingsObserver : this.mSensors) {
                registerSettingsObserver.registerSettingsObserver(this.mSettingsObserver);
            }
        }
        this.mSettingRegistered = z;
    }

    public void setTouchscreenSensorsListening(boolean z) {
        TriggerSensor[] triggerSensorArr;
        for (TriggerSensor triggerSensor : this.mSensors) {
            if (triggerSensor.mRequiresTouchscreen) {
                triggerSensor.setListening(z);
            }
        }
    }

    public void onUserSwitched() {
        for (TriggerSensor updateListening : this.mSensors) {
            updateListening.updateListening();
        }
    }

    public void setProxListening(boolean z) {
        if (this.mProximitySensor.isRegistered() && z) {
            this.mProximitySensor.alertListeners();
        } else if (z) {
            this.mProximitySensor.resume();
        } else {
            this.mProximitySensor.pause();
        }
    }

    public void ignoreTouchScreenSensorsSettingInterferingWithDocking(boolean z) {
        TriggerSensor[] triggerSensorArr;
        for (TriggerSensor triggerSensor : this.mSensors) {
            if (triggerSensor.mRequiresTouchscreen) {
                triggerSensor.ignoreSetting(z);
            }
        }
    }

    public void dump(PrintWriter printWriter) {
        TriggerSensor[] triggerSensorArr;
        for (TriggerSensor triggerSensor : this.mSensors) {
            StringBuilder sb = new StringBuilder();
            sb.append("  Sensor: ");
            sb.append(triggerSensor.toString());
            printWriter.println(sb.toString());
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  ProxSensor: ");
        sb2.append(this.mProximitySensor.toString());
        printWriter.println(sb2.toString());
    }

    public Boolean isProximityCurrentlyNear() {
        return this.mProximitySensor.isNear();
    }
}
