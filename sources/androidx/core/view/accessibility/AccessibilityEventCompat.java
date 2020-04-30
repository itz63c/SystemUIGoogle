package androidx.core.view.accessibility;

import android.os.Build.VERSION;
import android.view.accessibility.AccessibilityEvent;

public final class AccessibilityEventCompat {
    public static void setContentChangeTypes(AccessibilityEvent accessibilityEvent, int i) {
        if (VERSION.SDK_INT >= 19) {
            accessibilityEvent.setContentChangeTypes(i);
        }
    }

    public static int getContentChangeTypes(AccessibilityEvent accessibilityEvent) {
        if (VERSION.SDK_INT >= 19) {
            return accessibilityEvent.getContentChangeTypes();
        }
        return 0;
    }
}
