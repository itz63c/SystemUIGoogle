package com.android.systemui.statusbar.notification;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.C2011R$id;
import com.android.systemui.Interpolators;
import java.util.Set;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;

/* compiled from: ViewGroupFadeHelper.kt */
public final class ViewGroupFadeHelper {
    public static final Companion Companion = new Companion(null);
    /* access modifiers changed from: private */
    public static final Function1<View, Boolean> visibilityIncluder = ViewGroupFadeHelper$Companion$visibilityIncluder$1.INSTANCE;

    /* compiled from: ViewGroupFadeHelper.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final void fadeOutAllChildrenExcept(ViewGroup viewGroup, View view, long j, Runnable runnable) {
            Intrinsics.checkParameterIsNotNull(viewGroup, "root");
            Intrinsics.checkParameterIsNotNull(view, "excludedView");
            Set<View> gatherViews = gatherViews(viewGroup, view, ViewGroupFadeHelper.visibilityIncluder);
            for (View view2 : gatherViews) {
                if (view2.getHasOverlappingRendering() && view2.getLayerType() == 0) {
                    view2.setLayerType(2, null);
                    view2.setTag(C2011R$id.view_group_fade_helper_hardware_layer, Boolean.TRUE);
                }
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            Intrinsics.checkExpressionValueIsNotNull(ofFloat, "this");
            ofFloat.setDuration(j);
            ofFloat.setInterpolator(Interpolators.ALPHA_OUT);
            long j2 = j;
            ViewGroup viewGroup2 = viewGroup;
            Set set = gatherViews;
            Runnable runnable2 = runnable;
            C1190x6ee60e02 viewGroupFadeHelper$Companion$fadeOutAllChildrenExcept$$inlined$apply$lambda$1 = new C1190x6ee60e02(j2, viewGroup2, set, runnable2);
            ofFloat.addUpdateListener(viewGroupFadeHelper$Companion$fadeOutAllChildrenExcept$$inlined$apply$lambda$1);
            C1191x6ee60e03 viewGroupFadeHelper$Companion$fadeOutAllChildrenExcept$$inlined$apply$lambda$2 = new C1191x6ee60e03(j2, viewGroup2, set, runnable2);
            ofFloat.addListener(viewGroupFadeHelper$Companion$fadeOutAllChildrenExcept$$inlined$apply$lambda$2);
            ofFloat.start();
            viewGroup.setTag(C2011R$id.view_group_fade_helper_modified_views, gatherViews);
            viewGroup.setTag(C2011R$id.view_group_fade_helper_animator, ofFloat);
        }

        /* JADX WARNING: type inference failed for: r8v1 */
        /* JADX WARNING: type inference failed for: r0v2, types: [android.view.ViewGroup] */
        /* JADX WARNING: type inference failed for: r5v0 */
        /* JADX WARNING: type inference failed for: r0v3, types: [java.lang.Object] */
        /* JADX WARNING: type inference failed for: r8v2, types: [java.lang.Object, android.view.ViewGroup] */
        /* JADX WARNING: type inference failed for: r8v3 */
        /* JADX WARNING: type inference failed for: r8v4 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 5 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private final java.util.Set<android.view.View> gatherViews(android.view.ViewGroup r7, android.view.View r8, kotlin.jvm.functions.Function1<? super android.view.View, java.lang.Boolean> r9) {
            /*
                r6 = this;
                java.util.LinkedHashSet r6 = new java.util.LinkedHashSet
                r6.<init>()
                android.view.ViewParent r0 = r8.getParent()
                android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            L_0x000b:
                r5 = r0
                r0 = r8
                r8 = r5
                if (r8 == 0) goto L_0x0048
                r1 = 0
                int r2 = r8.getChildCount()
            L_0x0015:
                if (r1 >= r2) goto L_0x003a
                android.view.View r3 = r8.getChildAt(r1)
                java.lang.String r4 = "child"
                kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r3, r4)
                java.lang.Object r4 = r9.invoke(r3)
                java.lang.Boolean r4 = (java.lang.Boolean) r4
                boolean r4 = r4.booleanValue()
                if (r4 == 0) goto L_0x0037
                boolean r4 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r3)
                r4 = r4 ^ 1
                if (r4 == 0) goto L_0x0037
                r6.add(r3)
            L_0x0037:
                int r1 = r1 + 1
                goto L_0x0015
            L_0x003a:
                boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r8, r7)
                if (r0 == 0) goto L_0x0041
                goto L_0x0048
            L_0x0041:
                android.view.ViewParent r0 = r8.getParent()
                android.view.ViewGroup r0 = (android.view.ViewGroup) r0
                goto L_0x000b
            L_0x0048:
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.ViewGroupFadeHelper.Companion.gatherViews(android.view.ViewGroup, android.view.View, kotlin.jvm.functions.Function1):java.util.Set");
        }

        public final void reset(ViewGroup viewGroup) {
            Intrinsics.checkParameterIsNotNull(viewGroup, "root");
            Set<View> asMutableSet = TypeIntrinsics.asMutableSet(viewGroup.getTag(C2011R$id.view_group_fade_helper_modified_views));
            Animator animator = (Animator) viewGroup.getTag(C2011R$id.view_group_fade_helper_animator);
            if (asMutableSet != null && animator != null) {
                animator.cancel();
                Float f = (Float) viewGroup.getTag(C2011R$id.view_group_fade_helper_previous_value_tag);
                for (View view : asMutableSet) {
                    Float f2 = (Float) view.getTag(C2011R$id.view_group_fade_helper_restore_tag);
                    if (f2 != null) {
                        if (Intrinsics.areEqual(f, view.getAlpha())) {
                            view.setAlpha(f2.floatValue());
                        }
                        if (Intrinsics.areEqual((Object) (Boolean) view.getTag(C2011R$id.view_group_fade_helper_hardware_layer), (Object) Boolean.TRUE)) {
                            view.setLayerType(0, null);
                            view.setTag(C2011R$id.view_group_fade_helper_hardware_layer, null);
                        }
                        view.setTag(C2011R$id.view_group_fade_helper_restore_tag, null);
                    }
                }
                viewGroup.setTag(C2011R$id.view_group_fade_helper_modified_views, null);
                viewGroup.setTag(C2011R$id.view_group_fade_helper_previous_value_tag, null);
                viewGroup.setTag(C2011R$id.view_group_fade_helper_animator, null);
            }
        }
    }

    public static final void fadeOutAllChildrenExcept(ViewGroup viewGroup, View view, long j, Runnable runnable) {
        Companion.fadeOutAllChildrenExcept(viewGroup, view, j, runnable);
    }

    public static final void reset(ViewGroup viewGroup) {
        Companion.reset(viewGroup);
    }
}
