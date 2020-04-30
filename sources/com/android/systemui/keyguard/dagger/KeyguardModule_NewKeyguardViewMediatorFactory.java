package com.android.systemui.keyguard.dagger;

import android.app.trust.TrustManager;
import android.content.Context;
import android.os.PowerManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardViewController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.util.DeviceConfigProxy;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class KeyguardModule_NewKeyguardViewMediatorFactory implements Factory<KeyguardViewMediator> {
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigProxy> deviceConfigProvider;
    private final Provider<DismissCallbackRegistry> dismissCallbackRegistryProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<LockPatternUtils> lockPatternUtilsProvider;
    private final Provider<NavigationModeController> navigationModeControllerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<PowerManager> powerManagerProvider;
    private final Provider<KeyguardViewController> statusBarKeyguardViewManagerLazyProvider;
    private final Provider<TrustManager> trustManagerProvider;
    private final Provider<Executor> uiBgExecutorProvider;
    private final Provider<KeyguardUpdateMonitor> updateMonitorProvider;

    public KeyguardModule_NewKeyguardViewMediatorFactory(Provider<Context> provider, Provider<FalsingManager> provider2, Provider<LockPatternUtils> provider3, Provider<BroadcastDispatcher> provider4, Provider<NotificationShadeWindowController> provider5, Provider<KeyguardViewController> provider6, Provider<DismissCallbackRegistry> provider7, Provider<KeyguardUpdateMonitor> provider8, Provider<DumpManager> provider9, Provider<PowerManager> provider10, Provider<TrustManager> provider11, Provider<Executor> provider12, Provider<DeviceConfigProxy> provider13, Provider<NavigationModeController> provider14) {
        this.contextProvider = provider;
        this.falsingManagerProvider = provider2;
        this.lockPatternUtilsProvider = provider3;
        this.broadcastDispatcherProvider = provider4;
        this.notificationShadeWindowControllerProvider = provider5;
        this.statusBarKeyguardViewManagerLazyProvider = provider6;
        this.dismissCallbackRegistryProvider = provider7;
        this.updateMonitorProvider = provider8;
        this.dumpManagerProvider = provider9;
        this.powerManagerProvider = provider10;
        this.trustManagerProvider = provider11;
        this.uiBgExecutorProvider = provider12;
        this.deviceConfigProvider = provider13;
        this.navigationModeControllerProvider = provider14;
    }

    public KeyguardViewMediator get() {
        return provideInstance(this.contextProvider, this.falsingManagerProvider, this.lockPatternUtilsProvider, this.broadcastDispatcherProvider, this.notificationShadeWindowControllerProvider, this.statusBarKeyguardViewManagerLazyProvider, this.dismissCallbackRegistryProvider, this.updateMonitorProvider, this.dumpManagerProvider, this.powerManagerProvider, this.trustManagerProvider, this.uiBgExecutorProvider, this.deviceConfigProvider, this.navigationModeControllerProvider);
    }

    public static KeyguardViewMediator provideInstance(Provider<Context> provider, Provider<FalsingManager> provider2, Provider<LockPatternUtils> provider3, Provider<BroadcastDispatcher> provider4, Provider<NotificationShadeWindowController> provider5, Provider<KeyguardViewController> provider6, Provider<DismissCallbackRegistry> provider7, Provider<KeyguardUpdateMonitor> provider8, Provider<DumpManager> provider9, Provider<PowerManager> provider10, Provider<TrustManager> provider11, Provider<Executor> provider12, Provider<DeviceConfigProxy> provider13, Provider<NavigationModeController> provider14) {
        return proxyNewKeyguardViewMediator((Context) provider.get(), (FalsingManager) provider2.get(), (LockPatternUtils) provider3.get(), (BroadcastDispatcher) provider4.get(), (NotificationShadeWindowController) provider5.get(), DoubleCheck.lazy(provider6), (DismissCallbackRegistry) provider7.get(), (KeyguardUpdateMonitor) provider8.get(), (DumpManager) provider9.get(), (PowerManager) provider10.get(), (TrustManager) provider11.get(), (Executor) provider12.get(), (DeviceConfigProxy) provider13.get(), (NavigationModeController) provider14.get());
    }

    public static KeyguardModule_NewKeyguardViewMediatorFactory create(Provider<Context> provider, Provider<FalsingManager> provider2, Provider<LockPatternUtils> provider3, Provider<BroadcastDispatcher> provider4, Provider<NotificationShadeWindowController> provider5, Provider<KeyguardViewController> provider6, Provider<DismissCallbackRegistry> provider7, Provider<KeyguardUpdateMonitor> provider8, Provider<DumpManager> provider9, Provider<PowerManager> provider10, Provider<TrustManager> provider11, Provider<Executor> provider12, Provider<DeviceConfigProxy> provider13, Provider<NavigationModeController> provider14) {
        KeyguardModule_NewKeyguardViewMediatorFactory keyguardModule_NewKeyguardViewMediatorFactory = new KeyguardModule_NewKeyguardViewMediatorFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14);
        return keyguardModule_NewKeyguardViewMediatorFactory;
    }

    public static KeyguardViewMediator proxyNewKeyguardViewMediator(Context context, FalsingManager falsingManager, LockPatternUtils lockPatternUtils, BroadcastDispatcher broadcastDispatcher, NotificationShadeWindowController notificationShadeWindowController, Lazy<KeyguardViewController> lazy, DismissCallbackRegistry dismissCallbackRegistry, KeyguardUpdateMonitor keyguardUpdateMonitor, DumpManager dumpManager, PowerManager powerManager, TrustManager trustManager, Executor executor, DeviceConfigProxy deviceConfigProxy, NavigationModeController navigationModeController) {
        KeyguardViewMediator newKeyguardViewMediator = KeyguardModule.newKeyguardViewMediator(context, falsingManager, lockPatternUtils, broadcastDispatcher, notificationShadeWindowController, lazy, dismissCallbackRegistry, keyguardUpdateMonitor, dumpManager, powerManager, trustManager, executor, deviceConfigProxy, navigationModeController);
        Preconditions.checkNotNull(newKeyguardViewMediator, "Cannot return null from a non-@Nullable @Provides method");
        return newKeyguardViewMediator;
    }
}
