package com.android.systemui.statusbar.notification.people;

import android.content.pm.UserInfo;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

/* compiled from: PeopleHubNotificationListener.kt */
final class PeopleHubDataSourceImpl$addVisibleEntry$$inlined$let$lambda$1 implements Runnable {
    final /* synthetic */ PersonModel $personModel;
    final /* synthetic */ int $userId;
    final /* synthetic */ PeopleHubDataSourceImpl this$0;

    PeopleHubDataSourceImpl$addVisibleEntry$$inlined$let$lambda$1(int i, PersonModel personModel, PeopleHubDataSourceImpl peopleHubDataSourceImpl, NotificationEntry notificationEntry) {
        this.$userId = i;
        this.$personModel = personModel;
        this.this$0 = peopleHubDataSourceImpl;
    }

    public final void run() {
        UserInfo profileParent = this.this$0.userManager.getProfileParent(this.$userId);
        final int i = profileParent != null ? profileParent.id : this.$userId;
        this.this$0.mainExecutor.execute(new Runnable(this) {
            final /* synthetic */ PeopleHubDataSourceImpl$addVisibleEntry$$inlined$let$lambda$1 this$0;

            {
                this.this$0 = r1;
            }

            public final void run() {
                PeopleHubManager peopleHubManager = (PeopleHubManager) this.this$0.this$0.peopleHubManagerForUser.get(i);
                if (peopleHubManager == null) {
                    peopleHubManager = new PeopleHubManager();
                    this.this$0.this$0.peopleHubManagerForUser.put(i, peopleHubManager);
                }
                if (peopleHubManager.addActivePerson(this.this$0.$personModel)) {
                    this.this$0.this$0.updateUi();
                }
            }
        });
    }
}
