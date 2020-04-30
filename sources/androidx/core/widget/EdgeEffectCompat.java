package androidx.core.widget;

import android.os.Build.VERSION;
import android.widget.EdgeEffect;

public final class EdgeEffectCompat {
    public static void onPull(EdgeEffect edgeEffect, float f, float f2) {
        if (VERSION.SDK_INT >= 21) {
            edgeEffect.onPull(f, f2);
        } else {
            edgeEffect.onPull(f);
        }
    }
}
