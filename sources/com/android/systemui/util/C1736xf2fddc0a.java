package com.android.systemui.util;

import android.util.AttributeSet;
import com.android.systemui.util.InjectionInflationController.ViewAttributeProvider;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* renamed from: com.android.systemui.util.InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory */
public final class C1736xf2fddc0a implements Factory<AttributeSet> {
    public static AttributeSet proxyProvideAttributeSet(ViewAttributeProvider viewAttributeProvider) {
        AttributeSet provideAttributeSet = viewAttributeProvider.provideAttributeSet();
        Preconditions.checkNotNull(provideAttributeSet, "Cannot return null from a non-@Nullable @Provides method");
        return provideAttributeSet;
    }
}
