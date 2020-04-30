package com.android.systemui.assist;

import android.content.Context;
import android.os.Handler;
import com.android.internal.app.AssistUtils;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.phone.NavigationModeController;
import dagger.internal.Factory;
import java.util.Map;
import javax.inject.Provider;

public final class AssistHandleBehaviorController_Factory implements Factory<AssistHandleBehaviorController> {
    private final Provider<AssistHandleViewController> assistHandleViewControllerProvider;
    private final Provider<AssistUtils> assistUtilsProvider;
    private final Provider<Map<AssistHandleBehavior, BehaviorController>> behaviorMapProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<NavigationModeController> navigationModeControllerProvider;

    public AssistHandleBehaviorController_Factory(Provider<Context> provider, Provider<AssistUtils> provider2, Provider<Handler> provider3, Provider<AssistHandleViewController> provider4, Provider<DeviceConfigHelper> provider5, Provider<Map<AssistHandleBehavior, BehaviorController>> provider6, Provider<NavigationModeController> provider7, Provider<DumpManager> provider8) {
        this.contextProvider = provider;
        this.assistUtilsProvider = provider2;
        this.handlerProvider = provider3;
        this.assistHandleViewControllerProvider = provider4;
        this.deviceConfigHelperProvider = provider5;
        this.behaviorMapProvider = provider6;
        this.navigationModeControllerProvider = provider7;
        this.dumpManagerProvider = provider8;
    }

    public AssistHandleBehaviorController get() {
        return provideInstance(this.contextProvider, this.assistUtilsProvider, this.handlerProvider, this.assistHandleViewControllerProvider, this.deviceConfigHelperProvider, this.behaviorMapProvider, this.navigationModeControllerProvider, this.dumpManagerProvider);
    }

    public static AssistHandleBehaviorController provideInstance(Provider<Context> provider, Provider<AssistUtils> provider2, Provider<Handler> provider3, Provider<AssistHandleViewController> provider4, Provider<DeviceConfigHelper> provider5, Provider<Map<AssistHandleBehavior, BehaviorController>> provider6, Provider<NavigationModeController> provider7, Provider<DumpManager> provider8) {
        AssistHandleBehaviorController assistHandleBehaviorController = new AssistHandleBehaviorController((Context) provider.get(), (AssistUtils) provider2.get(), (Handler) provider3.get(), provider4, (DeviceConfigHelper) provider5.get(), (Map) provider6.get(), (NavigationModeController) provider7.get(), (DumpManager) provider8.get());
        return assistHandleBehaviorController;
    }

    public static AssistHandleBehaviorController_Factory create(Provider<Context> provider, Provider<AssistUtils> provider2, Provider<Handler> provider3, Provider<AssistHandleViewController> provider4, Provider<DeviceConfigHelper> provider5, Provider<Map<AssistHandleBehavior, BehaviorController>> provider6, Provider<NavigationModeController> provider7, Provider<DumpManager> provider8) {
        AssistHandleBehaviorController_Factory assistHandleBehaviorController_Factory = new AssistHandleBehaviorController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
        return assistHandleBehaviorController_Factory;
    }
}
