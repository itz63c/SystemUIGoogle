package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.UserHandle;
import android.service.controls.IControlsActionCallback.Stub;
import android.service.controls.IControlsSubscriber;
import android.service.controls.IControlsSubscription;
import android.service.controls.actions.ControlAction;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsProviderLifecycleManager.kt */
public final class ControlsProviderLifecycleManager implements DeathRecipient {
    /* access modifiers changed from: private */
    public static final int BIND_FLAGS = 67108897;
    /* access modifiers changed from: private */
    public final String TAG = ControlsProviderLifecycleManager.class.getSimpleName();
    /* access modifiers changed from: private */
    public final Stub actionCallbackService;
    /* access modifiers changed from: private */
    public int bindTryCount;
    private final ComponentName componentName;
    /* access modifiers changed from: private */
    public final Context context;
    private final DelayableExecutor executor;
    /* access modifiers changed from: private */
    public final Intent intent;
    private Runnable onLoadCanceller;
    @GuardedBy({"queuedServiceMethods"})
    private final Set<ServiceMethod> queuedServiceMethods = new ArraySet();
    /* access modifiers changed from: private */
    public boolean requiresBound;
    /* access modifiers changed from: private */
    public final ControlsProviderLifecycleManager$serviceConnection$1 serviceConnection;
    @GuardedBy({"subscriptions"})
    private final List<IControlsSubscription> subscriptions = new ArrayList();
    private final IBinder token = new Binder();
    private final UserHandle user;
    /* access modifiers changed from: private */
    public ServiceWrapper wrapper;

    /* compiled from: ControlsProviderLifecycleManager.kt */
    public final class Action extends ServiceMethod {
        private final ControlAction action;

        /* renamed from: id */
        private final String f42id;
        final /* synthetic */ ControlsProviderLifecycleManager this$0;

        public Action(ControlsProviderLifecycleManager controlsProviderLifecycleManager, String str, ControlAction controlAction) {
            Intrinsics.checkParameterIsNotNull(str, "id");
            Intrinsics.checkParameterIsNotNull(controlAction, "action");
            this.this$0 = controlsProviderLifecycleManager;
            super();
            this.f42id = str;
            this.action = controlAction;
        }

        /* renamed from: callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public boolean mo10965x93b7231b() {
            String access$getTAG$p = this.this$0.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onAction ");
            sb.append(this.this$0.getComponentName());
            sb.append(" - ");
            sb.append(this.f42id);
            Log.d(access$getTAG$p, sb.toString());
            ServiceWrapper access$getWrapper$p = this.this$0.wrapper;
            if (access$getWrapper$p != null) {
                return access$getWrapper$p.action(this.f42id, this.action, this.this$0.actionCallbackService);
            }
            return false;
        }
    }

    /* compiled from: ControlsProviderLifecycleManager.kt */
    public final class Load extends ServiceMethod {
        private final IControlsSubscriber.Stub subscriber;
        final /* synthetic */ ControlsProviderLifecycleManager this$0;

        public Load(ControlsProviderLifecycleManager controlsProviderLifecycleManager, IControlsSubscriber.Stub stub) {
            Intrinsics.checkParameterIsNotNull(stub, "subscriber");
            this.this$0 = controlsProviderLifecycleManager;
            super();
            this.subscriber = stub;
        }

        /* renamed from: callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public boolean mo10965x93b7231b() {
            String access$getTAG$p = this.this$0.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("load ");
            sb.append(this.this$0.getComponentName());
            Log.d(access$getTAG$p, sb.toString());
            ServiceWrapper access$getWrapper$p = this.this$0.wrapper;
            if (access$getWrapper$p != null) {
                return access$getWrapper$p.load(this.subscriber);
            }
            return false;
        }
    }

    /* compiled from: ControlsProviderLifecycleManager.kt */
    public abstract class ServiceMethod {
        /* renamed from: callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public abstract boolean mo10965x93b7231b();

        public ServiceMethod() {
        }

        public final void run() {
            if (!mo10965x93b7231b()) {
                ControlsProviderLifecycleManager.this.queueServiceMethod(this);
                ControlsProviderLifecycleManager.this.binderDied();
            }
        }
    }

    /* compiled from: ControlsProviderLifecycleManager.kt */
    public final class Subscribe extends ServiceMethod {
        private final List<String> list;
        private final IControlsSubscriber subscriber;
        final /* synthetic */ ControlsProviderLifecycleManager this$0;

        public Subscribe(ControlsProviderLifecycleManager controlsProviderLifecycleManager, List<String> list2, IControlsSubscriber iControlsSubscriber) {
            Intrinsics.checkParameterIsNotNull(list2, "list");
            Intrinsics.checkParameterIsNotNull(iControlsSubscriber, "subscriber");
            this.this$0 = controlsProviderLifecycleManager;
            super();
            this.list = list2;
            this.subscriber = iControlsSubscriber;
        }

        /* renamed from: callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public boolean mo10965x93b7231b() {
            String access$getTAG$p = this.this$0.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("subscribe ");
            sb.append(this.this$0.getComponentName());
            sb.append(" - ");
            sb.append(this.list);
            Log.d(access$getTAG$p, sb.toString());
            ServiceWrapper access$getWrapper$p = this.this$0.wrapper;
            if (access$getWrapper$p != null) {
                return access$getWrapper$p.subscribe(this.list, this.subscriber);
            }
            return false;
        }
    }

    /* compiled from: ControlsProviderLifecycleManager.kt */
    public final class Suggest extends ServiceMethod {
        private final IControlsSubscriber.Stub subscriber;
        final /* synthetic */ ControlsProviderLifecycleManager this$0;

        public Suggest(ControlsProviderLifecycleManager controlsProviderLifecycleManager, IControlsSubscriber.Stub stub) {
            Intrinsics.checkParameterIsNotNull(stub, "subscriber");
            this.this$0 = controlsProviderLifecycleManager;
            super();
            this.subscriber = stub;
        }

        /* renamed from: callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public boolean mo10965x93b7231b() {
            String access$getTAG$p = this.this$0.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("suggest ");
            sb.append(this.this$0.getComponentName());
            Log.d(access$getTAG$p, sb.toString());
            ServiceWrapper access$getWrapper$p = this.this$0.wrapper;
            if (access$getWrapper$p != null) {
                return access$getWrapper$p.loadSuggested(this.subscriber);
            }
            return false;
        }
    }

    public ControlsProviderLifecycleManager(Context context2, DelayableExecutor delayableExecutor, Stub stub, UserHandle userHandle, ComponentName componentName2) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Intrinsics.checkParameterIsNotNull(delayableExecutor, "executor");
        Intrinsics.checkParameterIsNotNull(stub, "actionCallbackService");
        Intrinsics.checkParameterIsNotNull(userHandle, "user");
        Intrinsics.checkParameterIsNotNull(componentName2, "componentName");
        this.context = context2;
        this.executor = delayableExecutor;
        this.actionCallbackService = stub;
        this.user = userHandle;
        this.componentName = componentName2;
        Intent intent2 = new Intent();
        intent2.setComponent(this.componentName);
        Bundle bundle = new Bundle();
        bundle.putBinder("CALLBACK_TOKEN", this.token);
        intent2.putExtra("CALLBACK_BUNDLE", bundle);
        this.intent = intent2;
        this.serviceConnection = new ControlsProviderLifecycleManager$serviceConnection$1(this);
    }

    public final UserHandle getUser() {
        return this.user;
    }

    public final ComponentName getComponentName() {
        return this.componentName;
    }

    public final IBinder getToken() {
        return this.token;
    }

    /* access modifiers changed from: private */
    public final void bindService(boolean z) {
        this.executor.execute(new ControlsProviderLifecycleManager$bindService$1(this, z));
    }

    /* access modifiers changed from: private */
    public final void handlePendingServiceMethods() {
        ArraySet<ServiceMethod> arraySet;
        synchronized (this.queuedServiceMethods) {
            arraySet = new ArraySet<>(this.queuedServiceMethods);
            this.queuedServiceMethods.clear();
        }
        for (ServiceMethod run : arraySet) {
            run.run();
        }
    }

    public void binderDied() {
        if (this.wrapper != null) {
            this.wrapper = null;
            if (this.requiresBound) {
                Log.d(this.TAG, "binderDied");
            }
        }
    }

    /* access modifiers changed from: private */
    public final void queueServiceMethod(ServiceMethod serviceMethod) {
        synchronized (this.queuedServiceMethods) {
            this.queuedServiceMethods.add(serviceMethod);
        }
    }

    private final void invokeOrQueue(ServiceMethod serviceMethod) {
        if (this.wrapper != null) {
            serviceMethod.run();
            return;
        }
        queueServiceMethod(serviceMethod);
        bindService(true);
    }

    public final void maybeBindAndLoad(IControlsSubscriber.Stub stub) {
        Intrinsics.checkParameterIsNotNull(stub, "subscriber");
        this.onLoadCanceller = this.executor.executeDelayed(new ControlsProviderLifecycleManager$maybeBindAndLoad$1(this, stub), 30, TimeUnit.SECONDS);
        invokeOrQueue(new Load(this, stub));
    }

    public final void maybeBindAndLoadSuggested(IControlsSubscriber.Stub stub) {
        Intrinsics.checkParameterIsNotNull(stub, "subscriber");
        this.onLoadCanceller = this.executor.executeDelayed(new ControlsProviderLifecycleManager$maybeBindAndLoadSuggested$1(this, stub), 30, TimeUnit.SECONDS);
        invokeOrQueue(new Suggest(this, stub));
    }

    public final void cancelLoadTimeout() {
        Runnable runnable = this.onLoadCanceller;
        if (runnable != null) {
            runnable.run();
        }
        this.onLoadCanceller = null;
    }

    public final void maybeBindAndSubscribe(List<String> list, IControlsSubscriber iControlsSubscriber) {
        Intrinsics.checkParameterIsNotNull(list, "controlIds");
        Intrinsics.checkParameterIsNotNull(iControlsSubscriber, "subscriber");
        invokeOrQueue(new Subscribe(this, list, iControlsSubscriber));
    }

    public final void maybeBindAndSendAction(String str, ControlAction controlAction) {
        Intrinsics.checkParameterIsNotNull(str, "controlId");
        Intrinsics.checkParameterIsNotNull(controlAction, "action");
        invokeOrQueue(new Action(this, str, controlAction));
    }

    public final void startSubscription(IControlsSubscription iControlsSubscription, long j) {
        Intrinsics.checkParameterIsNotNull(iControlsSubscription, "subscription");
        String str = this.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("startSubscription: ");
        sb.append(iControlsSubscription);
        Log.d(str, sb.toString());
        synchronized (this.subscriptions) {
            this.subscriptions.add(iControlsSubscription);
        }
        ServiceWrapper serviceWrapper = this.wrapper;
        if (serviceWrapper != null) {
            serviceWrapper.request(iControlsSubscription, j);
        }
    }

    public final void cancelSubscription(IControlsSubscription iControlsSubscription) {
        Intrinsics.checkParameterIsNotNull(iControlsSubscription, "subscription");
        String str = this.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("cancelSubscription: ");
        sb.append(iControlsSubscription);
        Log.d(str, sb.toString());
        synchronized (this.subscriptions) {
            this.subscriptions.remove(iControlsSubscription);
        }
        ServiceWrapper serviceWrapper = this.wrapper;
        if (serviceWrapper != null) {
            serviceWrapper.cancel(iControlsSubscription);
        }
    }

    public final void unbindService() {
        ArrayList<IControlsSubscription> arrayList;
        Runnable runnable = this.onLoadCanceller;
        if (runnable != null) {
            runnable.run();
        }
        this.onLoadCanceller = null;
        synchronized (this.subscriptions) {
            arrayList = new ArrayList<>(this.subscriptions);
            this.subscriptions.clear();
        }
        for (IControlsSubscription iControlsSubscription : arrayList) {
            ServiceWrapper serviceWrapper = this.wrapper;
            if (serviceWrapper != null) {
                Intrinsics.checkExpressionValueIsNotNull(iControlsSubscription, "it");
                serviceWrapper.cancel(iControlsSubscription);
            }
        }
        bindService(false);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ControlsProviderLifecycleManager(");
        StringBuilder sb2 = new StringBuilder();
        sb2.append("component=");
        sb2.append(this.componentName);
        sb.append(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append(", user=");
        sb3.append(this.user);
        sb.append(sb3.toString());
        sb.append(")");
        String sb4 = sb.toString();
        Intrinsics.checkExpressionValueIsNotNull(sb4, "StringBuilder(\"ControlsP…\")\")\n        }.toString()");
        return sb4;
    }
}
