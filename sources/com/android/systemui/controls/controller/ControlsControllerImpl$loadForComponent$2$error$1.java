package com.android.systemui.controls.controller;

import java.util.ArrayList;
import java.util.List;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$loadForComponent$2$error$1 implements Runnable {
    final /* synthetic */ ControlsControllerImpl$loadForComponent$2 this$0;

    ControlsControllerImpl$loadForComponent$2$error$1(ControlsControllerImpl$loadForComponent$2 controlsControllerImpl$loadForComponent$2) {
        this.this$0 = controlsControllerImpl$loadForComponent$2;
    }

    public final void run() {
        List<ControlInfo> controlsForComponent = Favorites.INSTANCE.getControlsForComponent(this.this$0.$componentName);
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(controlsForComponent, 10));
        for (ControlInfo controlId : controlsForComponent) {
            arrayList.add(controlId.getControlId());
        }
        ArrayList arrayList2 = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(controlsForComponent, 10));
        for (ControlInfo controlInfo : controlsForComponent) {
            ControlsControllerImpl$loadForComponent$2 controlsControllerImpl$loadForComponent$2 = this.this$0;
            arrayList2.add(controlsControllerImpl$loadForComponent$2.this$0.createRemovedStatus(controlsControllerImpl$loadForComponent$2.$componentName, controlInfo, false));
        }
        this.this$0.$dataCallback.accept(ControlsControllerKt.createLoadDataObject(arrayList2, arrayList, true));
    }
}
