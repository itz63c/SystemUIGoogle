package com.android.systemui.screenshot;

import android.app.Notification.BigPictureStyle;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.SystemUI;
import com.android.systemui.util.NotificationChannels;

public class ScreenshotNotificationsController {
    private final Context mContext;
    private int mIconSize = this.mResources.getDimensionPixelSize(17104902);
    private final NotificationManager mNotificationManager;
    private final BigPictureStyle mNotificationStyle;
    private int mPreviewHeight;
    private int mPreviewWidth;
    private final Resources mResources;

    ScreenshotNotificationsController(Context context, WindowManager windowManager) {
        int i;
        this.mContext = context;
        this.mResources = context.getResources();
        this.mNotificationManager = (NotificationManager) context.getSystemService("notification");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        try {
            i = this.mResources.getDimensionPixelSize(C2009R$dimen.notification_panel_width);
        } catch (NotFoundException unused) {
            i = 0;
        }
        if (i <= 0) {
            i = displayMetrics.widthPixels;
        }
        this.mPreviewWidth = i;
        this.mPreviewHeight = this.mResources.getDimensionPixelSize(C2009R$dimen.notification_max_height);
        this.mNotificationStyle = new BigPictureStyle();
    }

    public void notifyScreenshotError(int i) {
        Resources resources = this.mContext.getResources();
        String string = resources.getString(i);
        Builder color = new Builder(this.mContext, NotificationChannels.ALERTS).setTicker(resources.getString(C2017R$string.screenshot_failed_title)).setContentTitle(resources.getString(C2017R$string.screenshot_failed_title)).setContentText(string).setSmallIcon(C2010R$drawable.stat_notify_image_error).setWhen(System.currentTimeMillis()).setVisibility(1).setCategory("err").setAutoCancel(true).setColor(this.mContext.getColor(17170460));
        Intent createAdminSupportIntent = ((DevicePolicyManager) this.mContext.getSystemService("device_policy")).createAdminSupportIntent("policy_disable_screen_capture");
        if (createAdminSupportIntent != null) {
            color.setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, createAdminSupportIntent, 0, null, UserHandle.CURRENT));
        }
        SystemUI.overrideNotificationAppName(this.mContext, color, true);
        this.mNotificationManager.notify(1, new BigTextStyle(color).bigText(string).build());
    }

    static void cancelScreenshotNotification(Context context) {
        ((NotificationManager) context.getSystemService("notification")).cancel(1);
    }
}
