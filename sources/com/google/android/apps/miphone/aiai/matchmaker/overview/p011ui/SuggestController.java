package com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui;

import android.app.contentsuggestions.ClassificationsRequest;
import android.app.contentsuggestions.ContentClassification;
import android.app.contentsuggestions.ContentSuggestionsManager;
import android.app.contentsuggestions.ContentSuggestionsManager.ClassificationsCallback;
import android.app.contentsuggestions.SelectionsRequest.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$FeedbackBatch;
import com.google.android.apps.miphone.aiai.matchmaker.overview.common.BundleUtils.FeedbackBundle;
import com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui.ContentSuggestionsServiceWrapper.BundleCallback;
import com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui.utils.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.SuggestController */
public class SuggestController {
    public static Factory defaultFactory = SuggestController$$Lambda$3.$instance;
    private final ContentSuggestionsServiceWrapper wrapper;

    /* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.SuggestController$Factory */
    public interface Factory {
        ContentSuggestionsServiceWrapper create(Context context, Executor executor, Handler handler);
    }

    static final /* synthetic */ ContentSuggestionsServiceWrapper lambda$static$0$SuggestController(Context context, Executor executor, Handler handler) {
        return new ContentSuggestionsServiceWrapper((ContentSuggestionsManager) context.getSystemService(ContentSuggestionsManager.class), executor, handler) {
            final /* synthetic */ ContentSuggestionsManager val$contentSuggestionsManager;
            final /* synthetic */ Executor val$executor;

            {
                this.val$contentSuggestionsManager = r1;
                this.val$executor = r2;
            }

            public void suggestContentSelections(int i, Bundle bundle, BundleCallback bundleCallback) {
                try {
                    this.val$contentSuggestionsManager.suggestContentSelections(new Builder(i).setExtras(bundle).build(), this.val$executor, new SuggestController$1$$Lambda$0(bundleCallback));
                } catch (Throwable th) {
                    LogUtils.m118e("Failed to suggestContentSelections", th);
                }
            }

            public void classifyContentSelections(Bundle bundle, final BundleCallback bundleCallback) {
                try {
                    this.val$contentSuggestionsManager.classifyContentSelections(new ClassificationsRequest.Builder(new ArrayList()).setExtras(bundle).build(), this.val$executor, new ClassificationsCallback(this) {
                        public void onContentClassificationsAvailable(int i, List<ContentClassification> list) {
                            bundleCallback.onResult(((ContentClassification) list.get(0)).getExtras());
                        }
                    });
                } catch (Throwable th) {
                    LogUtils.m118e("Failed to classifyContentSelections", th);
                }
            }

            public void notifyInteraction(String str, Bundle bundle) {
                try {
                    this.val$contentSuggestionsManager.notifyInteraction(str, bundle);
                } catch (Throwable th) {
                    LogUtils.m118e("Failed to notifyInteraction", th);
                }
            }

            public void processContextImage(int i, @Nullable Bitmap bitmap, Bundle bundle) {
                bundle.putLong("CAPTURE_TIME_MS", System.currentTimeMillis());
                try {
                    this.val$contentSuggestionsManager.provideContextImage(i, bundle);
                } catch (Throwable th) {
                    LogUtils.m118e("Failed to provideContextImage", th);
                }
            }

            public void connectAndRunAsync(Runnable runnable) {
                this.val$executor.execute(runnable);
            }
        };
    }

    public static SuggestController create(Context context, Context context2, Executor executor, Handler handler) {
        return new SuggestController(context, context2, executor, handler);
    }

    /* access modifiers changed from: 0000 */
    public ContentSuggestionsServiceWrapper getWrapper() {
        return this.wrapper;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void reportMetricsToService(String str, FeedbackParcelables$FeedbackBatch feedbackParcelables$FeedbackBatch, @Nullable SuggestListener suggestListener) {
        if (!feedbackParcelables$FeedbackBatch.feedback.isEmpty()) {
            this.wrapper.connectAndRunAsync(new SuggestController$$Lambda$2(this, feedbackParcelables$FeedbackBatch, str, suggestListener));
        }
    }

    /* access modifiers changed from: 0000 */
    public final /* synthetic */ void lambda$reportMetricsToService$3$SuggestController(FeedbackParcelables$FeedbackBatch feedbackParcelables$FeedbackBatch, String str, SuggestListener suggestListener) {
        try {
            this.wrapper.notifyInteraction(str, FeedbackBundle.create(feedbackParcelables$FeedbackBatch).createBundle());
            if (suggestListener != null) {
                synchronized (this) {
                    suggestListener.onFeedbackBatchSent(str, feedbackParcelables$FeedbackBatch);
                }
            }
        } catch (Throwable th) {
            LogUtils.m118e("Failed to call service - report metrics/feedback.", th);
        }
    }

    protected SuggestController(Context context, Context context2, Executor executor, Handler handler) {
        this.wrapper = defaultFactory.create(context, executor, handler);
    }
}
