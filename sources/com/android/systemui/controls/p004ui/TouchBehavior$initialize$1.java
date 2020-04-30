package com.android.systemui.controls.p004ui;

import android.view.View;
import android.view.View.OnClickListener;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.TouchBehavior$initialize$1 */
/* compiled from: TouchBehavior.kt */
final class TouchBehavior$initialize$1 implements OnClickListener {
    final /* synthetic */ ControlViewHolder $cvh;
    final /* synthetic */ TouchBehavior this$0;

    TouchBehavior$initialize$1(TouchBehavior touchBehavior, ControlViewHolder controlViewHolder) {
        this.this$0 = touchBehavior;
        this.$cvh = controlViewHolder;
    }

    public final void onClick(View view) {
        ControlActionCoordinator controlActionCoordinator = ControlActionCoordinator.INSTANCE;
        ControlViewHolder controlViewHolder = this.$cvh;
        String templateId = this.this$0.getTemplate().getTemplateId();
        Intrinsics.checkExpressionValueIsNotNull(templateId, "template.getTemplateId()");
        controlActionCoordinator.touch(controlViewHolder, templateId);
    }
}
