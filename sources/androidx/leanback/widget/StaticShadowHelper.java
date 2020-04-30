package androidx.leanback.widget;

import android.os.Build.VERSION;
import android.view.View;

final class StaticShadowHelper {

    static class ShadowImpl {
        View mFocusShadow;
        View mNormalShadow;

        ShadowImpl() {
        }
    }

    static boolean supportsShadow() {
        return VERSION.SDK_INT >= 21;
    }

    static void setShadowFocusLevel(Object obj, float f) {
        if (VERSION.SDK_INT >= 21) {
            ShadowImpl shadowImpl = (ShadowImpl) obj;
            shadowImpl.mNormalShadow.setAlpha(1.0f - f);
            shadowImpl.mFocusShadow.setAlpha(f);
        }
    }
}
