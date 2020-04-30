package com.android.systemui.p009tv;

import android.content.Context;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.dagger.SystemUIRootComponent;
import com.android.systemui.p009tv.TvSystemUIRootComponent.Builder;

/* renamed from: com.android.systemui.tv.TvSystemUIFactory */
public class TvSystemUIFactory extends SystemUIFactory {
    /* access modifiers changed from: protected */
    public SystemUIRootComponent buildSystemUIRootComponent(Context context) {
        Builder builder = DaggerTvSystemUIRootComponent.builder();
        builder.context(context);
        return builder.build();
    }
}
