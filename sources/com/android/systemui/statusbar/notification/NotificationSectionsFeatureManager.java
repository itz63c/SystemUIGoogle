package com.android.systemui.statusbar.notification;

import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.util.DeviceConfigProxy;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotificationSectionsFeatureManager.kt */
public final class NotificationSectionsFeatureManager {
    private final Context context;
    private final DeviceConfigProxy proxy;

    public NotificationSectionsFeatureManager(DeviceConfigProxy deviceConfigProxy, Context context2) {
        Intrinsics.checkParameterIsNotNull(deviceConfigProxy, "proxy");
        Intrinsics.checkParameterIsNotNull(context2, "context");
        this.proxy = deviceConfigProxy;
        this.context = context2;
    }

    public final boolean isFilteringEnabled() {
        return NotificationSectionsFeatureManagerKt.usePeopleFiltering(this.proxy);
    }

    public final int[] getNotificationBuckets() {
        if (isFilteringEnabled()) {
            return new int[]{0, 1, 2, 3};
        }
        if (NotificationUtils.useNewInterruptionModel(this.context)) {
            return new int[]{2, 3};
        }
        return new int[]{2};
    }

    public final int getNumberOfBuckets() {
        return getNotificationBuckets().length;
    }

    @VisibleForTesting
    public final void clearCache() {
        NotificationSectionsFeatureManagerKt.sUsePeopleFiltering = null;
    }
}
