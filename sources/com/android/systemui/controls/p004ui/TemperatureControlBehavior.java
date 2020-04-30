package com.android.systemui.controls.p004ui;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.service.controls.Control;
import android.service.controls.templates.TemperatureControlTemplate;
import android.widget.TextView;
import com.android.systemui.C2011R$id;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.TemperatureControlBehavior */
/* compiled from: TemperatureControlBehavior.kt */
public final class TemperatureControlBehavior implements Behavior {
    public Drawable clipLayer;
    public Control control;
    public ControlViewHolder cvh;
    public TemperatureControlTemplate template;

    public void initialize(ControlViewHolder controlViewHolder) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        this.cvh = controlViewHolder;
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
                    ControlViewHolder controlViewHolder2 = this.cvh;
                    if (controlViewHolder2 != null) {
                        Drawable background = controlViewHolder2.getLayout().getBackground();
                        if (background != null) {
                            Drawable findDrawableByLayerId = ((LayerDrawable) background).findDrawableByLayerId(C2011R$id.clip_layer);
                            Intrinsics.checkExpressionValueIsNotNull(findDrawableByLayerId, "ld.findDrawableByLayerId(R.id.clip_layer)");
                            this.clipLayer = findDrawableByLayerId;
                            Control control4 = this.control;
                            if (control4 != null) {
                                TemperatureControlTemplate controlTemplate = control4.getControlTemplate();
                                if (controlTemplate != null) {
                                    TemperatureControlTemplate temperatureControlTemplate = controlTemplate;
                                    this.template = temperatureControlTemplate;
                                    if (temperatureControlTemplate != null) {
                                        int currentActiveMode = temperatureControlTemplate.getCurrentActiveMode();
                                        boolean z = true;
                                        int i = 0;
                                        if (currentActiveMode == 0 || currentActiveMode == 1) {
                                            z = false;
                                        }
                                        Drawable drawable = this.clipLayer;
                                        if (drawable != null) {
                                            if (z) {
                                                i = 10000;
                                            }
                                            drawable.setLevel(i);
                                            ControlViewHolder controlViewHolder3 = this.cvh;
                                            if (controlViewHolder3 != null) {
                                                controlViewHolder3.mo11110x3918d5b8(z, currentActiveMode);
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
                                    throw new TypeCastException("null cannot be cast to non-null type android.service.controls.templates.TemperatureControlTemplate");
                                }
                            } else {
                                Intrinsics.throwUninitializedPropertyAccessException(str2);
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
