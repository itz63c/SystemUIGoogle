package com.android.systemui.p007qs.customize;

import android.content.Context;
import android.widget.TextView;
import com.android.systemui.p007qs.tileimpl.QSTileView;
import com.android.systemui.plugins.p006qs.QSIconView;
import com.android.systemui.plugins.p006qs.QSTile.State;

/* renamed from: com.android.systemui.qs.customize.CustomizeTileView */
public class CustomizeTileView extends QSTileView {
    private boolean mShowAppLabel;

    /* access modifiers changed from: protected */
    public boolean animationsEnabled() {
        return false;
    }

    public boolean isLongClickable() {
        return false;
    }

    public CustomizeTileView(Context context, QSIconView qSIconView) {
        super(context, qSIconView);
    }

    public void setShowAppLabel(boolean z) {
        this.mShowAppLabel = z;
        this.mSecondLine.setVisibility(z ? 0 : 8);
        this.mLabel.setSingleLine(z);
    }

    /* access modifiers changed from: protected */
    public void handleStateChanged(State state) {
        super.handleStateChanged(state);
        this.mSecondLine.setVisibility(this.mShowAppLabel ? 0 : 8);
    }

    public TextView getAppLabel() {
        return this.mSecondLine;
    }
}
