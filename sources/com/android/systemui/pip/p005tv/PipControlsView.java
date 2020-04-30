package com.android.systemui.pip.p005tv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;

/* renamed from: com.android.systemui.pip.tv.PipControlsView */
public class PipControlsView extends LinearLayout {
    public PipControlsView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(C2013R$layout.tv_pip_controls, this);
        setOrientation(0);
        setGravity(49);
    }

    /* access modifiers changed from: 0000 */
    public PipControlButtonView getFullButtonView() {
        return (PipControlButtonView) findViewById(C2011R$id.full_button);
    }

    /* access modifiers changed from: 0000 */
    public PipControlButtonView getCloseButtonView() {
        return (PipControlButtonView) findViewById(C2011R$id.close_button);
    }

    /* access modifiers changed from: 0000 */
    public PipControlButtonView getPlayPauseButtonView() {
        return (PipControlButtonView) findViewById(C2011R$id.play_pause_button);
    }
}
