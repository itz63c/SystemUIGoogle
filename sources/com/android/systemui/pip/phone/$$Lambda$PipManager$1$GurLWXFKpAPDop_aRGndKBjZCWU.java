package com.android.systemui.pip.phone;

import com.android.systemui.shared.system.WindowManagerWrapper;

/* renamed from: com.android.systemui.pip.phone.-$$Lambda$PipManager$1$GurLWXFKpAPDop_aRGndKBjZCWU reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$PipManager$1$GurLWXFKpAPDop_aRGndKBjZCWU implements Runnable {
    public static final /* synthetic */ $$Lambda$PipManager$1$GurLWXFKpAPDop_aRGndKBjZCWU INSTANCE = new $$Lambda$PipManager$1$GurLWXFKpAPDop_aRGndKBjZCWU();

    private /* synthetic */ $$Lambda$PipManager$1$GurLWXFKpAPDop_aRGndKBjZCWU() {
    }

    public final void run() {
        WindowManagerWrapper.getInstance().setPipVisibility(true);
    }
}
