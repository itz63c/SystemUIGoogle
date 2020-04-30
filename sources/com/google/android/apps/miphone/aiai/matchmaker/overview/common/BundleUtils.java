package com.google.android.apps.miphone.aiai.matchmaker.overview.common;

import android.app.Notification.Action;
import android.os.Bundle;
import android.os.Parcelable;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.ContentParcelables$Contents;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.EntitiesData;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$FeedbackBatch;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.InteractionContextParcelables$InteractionContext;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.ParserParcelables$ParsedViewHierarchy;
import java.util.ArrayList;

public class BundleUtils {

    public static class ContentClassificationsBundle {
        public final String activityName;
        public final Bundle assistBundle;
        public final long captureTimestampMs;
        public final ContentParcelables$Contents contents;
        public final String packageName;
        public final int taskId;

        public static ContentClassificationsBundle create(String str, String str2, int i, long j, Bundle bundle, ContentParcelables$Contents contentParcelables$Contents) {
            ContentClassificationsBundle contentClassificationsBundle = new ContentClassificationsBundle(str, str2, i, j, bundle, contentParcelables$Contents);
            return contentClassificationsBundle;
        }

        public Bundle createBundle() {
            Bundle bundle = new Bundle();
            bundle.putString("PackageName", this.packageName);
            bundle.putString("ActivityName", this.activityName);
            bundle.putInt("TaskId", this.taskId);
            bundle.putLong("CaptureTimestampMs", this.captureTimestampMs);
            bundle.putParcelable("AssistBundle", this.assistBundle);
            bundle.putParcelable("Contents", this.contents);
            bundle.putInt("Version", 3);
            bundle.putInt("BundleTypedVersion", 3);
            return bundle;
        }

        private ContentClassificationsBundle(String str, String str2, int i, long j, Bundle bundle, ContentParcelables$Contents contentParcelables$Contents) {
            this.packageName = str;
            this.activityName = str2;
            this.taskId = i;
            this.captureTimestampMs = j;
            this.assistBundle = bundle;
            this.contents = contentParcelables$Contents;
        }
    }

    public static class ContentSelectionBundle {
        public final String activityName;
        public final Bundle assistBundle;
        public final long captureTimestampMs;
        public final InteractionContextParcelables$InteractionContext interactionContext;
        public final String packageName;
        public final ParserParcelables$ParsedViewHierarchy parsedViewHierarchy;
        public final int taskId;

        public static ContentSelectionBundle create(String str, String str2, int i, long j, InteractionContextParcelables$InteractionContext interactionContextParcelables$InteractionContext, Bundle bundle, ParserParcelables$ParsedViewHierarchy parserParcelables$ParsedViewHierarchy) {
            ContentSelectionBundle contentSelectionBundle = new ContentSelectionBundle(str, str2, i, j, interactionContextParcelables$InteractionContext, bundle, parserParcelables$ParsedViewHierarchy);
            return contentSelectionBundle;
        }

        public Bundle createBundle() {
            Bundle bundle = new Bundle();
            bundle.putString("PackageName", this.packageName);
            bundle.putString("ActivityName", this.activityName);
            bundle.putInt("TaskId", this.taskId);
            bundle.putLong("CaptureTimestampMs", this.captureTimestampMs);
            bundle.putParcelable("InteractionContext", this.interactionContext);
            bundle.putParcelable("AssistBundle", this.assistBundle);
            bundle.putParcelable("ParsedViewHierarchy", this.parsedViewHierarchy);
            bundle.putInt("Version", 3);
            bundle.putInt("BundleTypedVersion", 3);
            return bundle;
        }

        private ContentSelectionBundle(String str, String str2, int i, long j, InteractionContextParcelables$InteractionContext interactionContextParcelables$InteractionContext, Bundle bundle, ParserParcelables$ParsedViewHierarchy parserParcelables$ParsedViewHierarchy) {
            this.packageName = str;
            this.activityName = str2;
            this.taskId = i;
            this.captureTimestampMs = j;
            this.interactionContext = interactionContextParcelables$InteractionContext;
            this.assistBundle = bundle;
            this.parsedViewHierarchy = parserParcelables$ParsedViewHierarchy;
        }
    }

    public static class FeedbackBundle {
        public final FeedbackParcelables$FeedbackBatch feedbackBatch;

        public static FeedbackBundle create(FeedbackParcelables$FeedbackBatch feedbackParcelables$FeedbackBatch) {
            return new FeedbackBundle(feedbackParcelables$FeedbackBatch);
        }

        public static Bundle createBundle(Bundle bundle) {
            bundle.putInt("Version", 3);
            bundle.putInt("BundleTypedVersion", 6);
            return bundle;
        }

        public Bundle createBundle() {
            Bundle bundle = new Bundle();
            bundle.putParcelable("FeedbackBatch", this.feedbackBatch);
            createBundle(bundle);
            return bundle;
        }

        private FeedbackBundle(FeedbackParcelables$FeedbackBatch feedbackParcelables$FeedbackBatch) {
            this.feedbackBatch = feedbackParcelables$FeedbackBatch;
        }
    }

    public static <T extends Parcelable> T extractParcelable(Bundle bundle, String str, Class<T> cls) {
        bundle.setClassLoader(cls.getClassLoader());
        return bundle.getParcelable(str);
    }

    public static ContentParcelables$Contents extractContentsParcelable(Bundle bundle) {
        return (ContentParcelables$Contents) extractParcelable(bundle, "Contents", ContentParcelables$Contents.class);
    }

    public static EntitiesData extractEntitiesParcelable(Bundle bundle) {
        return (EntitiesData) extractParcelable(bundle, "EntitiesData", EntitiesData.class);
    }

    public static Bundle obtainContextImageBundle(boolean z, String str, String str2, long j) {
        Bundle bundle = new Bundle();
        bundle.putInt("CONTEXT_IMAGE_BUNDLE_VERSION_KEY", 1);
        bundle.putBoolean("CONTEXT_IMAGE_PRIMARY_TASK_KEY", z);
        bundle.putString("CONTEXT_IMAGE_PACKAGE_NAME_KEY", str);
        bundle.putString("CONTEXT_IMAGE_ACTIVITY_NAME_KEY", str2);
        bundle.putLong("CONTEXT_IMAGE_CAPTURE_TIME_MS_KEY", j);
        return bundle;
    }

    public static Bundle createScreenshotActionsResponse(ArrayList<Action> arrayList) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("ScreenshotNotificationActions", arrayList);
        return bundle;
    }

    public static Bundle createSelectionsRequest(String str, String str2, int i, long j, InteractionContextParcelables$InteractionContext interactionContextParcelables$InteractionContext, Bundle bundle, ParserParcelables$ParsedViewHierarchy parserParcelables$ParsedViewHierarchy) {
        return ContentSelectionBundle.create(str, str2, i, j, interactionContextParcelables$InteractionContext, bundle, parserParcelables$ParsedViewHierarchy).createBundle();
    }

    public static Bundle createClassificationsRequest(String str, String str2, int i, long j, Bundle bundle, ContentParcelables$Contents contentParcelables$Contents) {
        return ContentClassificationsBundle.create(str, str2, i, j, bundle, contentParcelables$Contents).createBundle();
    }
}
