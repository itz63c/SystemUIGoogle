package com.android.systemui.dock;

import com.android.systemui.dock.DockManager.AlignmentStateListener;
import com.android.systemui.dock.DockManager.DockEventListener;

public class DockManagerImpl implements DockManager {
    public void addAlignmentStateListener(AlignmentStateListener alignmentStateListener) {
    }

    public void addListener(DockEventListener dockEventListener) {
    }

    public boolean isDocked() {
        return false;
    }

    public boolean isHidden() {
        return false;
    }

    public void removeListener(DockEventListener dockEventListener) {
    }
}
