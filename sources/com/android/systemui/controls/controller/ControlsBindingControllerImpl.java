package com.android.systemui.controls.controller;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.controls.Control;
import android.service.controls.IControlsSubscriber.Stub;
import android.service.controls.IControlsSubscription;
import android.service.controls.actions.ControlAction;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.controls.controller.ControlsBindingController.LoadCallback;
import com.android.systemui.util.concurrency.DelayableExecutor;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

@VisibleForTesting
/* compiled from: ControlsBindingControllerImpl.kt */
public class ControlsBindingControllerImpl implements ControlsBindingController {
    private final ControlsBindingControllerImpl$actionCallbackService$1 actionCallbackService = new ControlsBindingControllerImpl$actionCallbackService$1(this);
    /* access modifiers changed from: private */
    public final DelayableExecutor backgroundExecutor;
    private final Context context;
    /* access modifiers changed from: private */
    public ControlsProviderLifecycleManager currentProvider;
    /* access modifiers changed from: private */
    public UserHandle currentUser = UserHandle.of(ActivityManager.getCurrentUser());
    /* access modifiers changed from: private */
    public final Lazy<ControlsController> lazyController;
    private StatefulControlSubscriber statefulControlSubscriber;

    /* compiled from: ControlsBindingControllerImpl.kt */
    private abstract class CallbackRunnable implements Runnable {
        private final ControlsProviderLifecycleManager provider;
        final /* synthetic */ ControlsBindingControllerImpl this$0;
        private final IBinder token;

        public abstract void doRun();

        public CallbackRunnable(ControlsBindingControllerImpl controlsBindingControllerImpl, IBinder iBinder) {
            Intrinsics.checkParameterIsNotNull(iBinder, "token");
            this.this$0 = controlsBindingControllerImpl;
            this.token = iBinder;
            this.provider = controlsBindingControllerImpl.currentProvider;
        }

        /* access modifiers changed from: protected */
        public final ControlsProviderLifecycleManager getProvider() {
            return this.provider;
        }

        public void run() {
            ControlsProviderLifecycleManager controlsProviderLifecycleManager = this.provider;
            String str = "ControlsBindingControllerImpl";
            if (controlsProviderLifecycleManager == null) {
                Log.e(str, "No current provider set");
            } else if (!Intrinsics.areEqual((Object) controlsProviderLifecycleManager.getUser(), (Object) this.this$0.currentUser)) {
                StringBuilder sb = new StringBuilder();
                sb.append("User ");
                sb.append(this.provider.getUser());
                sb.append(" is not current user");
                Log.e(str, sb.toString());
            } else if (!Intrinsics.areEqual((Object) this.token, (Object) this.provider.getToken())) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Provider for token:");
                sb2.append(this.token);
                sb2.append(" does not exist anymore");
                Log.e(str, sb2.toString());
            } else {
                doRun();
            }
        }
    }

    /* compiled from: ControlsBindingControllerImpl.kt */
    private final class LoadSubscriber extends Stub {
        /* access modifiers changed from: private */
        public Function0<Unit> _loadCancelInternal;
        private final LoadCallback callback;
        private boolean hasError;
        private final ArrayList<Control> loadedControls = new ArrayList<>();
        private final long requestLimit;
        final /* synthetic */ ControlsBindingControllerImpl this$0;

        public LoadSubscriber(ControlsBindingControllerImpl controlsBindingControllerImpl, LoadCallback loadCallback, long j) {
            Intrinsics.checkParameterIsNotNull(loadCallback, "callback");
            this.this$0 = controlsBindingControllerImpl;
            this.callback = loadCallback;
            this.requestLimit = j;
        }

        public final ArrayList<Control> getLoadedControls() {
            return this.loadedControls;
        }

        public final Runnable loadCancel() {
            return new ControlsBindingControllerImpl$LoadSubscriber$loadCancel$1(this);
        }

        public void onSubscribe(IBinder iBinder, IControlsSubscription iControlsSubscription) {
            Intrinsics.checkParameterIsNotNull(iBinder, "token");
            Intrinsics.checkParameterIsNotNull(iControlsSubscription, "subs");
            this._loadCancelInternal = new ControlsBindingControllerImpl$LoadSubscriber$onSubscribe$1(iControlsSubscription);
            DelayableExecutor access$getBackgroundExecutor$p = this.this$0.backgroundExecutor;
            OnSubscribeRunnable onSubscribeRunnable = new OnSubscribeRunnable(this.this$0, iBinder, iControlsSubscription, this.requestLimit);
            access$getBackgroundExecutor$p.execute(onSubscribeRunnable);
        }

        public void onNext(IBinder iBinder, Control control) {
            Intrinsics.checkParameterIsNotNull(iBinder, "token");
            Intrinsics.checkParameterIsNotNull(control, "c");
            this.this$0.backgroundExecutor.execute(new ControlsBindingControllerImpl$LoadSubscriber$onNext$1(this, control));
        }

        public void onError(IBinder iBinder, String str) {
            Intrinsics.checkParameterIsNotNull(iBinder, "token");
            Intrinsics.checkParameterIsNotNull(str, "s");
            this.hasError = true;
            this._loadCancelInternal = ControlsBindingControllerImpl$LoadSubscriber$onError$1.INSTANCE;
            ControlsProviderLifecycleManager access$getCurrentProvider$p = this.this$0.currentProvider;
            if (access$getCurrentProvider$p != null) {
                access$getCurrentProvider$p.cancelLoadTimeout();
            }
            this.this$0.backgroundExecutor.execute(new OnLoadErrorRunnable(this.this$0, iBinder, str, this.callback));
        }

        public void onComplete(IBinder iBinder) {
            Intrinsics.checkParameterIsNotNull(iBinder, "token");
            this._loadCancelInternal = ControlsBindingControllerImpl$LoadSubscriber$onComplete$1.INSTANCE;
            if (!this.hasError) {
                ControlsProviderLifecycleManager access$getCurrentProvider$p = this.this$0.currentProvider;
                if (access$getCurrentProvider$p != null) {
                    access$getCurrentProvider$p.cancelLoadTimeout();
                }
                this.this$0.backgroundExecutor.execute(new OnLoadRunnable(this.this$0, iBinder, this.loadedControls, this.callback));
            }
        }
    }

    /* compiled from: ControlsBindingControllerImpl.kt */
    private final class OnActionResponseRunnable extends CallbackRunnable {
        private final String controlId;
        private final int response;
        final /* synthetic */ ControlsBindingControllerImpl this$0;

        public OnActionResponseRunnable(ControlsBindingControllerImpl controlsBindingControllerImpl, IBinder iBinder, String str, int i) {
            Intrinsics.checkParameterIsNotNull(iBinder, "token");
            Intrinsics.checkParameterIsNotNull(str, "controlId");
            this.this$0 = controlsBindingControllerImpl;
            super(controlsBindingControllerImpl, iBinder);
            this.controlId = str;
            this.response = i;
        }

        public void doRun() {
            ControlsProviderLifecycleManager provider = getProvider();
            if (provider != null) {
                ((ControlsController) this.this$0.lazyController.get()).onActionResponse(provider.getComponentName(), this.controlId, this.response);
            }
        }
    }

    /* compiled from: ControlsBindingControllerImpl.kt */
    private final class OnLoadErrorRunnable extends CallbackRunnable {
        private final LoadCallback callback;
        private final String error;

        public OnLoadErrorRunnable(ControlsBindingControllerImpl controlsBindingControllerImpl, IBinder iBinder, String str, LoadCallback loadCallback) {
            Intrinsics.checkParameterIsNotNull(iBinder, "token");
            Intrinsics.checkParameterIsNotNull(str, "error");
            Intrinsics.checkParameterIsNotNull(loadCallback, "callback");
            super(controlsBindingControllerImpl, iBinder);
            this.error = str;
            this.callback = loadCallback;
        }

        public void doRun() {
            this.callback.error(this.error);
            ControlsProviderLifecycleManager provider = getProvider();
            if (provider != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("onError receive from '");
                sb.append(provider.getComponentName());
                sb.append("': ");
                sb.append(this.error);
                Log.e("ControlsBindingControllerImpl", sb.toString());
            }
        }
    }

    /* compiled from: ControlsBindingControllerImpl.kt */
    private final class OnLoadRunnable extends CallbackRunnable {
        private final LoadCallback callback;
        private final List<Control> list;

        public OnLoadRunnable(ControlsBindingControllerImpl controlsBindingControllerImpl, IBinder iBinder, List<Control> list2, LoadCallback loadCallback) {
            Intrinsics.checkParameterIsNotNull(iBinder, "token");
            Intrinsics.checkParameterIsNotNull(list2, "list");
            Intrinsics.checkParameterIsNotNull(loadCallback, "callback");
            super(controlsBindingControllerImpl, iBinder);
            this.list = list2;
            this.callback = loadCallback;
        }

        public void doRun() {
            this.callback.accept(this.list);
        }
    }

    /* compiled from: ControlsBindingControllerImpl.kt */
    private final class OnSubscribeRunnable extends CallbackRunnable {
        private final long requestLimit;
        private final IControlsSubscription subscription;

        public OnSubscribeRunnable(ControlsBindingControllerImpl controlsBindingControllerImpl, IBinder iBinder, IControlsSubscription iControlsSubscription, long j) {
            Intrinsics.checkParameterIsNotNull(iBinder, "token");
            Intrinsics.checkParameterIsNotNull(iControlsSubscription, "subscription");
            super(controlsBindingControllerImpl, iBinder);
            this.subscription = iControlsSubscription;
            this.requestLimit = j;
        }

        public void doRun() {
            ControlsProviderLifecycleManager provider = getProvider();
            if (provider != null) {
                provider.startSubscription(this.subscription, this.requestLimit);
            }
        }
    }

    public ControlsBindingControllerImpl(Context context2, DelayableExecutor delayableExecutor, Lazy<ControlsController> lazy) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Intrinsics.checkParameterIsNotNull(delayableExecutor, "backgroundExecutor");
        Intrinsics.checkParameterIsNotNull(lazy, "lazyController");
        this.context = context2;
        this.backgroundExecutor = delayableExecutor;
        this.lazyController = lazy;
    }

    public int getCurrentUserId() {
        UserHandle userHandle = this.currentUser;
        Intrinsics.checkExpressionValueIsNotNull(userHandle, "currentUser");
        return userHandle.getIdentifier();
    }

    @VisibleForTesting
    /* renamed from: createProviderManager$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public ControlsProviderLifecycleManager mo10893xb2527126(ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "component");
        Context context2 = this.context;
        DelayableExecutor delayableExecutor = this.backgroundExecutor;
        ControlsBindingControllerImpl$actionCallbackService$1 controlsBindingControllerImpl$actionCallbackService$1 = this.actionCallbackService;
        UserHandle userHandle = this.currentUser;
        Intrinsics.checkExpressionValueIsNotNull(userHandle, "currentUser");
        ControlsProviderLifecycleManager controlsProviderLifecycleManager = new ControlsProviderLifecycleManager(context2, delayableExecutor, controlsBindingControllerImpl$actionCallbackService$1, userHandle, componentName);
        return controlsProviderLifecycleManager;
    }

    private final ControlsProviderLifecycleManager retrieveLifecycleManager(ComponentName componentName) {
        ControlsProviderLifecycleManager controlsProviderLifecycleManager = this.currentProvider;
        if (controlsProviderLifecycleManager != null) {
            if (!Intrinsics.areEqual((Object) controlsProviderLifecycleManager != null ? controlsProviderLifecycleManager.getComponentName() : null, (Object) componentName)) {
                unbind();
            }
        }
        ControlsProviderLifecycleManager controlsProviderLifecycleManager2 = this.currentProvider;
        if (controlsProviderLifecycleManager2 == null) {
            controlsProviderLifecycleManager2 = mo10893xb2527126(componentName);
        }
        this.currentProvider = controlsProviderLifecycleManager2;
        return controlsProviderLifecycleManager2;
    }

    public Runnable bindAndLoad(ComponentName componentName, LoadCallback loadCallback) {
        Intrinsics.checkParameterIsNotNull(componentName, "component");
        Intrinsics.checkParameterIsNotNull(loadCallback, "callback");
        LoadSubscriber loadSubscriber = new LoadSubscriber(this, loadCallback, 100000);
        retrieveLifecycleManager(componentName).maybeBindAndLoad(loadSubscriber);
        return loadSubscriber.loadCancel();
    }

    public void bindAndLoadSuggested(ComponentName componentName, LoadCallback loadCallback) {
        Intrinsics.checkParameterIsNotNull(componentName, "component");
        Intrinsics.checkParameterIsNotNull(loadCallback, "callback");
        retrieveLifecycleManager(componentName).maybeBindAndLoadSuggested(new LoadSubscriber(this, loadCallback, 4));
    }

    public void subscribe(StructureInfo structureInfo) {
        Intrinsics.checkParameterIsNotNull(structureInfo, "structureInfo");
        unsubscribe();
        ControlsProviderLifecycleManager retrieveLifecycleManager = retrieveLifecycleManager(structureInfo.getComponentName());
        Object obj = this.lazyController.get();
        Intrinsics.checkExpressionValueIsNotNull(obj, "lazyController.get()");
        StatefulControlSubscriber statefulControlSubscriber2 = new StatefulControlSubscriber((ControlsController) obj, retrieveLifecycleManager, this.backgroundExecutor, 100000);
        this.statefulControlSubscriber = statefulControlSubscriber2;
        List<ControlInfo> controls = structureInfo.getControls();
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(controls, 10));
        for (ControlInfo controlId : controls) {
            arrayList.add(controlId.getControlId());
        }
        retrieveLifecycleManager.maybeBindAndSubscribe(arrayList, statefulControlSubscriber2);
    }

    public void unsubscribe() {
        StatefulControlSubscriber statefulControlSubscriber2 = this.statefulControlSubscriber;
        if (statefulControlSubscriber2 != null) {
            statefulControlSubscriber2.cancel();
        }
        this.statefulControlSubscriber = null;
    }

    public void action(ComponentName componentName, ControlInfo controlInfo, ControlAction controlAction) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(controlInfo, "controlInfo");
        Intrinsics.checkParameterIsNotNull(controlAction, "action");
        if (this.statefulControlSubscriber == null) {
            Log.w("ControlsBindingControllerImpl", "No actions can occur outside of an active subscription. Ignoring.");
        } else {
            retrieveLifecycleManager(componentName).maybeBindAndSendAction(controlInfo.getControlId(), controlAction);
        }
    }

    public void changeUser(UserHandle userHandle) {
        Intrinsics.checkParameterIsNotNull(userHandle, "newUser");
        if (!Intrinsics.areEqual((Object) userHandle, (Object) this.currentUser)) {
            unsubscribe();
            unbind();
            this.currentProvider = null;
            this.currentUser = userHandle;
        }
    }

    /* access modifiers changed from: private */
    public final void unbind() {
        ControlsProviderLifecycleManager controlsProviderLifecycleManager = this.currentProvider;
        if (controlsProviderLifecycleManager != null) {
            controlsProviderLifecycleManager.unbindService();
        }
        this.currentProvider = null;
    }

    public void onComponentRemoved(ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        this.backgroundExecutor.execute(new ControlsBindingControllerImpl$onComponentRemoved$1(this, componentName));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("  ControlsBindingController:\n");
        StringBuilder sb2 = new StringBuilder();
        sb2.append("    currentUser=");
        sb2.append(this.currentUser);
        sb2.append(10);
        sb.append(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("    StatefulControlSubscriber=");
        sb3.append(this.statefulControlSubscriber);
        sb.append(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("    Providers=");
        sb4.append(this.currentProvider);
        sb4.append(10);
        sb.append(sb4.toString());
        String sb5 = sb.toString();
        Intrinsics.checkExpressionValueIsNotNull(sb5, "StringBuilder(\"  Control…\\n\")\n        }.toString()");
        return sb5;
    }
}
