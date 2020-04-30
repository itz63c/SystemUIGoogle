package com.android.systemui.p007qs.logging;

import com.android.systemui.log.LogBuffer;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.logging.QSLogger_Factory */
public final class QSLogger_Factory implements Factory<QSLogger> {
    private final Provider<LogBuffer> bufferProvider;

    public QSLogger_Factory(Provider<LogBuffer> provider) {
        this.bufferProvider = provider;
    }

    public QSLogger get() {
        return provideInstance(this.bufferProvider);
    }

    public static QSLogger provideInstance(Provider<LogBuffer> provider) {
        return new QSLogger((LogBuffer) provider.get());
    }

    public static QSLogger_Factory create(Provider<LogBuffer> provider) {
        return new QSLogger_Factory(provider);
    }
}
