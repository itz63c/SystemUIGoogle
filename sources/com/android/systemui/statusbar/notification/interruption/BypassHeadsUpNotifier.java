package com.android.systemui.statusbar.notification.interruption;

import android.content.Context;
import android.media.MediaMetadata;
import android.provider.Settings.Secure;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationMediaManager.MediaListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: BypassHeadsUpNotifier.kt */
public final class BypassHeadsUpNotifier implements StateListener, MediaListener {
    private final KeyguardBypassController bypassController;
    /* access modifiers changed from: private */
    public final Context context;
    private NotificationEntry currentMediaEntry;
    /* access modifiers changed from: private */
    public boolean enabled = true;
    private final NotificationEntryManager entryManager;
    private boolean fullyAwake;
    private final HeadsUpManagerPhone headsUpManager;
    private final NotificationMediaManager mediaManager;
    private final NotificationLockscreenUserManager notificationLockscreenUserManager;
    private final StatusBarStateController statusBarStateController;

    public BypassHeadsUpNotifier(Context context2, KeyguardBypassController keyguardBypassController, StatusBarStateController statusBarStateController2, HeadsUpManagerPhone headsUpManagerPhone, NotificationLockscreenUserManager notificationLockscreenUserManager2, NotificationMediaManager notificationMediaManager, NotificationEntryManager notificationEntryManager, TunerService tunerService) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Intrinsics.checkParameterIsNotNull(keyguardBypassController, "bypassController");
        Intrinsics.checkParameterIsNotNull(statusBarStateController2, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(headsUpManagerPhone, "headsUpManager");
        Intrinsics.checkParameterIsNotNull(notificationLockscreenUserManager2, "notificationLockscreenUserManager");
        Intrinsics.checkParameterIsNotNull(notificationMediaManager, "mediaManager");
        Intrinsics.checkParameterIsNotNull(notificationEntryManager, "entryManager");
        Intrinsics.checkParameterIsNotNull(tunerService, "tunerService");
        this.context = context2;
        this.bypassController = keyguardBypassController;
        this.statusBarStateController = statusBarStateController2;
        this.headsUpManager = headsUpManagerPhone;
        this.notificationLockscreenUserManager = notificationLockscreenUserManager2;
        this.mediaManager = notificationMediaManager;
        this.entryManager = notificationEntryManager;
        statusBarStateController2.addCallback(this);
        tunerService.addTunable(new Tunable(this) {
            final /* synthetic */ BypassHeadsUpNotifier this$0;

            {
                this.this$0 = r1;
            }

            public final void onTuningChanged(String str, String str2) {
                BypassHeadsUpNotifier bypassHeadsUpNotifier = this.this$0;
                boolean z = false;
                if (Secure.getIntForUser(bypassHeadsUpNotifier.context.getContentResolver(), "show_media_when_bypassing", 0, KeyguardUpdateMonitor.getCurrentUser()) != 0) {
                    z = true;
                }
                bypassHeadsUpNotifier.enabled = z;
            }
        }, "show_media_when_bypassing");
    }

    public final void setFullyAwake(boolean z) {
        this.fullyAwake = z;
        if (z) {
            updateAutoHeadsUp(this.currentMediaEntry);
        }
    }

    public final void setUp() {
        this.mediaManager.addCallback(this);
    }

    public void onMetadataOrStateChanged(MediaMetadata mediaMetadata, int i) {
        NotificationEntry notificationEntry = this.currentMediaEntry;
        NotificationEntry activeNotificationUnfiltered = this.entryManager.getActiveNotificationUnfiltered(this.mediaManager.getMediaNotificationKey());
        if (!NotificationMediaManager.isPlayingState(i)) {
            activeNotificationUnfiltered = null;
        }
        this.currentMediaEntry = activeNotificationUnfiltered;
        updateAutoHeadsUp(notificationEntry);
        updateAutoHeadsUp(this.currentMediaEntry);
    }

    private final void updateAutoHeadsUp(NotificationEntry notificationEntry) {
        if (notificationEntry != null) {
            boolean z = Intrinsics.areEqual((Object) notificationEntry, (Object) this.currentMediaEntry) && canAutoHeadsUp(notificationEntry);
            notificationEntry.setAutoHeadsUp(z);
            if (z) {
                this.headsUpManager.showNotification(notificationEntry);
            }
        }
    }

    private final boolean canAutoHeadsUp(NotificationEntry notificationEntry) {
        if (isAutoHeadsUpAllowed() && !notificationEntry.isSensitive() && this.notificationLockscreenUserManager.shouldShowOnKeyguard(notificationEntry) && this.entryManager.getActiveNotificationUnfiltered(notificationEntry.getKey()) == null) {
            return true;
        }
        return false;
    }

    public void onStatePostChange() {
        updateAutoHeadsUp(this.currentMediaEntry);
    }

    private final boolean isAutoHeadsUpAllowed() {
        if (this.enabled && this.bypassController.getBypassEnabled() && this.statusBarStateController.getState() == 1 && this.fullyAwake) {
            return true;
        }
        return false;
    }
}
