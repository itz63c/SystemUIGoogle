package com.google.android.systemui.assist.uihints;

import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.AudioInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.CardInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.EdgeLightsInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.StartActivityInfoListener;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController;
import com.google.android.systemui.assist.uihints.input.NgaInputHandler;
import dagger.Lazy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class AssistantUIHintsModule {
    static Set<AudioInfoListener> provideAudioInfoListeners(EdgeLightsController edgeLightsController, GlowController glowController) {
        return new HashSet(Arrays.asList(new AudioInfoListener[]{edgeLightsController, glowController}));
    }

    static Set<CardInfoListener> provideCardInfoListeners(GlowController glowController, ScrimController scrimController, TranscriptionController transcriptionController, LightnessProvider lightnessProvider) {
        return new HashSet(Arrays.asList(new CardInfoListener[]{glowController, scrimController, transcriptionController, lightnessProvider}));
    }

    static Set<ConfigInfoListener> provideConfigInfoListeners(AssistantPresenceHandler assistantPresenceHandler, TouchInsideHandler touchInsideHandler, TouchOutsideHandler touchOutsideHandler, TaskStackNotifier taskStackNotifier, KeyboardMonitor keyboardMonitor, ColorChangeHandler colorChangeHandler, ConfigurationHandler configurationHandler) {
        return new HashSet(Arrays.asList(new ConfigInfoListener[]{assistantPresenceHandler, touchInsideHandler, touchOutsideHandler, taskStackNotifier, keyboardMonitor, colorChangeHandler, configurationHandler}));
    }

    static Set<EdgeLightsInfoListener> bindEdgeLightsInfoListeners(EdgeLightsController edgeLightsController, NgaInputHandler ngaInputHandler) {
        return new HashSet(Arrays.asList(new EdgeLightsInfoListener[]{edgeLightsController, ngaInputHandler}));
    }

    static StartActivityInfoListener provideActivityStarter(final Lazy<StatusBar> lazy) {
        return new StartActivityInfoListener() {
            public void onStartActivityInfo(Intent intent, boolean z) {
                if (intent == null) {
                    Log.e("ActivityStarter", "Null intent; cannot start activity");
                } else {
                    ((StatusBar) Lazy.this.get()).startActivity(intent, z);
                }
            }
        };
    }

    static ViewGroup provideParentViewGroup(OverlayUiHost overlayUiHost) {
        return overlayUiHost.getParent();
    }
}
