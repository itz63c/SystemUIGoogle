package com.android.systemui;

import com.android.systemui.statusbar.phone.DozeParameters;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ImageWallpaper_Factory implements Factory<ImageWallpaper> {
    private final Provider<DozeParameters> dozeParametersProvider;

    public ImageWallpaper_Factory(Provider<DozeParameters> provider) {
        this.dozeParametersProvider = provider;
    }

    public ImageWallpaper get() {
        return provideInstance(this.dozeParametersProvider);
    }

    public static ImageWallpaper provideInstance(Provider<DozeParameters> provider) {
        return new ImageWallpaper((DozeParameters) provider.get());
    }

    public static ImageWallpaper_Factory create(Provider<DozeParameters> provider) {
        return new ImageWallpaper_Factory(provider);
    }
}
