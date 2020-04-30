package com.android.systemui.screenshot;

import com.android.systemui.screenshot.GlobalScreenshot.ActionProxyReceiver;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class GlobalScreenshot_ActionProxyReceiver_Factory implements Factory<ActionProxyReceiver> {
    private final Provider<Optional<Lazy<StatusBar>>> statusBarLazyProvider;

    public GlobalScreenshot_ActionProxyReceiver_Factory(Provider<Optional<Lazy<StatusBar>>> provider) {
        this.statusBarLazyProvider = provider;
    }

    public ActionProxyReceiver get() {
        return provideInstance(this.statusBarLazyProvider);
    }

    public static ActionProxyReceiver provideInstance(Provider<Optional<Lazy<StatusBar>>> provider) {
        return new ActionProxyReceiver((Optional) provider.get());
    }

    public static GlobalScreenshot_ActionProxyReceiver_Factory create(Provider<Optional<Lazy<StatusBar>>> provider) {
        return new GlobalScreenshot_ActionProxyReceiver_Factory(provider);
    }
}
