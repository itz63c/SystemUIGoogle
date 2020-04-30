package com.android.systemui.statusbar.notification.row;

import android.app.NotificationChannel;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/* compiled from: ChannelEditorListView.kt */
final class ChannelRow$onFinishInflate$1 implements OnCheckedChangeListener {
    final /* synthetic */ ChannelRow this$0;

    ChannelRow$onFinishInflate$1(ChannelRow channelRow) {
        this.this$0 = channelRow;
    }

    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        NotificationChannel channel = this.this$0.getChannel();
        if (channel != null) {
            this.this$0.getController().proposeEditForChannel(channel, z ? channel.getImportance() : 0);
        }
    }
}
