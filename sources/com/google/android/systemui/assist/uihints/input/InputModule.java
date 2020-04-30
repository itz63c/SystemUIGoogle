package com.google.android.systemui.assist.uihints.input;

import com.google.android.systemui.assist.uihints.GlowController;
import com.google.android.systemui.assist.uihints.IconController;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.CardInfoListener;
import com.google.android.systemui.assist.uihints.ScrimController;
import com.google.android.systemui.assist.uihints.TranscriptionController;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class InputModule {
    static Set<TouchActionRegion> provideTouchActionRegions(IconController iconController, TranscriptionController transcriptionController) {
        return new HashSet(Arrays.asList(new TouchActionRegion[]{iconController, transcriptionController}));
    }

    static Set<TouchInsideRegion> provideTouchInsideRegions(GlowController glowController, ScrimController scrimController, TranscriptionController transcriptionController) {
        return new HashSet(Arrays.asList(new CardInfoListener[]{glowController, scrimController, transcriptionController}));
    }
}
