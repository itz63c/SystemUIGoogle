package com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui;

import android.os.Bundle;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.InteractionContextParcelables$InteractionContext;
import com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui.ContentSuggestionsServiceWrapper.BundleCallback;

/* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.ContentSuggestionsServiceClient$$Lambda$0 */
final /* synthetic */ class ContentSuggestionsServiceClient$$Lambda$0 implements Runnable {
    private final ContentSuggestionsServiceClient arg$1;
    private final int arg$2;
    private final Bundle arg$3;
    private final String arg$4;
    private final String arg$5;
    private final long arg$6;
    private final InteractionContextParcelables$InteractionContext arg$7;
    private final boolean arg$8;
    private final BundleCallback arg$9;

    ContentSuggestionsServiceClient$$Lambda$0(ContentSuggestionsServiceClient contentSuggestionsServiceClient, int i, Bundle bundle, String str, String str2, long j, InteractionContextParcelables$InteractionContext interactionContextParcelables$InteractionContext, boolean z, BundleCallback bundleCallback) {
        this.arg$1 = contentSuggestionsServiceClient;
        this.arg$2 = i;
        this.arg$3 = bundle;
        this.arg$4 = str;
        this.arg$5 = str2;
        this.arg$6 = j;
        this.arg$7 = interactionContextParcelables$InteractionContext;
        this.arg$8 = z;
        this.arg$9 = bundleCallback;
    }

    public void run() {
        this.arg$1.mo20174x7d6d7325(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9);
    }
}
