package com.google.android.systemui.columbus;

import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Set;

public final class ColumbusModule_ProvideBlockingSystemKeysFactory implements Factory<Set<Integer>> {
    private static final ColumbusModule_ProvideBlockingSystemKeysFactory INSTANCE = new ColumbusModule_ProvideBlockingSystemKeysFactory();

    public Set<Integer> get() {
        return provideInstance();
    }

    public static Set<Integer> provideInstance() {
        return proxyProvideBlockingSystemKeys();
    }

    public static ColumbusModule_ProvideBlockingSystemKeysFactory create() {
        return INSTANCE;
    }

    public static Set<Integer> proxyProvideBlockingSystemKeys() {
        Set<Integer> provideBlockingSystemKeys = ColumbusModule.provideBlockingSystemKeys();
        Preconditions.checkNotNull(provideBlockingSystemKeys, "Cannot return null from a non-@Nullable @Provides method");
        return provideBlockingSystemKeys;
    }
}
