package com.android.systemui.controls.controller;

import android.service.controls.Control;
import com.android.systemui.controls.ControlStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$loadForComponent$2$accept$1 implements Runnable {
    final /* synthetic */ List $controls;
    final /* synthetic */ ControlsControllerImpl$loadForComponent$2 this$0;

    ControlsControllerImpl$loadForComponent$2$accept$1(ControlsControllerImpl$loadForComponent$2 controlsControllerImpl$loadForComponent$2, List list) {
        this.this$0 = controlsControllerImpl$loadForComponent$2;
        this.$controls = list;
    }

    public final void run() {
        List<ControlInfo> controlsForComponent = Favorites.INSTANCE.getControlsForComponent(this.this$0.$componentName);
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(controlsForComponent, 10));
        for (ControlInfo controlId : controlsForComponent) {
            arrayList.add(controlId.getControlId());
        }
        if (Favorites.INSTANCE.updateControls(this.this$0.$componentName, this.$controls)) {
            this.this$0.this$0.persistenceWrapper.storeFavorites(Favorites.INSTANCE.getAllStructures());
        }
        Set access$findRemoved = this.this$0.this$0.findRemoved(CollectionsKt___CollectionsKt.toSet(arrayList), this.$controls);
        List<Control> list = this.$controls;
        ArrayList arrayList2 = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
        for (Control control : list) {
            ControlStatus controlStatus = new ControlStatus(control, this.this$0.$componentName, arrayList.contains(control.getControlId()), false, 8, null);
            arrayList2.add(controlStatus);
        }
        List controlsForComponent2 = Favorites.INSTANCE.getControlsForComponent(this.this$0.$componentName);
        ArrayList<ControlInfo> arrayList3 = new ArrayList<>();
        for (Object next : controlsForComponent2) {
            if (access$findRemoved.contains(((ControlInfo) next).getControlId())) {
                arrayList3.add(next);
            }
        }
        ArrayList arrayList4 = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(arrayList3, 10));
        for (ControlInfo controlInfo : arrayList3) {
            ControlsControllerImpl$loadForComponent$2 controlsControllerImpl$loadForComponent$2 = this.this$0;
            arrayList4.add(ControlsControllerImpl.createRemovedStatus$default(controlsControllerImpl$loadForComponent$2.this$0, controlsControllerImpl$loadForComponent$2.$componentName, controlInfo, false, 4, null));
        }
        this.this$0.$dataCallback.accept(ControlsControllerKt.createLoadDataObject$default(CollectionsKt___CollectionsKt.plus((Collection) arrayList4, (Iterable) arrayList2), arrayList, false, 4, null));
    }
}
