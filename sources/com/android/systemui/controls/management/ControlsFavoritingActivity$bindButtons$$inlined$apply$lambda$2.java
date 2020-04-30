package com.android.systemui.controls.management;

import android.content.ComponentName;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.systemui.controls.controller.ControlInfo.Builder;
import com.android.systemui.controls.controller.ControlsControllerImpl;
import com.android.systemui.controls.controller.StructureInfo;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsFavoritingActivity.kt */
final class ControlsFavoritingActivity$bindButtons$$inlined$apply$lambda$2 implements OnClickListener {
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$bindButtons$$inlined$apply$lambda$2(ControlsFavoritingActivity controlsFavoritingActivity) {
        this.this$0 = controlsFavoritingActivity;
    }

    public final void onClick(View view) {
        if (this.this$0.component != null) {
            for (StructureContainer structureContainer : this.this$0.listOfStructures) {
                List<Builder> favorites = structureContainer.getModel().getFavorites();
                ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(favorites, 10));
                for (Builder build : favorites) {
                    arrayList.add(build.build());
                }
                ControlsControllerImpl access$getController$p = this.this$0.controller;
                ComponentName access$getComponent$p = this.this$0.component;
                if (access$getComponent$p != null) {
                    access$getController$p.replaceFavoritesForStructure(new StructureInfo(access$getComponent$p, structureContainer.getStructureName(), arrayList));
                } else {
                    Intrinsics.throwNpe();
                    throw null;
                }
            }
            this.this$0.finishAffinity();
        }
    }
}
