package com.android.systemui.broadcast;

/* renamed from: com.android.systemui.broadcast.UserBroadcastDispatcher$HandleBroadcastRunnable$run$$inlined$forEach$lambda$1 */
/* compiled from: UserBroadcastDispatcher.kt */
final class C0755x519e51dd implements Runnable {
    final /* synthetic */ ReceiverData $it;
    final /* synthetic */ HandleBroadcastRunnable this$0;

    C0755x519e51dd(ReceiverData receiverData, HandleBroadcastRunnable handleBroadcastRunnable) {
        this.$it = receiverData;
        this.this$0 = handleBroadcastRunnable;
    }

    public final void run() {
        this.$it.getReceiver().setPendingResult(this.this$0.getPendingResult());
        this.$it.getReceiver().onReceive(this.this$0.getContext(), this.this$0.getIntent());
    }
}
