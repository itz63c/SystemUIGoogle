package com.android.systemui.controls.p004ui;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.service.controls.Control;
import android.service.controls.templates.ToggleTemplate;
import android.widget.TextView;
import com.android.systemui.C2011R$id;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ToggleBehavior */
/* compiled from: ToggleBehavior.kt */
public final class ToggleBehavior implements Behavior {
    public Drawable clipLayer;
    public Control control;
    public ControlViewHolder cvh;
    public ToggleTemplate template;

    public final ToggleTemplate getTemplate() {
        ToggleTemplate toggleTemplate = this.template;
        if (toggleTemplate != null) {
            return toggleTemplate;
        }
        Intrinsics.throwUninitializedPropertyAccessException("template");
        throw null;
    }

    public void initialize(ControlViewHolder controlViewHolder) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        this.cvh = controlViewHolder;
        ControlViewHolder.m29x1a61c355(controlViewHolder, false, 0, 2, null);
        controlViewHolder.getLayout().setOnClickListener(new ToggleBehavior$initialize$1(this, controlViewHolder));
    }

    public void bind(ControlWithState controlWithState) {
        Intrinsics.checkParameterIsNotNull(controlWithState, "cws");
        Control control2 = controlWithState.getControl();
        if (control2 != null) {
            this.control = control2;
            ControlViewHolder controlViewHolder = this.cvh;
            String str = "cvh";
            if (controlViewHolder != null) {
                TextView status = controlViewHolder.getStatus();
                Control control3 = this.control;
                String str2 = "control";
                if (control3 != null) {
                    status.setText(control3.getStatusText());
                    Control control4 = this.control;
                    if (control4 != null) {
                        ToggleTemplate controlTemplate = control4.getControlTemplate();
                        if (controlTemplate != null) {
                            this.template = controlTemplate;
                            ControlViewHolder controlViewHolder2 = this.cvh;
                            if (controlViewHolder2 != null) {
                                Drawable background = controlViewHolder2.getLayout().getBackground();
                                if (background != null) {
                                    Drawable findDrawableByLayerId = ((LayerDrawable) background).findDrawableByLayerId(C2011R$id.clip_layer);
                                    Intrinsics.checkExpressionValueIsNotNull(findDrawableByLayerId, "ld.findDrawableByLayerId(R.id.clip_layer)");
                                    this.clipLayer = findDrawableByLayerId;
                                    ToggleTemplate toggleTemplate = this.template;
                                    if (toggleTemplate != null) {
                                        boolean isChecked = toggleTemplate.isChecked();
                                        Drawable drawable = this.clipLayer;
                                        if (drawable != null) {
                                            drawable.setLevel(isChecked ? 10000 : 0);
                                            ControlViewHolder controlViewHolder3 = this.cvh;
                                            if (controlViewHolder3 != null) {
                                                ControlViewHolder.m29x1a61c355(controlViewHolder3, isChecked, 0, 2, null);
                                            } else {
                                                Intrinsics.throwUninitializedPropertyAccessException(str);
                                                throw null;
                                            }
                                        } else {
                                            Intrinsics.throwUninitializedPropertyAccessException("clipLayer");
                                            throw null;
                                        }
                                    } else {
                                        Intrinsics.throwUninitializedPropertyAccessException("template");
                                        throw null;
                                    }
                                } else {
                                    throw new TypeCastException("null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
                                }
                            } else {
                                Intrinsics.throwUninitializedPropertyAccessException(str);
                                throw null;
                            }
                        } else {
                            throw new TypeCastException("null cannot be cast to non-null type android.service.controls.templates.ToggleTemplate");
                        }
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException(str2);
                        throw null;
                    }
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException(str2);
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException(str);
                throw null;
            }
        } else {
            Intrinsics.throwNpe();
            throw null;
        }
    }
}
