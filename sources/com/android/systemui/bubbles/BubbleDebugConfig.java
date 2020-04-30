package com.android.systemui.bubbles;

import android.content.Context;
import android.provider.Settings.Secure;

public class BubbleDebugConfig {
    static boolean forceShowUserEducation(Context context) {
        return Secure.getInt(context.getContentResolver(), "force_show_bubbles_user_education", 0) != 0;
    }
}
