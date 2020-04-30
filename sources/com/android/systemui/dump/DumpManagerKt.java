package com.android.systemui.dump;

import kotlin.jvm.internal.SpreadBuilder;

/* compiled from: DumpManager.kt */
public final class DumpManagerKt {
    /* access modifiers changed from: private */
    public static final String[] COMMANDS = {"bugreport-critical", "bugreport-normal", "buffers", "dumpables"};
    /* access modifiers changed from: private */
    public static final String[] PRIORITY_OPTIONS = {"CRITICAL", "HIGH", "NORMAL"};
    /* access modifiers changed from: private */
    public static final String[] RESERVED_NAMES;

    static {
        SpreadBuilder spreadBuilder = new SpreadBuilder(2);
        spreadBuilder.add("config");
        spreadBuilder.addSpread(COMMANDS);
        RESERVED_NAMES = (String[]) spreadBuilder.toArray(new String[spreadBuilder.size()]);
    }
}
