package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.service.controls.Control;
import android.util.ArrayMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$seedFavoritesForComponent$2$accept$1 implements Runnable {
    final /* synthetic */ List $controls;
    final /* synthetic */ ControlsControllerImpl$seedFavoritesForComponent$2 this$0;

    ControlsControllerImpl$seedFavoritesForComponent$2$accept$1(ControlsControllerImpl$seedFavoritesForComponent$2 controlsControllerImpl$seedFavoritesForComponent$2, List list) {
        this.this$0 = controlsControllerImpl$seedFavoritesForComponent$2;
        this.$controls = list;
    }

    public final void run() {
        ArrayMap arrayMap = new ArrayMap();
        for (Control control : this.$controls) {
            Object structure = control.getStructure();
            if (structure == null) {
                structure = "";
            }
            List list = (List) arrayMap.get(structure);
            if (list == null) {
                list = new ArrayList();
            }
            Intrinsics.checkExpressionValueIsNotNull(list, "structureToControls.get(…ableListOf<ControlInfo>()");
            String controlId = control.getControlId();
            Intrinsics.checkExpressionValueIsNotNull(controlId, "it.controlId");
            CharSequence title = control.getTitle();
            Intrinsics.checkExpressionValueIsNotNull(title, "it.title");
            CharSequence subtitle = control.getSubtitle();
            Intrinsics.checkExpressionValueIsNotNull(subtitle, "it.subtitle");
            list.add(new ControlInfo(controlId, title, subtitle, control.getDeviceType()));
            arrayMap.put(structure, list);
        }
        for (Entry entry : arrayMap.entrySet()) {
            CharSequence charSequence = (CharSequence) entry.getKey();
            List list2 = (List) entry.getValue();
            Favorites favorites = Favorites.INSTANCE;
            ComponentName componentName = this.this$0.$componentName;
            Intrinsics.checkExpressionValueIsNotNull(charSequence, "s");
            Intrinsics.checkExpressionValueIsNotNull(list2, "cs");
            favorites.replaceControls(new StructureInfo(componentName, charSequence, list2));
        }
        this.this$0.this$0.persistenceWrapper.storeFavorites(Favorites.INSTANCE.getAllStructures());
        this.this$0.$callback.accept(Boolean.TRUE);
        this.this$0.this$0.endSeedingCall(true);
    }
}
