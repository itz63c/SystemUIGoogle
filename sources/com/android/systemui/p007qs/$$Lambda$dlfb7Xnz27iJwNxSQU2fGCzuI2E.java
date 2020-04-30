package com.android.systemui.p007qs;

import com.android.systemui.statusbar.phone.StatusBar;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.qs.-$$Lambda$dlfb7Xnz27iJwNxSQU2fGCzuI2E reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$dlfb7Xnz27iJwNxSQU2fGCzuI2E implements Consumer {
    public static final /* synthetic */ $$Lambda$dlfb7Xnz27iJwNxSQU2fGCzuI2E INSTANCE = new $$Lambda$dlfb7Xnz27iJwNxSQU2fGCzuI2E();

    private /* synthetic */ $$Lambda$dlfb7Xnz27iJwNxSQU2fGCzuI2E() {
    }

    public final void accept(Object obj) {
        ((StatusBar) obj).postAnimateOpenPanels();
    }
}
