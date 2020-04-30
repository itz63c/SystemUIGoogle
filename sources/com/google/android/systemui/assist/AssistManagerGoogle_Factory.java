package com.google.android.systemui.assist;

import android.content.Context;
import android.os.Handler;
import com.android.internal.app.AssistUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.assist.AssistHandleBehaviorController;
import com.android.systemui.assist.PhoneStateMonitor;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.model.SysUiState;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.google.android.systemui.assist.uihints.AssistantPresenceHandler;
import com.google.android.systemui.assist.uihints.NgaMessageHandler;
import com.google.android.systemui.assist.uihints.NgaUiController;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class AssistManagerGoogle_Factory implements Factory<AssistManagerGoogle> {
    private final Provider<AssistUtils> assistUtilsProvider;
    private final Provider<AssistantPresenceHandler> assistantPresenceHandlerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> controllerProvider;
    private final Provider<AssistHandleBehaviorController> handleControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<NavigationModeController> navigationModeControllerProvider;
    private final Provider<NgaMessageHandler> ngaMessageHandlerProvider;
    private final Provider<NgaUiController> ngaUiControllerProvider;
    private final Provider<OpaEnabledDispatcher> opaEnabledDispatcherProvider;
    private final Provider<OverviewProxyService> overviewProxyServiceProvider;
    private final Provider<PhoneStateMonitor> phoneStateMonitorProvider;
    private final Provider<SysUiState> sysUiStateProvider;
    private final Provider<Handler> uiHandlerProvider;

    public AssistManagerGoogle_Factory(Provider<DeviceProvisionedController> provider, Provider<Context> provider2, Provider<AssistUtils> provider3, Provider<AssistHandleBehaviorController> provider4, Provider<NgaUiController> provider5, Provider<CommandQueue> provider6, Provider<BroadcastDispatcher> provider7, Provider<PhoneStateMonitor> provider8, Provider<OverviewProxyService> provider9, Provider<OpaEnabledDispatcher> provider10, Provider<KeyguardUpdateMonitor> provider11, Provider<NavigationModeController> provider12, Provider<ConfigurationController> provider13, Provider<AssistantPresenceHandler> provider14, Provider<NgaMessageHandler> provider15, Provider<SysUiState> provider16, Provider<Handler> provider17) {
        this.controllerProvider = provider;
        this.contextProvider = provider2;
        this.assistUtilsProvider = provider3;
        this.handleControllerProvider = provider4;
        this.ngaUiControllerProvider = provider5;
        this.commandQueueProvider = provider6;
        this.broadcastDispatcherProvider = provider7;
        this.phoneStateMonitorProvider = provider8;
        this.overviewProxyServiceProvider = provider9;
        this.opaEnabledDispatcherProvider = provider10;
        this.keyguardUpdateMonitorProvider = provider11;
        this.navigationModeControllerProvider = provider12;
        this.configurationControllerProvider = provider13;
        this.assistantPresenceHandlerProvider = provider14;
        this.ngaMessageHandlerProvider = provider15;
        this.sysUiStateProvider = provider16;
        this.uiHandlerProvider = provider17;
    }

    public AssistManagerGoogle get() {
        return provideInstance(this.controllerProvider, this.contextProvider, this.assistUtilsProvider, this.handleControllerProvider, this.ngaUiControllerProvider, this.commandQueueProvider, this.broadcastDispatcherProvider, this.phoneStateMonitorProvider, this.overviewProxyServiceProvider, this.opaEnabledDispatcherProvider, this.keyguardUpdateMonitorProvider, this.navigationModeControllerProvider, this.configurationControllerProvider, this.assistantPresenceHandlerProvider, this.ngaMessageHandlerProvider, this.sysUiStateProvider, this.uiHandlerProvider);
    }

    public static AssistManagerGoogle provideInstance(Provider<DeviceProvisionedController> provider, Provider<Context> provider2, Provider<AssistUtils> provider3, Provider<AssistHandleBehaviorController> provider4, Provider<NgaUiController> provider5, Provider<CommandQueue> provider6, Provider<BroadcastDispatcher> provider7, Provider<PhoneStateMonitor> provider8, Provider<OverviewProxyService> provider9, Provider<OpaEnabledDispatcher> provider10, Provider<KeyguardUpdateMonitor> provider11, Provider<NavigationModeController> provider12, Provider<ConfigurationController> provider13, Provider<AssistantPresenceHandler> provider14, Provider<NgaMessageHandler> provider15, Provider<SysUiState> provider16, Provider<Handler> provider17) {
        AssistManagerGoogle assistManagerGoogle = new AssistManagerGoogle((DeviceProvisionedController) provider.get(), (Context) provider2.get(), (AssistUtils) provider3.get(), (AssistHandleBehaviorController) provider4.get(), (NgaUiController) provider5.get(), (CommandQueue) provider6.get(), (BroadcastDispatcher) provider7.get(), (PhoneStateMonitor) provider8.get(), (OverviewProxyService) provider9.get(), (OpaEnabledDispatcher) provider10.get(), (KeyguardUpdateMonitor) provider11.get(), (NavigationModeController) provider12.get(), (ConfigurationController) provider13.get(), (AssistantPresenceHandler) provider14.get(), (NgaMessageHandler) provider15.get(), DoubleCheck.lazy(provider16), (Handler) provider17.get());
        return assistManagerGoogle;
    }

    public static AssistManagerGoogle_Factory create(Provider<DeviceProvisionedController> provider, Provider<Context> provider2, Provider<AssistUtils> provider3, Provider<AssistHandleBehaviorController> provider4, Provider<NgaUiController> provider5, Provider<CommandQueue> provider6, Provider<BroadcastDispatcher> provider7, Provider<PhoneStateMonitor> provider8, Provider<OverviewProxyService> provider9, Provider<OpaEnabledDispatcher> provider10, Provider<KeyguardUpdateMonitor> provider11, Provider<NavigationModeController> provider12, Provider<ConfigurationController> provider13, Provider<AssistantPresenceHandler> provider14, Provider<NgaMessageHandler> provider15, Provider<SysUiState> provider16, Provider<Handler> provider17) {
        AssistManagerGoogle_Factory assistManagerGoogle_Factory = new AssistManagerGoogle_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17);
        return assistManagerGoogle_Factory;
    }
}
