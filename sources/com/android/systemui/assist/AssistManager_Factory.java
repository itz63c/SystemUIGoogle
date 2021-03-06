package com.android.systemui.assist;

import android.content.Context;
import com.android.internal.app.AssistUtils;
import com.android.systemui.model.SysUiState;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class AssistManager_Factory implements Factory<AssistManager> {
    private final Provider<AssistUtils> assistUtilsProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> controllerProvider;
    private final Provider<AssistHandleBehaviorController> handleControllerProvider;
    private final Provider<OverviewProxyService> overviewProxyServiceProvider;
    private final Provider<PhoneStateMonitor> phoneStateMonitorProvider;
    private final Provider<SysUiState> sysUiStateProvider;

    public AssistManager_Factory(Provider<DeviceProvisionedController> provider, Provider<Context> provider2, Provider<AssistUtils> provider3, Provider<AssistHandleBehaviorController> provider4, Provider<CommandQueue> provider5, Provider<PhoneStateMonitor> provider6, Provider<OverviewProxyService> provider7, Provider<ConfigurationController> provider8, Provider<SysUiState> provider9) {
        this.controllerProvider = provider;
        this.contextProvider = provider2;
        this.assistUtilsProvider = provider3;
        this.handleControllerProvider = provider4;
        this.commandQueueProvider = provider5;
        this.phoneStateMonitorProvider = provider6;
        this.overviewProxyServiceProvider = provider7;
        this.configurationControllerProvider = provider8;
        this.sysUiStateProvider = provider9;
    }

    public AssistManager get() {
        return provideInstance(this.controllerProvider, this.contextProvider, this.assistUtilsProvider, this.handleControllerProvider, this.commandQueueProvider, this.phoneStateMonitorProvider, this.overviewProxyServiceProvider, this.configurationControllerProvider, this.sysUiStateProvider);
    }

    public static AssistManager provideInstance(Provider<DeviceProvisionedController> provider, Provider<Context> provider2, Provider<AssistUtils> provider3, Provider<AssistHandleBehaviorController> provider4, Provider<CommandQueue> provider5, Provider<PhoneStateMonitor> provider6, Provider<OverviewProxyService> provider7, Provider<ConfigurationController> provider8, Provider<SysUiState> provider9) {
        AssistManager assistManager = new AssistManager((DeviceProvisionedController) provider.get(), (Context) provider2.get(), (AssistUtils) provider3.get(), (AssistHandleBehaviorController) provider4.get(), (CommandQueue) provider5.get(), (PhoneStateMonitor) provider6.get(), (OverviewProxyService) provider7.get(), (ConfigurationController) provider8.get(), DoubleCheck.lazy(provider9));
        return assistManager;
    }

    public static AssistManager_Factory create(Provider<DeviceProvisionedController> provider, Provider<Context> provider2, Provider<AssistUtils> provider3, Provider<AssistHandleBehaviorController> provider4, Provider<CommandQueue> provider5, Provider<PhoneStateMonitor> provider6, Provider<OverviewProxyService> provider7, Provider<ConfigurationController> provider8, Provider<SysUiState> provider9) {
        AssistManager_Factory assistManager_Factory = new AssistManager_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
        return assistManager_Factory;
    }
}
