package com.android.systemui.controls.management;

import com.android.systemui.C2017R$string;
import com.android.systemui.controls.ControlStatus;
import com.android.systemui.controls.controller.ControlsController.LoadData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsFavoritingActivity.kt */
final class ControlsFavoritingActivity$loadControls$$inlined$let$lambda$1<T> implements Consumer<LoadData> {
    final /* synthetic */ CharSequence $emptyZoneString;
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$loadControls$$inlined$let$lambda$1(CharSequence charSequence, ControlsFavoritingActivity controlsFavoritingActivity) {
        this.$emptyZoneString = charSequence;
        this.this$0 = controlsFavoritingActivity;
    }

    public final void accept(LoadData loadData) {
        Intrinsics.checkParameterIsNotNull(loadData, "data");
        List allControls = loadData.getAllControls();
        List favoritesIds = loadData.getFavoritesIds();
        final boolean errorOnLoad = loadData.getErrorOnLoad();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Object next : allControls) {
            Object structure = ((ControlStatus) next).getControl().getStructure();
            if (structure == null) {
                structure = "";
            }
            Object obj = linkedHashMap.get(structure);
            if (obj == null) {
                obj = new ArrayList();
                linkedHashMap.put(structure, obj);
            }
            ((List) obj).add(next);
        }
        ControlsFavoritingActivity controlsFavoritingActivity = this.this$0;
        ArrayList arrayList = new ArrayList(linkedHashMap.size());
        for (Entry entry : linkedHashMap.entrySet()) {
            CharSequence charSequence = (CharSequence) entry.getKey();
            List list = (List) entry.getValue();
            CharSequence charSequence2 = this.$emptyZoneString;
            Intrinsics.checkExpressionValueIsNotNull(charSequence2, "emptyZoneString");
            arrayList.add(new StructureContainer(charSequence, new AllModel(list, favoritesIds, charSequence2)));
        }
        controlsFavoritingActivity.listOfStructures = CollectionsKt___CollectionsKt.sortedWith(arrayList, ControlsFavoritingActivity.access$getComparator$p(this.this$0));
        this.this$0.executor.execute(new Runnable(this) {
            final /* synthetic */ ControlsFavoritingActivity$loadControls$$inlined$let$lambda$1 this$0;

            {
                this.this$0 = r1;
            }

            public final void run() {
                ControlsFavoritingActivity.access$getDoneButton$p(this.this$0.this$0).setEnabled(true);
                ControlsFavoritingActivity.access$getStructurePager$p(this.this$0.this$0).setAdapter(new StructureAdapter(this.this$0.this$0.listOfStructures));
                int i = 8;
                if (errorOnLoad) {
                    ControlsFavoritingActivity.access$getStatusText$p(this.this$0.this$0).setText(this.this$0.this$0.getResources().getText(C2017R$string.controls_favorite_load_error));
                } else {
                    ControlsFavoritingActivity.access$getStatusText$p(this.this$0.this$0).setVisibility(8);
                }
                ControlsFavoritingActivity.access$getPageIndicator$p(this.this$0.this$0).setNumPages(this.this$0.this$0.listOfStructures.size());
                ControlsFavoritingActivity.access$getPageIndicator$p(this.this$0.this$0).setLocation(0.0f);
                ManagementPageIndicator access$getPageIndicator$p = ControlsFavoritingActivity.access$getPageIndicator$p(this.this$0.this$0);
                if (this.this$0.this$0.listOfStructures.size() > 1) {
                    i = 0;
                }
                access$getPageIndicator$p.setVisibility(i);
            }
        });
    }
}
