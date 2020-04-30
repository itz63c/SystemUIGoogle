package com.android.systemui.controls.p004ui;

import android.content.ComponentName;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import com.android.systemui.controls.ControlsServiceInfo;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$createCallback$1$onServicesUpdated$1 */
/* compiled from: ControlsUiControllerImpl.kt */
final class ControlsUiControllerImpl$createCallback$1$onServicesUpdated$1 implements Runnable {
    final /* synthetic */ List $serviceInfos;
    final /* synthetic */ ControlsUiControllerImpl$createCallback$1 this$0;

    ControlsUiControllerImpl$createCallback$1$onServicesUpdated$1(ControlsUiControllerImpl$createCallback$1 controlsUiControllerImpl$createCallback$1, List list) {
        this.this$0 = controlsUiControllerImpl$createCallback$1;
        this.$serviceInfos = list;
    }

    public final void run() {
        Resources resources = this.this$0.this$0.getContext().getResources();
        Intrinsics.checkExpressionValueIsNotNull(resources, "context.resources");
        Configuration configuration = resources.getConfiguration();
        Intrinsics.checkExpressionValueIsNotNull(configuration, "context.resources.configuration");
        Collator instance = Collator.getInstance(configuration.getLocales().get(0));
        Intrinsics.checkExpressionValueIsNotNull(instance, "collator");
        C0815xb37c682a controlsUiControllerImpl$createCallback$1$onServicesUpdated$1$$special$$inlined$compareBy$1 = new C0815xb37c682a(instance);
        List<ControlsServiceInfo> mutableList = CollectionsKt___CollectionsKt.toMutableList((Collection) this.$serviceInfos);
        CollectionsKt__MutableCollectionsJVMKt.sortWith(mutableList, controlsUiControllerImpl$createCallback$1$onServicesUpdated$1$$special$$inlined$compareBy$1);
        ControlsUiControllerImpl controlsUiControllerImpl = this.this$0.this$0;
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(mutableList, 10));
        for (ControlsServiceInfo controlsServiceInfo : mutableList) {
            CharSequence loadLabel = controlsServiceInfo.loadLabel();
            Intrinsics.checkExpressionValueIsNotNull(loadLabel, "it.loadLabel()");
            Drawable loadIcon = controlsServiceInfo.loadIcon();
            Intrinsics.checkExpressionValueIsNotNull(loadIcon, "it.loadIcon()");
            ComponentName componentName = controlsServiceInfo.componentName;
            Intrinsics.checkExpressionValueIsNotNull(componentName, "it.componentName");
            arrayList.add(new SelectionItem(loadLabel, "", loadIcon, componentName));
        }
        controlsUiControllerImpl.lastItems = arrayList;
        this.this$0.this$0.getUiExecutor().execute(new Runnable(this) {
            final /* synthetic */ ControlsUiControllerImpl$createCallback$1$onServicesUpdated$1 this$0;

            {
                this.this$0 = r1;
            }

            public final void run() {
                ControlsUiControllerImpl$createCallback$1 controlsUiControllerImpl$createCallback$1 = this.this$0.this$0;
                controlsUiControllerImpl$createCallback$1.$onResult.invoke(ControlsUiControllerImpl.access$getLastItems$p(controlsUiControllerImpl$createCallback$1.this$0));
            }
        });
    }
}
