package com.google.android.systemui.assist.uihints;

import com.android.internal.app.AssistUtils;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class AssistantPresenceHandler_Factory implements Factory<AssistantPresenceHandler> {
    private final Provider<AssistUtils> assistUtilsProvider;

    public AssistantPresenceHandler_Factory(Provider<AssistUtils> provider) {
        this.assistUtilsProvider = provider;
    }

    public AssistantPresenceHandler get() {
        return provideInstance(this.assistUtilsProvider);
    }

    public static AssistantPresenceHandler provideInstance(Provider<AssistUtils> provider) {
        return new AssistantPresenceHandler((AssistUtils) provider.get());
    }

    public static AssistantPresenceHandler_Factory create(Provider<AssistUtils> provider) {
        return new AssistantPresenceHandler_Factory(provider);
    }
}
