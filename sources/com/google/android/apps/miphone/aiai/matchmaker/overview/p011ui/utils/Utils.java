package com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui.utils;

import android.support.annotation.Nullable;
import java.util.Random;

/* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.utils.Utils */
public final class Utils {
    static {
        new Random();
    }

    public static <T> T checkNotNull(@Nullable T t) {
        if (t != null) {
            return t;
        }
        throw null;
    }
}
