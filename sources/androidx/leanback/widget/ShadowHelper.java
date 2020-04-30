package androidx.leanback.widget;

import android.os.Build.VERSION;

final class ShadowHelper {
    static boolean supportsDynamicShadow() {
        return VERSION.SDK_INT >= 21;
    }

    static void setShadowFocusLevel(Object obj, float f) {
        if (VERSION.SDK_INT >= 21) {
            ShadowHelperApi21.setShadowFocusLevel(obj, f);
        }
    }
}
