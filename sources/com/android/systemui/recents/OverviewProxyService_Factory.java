package com.android.systemui.recents;

import android.content.Context;
import com.android.systemui.model.SysUiState;
import com.android.systemui.pip.PipUI;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import dagger.Lazy;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class OverviewProxyService_Factory implements Factory<OverviewProxyService> {
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Optional<Divider>> dividerOptionalProvider;
    private final Provider<NavigationBarController> navBarControllerProvider;
    private final Provider<NavigationModeController> navModeControllerProvider;
    private final Provider<PipUI> pipUIProvider;
    private final Provider<DeviceProvisionedController> provisionControllerProvider;
    private final Provider<Optional<Lazy<StatusBar>>> statusBarOptionalLazyProvider;
    private final Provider<NotificationShadeWindowController> statusBarWinControllerProvider;
    private final Provider<SysUiState> sysUiStateProvider;

    public OverviewProxyService_Factory(Provider<Context> provider, Provider<CommandQueue> provider2, Provider<DeviceProvisionedController> provider3, Provider<NavigationBarController> provider4, Provider<NavigationModeController> provider5, Provider<NotificationShadeWindowController> provider6, Provider<SysUiState> provider7, Provider<PipUI> provider8, Provider<Optional<Divider>> provider9, Provider<Optional<Lazy<StatusBar>>> provider10) {
        this.contextProvider = provider;
        this.commandQueueProvider = provider2;
        this.provisionControllerProvider = provider3;
        this.navBarControllerProvider = provider4;
        this.navModeControllerProvider = provider5;
        this.statusBarWinControllerProvider = provider6;
        this.sysUiStateProvider = provider7;
        this.pipUIProvider = provider8;
        this.dividerOptionalProvider = provider9;
        this.statusBarOptionalLazyProvider = provider10;
    }

    public OverviewProxyService get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider, this.provisionControllerProvider, this.navBarControllerProvider, this.navModeControllerProvider, this.statusBarWinControllerProvider, this.sysUiStateProvider, this.pipUIProvider, this.dividerOptionalProvider, this.statusBarOptionalLazyProvider);
    }

    public static OverviewProxyService provideInstance(Provider<Context> provider, Provider<CommandQueue> provider2, Provider<DeviceProvisionedController> provider3, Provider<NavigationBarController> provider4, Provider<NavigationModeController> provider5, Provider<NotificationShadeWindowController> provider6, Provider<SysUiState> provider7, Provider<PipUI> provider8, Provider<Optional<Divider>> provider9, Provider<Optional<Lazy<StatusBar>>> provider10) {
        OverviewProxyService overviewProxyService = new OverviewProxyService((Context) provider.get(), (CommandQueue) provider2.get(), (DeviceProvisionedController) provider3.get(), (NavigationBarController) provider4.get(), (NavigationModeController) provider5.get(), (NotificationShadeWindowController) provider6.get(), (SysUiState) provider7.get(), (PipUI) provider8.get(), (Optional) provider9.get(), (Optional) provider10.get());
        return overviewProxyService;
    }

    public static OverviewProxyService_Factory create(Provider<Context> provider, Provider<CommandQueue> provider2, Provider<DeviceProvisionedController> provider3, Provider<NavigationBarController> provider4, Provider<NavigationModeController> provider5, Provider<NotificationShadeWindowController> provider6, Provider<SysUiState> provider7, Provider<PipUI> provider8, Provider<Optional<Divider>> provider9, Provider<Optional<Lazy<StatusBar>>> provider10) {
        OverviewProxyService_Factory overviewProxyService_Factory = new OverviewProxyService_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10);
        return overviewProxyService_Factory;
    }
}
