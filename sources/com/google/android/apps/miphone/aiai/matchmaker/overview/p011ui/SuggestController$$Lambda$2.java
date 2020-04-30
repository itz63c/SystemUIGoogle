package com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui;

import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$FeedbackBatch;

/* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.SuggestController$$Lambda$2 */
final /* synthetic */ class SuggestController$$Lambda$2 implements Runnable {
    private final SuggestController arg$1;
    private final FeedbackParcelables$FeedbackBatch arg$2;
    private final String arg$3;
    private final SuggestListener arg$4;

    SuggestController$$Lambda$2(SuggestController suggestController, FeedbackParcelables$FeedbackBatch feedbackParcelables$FeedbackBatch, String str, SuggestListener suggestListener) {
        this.arg$1 = suggestController;
        this.arg$2 = feedbackParcelables$FeedbackBatch;
        this.arg$3 = str;
        this.arg$4 = suggestListener;
    }

    public void run() {
        this.arg$1.lambda$reportMetricsToService$3$SuggestController(this.arg$2, this.arg$3, this.arg$4);
    }
}
