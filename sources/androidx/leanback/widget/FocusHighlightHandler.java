package androidx.leanback.widget;

import android.view.View;

interface FocusHighlightHandler {
    void onInitializeView(View view);

    void onItemFocused(View view, boolean z);
}
