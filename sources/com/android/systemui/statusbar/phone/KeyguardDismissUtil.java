package com.android.systemui.statusbar.phone;

import android.util.Log;
import com.android.systemui.plugins.ActivityStarter.OnDismissAction;

public class KeyguardDismissUtil implements KeyguardDismissHandler {
    private volatile KeyguardDismissHandler mDismissHandler;

    public void setDismissHandler(KeyguardDismissHandler keyguardDismissHandler) {
        this.mDismissHandler = keyguardDismissHandler;
    }

    public void executeWhenUnlocked(OnDismissAction onDismissAction, boolean z) {
        KeyguardDismissHandler keyguardDismissHandler = this.mDismissHandler;
        if (keyguardDismissHandler == null) {
            Log.wtf("KeyguardDismissUtil", "KeyguardDismissHandler not set.");
            onDismissAction.onDismiss();
            return;
        }
        keyguardDismissHandler.executeWhenUnlocked(onDismissAction, z);
    }
}
