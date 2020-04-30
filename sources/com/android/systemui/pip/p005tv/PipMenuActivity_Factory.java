package com.android.systemui.pip.p005tv;

import com.android.systemui.pip.p005tv.dagger.TvPipComponent.Builder;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.pip.tv.PipMenuActivity_Factory */
public final class PipMenuActivity_Factory implements Factory<PipMenuActivity> {
    private final Provider<Builder> pipComponentBuilderProvider;
    private final Provider<PipManager> pipManagerProvider;

    public PipMenuActivity_Factory(Provider<Builder> provider, Provider<PipManager> provider2) {
        this.pipComponentBuilderProvider = provider;
        this.pipManagerProvider = provider2;
    }

    public PipMenuActivity get() {
        return provideInstance(this.pipComponentBuilderProvider, this.pipManagerProvider);
    }

    public static PipMenuActivity provideInstance(Provider<Builder> provider, Provider<PipManager> provider2) {
        return new PipMenuActivity((Builder) provider.get(), (PipManager) provider2.get());
    }

    public static PipMenuActivity_Factory create(Provider<Builder> provider, Provider<PipManager> provider2) {
        return new PipMenuActivity_Factory(provider, provider2);
    }
}
