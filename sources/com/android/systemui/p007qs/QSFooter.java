package com.android.systemui.p007qs;

import android.view.View.OnClickListener;

/* renamed from: com.android.systemui.qs.QSFooter */
public interface QSFooter {
    void disable(int i, int i2, boolean z) {
    }

    int getHeight();

    void setExpandClickListener(OnClickListener onClickListener);

    void setExpanded(boolean z);

    void setExpansion(float f);

    void setKeyguardShowing(boolean z);

    void setListening(boolean z);

    void setQSPanel(QSPanel qSPanel);

    void setVisibility(int i);
}
