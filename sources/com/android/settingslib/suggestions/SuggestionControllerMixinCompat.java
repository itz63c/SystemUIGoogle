package com.android.settingslib.suggestions;

import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.OnLifecycleEvent;

public class SuggestionControllerMixinCompat implements Object {
    private final SuggestionController mSuggestionController;

    @OnLifecycleEvent(Event.ON_START)
    public void onStart() {
        this.mSuggestionController.start();
    }

    @OnLifecycleEvent(Event.ON_STOP)
    public void onStop() {
        this.mSuggestionController.stop();
    }
}
