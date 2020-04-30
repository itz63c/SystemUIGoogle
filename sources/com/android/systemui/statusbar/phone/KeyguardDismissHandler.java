package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.ActivityStarter.OnDismissAction;

public interface KeyguardDismissHandler {
    void executeWhenUnlocked(OnDismissAction onDismissAction, boolean z);
}
