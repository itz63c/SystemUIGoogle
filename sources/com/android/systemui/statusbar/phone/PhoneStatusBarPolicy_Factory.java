package com.android.systemui.statusbar.phone;

import android.app.AlarmManager;
import android.app.IActivityManager;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.UserManager;
import android.telecom.TelecomManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.util.time.DateFormatUtil;
import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class PhoneStatusBarPolicy_Factory implements Factory<PhoneStatusBarPolicy> {
    private final Provider<AlarmManager> alarmManagerProvider;
    private final Provider<AudioManager> audioManagerProvider;
    private final Provider<BluetoothController> bluetoothControllerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<CastController> castControllerProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<DataSaverController> dataSaverControllerProvider;
    private final Provider<DateFormatUtil> dateFormatUtilProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<Integer> displayIdProvider;
    private final Provider<HotspotController> hotspotControllerProvider;
    private final Provider<IActivityManager> iActivityManagerProvider;
    private final Provider<StatusBarIconController> iconControllerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<LocationController> locationControllerProvider;
    private final Provider<NextAlarmController> nextAlarmControllerProvider;
    private final Provider<RecordingController> recordingControllerProvider;
    private final Provider<Resources> resourcesProvider;
    private final Provider<RotationLockController> rotationLockControllerProvider;
    private final Provider<SensorPrivacyController> sensorPrivacyControllerProvider;
    private final Provider<SharedPreferences> sharedPreferencesProvider;
    private final Provider<TelecomManager> telecomManagerProvider;
    private final Provider<Executor> uiBgExecutorProvider;
    private final Provider<UserInfoController> userInfoControllerProvider;
    private final Provider<UserManager> userManagerProvider;
    private final Provider<ZenModeController> zenModeControllerProvider;

    public PhoneStatusBarPolicy_Factory(Provider<StatusBarIconController> provider, Provider<CommandQueue> provider2, Provider<BroadcastDispatcher> provider3, Provider<Executor> provider4, Provider<Resources> provider5, Provider<CastController> provider6, Provider<HotspotController> provider7, Provider<BluetoothController> provider8, Provider<NextAlarmController> provider9, Provider<UserInfoController> provider10, Provider<RotationLockController> provider11, Provider<DataSaverController> provider12, Provider<ZenModeController> provider13, Provider<DeviceProvisionedController> provider14, Provider<KeyguardStateController> provider15, Provider<LocationController> provider16, Provider<SensorPrivacyController> provider17, Provider<IActivityManager> provider18, Provider<AlarmManager> provider19, Provider<UserManager> provider20, Provider<AudioManager> provider21, Provider<RecordingController> provider22, Provider<TelecomManager> provider23, Provider<Integer> provider24, Provider<SharedPreferences> provider25, Provider<DateFormatUtil> provider26) {
        this.iconControllerProvider = provider;
        this.commandQueueProvider = provider2;
        this.broadcastDispatcherProvider = provider3;
        this.uiBgExecutorProvider = provider4;
        this.resourcesProvider = provider5;
        this.castControllerProvider = provider6;
        this.hotspotControllerProvider = provider7;
        this.bluetoothControllerProvider = provider8;
        this.nextAlarmControllerProvider = provider9;
        this.userInfoControllerProvider = provider10;
        this.rotationLockControllerProvider = provider11;
        this.dataSaverControllerProvider = provider12;
        this.zenModeControllerProvider = provider13;
        this.deviceProvisionedControllerProvider = provider14;
        this.keyguardStateControllerProvider = provider15;
        this.locationControllerProvider = provider16;
        this.sensorPrivacyControllerProvider = provider17;
        this.iActivityManagerProvider = provider18;
        this.alarmManagerProvider = provider19;
        this.userManagerProvider = provider20;
        this.audioManagerProvider = provider21;
        this.recordingControllerProvider = provider22;
        this.telecomManagerProvider = provider23;
        this.displayIdProvider = provider24;
        this.sharedPreferencesProvider = provider25;
        this.dateFormatUtilProvider = provider26;
    }

    public PhoneStatusBarPolicy get() {
        return provideInstance(this.iconControllerProvider, this.commandQueueProvider, this.broadcastDispatcherProvider, this.uiBgExecutorProvider, this.resourcesProvider, this.castControllerProvider, this.hotspotControllerProvider, this.bluetoothControllerProvider, this.nextAlarmControllerProvider, this.userInfoControllerProvider, this.rotationLockControllerProvider, this.dataSaverControllerProvider, this.zenModeControllerProvider, this.deviceProvisionedControllerProvider, this.keyguardStateControllerProvider, this.locationControllerProvider, this.sensorPrivacyControllerProvider, this.iActivityManagerProvider, this.alarmManagerProvider, this.userManagerProvider, this.audioManagerProvider, this.recordingControllerProvider, this.telecomManagerProvider, this.displayIdProvider, this.sharedPreferencesProvider, this.dateFormatUtilProvider);
    }

    public static PhoneStatusBarPolicy provideInstance(Provider<StatusBarIconController> provider, Provider<CommandQueue> provider2, Provider<BroadcastDispatcher> provider3, Provider<Executor> provider4, Provider<Resources> provider5, Provider<CastController> provider6, Provider<HotspotController> provider7, Provider<BluetoothController> provider8, Provider<NextAlarmController> provider9, Provider<UserInfoController> provider10, Provider<RotationLockController> provider11, Provider<DataSaverController> provider12, Provider<ZenModeController> provider13, Provider<DeviceProvisionedController> provider14, Provider<KeyguardStateController> provider15, Provider<LocationController> provider16, Provider<SensorPrivacyController> provider17, Provider<IActivityManager> provider18, Provider<AlarmManager> provider19, Provider<UserManager> provider20, Provider<AudioManager> provider21, Provider<RecordingController> provider22, Provider<TelecomManager> provider23, Provider<Integer> provider24, Provider<SharedPreferences> provider25, Provider<DateFormatUtil> provider26) {
        PhoneStatusBarPolicy phoneStatusBarPolicy = new PhoneStatusBarPolicy((StatusBarIconController) provider.get(), (CommandQueue) provider2.get(), (BroadcastDispatcher) provider3.get(), (Executor) provider4.get(), (Resources) provider5.get(), (CastController) provider6.get(), (HotspotController) provider7.get(), (BluetoothController) provider8.get(), (NextAlarmController) provider9.get(), (UserInfoController) provider10.get(), (RotationLockController) provider11.get(), (DataSaverController) provider12.get(), (ZenModeController) provider13.get(), (DeviceProvisionedController) provider14.get(), (KeyguardStateController) provider15.get(), (LocationController) provider16.get(), (SensorPrivacyController) provider17.get(), (IActivityManager) provider18.get(), (AlarmManager) provider19.get(), (UserManager) provider20.get(), (AudioManager) provider21.get(), (RecordingController) provider22.get(), (TelecomManager) provider23.get(), ((Integer) provider24.get()).intValue(), (SharedPreferences) provider25.get(), (DateFormatUtil) provider26.get());
        return phoneStatusBarPolicy;
    }

    public static PhoneStatusBarPolicy_Factory create(Provider<StatusBarIconController> provider, Provider<CommandQueue> provider2, Provider<BroadcastDispatcher> provider3, Provider<Executor> provider4, Provider<Resources> provider5, Provider<CastController> provider6, Provider<HotspotController> provider7, Provider<BluetoothController> provider8, Provider<NextAlarmController> provider9, Provider<UserInfoController> provider10, Provider<RotationLockController> provider11, Provider<DataSaverController> provider12, Provider<ZenModeController> provider13, Provider<DeviceProvisionedController> provider14, Provider<KeyguardStateController> provider15, Provider<LocationController> provider16, Provider<SensorPrivacyController> provider17, Provider<IActivityManager> provider18, Provider<AlarmManager> provider19, Provider<UserManager> provider20, Provider<AudioManager> provider21, Provider<RecordingController> provider22, Provider<TelecomManager> provider23, Provider<Integer> provider24, Provider<SharedPreferences> provider25, Provider<DateFormatUtil> provider26) {
        PhoneStatusBarPolicy_Factory phoneStatusBarPolicy_Factory = new PhoneStatusBarPolicy_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20, provider21, provider22, provider23, provider24, provider25, provider26);
        return phoneStatusBarPolicy_Factory;
    }
}
