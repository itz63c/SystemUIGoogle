package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.Cache;
import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor.Strength;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type;
import java.util.ArrayList;

public class ConstraintWidget {
    public static float DEFAULT_BIAS = 0.5f;
    protected ArrayList<ConstraintAnchor> mAnchors;
    ConstraintAnchor mBaseline = new ConstraintAnchor(this, Type.BASELINE);
    int mBaselineDistance;
    ConstraintAnchor mBottom = new ConstraintAnchor(this, Type.BOTTOM);
    ConstraintAnchor mCenter;
    ConstraintAnchor mCenterX = new ConstraintAnchor(this, Type.CENTER_X);
    ConstraintAnchor mCenterY = new ConstraintAnchor(this, Type.CENTER_Y);
    private float mCircleConstraintAngle = 0.0f;
    private Object mCompanionWidget;
    private String mDebugName;
    protected float mDimensionRatio;
    protected int mDimensionRatioSide;
    private int mDrawX;
    private int mDrawY;
    int mHeight;
    float mHorizontalBiasPercent;
    int mHorizontalChainStyle;
    public int mHorizontalResolution = -1;
    ConstraintAnchor mLeft = new ConstraintAnchor(this, Type.LEFT);
    protected ConstraintAnchor[] mListAnchors;
    protected DimensionBehaviour[] mListDimensionBehaviors;
    protected ConstraintWidget[] mListNextMatchConstraintsWidget;
    protected ConstraintWidget[] mListNextVisibleWidget;
    int mMatchConstraintDefaultHeight = 0;
    int mMatchConstraintDefaultWidth = 0;
    int mMatchConstraintMaxHeight = 0;
    int mMatchConstraintMaxWidth = 0;
    int mMatchConstraintMinHeight = 0;
    int mMatchConstraintMinWidth = 0;
    float mMatchConstraintPercentHeight = 1.0f;
    float mMatchConstraintPercentWidth = 1.0f;
    private int[] mMaxDimension = {Integer.MAX_VALUE, Integer.MAX_VALUE};
    protected int mMinHeight;
    protected int mMinWidth;
    protected int mOffsetX;
    protected int mOffsetY;
    ConstraintWidget mParent;
    ResolutionDimension mResolutionHeight;
    ResolutionDimension mResolutionWidth;
    float mResolvedDimensionRatio = 1.0f;
    int mResolvedDimensionRatioSide = -1;
    int[] mResolvedMatchConstraintDefault = new int[2];
    ConstraintAnchor mRight = new ConstraintAnchor(this, Type.RIGHT);
    ConstraintAnchor mTop = new ConstraintAnchor(this, Type.TOP);
    private String mType;
    float mVerticalBiasPercent;
    int mVerticalChainStyle;
    public int mVerticalResolution = -1;
    private int mVisibility;
    float[] mWeight;
    int mWidth;
    private int mWrapHeight;
    private int mWrapWidth;

    /* renamed from: mX */
    protected int f6mX;

    /* renamed from: mY */
    protected int f7mY;

    /* renamed from: androidx.constraintlayout.solver.widgets.ConstraintWidget$1 */
    static /* synthetic */ class C00951 {

        /* renamed from: $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type */
        static final /* synthetic */ int[] f8x4c44d048;

        /* renamed from: $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintWidget$DimensionBehaviour */
        static final /* synthetic */ int[] f9xdde91696;

        /* JADX WARNING: Can't wrap try/catch for region: R(29:0|(2:1|2)|3|(2:5|6)|7|9|10|11|(2:13|14)|15|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|36) */
        /* JADX WARNING: Can't wrap try/catch for region: R(31:0|1|2|3|(2:5|6)|7|9|10|11|13|14|15|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|36) */
        /* JADX WARNING: Can't wrap try/catch for region: R(32:0|1|2|3|5|6|7|9|10|11|13|14|15|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|36) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x0044 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x004e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0058 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x0062 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x006d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:29:0x0078 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:31:0x0083 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:33:0x008f */
        static {
            /*
                androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r0 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f9xdde91696 = r0
                r1 = 1
                androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r2 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.FIXED     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r0[r2] = r1     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                r0 = 2
                int[] r2 = f9xdde91696     // Catch:{ NoSuchFieldError -> 0x001d }
                androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r3 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT     // Catch:{ NoSuchFieldError -> 0x001d }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2[r3] = r0     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                r2 = 3
                int[] r3 = f9xdde91696     // Catch:{ NoSuchFieldError -> 0x0028 }
                androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r4 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_PARENT     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r3[r4] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                r3 = 4
                int[] r4 = f9xdde91696     // Catch:{ NoSuchFieldError -> 0x0033 }
                androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r5 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type[] r4 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                f8x4c44d048 = r4
                androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r5 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.LEFT     // Catch:{ NoSuchFieldError -> 0x0044 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0044 }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x0044 }
            L_0x0044:
                int[] r1 = f8x4c44d048     // Catch:{ NoSuchFieldError -> 0x004e }
                androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r4 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.TOP     // Catch:{ NoSuchFieldError -> 0x004e }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x004e }
                r1[r4] = r0     // Catch:{ NoSuchFieldError -> 0x004e }
            L_0x004e:
                int[] r0 = f8x4c44d048     // Catch:{ NoSuchFieldError -> 0x0058 }
                androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r1 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.RIGHT     // Catch:{ NoSuchFieldError -> 0x0058 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0058 }
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0058 }
            L_0x0058:
                int[] r0 = f8x4c44d048     // Catch:{ NoSuchFieldError -> 0x0062 }
                androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r1 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BOTTOM     // Catch:{ NoSuchFieldError -> 0x0062 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0062 }
                r0[r1] = r3     // Catch:{ NoSuchFieldError -> 0x0062 }
            L_0x0062:
                int[] r0 = f8x4c44d048     // Catch:{ NoSuchFieldError -> 0x006d }
                androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r1 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BASELINE     // Catch:{ NoSuchFieldError -> 0x006d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x006d }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x006d }
            L_0x006d:
                int[] r0 = f8x4c44d048     // Catch:{ NoSuchFieldError -> 0x0078 }
                androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r1 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.CENTER     // Catch:{ NoSuchFieldError -> 0x0078 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0078 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0078 }
            L_0x0078:
                int[] r0 = f8x4c44d048     // Catch:{ NoSuchFieldError -> 0x0083 }
                androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r1 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.CENTER_X     // Catch:{ NoSuchFieldError -> 0x0083 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0083 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0083 }
            L_0x0083:
                int[] r0 = f8x4c44d048     // Catch:{ NoSuchFieldError -> 0x008f }
                androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r1 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.CENTER_Y     // Catch:{ NoSuchFieldError -> 0x008f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x008f }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x008f }
            L_0x008f:
                int[] r0 = f8x4c44d048     // Catch:{ NoSuchFieldError -> 0x009b }
                androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r1 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.NONE     // Catch:{ NoSuchFieldError -> 0x009b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x009b }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x009b }
            L_0x009b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.solver.widgets.ConstraintWidget.C00951.<clinit>():void");
        }
    }

    public enum DimensionBehaviour {
        FIXED,
        WRAP_CONTENT,
        MATCH_CONSTRAINT,
        MATCH_PARENT
    }

    public void resolve() {
    }

    public void setHeightWrapContent(boolean z) {
    }

    public void setWidthWrapContent(boolean z) {
    }

    public void setMaxWidth(int i) {
        this.mMaxDimension[0] = i;
    }

    public void setMaxHeight(int i) {
        this.mMaxDimension[1] = i;
    }

    public boolean isSpreadWidth() {
        return this.mMatchConstraintDefaultWidth == 0 && this.mDimensionRatio == 0.0f && this.mMatchConstraintMinWidth == 0 && this.mMatchConstraintMaxWidth == 0 && this.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_CONSTRAINT;
    }

    public boolean isSpreadHeight() {
        return this.mMatchConstraintDefaultHeight == 0 && this.mDimensionRatio == 0.0f && this.mMatchConstraintMinHeight == 0 && this.mMatchConstraintMaxHeight == 0 && this.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_CONSTRAINT;
    }

    public void reset() {
        this.mLeft.reset();
        this.mTop.reset();
        this.mRight.reset();
        this.mBottom.reset();
        this.mBaseline.reset();
        this.mCenterX.reset();
        this.mCenterY.reset();
        this.mCenter.reset();
        this.mParent = null;
        this.mCircleConstraintAngle = 0.0f;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.f6mX = 0;
        this.f7mY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mWrapWidth = 0;
        this.mWrapHeight = 0;
        float f = DEFAULT_BIAS;
        this.mHorizontalBiasPercent = f;
        this.mVerticalBiasPercent = f;
        DimensionBehaviour[] dimensionBehaviourArr = this.mListDimensionBehaviors;
        DimensionBehaviour dimensionBehaviour = DimensionBehaviour.FIXED;
        dimensionBehaviourArr[0] = dimensionBehaviour;
        dimensionBehaviourArr[1] = dimensionBehaviour;
        this.mCompanionWidget = null;
        this.mVisibility = 0;
        this.mType = null;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        float[] fArr = this.mWeight;
        fArr[0] = -1.0f;
        fArr[1] = -1.0f;
        this.mHorizontalResolution = -1;
        this.mVerticalResolution = -1;
        int[] iArr = this.mMaxDimension;
        iArr[0] = Integer.MAX_VALUE;
        iArr[1] = Integer.MAX_VALUE;
        this.mMatchConstraintDefaultWidth = 0;
        this.mMatchConstraintDefaultHeight = 0;
        this.mMatchConstraintPercentWidth = 1.0f;
        this.mMatchConstraintPercentHeight = 1.0f;
        this.mMatchConstraintMaxWidth = Integer.MAX_VALUE;
        this.mMatchConstraintMaxHeight = Integer.MAX_VALUE;
        this.mMatchConstraintMinWidth = 0;
        this.mMatchConstraintMinHeight = 0;
        this.mResolvedDimensionRatioSide = -1;
        this.mResolvedDimensionRatio = 1.0f;
        ResolutionDimension resolutionDimension = this.mResolutionWidth;
        if (resolutionDimension != null) {
            resolutionDimension.reset();
        }
        ResolutionDimension resolutionDimension2 = this.mResolutionHeight;
        if (resolutionDimension2 != null) {
            resolutionDimension2.reset();
        }
    }

    public void resetResolutionNodes() {
        for (int i = 0; i < 6; i++) {
            this.mListAnchors[i].getResolutionNode().reset();
        }
    }

    public void updateResolutionNodes() {
        for (int i = 0; i < 6; i++) {
            this.mListAnchors[i].getResolutionNode().update();
        }
    }

    public void analyze(int i) {
        Optimizer.analyze(i, this);
    }

    public ResolutionDimension getResolutionWidth() {
        if (this.mResolutionWidth == null) {
            this.mResolutionWidth = new ResolutionDimension();
        }
        return this.mResolutionWidth;
    }

    public ResolutionDimension getResolutionHeight() {
        if (this.mResolutionHeight == null) {
            this.mResolutionHeight = new ResolutionDimension();
        }
        return this.mResolutionHeight;
    }

    public ConstraintWidget() {
        ConstraintAnchor constraintAnchor = new ConstraintAnchor(this, Type.CENTER);
        this.mCenter = constraintAnchor;
        this.mListAnchors = new ConstraintAnchor[]{this.mLeft, this.mRight, this.mTop, this.mBottom, this.mBaseline, constraintAnchor};
        this.mAnchors = new ArrayList<>();
        DimensionBehaviour dimensionBehaviour = DimensionBehaviour.FIXED;
        this.mListDimensionBehaviors = new DimensionBehaviour[]{dimensionBehaviour, dimensionBehaviour};
        this.mParent = null;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.f6mX = 0;
        this.f7mY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        float f = DEFAULT_BIAS;
        this.mHorizontalBiasPercent = f;
        this.mVerticalBiasPercent = f;
        this.mVisibility = 0;
        this.mDebugName = null;
        this.mType = null;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        this.mWeight = new float[]{-1.0f, -1.0f};
        this.mListNextMatchConstraintsWidget = new ConstraintWidget[]{null, null};
        this.mListNextVisibleWidget = new ConstraintWidget[]{null, null};
        addAnchors();
    }

    public void resetSolverVariables(Cache cache) {
        this.mLeft.resetSolverVariable(cache);
        this.mTop.resetSolverVariable(cache);
        this.mRight.resetSolverVariable(cache);
        this.mBottom.resetSolverVariable(cache);
        this.mBaseline.resetSolverVariable(cache);
        this.mCenter.resetSolverVariable(cache);
        this.mCenterX.resetSolverVariable(cache);
        this.mCenterY.resetSolverVariable(cache);
    }

    private void addAnchors() {
        this.mAnchors.add(this.mLeft);
        this.mAnchors.add(this.mTop);
        this.mAnchors.add(this.mRight);
        this.mAnchors.add(this.mBottom);
        this.mAnchors.add(this.mCenterX);
        this.mAnchors.add(this.mCenterY);
        this.mAnchors.add(this.mCenter);
        this.mAnchors.add(this.mBaseline);
    }

    public ConstraintWidget getParent() {
        return this.mParent;
    }

    public void setParent(ConstraintWidget constraintWidget) {
        this.mParent = constraintWidget;
    }

    public void connectCircularConstraint(ConstraintWidget constraintWidget, float f, int i) {
        Type type = Type.CENTER;
        immediateConnect(type, constraintWidget, type, i, 0);
        this.mCircleConstraintAngle = f;
    }

    public void setVisibility(int i) {
        this.mVisibility = i;
    }

    public int getVisibility() {
        return this.mVisibility;
    }

    public String getDebugName() {
        return this.mDebugName;
    }

    public void setDebugName(String str) {
        this.mDebugName = str;
    }

    public void createObjectVariables(LinearSystem linearSystem) {
        linearSystem.createObjectVariable(this.mLeft);
        linearSystem.createObjectVariable(this.mTop);
        linearSystem.createObjectVariable(this.mRight);
        linearSystem.createObjectVariable(this.mBottom);
        if (this.mBaselineDistance > 0) {
            linearSystem.createObjectVariable(this.mBaseline);
        }
    }

    public String toString() {
        String str;
        StringBuilder sb = new StringBuilder();
        String str2 = " ";
        String str3 = "";
        if (this.mType != null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("type: ");
            sb2.append(this.mType);
            sb2.append(str2);
            str = sb2.toString();
        } else {
            str = str3;
        }
        sb.append(str);
        if (this.mDebugName != null) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("id: ");
            sb3.append(this.mDebugName);
            sb3.append(str2);
            str3 = sb3.toString();
        }
        sb.append(str3);
        sb.append("(");
        sb.append(this.f6mX);
        sb.append(", ");
        sb.append(this.f7mY);
        sb.append(") - (");
        sb.append(this.mWidth);
        String str4 = " x ";
        sb.append(str4);
        sb.append(this.mHeight);
        sb.append(") wrap: (");
        sb.append(this.mWrapWidth);
        sb.append(str4);
        sb.append(this.mWrapHeight);
        sb.append(")");
        return sb.toString();
    }

    public int getX() {
        return this.f6mX;
    }

    public int getY() {
        return this.f7mY;
    }

    public int getWidth() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mWidth;
    }

    public int getWrapWidth() {
        return this.mWrapWidth;
    }

    public int getHeight() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mHeight;
    }

    public int getWrapHeight() {
        return this.mWrapHeight;
    }

    public int getDrawX() {
        return this.mDrawX + this.mOffsetX;
    }

    public int getDrawY() {
        return this.mDrawY + this.mOffsetY;
    }

    /* access modifiers changed from: protected */
    public int getRootX() {
        return this.f6mX + this.mOffsetX;
    }

    /* access modifiers changed from: protected */
    public int getRootY() {
        return this.f7mY + this.mOffsetY;
    }

    public int getRight() {
        return getX() + this.mWidth;
    }

    public int getBottom() {
        return getY() + this.mHeight;
    }

    public float getHorizontalBiasPercent() {
        return this.mHorizontalBiasPercent;
    }

    public boolean hasBaseline() {
        return this.mBaselineDistance > 0;
    }

    public int getBaselineDistance() {
        return this.mBaselineDistance;
    }

    public Object getCompanionWidget() {
        return this.mCompanionWidget;
    }

    public ArrayList<ConstraintAnchor> getAnchors() {
        return this.mAnchors;
    }

    public void setX(int i) {
        this.f6mX = i;
    }

    public void setY(int i) {
        this.f7mY = i;
    }

    public void setOrigin(int i, int i2) {
        this.f6mX = i;
        this.f7mY = i2;
    }

    public void setOffset(int i, int i2) {
        this.mOffsetX = i;
        this.mOffsetY = i2;
    }

    public void updateDrawPosition() {
        int i = this.f6mX;
        int i2 = this.f7mY;
        this.mDrawX = i;
        this.mDrawY = i2;
    }

    public void setWidth(int i) {
        this.mWidth = i;
        int i2 = this.mMinWidth;
        if (i < i2) {
            this.mWidth = i2;
        }
    }

    public void setHeight(int i) {
        this.mHeight = i;
        int i2 = this.mMinHeight;
        if (i < i2) {
            this.mHeight = i2;
        }
    }

    public void setHorizontalMatchStyle(int i, int i2, int i3, float f) {
        this.mMatchConstraintDefaultWidth = i;
        this.mMatchConstraintMinWidth = i2;
        this.mMatchConstraintMaxWidth = i3;
        this.mMatchConstraintPercentWidth = f;
        if (f < 1.0f && i == 0) {
            this.mMatchConstraintDefaultWidth = 2;
        }
    }

    public void setVerticalMatchStyle(int i, int i2, int i3, float f) {
        this.mMatchConstraintDefaultHeight = i;
        this.mMatchConstraintMinHeight = i2;
        this.mMatchConstraintMaxHeight = i3;
        this.mMatchConstraintPercentHeight = f;
        if (f < 1.0f && i == 0) {
            this.mMatchConstraintDefaultHeight = 2;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x0089  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setDimensionRatio(java.lang.String r9) {
        /*
            r8 = this;
            r0 = 0
            if (r9 == 0) goto L_0x008e
            int r1 = r9.length()
            if (r1 != 0) goto L_0x000b
            goto L_0x008e
        L_0x000b:
            r1 = -1
            int r2 = r9.length()
            r3 = 44
            int r3 = r9.indexOf(r3)
            r4 = 0
            r5 = 1
            if (r3 <= 0) goto L_0x0037
            int r6 = r2 + -1
            if (r3 >= r6) goto L_0x0037
            java.lang.String r6 = r9.substring(r4, r3)
            java.lang.String r7 = "W"
            boolean r7 = r6.equalsIgnoreCase(r7)
            if (r7 == 0) goto L_0x002c
            r1 = r4
            goto L_0x0035
        L_0x002c:
            java.lang.String r4 = "H"
            boolean r4 = r6.equalsIgnoreCase(r4)
            if (r4 == 0) goto L_0x0035
            r1 = r5
        L_0x0035:
            int r4 = r3 + 1
        L_0x0037:
            r3 = 58
            int r3 = r9.indexOf(r3)
            if (r3 < 0) goto L_0x0075
            int r2 = r2 - r5
            if (r3 >= r2) goto L_0x0075
            java.lang.String r2 = r9.substring(r4, r3)
            int r3 = r3 + r5
            java.lang.String r9 = r9.substring(r3)
            int r3 = r2.length()
            if (r3 <= 0) goto L_0x0084
            int r3 = r9.length()
            if (r3 <= 0) goto L_0x0084
            float r2 = java.lang.Float.parseFloat(r2)     // Catch:{ NumberFormatException -> 0x0084 }
            float r9 = java.lang.Float.parseFloat(r9)     // Catch:{ NumberFormatException -> 0x0084 }
            int r3 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r3 <= 0) goto L_0x0084
            int r3 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r3 <= 0) goto L_0x0084
            if (r1 != r5) goto L_0x006f
            float r9 = r9 / r2
            float r9 = java.lang.Math.abs(r9)     // Catch:{ NumberFormatException -> 0x0084 }
            goto L_0x0085
        L_0x006f:
            float r2 = r2 / r9
            float r9 = java.lang.Math.abs(r2)     // Catch:{ NumberFormatException -> 0x0084 }
            goto L_0x0085
        L_0x0075:
            java.lang.String r9 = r9.substring(r4)
            int r2 = r9.length()
            if (r2 <= 0) goto L_0x0084
            float r9 = java.lang.Float.parseFloat(r9)     // Catch:{ NumberFormatException -> 0x0084 }
            goto L_0x0085
        L_0x0084:
            r9 = r0
        L_0x0085:
            int r0 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x008d
            r8.mDimensionRatio = r9
            r8.mDimensionRatioSide = r1
        L_0x008d:
            return
        L_0x008e:
            r8.mDimensionRatio = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.solver.widgets.ConstraintWidget.setDimensionRatio(java.lang.String):void");
    }

    public void setHorizontalBiasPercent(float f) {
        this.mHorizontalBiasPercent = f;
    }

    public void setVerticalBiasPercent(float f) {
        this.mVerticalBiasPercent = f;
    }

    public void setMinWidth(int i) {
        if (i < 0) {
            this.mMinWidth = 0;
        } else {
            this.mMinWidth = i;
        }
    }

    public void setMinHeight(int i) {
        if (i < 0) {
            this.mMinHeight = 0;
        } else {
            this.mMinHeight = i;
        }
    }

    public void setWrapWidth(int i) {
        this.mWrapWidth = i;
    }

    public void setWrapHeight(int i) {
        this.mWrapHeight = i;
    }

    public void setFrame(int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        int i6 = i4 - i2;
        this.f6mX = i;
        this.f7mY = i2;
        if (this.mVisibility == 8) {
            this.mWidth = 0;
            this.mHeight = 0;
            return;
        }
        if (this.mListDimensionBehaviors[0] == DimensionBehaviour.FIXED) {
            int i7 = this.mWidth;
            if (i5 < i7) {
                i5 = i7;
            }
        }
        if (this.mListDimensionBehaviors[1] == DimensionBehaviour.FIXED) {
            int i8 = this.mHeight;
            if (i6 < i8) {
                i6 = i8;
            }
        }
        this.mWidth = i5;
        this.mHeight = i6;
        int i9 = this.mMinHeight;
        if (i6 < i9) {
            this.mHeight = i9;
        }
        int i10 = this.mWidth;
        int i11 = this.mMinWidth;
        if (i10 < i11) {
            this.mWidth = i11;
        }
    }

    public void setHorizontalDimension(int i, int i2) {
        this.f6mX = i;
        int i3 = i2 - i;
        this.mWidth = i3;
        int i4 = this.mMinWidth;
        if (i3 < i4) {
            this.mWidth = i4;
        }
    }

    public void setVerticalDimension(int i, int i2) {
        this.f7mY = i;
        int i3 = i2 - i;
        this.mHeight = i3;
        int i4 = this.mMinHeight;
        if (i3 < i4) {
            this.mHeight = i4;
        }
    }

    public void setBaselineDistance(int i) {
        this.mBaselineDistance = i;
    }

    public void setCompanionWidget(Object obj) {
        this.mCompanionWidget = obj;
    }

    public void setHorizontalWeight(float f) {
        this.mWeight[0] = f;
    }

    public void setVerticalWeight(float f) {
        this.mWeight[1] = f;
    }

    public void setHorizontalChainStyle(int i) {
        this.mHorizontalChainStyle = i;
    }

    public void setVerticalChainStyle(int i) {
        this.mVerticalChainStyle = i;
    }

    public boolean allowedInBarrier() {
        return this.mVisibility != 8;
    }

    public void immediateConnect(Type type, ConstraintWidget constraintWidget, Type type2, int i, int i2) {
        getAnchor(type).connect(constraintWidget.getAnchor(type2), i, i2, Strength.STRONG, 0, true);
    }

    public void resetAnchors() {
        ConstraintWidget parent = getParent();
        if (parent == null || !(parent instanceof ConstraintWidgetContainer) || !((ConstraintWidgetContainer) getParent()).handlesInternalConstraints()) {
            int size = this.mAnchors.size();
            for (int i = 0; i < size; i++) {
                ((ConstraintAnchor) this.mAnchors.get(i)).reset();
            }
        }
    }

    public ConstraintAnchor getAnchor(Type type) {
        switch (C00951.f8x4c44d048[type.ordinal()]) {
            case 1:
                return this.mLeft;
            case 2:
                return this.mTop;
            case 3:
                return this.mRight;
            case 4:
                return this.mBottom;
            case 5:
                return this.mBaseline;
            case 6:
                return this.mCenter;
            case 7:
                return this.mCenterX;
            case 8:
                return this.mCenterY;
            case 9:
                return null;
            default:
                throw new AssertionError(type.name());
        }
    }

    public DimensionBehaviour getHorizontalDimensionBehaviour() {
        return this.mListDimensionBehaviors[0];
    }

    public DimensionBehaviour getVerticalDimensionBehaviour() {
        return this.mListDimensionBehaviors[1];
    }

    public void setHorizontalDimensionBehaviour(DimensionBehaviour dimensionBehaviour) {
        this.mListDimensionBehaviors[0] = dimensionBehaviour;
        if (dimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            setWidth(this.mWrapWidth);
        }
    }

    public void setVerticalDimensionBehaviour(DimensionBehaviour dimensionBehaviour) {
        this.mListDimensionBehaviors[1] = dimensionBehaviour;
        if (dimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            setHeight(this.mWrapHeight);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:129:0x01e3  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x01ed  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01f9  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0212  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0279  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x028a A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x02ad  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x02e2  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x02ec  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x02f5  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x02fb  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x0303  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x033a  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x0363  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x036d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addToSolver(androidx.constraintlayout.solver.LinearSystem r39) {
        /*
            r38 = this;
            r15 = r38
            r14 = r39
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r15.mLeft
            androidx.constraintlayout.solver.SolverVariable r21 = r14.createObjectVariable(r0)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r15.mRight
            androidx.constraintlayout.solver.SolverVariable r10 = r14.createObjectVariable(r0)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r15.mTop
            androidx.constraintlayout.solver.SolverVariable r6 = r14.createObjectVariable(r0)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r15.mBottom
            androidx.constraintlayout.solver.SolverVariable r4 = r14.createObjectVariable(r0)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r15.mBaseline
            androidx.constraintlayout.solver.SolverVariable r3 = r14.createObjectVariable(r0)
            androidx.constraintlayout.solver.widgets.ConstraintWidget r0 = r15.mParent
            r1 = 8
            r2 = 1
            r13 = 0
            if (r0 == 0) goto L_0x00ee
            if (r0 == 0) goto L_0x0036
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r0 = r0.mListDimensionBehaviors
            r0 = r0[r13]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r5 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r0 != r5) goto L_0x0036
            r0 = r2
            goto L_0x0037
        L_0x0036:
            r0 = r13
        L_0x0037:
            androidx.constraintlayout.solver.widgets.ConstraintWidget r5 = r15.mParent
            if (r5 == 0) goto L_0x0045
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r5 = r5.mListDimensionBehaviors
            r5 = r5[r2]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r7 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r5 != r7) goto L_0x0045
            r5 = r2
            goto L_0x0046
        L_0x0045:
            r5 = r13
        L_0x0046:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r7 = r15.mLeft
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r7.mTarget
            if (r8 == 0) goto L_0x0061
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r8.mTarget
            if (r8 == r7) goto L_0x0061
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r7 = r15.mRight
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r7.mTarget
            if (r8 == 0) goto L_0x0061
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r8.mTarget
            if (r8 != r7) goto L_0x0061
            androidx.constraintlayout.solver.widgets.ConstraintWidget r7 = r15.mParent
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r7 = (androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer) r7
            r7.addChain(r15, r13)
        L_0x0061:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r7 = r15.mLeft
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r7.mTarget
            if (r8 == 0) goto L_0x006b
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r8.mTarget
            if (r8 == r7) goto L_0x0075
        L_0x006b:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r7 = r15.mRight
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r7.mTarget
            if (r8 == 0) goto L_0x0077
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r8.mTarget
            if (r8 != r7) goto L_0x0077
        L_0x0075:
            r7 = r2
            goto L_0x0078
        L_0x0077:
            r7 = r13
        L_0x0078:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r15.mTop
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r8.mTarget
            if (r9 == 0) goto L_0x0093
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            if (r9 == r8) goto L_0x0093
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r15.mBottom
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r8.mTarget
            if (r9 == 0) goto L_0x0093
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            if (r9 != r8) goto L_0x0093
            androidx.constraintlayout.solver.widgets.ConstraintWidget r8 = r15.mParent
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r8 = (androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer) r8
            r8.addChain(r15, r2)
        L_0x0093:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r15.mTop
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r8.mTarget
            if (r9 == 0) goto L_0x009d
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            if (r9 == r8) goto L_0x00a7
        L_0x009d:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r15.mBottom
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r8.mTarget
            if (r9 == 0) goto L_0x00a9
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            if (r9 != r8) goto L_0x00a9
        L_0x00a7:
            r8 = r2
            goto L_0x00aa
        L_0x00a9:
            r8 = r13
        L_0x00aa:
            if (r0 == 0) goto L_0x00c7
            int r9 = r15.mVisibility
            if (r9 == r1) goto L_0x00c7
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r15.mLeft
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            if (r9 != 0) goto L_0x00c7
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r15.mRight
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            if (r9 != 0) goto L_0x00c7
            androidx.constraintlayout.solver.widgets.ConstraintWidget r9 = r15.mParent
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r9.mRight
            androidx.constraintlayout.solver.SolverVariable r9 = r14.createObjectVariable(r9)
            r14.addGreaterThan(r9, r10, r13, r2)
        L_0x00c7:
            if (r5 == 0) goto L_0x00e8
            int r9 = r15.mVisibility
            if (r9 == r1) goto L_0x00e8
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r15.mTop
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            if (r9 != 0) goto L_0x00e8
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r15.mBottom
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            if (r9 != 0) goto L_0x00e8
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r15.mBaseline
            if (r9 != 0) goto L_0x00e8
            androidx.constraintlayout.solver.widgets.ConstraintWidget r9 = r15.mParent
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r9 = r9.mBottom
            androidx.constraintlayout.solver.SolverVariable r9 = r14.createObjectVariable(r9)
            r14.addGreaterThan(r9, r4, r13, r2)
        L_0x00e8:
            r12 = r5
            r16 = r7
            r22 = r8
            goto L_0x00f4
        L_0x00ee:
            r0 = r13
            r12 = r0
            r16 = r12
            r22 = r16
        L_0x00f4:
            int r5 = r15.mWidth
            int r7 = r15.mMinWidth
            if (r5 >= r7) goto L_0x00fb
            r5 = r7
        L_0x00fb:
            int r7 = r15.mHeight
            int r8 = r15.mMinHeight
            if (r7 >= r8) goto L_0x0102
            r7 = r8
        L_0x0102:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r8 = r15.mListDimensionBehaviors
            r8 = r8[r13]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r9 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r8 == r9) goto L_0x010c
            r8 = r2
            goto L_0x010d
        L_0x010c:
            r8 = r13
        L_0x010d:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r9 = r15.mListDimensionBehaviors
            r9 = r9[r2]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r11 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r9 == r11) goto L_0x0117
            r9 = r2
            goto L_0x0118
        L_0x0117:
            r9 = r13
        L_0x0118:
            int r11 = r15.mDimensionRatioSide
            r15.mResolvedDimensionRatioSide = r11
            float r11 = r15.mDimensionRatio
            r15.mResolvedDimensionRatio = r11
            int r2 = r15.mMatchConstraintDefaultWidth
            int r13 = r15.mMatchConstraintDefaultHeight
            r18 = 0
            int r11 = (r11 > r18 ? 1 : (r11 == r18 ? 0 : -1))
            r18 = 4
            if (r11 <= 0) goto L_0x01cd
            int r11 = r15.mVisibility
            r1 = 8
            if (r11 == r1) goto L_0x01cd
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r1 = r15.mListDimensionBehaviors
            r11 = 0
            r1 = r1[r11]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r11 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            r24 = r3
            if (r1 != r11) goto L_0x0140
            if (r2 != 0) goto L_0x0140
            r2 = 3
        L_0x0140:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r1 = r15.mListDimensionBehaviors
            r11 = 1
            r1 = r1[r11]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r11 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r1 != r11) goto L_0x014c
            if (r13 != 0) goto L_0x014c
            r13 = 3
        L_0x014c:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r1 = r15.mListDimensionBehaviors
            r11 = 0
            r3 = r1[r11]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r11 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r3 != r11) goto L_0x0163
            r3 = 1
            r1 = r1[r3]
            if (r1 != r11) goto L_0x0163
            r1 = 3
            if (r2 != r1) goto L_0x0164
            if (r13 != r1) goto L_0x0164
            r15.setupDimensionRatio(r0, r12, r8, r9)
            goto L_0x01c2
        L_0x0163:
            r1 = 3
        L_0x0164:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r3 = r15.mListDimensionBehaviors
            r8 = 0
            r9 = r3[r8]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r11 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r9 != r11) goto L_0x018f
            if (r2 != r1) goto L_0x018f
            r15.mResolvedDimensionRatioSide = r8
            float r1 = r15.mResolvedDimensionRatio
            int r5 = r15.mHeight
            float r5 = (float) r5
            float r1 = r1 * r5
            int r1 = (int) r1
            r8 = 1
            r3 = r3[r8]
            r25 = r1
            if (r3 == r11) goto L_0x0186
            r26 = r7
            r29 = r13
            r28 = r18
            goto L_0x01d7
        L_0x0186:
            r28 = r2
            r26 = r7
            r27 = r8
            r29 = r13
            goto L_0x01d9
        L_0x018f:
            r8 = 1
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r1 = r15.mListDimensionBehaviors
            r1 = r1[r8]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r3 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r1 != r3) goto L_0x01c2
            r1 = 3
            if (r13 != r1) goto L_0x01c2
            r15.mResolvedDimensionRatioSide = r8
            int r1 = r15.mDimensionRatioSide
            r3 = -1
            if (r1 != r3) goto L_0x01a9
            r1 = 1065353216(0x3f800000, float:1.0)
            float r3 = r15.mResolvedDimensionRatio
            float r1 = r1 / r3
            r15.mResolvedDimensionRatio = r1
        L_0x01a9:
            float r1 = r15.mResolvedDimensionRatio
            int r3 = r15.mWidth
            float r3 = (float) r3
            float r1 = r1 * r3
            int r1 = (int) r1
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r3 = r15.mListDimensionBehaviors
            r7 = 0
            r3 = r3[r7]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r7 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            r26 = r1
            r28 = r2
            r25 = r5
            if (r3 == r7) goto L_0x01c8
            r29 = r18
            goto L_0x01d7
        L_0x01c2:
            r28 = r2
            r25 = r5
            r26 = r7
        L_0x01c8:
            r29 = r13
            r27 = 1
            goto L_0x01d9
        L_0x01cd:
            r24 = r3
            r28 = r2
            r25 = r5
            r26 = r7
            r29 = r13
        L_0x01d7:
            r27 = 0
        L_0x01d9:
            int[] r1 = r15.mResolvedMatchConstraintDefault
            r2 = 0
            r1[r2] = r28
            r2 = 1
            r1[r2] = r29
            if (r27 == 0) goto L_0x01ed
            int r1 = r15.mResolvedDimensionRatioSide
            r2 = -1
            if (r1 == 0) goto L_0x01ea
            if (r1 != r2) goto L_0x01ee
        L_0x01ea:
            r23 = 1
            goto L_0x01f0
        L_0x01ed:
            r2 = -1
        L_0x01ee:
            r23 = 0
        L_0x01f0:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r1 = r15.mListDimensionBehaviors
            r3 = 0
            r1 = r1[r3]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r3 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r1 != r3) goto L_0x0200
            boolean r1 = r15 instanceof androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer
            if (r1 == 0) goto L_0x0200
            r30 = 1
            goto L_0x0202
        L_0x0200:
            r30 = 0
        L_0x0202:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r1 = r15.mCenter
            boolean r1 = r1.isConnected()
            r3 = 1
            r31 = r1 ^ 1
            int r1 = r15.mHorizontalResolution
            r13 = 2
            r32 = 0
            if (r1 == r13) goto L_0x0279
            androidx.constraintlayout.solver.widgets.ConstraintWidget r1 = r15.mParent
            if (r1 == 0) goto L_0x021f
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r1 = r1.mRight
            androidx.constraintlayout.solver.SolverVariable r1 = r14.createObjectVariable(r1)
            r20 = r1
            goto L_0x0221
        L_0x021f:
            r20 = r32
        L_0x0221:
            androidx.constraintlayout.solver.widgets.ConstraintWidget r1 = r15.mParent
            if (r1 == 0) goto L_0x022e
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r1 = r1.mLeft
            androidx.constraintlayout.solver.SolverVariable r1 = r14.createObjectVariable(r1)
            r33 = r1
            goto L_0x0230
        L_0x022e:
            r33 = r32
        L_0x0230:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r1 = r15.mListDimensionBehaviors
            r17 = 0
            r5 = r1[r17]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r7 = r15.mLeft
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r15.mRight
            int r9 = r15.f6mX
            int r11 = r15.mMinWidth
            int[] r1 = r15.mMaxDimension
            r1 = r1[r17]
            r34 = r12
            r12 = r1
            float r1 = r15.mHorizontalBiasPercent
            r13 = r1
            int r1 = r15.mMatchConstraintMinWidth
            r17 = r1
            int r1 = r15.mMatchConstraintMaxWidth
            r18 = r1
            float r1 = r15.mMatchConstraintPercentWidth
            r19 = r1
            r35 = r0
            r0 = r38
            r1 = r39
            r3 = r2
            r2 = r35
            r36 = r24
            r3 = r33
            r24 = r4
            r4 = r20
            r37 = r6
            r6 = r30
            r30 = r10
            r10 = r25
            r14 = r23
            r15 = r16
            r16 = r28
            r20 = r31
            r0.applyConstraints(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            goto L_0x0283
        L_0x0279:
            r37 = r6
            r30 = r10
            r34 = r12
            r36 = r24
            r24 = r4
        L_0x0283:
            r15 = r38
            int r0 = r15.mVerticalResolution
            r1 = 2
            if (r0 != r1) goto L_0x028b
            return
        L_0x028b:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r0 = r15.mListDimensionBehaviors
            r14 = 1
            r0 = r0[r14]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r1 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r0 != r1) goto L_0x029a
            boolean r0 = r15 instanceof androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer
            if (r0 == 0) goto L_0x029a
            r6 = r14
            goto L_0x029b
        L_0x029a:
            r6 = 0
        L_0x029b:
            if (r27 == 0) goto L_0x02a7
            int r0 = r15.mResolvedDimensionRatioSide
            if (r0 == r14) goto L_0x02a4
            r1 = -1
            if (r0 != r1) goto L_0x02a7
        L_0x02a4:
            r16 = r14
            goto L_0x02a9
        L_0x02a7:
            r16 = 0
        L_0x02a9:
            int r0 = r15.mBaselineDistance
            if (r0 <= 0) goto L_0x02e2
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r15.mBaseline
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r0 = r0.getResolutionNode()
            int r0 = r0.state
            if (r0 != r14) goto L_0x02c3
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r15.mBaseline
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r0 = r0.getResolutionNode()
            r10 = r39
            r0.addResolvedValue(r10)
            goto L_0x02e4
        L_0x02c3:
            r10 = r39
            int r0 = r38.getBaselineDistance()
            r1 = 6
            r2 = r36
            r4 = r37
            r10.addEquality(r2, r4, r0, r1)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r15.mBaseline
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            if (r0 == 0) goto L_0x02e6
            androidx.constraintlayout.solver.SolverVariable r0 = r10.createObjectVariable(r0)
            r3 = 0
            r10.addEquality(r2, r0, r3, r1)
            r20 = r3
            goto L_0x02e8
        L_0x02e2:
            r10 = r39
        L_0x02e4:
            r4 = r37
        L_0x02e6:
            r20 = r31
        L_0x02e8:
            androidx.constraintlayout.solver.widgets.ConstraintWidget r0 = r15.mParent
            if (r0 == 0) goto L_0x02f5
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r0.mBottom
            androidx.constraintlayout.solver.SolverVariable r0 = r10.createObjectVariable(r0)
            r23 = r0
            goto L_0x02f7
        L_0x02f5:
            r23 = r32
        L_0x02f7:
            androidx.constraintlayout.solver.widgets.ConstraintWidget r0 = r15.mParent
            if (r0 == 0) goto L_0x0303
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r0.mTop
            androidx.constraintlayout.solver.SolverVariable r0 = r10.createObjectVariable(r0)
            r3 = r0
            goto L_0x0305
        L_0x0303:
            r3 = r32
        L_0x0305:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r0 = r15.mListDimensionBehaviors
            r5 = r0[r14]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r7 = r15.mTop
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r15.mBottom
            int r9 = r15.f7mY
            int r11 = r15.mMinHeight
            int[] r0 = r15.mMaxDimension
            r12 = r0[r14]
            float r13 = r15.mVerticalBiasPercent
            int r0 = r15.mMatchConstraintMinHeight
            r17 = r0
            int r0 = r15.mMatchConstraintMaxHeight
            r18 = r0
            float r0 = r15.mMatchConstraintPercentHeight
            r19 = r0
            r0 = r38
            r1 = r39
            r2 = r34
            r25 = r4
            r4 = r23
            r10 = r26
            r14 = r16
            r15 = r22
            r16 = r29
            r0.applyConstraints(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            if (r27 == 0) goto L_0x0363
            r6 = 6
            r7 = r38
            int r0 = r7.mResolvedDimensionRatioSide
            r1 = 1
            if (r0 != r1) goto L_0x0352
            float r5 = r7.mResolvedDimensionRatio
            r0 = r39
            r1 = r24
            r2 = r25
            r3 = r30
            r4 = r21
            r0.addRatio(r1, r2, r3, r4, r5, r6)
            goto L_0x0365
        L_0x0352:
            float r5 = r7.mResolvedDimensionRatio
            r6 = 6
            r0 = r39
            r1 = r30
            r2 = r21
            r3 = r24
            r4 = r25
            r0.addRatio(r1, r2, r3, r4, r5, r6)
            goto L_0x0365
        L_0x0363:
            r7 = r38
        L_0x0365:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r7.mCenter
            boolean r0 = r0.isConnected()
            if (r0 == 0) goto L_0x038d
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r7.mCenter
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r0.getTarget()
            androidx.constraintlayout.solver.widgets.ConstraintWidget r0 = r0.getOwner()
            float r1 = r7.mCircleConstraintAngle
            r2 = 1119092736(0x42b40000, float:90.0)
            float r1 = r1 + r2
            double r1 = (double) r1
            double r1 = java.lang.Math.toRadians(r1)
            float r1 = (float) r1
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r2 = r7.mCenter
            int r2 = r2.getMargin()
            r3 = r39
            r3.addCenterPoint(r7, r0, r1, r2)
        L_0x038d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.solver.widgets.ConstraintWidget.addToSolver(androidx.constraintlayout.solver.LinearSystem):void");
    }

    public void setupDimensionRatio(boolean z, boolean z2, boolean z3, boolean z4) {
        if (this.mResolvedDimensionRatioSide == -1) {
            if (z3 && !z4) {
                this.mResolvedDimensionRatioSide = 0;
            } else if (!z3 && z4) {
                this.mResolvedDimensionRatioSide = 1;
                if (this.mDimensionRatioSide == -1) {
                    this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                }
            }
        }
        if (this.mResolvedDimensionRatioSide == 0 && (!this.mTop.isConnected() || !this.mBottom.isConnected())) {
            this.mResolvedDimensionRatioSide = 1;
        } else if (this.mResolvedDimensionRatioSide == 1 && (!this.mLeft.isConnected() || !this.mRight.isConnected())) {
            this.mResolvedDimensionRatioSide = 0;
        }
        if (this.mResolvedDimensionRatioSide == -1 && (!this.mTop.isConnected() || !this.mBottom.isConnected() || !this.mLeft.isConnected() || !this.mRight.isConnected())) {
            if (this.mTop.isConnected() && this.mBottom.isConnected()) {
                this.mResolvedDimensionRatioSide = 0;
            } else if (this.mLeft.isConnected() && this.mRight.isConnected()) {
                this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                this.mResolvedDimensionRatioSide = 1;
            }
        }
        if (this.mResolvedDimensionRatioSide == -1) {
            if (z && !z2) {
                this.mResolvedDimensionRatioSide = 0;
            } else if (!z && z2) {
                this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                this.mResolvedDimensionRatioSide = 1;
            }
        }
        if (this.mResolvedDimensionRatioSide == -1) {
            if (this.mMatchConstraintMinWidth > 0 && this.mMatchConstraintMinHeight == 0) {
                this.mResolvedDimensionRatioSide = 0;
            } else if (this.mMatchConstraintMinWidth == 0 && this.mMatchConstraintMinHeight > 0) {
                this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                this.mResolvedDimensionRatioSide = 1;
            }
        }
        if (this.mResolvedDimensionRatioSide == -1 && z && z2) {
            this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
            this.mResolvedDimensionRatioSide = 1;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x01e6  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x01f1  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x02fd  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0304  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00dd  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0105  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyConstraints(androidx.constraintlayout.solver.LinearSystem r24, boolean r25, androidx.constraintlayout.solver.SolverVariable r26, androidx.constraintlayout.solver.SolverVariable r27, androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour r28, boolean r29, androidx.constraintlayout.solver.widgets.ConstraintAnchor r30, androidx.constraintlayout.solver.widgets.ConstraintAnchor r31, int r32, int r33, int r34, int r35, float r36, boolean r37, boolean r38, int r39, int r40, int r41, float r42, boolean r43) {
        /*
            r23 = this;
            r0 = r23
            r9 = r24
            r10 = r26
            r11 = r27
            r1 = r34
            r2 = r35
            r12 = r30
            androidx.constraintlayout.solver.SolverVariable r13 = r9.createObjectVariable(r12)
            r14 = r31
            androidx.constraintlayout.solver.SolverVariable r15 = r9.createObjectVariable(r14)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r3 = r30.getTarget()
            androidx.constraintlayout.solver.SolverVariable r8 = r9.createObjectVariable(r3)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r3 = r31.getTarget()
            androidx.constraintlayout.solver.SolverVariable r7 = r9.createObjectVariable(r3)
            boolean r3 = r9.graphOptimizer
            r6 = 1
            r4 = 6
            r5 = 0
            if (r3 == 0) goto L_0x0066
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r3 = r30.getResolutionNode()
            int r3 = r3.state
            if (r3 != r6) goto L_0x0066
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r3 = r31.getResolutionNode()
            int r3 = r3.state
            if (r3 != r6) goto L_0x0066
            androidx.constraintlayout.solver.Metrics r0 = androidx.constraintlayout.solver.LinearSystem.getMetrics()
            if (r0 == 0) goto L_0x0050
            androidx.constraintlayout.solver.Metrics r0 = androidx.constraintlayout.solver.LinearSystem.getMetrics()
            long r1 = r0.resolvedWidgets
            r6 = 1
            long r1 = r1 + r6
            r0.resolvedWidgets = r1
        L_0x0050:
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r0 = r30.getResolutionNode()
            r0.addResolvedValue(r9)
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r0 = r31.getResolutionNode()
            r0.addResolvedValue(r9)
            if (r38 != 0) goto L_0x0065
            if (r25 == 0) goto L_0x0065
            r9.addGreaterThan(r11, r15, r5, r4)
        L_0x0065:
            return
        L_0x0066:
            androidx.constraintlayout.solver.Metrics r3 = androidx.constraintlayout.solver.LinearSystem.getMetrics()
            if (r3 == 0) goto L_0x0078
            androidx.constraintlayout.solver.Metrics r3 = androidx.constraintlayout.solver.LinearSystem.getMetrics()
            long r4 = r3.nonresolvedWidgets
            r16 = 1
            long r4 = r4 + r16
            r3.nonresolvedWidgets = r4
        L_0x0078:
            boolean r16 = r30.isConnected()
            boolean r17 = r31.isConnected()
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r3 = r0.mCenter
            boolean r19 = r3.isConnected()
            if (r16 == 0) goto L_0x008a
            r3 = r6
            goto L_0x008b
        L_0x008a:
            r3 = 0
        L_0x008b:
            if (r17 == 0) goto L_0x008f
            int r3 = r3 + 1
        L_0x008f:
            if (r19 == 0) goto L_0x0093
            int r3 = r3 + 1
        L_0x0093:
            r5 = r3
            if (r37 == 0) goto L_0x0098
            r3 = 3
            goto L_0x009a
        L_0x0098:
            r3 = r39
        L_0x009a:
            int[] r20 = androidx.constraintlayout.solver.widgets.ConstraintWidget.C00951.f9xdde91696
            int r21 = r28.ordinal()
            r4 = r20[r21]
            r12 = 2
            r14 = 4
            if (r4 == r6) goto L_0x00ad
            if (r4 == r12) goto L_0x00ad
            r12 = 3
            if (r4 == r12) goto L_0x00ad
            if (r4 == r14) goto L_0x00af
        L_0x00ad:
            r4 = 0
            goto L_0x00b3
        L_0x00af:
            if (r3 != r14) goto L_0x00b2
            goto L_0x00ad
        L_0x00b2:
            r4 = r6
        L_0x00b3:
            int r12 = r0.mVisibility
            r14 = 8
            if (r12 != r14) goto L_0x00bc
            r4 = 0
            r12 = 0
            goto L_0x00bf
        L_0x00bc:
            r12 = r4
            r4 = r33
        L_0x00bf:
            if (r43 == 0) goto L_0x00da
            if (r16 != 0) goto L_0x00cd
            if (r17 != 0) goto L_0x00cd
            if (r19 != 0) goto L_0x00cd
            r14 = r32
            r9.addEquality(r13, r14)
            goto L_0x00da
        L_0x00cd:
            if (r16 == 0) goto L_0x00da
            if (r17 != 0) goto L_0x00da
            int r14 = r30.getMargin()
            r6 = 6
            r9.addEquality(r13, r8, r14, r6)
            goto L_0x00db
        L_0x00da:
            r6 = 6
        L_0x00db:
            if (r12 != 0) goto L_0x0105
            if (r29 == 0) goto L_0x00f3
            r6 = 0
            r14 = 3
            r9.addEquality(r15, r13, r6, r14)
            r4 = 6
            if (r1 <= 0) goto L_0x00ea
            r9.addGreaterThan(r15, r13, r1, r4)
        L_0x00ea:
            r6 = 2147483647(0x7fffffff, float:NaN)
            if (r2 >= r6) goto L_0x00f8
            r9.addLowerThan(r15, r13, r2, r4)
            goto L_0x00f8
        L_0x00f3:
            r2 = r6
            r14 = 3
            r9.addEquality(r15, r13, r4, r2)
        L_0x00f8:
            r14 = r41
            r32 = r3
            r0 = r5
            r1 = r7
            r22 = r8
            r5 = r12
            r12 = r40
            goto L_0x01ef
        L_0x0105:
            r14 = 3
            r2 = -2
            r6 = r40
            r14 = r41
            if (r6 != r2) goto L_0x010e
            r6 = r4
        L_0x010e:
            if (r14 != r2) goto L_0x0111
            r14 = r4
        L_0x0111:
            if (r6 <= 0) goto L_0x0123
            if (r25 == 0) goto L_0x011a
            r2 = 6
            r9.addGreaterThan(r15, r13, r6, r2)
            goto L_0x011e
        L_0x011a:
            r2 = 6
            r9.addGreaterThan(r15, r13, r6, r2)
        L_0x011e:
            int r4 = java.lang.Math.max(r4, r6)
            goto L_0x0124
        L_0x0123:
            r2 = 6
        L_0x0124:
            if (r14 <= 0) goto L_0x0135
            if (r25 == 0) goto L_0x012e
            r2 = 1
            r9.addLowerThan(r15, r13, r14, r2)
            r2 = 6
            goto L_0x0131
        L_0x012e:
            r9.addLowerThan(r15, r13, r14, r2)
        L_0x0131:
            int r4 = java.lang.Math.min(r4, r14)
        L_0x0135:
            r2 = 1
            if (r3 != r2) goto L_0x015e
            if (r25 == 0) goto L_0x014a
            r2 = 6
            r9.addEquality(r15, r13, r4, r2)
            r32 = r3
            r0 = r5
            r1 = r7
            r22 = r8
            r33 = r12
            r8 = r4
            r12 = r6
            goto L_0x01d7
        L_0x014a:
            r2 = 6
            if (r38 == 0) goto L_0x0155
            r33 = r12
            r12 = 4
            r9.addEquality(r15, r13, r4, r12)
            goto L_0x01cf
        L_0x0155:
            r33 = r12
            r2 = 1
            r12 = 4
            r9.addEquality(r15, r13, r4, r2)
            goto L_0x01cf
        L_0x015e:
            r33 = r12
            r2 = 2
            r12 = 4
            if (r3 != r2) goto L_0x01cf
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r2 = r30.getType()
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r12 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.TOP
            if (r2 == r12) goto L_0x0190
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r2 = r30.getType()
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r12 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BOTTOM
            if (r2 != r12) goto L_0x0175
            goto L_0x0190
        L_0x0175:
            androidx.constraintlayout.solver.widgets.ConstraintWidget r2 = r0.mParent
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r12 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.LEFT
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r2 = r2.getAnchor(r12)
            androidx.constraintlayout.solver.SolverVariable r2 = r9.createObjectVariable(r2)
            androidx.constraintlayout.solver.widgets.ConstraintWidget r12 = r0.mParent
            r29 = r2
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r2 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.RIGHT
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r2 = r12.getAnchor(r2)
            androidx.constraintlayout.solver.SolverVariable r2 = r9.createObjectVariable(r2)
            goto L_0x01aa
        L_0x0190:
            androidx.constraintlayout.solver.widgets.ConstraintWidget r2 = r0.mParent
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r12 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.TOP
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r2 = r2.getAnchor(r12)
            androidx.constraintlayout.solver.SolverVariable r2 = r9.createObjectVariable(r2)
            androidx.constraintlayout.solver.widgets.ConstraintWidget r12 = r0.mParent
            r29 = r2
            androidx.constraintlayout.solver.widgets.ConstraintAnchor$Type r2 = androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type.BOTTOM
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r2 = r12.getAnchor(r2)
            androidx.constraintlayout.solver.SolverVariable r2 = r9.createObjectVariable(r2)
        L_0x01aa:
            r21 = r29
            r12 = r2
            androidx.constraintlayout.solver.ArrayRow r2 = r24.createRow()
            r29 = r2
            r18 = 1
            r20 = 6
            r0 = r3
            r3 = r15
            r22 = r8
            r8 = r4
            r4 = r13
            r32 = r0
            r0 = r5
            r5 = r12
            r12 = r6
            r6 = r21
            r1 = r7
            r7 = r42
            r2.createRowDimensionRatio(r3, r4, r5, r6, r7)
            r9.addConstraint(r2)
            r5 = 0
            goto L_0x01d9
        L_0x01cf:
            r32 = r3
            r0 = r5
            r12 = r6
            r1 = r7
            r22 = r8
            r8 = r4
        L_0x01d7:
            r5 = r33
        L_0x01d9:
            if (r5 == 0) goto L_0x01ef
            r2 = 2
            if (r0 == r2) goto L_0x01ef
            if (r37 != 0) goto L_0x01ef
            int r2 = java.lang.Math.max(r12, r8)
            if (r14 <= 0) goto L_0x01ea
            int r2 = java.lang.Math.min(r14, r2)
        L_0x01ea:
            r3 = 6
            r9.addEquality(r15, r13, r2, r3)
            r5 = 0
        L_0x01ef:
            if (r43 == 0) goto L_0x0304
            if (r38 == 0) goto L_0x01fa
            r1 = r10
            r2 = r11
            r3 = 0
            r4 = 2
            r10 = 6
            goto L_0x0309
        L_0x01fa:
            r0 = 5
            if (r16 != 0) goto L_0x020c
            if (r17 != 0) goto L_0x020c
            if (r19 != 0) goto L_0x020c
            if (r25 == 0) goto L_0x0209
            r2 = 0
            r9.addGreaterThan(r11, r15, r2, r0)
            goto L_0x02f9
        L_0x0209:
            r0 = 0
            goto L_0x02fa
        L_0x020c:
            r2 = 0
            if (r16 == 0) goto L_0x0218
            if (r17 != 0) goto L_0x0218
            if (r25 == 0) goto L_0x02f9
            r9.addGreaterThan(r11, r15, r2, r0)
            goto L_0x02f9
        L_0x0218:
            if (r16 != 0) goto L_0x022c
            if (r17 == 0) goto L_0x022c
            int r3 = r31.getMargin()
            int r3 = -r3
            r4 = 6
            r9.addEquality(r15, r1, r3, r4)
            if (r25 == 0) goto L_0x02f9
            r9.addGreaterThan(r13, r10, r2, r0)
            goto L_0x02f9
        L_0x022c:
            if (r16 == 0) goto L_0x02f9
            if (r17 == 0) goto L_0x02f9
            if (r5 == 0) goto L_0x0299
            r8 = r1
            r7 = 6
            if (r25 == 0) goto L_0x023b
            if (r34 != 0) goto L_0x023b
            r9.addGreaterThan(r15, r13, r2, r7)
        L_0x023b:
            if (r32 != 0) goto L_0x0267
            if (r14 > 0) goto L_0x0245
            if (r12 <= 0) goto L_0x0242
            goto L_0x0245
        L_0x0242:
            r6 = r2
            r4 = r7
            goto L_0x0247
        L_0x0245:
            r4 = 4
            r6 = 1
        L_0x0247:
            int r1 = r30.getMargin()
            r5 = r22
            r9.addEquality(r13, r5, r1, r4)
            int r1 = r31.getMargin()
            int r1 = -r1
            r9.addEquality(r15, r8, r1, r4)
            if (r14 > 0) goto L_0x0260
            if (r12 <= 0) goto L_0x025d
            goto L_0x0260
        L_0x025d:
            r18 = r2
            goto L_0x0262
        L_0x0260:
            r18 = 1
        L_0x0262:
            r14 = r0
            r12 = r6
            r6 = r18
            goto L_0x02b2
        L_0x0267:
            r4 = r32
            r5 = r22
            r1 = 1
            if (r4 != r1) goto L_0x0272
            r6 = r1
            r12 = r6
            r14 = r7
            goto L_0x02b2
        L_0x0272:
            r3 = 3
            if (r4 != r3) goto L_0x0295
            if (r37 != 0) goto L_0x0282
            r3 = r23
            int r3 = r3.mResolvedDimensionRatioSide
            r4 = -1
            if (r3 == r4) goto L_0x0282
            if (r14 > 0) goto L_0x0282
            r4 = r7
            goto L_0x0283
        L_0x0282:
            r4 = 4
        L_0x0283:
            int r3 = r30.getMargin()
            r9.addEquality(r13, r5, r3, r4)
            int r3 = r31.getMargin()
            int r3 = -r3
            r9.addEquality(r15, r8, r3, r4)
            r14 = r0
            r6 = r1
            goto L_0x0297
        L_0x0295:
            r14 = r0
            r6 = r2
        L_0x0297:
            r12 = r6
            goto L_0x02b2
        L_0x0299:
            r8 = r1
            r5 = r22
            r1 = 1
            r7 = 6
            if (r25 == 0) goto L_0x02af
            int r3 = r30.getMargin()
            r9.addGreaterThan(r13, r5, r3, r0)
            int r3 = r31.getMargin()
            int r3 = -r3
            r9.addLowerThan(r15, r8, r3, r0)
        L_0x02af:
            r14 = r0
            r6 = r1
            r12 = r2
        L_0x02b2:
            if (r6 == 0) goto L_0x02d7
            int r3 = r30.getMargin()
            int r16 = r31.getMargin()
            r6 = r2
            r0 = r24
            r1 = r13
            r2 = r5
            r4 = r36
            r17 = r5
            r5 = r8
            r11 = r6
            r6 = r15
            r18 = r7
            r7 = r16
            r16 = r8
            r11 = r17
            r10 = r18
            r8 = r14
            r0.addCentering(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x02db
        L_0x02d7:
            r11 = r5
            r10 = r7
            r16 = r8
        L_0x02db:
            if (r12 == 0) goto L_0x02ee
            int r0 = r30.getMargin()
            r9.addGreaterThan(r13, r11, r0, r10)
            int r0 = r31.getMargin()
            int r0 = -r0
            r1 = r16
            r9.addLowerThan(r15, r1, r0, r10)
        L_0x02ee:
            if (r25 == 0) goto L_0x02f7
            r1 = r26
            r0 = 0
            r9.addGreaterThan(r13, r1, r0, r10)
            goto L_0x02fb
        L_0x02f7:
            r0 = 0
            goto L_0x02fb
        L_0x02f9:
            r0 = r2
        L_0x02fa:
            r10 = 6
        L_0x02fb:
            if (r25 == 0) goto L_0x0303
            r2 = r27
            r3 = r0
            r9.addGreaterThan(r2, r15, r3, r10)
        L_0x0303:
            return
        L_0x0304:
            r1 = r10
            r2 = r11
            r3 = 0
            r10 = 6
            r4 = 2
        L_0x0309:
            if (r0 >= r4) goto L_0x0313
            if (r25 == 0) goto L_0x0313
            r9.addGreaterThan(r13, r1, r3, r10)
            r9.addGreaterThan(r2, r15, r3, r10)
        L_0x0313:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.solver.widgets.ConstraintWidget.applyConstraints(androidx.constraintlayout.solver.LinearSystem, boolean, androidx.constraintlayout.solver.SolverVariable, androidx.constraintlayout.solver.SolverVariable, androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour, boolean, androidx.constraintlayout.solver.widgets.ConstraintAnchor, androidx.constraintlayout.solver.widgets.ConstraintAnchor, int, int, int, int, float, boolean, boolean, int, int, int, float, boolean):void");
    }

    public void updateFromSolver(LinearSystem linearSystem) {
        int objectVariableValue = linearSystem.getObjectVariableValue(this.mLeft);
        int objectVariableValue2 = linearSystem.getObjectVariableValue(this.mTop);
        int objectVariableValue3 = linearSystem.getObjectVariableValue(this.mRight);
        int objectVariableValue4 = linearSystem.getObjectVariableValue(this.mBottom);
        int i = objectVariableValue4 - objectVariableValue2;
        if (objectVariableValue3 - objectVariableValue < 0 || i < 0 || objectVariableValue == Integer.MIN_VALUE || objectVariableValue == Integer.MAX_VALUE || objectVariableValue2 == Integer.MIN_VALUE || objectVariableValue2 == Integer.MAX_VALUE || objectVariableValue3 == Integer.MIN_VALUE || objectVariableValue3 == Integer.MAX_VALUE || objectVariableValue4 == Integer.MIN_VALUE || objectVariableValue4 == Integer.MAX_VALUE) {
            objectVariableValue4 = 0;
            objectVariableValue = 0;
            objectVariableValue2 = 0;
            objectVariableValue3 = 0;
        }
        setFrame(objectVariableValue, objectVariableValue2, objectVariableValue3, objectVariableValue4);
    }
}
