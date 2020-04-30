package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.systemui.C2011R$id;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ForegroundServiceDungeonView.kt */
public final class ForegroundServiceDungeonView extends StackScrollerDecorView {
    /* access modifiers changed from: protected */
    public View findSecondaryView() {
        return null;
    }

    public void setVisible(boolean z, boolean z2) {
    }

    public ForegroundServiceDungeonView(Context context, AttributeSet attributeSet) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(attributeSet, "attrs");
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public View findContentView() {
        return findViewById(C2011R$id.foreground_service_dungeon);
    }
}
