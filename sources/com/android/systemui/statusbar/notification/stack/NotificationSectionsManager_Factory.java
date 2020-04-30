package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import com.android.systemui.statusbar.notification.people.PeopleHubViewAdapter;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;

public final class NotificationSectionsManager_Factory implements Factory<NotificationSectionsManager> {
    public static NotificationSectionsManager newNotificationSectionsManager(ActivityStarter activityStarter, StatusBarStateController statusBarStateController, ConfigurationController configurationController, PeopleHubViewAdapter peopleHubViewAdapter, NotificationSectionsFeatureManager notificationSectionsFeatureManager) {
        NotificationSectionsManager notificationSectionsManager = new NotificationSectionsManager(activityStarter, statusBarStateController, configurationController, peopleHubViewAdapter, notificationSectionsFeatureManager);
        return notificationSectionsManager;
    }
}
