package com.android.systemui.controls.controller;

import android.content.ComponentName;
import com.android.systemui.controls.ControlsServiceInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$listingCallback$1$onServicesUpdated$1 implements Runnable {
    final /* synthetic */ List $serviceInfos;
    final /* synthetic */ ControlsControllerImpl$listingCallback$1 this$0;

    ControlsControllerImpl$listingCallback$1$onServicesUpdated$1(ControlsControllerImpl$listingCallback$1 controlsControllerImpl$listingCallback$1, List list) {
        this.this$0 = controlsControllerImpl$listingCallback$1;
        this.$serviceInfos = list;
    }

    public final void run() {
        List<ControlsServiceInfo> list = this.$serviceInfos;
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
        for (ControlsServiceInfo controlsServiceInfo : list) {
            arrayList.add(controlsServiceInfo.componentName);
        }
        Set set = CollectionsKt___CollectionsKt.toSet(arrayList);
        List<StructureInfo> allStructures = Favorites.INSTANCE.getAllStructures();
        ArrayList arrayList2 = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(allStructures, 10));
        for (StructureInfo componentName : allStructures) {
            arrayList2.add(componentName.getComponentName());
        }
        boolean z = false;
        for (ComponentName componentName2 : CollectionsKt___CollectionsKt.subtract(CollectionsKt___CollectionsKt.toSet(arrayList2), set)) {
            z = true;
            Favorites favorites = Favorites.INSTANCE;
            Intrinsics.checkExpressionValueIsNotNull(componentName2, "it");
            favorites.removeStructures(componentName2);
            this.this$0.this$0.bindingController.onComponentRemoved(componentName2);
        }
        if (z) {
            this.this$0.this$0.persistenceWrapper.storeFavorites(Favorites.INSTANCE.getAllStructures());
        }
    }
}
