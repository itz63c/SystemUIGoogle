package com.android.settingslib.development;

import android.content.Context;
import android.os.Build;
import android.os.UserManager;
import android.provider.Settings.Global;

public class DevelopmentSettingsEnabler {
    public static boolean isDevelopmentSettingsEnabled(Context context) {
        UserManager userManager = (UserManager) context.getSystemService("user");
        boolean z = Global.getInt(context.getContentResolver(), "development_settings_enabled", Build.TYPE.equals("eng") ? 1 : 0) != 0;
        boolean hasUserRestriction = userManager.hasUserRestriction("no_debugging_features");
        if (!userManager.isAdminUser() || hasUserRestriction || !z) {
            return false;
        }
        return true;
    }
}
