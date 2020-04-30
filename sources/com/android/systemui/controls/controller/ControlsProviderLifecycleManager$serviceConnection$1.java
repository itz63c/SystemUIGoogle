package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.controls.IControlsProvider;
import android.service.controls.IControlsProvider.Stub;
import android.util.Log;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsProviderLifecycleManager.kt */
public final class ControlsProviderLifecycleManager$serviceConnection$1 implements ServiceConnection {
    final /* synthetic */ ControlsProviderLifecycleManager this$0;

    ControlsProviderLifecycleManager$serviceConnection$1(ControlsProviderLifecycleManager controlsProviderLifecycleManager) {
        this.this$0 = controlsProviderLifecycleManager;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Intrinsics.checkParameterIsNotNull(componentName, "name");
        Intrinsics.checkParameterIsNotNull(iBinder, "service");
        String access$getTAG$p = this.this$0.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onServiceConnected ");
        sb.append(componentName);
        Log.d(access$getTAG$p, sb.toString());
        this.this$0.bindTryCount = 0;
        ControlsProviderLifecycleManager controlsProviderLifecycleManager = this.this$0;
        IControlsProvider asInterface = Stub.asInterface(iBinder);
        Intrinsics.checkExpressionValueIsNotNull(asInterface, "IControlsProvider.Stub.asInterface(service)");
        controlsProviderLifecycleManager.wrapper = new ServiceWrapper(asInterface);
        try {
            iBinder.linkToDeath(this.this$0, 0);
        } catch (RemoteException unused) {
        }
        this.this$0.handlePendingServiceMethods();
    }

    public void onServiceDisconnected(ComponentName componentName) {
        String access$getTAG$p = this.this$0.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onServiceDisconnected ");
        sb.append(componentName);
        Log.d(access$getTAG$p, sb.toString());
        this.this$0.wrapper = null;
        this.this$0.bindService(false);
    }
}
