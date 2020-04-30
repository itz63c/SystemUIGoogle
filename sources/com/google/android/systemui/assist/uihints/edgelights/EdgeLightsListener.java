package com.google.android.systemui.assist.uihints.edgelights;

import com.android.systemui.assist.p003ui.EdgeLight;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView.Mode;

public interface EdgeLightsListener {
    void onAssistLightsUpdated(Mode mode, EdgeLight[] edgeLightArr) {
    }

    void onModeStarted(Mode mode) {
    }
}
