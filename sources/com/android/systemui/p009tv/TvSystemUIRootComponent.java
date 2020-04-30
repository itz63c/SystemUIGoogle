package com.android.systemui.p009tv;

import android.content.Context;
import com.android.systemui.dagger.SystemUIRootComponent;

/* renamed from: com.android.systemui.tv.TvSystemUIRootComponent */
public interface TvSystemUIRootComponent extends SystemUIRootComponent {

    /* renamed from: com.android.systemui.tv.TvSystemUIRootComponent$Builder */
    public interface Builder {
        TvSystemUIRootComponent build();

        Builder context(Context context);
    }
}
