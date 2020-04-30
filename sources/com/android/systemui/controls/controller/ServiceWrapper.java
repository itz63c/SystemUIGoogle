package com.android.systemui.controls.controller;

import android.service.controls.IControlsActionCallback;
import android.service.controls.IControlsProvider;
import android.service.controls.IControlsSubscriber;
import android.service.controls.IControlsSubscription;
import android.service.controls.actions.ControlAction;
import android.service.controls.actions.ControlActionWrapper;
import android.util.Log;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ServiceWrapper.kt */
public final class ServiceWrapper {
    private final IControlsProvider service;

    public ServiceWrapper(IControlsProvider iControlsProvider) {
        Intrinsics.checkParameterIsNotNull(iControlsProvider, "service");
        this.service = iControlsProvider;
    }

    public final boolean load(IControlsSubscriber iControlsSubscriber) {
        Intrinsics.checkParameterIsNotNull(iControlsSubscriber, "subscriber");
        try {
            this.service.load(iControlsSubscriber);
            return true;
        } catch (Exception e) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", e);
            return false;
        }
    }

    public final boolean loadSuggested(IControlsSubscriber iControlsSubscriber) {
        Intrinsics.checkParameterIsNotNull(iControlsSubscriber, "subscriber");
        try {
            this.service.loadSuggested(iControlsSubscriber);
            return true;
        } catch (Exception e) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", e);
            return false;
        }
    }

    public final boolean subscribe(List<String> list, IControlsSubscriber iControlsSubscriber) {
        Intrinsics.checkParameterIsNotNull(list, "controlIds");
        Intrinsics.checkParameterIsNotNull(iControlsSubscriber, "subscriber");
        try {
            this.service.subscribe(list, iControlsSubscriber);
            return true;
        } catch (Exception e) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", e);
            return false;
        }
    }

    public final boolean request(IControlsSubscription iControlsSubscription, long j) {
        Intrinsics.checkParameterIsNotNull(iControlsSubscription, "subscription");
        try {
            iControlsSubscription.request(j);
            return true;
        } catch (Exception e) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", e);
            return false;
        }
    }

    public final boolean cancel(IControlsSubscription iControlsSubscription) {
        Intrinsics.checkParameterIsNotNull(iControlsSubscription, "subscription");
        try {
            iControlsSubscription.cancel();
            return true;
        } catch (Exception e) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", e);
            return false;
        }
    }

    public final boolean action(String str, ControlAction controlAction, IControlsActionCallback iControlsActionCallback) {
        Intrinsics.checkParameterIsNotNull(str, "controlId");
        Intrinsics.checkParameterIsNotNull(controlAction, "action");
        Intrinsics.checkParameterIsNotNull(iControlsActionCallback, "cb");
        try {
            this.service.action(str, new ControlActionWrapper(controlAction), iControlsActionCallback);
            return true;
        } catch (Exception e) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", e);
            return false;
        }
    }
}
