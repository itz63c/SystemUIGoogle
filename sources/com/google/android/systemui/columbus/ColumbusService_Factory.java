package com.google.android.systemui.columbus;

import com.android.internal.logging.MetricsLogger;
import com.google.android.systemui.columbus.actions.Action;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import com.google.android.systemui.columbus.gates.Gate;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import dagger.internal.Factory;
import java.util.List;
import java.util.Set;
import javax.inject.Provider;

public final class ColumbusService_Factory implements Factory<ColumbusService> {
    private final Provider<List<Action>> actionsProvider;
    private final Provider<Set<FeedbackEffect>> effectsProvider;
    private final Provider<Set<Gate>> gatesProvider;
    private final Provider<GestureSensor> gestureSensorProvider;
    private final Provider<MetricsLogger> loggerProvider;
    private final Provider<PowerManagerWrapper> powerManagerProvider;

    public ColumbusService_Factory(Provider<List<Action>> provider, Provider<Set<FeedbackEffect>> provider2, Provider<Set<Gate>> provider3, Provider<GestureSensor> provider4, Provider<PowerManagerWrapper> provider5, Provider<MetricsLogger> provider6) {
        this.actionsProvider = provider;
        this.effectsProvider = provider2;
        this.gatesProvider = provider3;
        this.gestureSensorProvider = provider4;
        this.powerManagerProvider = provider5;
        this.loggerProvider = provider6;
    }

    public ColumbusService get() {
        return provideInstance(this.actionsProvider, this.effectsProvider, this.gatesProvider, this.gestureSensorProvider, this.powerManagerProvider, this.loggerProvider);
    }

    public static ColumbusService provideInstance(Provider<List<Action>> provider, Provider<Set<FeedbackEffect>> provider2, Provider<Set<Gate>> provider3, Provider<GestureSensor> provider4, Provider<PowerManagerWrapper> provider5, Provider<MetricsLogger> provider6) {
        ColumbusService columbusService = new ColumbusService((List) provider.get(), (Set) provider2.get(), (Set) provider3.get(), (GestureSensor) provider4.get(), (PowerManagerWrapper) provider5.get(), (MetricsLogger) provider6.get());
        return columbusService;
    }

    public static ColumbusService_Factory create(Provider<List<Action>> provider, Provider<Set<FeedbackEffect>> provider2, Provider<Set<Gate>> provider3, Provider<GestureSensor> provider4, Provider<PowerManagerWrapper> provider5, Provider<MetricsLogger> provider6) {
        ColumbusService_Factory columbusService_Factory = new ColumbusService_Factory(provider, provider2, provider3, provider4, provider5, provider6);
        return columbusService_Factory;
    }
}
