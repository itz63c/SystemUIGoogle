package com.google.android.systemui.columbus.gates;

import com.android.systemui.statusbar.CommandQueue.Callbacks;

/* compiled from: SystemKeyPress.kt */
public final class SystemKeyPress$commandQueueCallbacks$1 implements Callbacks {
    final /* synthetic */ SystemKeyPress this$0;

    SystemKeyPress$commandQueueCallbacks$1(SystemKeyPress systemKeyPress) {
        this.this$0 = systemKeyPress;
    }

    public void handleSystemKey(int i) {
        if (this.this$0.blockingKeys.contains(Integer.valueOf(i))) {
            SystemKeyPress systemKeyPress = this.this$0;
            systemKeyPress.blockForMillis(systemKeyPress.gateDuration);
        }
    }
}
