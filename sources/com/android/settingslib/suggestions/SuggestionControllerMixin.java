package com.android.settingslib.suggestions;

import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.service.settings.suggestions.Suggestion;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import java.util.List;

@Deprecated
public class SuggestionControllerMixin implements Object, LifecycleObserver {
    private final Context mContext;
    private final SuggestionControllerHost mHost;
    private final SuggestionController mSuggestionController;

    public interface SuggestionControllerHost {
        void onSuggestionReady(List<Suggestion> list);
    }

    public void onLoaderReset(Loader<List<Suggestion>> loader) {
    }

    @OnLifecycleEvent(Event.ON_START)
    public void onStart() {
        this.mSuggestionController.start();
    }

    @OnLifecycleEvent(Event.ON_STOP)
    public void onStop() {
        this.mSuggestionController.stop();
    }

    public Loader<List<Suggestion>> onCreateLoader(int i, Bundle bundle) {
        if (i == 42) {
            return new SuggestionLoader(this.mContext, this.mSuggestionController);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("This loader id is not supported ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    public void onLoadFinished(Loader<List<Suggestion>> loader, List<Suggestion> list) {
        this.mHost.onSuggestionReady(list);
    }
}
