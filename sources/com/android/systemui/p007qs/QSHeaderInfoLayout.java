package com.android.systemui.p007qs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.android.systemui.C2011R$id;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.qs.QSHeaderInfoLayout */
/* compiled from: QSHeaderInfoLayout.kt */
public final class QSHeaderInfoLayout extends FrameLayout {
    private View alarmContainer;
    private final Location location;
    private View ringerContainer;
    private View statusSeparator;

    /* renamed from: com.android.systemui.qs.QSHeaderInfoLayout$Location */
    /* compiled from: QSHeaderInfoLayout.kt */
    private static final class Location {
        private int left;
        private int right;

        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0012, code lost:
            if (r2.right == r3.right) goto L_0x0017;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean equals(java.lang.Object r3) {
            /*
                r2 = this;
                if (r2 == r3) goto L_0x0017
                boolean r0 = r3 instanceof com.android.systemui.p007qs.QSHeaderInfoLayout.Location
                if (r0 == 0) goto L_0x0015
                com.android.systemui.qs.QSHeaderInfoLayout$Location r3 = (com.android.systemui.p007qs.QSHeaderInfoLayout.Location) r3
                int r0 = r2.left
                int r1 = r3.left
                if (r0 != r1) goto L_0x0015
                int r2 = r2.right
                int r3 = r3.right
                if (r2 != r3) goto L_0x0015
                goto L_0x0017
            L_0x0015:
                r2 = 0
                return r2
            L_0x0017:
                r2 = 1
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p007qs.QSHeaderInfoLayout.Location.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            return (Integer.hashCode(this.left) * 31) + Integer.hashCode(this.right);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Location(left=");
            sb.append(this.left);
            sb.append(", right=");
            sb.append(this.right);
            sb.append(")");
            return sb.toString();
        }

        public Location(int i, int i2) {
            this.left = i;
            this.right = i2;
        }

        public final int getLeft() {
            return this.left;
        }

        public final int getRight() {
            return this.right;
        }

        public final void setLocationFromOffset(int i, int i2, int i3, boolean z) {
            if (z) {
                int i4 = i - i2;
                this.left = i4 - i3;
                this.right = i4;
                return;
            }
            this.left = i2;
            this.right = i2 + i3;
        }
    }

    public QSHeaderInfoLayout(Context context) {
        this(context, null, 0, 0, 14, null);
    }

    public QSHeaderInfoLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0, 0, 12, null);
    }

    public QSHeaderInfoLayout(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0, 8, null);
    }

    public /* synthetic */ QSHeaderInfoLayout(Context context, AttributeSet attributeSet, int i, int i2, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        if ((i3 & 2) != 0) {
            attributeSet = null;
        }
        if ((i3 & 4) != 0) {
            i = 0;
        }
        if ((i3 & 8) != 0) {
            i2 = 0;
        }
        this(context, attributeSet, i, i2);
    }

    public QSHeaderInfoLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context, attributeSet, i, i2);
        this.location = new Location(0, 0);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        View findViewById = findViewById(C2011R$id.alarm_container);
        Intrinsics.checkExpressionValueIsNotNull(findViewById, "findViewById(R.id.alarm_container)");
        this.alarmContainer = findViewById;
        View findViewById2 = findViewById(C2011R$id.ringer_container);
        Intrinsics.checkExpressionValueIsNotNull(findViewById2, "findViewById(R.id.ringer_container)");
        this.ringerContainer = findViewById2;
        View findViewById3 = findViewById(C2011R$id.status_separator);
        Intrinsics.checkExpressionValueIsNotNull(findViewById3, "findViewById(R.id.status_separator)");
        this.statusSeparator = findViewById3;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        View view = this.statusSeparator;
        String str = "statusSeparator";
        if (view == null) {
            Intrinsics.throwUninitializedPropertyAccessException(str);
            throw null;
        } else if (view.getVisibility() == 8) {
            super.onLayout(z, i, i2, i3, i4);
        } else {
            boolean isLayoutRtl = isLayoutRtl();
            int i5 = i3 - i;
            int i6 = i4 - i2;
            View view2 = this.alarmContainer;
            if (view2 != null) {
                int layoutView = 0 + layoutView(view2, i5, i6, 0, isLayoutRtl);
                View view3 = this.statusSeparator;
                if (view3 != null) {
                    int layoutView2 = layoutView(view3, i5, i6, layoutView, isLayoutRtl) + layoutView;
                    View view4 = this.ringerContainer;
                    if (view4 != null) {
                        layoutView(view4, i5, i6, layoutView2, isLayoutRtl);
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException("ringerContainer");
                        throw null;
                    }
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("alarmContainer");
                throw null;
            }
        }
    }

    private final int layoutView(View view, int i, int i2, int i3, boolean z) {
        this.location.setLocationFromOffset(i, i3, view.getMeasuredWidth(), z);
        view.layout(this.location.getLeft(), 0, this.location.getRight(), i2);
        return view.getMeasuredWidth();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), Integer.MIN_VALUE), i2);
        int size = MeasureSpec.getSize(i);
        View view = this.statusSeparator;
        String str = "statusSeparator";
        if (view != null) {
            if (view.getVisibility() != 8) {
                View view2 = this.alarmContainer;
                String str2 = "alarmContainer";
                if (view2 != null) {
                    int measuredWidth = view2.getMeasuredWidth();
                    View view3 = this.statusSeparator;
                    if (view3 != null) {
                        int measuredWidth2 = view3.getMeasuredWidth();
                        View view4 = this.ringerContainer;
                        String str3 = "ringerContainer";
                        if (view4 != null) {
                            int measuredWidth3 = view4.getMeasuredWidth();
                            int size2 = MeasureSpec.getSize(size) - measuredWidth2;
                            int i3 = size2 / 2;
                            if (measuredWidth < i3) {
                                View view5 = this.ringerContainer;
                                if (view5 != null) {
                                    measureChild(view5, MeasureSpec.makeMeasureSpec(Math.min(measuredWidth3, size2 - measuredWidth), Integer.MIN_VALUE), i2);
                                } else {
                                    Intrinsics.throwUninitializedPropertyAccessException(str3);
                                    throw null;
                                }
                            } else if (measuredWidth3 < i3) {
                                View view6 = this.alarmContainer;
                                if (view6 != null) {
                                    measureChild(view6, MeasureSpec.makeMeasureSpec(Math.min(measuredWidth, size2 - measuredWidth3), Integer.MIN_VALUE), i2);
                                } else {
                                    Intrinsics.throwUninitializedPropertyAccessException(str2);
                                    throw null;
                                }
                            } else {
                                View view7 = this.alarmContainer;
                                if (view7 != null) {
                                    measureChild(view7, MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), i2);
                                    View view8 = this.ringerContainer;
                                    if (view8 != null) {
                                        measureChild(view8, MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), i2);
                                    } else {
                                        Intrinsics.throwUninitializedPropertyAccessException(str3);
                                        throw null;
                                    }
                                } else {
                                    Intrinsics.throwUninitializedPropertyAccessException(str2);
                                    throw null;
                                }
                            }
                        } else {
                            Intrinsics.throwUninitializedPropertyAccessException(str3);
                            throw null;
                        }
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException(str);
                        throw null;
                    }
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException(str2);
                    throw null;
                }
            }
            setMeasuredDimension(size, getMeasuredHeight());
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException(str);
        throw null;
    }
}
