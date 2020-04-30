package com.android.settingslib.applications;

import android.content.pm.ApplicationInfo;
import android.os.SystemProperties;
import com.android.settingslib.applications.instantapps.InstantAppDataProvider;

public class AppUtils {
    private static InstantAppDataProvider sInstantAppDataProvider;

    public static boolean isInstant(ApplicationInfo applicationInfo) {
        InstantAppDataProvider instantAppDataProvider = sInstantAppDataProvider;
        if (instantAppDataProvider != null) {
            if (instantAppDataProvider.isInstantApp(applicationInfo)) {
                return true;
            }
        } else if (applicationInfo.isInstantApp()) {
            return true;
        }
        String str = SystemProperties.get("settingsdebug.instant.packages");
        if (!(str == null || str.isEmpty() || applicationInfo.packageName == null)) {
            String[] split = str.split(",");
            if (split != null) {
                for (String contains : split) {
                    if (applicationInfo.packageName.contains(contains)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
