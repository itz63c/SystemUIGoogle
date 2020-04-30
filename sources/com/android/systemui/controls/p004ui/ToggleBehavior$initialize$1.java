package com.android.systemui.controls.p004ui;

import android.view.View;
import android.view.View.OnClickListener;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ToggleBehavior$initialize$1 */
/* compiled from: ToggleBehavior.kt */
final class ToggleBehavior$initialize$1 implements OnClickListener {
    final /* synthetic */ ControlViewHolder $cvh;
    final /* synthetic */ ToggleBehavior this$0;

    ToggleBehavior$initialize$1(ToggleBehavior toggleBehavior, ControlViewHolder controlViewHolder) {
        this.this$0 = toggleBehavior;
        this.$cvh = controlViewHolder;
    }

    public final void onClick(View view) {
        ControlActionCoordinator controlActionCoordinator = ControlActionCoordinator.INSTANCE;
        ControlViewHolder controlViewHolder = this.$cvh;
        String templateId = this.this$0.getTemplate().getTemplateId();
        Intrinsics.checkExpressionValueIsNotNull(templateId, "template.getTemplateId()");
        controlActionCoordinator.toggle(controlViewHolder, templateId, this.this$0.getTemplate().isChecked());
    }
}
