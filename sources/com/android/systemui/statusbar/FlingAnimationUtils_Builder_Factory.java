package com.android.systemui.statusbar;

import android.util.DisplayMetrics;
import com.android.systemui.statusbar.FlingAnimationUtils.Builder;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class FlingAnimationUtils_Builder_Factory implements Factory<Builder> {
    private final Provider<DisplayMetrics> displayMetricsProvider;

    public FlingAnimationUtils_Builder_Factory(Provider<DisplayMetrics> provider) {
        this.displayMetricsProvider = provider;
    }

    public Builder get() {
        return provideInstance(this.displayMetricsProvider);
    }

    public static Builder provideInstance(Provider<DisplayMetrics> provider) {
        return new Builder((DisplayMetrics) provider.get());
    }

    public static FlingAnimationUtils_Builder_Factory create(Provider<DisplayMetrics> provider) {
        return new FlingAnimationUtils_Builder_Factory(provider);
    }
}
