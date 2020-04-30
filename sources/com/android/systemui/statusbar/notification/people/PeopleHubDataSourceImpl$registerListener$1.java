package com.android.systemui.statusbar.notification.people;

import android.content.pm.UserInfo;
import android.util.SparseArray;
import com.android.systemui.statusbar.NotificationLockscreenUserManager.UserChangedListener;

/* compiled from: PeopleHubNotificationListener.kt */
public final class PeopleHubDataSourceImpl$registerListener$1 implements UserChangedListener {
    final /* synthetic */ PeopleHubDataSourceImpl this$0;

    PeopleHubDataSourceImpl$registerListener$1(PeopleHubDataSourceImpl peopleHubDataSourceImpl) {
        this.this$0 = peopleHubDataSourceImpl;
    }

    public void onUserChanged(int i) {
        this.this$0.updateUi();
    }

    public void onCurrentProfilesChanged(SparseArray<UserInfo> sparseArray) {
        this.this$0.updateUi();
    }
}
