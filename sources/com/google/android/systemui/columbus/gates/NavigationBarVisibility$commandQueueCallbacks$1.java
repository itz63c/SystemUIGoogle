package com.google.android.systemui.columbus.gates;

import com.android.systemui.statusbar.CommandQueue.Callbacks;

/* compiled from: NavigationBarVisibility.kt */
public final class NavigationBarVisibility$commandQueueCallbacks$1 implements Callbacks {
    final /* synthetic */ NavigationBarVisibility this$0;

    NavigationBarVisibility$commandQueueCallbacks$1(NavigationBarVisibility navigationBarVisibility) {
        this.this$0 = navigationBarVisibility;
    }

    public void setWindowState(int i, int i2, int i3) {
        if (this.this$0.displayId == i && i2 == 2) {
            boolean z = i3 != 0;
            if (z != this.this$0.isNavigationHidden) {
                this.this$0.isNavigationHidden = z;
                this.this$0.notifyListener();
            }
        }
    }
}
