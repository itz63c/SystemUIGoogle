package com.google.android.systemui.face;

import android.content.Context;
import android.provider.Settings.Secure;

class FaceNotificationSettings {
    static boolean isReenrollRequired(Context context) {
        return Secure.getIntForUser(context.getContentResolver(), "face_unlock_re_enroll", 0, -2) == 3;
    }

    static void updateReenrollSetting(Context context, int i) {
        Secure.putIntForUser(context.getContentResolver(), "face_unlock_re_enroll", i, -2);
    }
}
