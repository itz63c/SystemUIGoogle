package com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui;

import android.app.contentsuggestions.ContentSelection;
import android.app.contentsuggestions.ContentSuggestionsManager.SelectionsCallback;
import com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui.ContentSuggestionsServiceWrapper.BundleCallback;
import java.util.List;

/* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.SuggestController$1$$Lambda$0 */
final /* synthetic */ class SuggestController$1$$Lambda$0 implements SelectionsCallback {
    private final BundleCallback arg$1;

    SuggestController$1$$Lambda$0(BundleCallback bundleCallback) {
        this.arg$1 = bundleCallback;
    }

    public void onContentSelectionsAvailable(int i, List list) {
        this.arg$1.onResult(((ContentSelection) list.get(0)).getExtras());
    }
}
