package com.android.systemui.controls.controller;

import com.android.systemui.controls.ControlStatus;
import com.android.systemui.controls.controller.ControlsController.LoadData;
import java.util.List;

/* compiled from: ControlsController.kt */
public final class ControlsControllerKt$createLoadDataObject$1 implements LoadData {
    private final List<ControlStatus> allControls;
    private final boolean errorOnLoad;
    private final List<String> favoritesIds;

    ControlsControllerKt$createLoadDataObject$1(List list, List list2, boolean z) {
        this.allControls = list;
        this.favoritesIds = list2;
        this.errorOnLoad = z;
    }

    public List<ControlStatus> getAllControls() {
        return this.allControls;
    }

    public List<String> getFavoritesIds() {
        return this.favoritesIds;
    }

    public boolean getErrorOnLoad() {
        return this.errorOnLoad;
    }
}
