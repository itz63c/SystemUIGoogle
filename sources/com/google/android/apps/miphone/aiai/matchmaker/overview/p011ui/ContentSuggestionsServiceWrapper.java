package com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;

/* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.ContentSuggestionsServiceWrapper */
public interface ContentSuggestionsServiceWrapper {

    /* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.ContentSuggestionsServiceWrapper$BundleCallback */
    public interface BundleCallback {
        void onResult(Bundle bundle);
    }

    void classifyContentSelections(Bundle bundle, BundleCallback bundleCallback);

    void connectAndRunAsync(Runnable runnable);

    void notifyInteraction(String str, Bundle bundle);

    void processContextImage(int i, @Nullable Bitmap bitmap, Bundle bundle);

    void suggestContentSelections(int i, Bundle bundle, BundleCallback bundleCallback);
}
