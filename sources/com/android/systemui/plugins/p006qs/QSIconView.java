package com.android.systemui.plugins.p006qs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.p006qs.QSTile.State;

@ProvidesInterface(version = 1)
/* renamed from: com.android.systemui.plugins.qs.QSIconView */
public abstract class QSIconView extends ViewGroup {
    public static final int VERSION = 1;

    public abstract void disableAnimation();

    public abstract View getIconView();

    public abstract void setIcon(State state, boolean z);

    public QSIconView(Context context) {
        super(context);
    }
}
