package com.android.systemui.statusbar.notification.row;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/* compiled from: ChannelEditorListView.kt */
final class ChannelEditorListView$updateAppControlRow$1 implements OnCheckedChangeListener {
    final /* synthetic */ ChannelEditorListView this$0;

    ChannelEditorListView$updateAppControlRow$1(ChannelEditorListView channelEditorListView) {
        this.this$0 = channelEditorListView;
    }

    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        this.this$0.getController().setAppNotificationsEnabled(z);
        this.this$0.updateRows();
    }
}
