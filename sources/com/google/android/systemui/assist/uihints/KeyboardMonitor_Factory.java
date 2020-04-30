package com.google.android.systemui.assist.uihints;

import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class KeyboardMonitor_Factory implements Factory<KeyboardMonitor> {
    private final Provider<Optional<CommandQueue>> commandQueueOptionalProvider;
    private final Provider<Context> contextProvider;

    public KeyboardMonitor_Factory(Provider<Context> provider, Provider<Optional<CommandQueue>> provider2) {
        this.contextProvider = provider;
        this.commandQueueOptionalProvider = provider2;
    }

    public KeyboardMonitor get() {
        return provideInstance(this.contextProvider, this.commandQueueOptionalProvider);
    }

    public static KeyboardMonitor provideInstance(Provider<Context> provider, Provider<Optional<CommandQueue>> provider2) {
        return new KeyboardMonitor((Context) provider.get(), (Optional) provider2.get());
    }

    public static KeyboardMonitor_Factory create(Provider<Context> provider, Provider<Optional<CommandQueue>> provider2) {
        return new KeyboardMonitor_Factory(provider, provider2);
    }
}
