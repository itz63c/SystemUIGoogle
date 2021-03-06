package com.android.systemui.controls.controller;

import android.os.IBinder;
import android.service.controls.Control;
import android.service.controls.IControlsSubscriber.Stub;
import android.service.controls.IControlsSubscription;
import com.android.systemui.util.concurrency.DelayableExecutor;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: StatefulControlSubscriber.kt */
public final class StatefulControlSubscriber extends Stub {
    private final DelayableExecutor bgExecutor;
    /* access modifiers changed from: private */
    public final ControlsController controller;
    /* access modifiers changed from: private */
    public final ControlsProviderLifecycleManager provider;
    /* access modifiers changed from: private */
    public final long requestLimit;
    /* access modifiers changed from: private */
    public IControlsSubscription subscription;
    /* access modifiers changed from: private */
    public boolean subscriptionOpen;

    public StatefulControlSubscriber(ControlsController controlsController, ControlsProviderLifecycleManager controlsProviderLifecycleManager, DelayableExecutor delayableExecutor, long j) {
        Intrinsics.checkParameterIsNotNull(controlsController, "controller");
        Intrinsics.checkParameterIsNotNull(controlsProviderLifecycleManager, "provider");
        Intrinsics.checkParameterIsNotNull(delayableExecutor, "bgExecutor");
        this.controller = controlsController;
        this.provider = controlsProviderLifecycleManager;
        this.bgExecutor = delayableExecutor;
        this.requestLimit = j;
    }

    private final void run(IBinder iBinder, Function0<Unit> function0) {
        if (Intrinsics.areEqual((Object) this.provider.getToken(), (Object) iBinder)) {
            this.bgExecutor.execute(new StatefulControlSubscriber$run$1(function0));
        }
    }

    public void onSubscribe(IBinder iBinder, IControlsSubscription iControlsSubscription) {
        Intrinsics.checkParameterIsNotNull(iBinder, "token");
        Intrinsics.checkParameterIsNotNull(iControlsSubscription, "subs");
        run(iBinder, new StatefulControlSubscriber$onSubscribe$1(this, iControlsSubscription));
    }

    public void onNext(IBinder iBinder, Control control) {
        Intrinsics.checkParameterIsNotNull(iBinder, "token");
        Intrinsics.checkParameterIsNotNull(control, "control");
        run(iBinder, new StatefulControlSubscriber$onNext$1(this, iBinder, control));
    }

    public void onError(IBinder iBinder, String str) {
        Intrinsics.checkParameterIsNotNull(iBinder, "token");
        Intrinsics.checkParameterIsNotNull(str, "error");
        run(iBinder, new StatefulControlSubscriber$onError$1(this, str));
    }

    public void onComplete(IBinder iBinder) {
        Intrinsics.checkParameterIsNotNull(iBinder, "token");
        run(iBinder, new StatefulControlSubscriber$onComplete$1(this));
    }

    public final void cancel() {
        if (this.subscriptionOpen) {
            this.bgExecutor.execute(new StatefulControlSubscriber$cancel$1(this));
        }
    }
}
