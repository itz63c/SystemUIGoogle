package androidx.constraintlayout.widget;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.support.constraint.R$styleable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.constraintlayout.solver.Metrics;
import androidx.constraintlayout.solver.widgets.ConstraintWidget;
import androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour;
import androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.solver.widgets.Guideline;
import java.util.ArrayList;
import java.util.HashMap;

public class ConstraintLayout extends ViewGroup {
    SparseArray<View> mChildrenByIds = new SparseArray<>();
    private ArrayList<ConstraintHelper> mConstraintHelpers = new ArrayList<>(4);
    private ConstraintSet mConstraintSet = null;
    private int mConstraintSetId = -1;
    private HashMap<String, Integer> mDesignIds = new HashMap<>();
    private boolean mDirtyHierarchy = true;
    private int mLastMeasureHeight = -1;
    int mLastMeasureHeightMode = 0;
    int mLastMeasureHeightSize = -1;
    private int mLastMeasureWidth = -1;
    int mLastMeasureWidthMode = 0;
    int mLastMeasureWidthSize = -1;
    ConstraintWidgetContainer mLayoutWidget = new ConstraintWidgetContainer();
    private int mMaxHeight = Integer.MAX_VALUE;
    private int mMaxWidth = Integer.MAX_VALUE;
    private Metrics mMetrics;
    private int mMinHeight = 0;
    private int mMinWidth = 0;
    private int mOptimizationLevel = 3;
    private final ArrayList<ConstraintWidget> mVariableDimensionsWidgets = new ArrayList<>(100);

    public static class LayoutParams extends MarginLayoutParams {
        public int baselineToBaseline = -1;
        public int bottomToBottom = -1;
        public int bottomToTop = -1;
        public float circleAngle = 0.0f;
        public int circleConstraint = -1;
        public int circleRadius = 0;
        public boolean constrainedHeight = false;
        public boolean constrainedWidth = false;
        public String dimensionRatio = null;
        int dimensionRatioSide = 1;
        public int editorAbsoluteX = -1;
        public int editorAbsoluteY = -1;
        public int endToEnd = -1;
        public int endToStart = -1;
        public int goneBottomMargin = -1;
        public int goneEndMargin = -1;
        public int goneLeftMargin = -1;
        public int goneRightMargin = -1;
        public int goneStartMargin = -1;
        public int goneTopMargin = -1;
        public int guideBegin = -1;
        public int guideEnd = -1;
        public float guidePercent = -1.0f;
        public boolean helped = false;
        public float horizontalBias = 0.5f;
        public int horizontalChainStyle = 0;
        boolean horizontalDimensionFixed = true;
        public float horizontalWeight = -1.0f;
        boolean isGuideline = false;
        boolean isHelper = false;
        boolean isInPlaceholder = false;
        public int leftToLeft = -1;
        public int leftToRight = -1;
        public int matchConstraintDefaultHeight = 0;
        public int matchConstraintDefaultWidth = 0;
        public int matchConstraintMaxHeight = 0;
        public int matchConstraintMaxWidth = 0;
        public int matchConstraintMinHeight = 0;
        public int matchConstraintMinWidth = 0;
        public float matchConstraintPercentHeight = 1.0f;
        public float matchConstraintPercentWidth = 1.0f;
        boolean needsBaseline = false;
        public int orientation = -1;
        int resolveGoneLeftMargin = -1;
        int resolveGoneRightMargin = -1;
        int resolvedGuideBegin;
        int resolvedGuideEnd;
        float resolvedGuidePercent;
        float resolvedHorizontalBias = 0.5f;
        int resolvedLeftToLeft = -1;
        int resolvedLeftToRight = -1;
        int resolvedRightToLeft = -1;
        int resolvedRightToRight = -1;
        public int rightToLeft = -1;
        public int rightToRight = -1;
        public int startToEnd = -1;
        public int startToStart = -1;
        public int topToBottom = -1;
        public int topToTop = -1;
        public float verticalBias = 0.5f;
        public int verticalChainStyle = 0;
        boolean verticalDimensionFixed = true;
        public float verticalWeight = -1.0f;
        ConstraintWidget widget = new ConstraintWidget();

        private static class Table {
            public static final SparseIntArray map;

            static {
                SparseIntArray sparseIntArray = new SparseIntArray();
                map = sparseIntArray;
                sparseIntArray.append(R$styleable.ConstraintLayout_Layout_layout_constraintLeft_toLeftOf, 8);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintLeft_toRightOf, 9);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintRight_toLeftOf, 10);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintRight_toRightOf, 11);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintTop_toTopOf, 12);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintTop_toBottomOf, 13);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintBottom_toTopOf, 14);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintBottom_toBottomOf, 15);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintBaseline_toBaselineOf, 16);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintCircle, 2);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintCircleRadius, 3);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintCircleAngle, 4);
                map.append(R$styleable.ConstraintLayout_Layout_layout_editor_absoluteX, 49);
                map.append(R$styleable.ConstraintLayout_Layout_layout_editor_absoluteY, 50);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintGuide_begin, 5);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintGuide_end, 6);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintGuide_percent, 7);
                map.append(R$styleable.ConstraintLayout_Layout_android_orientation, 1);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintStart_toEndOf, 17);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintStart_toStartOf, 18);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintEnd_toStartOf, 19);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintEnd_toEndOf, 20);
                map.append(R$styleable.ConstraintLayout_Layout_layout_goneMarginLeft, 21);
                map.append(R$styleable.ConstraintLayout_Layout_layout_goneMarginTop, 22);
                map.append(R$styleable.ConstraintLayout_Layout_layout_goneMarginRight, 23);
                map.append(R$styleable.ConstraintLayout_Layout_layout_goneMarginBottom, 24);
                map.append(R$styleable.ConstraintLayout_Layout_layout_goneMarginStart, 25);
                map.append(R$styleable.ConstraintLayout_Layout_layout_goneMarginEnd, 26);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHorizontal_bias, 29);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintVertical_bias, 30);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintDimensionRatio, 44);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHorizontal_weight, 45);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintVertical_weight, 46);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHorizontal_chainStyle, 47);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintVertical_chainStyle, 48);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constrainedWidth, 27);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constrainedHeight, 28);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintWidth_default, 31);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHeight_default, 32);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintWidth_min, 33);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintWidth_max, 34);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintWidth_percent, 35);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHeight_min, 36);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHeight_max, 37);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintHeight_percent, 38);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintLeft_creator, 39);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintTop_creator, 40);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintRight_creator, 41);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintBottom_creator, 42);
                map.append(R$styleable.ConstraintLayout_Layout_layout_constraintBaseline_creator, 43);
            }
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            int i;
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ConstraintLayout_Layout);
            int indexCount = obtainStyledAttributes.getIndexCount();
            for (int i2 = 0; i2 < indexCount; i2++) {
                int index = obtainStyledAttributes.getIndex(i2);
                int i3 = Table.map.get(index);
                String str = "ConstraintLayout";
                switch (i3) {
                    case 1:
                        this.orientation = obtainStyledAttributes.getInt(index, this.orientation);
                        break;
                    case 2:
                        int resourceId = obtainStyledAttributes.getResourceId(index, this.circleConstraint);
                        this.circleConstraint = resourceId;
                        if (resourceId != -1) {
                            break;
                        } else {
                            this.circleConstraint = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 3:
                        this.circleRadius = obtainStyledAttributes.getDimensionPixelSize(index, this.circleRadius);
                        break;
                    case 4:
                        float f = obtainStyledAttributes.getFloat(index, this.circleAngle) % 360.0f;
                        this.circleAngle = f;
                        if (f >= 0.0f) {
                            break;
                        } else {
                            this.circleAngle = (360.0f - f) % 360.0f;
                            break;
                        }
                    case 5:
                        this.guideBegin = obtainStyledAttributes.getDimensionPixelOffset(index, this.guideBegin);
                        break;
                    case 6:
                        this.guideEnd = obtainStyledAttributes.getDimensionPixelOffset(index, this.guideEnd);
                        break;
                    case 7:
                        this.guidePercent = obtainStyledAttributes.getFloat(index, this.guidePercent);
                        break;
                    case 8:
                        int resourceId2 = obtainStyledAttributes.getResourceId(index, this.leftToLeft);
                        this.leftToLeft = resourceId2;
                        if (resourceId2 != -1) {
                            break;
                        } else {
                            this.leftToLeft = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 9:
                        int resourceId3 = obtainStyledAttributes.getResourceId(index, this.leftToRight);
                        this.leftToRight = resourceId3;
                        if (resourceId3 != -1) {
                            break;
                        } else {
                            this.leftToRight = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 10:
                        int resourceId4 = obtainStyledAttributes.getResourceId(index, this.rightToLeft);
                        this.rightToLeft = resourceId4;
                        if (resourceId4 != -1) {
                            break;
                        } else {
                            this.rightToLeft = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 11:
                        int resourceId5 = obtainStyledAttributes.getResourceId(index, this.rightToRight);
                        this.rightToRight = resourceId5;
                        if (resourceId5 != -1) {
                            break;
                        } else {
                            this.rightToRight = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 12:
                        int resourceId6 = obtainStyledAttributes.getResourceId(index, this.topToTop);
                        this.topToTop = resourceId6;
                        if (resourceId6 != -1) {
                            break;
                        } else {
                            this.topToTop = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 13:
                        int resourceId7 = obtainStyledAttributes.getResourceId(index, this.topToBottom);
                        this.topToBottom = resourceId7;
                        if (resourceId7 != -1) {
                            break;
                        } else {
                            this.topToBottom = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 14:
                        int resourceId8 = obtainStyledAttributes.getResourceId(index, this.bottomToTop);
                        this.bottomToTop = resourceId8;
                        if (resourceId8 != -1) {
                            break;
                        } else {
                            this.bottomToTop = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 15:
                        int resourceId9 = obtainStyledAttributes.getResourceId(index, this.bottomToBottom);
                        this.bottomToBottom = resourceId9;
                        if (resourceId9 != -1) {
                            break;
                        } else {
                            this.bottomToBottom = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 16:
                        int resourceId10 = obtainStyledAttributes.getResourceId(index, this.baselineToBaseline);
                        this.baselineToBaseline = resourceId10;
                        if (resourceId10 != -1) {
                            break;
                        } else {
                            this.baselineToBaseline = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 17:
                        int resourceId11 = obtainStyledAttributes.getResourceId(index, this.startToEnd);
                        this.startToEnd = resourceId11;
                        if (resourceId11 != -1) {
                            break;
                        } else {
                            this.startToEnd = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 18:
                        int resourceId12 = obtainStyledAttributes.getResourceId(index, this.startToStart);
                        this.startToStart = resourceId12;
                        if (resourceId12 != -1) {
                            break;
                        } else {
                            this.startToStart = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 19:
                        int resourceId13 = obtainStyledAttributes.getResourceId(index, this.endToStart);
                        this.endToStart = resourceId13;
                        if (resourceId13 != -1) {
                            break;
                        } else {
                            this.endToStart = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 20:
                        int resourceId14 = obtainStyledAttributes.getResourceId(index, this.endToEnd);
                        this.endToEnd = resourceId14;
                        if (resourceId14 != -1) {
                            break;
                        } else {
                            this.endToEnd = obtainStyledAttributes.getInt(index, -1);
                            break;
                        }
                    case 21:
                        this.goneLeftMargin = obtainStyledAttributes.getDimensionPixelSize(index, this.goneLeftMargin);
                        break;
                    case 22:
                        this.goneTopMargin = obtainStyledAttributes.getDimensionPixelSize(index, this.goneTopMargin);
                        break;
                    case 23:
                        this.goneRightMargin = obtainStyledAttributes.getDimensionPixelSize(index, this.goneRightMargin);
                        break;
                    case 24:
                        this.goneBottomMargin = obtainStyledAttributes.getDimensionPixelSize(index, this.goneBottomMargin);
                        break;
                    case 25:
                        this.goneStartMargin = obtainStyledAttributes.getDimensionPixelSize(index, this.goneStartMargin);
                        break;
                    case 26:
                        this.goneEndMargin = obtainStyledAttributes.getDimensionPixelSize(index, this.goneEndMargin);
                        break;
                    case 27:
                        this.constrainedWidth = obtainStyledAttributes.getBoolean(index, this.constrainedWidth);
                        break;
                    case 28:
                        this.constrainedHeight = obtainStyledAttributes.getBoolean(index, this.constrainedHeight);
                        break;
                    case 29:
                        this.horizontalBias = obtainStyledAttributes.getFloat(index, this.horizontalBias);
                        break;
                    case 30:
                        this.verticalBias = obtainStyledAttributes.getFloat(index, this.verticalBias);
                        break;
                    case 31:
                        int i4 = obtainStyledAttributes.getInt(index, 0);
                        this.matchConstraintDefaultWidth = i4;
                        if (i4 != 1) {
                            break;
                        } else {
                            Log.e(str, "layout_constraintWidth_default=\"wrap\" is deprecated.\nUse layout_width=\"WRAP_CONTENT\" and layout_constrainedWidth=\"true\" instead.");
                            break;
                        }
                    case 32:
                        int i5 = obtainStyledAttributes.getInt(index, 0);
                        this.matchConstraintDefaultHeight = i5;
                        if (i5 != 1) {
                            break;
                        } else {
                            Log.e(str, "layout_constraintHeight_default=\"wrap\" is deprecated.\nUse layout_height=\"WRAP_CONTENT\" and layout_constrainedHeight=\"true\" instead.");
                            break;
                        }
                    case 33:
                        try {
                            this.matchConstraintMinWidth = obtainStyledAttributes.getDimensionPixelSize(index, this.matchConstraintMinWidth);
                            break;
                        } catch (Exception unused) {
                            if (obtainStyledAttributes.getInt(index, this.matchConstraintMinWidth) != -2) {
                                break;
                            } else {
                                this.matchConstraintMinWidth = -2;
                                break;
                            }
                        }
                    case 34:
                        try {
                            this.matchConstraintMaxWidth = obtainStyledAttributes.getDimensionPixelSize(index, this.matchConstraintMaxWidth);
                            break;
                        } catch (Exception unused2) {
                            if (obtainStyledAttributes.getInt(index, this.matchConstraintMaxWidth) != -2) {
                                break;
                            } else {
                                this.matchConstraintMaxWidth = -2;
                                break;
                            }
                        }
                    case 35:
                        this.matchConstraintPercentWidth = Math.max(0.0f, obtainStyledAttributes.getFloat(index, this.matchConstraintPercentWidth));
                        break;
                    case 36:
                        try {
                            this.matchConstraintMinHeight = obtainStyledAttributes.getDimensionPixelSize(index, this.matchConstraintMinHeight);
                            break;
                        } catch (Exception unused3) {
                            if (obtainStyledAttributes.getInt(index, this.matchConstraintMinHeight) != -2) {
                                break;
                            } else {
                                this.matchConstraintMinHeight = -2;
                                break;
                            }
                        }
                    case 37:
                        try {
                            this.matchConstraintMaxHeight = obtainStyledAttributes.getDimensionPixelSize(index, this.matchConstraintMaxHeight);
                            break;
                        } catch (Exception unused4) {
                            if (obtainStyledAttributes.getInt(index, this.matchConstraintMaxHeight) != -2) {
                                break;
                            } else {
                                this.matchConstraintMaxHeight = -2;
                                break;
                            }
                        }
                    case 38:
                        this.matchConstraintPercentHeight = Math.max(0.0f, obtainStyledAttributes.getFloat(index, this.matchConstraintPercentHeight));
                        break;
                    default:
                        switch (i3) {
                            case 44:
                                String string = obtainStyledAttributes.getString(index);
                                this.dimensionRatio = string;
                                this.dimensionRatioSide = -1;
                                if (string == null) {
                                    break;
                                } else {
                                    int length = string.length();
                                    int indexOf = this.dimensionRatio.indexOf(44);
                                    if (indexOf <= 0 || indexOf >= length - 1) {
                                        i = 0;
                                    } else {
                                        String substring = this.dimensionRatio.substring(0, indexOf);
                                        if (substring.equalsIgnoreCase("W")) {
                                            this.dimensionRatioSide = 0;
                                        } else if (substring.equalsIgnoreCase("H")) {
                                            this.dimensionRatioSide = 1;
                                        }
                                        i = indexOf + 1;
                                    }
                                    int indexOf2 = this.dimensionRatio.indexOf(58);
                                    if (indexOf2 >= 0 && indexOf2 < length - 1) {
                                        String substring2 = this.dimensionRatio.substring(i, indexOf2);
                                        String substring3 = this.dimensionRatio.substring(indexOf2 + 1);
                                        if (substring2.length() > 0 && substring3.length() > 0) {
                                            try {
                                                float parseFloat = Float.parseFloat(substring2);
                                                float parseFloat2 = Float.parseFloat(substring3);
                                                if (parseFloat > 0.0f && parseFloat2 > 0.0f) {
                                                    if (this.dimensionRatioSide != 1) {
                                                        Math.abs(parseFloat / parseFloat2);
                                                        break;
                                                    } else {
                                                        Math.abs(parseFloat2 / parseFloat);
                                                        break;
                                                    }
                                                }
                                            } catch (NumberFormatException unused5) {
                                                break;
                                            }
                                        }
                                    } else {
                                        String substring4 = this.dimensionRatio.substring(i);
                                        if (substring4.length() <= 0) {
                                            break;
                                        } else {
                                            Float.parseFloat(substring4);
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 45:
                                this.horizontalWeight = obtainStyledAttributes.getFloat(index, this.horizontalWeight);
                                break;
                            case 46:
                                this.verticalWeight = obtainStyledAttributes.getFloat(index, this.verticalWeight);
                                break;
                            case 47:
                                this.horizontalChainStyle = obtainStyledAttributes.getInt(index, 0);
                                break;
                            case 48:
                                this.verticalChainStyle = obtainStyledAttributes.getInt(index, 0);
                                break;
                            case 49:
                                this.editorAbsoluteX = obtainStyledAttributes.getDimensionPixelOffset(index, this.editorAbsoluteX);
                                break;
                            case 50:
                                this.editorAbsoluteY = obtainStyledAttributes.getDimensionPixelOffset(index, this.editorAbsoluteY);
                                break;
                        }
                }
            }
            obtainStyledAttributes.recycle();
            validate();
        }

        public void validate() {
            this.isGuideline = false;
            this.horizontalDimensionFixed = true;
            this.verticalDimensionFixed = true;
            if (this.width == -2 && this.constrainedWidth) {
                this.horizontalDimensionFixed = false;
                this.matchConstraintDefaultWidth = 1;
            }
            if (this.height == -2 && this.constrainedHeight) {
                this.verticalDimensionFixed = false;
                this.matchConstraintDefaultHeight = 1;
            }
            int i = this.width;
            if (i == 0 || i == -1) {
                this.horizontalDimensionFixed = false;
                if (this.width == 0 && this.matchConstraintDefaultWidth == 1) {
                    this.width = -2;
                    this.constrainedWidth = true;
                }
            }
            int i2 = this.height;
            if (i2 == 0 || i2 == -1) {
                this.verticalDimensionFixed = false;
                if (this.height == 0 && this.matchConstraintDefaultHeight == 1) {
                    this.height = -2;
                    this.constrainedHeight = true;
                }
            }
            if (this.guidePercent != -1.0f || this.guideBegin != -1 || this.guideEnd != -1) {
                this.isGuideline = true;
                this.horizontalDimensionFixed = true;
                this.verticalDimensionFixed = true;
                if (!(this.widget instanceof Guideline)) {
                    this.widget = new Guideline();
                }
                ((Guideline) this.widget).setOrientation(this.orientation);
            }
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        /* JADX WARNING: Removed duplicated region for block: B:14:0x004c  */
        /* JADX WARNING: Removed duplicated region for block: B:17:0x0053  */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x005a  */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x0060  */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x0066  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x007c  */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x0084  */
        @android.annotation.TargetApi(17)
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void resolveLayoutDirection(int r7) {
            /*
                r6 = this;
                int r0 = r6.leftMargin
                int r1 = r6.rightMargin
                super.resolveLayoutDirection(r7)
                r7 = -1
                r6.resolvedRightToLeft = r7
                r6.resolvedRightToRight = r7
                r6.resolvedLeftToLeft = r7
                r6.resolvedLeftToRight = r7
                r6.resolveGoneLeftMargin = r7
                r6.resolveGoneRightMargin = r7
                int r2 = r6.goneLeftMargin
                r6.resolveGoneLeftMargin = r2
                int r2 = r6.goneRightMargin
                r6.resolveGoneRightMargin = r2
                float r2 = r6.horizontalBias
                r6.resolvedHorizontalBias = r2
                int r2 = r6.guideBegin
                r6.resolvedGuideBegin = r2
                int r2 = r6.guideEnd
                r6.resolvedGuideEnd = r2
                float r2 = r6.guidePercent
                r6.resolvedGuidePercent = r2
                int r2 = r6.getLayoutDirection()
                r3 = 0
                r4 = 1
                if (r4 != r2) goto L_0x0036
                r2 = r4
                goto L_0x0037
            L_0x0036:
                r2 = r3
            L_0x0037:
                if (r2 == 0) goto L_0x009a
                int r2 = r6.startToEnd
                if (r2 == r7) goto L_0x0041
                r6.resolvedRightToLeft = r2
            L_0x003f:
                r3 = r4
                goto L_0x0048
            L_0x0041:
                int r2 = r6.startToStart
                if (r2 == r7) goto L_0x0048
                r6.resolvedRightToRight = r2
                goto L_0x003f
            L_0x0048:
                int r2 = r6.endToStart
                if (r2 == r7) goto L_0x004f
                r6.resolvedLeftToRight = r2
                r3 = r4
            L_0x004f:
                int r2 = r6.endToEnd
                if (r2 == r7) goto L_0x0056
                r6.resolvedLeftToLeft = r2
                r3 = r4
            L_0x0056:
                int r2 = r6.goneStartMargin
                if (r2 == r7) goto L_0x005c
                r6.resolveGoneRightMargin = r2
            L_0x005c:
                int r2 = r6.goneEndMargin
                if (r2 == r7) goto L_0x0062
                r6.resolveGoneLeftMargin = r2
            L_0x0062:
                r2 = 1065353216(0x3f800000, float:1.0)
                if (r3 == 0) goto L_0x006c
                float r3 = r6.horizontalBias
                float r3 = r2 - r3
                r6.resolvedHorizontalBias = r3
            L_0x006c:
                boolean r3 = r6.isGuideline
                if (r3 == 0) goto L_0x00be
                int r3 = r6.orientation
                if (r3 != r4) goto L_0x00be
                float r3 = r6.guidePercent
                r4 = -1082130432(0xffffffffbf800000, float:-1.0)
                int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r5 == 0) goto L_0x0084
                float r2 = r2 - r3
                r6.resolvedGuidePercent = r2
                r6.resolvedGuideBegin = r7
                r6.resolvedGuideEnd = r7
                goto L_0x00be
            L_0x0084:
                int r2 = r6.guideBegin
                if (r2 == r7) goto L_0x008f
                r6.resolvedGuideEnd = r2
                r6.resolvedGuideBegin = r7
                r6.resolvedGuidePercent = r4
                goto L_0x00be
            L_0x008f:
                int r2 = r6.guideEnd
                if (r2 == r7) goto L_0x00be
                r6.resolvedGuideBegin = r2
                r6.resolvedGuideEnd = r7
                r6.resolvedGuidePercent = r4
                goto L_0x00be
            L_0x009a:
                int r2 = r6.startToEnd
                if (r2 == r7) goto L_0x00a0
                r6.resolvedLeftToRight = r2
            L_0x00a0:
                int r2 = r6.startToStart
                if (r2 == r7) goto L_0x00a6
                r6.resolvedLeftToLeft = r2
            L_0x00a6:
                int r2 = r6.endToStart
                if (r2 == r7) goto L_0x00ac
                r6.resolvedRightToLeft = r2
            L_0x00ac:
                int r2 = r6.endToEnd
                if (r2 == r7) goto L_0x00b2
                r6.resolvedRightToRight = r2
            L_0x00b2:
                int r2 = r6.goneStartMargin
                if (r2 == r7) goto L_0x00b8
                r6.resolveGoneLeftMargin = r2
            L_0x00b8:
                int r2 = r6.goneEndMargin
                if (r2 == r7) goto L_0x00be
                r6.resolveGoneRightMargin = r2
            L_0x00be:
                int r2 = r6.endToStart
                if (r2 != r7) goto L_0x0108
                int r2 = r6.endToEnd
                if (r2 != r7) goto L_0x0108
                int r2 = r6.startToStart
                if (r2 != r7) goto L_0x0108
                int r2 = r6.startToEnd
                if (r2 != r7) goto L_0x0108
                int r2 = r6.rightToLeft
                if (r2 == r7) goto L_0x00dd
                r6.resolvedRightToLeft = r2
                int r2 = r6.rightMargin
                if (r2 > 0) goto L_0x00eb
                if (r1 <= 0) goto L_0x00eb
                r6.rightMargin = r1
                goto L_0x00eb
            L_0x00dd:
                int r2 = r6.rightToRight
                if (r2 == r7) goto L_0x00eb
                r6.resolvedRightToRight = r2
                int r2 = r6.rightMargin
                if (r2 > 0) goto L_0x00eb
                if (r1 <= 0) goto L_0x00eb
                r6.rightMargin = r1
            L_0x00eb:
                int r1 = r6.leftToLeft
                if (r1 == r7) goto L_0x00fa
                r6.resolvedLeftToLeft = r1
                int r7 = r6.leftMargin
                if (r7 > 0) goto L_0x0108
                if (r0 <= 0) goto L_0x0108
                r6.leftMargin = r0
                goto L_0x0108
            L_0x00fa:
                int r1 = r6.leftToRight
                if (r1 == r7) goto L_0x0108
                r6.resolvedLeftToRight = r1
                int r7 = r6.leftMargin
                if (r7 > 0) goto L_0x0108
                if (r0 <= 0) goto L_0x0108
                r6.leftMargin = r0
            L_0x0108:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.resolveLayoutDirection(int):void");
        }
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public void setDesignInformation(int i, Object obj, Object obj2) {
        if (i == 0 && (obj instanceof String) && (obj2 instanceof Integer)) {
            if (this.mDesignIds == null) {
                this.mDesignIds = new HashMap<>();
            }
            String str = (String) obj;
            int indexOf = str.indexOf("/");
            if (indexOf != -1) {
                str = str.substring(indexOf + 1);
            }
            this.mDesignIds.put(str, Integer.valueOf(((Integer) obj2).intValue()));
        }
    }

    public Object getDesignInformation(int i, Object obj) {
        if (i == 0 && (obj instanceof String)) {
            String str = (String) obj;
            HashMap<String, Integer> hashMap = this.mDesignIds;
            if (hashMap != null && hashMap.containsKey(str)) {
                return this.mDesignIds.get(str);
            }
        }
        return null;
    }

    public ConstraintLayout(Context context) {
        super(context);
        init(null);
    }

    public ConstraintLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet);
    }

    public ConstraintLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet);
    }

    public void setId(int i) {
        this.mChildrenByIds.remove(getId());
        super.setId(i);
        this.mChildrenByIds.put(getId(), this);
    }

    private void init(AttributeSet attributeSet) {
        this.mLayoutWidget.setCompanionWidget(this);
        this.mChildrenByIds.put(getId(), this);
        this.mConstraintSet = null;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.ConstraintLayout_Layout);
            int indexCount = obtainStyledAttributes.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = obtainStyledAttributes.getIndex(i);
                if (index == R$styleable.ConstraintLayout_Layout_android_minWidth) {
                    this.mMinWidth = obtainStyledAttributes.getDimensionPixelOffset(index, this.mMinWidth);
                } else if (index == R$styleable.ConstraintLayout_Layout_android_minHeight) {
                    this.mMinHeight = obtainStyledAttributes.getDimensionPixelOffset(index, this.mMinHeight);
                } else if (index == R$styleable.ConstraintLayout_Layout_android_maxWidth) {
                    this.mMaxWidth = obtainStyledAttributes.getDimensionPixelOffset(index, this.mMaxWidth);
                } else if (index == R$styleable.ConstraintLayout_Layout_android_maxHeight) {
                    this.mMaxHeight = obtainStyledAttributes.getDimensionPixelOffset(index, this.mMaxHeight);
                } else if (index == R$styleable.ConstraintLayout_Layout_layout_optimizationLevel) {
                    this.mOptimizationLevel = obtainStyledAttributes.getInt(index, this.mOptimizationLevel);
                } else if (index == R$styleable.ConstraintLayout_Layout_constraintSet) {
                    int resourceId = obtainStyledAttributes.getResourceId(index, 0);
                    try {
                        ConstraintSet constraintSet = new ConstraintSet();
                        this.mConstraintSet = constraintSet;
                        constraintSet.load(getContext(), resourceId);
                    } catch (NotFoundException unused) {
                        this.mConstraintSet = null;
                    }
                    this.mConstraintSetId = resourceId;
                }
            }
            obtainStyledAttributes.recycle();
        }
        this.mLayoutWidget.setOptimizationLevel(this.mOptimizationLevel);
    }

    public void addView(View view, int i, android.view.ViewGroup.LayoutParams layoutParams) {
        super.addView(view, i, layoutParams);
        if (VERSION.SDK_INT < 14) {
            onViewAdded(view);
        }
    }

    public void removeView(View view) {
        super.removeView(view);
        if (VERSION.SDK_INT < 14) {
            onViewRemoved(view);
        }
    }

    public void onViewAdded(View view) {
        if (VERSION.SDK_INT >= 14) {
            super.onViewAdded(view);
        }
        ConstraintWidget viewWidget = getViewWidget(view);
        if ((view instanceof Guideline) && !(viewWidget instanceof Guideline)) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            Guideline guideline = new Guideline();
            layoutParams.widget = guideline;
            layoutParams.isGuideline = true;
            guideline.setOrientation(layoutParams.orientation);
        }
        if (view instanceof ConstraintHelper) {
            ConstraintHelper constraintHelper = (ConstraintHelper) view;
            constraintHelper.validateParams();
            ((LayoutParams) view.getLayoutParams()).isHelper = true;
            if (!this.mConstraintHelpers.contains(constraintHelper)) {
                this.mConstraintHelpers.add(constraintHelper);
            }
        }
        this.mChildrenByIds.put(view.getId(), view);
        this.mDirtyHierarchy = true;
    }

    public void onViewRemoved(View view) {
        if (VERSION.SDK_INT >= 14) {
            super.onViewRemoved(view);
        }
        this.mChildrenByIds.remove(view.getId());
        ConstraintWidget viewWidget = getViewWidget(view);
        this.mLayoutWidget.remove(viewWidget);
        this.mConstraintHelpers.remove(view);
        this.mVariableDimensionsWidgets.remove(viewWidget);
        this.mDirtyHierarchy = true;
    }

    private void updateHierarchy() {
        int childCount = getChildCount();
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= childCount) {
                break;
            } else if (getChildAt(i).isLayoutRequested()) {
                z = true;
                break;
            } else {
                i++;
            }
        }
        if (z) {
            this.mVariableDimensionsWidgets.clear();
            setChildrenConstraints();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:118:0x01d2, code lost:
        if (r13 != -1) goto L_0x01d6;
     */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01df  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x01e7  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0208  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0217  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x0350  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x0378  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0386  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x03af  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x03be  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setChildrenConstraints() {
        /*
            r24 = this;
            r0 = r24
            int r1 = android.os.Build.VERSION.SDK_INT
            boolean r2 = r24.isInEditMode()
            int r3 = r24.getChildCount()
            r4 = 0
            r5 = -1
            if (r2 == 0) goto L_0x004a
            r6 = r4
        L_0x0011:
            if (r6 >= r3) goto L_0x004a
            android.view.View r7 = r0.getChildAt(r6)
            android.content.res.Resources r8 = r24.getResources()     // Catch:{ NotFoundException -> 0x0047 }
            int r9 = r7.getId()     // Catch:{ NotFoundException -> 0x0047 }
            java.lang.String r8 = r8.getResourceName(r9)     // Catch:{ NotFoundException -> 0x0047 }
            int r9 = r7.getId()     // Catch:{ NotFoundException -> 0x0047 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ NotFoundException -> 0x0047 }
            r0.setDesignInformation(r4, r8, r9)     // Catch:{ NotFoundException -> 0x0047 }
            r9 = 47
            int r9 = r8.indexOf(r9)     // Catch:{ NotFoundException -> 0x0047 }
            if (r9 == r5) goto L_0x003c
            int r9 = r9 + 1
            java.lang.String r8 = r8.substring(r9)     // Catch:{ NotFoundException -> 0x0047 }
        L_0x003c:
            int r7 = r7.getId()     // Catch:{ NotFoundException -> 0x0047 }
            androidx.constraintlayout.solver.widgets.ConstraintWidget r7 = r0.getTargetWidget(r7)     // Catch:{ NotFoundException -> 0x0047 }
            r7.setDebugName(r8)     // Catch:{ NotFoundException -> 0x0047 }
        L_0x0047:
            int r6 = r6 + 1
            goto L_0x0011
        L_0x004a:
            r6 = r4
        L_0x004b:
            if (r6 >= r3) goto L_0x005e
            android.view.View r7 = r0.getChildAt(r6)
            androidx.constraintlayout.solver.widgets.ConstraintWidget r7 = r0.getViewWidget(r7)
            if (r7 != 0) goto L_0x0058
            goto L_0x005b
        L_0x0058:
            r7.reset()
        L_0x005b:
            int r6 = r6 + 1
            goto L_0x004b
        L_0x005e:
            int r6 = r0.mConstraintSetId
            if (r6 == r5) goto L_0x0080
            r6 = r4
        L_0x0063:
            if (r6 >= r3) goto L_0x0080
            android.view.View r7 = r0.getChildAt(r6)
            int r8 = r7.getId()
            int r9 = r0.mConstraintSetId
            if (r8 != r9) goto L_0x007d
            boolean r8 = r7 instanceof androidx.constraintlayout.widget.Constraints
            if (r8 == 0) goto L_0x007d
            androidx.constraintlayout.widget.Constraints r7 = (androidx.constraintlayout.widget.Constraints) r7
            androidx.constraintlayout.widget.ConstraintSet r7 = r7.getConstraintSet()
            r0.mConstraintSet = r7
        L_0x007d:
            int r6 = r6 + 1
            goto L_0x0063
        L_0x0080:
            androidx.constraintlayout.widget.ConstraintSet r6 = r0.mConstraintSet
            if (r6 == 0) goto L_0x0087
            r6.applyToInternal(r0)
        L_0x0087:
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r6 = r0.mLayoutWidget
            r6.removeAllChildren()
            java.util.ArrayList<androidx.constraintlayout.widget.ConstraintHelper> r6 = r0.mConstraintHelpers
            int r6 = r6.size()
            if (r6 <= 0) goto L_0x00a5
            r7 = r4
        L_0x0095:
            if (r7 >= r6) goto L_0x00a5
            java.util.ArrayList<androidx.constraintlayout.widget.ConstraintHelper> r8 = r0.mConstraintHelpers
            java.lang.Object r8 = r8.get(r7)
            androidx.constraintlayout.widget.ConstraintHelper r8 = (androidx.constraintlayout.widget.ConstraintHelper) r8
            r8.updatePreLayout(r0)
            int r7 = r7 + 1
            goto L_0x0095
        L_0x00a5:
            r6 = r4
        L_0x00a6:
            if (r6 >= r3) goto L_0x00b8
            android.view.View r7 = r0.getChildAt(r6)
            boolean r8 = r7 instanceof androidx.constraintlayout.widget.Placeholder
            if (r8 == 0) goto L_0x00b5
            androidx.constraintlayout.widget.Placeholder r7 = (androidx.constraintlayout.widget.Placeholder) r7
            r7.updatePreLayout(r0)
        L_0x00b5:
            int r6 = r6 + 1
            goto L_0x00a6
        L_0x00b8:
            r6 = r4
        L_0x00b9:
            if (r6 >= r3) goto L_0x03ef
            android.view.View r7 = r0.getChildAt(r6)
            androidx.constraintlayout.solver.widgets.ConstraintWidget r14 = r0.getViewWidget(r7)
            if (r14 != 0) goto L_0x00c7
            goto L_0x03eb
        L_0x00c7:
            android.view.ViewGroup$LayoutParams r8 = r7.getLayoutParams()
            r15 = r8
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r15 = (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) r15
            r15.validate()
            boolean r8 = r15.helped
            if (r8 == 0) goto L_0x00d8
            r15.helped = r4
            goto L_0x0108
        L_0x00d8:
            if (r2 == 0) goto L_0x0108
            android.content.res.Resources r8 = r24.getResources()     // Catch:{ NotFoundException -> 0x0108 }
            int r9 = r7.getId()     // Catch:{ NotFoundException -> 0x0108 }
            java.lang.String r8 = r8.getResourceName(r9)     // Catch:{ NotFoundException -> 0x0108 }
            int r9 = r7.getId()     // Catch:{ NotFoundException -> 0x0108 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ NotFoundException -> 0x0108 }
            r0.setDesignInformation(r4, r8, r9)     // Catch:{ NotFoundException -> 0x0108 }
            java.lang.String r9 = "id/"
            int r9 = r8.indexOf(r9)     // Catch:{ NotFoundException -> 0x0108 }
            int r9 = r9 + 3
            java.lang.String r8 = r8.substring(r9)     // Catch:{ NotFoundException -> 0x0108 }
            int r9 = r7.getId()     // Catch:{ NotFoundException -> 0x0108 }
            androidx.constraintlayout.solver.widgets.ConstraintWidget r9 = r0.getTargetWidget(r9)     // Catch:{ NotFoundException -> 0x0108 }
            r9.setDebugName(r8)     // Catch:{ NotFoundException -> 0x0108 }
        L_0x0108:
            int r8 = r7.getVisibility()
            r14.setVisibility(r8)
            boolean r8 = r15.isInPlaceholder
            if (r8 == 0) goto L_0x0118
            r8 = 8
            r14.setVisibility(r8)
        L_0x0118:
            r14.setCompanionWidget(r7)
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r7 = r0.mLayoutWidget
            r7.add(r14)
            boolean r7 = r15.verticalDimensionFixed
            if (r7 == 0) goto L_0x0128
            boolean r7 = r15.horizontalDimensionFixed
            if (r7 != 0) goto L_0x012d
        L_0x0128:
            java.util.ArrayList<androidx.constraintlayout.solver.widgets.ConstraintWidget> r7 = r0.mVariableDimensionsWidgets
            r7.add(r14)
        L_0x012d:
            boolean r7 = r15.isGuideline
            r8 = 17
            if (r7 == 0) goto L_0x015c
            androidx.constraintlayout.solver.widgets.Guideline r14 = (androidx.constraintlayout.solver.widgets.Guideline) r14
            int r7 = r15.resolvedGuideBegin
            int r9 = r15.resolvedGuideEnd
            float r10 = r15.resolvedGuidePercent
            if (r1 >= r8) goto L_0x0143
            int r7 = r15.guideBegin
            int r9 = r15.guideEnd
            float r10 = r15.guidePercent
        L_0x0143:
            r8 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r8 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r8 == 0) goto L_0x014e
            r14.setGuidePercent(r10)
            goto L_0x03eb
        L_0x014e:
            if (r7 == r5) goto L_0x0155
            r14.setGuideBegin(r7)
            goto L_0x03eb
        L_0x0155:
            if (r9 == r5) goto L_0x03eb
            r14.setGuideEnd(r9)
            goto L_0x03eb
        L_0x015c:
            int r7 = r15.leftToLeft
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.leftToRight
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.rightToLeft
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.rightToRight
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.startToStart
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.startToEnd
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.endToStart
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.endToEnd
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.topToTop
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.topToBottom
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.bottomToTop
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.bottomToBottom
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.baselineToBaseline
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.editorAbsoluteX
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.editorAbsoluteY
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.circleConstraint
            if (r7 != r5) goto L_0x01a4
            int r7 = r15.width
            if (r7 == r5) goto L_0x01a4
            int r7 = r15.height
            if (r7 != r5) goto L_0x03eb
        L_0x01a4:
            int r7 = r15.resolvedLeftToLeft
            int r9 = r15.resolvedLeftToRight
            int r10 = r15.resolvedRightToLeft
            int r11 = r15.resolvedRightToRight
            int r12 = r15.resolveGoneLeftMargin
            int r13 = r15.resolveGoneRightMargin
            float r4 = r15.resolvedHorizontalBias
            if (r1 >= r8) goto L_0x01fe
            int r4 = r15.leftToLeft
            int r7 = r15.leftToRight
            int r10 = r15.rightToLeft
            int r11 = r15.rightToRight
            int r8 = r15.goneLeftMargin
            int r9 = r15.goneRightMargin
            float r12 = r15.horizontalBias
            if (r4 != r5) goto L_0x01d5
            if (r7 != r5) goto L_0x01d5
            int r13 = r15.startToStart
            if (r13 == r5) goto L_0x01d0
            r23 = r13
            r13 = r7
            r7 = r23
            goto L_0x01d7
        L_0x01d0:
            int r13 = r15.startToEnd
            if (r13 == r5) goto L_0x01d5
            goto L_0x01d6
        L_0x01d5:
            r13 = r7
        L_0x01d6:
            r7 = r4
        L_0x01d7:
            if (r10 != r5) goto L_0x01f7
            if (r11 != r5) goto L_0x01f7
            int r4 = r15.endToStart
            if (r4 == r5) goto L_0x01e7
            r16 = r8
            r17 = r9
            r9 = r13
            r13 = r4
            r4 = r12
            goto L_0x0203
        L_0x01e7:
            int r4 = r15.endToEnd
            if (r4 == r5) goto L_0x01f7
            r16 = r8
            r17 = r9
            r9 = r13
            r13 = r10
            r23 = r12
            r12 = r4
            r4 = r23
            goto L_0x0204
        L_0x01f7:
            r16 = r8
            r17 = r9
            r4 = r12
            r9 = r13
            goto L_0x0202
        L_0x01fe:
            r16 = r12
            r17 = r13
        L_0x0202:
            r13 = r10
        L_0x0203:
            r12 = r11
        L_0x0204:
            int r8 = r15.circleConstraint
            if (r8 == r5) goto L_0x0217
            androidx.constraintlayout.solver.widgets.ConstraintWidget r4 = r0.getTargetWidget(r8)
            if (r4 == 0) goto L_0x033b
            float r7 = r15.circleAngle
            int r8 = r15.circleRadius
            r14.connectCircularConstraint(r4, r7, r8)
            goto L_0x033b
        L_0x0217:
            if (r7 == r5) goto L_0x0233
            androidx.constraintlayout.solver.widgets.ConstraintWidget r10 = r0.getTargetWidget(r7)
            if (r10 == 0) goto L_0x022f
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r11 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.LEFT
            int r7 = r15.leftMargin
            r8 = r14
            r9 = r11
            r18 = r12
            r12 = r7
            r7 = r13
            r13 = r16
            r8.immediateConnect(r9, r10, r11, r12, r13)
            goto L_0x024a
        L_0x022f:
            r18 = r12
            r7 = r13
            goto L_0x024a
        L_0x0233:
            r18 = r12
            r7 = r13
            if (r9 == r5) goto L_0x024a
            androidx.constraintlayout.solver.widgets.ConstraintWidget r10 = r0.getTargetWidget(r9)
            if (r10 == 0) goto L_0x024a
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r9 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.LEFT
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r11 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.RIGHT
            int r12 = r15.leftMargin
            r8 = r14
            r13 = r16
            r8.immediateConnect(r9, r10, r11, r12, r13)
        L_0x024a:
            if (r7 == r5) goto L_0x025f
            androidx.constraintlayout.solver.widgets.ConstraintWidget r10 = r0.getTargetWidget(r7)
            if (r10 == 0) goto L_0x0274
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r9 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.RIGHT
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r11 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.LEFT
            int r12 = r15.rightMargin
            r8 = r14
            r13 = r17
            r8.immediateConnect(r9, r10, r11, r12, r13)
            goto L_0x0274
        L_0x025f:
            r11 = r18
            if (r11 == r5) goto L_0x0274
            androidx.constraintlayout.solver.widgets.ConstraintWidget r10 = r0.getTargetWidget(r11)
            if (r10 == 0) goto L_0x0274
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r11 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.RIGHT
            int r12 = r15.rightMargin
            r8 = r14
            r9 = r11
            r13 = r17
            r8.immediateConnect(r9, r10, r11, r12, r13)
        L_0x0274:
            int r7 = r15.topToTop
            if (r7 == r5) goto L_0x028a
            androidx.constraintlayout.solver.widgets.ConstraintWidget r10 = r0.getTargetWidget(r7)
            if (r10 == 0) goto L_0x02a0
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r11 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.TOP
            int r12 = r15.topMargin
            int r13 = r15.goneTopMargin
            r8 = r14
            r9 = r11
            r8.immediateConnect(r9, r10, r11, r12, r13)
            goto L_0x02a0
        L_0x028a:
            int r7 = r15.topToBottom
            if (r7 == r5) goto L_0x02a0
            androidx.constraintlayout.solver.widgets.ConstraintWidget r10 = r0.getTargetWidget(r7)
            if (r10 == 0) goto L_0x02a0
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r9 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.TOP
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r11 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BOTTOM
            int r12 = r15.topMargin
            int r13 = r15.goneTopMargin
            r8 = r14
            r8.immediateConnect(r9, r10, r11, r12, r13)
        L_0x02a0:
            int r7 = r15.bottomToTop
            if (r7 == r5) goto L_0x02b7
            androidx.constraintlayout.solver.widgets.ConstraintWidget r10 = r0.getTargetWidget(r7)
            if (r10 == 0) goto L_0x02cc
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r9 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BOTTOM
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r11 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.TOP
            int r12 = r15.bottomMargin
            int r13 = r15.goneBottomMargin
            r8 = r14
            r8.immediateConnect(r9, r10, r11, r12, r13)
            goto L_0x02cc
        L_0x02b7:
            int r7 = r15.bottomToBottom
            if (r7 == r5) goto L_0x02cc
            androidx.constraintlayout.solver.widgets.ConstraintWidget r10 = r0.getTargetWidget(r7)
            if (r10 == 0) goto L_0x02cc
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r11 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BOTTOM
            int r12 = r15.bottomMargin
            int r13 = r15.goneBottomMargin
            r8 = r14
            r9 = r11
            r8.immediateConnect(r9, r10, r11, r12, r13)
        L_0x02cc:
            int r7 = r15.baselineToBaseline
            if (r7 == r5) goto L_0x0320
            android.util.SparseArray<android.view.View> r8 = r0.mChildrenByIds
            java.lang.Object r7 = r8.get(r7)
            android.view.View r7 = (android.view.View) r7
            int r8 = r15.baselineToBaseline
            androidx.constraintlayout.solver.widgets.ConstraintWidget r8 = r0.getTargetWidget(r8)
            if (r8 == 0) goto L_0x0320
            if (r7 == 0) goto L_0x0320
            android.view.ViewGroup$LayoutParams r9 = r7.getLayoutParams()
            boolean r9 = r9 instanceof androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            if (r9 == 0) goto L_0x0320
            android.view.ViewGroup$LayoutParams r7 = r7.getLayoutParams()
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r7 = (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) r7
            r9 = 1
            r15.needsBaseline = r9
            r7.needsBaseline = r9
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r7 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BASELINE
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r16 = r14.getAnchor(r7)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r7 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BASELINE
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r17 = r8.getAnchor(r7)
            r18 = 0
            r19 = -1
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Strength r20 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Strength.STRONG
            r21 = 0
            r22 = 1
            r16.connect(r17, r18, r19, r20, r21, r22)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r7 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.TOP
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r7 = r14.getAnchor(r7)
            r7.reset()
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r7 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BOTTOM
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r7 = r14.getAnchor(r7)
            r7.reset()
        L_0x0320:
            r7 = 0
            int r8 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            r9 = 1056964608(0x3f000000, float:0.5)
            if (r8 < 0) goto L_0x032e
            int r8 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r8 == 0) goto L_0x032e
            r14.setHorizontalBiasPercent(r4)
        L_0x032e:
            float r4 = r15.verticalBias
            int r7 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r7 < 0) goto L_0x033b
            int r7 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r7 == 0) goto L_0x033b
            r14.setVerticalBiasPercent(r4)
        L_0x033b:
            if (r2 == 0) goto L_0x034c
            int r4 = r15.editorAbsoluteX
            if (r4 != r5) goto L_0x0345
            int r4 = r15.editorAbsoluteY
            if (r4 == r5) goto L_0x034c
        L_0x0345:
            int r4 = r15.editorAbsoluteX
            int r7 = r15.editorAbsoluteY
            r14.setOrigin(r4, r7)
        L_0x034c:
            boolean r4 = r15.horizontalDimensionFixed
            if (r4 != 0) goto L_0x0378
            int r4 = r15.width
            if (r4 != r5) goto L_0x036e
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r4 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_PARENT
            r14.setHorizontalDimensionBehaviour(r4)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r4 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.LEFT
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r4 = r14.getAnchor(r4)
            int r7 = r15.leftMargin
            r4.mMargin = r7
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r4 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.RIGHT
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r4 = r14.getAnchor(r4)
            int r7 = r15.rightMargin
            r4.mMargin = r7
            goto L_0x0382
        L_0x036e:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r4 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            r14.setHorizontalDimensionBehaviour(r4)
            r4 = 0
            r14.setWidth(r4)
            goto L_0x0382
        L_0x0378:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r4 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.FIXED
            r14.setHorizontalDimensionBehaviour(r4)
            int r4 = r15.width
            r14.setWidth(r4)
        L_0x0382:
            boolean r4 = r15.verticalDimensionFixed
            if (r4 != 0) goto L_0x03af
            int r4 = r15.height
            if (r4 != r5) goto L_0x03a5
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r4 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_PARENT
            r14.setVerticalDimensionBehaviour(r4)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r4 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.TOP
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r4 = r14.getAnchor(r4)
            int r7 = r15.topMargin
            r4.mMargin = r7
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r4 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BOTTOM
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r4 = r14.getAnchor(r4)
            int r7 = r15.bottomMargin
            r4.mMargin = r7
            r4 = 0
            goto L_0x03ba
        L_0x03a5:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r4 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            r14.setVerticalDimensionBehaviour(r4)
            r4 = 0
            r14.setHeight(r4)
            goto L_0x03ba
        L_0x03af:
            r4 = 0
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r7 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.FIXED
            r14.setVerticalDimensionBehaviour(r7)
            int r7 = r15.height
            r14.setHeight(r7)
        L_0x03ba:
            java.lang.String r7 = r15.dimensionRatio
            if (r7 == 0) goto L_0x03c1
            r14.setDimensionRatio(r7)
        L_0x03c1:
            float r7 = r15.horizontalWeight
            r14.setHorizontalWeight(r7)
            float r7 = r15.verticalWeight
            r14.setVerticalWeight(r7)
            int r7 = r15.horizontalChainStyle
            r14.setHorizontalChainStyle(r7)
            int r7 = r15.verticalChainStyle
            r14.setVerticalChainStyle(r7)
            int r7 = r15.matchConstraintDefaultWidth
            int r8 = r15.matchConstraintMinWidth
            int r9 = r15.matchConstraintMaxWidth
            float r10 = r15.matchConstraintPercentWidth
            r14.setHorizontalMatchStyle(r7, r8, r9, r10)
            int r7 = r15.matchConstraintDefaultHeight
            int r8 = r15.matchConstraintMinHeight
            int r9 = r15.matchConstraintMaxHeight
            float r10 = r15.matchConstraintPercentHeight
            r14.setVerticalMatchStyle(r7, r8, r9, r10)
        L_0x03eb:
            int r6 = r6 + 1
            goto L_0x00b9
        L_0x03ef:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.widget.ConstraintLayout.setChildrenConstraints():void");
    }

    private final ConstraintWidget getTargetWidget(int i) {
        ConstraintWidget constraintWidget;
        if (i == 0) {
            return this.mLayoutWidget;
        }
        View view = (View) this.mChildrenByIds.get(i);
        if (view == this) {
            return this.mLayoutWidget;
        }
        if (view == null) {
            constraintWidget = null;
        } else {
            constraintWidget = ((LayoutParams) view.getLayoutParams()).widget;
        }
        return constraintWidget;
    }

    public final ConstraintWidget getViewWidget(View view) {
        ConstraintWidget constraintWidget;
        if (view == this) {
            return this.mLayoutWidget;
        }
        if (view == null) {
            constraintWidget = null;
        } else {
            constraintWidget = ((LayoutParams) view.getLayoutParams()).widget;
        }
        return constraintWidget;
    }

    private void internalMeasureChildren(int i, int i2) {
        boolean z;
        boolean z2;
        int i3;
        int i4;
        int i5 = i;
        int i6 = i2;
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int childCount = getChildCount();
        for (int i7 = 0; i7 < childCount; i7++) {
            View childAt = getChildAt(i7);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                ConstraintWidget constraintWidget = layoutParams.widget;
                if (!layoutParams.isGuideline && !layoutParams.isHelper) {
                    constraintWidget.setVisibility(childAt.getVisibility());
                    int i8 = layoutParams.width;
                    int i9 = layoutParams.height;
                    boolean z3 = layoutParams.horizontalDimensionFixed;
                    if (z3 || layoutParams.verticalDimensionFixed || (!z3 && layoutParams.matchConstraintDefaultWidth == 1) || layoutParams.width == -1 || (!layoutParams.verticalDimensionFixed && (layoutParams.matchConstraintDefaultHeight == 1 || layoutParams.height == -1))) {
                        if (i8 == 0) {
                            i3 = ViewGroup.getChildMeasureSpec(i5, paddingLeft, -2);
                            z2 = true;
                        } else if (i8 == -1) {
                            i3 = ViewGroup.getChildMeasureSpec(i5, paddingLeft, -1);
                            z2 = false;
                        } else {
                            z2 = i8 == -2;
                            i3 = ViewGroup.getChildMeasureSpec(i5, paddingLeft, i8);
                        }
                        if (i9 == 0) {
                            z = true;
                            i4 = ViewGroup.getChildMeasureSpec(i6, paddingTop, -2);
                        } else if (i9 == -1) {
                            i4 = ViewGroup.getChildMeasureSpec(i6, paddingTop, -1);
                            z = false;
                        } else {
                            z = i9 == -2;
                            i4 = ViewGroup.getChildMeasureSpec(i6, paddingTop, i9);
                        }
                        childAt.measure(i3, i4);
                        Metrics metrics = this.mMetrics;
                        if (metrics != null) {
                            metrics.measures++;
                        }
                        constraintWidget.setWidthWrapContent(i8 == -2);
                        constraintWidget.setHeightWrapContent(i9 == -2);
                        i8 = childAt.getMeasuredWidth();
                        i9 = childAt.getMeasuredHeight();
                    } else {
                        z2 = false;
                        z = false;
                    }
                    constraintWidget.setWidth(i8);
                    constraintWidget.setHeight(i9);
                    if (z2) {
                        constraintWidget.setWrapWidth(i8);
                    }
                    if (z) {
                        constraintWidget.setWrapHeight(i9);
                    }
                    if (layoutParams.needsBaseline) {
                        int baseline = childAt.getBaseline();
                        if (baseline != -1) {
                            constraintWidget.setBaselineDistance(baseline);
                        }
                    }
                }
            }
        }
    }

    private void updatePostMeasures() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof Placeholder) {
                ((Placeholder) childAt).updatePostMeasure(this);
            }
        }
        int size = this.mConstraintHelpers.size();
        if (size > 0) {
            for (int i2 = 0; i2 < size; i2++) {
                ((ConstraintHelper) this.mConstraintHelpers.get(i2)).updatePostMeasure(this);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:108:0x0209  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x0242  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0266  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x026f  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0274  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0276  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x027c  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x027e  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0292  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x0297  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x029c  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x02a4  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x02ad  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x02b5  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x02c2  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02cd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void internalMeasureDimensions(int r24, int r25) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            r2 = r25
            int r3 = r23.getPaddingTop()
            int r4 = r23.getPaddingBottom()
            int r3 = r3 + r4
            int r4 = r23.getPaddingLeft()
            int r5 = r23.getPaddingRight()
            int r4 = r4 + r5
            int r5 = r23.getChildCount()
            r7 = 0
        L_0x001d:
            r8 = 1
            r10 = 8
            r12 = -2
            if (r7 >= r5) goto L_0x00dc
            android.view.View r14 = r0.getChildAt(r7)
            int r15 = r14.getVisibility()
            if (r15 != r10) goto L_0x0030
            goto L_0x00d4
        L_0x0030:
            android.view.ViewGroup$LayoutParams r10 = r14.getLayoutParams()
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r10 = (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) r10
            androidx.constraintlayout.solver.widgets.ConstraintWidget r15 = r10.widget
            boolean r6 = r10.isGuideline
            if (r6 != 0) goto L_0x00d4
            boolean r6 = r10.isHelper
            if (r6 == 0) goto L_0x0042
            goto L_0x00d4
        L_0x0042:
            int r6 = r14.getVisibility()
            r15.setVisibility(r6)
            int r6 = r10.width
            int r13 = r10.height
            if (r6 == 0) goto L_0x00c4
            if (r13 != 0) goto L_0x0053
            goto L_0x00c4
        L_0x0053:
            if (r6 != r12) goto L_0x0058
            r16 = 1
            goto L_0x005a
        L_0x0058:
            r16 = 0
        L_0x005a:
            int r11 = android.view.ViewGroup.getChildMeasureSpec(r1, r4, r6)
            if (r13 != r12) goto L_0x0063
            r17 = 1
            goto L_0x0065
        L_0x0063:
            r17 = 0
        L_0x0065:
            int r12 = android.view.ViewGroup.getChildMeasureSpec(r2, r3, r13)
            r14.measure(r11, r12)
            androidx.constraintlayout.solver.Metrics r11 = r0.mMetrics
            r12 = r3
            if (r11 == 0) goto L_0x0076
            long r2 = r11.measures
            long r2 = r2 + r8
            r11.measures = r2
        L_0x0076:
            r2 = -2
            if (r6 != r2) goto L_0x007b
            r3 = 1
            goto L_0x007c
        L_0x007b:
            r3 = 0
        L_0x007c:
            r15.setWidthWrapContent(r3)
            if (r13 != r2) goto L_0x0083
            r13 = 1
            goto L_0x0084
        L_0x0083:
            r13 = 0
        L_0x0084:
            r15.setHeightWrapContent(r13)
            int r2 = r14.getMeasuredWidth()
            int r3 = r14.getMeasuredHeight()
            r15.setWidth(r2)
            r15.setHeight(r3)
            if (r16 == 0) goto L_0x009a
            r15.setWrapWidth(r2)
        L_0x009a:
            if (r17 == 0) goto L_0x009f
            r15.setWrapHeight(r3)
        L_0x009f:
            boolean r6 = r10.needsBaseline
            if (r6 == 0) goto L_0x00ad
            int r6 = r14.getBaseline()
            r8 = -1
            if (r6 == r8) goto L_0x00ad
            r15.setBaselineDistance(r6)
        L_0x00ad:
            boolean r6 = r10.horizontalDimensionFixed
            if (r6 == 0) goto L_0x00d5
            boolean r6 = r10.verticalDimensionFixed
            if (r6 == 0) goto L_0x00d5
            androidx.constraintlayout.solver.widgets.ResolutionDimension r6 = r15.getResolutionWidth()
            r6.resolve(r2)
            androidx.constraintlayout.solver.widgets.ResolutionDimension r2 = r15.getResolutionHeight()
            r2.resolve(r3)
            goto L_0x00d5
        L_0x00c4:
            r12 = r3
            androidx.constraintlayout.solver.widgets.ResolutionDimension r2 = r15.getResolutionWidth()
            r2.invalidate()
            androidx.constraintlayout.solver.widgets.ResolutionDimension r2 = r15.getResolutionHeight()
            r2.invalidate()
            goto L_0x00d5
        L_0x00d4:
            r12 = r3
        L_0x00d5:
            int r7 = r7 + 1
            r2 = r25
            r3 = r12
            goto L_0x001d
        L_0x00dc:
            r12 = r3
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r2 = r0.mLayoutWidget
            r2.solveGraph()
            r2 = 0
        L_0x00e3:
            if (r2 >= r5) goto L_0x02e6
            android.view.View r3 = r0.getChildAt(r2)
            int r6 = r3.getVisibility()
            if (r6 != r10) goto L_0x00f1
            goto L_0x02cf
        L_0x00f1:
            android.view.ViewGroup$LayoutParams r6 = r3.getLayoutParams()
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r6 = (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) r6
            androidx.constraintlayout.solver.widgets.ConstraintWidget r7 = r6.widget
            boolean r11 = r6.isGuideline
            if (r11 != 0) goto L_0x02cf
            boolean r11 = r6.isHelper
            if (r11 == 0) goto L_0x0103
            goto L_0x02cf
        L_0x0103:
            int r11 = r3.getVisibility()
            r7.setVisibility(r11)
            int r11 = r6.width
            int r13 = r6.height
            if (r11 == 0) goto L_0x0114
            if (r13 == 0) goto L_0x0114
            goto L_0x02cf
        L_0x0114:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r14 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.LEFT
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r14 = r7.getAnchor(r14)
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r14 = r14.getResolutionNode()
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r15 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.RIGHT
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r15 = r7.getAnchor(r15)
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r15 = r15.getResolutionNode()
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r10 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.LEFT
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r10 = r7.getAnchor(r10)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r10 = r10.getTarget()
            if (r10 == 0) goto L_0x0142
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r10 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.RIGHT
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r10 = r7.getAnchor(r10)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r10 = r10.getTarget()
            if (r10 == 0) goto L_0x0142
            r10 = 1
            goto L_0x0143
        L_0x0142:
            r10 = 0
        L_0x0143:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r8 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.TOP
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r7.getAnchor(r8)
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r8 = r8.getResolutionNode()
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r9 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BOTTOM
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r7.getAnchor(r9)
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r9 = r9.getResolutionNode()
            r17 = r5
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r5 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.TOP
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r5 = r7.getAnchor(r5)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r5 = r5.getTarget()
            if (r5 == 0) goto L_0x0173
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r5 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BOTTOM
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r5 = r7.getAnchor(r5)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r5 = r5.getTarget()
            if (r5 == 0) goto L_0x0173
            r5 = 1
            goto L_0x0174
        L_0x0173:
            r5 = 0
        L_0x0174:
            if (r11 != 0) goto L_0x0187
            if (r13 != 0) goto L_0x0187
            if (r10 == 0) goto L_0x0187
            if (r5 == 0) goto L_0x0187
            r5 = r25
            r6 = r0
            r20 = r2
            r2 = -1
            r8 = -2
            r18 = 1
            goto L_0x02da
        L_0x0187:
            r20 = r2
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r2 = r0.mLayoutWidget
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r2 = r2.getHorizontalDimensionBehaviour()
            r21 = r6
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r6 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r2 == r6) goto L_0x0197
            r2 = 1
            goto L_0x0198
        L_0x0197:
            r2 = 0
        L_0x0198:
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r6 = r0.mLayoutWidget
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r6 = r6.getVerticalDimensionBehaviour()
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r0 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r6 == r0) goto L_0x01a4
            r0 = 1
            goto L_0x01a5
        L_0x01a4:
            r0 = 0
        L_0x01a5:
            if (r2 != 0) goto L_0x01ae
            androidx.constraintlayout.solver.widgets.ResolutionDimension r6 = r7.getResolutionWidth()
            r6.invalidate()
        L_0x01ae:
            if (r0 != 0) goto L_0x01b7
            androidx.constraintlayout.solver.widgets.ResolutionDimension r6 = r7.getResolutionHeight()
            r6.invalidate()
        L_0x01b7:
            if (r11 != 0) goto L_0x01ee
            if (r2 == 0) goto L_0x01e5
            boolean r6 = r7.isSpreadWidth()
            if (r6 == 0) goto L_0x01e5
            if (r10 == 0) goto L_0x01e5
            boolean r6 = r14.isResolved()
            if (r6 == 0) goto L_0x01e5
            boolean r6 = r15.isResolved()
            if (r6 == 0) goto L_0x01e5
            float r6 = r15.getResolvedValue()
            float r10 = r14.getResolvedValue()
            float r6 = r6 - r10
            int r11 = (int) r6
            androidx.constraintlayout.solver.widgets.ResolutionDimension r6 = r7.getResolutionWidth()
            r6.resolve(r11)
            int r6 = android.view.ViewGroup.getChildMeasureSpec(r1, r4, r11)
            goto L_0x01f7
        L_0x01e5:
            r6 = -2
            int r2 = android.view.ViewGroup.getChildMeasureSpec(r1, r4, r6)
            r6 = r2
            r2 = 0
            r10 = 1
            goto L_0x0207
        L_0x01ee:
            r6 = -2
            r10 = -1
            if (r11 != r10) goto L_0x01f9
            int r14 = android.view.ViewGroup.getChildMeasureSpec(r1, r4, r10)
            r6 = r14
        L_0x01f7:
            r10 = 0
            goto L_0x0207
        L_0x01f9:
            if (r11 != r6) goto L_0x01fd
            r6 = 1
            goto L_0x01fe
        L_0x01fd:
            r6 = 0
        L_0x01fe:
            int r10 = android.view.ViewGroup.getChildMeasureSpec(r1, r4, r11)
            r22 = r10
            r10 = r6
            r6 = r22
        L_0x0207:
            if (r13 != 0) goto L_0x0242
            if (r0 == 0) goto L_0x0237
            boolean r14 = r7.isSpreadHeight()
            if (r14 == 0) goto L_0x0237
            if (r5 == 0) goto L_0x0237
            boolean r5 = r8.isResolved()
            if (r5 == 0) goto L_0x0237
            boolean r5 = r9.isResolved()
            if (r5 == 0) goto L_0x0237
            float r5 = r9.getResolvedValue()
            float r8 = r8.getResolvedValue()
            float r5 = r5 - r8
            int r13 = (int) r5
            androidx.constraintlayout.solver.widgets.ResolutionDimension r5 = r7.getResolutionHeight()
            r5.resolve(r13)
            r5 = r25
            int r8 = android.view.ViewGroup.getChildMeasureSpec(r5, r12, r13)
            goto L_0x024d
        L_0x0237:
            r5 = r25
            r8 = -2
            int r0 = android.view.ViewGroup.getChildMeasureSpec(r5, r12, r8)
            r8 = r0
            r0 = 0
            r9 = 1
            goto L_0x025d
        L_0x0242:
            r5 = r25
            r8 = -2
            r9 = -1
            if (r13 != r9) goto L_0x024f
            int r14 = android.view.ViewGroup.getChildMeasureSpec(r5, r12, r9)
            r8 = r14
        L_0x024d:
            r9 = 0
            goto L_0x025d
        L_0x024f:
            if (r13 != r8) goto L_0x0253
            r8 = 1
            goto L_0x0254
        L_0x0253:
            r8 = 0
        L_0x0254:
            int r9 = android.view.ViewGroup.getChildMeasureSpec(r5, r12, r13)
            r22 = r9
            r9 = r8
            r8 = r22
        L_0x025d:
            r3.measure(r6, r8)
            r6 = r23
            androidx.constraintlayout.solver.Metrics r8 = r6.mMetrics
            if (r8 == 0) goto L_0x026f
            long r14 = r8.measures
            r18 = 1
            long r14 = r14 + r18
            r8.measures = r14
            goto L_0x0271
        L_0x026f:
            r18 = 1
        L_0x0271:
            r8 = -2
            if (r11 != r8) goto L_0x0276
            r11 = 1
            goto L_0x0277
        L_0x0276:
            r11 = 0
        L_0x0277:
            r7.setWidthWrapContent(r11)
            if (r13 != r8) goto L_0x027e
            r11 = 1
            goto L_0x027f
        L_0x027e:
            r11 = 0
        L_0x027f:
            r7.setHeightWrapContent(r11)
            int r11 = r3.getMeasuredWidth()
            int r13 = r3.getMeasuredHeight()
            r7.setWidth(r11)
            r7.setHeight(r13)
            if (r10 == 0) goto L_0x0295
            r7.setWrapWidth(r11)
        L_0x0295:
            if (r9 == 0) goto L_0x029a
            r7.setWrapHeight(r13)
        L_0x029a:
            if (r2 == 0) goto L_0x02a4
            androidx.constraintlayout.solver.widgets.ResolutionDimension r2 = r7.getResolutionWidth()
            r2.resolve(r11)
            goto L_0x02ab
        L_0x02a4:
            androidx.constraintlayout.solver.widgets.ResolutionDimension r2 = r7.getResolutionWidth()
            r2.remove()
        L_0x02ab:
            if (r0 == 0) goto L_0x02b5
            androidx.constraintlayout.solver.widgets.ResolutionDimension r0 = r7.getResolutionHeight()
            r0.resolve(r13)
            goto L_0x02bc
        L_0x02b5:
            androidx.constraintlayout.solver.widgets.ResolutionDimension r0 = r7.getResolutionHeight()
            r0.remove()
        L_0x02bc:
            r0 = r21
            boolean r0 = r0.needsBaseline
            if (r0 == 0) goto L_0x02cd
            int r0 = r3.getBaseline()
            r2 = -1
            if (r0 == r2) goto L_0x02da
            r7.setBaselineDistance(r0)
            goto L_0x02da
        L_0x02cd:
            r2 = -1
            goto L_0x02da
        L_0x02cf:
            r6 = r0
            r20 = r2
            r17 = r5
            r18 = r8
            r2 = -1
            r8 = -2
            r5 = r25
        L_0x02da:
            int r0 = r20 + 1
            r2 = r0
            r0 = r6
            r5 = r17
            r8 = r18
            r10 = 8
            goto L_0x00e3
        L_0x02e6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.widget.ConstraintLayout.internalMeasureDimensions(int, int):void");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0240  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x024b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r25, int r26) {
        /*
            r24 = this;
            r0 = r24
            r1 = r25
            r2 = r26
            int r3 = android.os.Build.VERSION.SDK_INT
            java.lang.System.currentTimeMillis()
            int r4 = android.view.View.MeasureSpec.getMode(r25)
            int r5 = android.view.View.MeasureSpec.getSize(r25)
            int r6 = android.view.View.MeasureSpec.getMode(r26)
            int r7 = android.view.View.MeasureSpec.getSize(r26)
            int r8 = r0.mLastMeasureWidth
            r9 = -1
            if (r8 == r9) goto L_0x0022
            int r8 = r0.mLastMeasureHeight
        L_0x0022:
            r8 = 1073741824(0x40000000, float:2.0)
            if (r4 != r8) goto L_0x002e
            if (r6 != r8) goto L_0x002e
            int r10 = r0.mLastMeasureWidth
            if (r5 != r10) goto L_0x002e
            int r10 = r0.mLastMeasureHeight
        L_0x002e:
            int r10 = r0.mLastMeasureWidthMode
            r11 = 0
            r12 = 1
            if (r4 != r10) goto L_0x003a
            int r10 = r0.mLastMeasureHeightMode
            if (r6 != r10) goto L_0x003a
            r10 = r12
            goto L_0x003b
        L_0x003a:
            r10 = r11
        L_0x003b:
            if (r10 == 0) goto L_0x0043
            int r13 = r0.mLastMeasureWidthSize
            if (r5 != r13) goto L_0x0043
            int r13 = r0.mLastMeasureHeightSize
        L_0x0043:
            r13 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r10 == 0) goto L_0x0051
            if (r4 != r13) goto L_0x0051
            if (r6 != r8) goto L_0x0051
            int r14 = r0.mLastMeasureWidth
            if (r5 < r14) goto L_0x0051
            int r14 = r0.mLastMeasureHeight
        L_0x0051:
            if (r10 == 0) goto L_0x005d
            if (r4 != r8) goto L_0x005d
            if (r6 != r13) goto L_0x005d
            int r10 = r0.mLastMeasureWidth
            if (r5 != r10) goto L_0x005d
            int r10 = r0.mLastMeasureHeight
        L_0x005d:
            r0.mLastMeasureWidthMode = r4
            r0.mLastMeasureHeightMode = r6
            r0.mLastMeasureWidthSize = r5
            r0.mLastMeasureHeightSize = r7
            int r4 = r24.getPaddingLeft()
            int r5 = r24.getPaddingTop()
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r6 = r0.mLayoutWidget
            r6.setX(r4)
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r6 = r0.mLayoutWidget
            r6.setY(r5)
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r6 = r0.mLayoutWidget
            int r7 = r0.mMaxWidth
            r6.setMaxWidth(r7)
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r6 = r0.mLayoutWidget
            int r7 = r0.mMaxHeight
            r6.setMaxHeight(r7)
            r6 = 17
            if (r3 < r6) goto L_0x0097
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r6 = r0.mLayoutWidget
            int r7 = r24.getLayoutDirection()
            if (r7 != r12) goto L_0x0093
            r7 = r12
            goto L_0x0094
        L_0x0093:
            r7 = r11
        L_0x0094:
            r6.setRtl(r7)
        L_0x0097:
            r24.setSelfDimensionBehaviour(r25, r26)
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r6 = r0.mLayoutWidget
            int r6 = r6.getWidth()
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r7 = r0.mLayoutWidget
            int r7 = r7.getHeight()
            boolean r10 = r0.mDirtyHierarchy
            if (r10 == 0) goto L_0x00af
            r0.mDirtyHierarchy = r11
            r24.updateHierarchy()
        L_0x00af:
            int r10 = r0.mOptimizationLevel
            r13 = 8
            r10 = r10 & r13
            if (r10 != r13) goto L_0x00b8
            r10 = r12
            goto L_0x00b9
        L_0x00b8:
            r10 = r11
        L_0x00b9:
            if (r10 == 0) goto L_0x00c9
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r14 = r0.mLayoutWidget
            r14.preOptimize()
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r14 = r0.mLayoutWidget
            r14.optimizeForDimensions(r6, r7)
            r24.internalMeasureDimensions(r25, r26)
            goto L_0x00cc
        L_0x00c9:
            r24.internalMeasureChildren(r25, r26)
        L_0x00cc:
            r24.updatePostMeasures()
            int r14 = r24.getChildCount()
            if (r14 <= 0) goto L_0x00da
            java.lang.String r14 = "First pass"
            r0.solveLinearSystem(r14)
        L_0x00da:
            java.util.ArrayList<androidx.constraintlayout.solver.widgets.ConstraintWidget> r14 = r0.mVariableDimensionsWidgets
            int r14 = r14.size()
            int r15 = r24.getPaddingBottom()
            int r5 = r5 + r15
            int r15 = r24.getPaddingRight()
            int r4 = r4 + r15
            if (r14 <= 0) goto L_0x030c
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r11 = r0.mLayoutWidget
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r11 = r11.getHorizontalDimensionBehaviour()
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r12 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r11 != r12) goto L_0x00f8
            r11 = 1
            goto L_0x00f9
        L_0x00f8:
            r11 = 0
        L_0x00f9:
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r12 = r0.mLayoutWidget
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r12 = r12.getVerticalDimensionBehaviour()
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r15 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r12 != r15) goto L_0x0105
            r12 = 1
            goto L_0x0106
        L_0x0105:
            r12 = 0
        L_0x0106:
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r15 = r0.mLayoutWidget
            int r15 = r15.getWidth()
            int r9 = r0.mMinWidth
            int r9 = java.lang.Math.max(r15, r9)
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r15 = r0.mLayoutWidget
            int r15 = r15.getHeight()
            int r8 = r0.mMinHeight
            int r8 = java.lang.Math.max(r15, r8)
            r15 = 0
            r16 = 0
            r17 = 0
        L_0x0123:
            r18 = 1
            if (r15 >= r14) goto L_0x0266
            java.util.ArrayList<androidx.constraintlayout.solver.widgets.ConstraintWidget> r13 = r0.mVariableDimensionsWidgets
            java.lang.Object r13 = r13.get(r15)
            androidx.constraintlayout.solver.widgets.ConstraintWidget r13 = (androidx.constraintlayout.solver.widgets.ConstraintWidget) r13
            java.lang.Object r20 = r13.getCompanionWidget()
            r21 = r14
            r14 = r20
            android.view.View r14 = (android.view.View) r14
            if (r14 != 0) goto L_0x0141
            r20 = r6
            r22 = r7
            goto L_0x024e
        L_0x0141:
            android.view.ViewGroup$LayoutParams r20 = r14.getLayoutParams()
            r22 = r7
            r7 = r20
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r7 = (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) r7
            r20 = r6
            boolean r6 = r7.isHelper
            if (r6 != 0) goto L_0x024e
            boolean r6 = r7.isGuideline
            if (r6 == 0) goto L_0x0157
            goto L_0x024e
        L_0x0157:
            int r6 = r14.getVisibility()
            r23 = r15
            r15 = 8
            if (r6 != r15) goto L_0x0166
        L_0x0161:
            r15 = r5
            r6 = r17
            goto L_0x0253
        L_0x0166:
            if (r10 == 0) goto L_0x017d
            androidx.constraintlayout.solver.widgets.ResolutionDimension r6 = r13.getResolutionWidth()
            boolean r6 = r6.isResolved()
            if (r6 == 0) goto L_0x017d
            androidx.constraintlayout.solver.widgets.ResolutionDimension r6 = r13.getResolutionHeight()
            boolean r6 = r6.isResolved()
            if (r6 == 0) goto L_0x017d
            goto L_0x0161
        L_0x017d:
            int r6 = r7.width
            r15 = -2
            if (r6 != r15) goto L_0x018b
            boolean r15 = r7.horizontalDimensionFixed
            if (r15 == 0) goto L_0x018b
            int r6 = android.view.ViewGroup.getChildMeasureSpec(r1, r4, r6)
            goto L_0x0195
        L_0x018b:
            int r6 = r13.getWidth()
            r15 = 1073741824(0x40000000, float:2.0)
            int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r6, r15)
        L_0x0195:
            int r15 = r7.height
            r1 = -2
            if (r15 != r1) goto L_0x01a3
            boolean r1 = r7.verticalDimensionFixed
            if (r1 == 0) goto L_0x01a3
            int r1 = android.view.ViewGroup.getChildMeasureSpec(r2, r5, r15)
            goto L_0x01ad
        L_0x01a3:
            int r1 = r13.getHeight()
            r15 = 1073741824(0x40000000, float:2.0)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r15)
        L_0x01ad:
            r14.measure(r6, r1)
            androidx.constraintlayout.solver.Metrics r1 = r0.mMetrics
            r15 = r5
            if (r1 == 0) goto L_0x01bb
            long r5 = r1.additionalMeasures
            long r5 = r5 + r18
            r1.additionalMeasures = r5
        L_0x01bb:
            int r1 = r14.getMeasuredWidth()
            int r5 = r14.getMeasuredHeight()
            int r6 = r13.getWidth()
            if (r1 == r6) goto L_0x01f2
            r13.setWidth(r1)
            if (r10 == 0) goto L_0x01d5
            androidx.constraintlayout.solver.widgets.ResolutionDimension r6 = r13.getResolutionWidth()
            r6.resolve(r1)
        L_0x01d5:
            if (r11 == 0) goto L_0x01f0
            int r1 = r13.getRight()
            if (r1 <= r9) goto L_0x01f0
            int r1 = r13.getRight()
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r6 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.RIGHT
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r6 = r13.getAnchor(r6)
            int r6 = r6.getMargin()
            int r1 = r1 + r6
            int r9 = java.lang.Math.max(r9, r1)
        L_0x01f0:
            r16 = 1
        L_0x01f2:
            int r1 = r13.getHeight()
            if (r5 == r1) goto L_0x0222
            r13.setHeight(r5)
            if (r10 == 0) goto L_0x0204
            androidx.constraintlayout.solver.widgets.ResolutionDimension r1 = r13.getResolutionHeight()
            r1.resolve(r5)
        L_0x0204:
            if (r12 == 0) goto L_0x0220
            int r1 = r13.getBottom()
            if (r1 <= r8) goto L_0x0220
            int r1 = r13.getBottom()
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r5 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BOTTOM
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r5 = r13.getAnchor(r5)
            int r5 = r5.getMargin()
            int r1 = r1 + r5
            int r1 = java.lang.Math.max(r8, r1)
            r8 = r1
        L_0x0220:
            r16 = 1
        L_0x0222:
            boolean r1 = r7.needsBaseline
            if (r1 == 0) goto L_0x023b
            int r1 = r14.getBaseline()
            r5 = -1
            if (r1 == r5) goto L_0x023c
            int r6 = r13.getBaselineDistance()
            if (r1 == r6) goto L_0x023c
            r13.setBaselineDistance(r1)
            r1 = 11
            r16 = 1
            goto L_0x023e
        L_0x023b:
            r5 = -1
        L_0x023c:
            r1 = 11
        L_0x023e:
            if (r3 < r1) goto L_0x024b
            int r1 = r14.getMeasuredState()
            r6 = r17
            int r17 = android.view.ViewGroup.combineMeasuredStates(r6, r1)
            goto L_0x0256
        L_0x024b:
            r6 = r17
            goto L_0x0256
        L_0x024e:
            r23 = r15
            r6 = r17
            r15 = r5
        L_0x0253:
            r5 = -1
            r17 = r6
        L_0x0256:
            int r1 = r23 + 1
            r5 = r15
            r6 = r20
            r14 = r21
            r7 = r22
            r13 = 8
            r15 = r1
            r1 = r25
            goto L_0x0123
        L_0x0266:
            r15 = r5
            r20 = r6
            r22 = r7
            r21 = r14
            r6 = r17
            if (r16 == 0) goto L_0x02b2
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r1 = r0.mLayoutWidget
            r5 = r20
            r1.setWidth(r5)
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r1 = r0.mLayoutWidget
            r5 = r22
            r1.setHeight(r5)
            if (r10 == 0) goto L_0x0286
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r1 = r0.mLayoutWidget
            r1.solveGraph()
        L_0x0286:
            java.lang.String r1 = "2nd pass"
            r0.solveLinearSystem(r1)
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r1 = r0.mLayoutWidget
            int r1 = r1.getWidth()
            if (r1 >= r9) goto L_0x029a
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r1 = r0.mLayoutWidget
            r1.setWidth(r9)
            r1 = 1
            goto L_0x029b
        L_0x029a:
            r1 = 0
        L_0x029b:
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r5 = r0.mLayoutWidget
            int r5 = r5.getHeight()
            if (r5 >= r8) goto L_0x02aa
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r1 = r0.mLayoutWidget
            r1.setHeight(r8)
            r12 = 1
            goto L_0x02ab
        L_0x02aa:
            r12 = r1
        L_0x02ab:
            if (r12 == 0) goto L_0x02b2
            java.lang.String r1 = "3rd pass"
            r0.solveLinearSystem(r1)
        L_0x02b2:
            r1 = r21
            r11 = 0
        L_0x02b5:
            if (r11 >= r1) goto L_0x030a
            java.util.ArrayList<androidx.constraintlayout.solver.widgets.ConstraintWidget> r5 = r0.mVariableDimensionsWidgets
            java.lang.Object r5 = r5.get(r11)
            androidx.constraintlayout.solver.widgets.ConstraintWidget r5 = (androidx.constraintlayout.solver.widgets.ConstraintWidget) r5
            java.lang.Object r7 = r5.getCompanionWidget()
            android.view.View r7 = (android.view.View) r7
            if (r7 != 0) goto L_0x02cc
        L_0x02c7:
            r9 = 8
        L_0x02c9:
            r10 = 1073741824(0x40000000, float:2.0)
            goto L_0x0307
        L_0x02cc:
            int r8 = r7.getMeasuredWidth()
            int r9 = r5.getWidth()
            if (r8 != r9) goto L_0x02e0
            int r8 = r7.getMeasuredHeight()
            int r9 = r5.getHeight()
            if (r8 == r9) goto L_0x02c7
        L_0x02e0:
            int r8 = r5.getVisibility()
            r9 = 8
            if (r8 == r9) goto L_0x02c9
            int r8 = r5.getWidth()
            r10 = 1073741824(0x40000000, float:2.0)
            int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r10)
            int r5 = r5.getHeight()
            int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r10)
            r7.measure(r8, r5)
            androidx.constraintlayout.solver.Metrics r5 = r0.mMetrics
            if (r5 == 0) goto L_0x0307
            long r7 = r5.additionalMeasures
            long r7 = r7 + r18
            r5.additionalMeasures = r7
        L_0x0307:
            int r11 = r11 + 1
            goto L_0x02b5
        L_0x030a:
            r11 = r6
            goto L_0x030e
        L_0x030c:
            r15 = r5
            r11 = 0
        L_0x030e:
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r1 = r0.mLayoutWidget
            int r1 = r1.getWidth()
            int r1 = r1 + r4
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r4 = r0.mLayoutWidget
            int r4 = r4.getHeight()
            int r4 = r4 + r15
            r5 = 11
            if (r3 < r5) goto L_0x0359
            r3 = r25
            int r1 = android.view.ViewGroup.resolveSizeAndState(r1, r3, r11)
            int r3 = r11 << 16
            int r2 = android.view.ViewGroup.resolveSizeAndState(r4, r2, r3)
            r3 = 16777215(0xffffff, float:2.3509886E-38)
            r1 = r1 & r3
            r2 = r2 & r3
            int r3 = r0.mMaxWidth
            int r1 = java.lang.Math.min(r3, r1)
            int r3 = r0.mMaxHeight
            int r2 = java.lang.Math.min(r3, r2)
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r3 = r0.mLayoutWidget
            boolean r3 = r3.isWidthMeasuredTooSmall()
            r4 = 16777216(0x1000000, float:2.3509887E-38)
            if (r3 == 0) goto L_0x0348
            r1 = r1 | r4
        L_0x0348:
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r3 = r0.mLayoutWidget
            boolean r3 = r3.isHeightMeasuredTooSmall()
            if (r3 == 0) goto L_0x0351
            r2 = r2 | r4
        L_0x0351:
            r0.setMeasuredDimension(r1, r2)
            r0.mLastMeasureWidth = r1
            r0.mLastMeasureHeight = r2
            goto L_0x0360
        L_0x0359:
            r0.setMeasuredDimension(r1, r4)
            r0.mLastMeasureWidth = r1
            r0.mLastMeasureHeight = r4
        L_0x0360:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.widget.ConstraintLayout.onMeasure(int, int):void");
    }

    private void setSelfDimensionBehaviour(int i, int i2) {
        DimensionBehaviour dimensionBehaviour;
        int i3;
        int mode = MeasureSpec.getMode(i);
        int size = MeasureSpec.getSize(i);
        int mode2 = MeasureSpec.getMode(i2);
        int size2 = MeasureSpec.getSize(i2);
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        DimensionBehaviour dimensionBehaviour2 = DimensionBehaviour.FIXED;
        getLayoutParams();
        if (mode != Integer.MIN_VALUE) {
            if (mode == 0) {
                dimensionBehaviour = DimensionBehaviour.WRAP_CONTENT;
            } else if (mode != 1073741824) {
                dimensionBehaviour = dimensionBehaviour2;
            } else {
                i3 = Math.min(this.mMaxWidth, size) - paddingLeft;
                dimensionBehaviour = dimensionBehaviour2;
            }
            i3 = 0;
        } else {
            i3 = size;
            dimensionBehaviour = DimensionBehaviour.WRAP_CONTENT;
        }
        if (mode2 != Integer.MIN_VALUE) {
            if (mode2 == 0) {
                dimensionBehaviour2 = DimensionBehaviour.WRAP_CONTENT;
            } else if (mode2 == 1073741824) {
                size2 = Math.min(this.mMaxHeight, size2) - paddingTop;
            }
            size2 = 0;
        } else {
            dimensionBehaviour2 = DimensionBehaviour.WRAP_CONTENT;
        }
        this.mLayoutWidget.setMinWidth(0);
        this.mLayoutWidget.setMinHeight(0);
        this.mLayoutWidget.setHorizontalDimensionBehaviour(dimensionBehaviour);
        this.mLayoutWidget.setWidth(i3);
        this.mLayoutWidget.setVerticalDimensionBehaviour(dimensionBehaviour2);
        this.mLayoutWidget.setHeight(size2);
        this.mLayoutWidget.setMinWidth((this.mMinWidth - getPaddingLeft()) - getPaddingRight());
        this.mLayoutWidget.setMinHeight((this.mMinHeight - getPaddingTop()) - getPaddingBottom());
    }

    /* access modifiers changed from: protected */
    public void solveLinearSystem(String str) {
        this.mLayoutWidget.layout();
        Metrics metrics = this.mMetrics;
        if (metrics != null) {
            metrics.resolutions++;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        boolean isInEditMode = isInEditMode();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            ConstraintWidget constraintWidget = layoutParams.widget;
            if ((childAt.getVisibility() != 8 || layoutParams.isGuideline || layoutParams.isHelper || isInEditMode) && !layoutParams.isInPlaceholder) {
                int drawX = constraintWidget.getDrawX();
                int drawY = constraintWidget.getDrawY();
                int width = constraintWidget.getWidth() + drawX;
                int height = constraintWidget.getHeight() + drawY;
                childAt.layout(drawX, drawY, width, height);
                if (childAt instanceof Placeholder) {
                    View content = ((Placeholder) childAt).getContent();
                    if (content != null) {
                        content.setVisibility(0);
                        content.layout(drawX, drawY, width, height);
                    }
                }
            }
        }
        int size = this.mConstraintHelpers.size();
        if (size > 0) {
            for (int i6 = 0; i6 < size; i6++) {
                ((ConstraintHelper) this.mConstraintHelpers.get(i6)).updatePostLayout(this);
            }
        }
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* access modifiers changed from: protected */
    public android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public View getViewById(int i) {
        return (View) this.mChildrenByIds.get(i);
    }

    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isInEditMode()) {
            int childCount = getChildCount();
            float width = (float) getWidth();
            float height = (float) getHeight();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt.getVisibility() != 8) {
                    Object tag = childAt.getTag();
                    if (tag != null && (tag instanceof String)) {
                        String[] split = ((String) tag).split(",");
                        if (split.length == 4) {
                            int parseInt = Integer.parseInt(split[0]);
                            int parseInt2 = Integer.parseInt(split[1]);
                            int i2 = (int) ((((float) parseInt) / 1080.0f) * width);
                            int i3 = (int) ((((float) parseInt2) / 1920.0f) * height);
                            int parseInt3 = (int) ((((float) Integer.parseInt(split[2])) / 1080.0f) * width);
                            int parseInt4 = (int) ((((float) Integer.parseInt(split[3])) / 1920.0f) * height);
                            Paint paint = new Paint();
                            paint.setColor(-65536);
                            float f = (float) i2;
                            float f2 = (float) (i2 + parseInt3);
                            Canvas canvas2 = canvas;
                            float f3 = (float) i3;
                            float f4 = f;
                            float f5 = f;
                            float f6 = f3;
                            Paint paint2 = paint;
                            float f7 = f2;
                            Paint paint3 = paint2;
                            canvas2.drawLine(f4, f6, f7, f3, paint3);
                            float f8 = (float) (i3 + parseInt4);
                            float f9 = f2;
                            float f10 = f8;
                            canvas2.drawLine(f9, f6, f7, f10, paint3);
                            float f11 = f8;
                            float f12 = f5;
                            canvas2.drawLine(f9, f11, f12, f10, paint3);
                            float f13 = f5;
                            canvas2.drawLine(f13, f11, f12, f3, paint3);
                            Paint paint4 = paint2;
                            paint4.setColor(-16711936);
                            Paint paint5 = paint4;
                            float f14 = f2;
                            Paint paint6 = paint5;
                            canvas2.drawLine(f13, f3, f14, f8, paint6);
                            canvas2.drawLine(f13, f8, f14, f3, paint6);
                        }
                    }
                }
            }
        }
    }

    public void requestLayout() {
        super.requestLayout();
        this.mDirtyHierarchy = true;
        this.mLastMeasureWidth = -1;
        this.mLastMeasureHeight = -1;
        this.mLastMeasureWidthSize = -1;
        this.mLastMeasureHeightSize = -1;
        this.mLastMeasureWidthMode = 0;
        this.mLastMeasureHeightMode = 0;
    }
}
