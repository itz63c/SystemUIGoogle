package com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui;

import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$ScreenshotOp;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$ScreenshotOpStatus;

/* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.ContentSuggestionsServiceClient$$Lambda$1 */
final /* synthetic */ class ContentSuggestionsServiceClient$$Lambda$1 implements Runnable {
    private final ContentSuggestionsServiceClient arg$1;
    private final String arg$2;
    private final FeedbackParcelables$ScreenshotOp arg$3;
    private final FeedbackParcelables$ScreenshotOpStatus arg$4;
    private final long arg$5;

    ContentSuggestionsServiceClient$$Lambda$1(ContentSuggestionsServiceClient contentSuggestionsServiceClient, String str, FeedbackParcelables$ScreenshotOp feedbackParcelables$ScreenshotOp, FeedbackParcelables$ScreenshotOpStatus feedbackParcelables$ScreenshotOpStatus, long j) {
        this.arg$1 = contentSuggestionsServiceClient;
        this.arg$2 = str;
        this.arg$3 = feedbackParcelables$ScreenshotOp;
        this.arg$4 = feedbackParcelables$ScreenshotOpStatus;
        this.arg$5 = j;
    }

    public void run() {
        this.arg$1.lambda$notifyOp$1$ContentSuggestionsServiceClient(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
