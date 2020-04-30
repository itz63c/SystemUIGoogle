package com.google.android.systemui.elmyra;

import com.google.android.systemui.elmyra.actions.Action;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import java.util.Collections;
import java.util.List;

public interface ServiceConfiguration {
    GestureSensor getGestureSensor() {
        return null;
    }

    List<Action> getActions() {
        return Collections.emptyList();
    }

    List<FeedbackEffect> getFeedbackEffects() {
        return Collections.emptyList();
    }

    List<Gate> getGates() {
        return Collections.emptyList();
    }
}
