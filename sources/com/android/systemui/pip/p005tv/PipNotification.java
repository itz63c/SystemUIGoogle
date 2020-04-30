package com.android.systemui.pip.p005tv;

import android.app.Notification.BigPictureStyle;
import android.app.Notification.Builder;
import android.app.Notification.TvExtender;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ParceledListSlice;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaController.Callback;
import android.media.session.PlaybackState;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.pip.p005tv.PipManager.Listener;
import com.android.systemui.pip.p005tv.PipManager.MediaListener;
import com.android.systemui.util.NotificationChannels;

/* renamed from: com.android.systemui.pip.tv.PipNotification */
public class PipNotification {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = PipManager.DEBUG;
    private static final String NOTIFICATION_TAG = "PipNotification";
    private Bitmap mArt;
    private int mDefaultIconResId;
    private String mDefaultTitle;
    private final BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (PipNotification.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Received ");
                sb.append(intent.getAction());
                sb.append(" from the notification UI");
                Log.d(PipNotification.NOTIFICATION_TAG, sb.toString());
            }
            String action = intent.getAction();
            char c = 65535;
            int hashCode = action.hashCode();
            if (hashCode != -1402086132) {
                if (hashCode == 1201988555 && action.equals("PipNotification.menu")) {
                    c = 0;
                }
            } else if (action.equals("PipNotification.close")) {
                c = 1;
            }
            if (c == 0) {
                PipNotification.this.mPipManager.showPictureInPictureMenu();
            } else if (c == 1) {
                PipNotification.this.mPipManager.closePip();
            }
        }
    };
    /* access modifiers changed from: private */
    public MediaController mMediaController;
    /* access modifiers changed from: private */
    public Callback mMediaControllerCallback = new Callback() {
        public void onPlaybackStateChanged(PlaybackState playbackState) {
            if (PipNotification.this.updateMediaControllerMetadata() && PipNotification.this.mNotified) {
                PipNotification.this.notifyPipNotification();
            }
        }
    };
    private final Builder mNotificationBuilder;
    private final NotificationManager mNotificationManager;
    /* access modifiers changed from: private */
    public boolean mNotified;
    private Listener mPipListener = new Listener() {
        public void onPipMenuActionsChanged(ParceledListSlice parceledListSlice) {
        }

        public void onPipResizeAboutToStart() {
        }

        public void onShowPipMenu() {
        }

        public void onPipEntered() {
            PipNotification.this.updateMediaControllerMetadata();
            PipNotification.this.notifyPipNotification();
        }

        public void onPipActivityClosed() {
            PipNotification.this.dismissPipNotification();
        }

        public void onMoveToFullscreen() {
            PipNotification.this.dismissPipNotification();
        }
    };
    /* access modifiers changed from: private */
    public final PipManager mPipManager;
    private final MediaListener mPipMediaListener = new MediaListener() {
        public void onMediaControllerChanged() {
            MediaController mediaController = PipNotification.this.mPipManager.getMediaController();
            if (PipNotification.this.mMediaController != mediaController) {
                if (PipNotification.this.mMediaController != null) {
                    PipNotification.this.mMediaController.unregisterCallback(PipNotification.this.mMediaControllerCallback);
                }
                PipNotification.this.mMediaController = mediaController;
                if (PipNotification.this.mMediaController != null) {
                    PipNotification.this.mMediaController.registerCallback(PipNotification.this.mMediaControllerCallback);
                }
                if (PipNotification.this.updateMediaControllerMetadata() && PipNotification.this.mNotified) {
                    PipNotification.this.notifyPipNotification();
                }
            }
        }
    };
    private String mTitle;

    public PipNotification(Context context, BroadcastDispatcher broadcastDispatcher, PipManager pipManager) {
        this.mNotificationManager = (NotificationManager) context.getSystemService("notification");
        String str = "PipNotification.menu";
        String str2 = "PipNotification.close";
        this.mNotificationBuilder = new Builder(context, NotificationChannels.TVPIP).setLocalOnly(true).setOngoing(DEBUG).setCategory("sys").extend(new TvExtender().setContentIntent(createPendingIntent(context, str)).setDeleteIntent(createPendingIntent(context, str2)));
        this.mPipManager = pipManager;
        pipManager.addListener(this.mPipListener);
        this.mPipManager.addMediaListener(this.mPipMediaListener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(str);
        intentFilter.addAction(str2);
        broadcastDispatcher.registerReceiver(this.mEventReceiver, intentFilter);
        onConfigurationChanged(context);
    }

    /* access modifiers changed from: 0000 */
    public void onConfigurationChanged(Context context) {
        this.mDefaultTitle = context.getResources().getString(C2017R$string.pip_notification_unknown_title);
        this.mDefaultIconResId = C2010R$drawable.pip_icon;
        if (this.mNotified) {
            notifyPipNotification();
        }
    }

    /* access modifiers changed from: private */
    public void notifyPipNotification() {
        this.mNotified = true;
        this.mNotificationBuilder.setShowWhen(true).setWhen(System.currentTimeMillis()).setSmallIcon(this.mDefaultIconResId).setContentTitle(!TextUtils.isEmpty(this.mTitle) ? this.mTitle : this.mDefaultTitle);
        if (this.mArt != null) {
            this.mNotificationBuilder.setStyle(new BigPictureStyle().bigPicture(this.mArt));
        } else {
            this.mNotificationBuilder.setStyle(null);
        }
        this.mNotificationManager.notify(NOTIFICATION_TAG, 1100, this.mNotificationBuilder.build());
    }

    /* access modifiers changed from: private */
    public void dismissPipNotification() {
        this.mNotified = DEBUG;
        this.mNotificationManager.cancel(NOTIFICATION_TAG, 1100);
    }

    /* access modifiers changed from: private */
    public boolean updateMediaControllerMetadata() {
        Bitmap bitmap;
        String str = null;
        if (this.mPipManager.getMediaController() != null) {
            MediaMetadata metadata = this.mPipManager.getMediaController().getMetadata();
            if (metadata != null) {
                str = metadata.getString("android.media.metadata.DISPLAY_TITLE");
                if (TextUtils.isEmpty(str)) {
                    str = metadata.getString("android.media.metadata.TITLE");
                }
                Bitmap bitmap2 = metadata.getBitmap("android.media.metadata.ALBUM_ART");
                bitmap = bitmap2 == null ? metadata.getBitmap("android.media.metadata.ART") : bitmap2;
                if (!TextUtils.equals(str, this.mTitle) && bitmap == this.mArt) {
                    return DEBUG;
                }
                this.mTitle = str;
                this.mArt = bitmap;
                return true;
            }
        }
        bitmap = null;
        if (!TextUtils.equals(str, this.mTitle)) {
        }
        this.mTitle = str;
        this.mArt = bitmap;
        return true;
    }

    private static PendingIntent createPendingIntent(Context context, String str) {
        return PendingIntent.getBroadcast(context, 0, new Intent(str), 268435456);
    }
}
