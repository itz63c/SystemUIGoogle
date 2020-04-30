package com.android.systemui.controls.p004ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.service.controls.Control;
import android.service.controls.actions.FloatAction;
import android.service.controls.templates.RangeTemplate;
import android.service.controls.templates.ToggleRangeTemplate;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import java.util.Arrays;
import java.util.IllegalFormatException;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;

/* renamed from: com.android.systemui.controls.ui.ToggleRangeBehavior */
/* compiled from: ToggleRangeBehavior.kt */
public final class ToggleRangeBehavior implements Behavior {
    public Drawable clipLayer;
    public Context context;
    public Control control;
    public ControlViewHolder cvh;
    public RangeTemplate rangeTemplate;
    public TextView status;
    public TextView statusExtra;
    public ToggleRangeTemplate template;

    /* renamed from: com.android.systemui.controls.ui.ToggleRangeBehavior$ToggleRangeGestureListener */
    /* compiled from: ToggleRangeBehavior.kt */
    public final class ToggleRangeGestureListener extends SimpleOnGestureListener {
        private boolean isDragging;
        final /* synthetic */ ToggleRangeBehavior this$0;

        /* renamed from: v */
        private final View f45v;

        public boolean onDown(MotionEvent motionEvent) {
            Intrinsics.checkParameterIsNotNull(motionEvent, "e");
            return true;
        }

        public ToggleRangeGestureListener(ToggleRangeBehavior toggleRangeBehavior, View view) {
            Intrinsics.checkParameterIsNotNull(view, "v");
            this.this$0 = toggleRangeBehavior;
            this.f45v = view;
        }

        public final boolean isDragging() {
            return this.isDragging;
        }

        public final void setDragging(boolean z) {
            this.isDragging = z;
        }

        public void onLongPress(MotionEvent motionEvent) {
            Intrinsics.checkParameterIsNotNull(motionEvent, "e");
            ControlActionCoordinator.INSTANCE.longPress(this.this$0.getCvh());
        }

        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            Intrinsics.checkParameterIsNotNull(motionEvent, "e1");
            Intrinsics.checkParameterIsNotNull(motionEvent2, "e2");
            if (!this.isDragging) {
                this.this$0.beginUpdateRange();
                this.isDragging = true;
            }
            this.this$0.updateRange((-f) / ((float) this.f45v.getWidth()), true);
            return true;
        }

        public boolean onSingleTapUp(MotionEvent motionEvent) {
            Intrinsics.checkParameterIsNotNull(motionEvent, "e");
            ToggleRangeBehavior toggleRangeBehavior = this.this$0;
            ControlActionCoordinator controlActionCoordinator = ControlActionCoordinator.INSTANCE;
            ControlViewHolder cvh = toggleRangeBehavior.getCvh();
            String templateId = toggleRangeBehavior.getTemplate().getTemplateId();
            Intrinsics.checkExpressionValueIsNotNull(templateId, "th.template.getTemplateId()");
            controlActionCoordinator.toggle(cvh, templateId, toggleRangeBehavior.getTemplate().isChecked());
            return true;
        }
    }

    public final Drawable getClipLayer() {
        Drawable drawable = this.clipLayer;
        if (drawable != null) {
            return drawable;
        }
        Intrinsics.throwUninitializedPropertyAccessException("clipLayer");
        throw null;
    }

    public final ToggleRangeTemplate getTemplate() {
        ToggleRangeTemplate toggleRangeTemplate = this.template;
        if (toggleRangeTemplate != null) {
            return toggleRangeTemplate;
        }
        Intrinsics.throwUninitializedPropertyAccessException("template");
        throw null;
    }

    public final ControlViewHolder getCvh() {
        ControlViewHolder controlViewHolder = this.cvh;
        if (controlViewHolder != null) {
            return controlViewHolder;
        }
        Intrinsics.throwUninitializedPropertyAccessException("cvh");
        throw null;
    }

    public final RangeTemplate getRangeTemplate() {
        RangeTemplate rangeTemplate2 = this.rangeTemplate;
        if (rangeTemplate2 != null) {
            return rangeTemplate2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
        throw null;
    }

    public void initialize(ControlViewHolder controlViewHolder) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        this.cvh = controlViewHolder;
        TextView status2 = controlViewHolder.getStatus();
        this.status = status2;
        if (status2 != null) {
            Context context2 = status2.getContext();
            Intrinsics.checkExpressionValueIsNotNull(context2, "status.getContext()");
            this.context = context2;
            ControlViewHolder.m29x1a61c355(controlViewHolder, false, 0, 2, null);
            ToggleRangeGestureListener toggleRangeGestureListener = new ToggleRangeGestureListener(this, controlViewHolder.getLayout());
            Context context3 = this.context;
            if (context3 != null) {
                controlViewHolder.getLayout().setOnTouchListener(new ToggleRangeBehavior$initialize$1(this, new GestureDetector(context3, toggleRangeGestureListener), toggleRangeGestureListener));
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException("context");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("status");
        throw null;
    }

    public void bind(ControlWithState controlWithState) {
        Intrinsics.checkParameterIsNotNull(controlWithState, "cws");
        Control control2 = controlWithState.getControl();
        if (control2 != null) {
            this.control = control2;
            ControlViewHolder controlViewHolder = this.cvh;
            String str = "cvh";
            if (controlViewHolder != null) {
                this.statusExtra = controlViewHolder.getStatusExtra();
                TextView textView = this.status;
                if (textView != null) {
                    Control control3 = this.control;
                    String str2 = "control";
                    if (control3 != null) {
                        textView.setText(control3.getStatusText());
                        ControlViewHolder controlViewHolder2 = this.cvh;
                        if (controlViewHolder2 != null) {
                            Drawable background = controlViewHolder2.getLayout().getBackground();
                            if (background != null) {
                                Drawable findDrawableByLayerId = ((LayerDrawable) background).findDrawableByLayerId(C2011R$id.clip_layer);
                                Intrinsics.checkExpressionValueIsNotNull(findDrawableByLayerId, "ld.findDrawableByLayerId(R.id.clip_layer)");
                                this.clipLayer = findDrawableByLayerId;
                                if (findDrawableByLayerId != null) {
                                    findDrawableByLayerId.setLevel(0);
                                    Control control4 = this.control;
                                    if (control4 != null) {
                                        ToggleRangeTemplate controlTemplate = control4.getControlTemplate();
                                        if (controlTemplate != null) {
                                            ToggleRangeTemplate toggleRangeTemplate = controlTemplate;
                                            this.template = toggleRangeTemplate;
                                            String str3 = "template";
                                            if (toggleRangeTemplate != null) {
                                                RangeTemplate range = toggleRangeTemplate.getRange();
                                                Intrinsics.checkExpressionValueIsNotNull(range, "template.getRange()");
                                                this.rangeTemplate = range;
                                                ToggleRangeTemplate toggleRangeTemplate2 = this.template;
                                                if (toggleRangeTemplate2 != null) {
                                                    boolean isChecked = toggleRangeTemplate2.isChecked();
                                                    RangeTemplate rangeTemplate2 = this.rangeTemplate;
                                                    String str4 = "rangeTemplate";
                                                    if (rangeTemplate2 != null) {
                                                        float currentValue = rangeTemplate2.getCurrentValue();
                                                        RangeTemplate rangeTemplate3 = this.rangeTemplate;
                                                        if (rangeTemplate3 != null) {
                                                            float maxValue = rangeTemplate3.getMaxValue();
                                                            RangeTemplate rangeTemplate4 = this.rangeTemplate;
                                                            if (rangeTemplate4 != null) {
                                                                updateRange(currentValue / (maxValue - rangeTemplate4.getMinValue()), isChecked);
                                                                ControlViewHolder controlViewHolder3 = this.cvh;
                                                                if (controlViewHolder3 != null) {
                                                                    ControlViewHolder.m29x1a61c355(controlViewHolder3, isChecked, 0, 2, null);
                                                                    ControlViewHolder controlViewHolder4 = this.cvh;
                                                                    if (controlViewHolder4 != null) {
                                                                        controlViewHolder4.getLayout().setAccessibilityDelegate(new ToggleRangeBehavior$bind$1(this));
                                                                    } else {
                                                                        Intrinsics.throwUninitializedPropertyAccessException(str);
                                                                        throw null;
                                                                    }
                                                                } else {
                                                                    Intrinsics.throwUninitializedPropertyAccessException(str);
                                                                    throw null;
                                                                }
                                                            } else {
                                                                Intrinsics.throwUninitializedPropertyAccessException(str4);
                                                                throw null;
                                                            }
                                                        } else {
                                                            Intrinsics.throwUninitializedPropertyAccessException(str4);
                                                            throw null;
                                                        }
                                                    } else {
                                                        Intrinsics.throwUninitializedPropertyAccessException(str4);
                                                        throw null;
                                                    }
                                                } else {
                                                    Intrinsics.throwUninitializedPropertyAccessException(str3);
                                                    throw null;
                                                }
                                            } else {
                                                Intrinsics.throwUninitializedPropertyAccessException(str3);
                                                throw null;
                                            }
                                        } else {
                                            throw new TypeCastException("null cannot be cast to non-null type android.service.controls.templates.ToggleRangeTemplate");
                                        }
                                    } else {
                                        Intrinsics.throwUninitializedPropertyAccessException(str2);
                                        throw null;
                                    }
                                } else {
                                    Intrinsics.throwUninitializedPropertyAccessException("clipLayer");
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
                    Intrinsics.throwUninitializedPropertyAccessException("status");
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

    public final void beginUpdateRange() {
        TextView textView = this.status;
        if (textView != null) {
            textView.setVisibility(8);
            TextView textView2 = this.statusExtra;
            if (textView2 != null) {
                Context context2 = this.context;
                if (context2 != null) {
                    textView2.setTextSize(0, (float) context2.getResources().getDimensionPixelSize(C2009R$dimen.control_status_expanded));
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException("context");
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("statusExtra");
                throw null;
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("status");
            throw null;
        }
    }

    public final void updateRange(float f, boolean z) {
        int i = z ? (int) (((float) 10000) * f) : 0;
        Drawable drawable = this.clipLayer;
        String str = "clipLayer";
        if (drawable != null) {
            int max = Math.max(0, Math.min(10000, drawable.getLevel() + i));
            Drawable drawable2 = this.clipLayer;
            if (drawable2 != null) {
                drawable2.setLevel(max);
                String str2 = "statusExtra";
                if (z) {
                    Drawable drawable3 = this.clipLayer;
                    if (drawable3 != null) {
                        float levelToRangeValue = levelToRangeValue(drawable3.getLevel());
                        RangeTemplate rangeTemplate2 = this.rangeTemplate;
                        if (rangeTemplate2 != null) {
                            String format = format(rangeTemplate2.getFormatString().toString(), "%.1f", levelToRangeValue);
                            TextView textView = this.statusExtra;
                            if (textView != null) {
                                textView.setText(format);
                                TextView textView2 = this.statusExtra;
                                if (textView2 != null) {
                                    textView2.setVisibility(0);
                                } else {
                                    Intrinsics.throwUninitializedPropertyAccessException(str2);
                                    throw null;
                                }
                            } else {
                                Intrinsics.throwUninitializedPropertyAccessException(str2);
                                throw null;
                            }
                        } else {
                            Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
                            throw null;
                        }
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException(str);
                        throw null;
                    }
                } else {
                    TextView textView3 = this.statusExtra;
                    if (textView3 != null) {
                        textView3.setText("");
                        TextView textView4 = this.statusExtra;
                        if (textView4 != null) {
                            textView4.setVisibility(8);
                        } else {
                            Intrinsics.throwUninitializedPropertyAccessException(str2);
                            throw null;
                        }
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException(str2);
                        throw null;
                    }
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException(str);
                throw null;
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException(str);
            throw null;
        }
    }

    private final String format(String str, String str2, float f) {
        try {
            StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
            String format = String.format(str, Arrays.copyOf(new Object[]{Float.valueOf(f)}, 1));
            Intrinsics.checkExpressionValueIsNotNull(format, "java.lang.String.format(format, *args)");
            return format;
        } catch (IllegalFormatException e) {
            Log.w("ControlsUiController", "Illegal format in range template", e);
            String str3 = "";
            return Intrinsics.areEqual((Object) str2, (Object) str3) ? str3 : format(str2, str3, f);
        }
    }

    /* access modifiers changed from: private */
    public final float levelToRangeValue(int i) {
        float f = ((float) i) / ((float) 10000);
        RangeTemplate rangeTemplate2 = this.rangeTemplate;
        String str = "rangeTemplate";
        if (rangeTemplate2 != null) {
            float minValue = rangeTemplate2.getMinValue();
            RangeTemplate rangeTemplate3 = this.rangeTemplate;
            if (rangeTemplate3 != null) {
                float maxValue = rangeTemplate3.getMaxValue();
                RangeTemplate rangeTemplate4 = this.rangeTemplate;
                if (rangeTemplate4 != null) {
                    return minValue + (f * (maxValue - rangeTemplate4.getMinValue()));
                }
                Intrinsics.throwUninitializedPropertyAccessException(str);
                throw null;
            }
            Intrinsics.throwUninitializedPropertyAccessException(str);
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException(str);
        throw null;
    }

    public final void endUpdateRange() {
        TextView textView = this.statusExtra;
        if (textView != null) {
            Context context2 = this.context;
            if (context2 != null) {
                textView.setTextSize(0, (float) context2.getResources().getDimensionPixelSize(C2009R$dimen.control_status_normal));
                TextView textView2 = this.status;
                if (textView2 != null) {
                    textView2.setVisibility(0);
                    ControlViewHolder controlViewHolder = this.cvh;
                    if (controlViewHolder != null) {
                        RangeTemplate rangeTemplate2 = this.rangeTemplate;
                        if (rangeTemplate2 != null) {
                            String templateId = rangeTemplate2.getTemplateId();
                            Drawable drawable = this.clipLayer;
                            if (drawable != null) {
                                controlViewHolder.action(new FloatAction(templateId, findNearestStep(levelToRangeValue(drawable.getLevel()))));
                            } else {
                                Intrinsics.throwUninitializedPropertyAccessException("clipLayer");
                                throw null;
                            }
                        } else {
                            Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
                            throw null;
                        }
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException("cvh");
                        throw null;
                    }
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException("status");
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("context");
                throw null;
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("statusExtra");
            throw null;
        }
    }

    public final float findNearestStep(float f) {
        RangeTemplate rangeTemplate2 = this.rangeTemplate;
        String str = "rangeTemplate";
        if (rangeTemplate2 != null) {
            float minValue = rangeTemplate2.getMinValue();
            float f2 = 1000.0f;
            while (true) {
                RangeTemplate rangeTemplate3 = this.rangeTemplate;
                if (rangeTemplate3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    throw null;
                } else if (minValue <= rangeTemplate3.getMaxValue()) {
                    float abs = Math.abs(f - minValue);
                    if (abs < f2) {
                        RangeTemplate rangeTemplate4 = this.rangeTemplate;
                        if (rangeTemplate4 != null) {
                            minValue += rangeTemplate4.getStepValue();
                            f2 = abs;
                        } else {
                            Intrinsics.throwUninitializedPropertyAccessException(str);
                            throw null;
                        }
                    } else {
                        RangeTemplate rangeTemplate5 = this.rangeTemplate;
                        if (rangeTemplate5 != null) {
                            return minValue - rangeTemplate5.getStepValue();
                        }
                        Intrinsics.throwUninitializedPropertyAccessException(str);
                        throw null;
                    }
                } else {
                    RangeTemplate rangeTemplate6 = this.rangeTemplate;
                    if (rangeTemplate6 != null) {
                        return rangeTemplate6.getMaxValue();
                    }
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    throw null;
                }
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException(str);
            throw null;
        }
    }
}
