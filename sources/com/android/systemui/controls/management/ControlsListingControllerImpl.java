package com.android.systemui.controls.management;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.os.UserHandle;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.applications.ServiceListing;
import com.android.settingslib.applications.ServiceListing.Callback;
import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.management.ControlsListingController.ControlsListingCallback;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsListingControllerImpl.kt */
public final class ControlsListingControllerImpl implements ControlsListingController {
    /* access modifiers changed from: private */
    public List<? extends ServiceInfo> availableServices;
    /* access modifiers changed from: private */
    public final Executor backgroundExecutor;
    /* access modifiers changed from: private */
    public final Set<ControlsListingCallback> callbacks;
    /* access modifiers changed from: private */
    public final Context context;
    /* access modifiers changed from: private */
    public int currentUserId;
    /* access modifiers changed from: private */
    public ServiceListing serviceListing;
    /* access modifiers changed from: private */
    public final Function1<Context, ServiceListing> serviceListingBuilder;
    /* access modifiers changed from: private */
    public final Callback serviceListingCallback;

    @VisibleForTesting
    public ControlsListingControllerImpl(Context context2, Executor executor, Function1<? super Context, ? extends ServiceListing> function1) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Intrinsics.checkParameterIsNotNull(executor, "backgroundExecutor");
        Intrinsics.checkParameterIsNotNull(function1, "serviceListingBuilder");
        this.context = context2;
        this.backgroundExecutor = executor;
        this.serviceListingBuilder = function1;
        this.serviceListing = (ServiceListing) function1.invoke(context2);
        this.availableServices = CollectionsKt__CollectionsKt.emptyList();
        this.currentUserId = ActivityManager.getCurrentUser();
        ControlsListingControllerImpl$serviceListingCallback$1 controlsListingControllerImpl$serviceListingCallback$1 = new ControlsListingControllerImpl$serviceListingCallback$1(this);
        this.serviceListingCallback = controlsListingControllerImpl$serviceListingCallback$1;
        this.serviceListing.addCallback(controlsListingControllerImpl$serviceListingCallback$1);
        this.serviceListing.setListening(true);
        this.serviceListing.reload();
        this.callbacks = new LinkedHashSet();
    }

    public ControlsListingControllerImpl(Context context2, Executor executor) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Intrinsics.checkParameterIsNotNull(executor, "executor");
        this(context2, executor, C08121.INSTANCE);
    }

    public int getCurrentUserId() {
        return this.currentUserId;
    }

    public void changeUser(UserHandle userHandle) {
        Intrinsics.checkParameterIsNotNull(userHandle, "newUser");
        this.backgroundExecutor.execute(new ControlsListingControllerImpl$changeUser$1(this, userHandle));
    }

    public void addCallback(ControlsListingCallback controlsListingCallback) {
        Intrinsics.checkParameterIsNotNull(controlsListingCallback, "listener");
        this.backgroundExecutor.execute(new ControlsListingControllerImpl$addCallback$1(this, controlsListingCallback));
    }

    public void removeCallback(ControlsListingCallback controlsListingCallback) {
        Intrinsics.checkParameterIsNotNull(controlsListingCallback, "listener");
        this.backgroundExecutor.execute(new ControlsListingControllerImpl$removeCallback$1(this, controlsListingCallback));
    }

    public List<ControlsServiceInfo> getCurrentServices() {
        List<? extends ServiceInfo> list = this.availableServices;
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
        for (ServiceInfo controlsServiceInfo : list) {
            arrayList.add(new ControlsServiceInfo(this.context, controlsServiceInfo));
        }
        return arrayList;
    }

    public CharSequence getAppLabel(ComponentName componentName) {
        Object obj;
        Intrinsics.checkParameterIsNotNull(componentName, "name");
        Iterator it = getCurrentServices().iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            if (Intrinsics.areEqual((Object) ((ControlsServiceInfo) obj).componentName, (Object) componentName)) {
                break;
            }
        }
        ControlsServiceInfo controlsServiceInfo = (ControlsServiceInfo) obj;
        if (controlsServiceInfo != null) {
            return controlsServiceInfo.loadLabel();
        }
        return null;
    }
}
