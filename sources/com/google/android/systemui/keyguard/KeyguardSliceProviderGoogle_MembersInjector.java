package com.google.android.systemui.keyguard;

import com.google.android.systemui.smartspace.SmartSpaceController;

public final class KeyguardSliceProviderGoogle_MembersInjector {
    public static void injectMSmartSpaceController(KeyguardSliceProviderGoogle keyguardSliceProviderGoogle, SmartSpaceController smartSpaceController) {
        keyguardSliceProviderGoogle.mSmartSpaceController = smartSpaceController;
    }
}
