package com.android.systemui.dock;

public interface DockManager {

    public interface AlignmentStateListener {
        void onAlignmentStateChanged(int i);
    }

    public interface DockEventListener {
        void onEvent(int i);
    }

    void addAlignmentStateListener(AlignmentStateListener alignmentStateListener);

    void addListener(DockEventListener dockEventListener);

    boolean isDocked();

    boolean isHidden();

    void removeListener(DockEventListener dockEventListener);
}
