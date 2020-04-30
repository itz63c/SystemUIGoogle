package com.google.android.systemui.dagger;

import com.android.systemui.dagger.SystemUIRootComponent;
import com.google.android.systemui.keyguard.KeyguardSliceProviderGoogle;

public interface SystemUIGoogleRootComponent extends SystemUIRootComponent {
    void inject(KeyguardSliceProviderGoogle keyguardSliceProviderGoogle);
}
