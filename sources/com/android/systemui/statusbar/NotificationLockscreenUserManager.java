package com.android.systemui.statusbar;

import android.content.pm.UserInfo;
import android.util.SparseArray;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotificationLockscreenUserManager {

    public interface UserChangedListener {
        void onCurrentProfilesChanged(SparseArray<UserInfo> sparseArray) {
        }

        void onUserChanged(int i) {
        }
    }

    void addUserChangedListener(UserChangedListener userChangedListener);

    SparseArray<UserInfo> getCurrentProfiles();

    int getCurrentUserId();

    boolean isAnyProfilePublicMode();

    boolean isCurrentProfile(int i);

    boolean isLockscreenPublicMode(int i);

    boolean needsRedaction(NotificationEntry notificationEntry);

    boolean needsSeparateWorkChallenge(int i) {
        return false;
    }

    void removeUserChangedListener(UserChangedListener userChangedListener);

    void setUpWithPresenter(NotificationPresenter notificationPresenter);

    boolean shouldAllowLockscreenRemoteInput();

    boolean shouldHideNotifications(int i);

    boolean shouldHideNotifications(String str);

    boolean shouldShowLockscreenNotifications();

    boolean shouldShowOnKeyguard(NotificationEntry notificationEntry);

    void updatePublicMode();

    boolean userAllowsNotificationsInPublic(int i);

    boolean userAllowsPrivateNotificationsInPublic(int i);
}
