package com.android.systemui.controls.p004ui;

import android.content.ComponentName;
import android.service.controls.Control;
import android.view.ViewGroup;
import java.util.List;

/* renamed from: com.android.systemui.controls.ui.ControlsUiController */
/* compiled from: ControlsUiController.kt */
public interface ControlsUiController {

    /* renamed from: com.android.systemui.controls.ui.ControlsUiController$Companion */
    /* compiled from: ControlsUiController.kt */
    public static final class Companion {
        static final /* synthetic */ Companion $$INSTANCE = new Companion();

        private Companion() {
        }
    }

    static {
        Companion companion = Companion.$$INSTANCE;
    }

    boolean getAvailable();

    void hide();

    void onActionResponse(ComponentName componentName, String str, int i);

    void onRefreshState(ComponentName componentName, List<Control> list);

    void show(ViewGroup viewGroup);
}
