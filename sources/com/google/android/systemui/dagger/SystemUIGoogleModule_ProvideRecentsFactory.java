package com.google.android.systemui.dagger;

import android.content.Context;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsImplementation;
import com.android.systemui.statusbar.CommandQueue;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class SystemUIGoogleModule_ProvideRecentsFactory implements Factory<Recents> {
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<RecentsImplementation> recentsImplementationProvider;

    public SystemUIGoogleModule_ProvideRecentsFactory(Provider<Context> provider, Provider<RecentsImplementation> provider2, Provider<CommandQueue> provider3) {
        this.contextProvider = provider;
        this.recentsImplementationProvider = provider2;
        this.commandQueueProvider = provider3;
    }

    public Recents get() {
        return provideInstance(this.contextProvider, this.recentsImplementationProvider, this.commandQueueProvider);
    }

    public static Recents provideInstance(Provider<Context> provider, Provider<RecentsImplementation> provider2, Provider<CommandQueue> provider3) {
        return proxyProvideRecents((Context) provider.get(), (RecentsImplementation) provider2.get(), (CommandQueue) provider3.get());
    }

    public static SystemUIGoogleModule_ProvideRecentsFactory create(Provider<Context> provider, Provider<RecentsImplementation> provider2, Provider<CommandQueue> provider3) {
        return new SystemUIGoogleModule_ProvideRecentsFactory(provider, provider2, provider3);
    }

    public static Recents proxyProvideRecents(Context context, RecentsImplementation recentsImplementation, CommandQueue commandQueue) {
        Recents provideRecents = SystemUIGoogleModule.provideRecents(context, recentsImplementation, commandQueue);
        Preconditions.checkNotNull(provideRecents, "Cannot return null from a non-@Nullable @Provides method");
        return provideRecents;
    }
}
