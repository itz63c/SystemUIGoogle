package com.google.android.systemui.screenshot;

import android.app.Notification.Action;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import com.android.systemui.screenshot.ScreenshotNotificationSmartActionsProvider;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$ScreenshotOp;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$ScreenshotOpStatus;
import com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui.ContentSuggestionsServiceClient;
import com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui.ContentSuggestionsServiceWrapper.BundleCallback;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ScreenshotNotificationSmartActionsProviderGoogle extends ScreenshotNotificationSmartActionsProvider {
    private static final ImmutableMap<ScreenshotOp, FeedbackParcelables$ScreenshotOp> SCREENSHOT_OP_MAP = ImmutableMap.builder().put(ScreenshotOp.RETRIEVE_SMART_ACTIONS, FeedbackParcelables$ScreenshotOp.RETRIEVE_SMART_ACTIONS).put(ScreenshotOp.REQUEST_SMART_ACTIONS, FeedbackParcelables$ScreenshotOp.REQUEST_SMART_ACTIONS).put(ScreenshotOp.WAIT_FOR_SMART_ACTIONS, FeedbackParcelables$ScreenshotOp.WAIT_FOR_SMART_ACTIONS).build();
    private static final ImmutableMap<ScreenshotOpStatus, FeedbackParcelables$ScreenshotOpStatus> SCREENSHOT_OP_STATUS_MAP = ImmutableMap.builder().put(ScreenshotOpStatus.SUCCESS, FeedbackParcelables$ScreenshotOpStatus.SUCCESS).put(ScreenshotOpStatus.ERROR, FeedbackParcelables$ScreenshotOpStatus.ERROR).put(ScreenshotOpStatus.TIMEOUT, FeedbackParcelables$ScreenshotOpStatus.TIMEOUT).build();
    private final ContentSuggestionsServiceClient mClient;

    public ScreenshotNotificationSmartActionsProviderGoogle(Context context, Executor executor, Handler handler) {
        this.mClient = new ContentSuggestionsServiceClient(context, executor, handler);
    }

    public CompletableFuture<List<Action>> getActions(String str, Bitmap bitmap, ComponentName componentName, boolean z) {
        CompletableFuture<List<Action>> completableFuture = new CompletableFuture<>();
        String str2 = "ScreenshotActionsGoogle";
        if (bitmap.getConfig() != Config.HARDWARE) {
            Log.e(str2, String.format("Bitmap expected: Hardware, Bitmap found: %s. Returning empty list.", new Object[]{bitmap.getConfig()}));
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        final long uptimeMillis = SystemClock.uptimeMillis();
        Log.d(str2, "Calling AiAi to obtain screenshot notification smart actions.");
        ContentSuggestionsServiceClient contentSuggestionsServiceClient = this.mClient;
        String packageName = componentName.getPackageName();
        String className = componentName.getClassName();
        final CompletableFuture<List<Action>> completableFuture2 = completableFuture;
        final String str3 = str;
        C19441 r0 = new BundleCallback() {
            public void onResult(Bundle bundle) {
                ScreenshotNotificationSmartActionsProviderGoogle.this.completeFuture(bundle, completableFuture2);
                long uptimeMillis = SystemClock.uptimeMillis() - uptimeMillis;
                Log.d("ScreenshotActionsGoogle", String.format("Total time taken to get smart actions: %d ms", new Object[]{Long.valueOf(uptimeMillis)}));
                ScreenshotNotificationSmartActionsProviderGoogle.this.notifyOp(str3, ScreenshotOp.RETRIEVE_SMART_ACTIONS, ScreenshotOpStatus.SUCCESS, uptimeMillis);
            }
        };
        contentSuggestionsServiceClient.provideScreenshotActions(bitmap, packageName, className, z, r0);
        return completableFuture;
    }

    public void notifyOp(String str, ScreenshotOp screenshotOp, ScreenshotOpStatus screenshotOpStatus, long j) {
        this.mClient.notifyOp(str, (FeedbackParcelables$ScreenshotOp) SCREENSHOT_OP_MAP.getOrDefault(screenshotOp, FeedbackParcelables$ScreenshotOp.OP_UNKNOWN), (FeedbackParcelables$ScreenshotOpStatus) SCREENSHOT_OP_STATUS_MAP.getOrDefault(screenshotOpStatus, FeedbackParcelables$ScreenshotOpStatus.OP_STATUS_UNKNOWN), j);
    }

    public void notifyAction(String str, String str2, boolean z) {
        this.mClient.notifyAction(str, str2, z);
    }

    /* access modifiers changed from: 0000 */
    public void completeFuture(Bundle bundle, CompletableFuture<List<Action>> completableFuture) {
        String str = "ScreenshotNotificationActions";
        if (bundle.containsKey(str)) {
            completableFuture.complete(bundle.getParcelableArrayList(str));
        } else {
            completableFuture.complete(Collections.emptyList());
        }
    }
}
