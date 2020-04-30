package com.android.systemui.statusbar;

import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;

public interface StatusIconDisplayable extends DarkReceiver {
    String getSlot();

    int getVisibleState();

    boolean isIconBlocked() {
        return false;
    }

    boolean isIconVisible();

    void setDecorColor(int i);

    void setStaticDrawableColor(int i);

    void setVisibleState(int i, boolean z);

    void setVisibleState(int i) {
        setVisibleState(i, false);
    }
}
