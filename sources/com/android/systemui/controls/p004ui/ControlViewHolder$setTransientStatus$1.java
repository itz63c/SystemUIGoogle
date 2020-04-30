package com.android.systemui.controls.p004ui;

/* renamed from: com.android.systemui.controls.ui.ControlViewHolder$setTransientStatus$1 */
/* compiled from: ControlViewHolder.kt */
final class ControlViewHolder$setTransientStatus$1 implements Runnable {
    final /* synthetic */ CharSequence $previousText;
    final /* synthetic */ CharSequence $previousTextExtra;
    final /* synthetic */ ControlViewHolder this$0;

    ControlViewHolder$setTransientStatus$1(ControlViewHolder controlViewHolder, CharSequence charSequence, CharSequence charSequence2) {
        this.this$0 = controlViewHolder;
        this.$previousText = charSequence;
        this.$previousTextExtra = charSequence2;
    }

    public final void run() {
        this.this$0.getStatus().setText(this.$previousText);
        this.this$0.getStatusExtra().setText(this.$previousTextExtra);
    }
}
