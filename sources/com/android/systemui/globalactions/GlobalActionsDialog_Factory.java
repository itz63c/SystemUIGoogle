package com.android.systemui.globalactions;

import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.UserManager;
import android.os.Vibrator;
import android.service.dreams.IDreamManager;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.IWindowManager;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.controls.p004ui.ControlsUiController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.GlobalActions.GlobalActionsManager;
import com.android.systemui.statusbar.BlurUtils;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class GlobalActionsDialog_Factory implements Factory<GlobalActionsDialog> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<AudioManager> audioManagerProvider;
    private final Provider<Executor> backgroundExecutorProvider;
    private final Provider<BlurUtils> blurUtilsProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<SysuiColorExtractor> colorExtractorProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<ConnectivityManager> connectivityManagerProvider;
    private final Provider<ContentResolver> contentResolverProvider;
    private final Provider<Context> contextProvider;
    private final Provider<ControlsController> controlsControllerProvider;
    private final Provider<ControlsListingController> controlsListingControllerProvider;
    private final Provider<ControlsUiController> controlsUiControllerProvider;
    private final Provider<NotificationShadeDepthController> depthControllerProvider;
    private final Provider<DevicePolicyManager> devicePolicyManagerProvider;
    private final Provider<IActivityManager> iActivityManagerProvider;
    private final Provider<IDreamManager> iDreamManagerProvider;
    private final Provider<IWindowManager> iWindowManagerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<LockPatternUtils> lockPatternUtilsProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<Resources> resourcesProvider;
    private final Provider<IStatusBarService> statusBarServiceProvider;
    private final Provider<TelecomManager> telecomManagerProvider;
    private final Provider<TelephonyManager> telephonyManagerProvider;
    private final Provider<TrustManager> trustManagerProvider;
    private final Provider<UserManager> userManagerProvider;
    private final Provider<Vibrator> vibratorProvider;
    private final Provider<GlobalActionsManager> windowManagerFuncsProvider;

    public GlobalActionsDialog_Factory(Provider<Context> provider, Provider<GlobalActionsManager> provider2, Provider<AudioManager> provider3, Provider<IDreamManager> provider4, Provider<DevicePolicyManager> provider5, Provider<LockPatternUtils> provider6, Provider<BroadcastDispatcher> provider7, Provider<ConnectivityManager> provider8, Provider<TelephonyManager> provider9, Provider<ContentResolver> provider10, Provider<Vibrator> provider11, Provider<Resources> provider12, Provider<ConfigurationController> provider13, Provider<ActivityStarter> provider14, Provider<KeyguardStateController> provider15, Provider<UserManager> provider16, Provider<TrustManager> provider17, Provider<IActivityManager> provider18, Provider<TelecomManager> provider19, Provider<MetricsLogger> provider20, Provider<NotificationShadeDepthController> provider21, Provider<SysuiColorExtractor> provider22, Provider<IStatusBarService> provider23, Provider<BlurUtils> provider24, Provider<NotificationShadeWindowController> provider25, Provider<ControlsUiController> provider26, Provider<IWindowManager> provider27, Provider<Executor> provider28, Provider<ControlsListingController> provider29, Provider<ControlsController> provider30) {
        this.contextProvider = provider;
        this.windowManagerFuncsProvider = provider2;
        this.audioManagerProvider = provider3;
        this.iDreamManagerProvider = provider4;
        this.devicePolicyManagerProvider = provider5;
        this.lockPatternUtilsProvider = provider6;
        this.broadcastDispatcherProvider = provider7;
        this.connectivityManagerProvider = provider8;
        this.telephonyManagerProvider = provider9;
        this.contentResolverProvider = provider10;
        this.vibratorProvider = provider11;
        this.resourcesProvider = provider12;
        this.configurationControllerProvider = provider13;
        this.activityStarterProvider = provider14;
        this.keyguardStateControllerProvider = provider15;
        this.userManagerProvider = provider16;
        this.trustManagerProvider = provider17;
        this.iActivityManagerProvider = provider18;
        this.telecomManagerProvider = provider19;
        this.metricsLoggerProvider = provider20;
        this.depthControllerProvider = provider21;
        this.colorExtractorProvider = provider22;
        this.statusBarServiceProvider = provider23;
        this.blurUtilsProvider = provider24;
        this.notificationShadeWindowControllerProvider = provider25;
        this.controlsUiControllerProvider = provider26;
        this.iWindowManagerProvider = provider27;
        this.backgroundExecutorProvider = provider28;
        this.controlsListingControllerProvider = provider29;
        this.controlsControllerProvider = provider30;
    }

    public GlobalActionsDialog get() {
        return provideInstance(this.contextProvider, this.windowManagerFuncsProvider, this.audioManagerProvider, this.iDreamManagerProvider, this.devicePolicyManagerProvider, this.lockPatternUtilsProvider, this.broadcastDispatcherProvider, this.connectivityManagerProvider, this.telephonyManagerProvider, this.contentResolverProvider, this.vibratorProvider, this.resourcesProvider, this.configurationControllerProvider, this.activityStarterProvider, this.keyguardStateControllerProvider, this.userManagerProvider, this.trustManagerProvider, this.iActivityManagerProvider, this.telecomManagerProvider, this.metricsLoggerProvider, this.depthControllerProvider, this.colorExtractorProvider, this.statusBarServiceProvider, this.blurUtilsProvider, this.notificationShadeWindowControllerProvider, this.controlsUiControllerProvider, this.iWindowManagerProvider, this.backgroundExecutorProvider, this.controlsListingControllerProvider, this.controlsControllerProvider);
    }

    public static GlobalActionsDialog provideInstance(Provider<Context> provider, Provider<GlobalActionsManager> provider2, Provider<AudioManager> provider3, Provider<IDreamManager> provider4, Provider<DevicePolicyManager> provider5, Provider<LockPatternUtils> provider6, Provider<BroadcastDispatcher> provider7, Provider<ConnectivityManager> provider8, Provider<TelephonyManager> provider9, Provider<ContentResolver> provider10, Provider<Vibrator> provider11, Provider<Resources> provider12, Provider<ConfigurationController> provider13, Provider<ActivityStarter> provider14, Provider<KeyguardStateController> provider15, Provider<UserManager> provider16, Provider<TrustManager> provider17, Provider<IActivityManager> provider18, Provider<TelecomManager> provider19, Provider<MetricsLogger> provider20, Provider<NotificationShadeDepthController> provider21, Provider<SysuiColorExtractor> provider22, Provider<IStatusBarService> provider23, Provider<BlurUtils> provider24, Provider<NotificationShadeWindowController> provider25, Provider<ControlsUiController> provider26, Provider<IWindowManager> provider27, Provider<Executor> provider28, Provider<ControlsListingController> provider29, Provider<ControlsController> provider30) {
        GlobalActionsDialog globalActionsDialog = new GlobalActionsDialog((Context) provider.get(), (GlobalActionsManager) provider2.get(), (AudioManager) provider3.get(), (IDreamManager) provider4.get(), (DevicePolicyManager) provider5.get(), (LockPatternUtils) provider6.get(), (BroadcastDispatcher) provider7.get(), (ConnectivityManager) provider8.get(), (TelephonyManager) provider9.get(), (ContentResolver) provider10.get(), (Vibrator) provider11.get(), (Resources) provider12.get(), (ConfigurationController) provider13.get(), (ActivityStarter) provider14.get(), (KeyguardStateController) provider15.get(), (UserManager) provider16.get(), (TrustManager) provider17.get(), (IActivityManager) provider18.get(), (TelecomManager) provider19.get(), (MetricsLogger) provider20.get(), (NotificationShadeDepthController) provider21.get(), (SysuiColorExtractor) provider22.get(), (IStatusBarService) provider23.get(), (BlurUtils) provider24.get(), (NotificationShadeWindowController) provider25.get(), (ControlsUiController) provider26.get(), (IWindowManager) provider27.get(), (Executor) provider28.get(), (ControlsListingController) provider29.get(), (ControlsController) provider30.get());
        return globalActionsDialog;
    }

    public static GlobalActionsDialog_Factory create(Provider<Context> provider, Provider<GlobalActionsManager> provider2, Provider<AudioManager> provider3, Provider<IDreamManager> provider4, Provider<DevicePolicyManager> provider5, Provider<LockPatternUtils> provider6, Provider<BroadcastDispatcher> provider7, Provider<ConnectivityManager> provider8, Provider<TelephonyManager> provider9, Provider<ContentResolver> provider10, Provider<Vibrator> provider11, Provider<Resources> provider12, Provider<ConfigurationController> provider13, Provider<ActivityStarter> provider14, Provider<KeyguardStateController> provider15, Provider<UserManager> provider16, Provider<TrustManager> provider17, Provider<IActivityManager> provider18, Provider<TelecomManager> provider19, Provider<MetricsLogger> provider20, Provider<NotificationShadeDepthController> provider21, Provider<SysuiColorExtractor> provider22, Provider<IStatusBarService> provider23, Provider<BlurUtils> provider24, Provider<NotificationShadeWindowController> provider25, Provider<ControlsUiController> provider26, Provider<IWindowManager> provider27, Provider<Executor> provider28, Provider<ControlsListingController> provider29, Provider<ControlsController> provider30) {
        GlobalActionsDialog_Factory globalActionsDialog_Factory = new GlobalActionsDialog_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20, provider21, provider22, provider23, provider24, provider25, provider26, provider27, provider28, provider29, provider30);
        return globalActionsDialog_Factory;
    }
}
