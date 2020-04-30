package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifPromoter;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSection;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender.OnEndLifetimeExtensionCallback;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.util.Objects;

public class HeadsUpCoordinator implements Coordinator {
    /* access modifiers changed from: private */
    public NotificationEntry mCurrentHun;
    /* access modifiers changed from: private */
    public OnEndLifetimeExtensionCallback mEndLifetimeExtension;
    /* access modifiers changed from: private */
    public final HeadsUpManager mHeadsUpManager;
    private final NotifLifetimeExtender mLifetimeExtender = new NotifLifetimeExtender() {
        public String getName() {
            return "HeadsUpCoordinator";
        }

        public void setCallback(OnEndLifetimeExtensionCallback onEndLifetimeExtensionCallback) {
            HeadsUpCoordinator.this.mEndLifetimeExtension = onEndLifetimeExtensionCallback;
        }

        public boolean shouldExtendLifetime(NotificationEntry notificationEntry, int i) {
            boolean access$300 = HeadsUpCoordinator.this.isCurrentlyShowingHun(notificationEntry);
            if (access$300) {
                HeadsUpCoordinator.this.mNotifExtendingLifetime = notificationEntry;
            }
            return access$300;
        }

        public void cancelLifetimeExtension(NotificationEntry notificationEntry) {
            if (Objects.equals(HeadsUpCoordinator.this.mNotifExtendingLifetime, notificationEntry)) {
                HeadsUpCoordinator.this.mNotifExtendingLifetime = null;
            }
        }
    };
    private final NotifCollectionListener mNotifCollectionListener = new NotifCollectionListener() {
        public void onEntryRemoved(NotificationEntry notificationEntry, int i) {
            String key = notificationEntry.getKey();
            if (HeadsUpCoordinator.this.mHeadsUpManager.isAlerting(key)) {
                HeadsUpCoordinator.this.mHeadsUpManager.removeNotification(notificationEntry.getKey(), HeadsUpCoordinator.this.mRemoteInputManager.getController().isSpinning(key) && !NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY);
            }
        }
    };
    /* access modifiers changed from: private */
    public NotificationEntry mNotifExtendingLifetime;
    /* access modifiers changed from: private */
    public final NotifPromoter mNotifPromoter;
    /* access modifiers changed from: private */
    public final NotifSection mNotifSection;
    private final OnHeadsUpChangedListener mOnHeadsUpChangedListener;
    /* access modifiers changed from: private */
    public final NotificationRemoteInputManager mRemoteInputManager;

    public HeadsUpCoordinator(HeadsUpManager headsUpManager, NotificationRemoteInputManager notificationRemoteInputManager) {
        String str = "HeadsUpCoordinator";
        this.mNotifPromoter = new NotifPromoter(str) {
            public boolean shouldPromoteToTopLevel(NotificationEntry notificationEntry) {
                return HeadsUpCoordinator.this.isCurrentlyShowingHun(notificationEntry);
            }
        };
        this.mNotifSection = new NotifSection(str) {
            public boolean isInSection(ListEntry listEntry) {
                return HeadsUpCoordinator.this.isCurrentlyShowingHun(listEntry);
            }
        };
        this.mOnHeadsUpChangedListener = new OnHeadsUpChangedListener() {
            public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
                NotificationEntry topEntry = HeadsUpCoordinator.this.mHeadsUpManager.getTopEntry();
                if (!Objects.equals(HeadsUpCoordinator.this.mCurrentHun, topEntry)) {
                    HeadsUpCoordinator.this.endNotifLifetimeExtension();
                    HeadsUpCoordinator.this.mCurrentHun = topEntry;
                    HeadsUpCoordinator.this.mNotifPromoter.invalidateList();
                    HeadsUpCoordinator.this.mNotifSection.invalidateList();
                }
            }
        };
        this.mHeadsUpManager = headsUpManager;
        this.mRemoteInputManager = notificationRemoteInputManager;
    }

    public void attach(NotifPipeline notifPipeline) {
        this.mHeadsUpManager.addListener(this.mOnHeadsUpChangedListener);
        notifPipeline.addCollectionListener(this.mNotifCollectionListener);
        notifPipeline.addPromoter(this.mNotifPromoter);
        notifPipeline.addNotificationLifetimeExtender(this.mLifetimeExtender);
    }

    public NotifSection getSection() {
        return this.mNotifSection;
    }

    /* access modifiers changed from: private */
    public boolean isCurrentlyShowingHun(ListEntry listEntry) {
        return this.mCurrentHun == listEntry.getRepresentativeEntry();
    }

    /* access modifiers changed from: private */
    public void endNotifLifetimeExtension() {
        NotificationEntry notificationEntry = this.mNotifExtendingLifetime;
        if (notificationEntry != null) {
            this.mEndLifetimeExtension.onEndLifetimeExtension(this.mLifetimeExtender, notificationEntry);
            this.mNotifExtendingLifetime = null;
        }
    }
}
