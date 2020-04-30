package com.android.systemui.dagger;

import android.content.ContentProvider;
import com.android.systemui.BootCompleteCacheImpl;
import com.android.systemui.Dependency.DependencyInjector;
import com.android.systemui.InitController;
import com.android.systemui.SystemUIAppComponentFactory;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.fragments.FragmentService.FragmentCreator;
import com.android.systemui.keyguard.KeyguardSliceProvider;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.InjectionInflationController.ViewCreator;

public interface SystemUIRootComponent {
    DependencyInjector createDependency();

    DumpManager createDumpManager();

    FragmentCreator createFragmentCreator();

    ViewCreator createViewCreator();

    ConfigurationController getConfigurationController();

    ContextComponentHelper getContextComponentHelper();

    InitController getInitController();

    void inject(ContentProvider contentProvider);

    void inject(SystemUIAppComponentFactory systemUIAppComponentFactory);

    void inject(KeyguardSliceProvider keyguardSliceProvider);

    BootCompleteCacheImpl provideBootCacheImpl();
}
