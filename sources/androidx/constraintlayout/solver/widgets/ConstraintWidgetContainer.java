package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type;
import androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour;
import java.util.Arrays;

public class ConstraintWidgetContainer extends WidgetContainer {
    private boolean mHeightMeasuredTooSmall = false;
    ChainHead[] mHorizontalChainsArray = new ChainHead[4];
    int mHorizontalChainsSize = 0;
    private boolean mIsRtl = false;
    private int mOptimizationLevel = 3;
    int mPaddingBottom;
    int mPaddingLeft;
    int mPaddingRight;
    int mPaddingTop;
    private Snapshot mSnapshot;
    protected LinearSystem mSystem = new LinearSystem();
    ChainHead[] mVerticalChainsArray = new ChainHead[4];
    int mVerticalChainsSize = 0;
    private boolean mWidthMeasuredTooSmall = false;

    public boolean handlesInternalConstraints() {
        return false;
    }

    public void setOptimizationLevel(int i) {
        this.mOptimizationLevel = i;
    }

    public boolean optimizeFor(int i) {
        return (this.mOptimizationLevel & i) == i;
    }

    public void reset() {
        this.mSystem.reset();
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mPaddingTop = 0;
        this.mPaddingBottom = 0;
        super.reset();
    }

    public boolean isWidthMeasuredTooSmall() {
        return this.mWidthMeasuredTooSmall;
    }

    public boolean isHeightMeasuredTooSmall() {
        return this.mHeightMeasuredTooSmall;
    }

    public boolean addChildrenToSolver(LinearSystem linearSystem) {
        addToSolver(linearSystem);
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = (ConstraintWidget) this.mChildren.get(i);
            if (constraintWidget instanceof ConstraintWidgetContainer) {
                DimensionBehaviour[] dimensionBehaviourArr = constraintWidget.mListDimensionBehaviors;
                DimensionBehaviour dimensionBehaviour = dimensionBehaviourArr[0];
                DimensionBehaviour dimensionBehaviour2 = dimensionBehaviourArr[1];
                if (dimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setHorizontalDimensionBehaviour(DimensionBehaviour.FIXED);
                }
                if (dimensionBehaviour2 == DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setVerticalDimensionBehaviour(DimensionBehaviour.FIXED);
                }
                constraintWidget.addToSolver(linearSystem);
                if (dimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setHorizontalDimensionBehaviour(dimensionBehaviour);
                }
                if (dimensionBehaviour2 == DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setVerticalDimensionBehaviour(dimensionBehaviour2);
                }
            } else {
                Optimizer.checkMatchParent(this, linearSystem, constraintWidget);
                constraintWidget.addToSolver(linearSystem);
            }
        }
        if (this.mHorizontalChainsSize > 0) {
            Chain.applyChainConstraints(this, linearSystem, 0);
        }
        if (this.mVerticalChainsSize > 0) {
            Chain.applyChainConstraints(this, linearSystem, 1);
        }
        return true;
    }

    public void updateChildrenFromSolver(LinearSystem linearSystem, boolean[] zArr) {
        zArr[2] = false;
        updateFromSolver(linearSystem);
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = (ConstraintWidget) this.mChildren.get(i);
            constraintWidget.updateFromSolver(linearSystem);
            if (constraintWidget.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getWidth() < constraintWidget.getWrapWidth()) {
                zArr[2] = true;
            }
            if (constraintWidget.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getHeight() < constraintWidget.getWrapHeight()) {
                zArr[2] = true;
            }
        }
    }

    public void setRtl(boolean z) {
        this.mIsRtl = z;
    }

    public boolean isRtl() {
        return this.mIsRtl;
    }

    public void analyze(int i) {
        super.analyze(i);
        int size = this.mChildren.size();
        for (int i2 = 0; i2 < size; i2++) {
            ((ConstraintWidget) this.mChildren.get(i2)).analyze(i);
        }
    }

    /* JADX WARNING: type inference failed for: r13v0 */
    /* JADX WARNING: type inference failed for: r12v0 */
    /* JADX WARNING: type inference failed for: r13v1 */
    /* JADX WARNING: type inference failed for: r12v1 */
    /* JADX WARNING: type inference failed for: r12v2 */
    /* JADX WARNING: type inference failed for: r13v2 */
    /* JADX WARNING: type inference failed for: r0v22 */
    /* JADX WARNING: type inference failed for: r13v3 */
    /* JADX WARNING: type inference failed for: r0v23 */
    /* JADX WARNING: type inference failed for: r12v8 */
    /* JADX WARNING: type inference failed for: r8v9, types: [boolean] */
    /* JADX WARNING: type inference failed for: r0v24 */
    /* JADX WARNING: type inference failed for: r13v4 */
    /* JADX WARNING: type inference failed for: r12v9 */
    /* JADX WARNING: type inference failed for: r12v10 */
    /* JADX WARNING: type inference failed for: r0v26 */
    /* JADX WARNING: type inference failed for: r13v5 */
    /* JADX WARNING: type inference failed for: r12v11 */
    /* JADX WARNING: type inference failed for: r12v12 */
    /* JADX WARNING: type inference failed for: r0v27 */
    /* JADX WARNING: type inference failed for: r12v13 */
    /* JADX WARNING: type inference failed for: r13v8 */
    /* JADX WARNING: type inference failed for: r0v30 */
    /* JADX WARNING: type inference failed for: r12v14 */
    /* JADX WARNING: type inference failed for: r8v11 */
    /* JADX WARNING: type inference failed for: r12v15 */
    /* JADX WARNING: type inference failed for: r8v12 */
    /* JADX WARNING: type inference failed for: r0v32 */
    /* JADX WARNING: type inference failed for: r12v16 */
    /* JADX WARNING: type inference failed for: r0v34 */
    /* JADX WARNING: type inference failed for: r13v9 */
    /* JADX WARNING: type inference failed for: r0v35 */
    /* JADX WARNING: type inference failed for: r13v10 */
    /* JADX WARNING: type inference failed for: r0v41 */
    /* JADX WARNING: type inference failed for: r0v43 */
    /* JADX WARNING: type inference failed for: r13v11 */
    /* JADX WARNING: type inference failed for: r0v44 */
    /* JADX WARNING: type inference failed for: r0v46 */
    /* JADX WARNING: type inference failed for: r13v12 */
    /* JADX WARNING: type inference failed for: r12v22, types: [boolean] */
    /* JADX WARNING: type inference failed for: r13v14 */
    /* JADX WARNING: type inference failed for: r13v15 */
    /* JADX WARNING: type inference failed for: r12v27 */
    /* JADX WARNING: type inference failed for: r0v68 */
    /* JADX WARNING: type inference failed for: r13v16 */
    /* JADX WARNING: type inference failed for: r12v28 */
    /* JADX WARNING: type inference failed for: r12v29 */
    /* JADX WARNING: type inference failed for: r13v17 */
    /* JADX WARNING: type inference failed for: r0v69 */
    /* JADX WARNING: type inference failed for: r8v25 */
    /* JADX WARNING: type inference failed for: r8v26 */
    /* JADX WARNING: type inference failed for: r0v70 */
    /* JADX WARNING: type inference failed for: r13v18 */
    /* JADX WARNING: type inference failed for: r0v71 */
    /* JADX WARNING: type inference failed for: r0v72 */
    /* JADX WARNING: type inference failed for: r13v19 */
    /* JADX WARNING: type inference failed for: r0v73 */
    /* JADX WARNING: type inference failed for: r0v74 */
    /* JADX WARNING: type inference failed for: r13v20 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r0v22
      assigns: []
      uses: []
      mth insns count: 274
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 19 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void layout() {
        /*
            r17 = this;
            r1 = r17
            int r2 = r1.f6mX
            int r3 = r1.f7mY
            int r0 = r17.getWidth()
            r4 = 0
            int r5 = java.lang.Math.max(r4, r0)
            int r0 = r17.getHeight()
            int r6 = java.lang.Math.max(r4, r0)
            r1.mWidthMeasuredTooSmall = r4
            r1.mHeightMeasuredTooSmall = r4
            androidx.constraintlayout.solver.widgets.ConstraintWidget r0 = r1.mParent
            if (r0 == 0) goto L_0x0046
            androidx.constraintlayout.solver.widgets.Snapshot r0 = r1.mSnapshot
            if (r0 != 0) goto L_0x002a
            androidx.constraintlayout.solver.widgets.Snapshot r0 = new androidx.constraintlayout.solver.widgets.Snapshot
            r0.<init>(r1)
            r1.mSnapshot = r0
        L_0x002a:
            androidx.constraintlayout.solver.widgets.Snapshot r0 = r1.mSnapshot
            r0.updateFrom(r1)
            int r0 = r1.mPaddingLeft
            r1.setX(r0)
            int r0 = r1.mPaddingTop
            r1.setY(r0)
            r17.resetAnchors()
            androidx.constraintlayout.solver.LinearSystem r0 = r1.mSystem
            androidx.constraintlayout.solver.Cache r0 = r0.getCache()
            r1.resetSolverVariables(r0)
            goto L_0x004a
        L_0x0046:
            r1.f6mX = r4
            r1.f7mY = r4
        L_0x004a:
            int r0 = r1.mOptimizationLevel
            r7 = 8
            r8 = 1
            if (r0 == 0) goto L_0x0062
            boolean r0 = r1.optimizeFor(r7)
            if (r0 != 0) goto L_0x005a
            r17.optimizeReset()
        L_0x005a:
            r17.optimize()
            androidx.constraintlayout.solver.LinearSystem r0 = r1.mSystem
            r0.graphOptimizer = r8
            goto L_0x0066
        L_0x0062:
            androidx.constraintlayout.solver.LinearSystem r0 = r1.mSystem
            r0.graphOptimizer = r4
        L_0x0066:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r0 = r1.mListDimensionBehaviors
            r9 = r0[r8]
            r10 = r0[r4]
            r17.resetChains()
            java.util.ArrayList<androidx.constraintlayout.solver.widgets.ConstraintWidget> r0 = r1.mChildren
            int r11 = r0.size()
            r0 = r4
        L_0x0076:
            if (r0 >= r11) goto L_0x008c
            java.util.ArrayList<androidx.constraintlayout.solver.widgets.ConstraintWidget> r12 = r1.mChildren
            java.lang.Object r12 = r12.get(r0)
            androidx.constraintlayout.solver.widgets.ConstraintWidget r12 = (androidx.constraintlayout.solver.widgets.ConstraintWidget) r12
            boolean r13 = r12 instanceof androidx.constraintlayout.solver.widgets.WidgetContainer
            if (r13 == 0) goto L_0x0089
            androidx.constraintlayout.solver.widgets.WidgetContainer r12 = (androidx.constraintlayout.solver.widgets.WidgetContainer) r12
            r12.layout()
        L_0x0089:
            int r0 = r0 + 1
            goto L_0x0076
        L_0x008c:
            r0 = r4
            r13 = r0
            r12 = r8
        L_0x008f:
            if (r12 == 0) goto L_0x0217
            int r14 = r0 + 1
            androidx.constraintlayout.solver.LinearSystem r0 = r1.mSystem     // Catch:{ Exception -> 0x00c0 }
            r0.reset()     // Catch:{ Exception -> 0x00c0 }
            androidx.constraintlayout.solver.LinearSystem r0 = r1.mSystem     // Catch:{ Exception -> 0x00c0 }
            r1.createObjectVariables(r0)     // Catch:{ Exception -> 0x00c0 }
            r0 = r4
        L_0x009e:
            if (r0 >= r11) goto L_0x00b2
            java.util.ArrayList<androidx.constraintlayout.solver.widgets.ConstraintWidget> r15 = r1.mChildren     // Catch:{ Exception -> 0x00c0 }
            java.lang.Object r15 = r15.get(r0)     // Catch:{ Exception -> 0x00c0 }
            androidx.constraintlayout.solver.widgets.ConstraintWidget r15 = (androidx.constraintlayout.solver.widgets.ConstraintWidget) r15     // Catch:{ Exception -> 0x00c0 }
            androidx.constraintlayout.solver.LinearSystem r7 = r1.mSystem     // Catch:{ Exception -> 0x00c0 }
            r15.createObjectVariables(r7)     // Catch:{ Exception -> 0x00c0 }
            int r0 = r0 + 1
            r7 = 8
            goto L_0x009e
        L_0x00b2:
            androidx.constraintlayout.solver.LinearSystem r0 = r1.mSystem     // Catch:{ Exception -> 0x00c0 }
            boolean r12 = r1.addChildrenToSolver(r0)     // Catch:{ Exception -> 0x00c0 }
            if (r12 == 0) goto L_0x00da
            androidx.constraintlayout.solver.LinearSystem r0 = r1.mSystem     // Catch:{ Exception -> 0x00c0 }
            r0.minimize()     // Catch:{ Exception -> 0x00c0 }
            goto L_0x00da
        L_0x00c0:
            r0 = move-exception
            r0.printStackTrace()
            java.io.PrintStream r7 = java.lang.System.out
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r8 = "EXCEPTION : "
            r15.append(r8)
            r15.append(r0)
            java.lang.String r0 = r15.toString()
            r7.println(r0)
        L_0x00da:
            r0 = 2
            if (r12 == 0) goto L_0x00e7
            androidx.constraintlayout.solver.LinearSystem r7 = r1.mSystem
            boolean[] r8 = androidx.constraintlayout.solver.widgets.Optimizer.flags
            r1.updateChildrenFromSolver(r7, r8)
        L_0x00e4:
            r4 = 8
            goto L_0x012b
        L_0x00e7:
            androidx.constraintlayout.solver.LinearSystem r7 = r1.mSystem
            r1.updateFromSolver(r7)
            r7 = r4
        L_0x00ed:
            if (r7 >= r11) goto L_0x00e4
            java.util.ArrayList<androidx.constraintlayout.solver.widgets.ConstraintWidget> r8 = r1.mChildren
            java.lang.Object r8 = r8.get(r7)
            androidx.constraintlayout.solver.widgets.ConstraintWidget r8 = (androidx.constraintlayout.solver.widgets.ConstraintWidget) r8
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r12 = r8.mListDimensionBehaviors
            r12 = r12[r4]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r15 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r12 != r15) goto L_0x010f
            int r12 = r8.getWidth()
            int r15 = r8.getWrapWidth()
            if (r12 >= r15) goto L_0x010f
            boolean[] r7 = androidx.constraintlayout.solver.widgets.Optimizer.flags
            r12 = 1
            r7[r0] = r12
            goto L_0x00e4
        L_0x010f:
            r12 = 1
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r15 = r8.mListDimensionBehaviors
            r15 = r15[r12]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r4 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r15 != r4) goto L_0x0127
            int r4 = r8.getHeight()
            int r8 = r8.getWrapHeight()
            if (r4 >= r8) goto L_0x0127
            boolean[] r4 = androidx.constraintlayout.solver.widgets.Optimizer.flags
            r4[r0] = r12
            goto L_0x00e4
        L_0x0127:
            int r7 = r7 + 1
            r4 = 0
            goto L_0x00ed
        L_0x012b:
            if (r14 >= r4) goto L_0x0195
            boolean[] r7 = androidx.constraintlayout.solver.widgets.Optimizer.flags
            boolean r0 = r7[r0]
            if (r0 == 0) goto L_0x0195
            r0 = 0
            r7 = 0
            r8 = 0
        L_0x0136:
            if (r0 >= r11) goto L_0x015a
            java.util.ArrayList<androidx.constraintlayout.solver.widgets.ConstraintWidget> r12 = r1.mChildren
            java.lang.Object r12 = r12.get(r0)
            androidx.constraintlayout.solver.widgets.ConstraintWidget r12 = (androidx.constraintlayout.solver.widgets.ConstraintWidget) r12
            int r15 = r12.f6mX
            int r16 = r12.getWidth()
            int r15 = r15 + r16
            int r7 = java.lang.Math.max(r7, r15)
            int r15 = r12.f7mY
            int r12 = r12.getHeight()
            int r15 = r15 + r12
            int r8 = java.lang.Math.max(r8, r15)
            int r0 = r0 + 1
            goto L_0x0136
        L_0x015a:
            int r0 = r1.mMinWidth
            int r0 = java.lang.Math.max(r0, r7)
            int r7 = r1.mMinHeight
            int r7 = java.lang.Math.max(r7, r8)
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r8 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r10 != r8) goto L_0x017d
            int r8 = r17.getWidth()
            if (r8 >= r0) goto L_0x017d
            r1.setWidth(r0)
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r0 = r1.mListDimensionBehaviors
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r8 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            r12 = 0
            r0[r12] = r8
            r0 = 1
            r13 = 1
            goto L_0x017e
        L_0x017d:
            r0 = 0
        L_0x017e:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r8 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r9 != r8) goto L_0x0196
            int r8 = r17.getHeight()
            if (r8 >= r7) goto L_0x0196
            r1.setHeight(r7)
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r0 = r1.mListDimensionBehaviors
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r7 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            r8 = 1
            r0[r8] = r7
            r0 = 1
            r13 = 1
            goto L_0x0196
        L_0x0195:
            r0 = 0
        L_0x0196:
            int r7 = r1.mMinWidth
            int r8 = r17.getWidth()
            int r7 = java.lang.Math.max(r7, r8)
            int r8 = r17.getWidth()
            if (r7 <= r8) goto L_0x01b2
            r1.setWidth(r7)
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r0 = r1.mListDimensionBehaviors
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r7 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.FIXED
            r8 = 0
            r0[r8] = r7
            r0 = 1
            r13 = 1
        L_0x01b2:
            int r7 = r1.mMinHeight
            int r8 = r17.getHeight()
            int r7 = java.lang.Math.max(r7, r8)
            int r8 = r17.getHeight()
            if (r7 <= r8) goto L_0x01cf
            r1.setHeight(r7)
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r0 = r1.mListDimensionBehaviors
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r7 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.FIXED
            r8 = 1
            r0[r8] = r7
            r0 = r8
            r12 = r0
            goto L_0x01d1
        L_0x01cf:
            r8 = 1
            r12 = r13
        L_0x01d1:
            if (r12 != 0) goto L_0x020f
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r7 = r1.mListDimensionBehaviors
            r13 = 0
            r7 = r7[r13]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r15 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r7 != r15) goto L_0x01f1
            if (r5 <= 0) goto L_0x01f1
            int r7 = r17.getWidth()
            if (r7 <= r5) goto L_0x01f1
            r1.mWidthMeasuredTooSmall = r8
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r0 = r1.mListDimensionBehaviors
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r7 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.FIXED
            r0[r13] = r7
            r1.setWidth(r5)
            r0 = r8
            r12 = r0
        L_0x01f1:
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r7 = r1.mListDimensionBehaviors
            r7 = r7[r8]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r13 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r7 != r13) goto L_0x020f
            if (r6 <= 0) goto L_0x020f
            int r7 = r17.getHeight()
            if (r7 <= r6) goto L_0x020f
            r1.mHeightMeasuredTooSmall = r8
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r0 = r1.mListDimensionBehaviors
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r7 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.FIXED
            r0[r8] = r7
            r1.setHeight(r6)
            r12 = 1
            r13 = 1
            goto L_0x0211
        L_0x020f:
            r13 = r12
            r12 = r0
        L_0x0211:
            r7 = r4
            r0 = r14
            r4 = 0
            r8 = 1
            goto L_0x008f
        L_0x0217:
            androidx.constraintlayout.solver.widgets.ConstraintWidget r0 = r1.mParent
            if (r0 == 0) goto L_0x0247
            int r0 = r1.mMinWidth
            int r2 = r17.getWidth()
            int r0 = java.lang.Math.max(r0, r2)
            int r2 = r1.mMinHeight
            int r3 = r17.getHeight()
            int r2 = java.lang.Math.max(r2, r3)
            androidx.constraintlayout.solver.widgets.Snapshot r3 = r1.mSnapshot
            r3.applyTo(r1)
            int r3 = r1.mPaddingLeft
            int r0 = r0 + r3
            int r3 = r1.mPaddingRight
            int r0 = r0 + r3
            r1.setWidth(r0)
            int r0 = r1.mPaddingTop
            int r2 = r2 + r0
            int r0 = r1.mPaddingBottom
            int r2 = r2 + r0
            r1.setHeight(r2)
            goto L_0x024b
        L_0x0247:
            r1.f6mX = r2
            r1.f7mY = r3
        L_0x024b:
            if (r13 == 0) goto L_0x0255
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r0 = r1.mListDimensionBehaviors
            r2 = 0
            r0[r2] = r10
            r2 = 1
            r0[r2] = r9
        L_0x0255:
            androidx.constraintlayout.solver.LinearSystem r0 = r1.mSystem
            androidx.constraintlayout.solver.Cache r0 = r0.getCache()
            r1.resetSolverVariables(r0)
            androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r0 = r17.getRootConstraintContainer()
            if (r1 != r0) goto L_0x0267
            r17.updateDrawPosition()
        L_0x0267:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer.layout():void");
    }

    public void preOptimize() {
        optimizeReset();
        analyze(this.mOptimizationLevel);
    }

    public void solveGraph() {
        ResolutionAnchor resolutionNode = getAnchor(Type.LEFT).getResolutionNode();
        ResolutionAnchor resolutionNode2 = getAnchor(Type.TOP).getResolutionNode();
        resolutionNode.resolve(null, 0.0f);
        resolutionNode2.resolve(null, 0.0f);
    }

    public void optimizeForDimensions(int i, int i2) {
        if (this.mListDimensionBehaviors[0] != DimensionBehaviour.WRAP_CONTENT) {
            ResolutionDimension resolutionDimension = this.mResolutionWidth;
            if (resolutionDimension != null) {
                resolutionDimension.resolve(i);
            }
        }
        if (this.mListDimensionBehaviors[1] != DimensionBehaviour.WRAP_CONTENT) {
            ResolutionDimension resolutionDimension2 = this.mResolutionHeight;
            if (resolutionDimension2 != null) {
                resolutionDimension2.resolve(i2);
            }
        }
    }

    public void optimizeReset() {
        int size = this.mChildren.size();
        resetResolutionNodes();
        for (int i = 0; i < size; i++) {
            ((ConstraintWidget) this.mChildren.get(i)).resetResolutionNodes();
        }
    }

    public void optimize() {
        if (!optimizeFor(8)) {
            analyze(this.mOptimizationLevel);
        }
        solveGraph();
    }

    private void resetChains() {
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
    }

    /* access modifiers changed from: 0000 */
    public void addChain(ConstraintWidget constraintWidget, int i) {
        if (i == 0) {
            addHorizontalChain(constraintWidget);
        } else if (i == 1) {
            addVerticalChain(constraintWidget);
        }
    }

    private void addHorizontalChain(ConstraintWidget constraintWidget) {
        int i = this.mHorizontalChainsSize + 1;
        ChainHead[] chainHeadArr = this.mHorizontalChainsArray;
        if (i >= chainHeadArr.length) {
            this.mHorizontalChainsArray = (ChainHead[]) Arrays.copyOf(chainHeadArr, chainHeadArr.length * 2);
        }
        this.mHorizontalChainsArray[this.mHorizontalChainsSize] = new ChainHead(constraintWidget, 0, isRtl());
        this.mHorizontalChainsSize++;
    }

    private void addVerticalChain(ConstraintWidget constraintWidget) {
        int i = this.mVerticalChainsSize + 1;
        ChainHead[] chainHeadArr = this.mVerticalChainsArray;
        if (i >= chainHeadArr.length) {
            this.mVerticalChainsArray = (ChainHead[]) Arrays.copyOf(chainHeadArr, chainHeadArr.length * 2);
        }
        this.mVerticalChainsArray[this.mVerticalChainsSize] = new ChainHead(constraintWidget, 1, isRtl());
        this.mVerticalChainsSize++;
    }
}
