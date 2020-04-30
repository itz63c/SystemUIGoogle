package com.google.android.systemui.statusbar.phone;

import android.os.Handler;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.SensorManagerPlugin.SensorEvent;
import com.android.systemui.plugins.SensorManagerPlugin.SensorEventListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import com.android.systemui.util.Assert;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Objects;

public class NotificationIconCenteringController implements SensorEventListener {
    private NotificationEntry mEntryCentered;
    private final NotificationEntryManager mEntryManager;
    @VisibleForTesting
    protected boolean mIsSkipGestureEnabled;
    @VisibleForTesting
    protected Handler mMainThreadHandler;
    @VisibleForTesting
    protected String mMusicPlayingPkg;
    private NotificationIconAreaController mNotificationIconAreaController;
    private boolean mRegistered;
    @VisibleForTesting
    protected final Runnable mResetCenteredIconRunnable;

    public void onSensorChanged(SensorEvent sensorEvent) {
        this.mMainThreadHandler.post(new Runnable(sensorEvent) {
            public final /* synthetic */ SensorEvent f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NotificationIconCenteringController.this.lambda$onSensorChanged$1$NotificationIconCenteringController(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onSensorChanged$1 */
    public /* synthetic */ void lambda$onSensorChanged$1$NotificationIconCenteringController(SensorEvent sensorEvent) {
        boolean z = false;
        if (sensorEvent.getValues()[0] == 1.0f) {
            z = true;
        }
        this.mIsSkipGestureEnabled = z;
        updateCenteredIcon();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void updateCenteredIcon() {
        if (isMusicPlaying() && isSkipGestureEnabled()) {
            for (NotificationEntry notificationEntry : this.mEntryManager.getVisibleNotifications()) {
                if (notificationEntry.isMediaNotification() && Objects.equals(notificationEntry.getSbn().getPackageName(), this.mMusicPlayingPkg)) {
                    showIconCentered(notificationEntry);
                    return;
                }
            }
        }
        showIconCentered(null);
    }

    private void showIconCentered(NotificationEntry notificationEntry) {
        Assert.isMainThread();
        this.mMainThreadHandler.removeCallbacks(this.mResetCenteredIconRunnable);
        if (notificationEntry == null) {
            this.mMainThreadHandler.postDelayed(this.mResetCenteredIconRunnable, 250);
            return;
        }
        this.mNotificationIconAreaController.showIconCentered(notificationEntry);
        this.mEntryCentered = notificationEntry;
    }

    private boolean isMusicPlaying() {
        return this.mMusicPlayingPkg != null;
    }

    private boolean isSkipGestureEnabled() {
        return this.mIsSkipGestureEnabled;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        PrintWriter append = printWriter.append("NotifIconCenterContr").append(": ");
        StringBuilder sb = new StringBuilder();
        sb.append("\nisMusicPlaying: ");
        sb.append(isMusicPlaying());
        PrintWriter append2 = append.append(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("\nisSkipGestureEnabled: ");
        sb2.append(isSkipGestureEnabled());
        PrintWriter append3 = append2.append(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("\nmSkipStatusRegistered: ");
        sb3.append(this.mRegistered);
        PrintWriter append4 = append3.append(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("\nmEntryCentered: ");
        sb4.append(this.mEntryCentered);
        sb4.append("\n");
        append4.append(sb4.toString());
    }
}
