package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;

class Chain {
    static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i) {
        ChainHead[] chainHeadArr;
        int i2;
        int i3;
        if (i == 0) {
            int i4 = constraintWidgetContainer.mHorizontalChainsSize;
            chainHeadArr = constraintWidgetContainer.mHorizontalChainsArray;
            i2 = i4;
            i3 = 0;
        } else {
            i3 = 2;
            i2 = constraintWidgetContainer.mVerticalChainsSize;
            chainHeadArr = constraintWidgetContainer.mVerticalChainsArray;
        }
        for (int i5 = 0; i5 < i2; i5++) {
            ChainHead chainHead = chainHeadArr[i5];
            chainHead.define();
            if (!constraintWidgetContainer.optimizeFor(4)) {
                applyChainConstraints(constraintWidgetContainer, linearSystem, i, i3, chainHead);
            } else if (!Optimizer.applyChainOptimized(constraintWidgetContainer, linearSystem, i, i3, chainHead)) {
                applyChainConstraints(constraintWidgetContainer, linearSystem, i, i3, chainHead);
            }
        }
    }

    /* JADX WARNING: type inference failed for: r4v6 */
    /* JADX WARNING: type inference failed for: r20v1 */
    /* JADX WARNING: type inference failed for: r4v11, types: [androidx.constraintlayout.solver.SolverVariable] */
    /* JADX WARNING: type inference failed for: r20v2 */
    /* JADX WARNING: type inference failed for: r4v12 */
    /* JADX WARNING: type inference failed for: r4v13, types: [androidx.constraintlayout.solver.SolverVariable] */
    /* JADX WARNING: type inference failed for: r4v44 */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0035, code lost:
        if (r2.mHorizontalChainStyle == 2) goto L_0x004a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0048, code lost:
        if (r2.mVerticalChainStyle == 2) goto L_0x004a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x004c, code lost:
        r5 = false;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x036d  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0423  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x0479  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x0482  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0485  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x0489  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0498  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x049b  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0153  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x016f  */
    /* JADX WARNING: Unknown variable types count: 3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void applyChainConstraints(androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r36, androidx.constraintlayout.solver.LinearSystem r37, int r38, int r39, androidx.constraintlayout.solver.widgets.ChainHead r40) {
        /*
            r0 = r36
            r9 = r37
            r1 = r40
            androidx.constraintlayout.solver.widgets.ConstraintWidget r10 = r1.mFirst
            androidx.constraintlayout.solver.widgets.ConstraintWidget r11 = r1.mLast
            androidx.constraintlayout.solver.widgets.ConstraintWidget r12 = r1.mFirstVisibleWidget
            androidx.constraintlayout.solver.widgets.ConstraintWidget r13 = r1.mLastVisibleWidget
            androidx.constraintlayout.solver.widgets.ConstraintWidget r2 = r1.mHead
            float r3 = r1.mTotalWeight
            androidx.constraintlayout.solver.widgets.ConstraintWidget r4 = r1.mFirstMatchConstraintWidget
            androidx.constraintlayout.solver.widgets.ConstraintWidget r4 = r1.mLastMatchConstraintWidget
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r4 = r0.mListDimensionBehaviors
            r4 = r4[r38]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r5 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            r7 = 1
            if (r4 != r5) goto L_0x0021
            r4 = r7
            goto L_0x0022
        L_0x0021:
            r4 = 0
        L_0x0022:
            r5 = 2
            if (r38 != 0) goto L_0x0038
            int r8 = r2.mHorizontalChainStyle
            if (r8 != 0) goto L_0x002b
            r8 = r7
            goto L_0x002c
        L_0x002b:
            r8 = 0
        L_0x002c:
            int r14 = r2.mHorizontalChainStyle
            if (r14 != r7) goto L_0x0032
            r14 = r7
            goto L_0x0033
        L_0x0032:
            r14 = 0
        L_0x0033:
            int r15 = r2.mHorizontalChainStyle
            if (r15 != r5) goto L_0x004c
            goto L_0x004a
        L_0x0038:
            int r8 = r2.mVerticalChainStyle
            if (r8 != 0) goto L_0x003e
            r8 = r7
            goto L_0x003f
        L_0x003e:
            r8 = 0
        L_0x003f:
            int r14 = r2.mVerticalChainStyle
            if (r14 != r7) goto L_0x0045
            r14 = r7
            goto L_0x0046
        L_0x0045:
            r14 = 0
        L_0x0046:
            int r15 = r2.mVerticalChainStyle
            if (r15 != r5) goto L_0x004c
        L_0x004a:
            r5 = r7
            goto L_0x004d
        L_0x004c:
            r5 = 0
        L_0x004d:
            r7 = r10
            r15 = r14
            r14 = r8
            r8 = 0
        L_0x0051:
            r20 = 0
            if (r8 != 0) goto L_0x0126
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r6 = r7.mListAnchors
            r6 = r6[r39]
            if (r4 != 0) goto L_0x0061
            if (r5 == 0) goto L_0x005e
            goto L_0x0061
        L_0x005e:
            r22 = 4
            goto L_0x0063
        L_0x0061:
            r22 = 1
        L_0x0063:
            int r23 = r6.getMargin()
            r24 = r3
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r3 = r6.mTarget
            if (r3 == 0) goto L_0x0075
            if (r7 == r10) goto L_0x0075
            int r3 = r3.getMargin()
            int r23 = r23 + r3
        L_0x0075:
            r3 = r23
            if (r5 == 0) goto L_0x0083
            if (r7 == r10) goto L_0x0083
            if (r7 == r12) goto L_0x0083
            r23 = r8
            r22 = r15
            r8 = 6
            goto L_0x0093
        L_0x0083:
            if (r14 == 0) goto L_0x008d
            if (r4 == 0) goto L_0x008d
            r23 = r8
            r22 = r15
            r8 = 4
            goto L_0x0093
        L_0x008d:
            r23 = r8
            r8 = r22
            r22 = r15
        L_0x0093:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r15 = r6.mTarget
            if (r15 == 0) goto L_0x00bc
            if (r7 != r12) goto L_0x00a6
            r25 = r14
            androidx.constraintlayout.solver.SolverVariable r14 = r6.mSolverVariable
            androidx.constraintlayout.solver.SolverVariable r15 = r15.mSolverVariable
            r26 = r2
            r2 = 5
            r9.addGreaterThan(r14, r15, r3, r2)
            goto L_0x00b2
        L_0x00a6:
            r26 = r2
            r25 = r14
            androidx.constraintlayout.solver.SolverVariable r2 = r6.mSolverVariable
            androidx.constraintlayout.solver.SolverVariable r14 = r15.mSolverVariable
            r15 = 6
            r9.addGreaterThan(r2, r14, r3, r15)
        L_0x00b2:
            androidx.constraintlayout.solver.SolverVariable r2 = r6.mSolverVariable
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r6 = r6.mTarget
            androidx.constraintlayout.solver.SolverVariable r6 = r6.mSolverVariable
            r9.addEquality(r2, r6, r3, r8)
            goto L_0x00c0
        L_0x00bc:
            r26 = r2
            r25 = r14
        L_0x00c0:
            if (r4 == 0) goto L_0x00f5
            int r2 = r7.getVisibility()
            r3 = 8
            if (r2 == r3) goto L_0x00e4
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r2 = r7.mListDimensionBehaviors
            r2 = r2[r38]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r3 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r2 != r3) goto L_0x00e4
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r2 = r7.mListAnchors
            int r3 = r39 + 1
            r3 = r2[r3]
            androidx.constraintlayout.solver.SolverVariable r3 = r3.mSolverVariable
            r2 = r2[r39]
            androidx.constraintlayout.solver.SolverVariable r2 = r2.mSolverVariable
            r6 = 5
            r8 = 0
            r9.addGreaterThan(r3, r2, r8, r6)
            goto L_0x00e5
        L_0x00e4:
            r8 = 0
        L_0x00e5:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r2 = r7.mListAnchors
            r2 = r2[r39]
            androidx.constraintlayout.solver.SolverVariable r2 = r2.mSolverVariable
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r3 = r0.mListAnchors
            r3 = r3[r39]
            androidx.constraintlayout.solver.SolverVariable r3 = r3.mSolverVariable
            r6 = 6
            r9.addGreaterThan(r2, r3, r8, r6)
        L_0x00f5:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r2 = r7.mListAnchors
            int r3 = r39 + 1
            r2 = r2[r3]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            if (r2 == 0) goto L_0x0114
            androidx.constraintlayout.solver.widgets.ConstraintWidget r2 = r2.mOwner
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r3 = r2.mListAnchors
            r6 = r3[r39]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r6 = r6.mTarget
            if (r6 == 0) goto L_0x0114
            r3 = r3[r39]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r3 = r3.mTarget
            androidx.constraintlayout.solver.widgets.ConstraintWidget r3 = r3.mOwner
            if (r3 == r7) goto L_0x0112
            goto L_0x0114
        L_0x0112:
            r20 = r2
        L_0x0114:
            if (r20 == 0) goto L_0x011b
            r7 = r20
            r8 = r23
            goto L_0x011c
        L_0x011b:
            r8 = 1
        L_0x011c:
            r15 = r22
            r3 = r24
            r14 = r25
            r2 = r26
            goto L_0x0051
        L_0x0126:
            r26 = r2
            r24 = r3
            r25 = r14
            r22 = r15
            if (r13 == 0) goto L_0x0150
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r2 = r11.mListAnchors
            int r3 = r39 + 1
            r6 = r2[r3]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r6 = r6.mTarget
            if (r6 == 0) goto L_0x0150
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r6 = r13.mListAnchors
            r6 = r6[r3]
            androidx.constraintlayout.solver.SolverVariable r7 = r6.mSolverVariable
            r2 = r2[r3]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            androidx.constraintlayout.solver.SolverVariable r2 = r2.mSolverVariable
            int r3 = r6.getMargin()
            int r3 = -r3
            r6 = 5
            r9.addLowerThan(r7, r2, r3, r6)
            goto L_0x0151
        L_0x0150:
            r6 = 5
        L_0x0151:
            if (r4 == 0) goto L_0x016b
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r0.mListAnchors
            int r2 = r39 + 1
            r0 = r0[r2]
            androidx.constraintlayout.solver.SolverVariable r0 = r0.mSolverVariable
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r3 = r11.mListAnchors
            r4 = r3[r2]
            androidx.constraintlayout.solver.SolverVariable r4 = r4.mSolverVariable
            r2 = r3[r2]
            int r2 = r2.getMargin()
            r3 = 6
            r9.addGreaterThan(r0, r4, r2, r3)
        L_0x016b:
            java.util.ArrayList<androidx.constraintlayout.solver.widgets.ConstraintWidget> r0 = r1.mWeightedMatchConstraintsWidgets
            if (r0 == 0) goto L_0x021a
            int r2 = r0.size()
            r3 = 1
            if (r2 <= r3) goto L_0x021a
            boolean r4 = r1.mHasUndefinedWeights
            if (r4 == 0) goto L_0x0182
            boolean r4 = r1.mHasComplexMatchWeights
            if (r4 != 0) goto L_0x0182
            int r4 = r1.mWidgetsMatchCount
            float r4 = (float) r4
            goto L_0x0184
        L_0x0182:
            r4 = r24
        L_0x0184:
            r7 = 0
            r28 = r7
            r14 = r20
            r8 = 0
        L_0x018a:
            if (r8 >= r2) goto L_0x021a
            java.lang.Object r15 = r0.get(r8)
            androidx.constraintlayout.solver.widgets.ConstraintWidget r15 = (androidx.constraintlayout.solver.widgets.ConstraintWidget) r15
            float[] r3 = r15.mWeight
            r3 = r3[r38]
            int r18 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r18 >= 0) goto L_0x01b6
            boolean r3 = r1.mHasComplexMatchWeights
            if (r3 == 0) goto L_0x01b1
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r3 = r15.mListAnchors
            int r15 = r39 + 1
            r15 = r3[r15]
            androidx.constraintlayout.solver.SolverVariable r15 = r15.mSolverVariable
            r3 = r3[r39]
            androidx.constraintlayout.solver.SolverVariable r3 = r3.mSolverVariable
            r6 = 4
            r7 = 0
            r9.addEquality(r15, r3, r7, r6)
            r6 = 6
            goto L_0x01cc
        L_0x01b1:
            r6 = 4
            r3 = 1065353216(0x3f800000, float:1.0)
            r7 = 0
            goto L_0x01b7
        L_0x01b6:
            r6 = 4
        L_0x01b7:
            int r19 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r19 != 0) goto L_0x01d1
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r3 = r15.mListAnchors
            int r15 = r39 + 1
            r15 = r3[r15]
            androidx.constraintlayout.solver.SolverVariable r15 = r15.mSolverVariable
            r3 = r3[r39]
            androidx.constraintlayout.solver.SolverVariable r3 = r3.mSolverVariable
            r6 = 6
            r7 = 0
            r9.addEquality(r15, r3, r7, r6)
        L_0x01cc:
            r24 = r0
            r17 = r2
            goto L_0x020f
        L_0x01d1:
            r6 = 6
            r7 = 0
            if (r14 == 0) goto L_0x0208
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r14 = r14.mListAnchors
            r6 = r14[r39]
            androidx.constraintlayout.solver.SolverVariable r6 = r6.mSolverVariable
            int r17 = r39 + 1
            r14 = r14[r17]
            androidx.constraintlayout.solver.SolverVariable r14 = r14.mSolverVariable
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r7 = r15.mListAnchors
            r24 = r0
            r0 = r7[r39]
            androidx.constraintlayout.solver.SolverVariable r0 = r0.mSolverVariable
            r7 = r7[r17]
            androidx.constraintlayout.solver.SolverVariable r7 = r7.mSolverVariable
            r17 = r2
            androidx.constraintlayout.solver.ArrayRow r2 = r37.createRow()
            r27 = r2
            r29 = r4
            r30 = r3
            r31 = r6
            r32 = r14
            r33 = r0
            r34 = r7
            r27.createRowEqualMatchDimensions(r28, r29, r30, r31, r32, r33, r34)
            r9.addConstraint(r2)
            goto L_0x020c
        L_0x0208:
            r24 = r0
            r17 = r2
        L_0x020c:
            r28 = r3
            r14 = r15
        L_0x020f:
            int r8 = r8 + 1
            r2 = r17
            r0 = r24
            r3 = 1
            r6 = 5
            r7 = 0
            goto L_0x018a
        L_0x021a:
            if (r12 == 0) goto L_0x027c
            if (r12 == r13) goto L_0x0220
            if (r5 == 0) goto L_0x027c
        L_0x0220:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r10.mListAnchors
            r1 = r0[r39]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r2 = r11.mListAnchors
            int r3 = r39 + 1
            r2 = r2[r3]
            r4 = r0[r39]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x0238
            r0 = r0[r39]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            androidx.constraintlayout.solver.SolverVariable r0 = r0.mSolverVariable
            r4 = r0
            goto L_0x023a
        L_0x0238:
            r4 = r20
        L_0x023a:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r11.mListAnchors
            r5 = r0[r3]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r5 = r5.mTarget
            if (r5 == 0) goto L_0x024a
            r0 = r0[r3]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            androidx.constraintlayout.solver.SolverVariable r0 = r0.mSolverVariable
            r5 = r0
            goto L_0x024c
        L_0x024a:
            r5 = r20
        L_0x024c:
            if (r12 != r13) goto L_0x0254
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r12.mListAnchors
            r1 = r0[r39]
            r2 = r0[r3]
        L_0x0254:
            if (r4 == 0) goto L_0x0465
            if (r5 == 0) goto L_0x0465
            if (r38 != 0) goto L_0x025f
            r0 = r26
            float r0 = r0.mHorizontalBiasPercent
            goto L_0x0263
        L_0x025f:
            r0 = r26
            float r0 = r0.mVerticalBiasPercent
        L_0x0263:
            r6 = r0
            int r3 = r1.getMargin()
            int r7 = r2.getMargin()
            androidx.constraintlayout.solver.SolverVariable r1 = r1.mSolverVariable
            androidx.constraintlayout.solver.SolverVariable r8 = r2.mSolverVariable
            r10 = 5
            r0 = r37
            r2 = r4
            r4 = r6
            r6 = r8
            r8 = r10
            r0.addCentering(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x0465
        L_0x027c:
            if (r25 == 0) goto L_0x0354
            if (r12 == 0) goto L_0x0354
            int r0 = r1.mWidgetsMatchCount
            if (r0 <= 0) goto L_0x028b
            int r1 = r1.mWidgetsCount
            if (r1 != r0) goto L_0x028b
            r16 = 1
            goto L_0x028d
        L_0x028b:
            r16 = 0
        L_0x028d:
            r0 = r12
            r14 = r0
        L_0x028f:
            if (r14 == 0) goto L_0x0465
            androidx.constraintlayout.solver.widgets.ConstraintWidget[] r1 = r14.mListNextVisibleWidget
            r15 = r1[r38]
            if (r15 != 0) goto L_0x02a0
            if (r14 != r13) goto L_0x029a
            goto L_0x02a0
        L_0x029a:
            r19 = 4
            r21 = 6
            goto L_0x0350
        L_0x02a0:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r1 = r14.mListAnchors
            r1 = r1[r39]
            androidx.constraintlayout.solver.SolverVariable r2 = r1.mSolverVariable
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r3 = r1.mTarget
            if (r3 == 0) goto L_0x02ad
            androidx.constraintlayout.solver.SolverVariable r3 = r3.mSolverVariable
            goto L_0x02af
        L_0x02ad:
            r3 = r20
        L_0x02af:
            if (r0 == r14) goto L_0x02ba
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r3 = r0.mListAnchors
            int r4 = r39 + 1
            r3 = r3[r4]
            androidx.constraintlayout.solver.SolverVariable r3 = r3.mSolverVariable
            goto L_0x02cf
        L_0x02ba:
            if (r14 != r12) goto L_0x02cf
            if (r0 != r14) goto L_0x02cf
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r3 = r10.mListAnchors
            r4 = r3[r39]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x02cd
            r3 = r3[r39]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r3 = r3.mTarget
            androidx.constraintlayout.solver.SolverVariable r3 = r3.mSolverVariable
            goto L_0x02cf
        L_0x02cd:
            r3 = r20
        L_0x02cf:
            int r1 = r1.getMargin()
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r4 = r14.mListAnchors
            int r5 = r39 + 1
            r4 = r4[r5]
            int r4 = r4.getMargin()
            if (r15 == 0) goto L_0x02ec
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r6 = r15.mListAnchors
            r6 = r6[r39]
            androidx.constraintlayout.solver.SolverVariable r7 = r6.mSolverVariable
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r8 = r14.mListAnchors
            r8 = r8[r5]
            androidx.constraintlayout.solver.SolverVariable r8 = r8.mSolverVariable
            goto L_0x02ff
        L_0x02ec:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r6 = r11.mListAnchors
            r6 = r6[r5]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r6 = r6.mTarget
            if (r6 == 0) goto L_0x02f7
            androidx.constraintlayout.solver.SolverVariable r7 = r6.mSolverVariable
            goto L_0x02f9
        L_0x02f7:
            r7 = r20
        L_0x02f9:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r8 = r14.mListAnchors
            r8 = r8[r5]
            androidx.constraintlayout.solver.SolverVariable r8 = r8.mSolverVariable
        L_0x02ff:
            if (r6 == 0) goto L_0x0306
            int r6 = r6.getMargin()
            int r4 = r4 + r6
        L_0x0306:
            if (r0 == 0) goto L_0x0311
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r0.mListAnchors
            r0 = r0[r5]
            int r0 = r0.getMargin()
            int r1 = r1 + r0
        L_0x0311:
            if (r2 == 0) goto L_0x029a
            if (r3 == 0) goto L_0x029a
            if (r7 == 0) goto L_0x029a
            if (r8 == 0) goto L_0x029a
            if (r14 != r12) goto L_0x0325
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r12.mListAnchors
            r0 = r0[r39]
            int r0 = r0.getMargin()
            r6 = r0
            goto L_0x0326
        L_0x0325:
            r6 = r1
        L_0x0326:
            if (r14 != r13) goto L_0x0333
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r13.mListAnchors
            r0 = r0[r5]
            int r0 = r0.getMargin()
            r17 = r0
            goto L_0x0335
        L_0x0333:
            r17 = r4
        L_0x0335:
            if (r16 == 0) goto L_0x033a
            r18 = 6
            goto L_0x033c
        L_0x033a:
            r18 = 4
        L_0x033c:
            r4 = 1056964608(0x3f000000, float:0.5)
            r0 = r37
            r1 = r2
            r2 = r3
            r3 = r6
            r5 = r7
            r19 = 4
            r21 = 6
            r6 = r8
            r7 = r17
            r8 = r18
            r0.addCentering(r1, r2, r3, r4, r5, r6, r7, r8)
        L_0x0350:
            r0 = r14
            r14 = r15
            goto L_0x028f
        L_0x0354:
            r19 = 4
            r21 = 6
            if (r22 == 0) goto L_0x0465
            if (r12 == 0) goto L_0x0465
            int r0 = r1.mWidgetsMatchCount
            if (r0 <= 0) goto L_0x0367
            int r1 = r1.mWidgetsCount
            if (r1 != r0) goto L_0x0367
            r16 = 1
            goto L_0x0369
        L_0x0367:
            r16 = 0
        L_0x0369:
            r0 = r12
            r14 = r0
        L_0x036b:
            if (r14 == 0) goto L_0x040a
            androidx.constraintlayout.solver.widgets.ConstraintWidget[] r1 = r14.mListNextVisibleWidget
            r1 = r1[r38]
            if (r14 == r12) goto L_0x0402
            if (r14 == r13) goto L_0x0402
            if (r1 == 0) goto L_0x0402
            if (r1 != r13) goto L_0x037c
            r15 = r20
            goto L_0x037d
        L_0x037c:
            r15 = r1
        L_0x037d:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r1 = r14.mListAnchors
            r1 = r1[r39]
            androidx.constraintlayout.solver.SolverVariable r2 = r1.mSolverVariable
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r3 = r1.mTarget
            if (r3 == 0) goto L_0x0389
            androidx.constraintlayout.solver.SolverVariable r3 = r3.mSolverVariable
        L_0x0389:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r3 = r0.mListAnchors
            int r4 = r39 + 1
            r3 = r3[r4]
            androidx.constraintlayout.solver.SolverVariable r3 = r3.mSolverVariable
            int r1 = r1.getMargin()
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r5 = r14.mListAnchors
            r5 = r5[r4]
            int r5 = r5.getMargin()
            if (r15 == 0) goto L_0x03af
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r6 = r15.mListAnchors
            r6 = r6[r39]
            androidx.constraintlayout.solver.SolverVariable r7 = r6.mSolverVariable
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r6.mTarget
            if (r8 == 0) goto L_0x03ac
            androidx.constraintlayout.solver.SolverVariable r8 = r8.mSolverVariable
            goto L_0x03c2
        L_0x03ac:
            r8 = r20
            goto L_0x03c2
        L_0x03af:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r6 = r14.mListAnchors
            r6 = r6[r4]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r6 = r6.mTarget
            if (r6 == 0) goto L_0x03ba
            androidx.constraintlayout.solver.SolverVariable r7 = r6.mSolverVariable
            goto L_0x03bc
        L_0x03ba:
            r7 = r20
        L_0x03bc:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r8 = r14.mListAnchors
            r8 = r8[r4]
            androidx.constraintlayout.solver.SolverVariable r8 = r8.mSolverVariable
        L_0x03c2:
            if (r6 == 0) goto L_0x03c9
            int r6 = r6.getMargin()
            int r5 = r5 + r6
        L_0x03c9:
            r17 = r5
            if (r0 == 0) goto L_0x03d6
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r0.mListAnchors
            r0 = r0[r4]
            int r0 = r0.getMargin()
            int r1 = r1 + r0
        L_0x03d6:
            r4 = r1
            if (r16 == 0) goto L_0x03dc
            r23 = r21
            goto L_0x03de
        L_0x03dc:
            r23 = r19
        L_0x03de:
            if (r2 == 0) goto L_0x03fd
            if (r3 == 0) goto L_0x03fd
            if (r7 == 0) goto L_0x03fd
            if (r8 == 0) goto L_0x03fd
            r5 = 1056964608(0x3f000000, float:0.5)
            r0 = r37
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r7
            r7 = 5
            r6 = r8
            r8 = r7
            r7 = r17
            r36 = r14
            r14 = r8
            r8 = r23
            r0.addCentering(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x0400
        L_0x03fd:
            r36 = r14
            r14 = 5
        L_0x0400:
            r1 = r15
            goto L_0x0405
        L_0x0402:
            r36 = r14
            r14 = 5
        L_0x0405:
            r0 = r36
            r14 = r1
            goto L_0x036b
        L_0x040a:
            r14 = 5
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r12.mListAnchors
            r0 = r0[r39]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r1 = r10.mListAnchors
            r1 = r1[r39]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r1 = r1.mTarget
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r2 = r13.mListAnchors
            int r3 = r39 + 1
            r10 = r2[r3]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r2 = r11.mListAnchors
            r2 = r2[r3]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r15 = r2.mTarget
            if (r1 == 0) goto L_0x0455
            if (r12 == r13) goto L_0x0431
            androidx.constraintlayout.solver.SolverVariable r2 = r0.mSolverVariable
            androidx.constraintlayout.solver.SolverVariable r1 = r1.mSolverVariable
            int r0 = r0.getMargin()
            r9.addEquality(r2, r1, r0, r14)
            goto L_0x0455
        L_0x0431:
            if (r15 == 0) goto L_0x0455
            androidx.constraintlayout.solver.SolverVariable r2 = r0.mSolverVariable
            androidx.constraintlayout.solver.SolverVariable r3 = r1.mSolverVariable
            int r4 = r0.getMargin()
            r5 = 1056964608(0x3f000000, float:0.5)
            androidx.constraintlayout.solver.SolverVariable r6 = r10.mSolverVariable
            androidx.constraintlayout.solver.SolverVariable r7 = r15.mSolverVariable
            int r8 = r10.getMargin()
            r16 = 5
            r0 = r37
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r8
            r8 = r16
            r0.addCentering(r1, r2, r3, r4, r5, r6, r7, r8)
        L_0x0455:
            if (r15 == 0) goto L_0x0465
            if (r12 == r13) goto L_0x0465
            androidx.constraintlayout.solver.SolverVariable r0 = r10.mSolverVariable
            androidx.constraintlayout.solver.SolverVariable r1 = r15.mSolverVariable
            int r2 = r10.getMargin()
            int r2 = -r2
            r9.addEquality(r0, r1, r2, r14)
        L_0x0465:
            if (r25 != 0) goto L_0x0469
            if (r22 == 0) goto L_0x04cb
        L_0x0469:
            if (r12 == 0) goto L_0x04cb
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r12.mListAnchors
            r0 = r0[r39]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r1 = r13.mListAnchors
            int r2 = r39 + 1
            r1 = r1[r2]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r3 = r0.mTarget
            if (r3 == 0) goto L_0x047c
            androidx.constraintlayout.solver.SolverVariable r3 = r3.mSolverVariable
            goto L_0x047e
        L_0x047c:
            r3 = r20
        L_0x047e:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r4 = r1.mTarget
            if (r4 == 0) goto L_0x0485
            androidx.constraintlayout.solver.SolverVariable r4 = r4.mSolverVariable
            goto L_0x0487
        L_0x0485:
            r4 = r20
        L_0x0487:
            if (r11 == r13) goto L_0x0498
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r4 = r11.mListAnchors
            r4 = r4[r2]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x0495
            androidx.constraintlayout.solver.SolverVariable r4 = r4.mSolverVariable
            r20 = r4
        L_0x0495:
            r5 = r20
            goto L_0x0499
        L_0x0498:
            r5 = r4
        L_0x0499:
            if (r12 != r13) goto L_0x04a6
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r12.mListAnchors
            r1 = r0[r39]
            r0 = r0[r2]
            r35 = r1
            r1 = r0
            r0 = r35
        L_0x04a6:
            if (r3 == 0) goto L_0x04cb
            if (r5 == 0) goto L_0x04cb
            r4 = 1056964608(0x3f000000, float:0.5)
            int r6 = r0.getMargin()
            if (r13 != 0) goto L_0x04b3
            goto L_0x04b4
        L_0x04b3:
            r11 = r13
        L_0x04b4:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r7 = r11.mListAnchors
            r2 = r7[r2]
            int r7 = r2.getMargin()
            androidx.constraintlayout.solver.SolverVariable r2 = r0.mSolverVariable
            androidx.constraintlayout.solver.SolverVariable r8 = r1.mSolverVariable
            r10 = 5
            r0 = r37
            r1 = r2
            r2 = r3
            r3 = r6
            r6 = r8
            r8 = r10
            r0.addCentering(r1, r2, r3, r4, r5, r6, r7, r8)
        L_0x04cb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.solver.widgets.Chain.applyChainConstraints(androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer, androidx.constraintlayout.solver.LinearSystem, int, int, androidx.constraintlayout.solver.widgets.ChainHead):void");
    }
}
