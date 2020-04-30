package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import com.android.systemui.C2005R$array;
import com.android.systemui.C2012R$integer;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;

public class SystemKeyPress extends TransientGate {
    /* access modifiers changed from: private */
    public final int[] mBlockingKeys;
    private final CommandQueue mCommandQueue;
    private final Callbacks mCommandQueueCallbacks = new Callbacks() {
        public void handleSystemKey(int i) {
            for (int i2 : SystemKeyPress.this.mBlockingKeys) {
                if (i2 == i) {
                    SystemKeyPress.this.block();
                    return;
                }
            }
        }
    };

    public SystemKeyPress(Context context) {
        super(context, (long) context.getResources().getInteger(C2012R$integer.elmyra_system_key_gate_duration));
        this.mBlockingKeys = context.getResources().getIntArray(C2005R$array.elmyra_blocking_system_keys);
        this.mCommandQueue = (CommandQueue) Dependency.get(CommandQueue.class);
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.mCommandQueue.addCallback(this.mCommandQueueCallbacks);
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.mCommandQueue.removeCallback(this.mCommandQueueCallbacks);
    }
}
