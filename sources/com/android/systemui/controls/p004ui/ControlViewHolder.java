package com.android.systemui.controls.p004ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.service.controls.Control;
import android.service.controls.actions.ControlAction;
import android.service.controls.templates.ControlTemplate;
import android.service.controls.templates.StatelessTemplate;
import android.service.controls.templates.TemperatureControlTemplate;
import android.service.controls.templates.ToggleRangeTemplate;
import android.service.controls.templates.ToggleTemplate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.C2008R$color;
import com.android.systemui.C2011R$id;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.controls.p004ui.RenderInfo.Companion;
import com.android.systemui.util.concurrency.DelayableExecutor;
import kotlin.Pair;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;

/* renamed from: com.android.systemui.controls.ui.ControlViewHolder */
/* compiled from: ControlViewHolder.kt */
public final class ControlViewHolder {
    private Behavior behavior;
    private Runnable cancelUpdate;
    private final ClipDrawable clipLayer;
    private final Context context;
    private final ControlsController controlsController;
    public ControlWithState cws;
    private final ImageView icon;
    private ControlAction lastAction;
    private final ViewGroup layout;
    private final TextView status;
    private final TextView statusExtra;
    private final TextView subtitle;
    private final TextView title;
    private final DelayableExecutor uiExecutor;

    public final void actionResponse(int i) {
    }

    public ControlViewHolder(ViewGroup viewGroup, ControlsController controlsController2, DelayableExecutor delayableExecutor, DelayableExecutor delayableExecutor2) {
        Intrinsics.checkParameterIsNotNull(viewGroup, "layout");
        Intrinsics.checkParameterIsNotNull(controlsController2, "controlsController");
        Intrinsics.checkParameterIsNotNull(delayableExecutor, "uiExecutor");
        Intrinsics.checkParameterIsNotNull(delayableExecutor2, "bgExecutor");
        this.layout = viewGroup;
        this.controlsController = controlsController2;
        this.uiExecutor = delayableExecutor;
        View requireViewById = viewGroup.requireViewById(C2011R$id.icon);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "layout.requireViewById(R.id.icon)");
        this.icon = (ImageView) requireViewById;
        View requireViewById2 = this.layout.requireViewById(C2011R$id.status);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById2, "layout.requireViewById(R.id.status)");
        this.status = (TextView) requireViewById2;
        View requireViewById3 = this.layout.requireViewById(C2011R$id.status_extra);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById3, "layout.requireViewById(R.id.status_extra)");
        this.statusExtra = (TextView) requireViewById3;
        View requireViewById4 = this.layout.requireViewById(C2011R$id.title);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById4, "layout.requireViewById(R.id.title)");
        this.title = (TextView) requireViewById4;
        View requireViewById5 = this.layout.requireViewById(C2011R$id.subtitle);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById5, "layout.requireViewById(R.id.subtitle)");
        this.subtitle = (TextView) requireViewById5;
        Context context2 = this.layout.getContext();
        Intrinsics.checkExpressionValueIsNotNull(context2, "layout.getContext()");
        this.context = context2;
        Drawable background = this.layout.getBackground();
        if (background != null) {
            LayerDrawable layerDrawable = (LayerDrawable) background;
            layerDrawable.mutate();
            Drawable findDrawableByLayerId = layerDrawable.findDrawableByLayerId(C2011R$id.clip_layer);
            if (findDrawableByLayerId != null) {
                this.clipLayer = (ClipDrawable) findDrawableByLayerId;
                return;
            }
            throw new TypeCastException("null cannot be cast to non-null type android.graphics.drawable.ClipDrawable");
        }
        throw new TypeCastException("null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
    }

    public final ViewGroup getLayout() {
        return this.layout;
    }

    public final TextView getStatus() {
        return this.status;
    }

    public final TextView getStatusExtra() {
        return this.statusExtra;
    }

    public final Context getContext() {
        return this.context;
    }

    public final ClipDrawable getClipLayer() {
        return this.clipLayer;
    }

    public final ControlWithState getCws() {
        ControlWithState controlWithState = this.cws;
        if (controlWithState != null) {
            return controlWithState;
        }
        Intrinsics.throwUninitializedPropertyAccessException("cws");
        throw null;
    }

    public final ControlAction getLastAction() {
        return this.lastAction;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x00a1, code lost:
        if ((!kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) kotlin.jvm.internal.Reflection.getOrCreateKotlinClass(r1.getClass()), (java.lang.Object) r0)) != false) goto L_0x00a8;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void bindData(com.android.systemui.controls.p004ui.ControlWithState r6) {
        /*
            r5 = this;
            java.lang.String r0 = "cws"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r6, r0)
            r5.cws = r6
            java.lang.Runnable r0 = r5.cancelUpdate
            if (r0 == 0) goto L_0x000e
            r0.run()
        L_0x000e:
            android.service.controls.Control r0 = r6.getControl()
            if (r0 == 0) goto L_0x0038
            android.widget.TextView r1 = r5.title
            java.lang.CharSequence r2 = r0.getTitle()
            r1.setText(r2)
            android.widget.TextView r1 = r5.subtitle
            java.lang.CharSequence r2 = r0.getSubtitle()
            r1.setText(r2)
            kotlin.Pair r1 = new kotlin.Pair
            int r2 = r0.getStatus()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            android.service.controls.templates.ControlTemplate r0 = r0.getControlTemplate()
            r1.<init>(r2, r0)
            goto L_0x005e
        L_0x0038:
            android.widget.TextView r0 = r5.title
            com.android.systemui.controls.controller.ControlInfo r1 = r6.getCi()
            java.lang.CharSequence r1 = r1.getControlTitle()
            r0.setText(r1)
            android.widget.TextView r0 = r5.subtitle
            com.android.systemui.controls.controller.ControlInfo r1 = r6.getCi()
            java.lang.CharSequence r1 = r1.getControlSubtitle()
            r0.setText(r1)
            kotlin.Pair r1 = new kotlin.Pair
            r0 = 0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            android.service.controls.templates.ControlTemplate r2 = android.service.controls.templates.ControlTemplate.NO_TEMPLATE
            r1.<init>(r0, r2)
        L_0x005e:
            java.lang.Object r0 = r1.component1()
            java.lang.Number r0 = (java.lang.Number) r0
            int r0 = r0.intValue()
            java.lang.Object r1 = r1.component2()
            android.service.controls.templates.ControlTemplate r1 = (android.service.controls.templates.ControlTemplate) r1
            android.service.controls.Control r2 = r6.getControl()
            r3 = 1
            if (r2 == 0) goto L_0x0084
            android.view.ViewGroup r2 = r5.layout
            r2.setClickable(r3)
            android.view.ViewGroup r2 = r5.layout
            com.android.systemui.controls.ui.ControlViewHolder$bindData$$inlined$let$lambda$1 r4 = new com.android.systemui.controls.ui.ControlViewHolder$bindData$$inlined$let$lambda$1
            r4.<init>(r5)
            r2.setOnLongClickListener(r4)
        L_0x0084:
            java.lang.String r2 = "template"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r1, r2)
            kotlin.reflect.KClass r0 = r5.findBehavior(r0, r1)
            com.android.systemui.controls.ui.Behavior r1 = r5.behavior
            r2 = 0
            if (r1 == 0) goto L_0x00a8
            if (r1 == 0) goto L_0x00a4
            java.lang.Class r1 = r1.getClass()
            kotlin.reflect.KClass r1 = kotlin.jvm.internal.Reflection.getOrCreateKotlinClass(r1)
            boolean r1 = kotlin.jvm.internal.Intrinsics.areEqual(r1, r0)
            r1 = r1 ^ r3
            if (r1 == 0) goto L_0x00be
            goto L_0x00a8
        L_0x00a4:
            kotlin.jvm.internal.Intrinsics.throwNpe()
            throw r2
        L_0x00a8:
            java.lang.Class r0 = kotlin.jvm.JvmClassMappingKt.getJavaClass(r0)
            java.lang.Object r0 = r0.newInstance()
            com.android.systemui.controls.ui.Behavior r0 = (com.android.systemui.controls.p004ui.Behavior) r0
            r5.behavior = r0
            if (r0 == 0) goto L_0x00b9
            r0.initialize(r5)
        L_0x00b9:
            android.view.ViewGroup r0 = r5.layout
            r0.setAccessibilityDelegate(r2)
        L_0x00be:
            com.android.systemui.controls.ui.Behavior r0 = r5.behavior
            if (r0 == 0) goto L_0x00c5
            r0.bind(r6)
        L_0x00c5:
            android.view.ViewGroup r6 = r5.layout
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            android.widget.TextView r1 = r5.title
            java.lang.CharSequence r1 = r1.getText()
            r0.append(r1)
            r1 = 32
            r0.append(r1)
            android.widget.TextView r2 = r5.subtitle
            java.lang.CharSequence r2 = r2.getText()
            r0.append(r2)
            r0.append(r1)
            android.widget.TextView r2 = r5.status
            java.lang.CharSequence r2 = r2.getText()
            r0.append(r2)
            r0.append(r1)
            android.widget.TextView r5 = r5.statusExtra
            java.lang.CharSequence r5 = r5.getText()
            r0.append(r5)
            java.lang.String r5 = r0.toString()
            r6.setContentDescription(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.p004ui.ControlViewHolder.bindData(com.android.systemui.controls.ui.ControlWithState):void");
    }

    public final void setTransientStatus(String str) {
        Intrinsics.checkParameterIsNotNull(str, "tempStatus");
        this.cancelUpdate = this.uiExecutor.executeDelayed(new ControlViewHolder$setTransientStatus$1(this, this.status.getText(), this.statusExtra.getText()), 3000);
        this.status.setText(str);
        this.statusExtra.setText("");
    }

    public final void action(ControlAction controlAction) {
        Intrinsics.checkParameterIsNotNull(controlAction, "action");
        this.lastAction = controlAction;
        ControlsController controlsController2 = this.controlsController;
        ControlWithState controlWithState = this.cws;
        String str = "cws";
        if (controlWithState != null) {
            ComponentName componentName = controlWithState.getComponentName();
            ControlWithState controlWithState2 = this.cws;
            if (controlWithState2 != null) {
                controlsController2.action(componentName, controlWithState2.getCi(), controlAction);
            } else {
                Intrinsics.throwUninitializedPropertyAccessException(str);
                throw null;
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException(str);
            throw null;
        }
    }

    private final KClass<? extends Behavior> findBehavior(int i, ControlTemplate controlTemplate) {
        if (i == 0) {
            return Reflection.getOrCreateKotlinClass(UnknownBehavior.class);
        }
        if (controlTemplate instanceof ToggleTemplate) {
            return Reflection.getOrCreateKotlinClass(ToggleBehavior.class);
        }
        if (controlTemplate instanceof StatelessTemplate) {
            return Reflection.getOrCreateKotlinClass(TouchBehavior.class);
        }
        if (controlTemplate instanceof ToggleRangeTemplate) {
            return Reflection.getOrCreateKotlinClass(ToggleRangeBehavior.class);
        }
        if (controlTemplate instanceof TemperatureControlTemplate) {
            return Reflection.getOrCreateKotlinClass(TemperatureControlBehavior.class);
        }
        return Reflection.getOrCreateKotlinClass(DefaultBehavior.class);
    }

    /* renamed from: applyRenderInfo$frameworks__base__packages__SystemUI__android_common__SystemUI_core$default */
    public static /* synthetic */ void m29x1a61c355(ControlViewHolder controlViewHolder, boolean z, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        controlViewHolder.mo11110x3918d5b8(z, i);
    }

    /* renamed from: applyRenderInfo$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo11110x3918d5b8(boolean z, int i) {
        int deviceType;
        Pair pair;
        setEnabled(z);
        ControlWithState controlWithState = this.cws;
        String str = "cws";
        if (controlWithState != null) {
            Control control = controlWithState.getControl();
            if (control != null) {
                deviceType = control.getDeviceType();
            } else {
                ControlWithState controlWithState2 = this.cws;
                if (controlWithState2 != null) {
                    deviceType = controlWithState2.getCi().getDeviceType();
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    throw null;
                }
            }
            int i2 = deviceType;
            Companion companion = RenderInfo.Companion;
            Context context2 = this.context;
            ControlWithState controlWithState3 = this.cws;
            if (controlWithState3 != null) {
                RenderInfo lookup = companion.lookup(context2, controlWithState3.getComponentName(), i2, z, i);
                ColorStateList colorStateList = this.context.getResources().getColorStateList(lookup.getForeground(), this.context.getTheme());
                if (z) {
                    pair = new Pair(Integer.valueOf(lookup.getEnabledBackground()), Integer.valueOf(51));
                } else {
                    pair = new Pair(Integer.valueOf(C2008R$color.control_default_background), Integer.valueOf(255));
                }
                int intValue = ((Number) pair.component1()).intValue();
                int intValue2 = ((Number) pair.component2()).intValue();
                this.status.setTextColor(colorStateList);
                this.statusExtra.setTextColor(colorStateList);
                this.icon.setImageDrawable(lookup.getIcon());
                this.icon.setImageTintList(colorStateList);
                Drawable drawable = this.clipLayer.getDrawable();
                if (drawable != null) {
                    GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                    gradientDrawable.setColor(this.context.getResources().getColor(intValue, this.context.getTheme()));
                    gradientDrawable.setAlpha(intValue2);
                    return;
                }
                throw new TypeCastException("null cannot be cast to non-null type android.graphics.drawable.GradientDrawable");
            }
            Intrinsics.throwUninitializedPropertyAccessException(str);
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException(str);
        throw null;
    }

    private final void setEnabled(boolean z) {
        this.status.setEnabled(z);
        this.icon.setEnabled(z);
    }
}
