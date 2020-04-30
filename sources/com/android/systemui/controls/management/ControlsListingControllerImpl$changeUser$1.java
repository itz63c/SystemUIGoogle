package com.android.systemui.controls.management;

import android.content.Context;
import android.os.UserHandle;
import com.android.settingslib.applications.ServiceListing;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsListingControllerImpl.kt */
final class ControlsListingControllerImpl$changeUser$1 implements Runnable {
    final /* synthetic */ UserHandle $newUser;
    final /* synthetic */ ControlsListingControllerImpl this$0;

    ControlsListingControllerImpl$changeUser$1(ControlsListingControllerImpl controlsListingControllerImpl, UserHandle userHandle) {
        this.this$0 = controlsListingControllerImpl;
        this.$newUser = userHandle;
    }

    public final void run() {
        this.this$0.callbacks.clear();
        this.this$0.availableServices = CollectionsKt__CollectionsKt.emptyList();
        this.this$0.serviceListing.setListening(false);
        this.this$0.currentUserId = this.$newUser.getIdentifier();
        Context createContextAsUser = this.this$0.context.createContextAsUser(this.$newUser, 0);
        ControlsListingControllerImpl controlsListingControllerImpl = this.this$0;
        Function1 access$getServiceListingBuilder$p = controlsListingControllerImpl.serviceListingBuilder;
        Intrinsics.checkExpressionValueIsNotNull(createContextAsUser, "contextForUser");
        controlsListingControllerImpl.serviceListing = (ServiceListing) access$getServiceListingBuilder$p.invoke(createContextAsUser);
        this.this$0.serviceListing.addCallback(this.this$0.serviceListingCallback);
        this.this$0.serviceListing.setListening(true);
        this.this$0.serviceListing.reload();
    }
}
