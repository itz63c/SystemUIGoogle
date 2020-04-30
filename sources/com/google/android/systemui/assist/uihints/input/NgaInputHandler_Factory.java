package com.google.android.systemui.assist.uihints.input;

import com.google.android.systemui.assist.uihints.TouchInsideHandler;
import dagger.internal.Factory;
import java.util.Set;
import javax.inject.Provider;

public final class NgaInputHandler_Factory implements Factory<NgaInputHandler> {
    private final Provider<Set<TouchInsideRegion>> dismissablesProvider;
    private final Provider<TouchInsideHandler> touchInsideHandlerProvider;
    private final Provider<Set<TouchActionRegion>> touchablesProvider;

    public NgaInputHandler_Factory(Provider<TouchInsideHandler> provider, Provider<Set<TouchActionRegion>> provider2, Provider<Set<TouchInsideRegion>> provider3) {
        this.touchInsideHandlerProvider = provider;
        this.touchablesProvider = provider2;
        this.dismissablesProvider = provider3;
    }

    public NgaInputHandler get() {
        return provideInstance(this.touchInsideHandlerProvider, this.touchablesProvider, this.dismissablesProvider);
    }

    public static NgaInputHandler provideInstance(Provider<TouchInsideHandler> provider, Provider<Set<TouchActionRegion>> provider2, Provider<Set<TouchInsideRegion>> provider3) {
        return new NgaInputHandler((TouchInsideHandler) provider.get(), (Set) provider2.get(), (Set) provider3.get());
    }

    public static NgaInputHandler_Factory create(Provider<TouchInsideHandler> provider, Provider<Set<TouchActionRegion>> provider2, Provider<Set<TouchInsideRegion>> provider3) {
        return new NgaInputHandler_Factory(provider, provider2, provider3);
    }
}
