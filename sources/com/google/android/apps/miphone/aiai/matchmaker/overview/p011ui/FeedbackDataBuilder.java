package com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui;

import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$Feedback;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$FeedbackBatch;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$ScreenshotActionFeedback;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$ScreenshotFeedback;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$ScreenshotOp;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$ScreenshotOpFeedback;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$ScreenshotOpStatus;
import com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.FeedbackDataBuilder */
public class FeedbackDataBuilder {
    final List<FeedbackParcelables$Feedback> feedbacks = new ArrayList();
    final String overviewSessionId;
    final int screenSessionId;

    public FeedbackParcelables$FeedbackBatch build() {
        FeedbackParcelables$FeedbackBatch create = FeedbackParcelables$FeedbackBatch.create();
        create.screenSessionId = (long) this.screenSessionId;
        create.overviewSessionId = this.overviewSessionId;
        List<FeedbackParcelables$Feedback> list = this.feedbacks;
        Utils.checkNotNull(list);
        create.feedback = list;
        return create;
    }

    static FeedbackDataBuilder newBuilder(String str) {
        return new FeedbackDataBuilder(str);
    }

    /* access modifiers changed from: 0000 */
    public FeedbackDataBuilder addScreenshotActionFeedback(String str, String str2, boolean z) {
        FeedbackParcelables$ScreenshotActionFeedback create = FeedbackParcelables$ScreenshotActionFeedback.create();
        create.actionType = str2;
        create.isSmartActions = z;
        FeedbackParcelables$ScreenshotFeedback create2 = FeedbackParcelables$ScreenshotFeedback.create();
        create2.screenshotId = str;
        create2.screenshotFeedback = create;
        addFeedback().feedback = create2;
        return this;
    }

    /* access modifiers changed from: 0000 */
    public FeedbackDataBuilder addScreenshotOpFeedback(String str, FeedbackParcelables$ScreenshotOp feedbackParcelables$ScreenshotOp, FeedbackParcelables$ScreenshotOpStatus feedbackParcelables$ScreenshotOpStatus, long j) {
        FeedbackParcelables$ScreenshotOpFeedback create = FeedbackParcelables$ScreenshotOpFeedback.create();
        create.durationMs = j;
        create.f92op = feedbackParcelables$ScreenshotOp;
        create.status = feedbackParcelables$ScreenshotOpStatus;
        FeedbackParcelables$ScreenshotFeedback create2 = FeedbackParcelables$ScreenshotFeedback.create();
        create2.screenshotId = str;
        create2.screenshotFeedback = create;
        addFeedback().feedback = create2;
        return this;
    }

    private FeedbackParcelables$Feedback addFeedback() {
        FeedbackParcelables$Feedback create = FeedbackParcelables$Feedback.create();
        this.feedbacks.add(create);
        return create;
    }

    private FeedbackDataBuilder(String str) {
        this.overviewSessionId = str;
        this.screenSessionId = 0;
    }
}
