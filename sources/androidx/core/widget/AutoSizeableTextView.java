package androidx.core.widget;

import android.os.Build.VERSION;

public interface AutoSizeableTextView {
    public static final boolean PLATFORM_SUPPORTS_AUTOSIZE = (VERSION.SDK_INT >= 27);
}
