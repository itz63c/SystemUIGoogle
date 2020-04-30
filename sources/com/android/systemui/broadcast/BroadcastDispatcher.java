package com.android.systemui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: BroadcastDispatcher.kt */
public class BroadcastDispatcher implements Dumpable {
    private final Looper bgLooper;
    /* access modifiers changed from: private */
    public final Context context;
    private final BroadcastDispatcher$handler$1 handler;
    private final Handler mainHandler;
    /* access modifiers changed from: private */
    public final SparseArray<UserBroadcastDispatcher> receiversByUser = new SparseArray<>(20);

    public void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        registerReceiver$default(this, broadcastReceiver, intentFilter, null, null, 12, null);
    }

    public void registerReceiverWithHandler(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, Handler handler2) {
        registerReceiverWithHandler$default(this, broadcastReceiver, intentFilter, handler2, null, 8, null);
    }

    public BroadcastDispatcher(Context context2, Handler handler2, Looper looper, DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Intrinsics.checkParameterIsNotNull(handler2, "mainHandler");
        Intrinsics.checkParameterIsNotNull(looper, "bgLooper");
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        this.context = context2;
        this.mainHandler = handler2;
        this.bgLooper = looper;
        String name = BroadcastDispatcher.class.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, "javaClass.name");
        dumpManager.registerDumpable(name, this);
        this.handler = new BroadcastDispatcher$handler$1(this, this.bgLooper);
    }

    public static /* synthetic */ void registerReceiverWithHandler$default(BroadcastDispatcher broadcastDispatcher, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, Handler handler2, UserHandle userHandle, int i, Object obj) {
        if (obj == null) {
            if ((i & 8) != 0) {
                userHandle = broadcastDispatcher.context.getUser();
                Intrinsics.checkExpressionValueIsNotNull(userHandle, "context.user");
            }
            broadcastDispatcher.registerReceiverWithHandler(broadcastReceiver, intentFilter, handler2, userHandle);
            return;
        }
        throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: registerReceiverWithHandler");
    }

    public void registerReceiverWithHandler(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, Handler handler2, UserHandle userHandle) {
        Intrinsics.checkParameterIsNotNull(broadcastReceiver, "receiver");
        Intrinsics.checkParameterIsNotNull(intentFilter, "filter");
        Intrinsics.checkParameterIsNotNull(handler2, "handler");
        Intrinsics.checkParameterIsNotNull(userHandle, "user");
        registerReceiver(broadcastReceiver, intentFilter, new HandlerExecutor(handler2), userHandle);
    }

    public static /* synthetic */ void registerReceiver$default(BroadcastDispatcher broadcastDispatcher, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, Executor executor, UserHandle userHandle, int i, Object obj) {
        if (obj == null) {
            if ((i & 4) != 0) {
                executor = broadcastDispatcher.context.getMainExecutor();
            }
            if ((i & 8) != 0) {
                userHandle = broadcastDispatcher.context.getUser();
                Intrinsics.checkExpressionValueIsNotNull(userHandle, "context.user");
            }
            broadcastDispatcher.registerReceiver(broadcastReceiver, intentFilter, executor, userHandle);
            return;
        }
        throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: registerReceiver");
    }

    public void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, Executor executor, UserHandle userHandle) {
        Intrinsics.checkParameterIsNotNull(broadcastReceiver, "receiver");
        Intrinsics.checkParameterIsNotNull(intentFilter, "filter");
        Intrinsics.checkParameterIsNotNull(userHandle, "user");
        checkFilter(intentFilter);
        BroadcastDispatcher$handler$1 broadcastDispatcher$handler$1 = this.handler;
        if (executor == null) {
            executor = this.context.getMainExecutor();
            Intrinsics.checkExpressionValueIsNotNull(executor, "context.mainExecutor");
        }
        broadcastDispatcher$handler$1.obtainMessage(0, new ReceiverData(broadcastReceiver, intentFilter, executor, userHandle)).sendToTarget();
    }

    private final void checkFilter(IntentFilter intentFilter) {
        StringBuilder sb = new StringBuilder();
        if (intentFilter.countActions() == 0) {
            sb.append("Filter must contain at least one action. ");
        }
        if (intentFilter.countDataAuthorities() != 0) {
            sb.append("Filter cannot contain DataAuthorities. ");
        }
        if (intentFilter.countDataPaths() != 0) {
            sb.append("Filter cannot contain DataPaths. ");
        }
        if (intentFilter.countDataSchemes() != 0) {
            sb.append("Filter cannot contain DataSchemes. ");
        }
        if (intentFilter.countDataTypes() != 0) {
            sb.append("Filter cannot contain DataTypes. ");
        }
        if (intentFilter.getPriority() != 0) {
            sb.append("Filter cannot modify priority. ");
        }
        if (!TextUtils.isEmpty(sb)) {
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        Intrinsics.checkParameterIsNotNull(broadcastReceiver, "receiver");
        this.handler.obtainMessage(1, broadcastReceiver).sendToTarget();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public UserBroadcastDispatcher createUBRForUser(int i) {
        return new UserBroadcastDispatcher(this.context, i, this.mainHandler, this.bgLooper);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(strArr, "args");
        printWriter.println("Broadcast dispatcher:");
        int size = this.receiversByUser.size();
        for (int i = 0; i < size; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append("  User ");
            sb.append(this.receiversByUser.keyAt(i));
            printWriter.println(sb.toString());
            ((UserBroadcastDispatcher) this.receiversByUser.valueAt(i)).dump(fileDescriptor, printWriter, strArr);
        }
    }
}
