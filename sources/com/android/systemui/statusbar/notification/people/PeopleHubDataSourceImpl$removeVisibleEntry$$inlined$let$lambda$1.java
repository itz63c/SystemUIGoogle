package com.android.systemui.statusbar.notification.people;

import android.content.pm.UserInfo;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

/* compiled from: PeopleHubNotificationListener.kt */
final class PeopleHubDataSourceImpl$removeVisibleEntry$$inlined$let$lambda$1 implements Runnable {
    final /* synthetic */ String $key;
    final /* synthetic */ int $reason$inlined;
    final /* synthetic */ int $userId;
    final /* synthetic */ PeopleHubDataSourceImpl this$0;

    PeopleHubDataSourceImpl$removeVisibleEntry$$inlined$let$lambda$1(int i, String str, PeopleHubDataSourceImpl peopleHubDataSourceImpl, NotificationEntry notificationEntry, int i2) {
        this.$userId = i;
        this.$key = str;
        this.this$0 = peopleHubDataSourceImpl;
        this.$reason$inlined = i2;
    }

    public final void run() {
        UserInfo profileParent = this.this$0.userManager.getProfileParent(this.$userId);
        final int i = profileParent != null ? profileParent.id : this.$userId;
        this.this$0.mainExecutor.execute(new Runnable(this) {
            final /* synthetic */ PeopleHubDataSourceImpl$removeVisibleEntry$$inlined$let$lambda$1 this$0;

            {
                this.this$0 = r1;
            }

            public final void run() {
                PeopleHubDataSourceImpl$removeVisibleEntry$$inlined$let$lambda$1 peopleHubDataSourceImpl$removeVisibleEntry$$inlined$let$lambda$1 = this.this$0;
                if (peopleHubDataSourceImpl$removeVisibleEntry$$inlined$let$lambda$1.$reason$inlined == 18) {
                    PeopleHubManager peopleHubManager = (PeopleHubManager) peopleHubDataSourceImpl$removeVisibleEntry$$inlined$let$lambda$1.this$0.peopleHubManagerForUser.get(i);
                    if (peopleHubManager != null && peopleHubManager.migrateActivePerson(this.this$0.$key)) {
                        this.this$0.this$0.updateUi();
                        return;
                    }
                    return;
                }
                PeopleHubManager peopleHubManager2 = (PeopleHubManager) peopleHubDataSourceImpl$removeVisibleEntry$$inlined$let$lambda$1.this$0.peopleHubManagerForUser.get(i);
                if (peopleHubManager2 != null) {
                    peopleHubManager2.removeActivePerson(this.this$0.$key);
                }
            }
        });
    }
}
