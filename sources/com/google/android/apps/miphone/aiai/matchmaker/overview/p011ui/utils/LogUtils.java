package com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui.utils;

import android.support.annotation.Nullable;
import android.util.Log;

/* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.utils.LogUtils */
public final class LogUtils {
    private static boolean loggingEnabled = false;

    /* renamed from: d */
    public static void m117d(String str) {
        if (loggingEnabled) {
            Log.d("AiAiSuggestUi", str);
        }
    }

    /* renamed from: e */
    public static void m118e(String str, @Nullable Throwable th) {
        Log.e("AiAiSuggestUi", str, th);
    }
}
