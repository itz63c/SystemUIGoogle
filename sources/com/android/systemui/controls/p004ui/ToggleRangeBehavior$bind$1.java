package com.android.systemui.controls.p004ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo.RangeInfo;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ToggleRangeBehavior$bind$1 */
/* compiled from: ToggleRangeBehavior.kt */
public final class ToggleRangeBehavior$bind$1 extends AccessibilityDelegate {
    final /* synthetic */ ToggleRangeBehavior this$0;

    public boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
        Intrinsics.checkParameterIsNotNull(viewGroup, "host");
        Intrinsics.checkParameterIsNotNull(view, "child");
        Intrinsics.checkParameterIsNotNull(accessibilityEvent, "event");
        return false;
    }

    ToggleRangeBehavior$bind$1(ToggleRangeBehavior toggleRangeBehavior) {
        this.this$0 = toggleRangeBehavior;
    }

    public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
        Intrinsics.checkParameterIsNotNull(view, "host");
        Intrinsics.checkParameterIsNotNull(accessibilityNodeInfo, "info");
        super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
        int i = 0;
        float access$levelToRangeValue = this.this$0.levelToRangeValue(0);
        ToggleRangeBehavior toggleRangeBehavior = this.this$0;
        float access$levelToRangeValue2 = toggleRangeBehavior.levelToRangeValue(toggleRangeBehavior.getClipLayer().getLevel());
        float access$levelToRangeValue3 = this.this$0.levelToRangeValue(10000);
        double stepValue = (double) this.this$0.getRangeTemplate().getStepValue();
        if (stepValue != Math.floor(stepValue)) {
            i = 1;
        }
        accessibilityNodeInfo.setRangeInfo(RangeInfo.obtain(i, access$levelToRangeValue, access$levelToRangeValue3, access$levelToRangeValue2));
        accessibilityNodeInfo.addAction(AccessibilityAction.ACTION_SET_PROGRESS);
    }

    public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
        boolean z;
        Intrinsics.checkParameterIsNotNull(view, "host");
        if (i == 16) {
            ControlActionCoordinator controlActionCoordinator = ControlActionCoordinator.INSTANCE;
            ControlViewHolder cvh = this.this$0.getCvh();
            String templateId = this.this$0.getTemplate().getTemplateId();
            Intrinsics.checkExpressionValueIsNotNull(templateId, "template.getTemplateId()");
            controlActionCoordinator.toggle(cvh, templateId, this.this$0.getTemplate().isChecked());
        } else {
            if (i == AccessibilityAction.ACTION_SET_PROGRESS.getId() && bundle != null) {
                String str = "android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE";
                if (bundle.containsKey(str)) {
                    float f = (bundle.getFloat(str) - this.this$0.getRangeTemplate().getCurrentValue()) / (this.this$0.getRangeTemplate().getMaxValue() - this.this$0.getRangeTemplate().getMinValue());
                    ToggleRangeBehavior toggleRangeBehavior = this.this$0;
                    toggleRangeBehavior.updateRange(f, toggleRangeBehavior.getTemplate().isChecked());
                    this.this$0.endUpdateRange();
                }
            }
            z = false;
            if (!z || super.performAccessibilityAction(view, i, bundle)) {
                return true;
            }
            return false;
        }
        z = true;
        if (!z) {
        }
        return true;
    }
}
