package com.google.android.systemui.elmyra.actions;

import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.function.Consumer;

/* renamed from: com.google.android.systemui.elmyra.actions.-$$Lambda$UnpinNotifications$2AIQLXUnga6EFVuQA5J5PzBLz_w reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$UnpinNotifications$2AIQLXUnga6EFVuQA5J5PzBLz_w implements Consumer {
    public static final /* synthetic */ $$Lambda$UnpinNotifications$2AIQLXUnga6EFVuQA5J5PzBLz_w INSTANCE = new $$Lambda$UnpinNotifications$2AIQLXUnga6EFVuQA5J5PzBLz_w();

    private /* synthetic */ $$Lambda$UnpinNotifications$2AIQLXUnga6EFVuQA5J5PzBLz_w() {
    }

    public final void accept(Object obj) {
        ((HeadsUpManager) obj).unpinAll(true);
    }
}
