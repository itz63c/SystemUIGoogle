package com.android.systemui.broadcast;

import android.content.BroadcastReceiver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: BroadcastDispatcher.kt */
public final class BroadcastDispatcher$handler$1 extends Handler {
    final /* synthetic */ BroadcastDispatcher this$0;

    BroadcastDispatcher$handler$1(BroadcastDispatcher broadcastDispatcher, Looper looper) {
        this.this$0 = broadcastDispatcher;
        super(looper);
    }

    public void handleMessage(Message message) {
        int i;
        Intrinsics.checkParameterIsNotNull(message, "msg");
        int i2 = message.what;
        if (i2 != 0) {
            String str = "null cannot be cast to non-null type android.content.BroadcastReceiver";
            if (i2 == 1) {
                int i3 = 0;
                int size = this.this$0.receiversByUser.size();
                while (i3 < size) {
                    UserBroadcastDispatcher userBroadcastDispatcher = (UserBroadcastDispatcher) this.this$0.receiversByUser.valueAt(i3);
                    Object obj = message.obj;
                    if (obj != null) {
                        userBroadcastDispatcher.unregisterReceiver((BroadcastReceiver) obj);
                        i3++;
                    } else {
                        throw new TypeCastException(str);
                    }
                }
            } else if (i2 != 2) {
                super.handleMessage(message);
            } else {
                UserBroadcastDispatcher userBroadcastDispatcher2 = (UserBroadcastDispatcher) this.this$0.receiversByUser.get(message.arg1);
                if (userBroadcastDispatcher2 != null) {
                    Object obj2 = message.obj;
                    if (obj2 != null) {
                        userBroadcastDispatcher2.unregisterReceiver((BroadcastReceiver) obj2);
                    } else {
                        throw new TypeCastException(str);
                    }
                }
            }
        } else {
            Object obj3 = message.obj;
            if (obj3 != null) {
                ReceiverData receiverData = (ReceiverData) obj3;
                if (receiverData.getUser().getIdentifier() == -2) {
                    i = this.this$0.context.getUserId();
                } else {
                    i = receiverData.getUser().getIdentifier();
                }
                if (i < -1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Register receiver for invalid user: ");
                    sb.append(i);
                    Log.w("BroadcastDispatcher", sb.toString());
                    return;
                }
                UserBroadcastDispatcher userBroadcastDispatcher3 = (UserBroadcastDispatcher) this.this$0.receiversByUser.get(i, this.this$0.createUBRForUser(i));
                this.this$0.receiversByUser.put(i, userBroadcastDispatcher3);
                userBroadcastDispatcher3.registerReceiver(receiverData);
            } else {
                throw new TypeCastException("null cannot be cast to non-null type com.android.systemui.broadcast.ReceiverData");
            }
        }
    }
}
