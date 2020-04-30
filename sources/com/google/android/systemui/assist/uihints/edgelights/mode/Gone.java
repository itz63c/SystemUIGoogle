package com.google.android.systemui.assist.uihints.edgelights.mode;

import android.metrics.LogMaker;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.assist.p003ui.EdgeLight;
import com.android.systemui.assist.p003ui.PerimeterPathGuide;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView.Mode;

public final class Gone implements Mode {
    public int getSubType() {
        return 0;
    }

    public void start(EdgeLightsView edgeLightsView, PerimeterPathGuide perimeterPathGuide, Mode mode) {
        edgeLightsView.setAssistLights(new EdgeLight[0]);
    }

    public void onNewModeRequest(EdgeLightsView edgeLightsView, Mode mode) {
        edgeLightsView.setVisibility(0);
        edgeLightsView.commitModeTransition(mode);
    }

    public void logState() {
        MetricsLogger.action(new LogMaker(1716).setType(2));
    }
}
