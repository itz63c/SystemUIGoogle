package com.android.systemui.statusbar.notification.row;

import android.view.View;
import android.view.View.OnClickListener;

/* compiled from: ChannelEditorListView.kt */
final class AppControlView$onFinishInflate$1 implements OnClickListener {
    final /* synthetic */ AppControlView this$0;

    AppControlView$onFinishInflate$1(AppControlView appControlView) {
        this.this$0 = appControlView;
    }

    public final void onClick(View view) {
        this.this$0.getSwitch().toggle();
    }
}
