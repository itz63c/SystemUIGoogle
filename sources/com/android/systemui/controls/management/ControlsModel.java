package com.android.systemui.controls.management;

import com.android.systemui.controls.controller.ControlInfo.Builder;
import java.util.List;

/* compiled from: ControlsModel.kt */
public interface ControlsModel {
    void changeFavoriteStatus(String str, boolean z);

    List<ElementWrapper> getElements();

    List<Builder> getFavorites();
}
