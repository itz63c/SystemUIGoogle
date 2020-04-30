package com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui;

/* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.ContentSuggestionsServiceClient$$Lambda$2 */
final /* synthetic */ class ContentSuggestionsServiceClient$$Lambda$2 implements Runnable {
    private final ContentSuggestionsServiceClient arg$1;
    private final String arg$2;
    private final String arg$3;
    private final boolean arg$4;

    ContentSuggestionsServiceClient$$Lambda$2(ContentSuggestionsServiceClient contentSuggestionsServiceClient, String str, String str2, boolean z) {
        this.arg$1 = contentSuggestionsServiceClient;
        this.arg$2 = str;
        this.arg$3 = str2;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$notifyAction$2$ContentSuggestionsServiceClient(this.arg$2, this.arg$3, this.arg$4);
    }
}
