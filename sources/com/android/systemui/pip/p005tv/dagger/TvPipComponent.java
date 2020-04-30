package com.android.systemui.pip.p005tv.dagger;

import com.android.systemui.pip.p005tv.PipControlsView;
import com.android.systemui.pip.p005tv.PipControlsViewController;

/* renamed from: com.android.systemui.pip.tv.dagger.TvPipComponent */
public interface TvPipComponent {

    /* renamed from: com.android.systemui.pip.tv.dagger.TvPipComponent$Builder */
    public interface Builder {
        TvPipComponent build();

        Builder pipControlsView(PipControlsView pipControlsView);
    }

    PipControlsViewController getPipControlsViewController();
}
