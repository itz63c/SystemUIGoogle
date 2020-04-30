package com.android.systemui.screenshot;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification.Action;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ScreenshotSmartActions {
    @VisibleForTesting
    static CompletableFuture<List<Action>> getSmartActionsFuture(String str, Bitmap bitmap, ScreenshotNotificationSmartActionsProvider screenshotNotificationSmartActionsProvider, boolean z, boolean z2) {
        CompletableFuture<List<Action>> completableFuture;
        ComponentName componentName;
        String str2 = "";
        String str3 = "ScreenshotSmartActions";
        if (!z) {
            Slog.i(str3, "Screenshot Intelligence not enabled, returning empty list.");
            return CompletableFuture.completedFuture(Collections.emptyList());
        } else if (bitmap.getConfig() != Config.HARDWARE) {
            Slog.w(str3, String.format("Bitmap expected: Hardware, Bitmap found: %s. Returning empty list.", new Object[]{bitmap.getConfig()}));
            return CompletableFuture.completedFuture(Collections.emptyList());
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Screenshot from a managed profile: ");
            sb.append(z2);
            Slog.d(str3, sb.toString());
            long uptimeMillis = SystemClock.uptimeMillis();
            try {
                RunningTaskInfo runningTask = ActivityManagerWrapper.getInstance().getRunningTask();
                if (runningTask == null || runningTask.topActivity == null) {
                    componentName = new ComponentName(str2, str2);
                } else {
                    componentName = runningTask.topActivity;
                }
                completableFuture = screenshotNotificationSmartActionsProvider.getActions(str, bitmap, componentName, z2);
            } catch (Throwable th) {
                long uptimeMillis2 = SystemClock.uptimeMillis() - uptimeMillis;
                CompletableFuture<List<Action>> completedFuture = CompletableFuture.completedFuture(Collections.emptyList());
                Slog.e(str3, "Failed to get future for screenshot notification smart actions.", th);
                notifyScreenshotOp(str, screenshotNotificationSmartActionsProvider, ScreenshotOp.REQUEST_SMART_ACTIONS, ScreenshotOpStatus.ERROR, uptimeMillis2);
                completableFuture = completedFuture;
            }
            return completableFuture;
        }
    }

    @VisibleForTesting
    static List<Action> getSmartActions(String str, CompletableFuture<List<Action>> completableFuture, int i, ScreenshotNotificationSmartActionsProvider screenshotNotificationSmartActionsProvider) {
        ScreenshotOpStatus screenshotOpStatus;
        String str2 = "ScreenshotSmartActions";
        long uptimeMillis = SystemClock.uptimeMillis();
        try {
            List<Action> list = (List) completableFuture.get((long) i, TimeUnit.MILLISECONDS);
            long uptimeMillis2 = SystemClock.uptimeMillis() - uptimeMillis;
            Slog.d(str2, String.format("Got %d smart actions. Wait time: %d ms", new Object[]{Integer.valueOf(list.size()), Long.valueOf(uptimeMillis2)}));
            notifyScreenshotOp(str, screenshotNotificationSmartActionsProvider, ScreenshotOp.WAIT_FOR_SMART_ACTIONS, ScreenshotOpStatus.SUCCESS, uptimeMillis2);
            return list;
        } catch (Throwable th) {
            long uptimeMillis3 = SystemClock.uptimeMillis() - uptimeMillis;
            Slog.e(str2, String.format("Error getting smart actions. Wait time: %d ms", new Object[]{Long.valueOf(uptimeMillis3)}), th);
            if (th instanceof TimeoutException) {
                screenshotOpStatus = ScreenshotOpStatus.TIMEOUT;
            } else {
                screenshotOpStatus = ScreenshotOpStatus.ERROR;
            }
            String str3 = str;
            ScreenshotNotificationSmartActionsProvider screenshotNotificationSmartActionsProvider2 = screenshotNotificationSmartActionsProvider;
            notifyScreenshotOp(str3, screenshotNotificationSmartActionsProvider2, ScreenshotOp.WAIT_FOR_SMART_ACTIONS, screenshotOpStatus, uptimeMillis3);
            return Collections.emptyList();
        }
    }

    static void notifyScreenshotOp(String str, ScreenshotNotificationSmartActionsProvider screenshotNotificationSmartActionsProvider, ScreenshotOp screenshotOp, ScreenshotOpStatus screenshotOpStatus, long j) {
        try {
            screenshotNotificationSmartActionsProvider.notifyOp(str, screenshotOp, screenshotOpStatus, j);
        } catch (Throwable th) {
            Slog.e("ScreenshotSmartActions", "Error in notifyScreenshotOp: ", th);
        }
    }

    static void notifyScreenshotAction(Context context, String str, String str2, boolean z) {
        try {
            SystemUIFactory.getInstance().createScreenshotNotificationSmartActionsProvider(context, AsyncTask.THREAD_POOL_EXECUTOR, new Handler()).notifyAction(str, str2, z);
        } catch (Throwable th) {
            Slog.e("ScreenshotSmartActions", "Error in notifyScreenshotAction: ", th);
        }
    }
}
