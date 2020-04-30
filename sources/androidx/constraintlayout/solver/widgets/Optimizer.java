package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour;

public class Optimizer {
    static boolean[] flags = new boolean[3];

    static void checkMatchParent(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ConstraintWidget constraintWidget) {
        if (constraintWidgetContainer.mListDimensionBehaviors[0] != DimensionBehaviour.WRAP_CONTENT && constraintWidget.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_PARENT) {
            int i = constraintWidget.mLeft.mMargin;
            int width = constraintWidgetContainer.getWidth() - constraintWidget.mRight.mMargin;
            ConstraintAnchor constraintAnchor = constraintWidget.mLeft;
            constraintAnchor.mSolverVariable = linearSystem.createObjectVariable(constraintAnchor);
            ConstraintAnchor constraintAnchor2 = constraintWidget.mRight;
            constraintAnchor2.mSolverVariable = linearSystem.createObjectVariable(constraintAnchor2);
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, i);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width);
            constraintWidget.mHorizontalResolution = 2;
            constraintWidget.setHorizontalDimension(i, width);
        }
        if (constraintWidgetContainer.mListDimensionBehaviors[1] != DimensionBehaviour.WRAP_CONTENT && constraintWidget.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_PARENT) {
            int i2 = constraintWidget.mTop.mMargin;
            int height = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.mMargin;
            ConstraintAnchor constraintAnchor3 = constraintWidget.mTop;
            constraintAnchor3.mSolverVariable = linearSystem.createObjectVariable(constraintAnchor3);
            ConstraintAnchor constraintAnchor4 = constraintWidget.mBottom;
            constraintAnchor4.mSolverVariable = linearSystem.createObjectVariable(constraintAnchor4);
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, i2);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                ConstraintAnchor constraintAnchor5 = constraintWidget.mBaseline;
                constraintAnchor5.mSolverVariable = linearSystem.createObjectVariable(constraintAnchor5);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + i2);
            }
            constraintWidget.mVerticalResolution = 2;
            constraintWidget.setVerticalDimension(i2, height);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x003b A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean optimizableMatchConstraint(androidx.constraintlayout.solver.widgets.ConstraintWidget r4, int r5) {
        /*
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r0 = r4.mListDimensionBehaviors
            r1 = r0[r5]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r2 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            r3 = 0
            if (r1 == r2) goto L_0x000a
            return r3
        L_0x000a:
            float r1 = r4.mDimensionRatio
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            r2 = 1
            if (r1 == 0) goto L_0x001d
            if (r5 != 0) goto L_0x0015
            goto L_0x0016
        L_0x0015:
            r2 = r3
        L_0x0016:
            r4 = r0[r2]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r5 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r4 != r5) goto L_0x001c
        L_0x001c:
            return r3
        L_0x001d:
            if (r5 != 0) goto L_0x002d
            int r5 = r4.mMatchConstraintDefaultWidth
            if (r5 == 0) goto L_0x0024
            return r3
        L_0x0024:
            int r5 = r4.mMatchConstraintMinWidth
            if (r5 != 0) goto L_0x002c
            int r4 = r4.mMatchConstraintMaxWidth
            if (r4 == 0) goto L_0x003b
        L_0x002c:
            return r3
        L_0x002d:
            int r5 = r4.mMatchConstraintDefaultHeight
            if (r5 == 0) goto L_0x0032
            return r3
        L_0x0032:
            int r5 = r4.mMatchConstraintMinHeight
            if (r5 != 0) goto L_0x003c
            int r4 = r4.mMatchConstraintMaxHeight
            if (r4 == 0) goto L_0x003b
            goto L_0x003c
        L_0x003b:
            return r2
        L_0x003c:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.solver.widgets.Optimizer.optimizableMatchConstraint(androidx.constraintlayout.solver.widgets.ConstraintWidget, int):boolean");
    }

    static void analyze(int i, ConstraintWidget constraintWidget) {
        ConstraintWidget constraintWidget2 = constraintWidget;
        constraintWidget.updateResolutionNodes();
        ResolutionAnchor resolutionNode = constraintWidget2.mLeft.getResolutionNode();
        ResolutionAnchor resolutionNode2 = constraintWidget2.mTop.getResolutionNode();
        ResolutionAnchor resolutionNode3 = constraintWidget2.mRight.getResolutionNode();
        ResolutionAnchor resolutionNode4 = constraintWidget2.mBottom.getResolutionNode();
        boolean z = (i & 8) == 8;
        boolean z2 = constraintWidget2.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_CONSTRAINT && optimizableMatchConstraint(constraintWidget2, 0);
        if (!(resolutionNode.type == 4 || resolutionNode3.type == 4)) {
            if (constraintWidget2.mListDimensionBehaviors[0] == DimensionBehaviour.FIXED || (z2 && constraintWidget.getVisibility() == 8)) {
                if (constraintWidget2.mLeft.mTarget == null && constraintWidget2.mRight.mTarget == null) {
                    resolutionNode.setType(1);
                    resolutionNode3.setType(1);
                    if (z) {
                        resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode3.dependsOn(resolutionNode, constraintWidget.getWidth());
                    }
                } else if (constraintWidget2.mLeft.mTarget != null && constraintWidget2.mRight.mTarget == null) {
                    resolutionNode.setType(1);
                    resolutionNode3.setType(1);
                    if (z) {
                        resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode3.dependsOn(resolutionNode, constraintWidget.getWidth());
                    }
                } else if (constraintWidget2.mLeft.mTarget == null && constraintWidget2.mRight.mTarget != null) {
                    resolutionNode.setType(1);
                    resolutionNode3.setType(1);
                    resolutionNode.dependsOn(resolutionNode3, -constraintWidget.getWidth());
                    if (z) {
                        resolutionNode.dependsOn(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode.dependsOn(resolutionNode3, -constraintWidget.getWidth());
                    }
                } else if (!(constraintWidget2.mLeft.mTarget == null || constraintWidget2.mRight.mTarget == null)) {
                    resolutionNode.setType(2);
                    resolutionNode3.setType(2);
                    if (z) {
                        constraintWidget.getResolutionWidth().addDependent(resolutionNode);
                        constraintWidget.getResolutionWidth().addDependent(resolutionNode3);
                        resolutionNode.setOpposite(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                        resolutionNode3.setOpposite(resolutionNode, 1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode.setOpposite(resolutionNode3, (float) (-constraintWidget.getWidth()));
                        resolutionNode3.setOpposite(resolutionNode, (float) constraintWidget.getWidth());
                    }
                }
            } else if (z2) {
                int width = constraintWidget.getWidth();
                resolutionNode.setType(1);
                resolutionNode3.setType(1);
                if (constraintWidget2.mLeft.mTarget == null && constraintWidget2.mRight.mTarget == null) {
                    if (z) {
                        resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode3.dependsOn(resolutionNode, width);
                    }
                } else if (constraintWidget2.mLeft.mTarget == null || constraintWidget2.mRight.mTarget != null) {
                    if (constraintWidget2.mLeft.mTarget != null || constraintWidget2.mRight.mTarget == null) {
                        if (!(constraintWidget2.mLeft.mTarget == null || constraintWidget2.mRight.mTarget == null)) {
                            if (z) {
                                constraintWidget.getResolutionWidth().addDependent(resolutionNode);
                                constraintWidget.getResolutionWidth().addDependent(resolutionNode3);
                            }
                            if (constraintWidget2.mDimensionRatio == 0.0f) {
                                resolutionNode.setType(3);
                                resolutionNode3.setType(3);
                                resolutionNode.setOpposite(resolutionNode3, 0.0f);
                                resolutionNode3.setOpposite(resolutionNode, 0.0f);
                            } else {
                                resolutionNode.setType(2);
                                resolutionNode3.setType(2);
                                resolutionNode.setOpposite(resolutionNode3, (float) (-width));
                                resolutionNode3.setOpposite(resolutionNode, (float) width);
                                constraintWidget2.setWidth(width);
                            }
                        }
                    } else if (z) {
                        resolutionNode.dependsOn(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode.dependsOn(resolutionNode3, -width);
                    }
                } else if (z) {
                    resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                } else {
                    resolutionNode3.dependsOn(resolutionNode, width);
                }
            }
        }
        boolean z3 = constraintWidget2.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_CONSTRAINT && optimizableMatchConstraint(constraintWidget2, 1);
        if (resolutionNode2.type != 4 && resolutionNode4.type != 4) {
            if (constraintWidget2.mListDimensionBehaviors[1] == DimensionBehaviour.FIXED || (z3 && constraintWidget.getVisibility() == 8)) {
                if (constraintWidget2.mTop.mTarget == null && constraintWidget2.mBottom.mTarget == null) {
                    resolutionNode2.setType(1);
                    resolutionNode4.setType(1);
                    if (z) {
                        resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                    } else {
                        resolutionNode4.dependsOn(resolutionNode2, constraintWidget.getHeight());
                    }
                    ConstraintAnchor constraintAnchor = constraintWidget2.mBaseline;
                    if (constraintAnchor.mTarget != null) {
                        constraintAnchor.getResolutionNode().setType(1);
                        resolutionNode2.dependsOn(1, constraintWidget2.mBaseline.getResolutionNode(), -constraintWidget2.mBaselineDistance);
                    }
                } else if (constraintWidget2.mTop.mTarget != null && constraintWidget2.mBottom.mTarget == null) {
                    resolutionNode2.setType(1);
                    resolutionNode4.setType(1);
                    if (z) {
                        resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                    } else {
                        resolutionNode4.dependsOn(resolutionNode2, constraintWidget.getHeight());
                    }
                    if (constraintWidget2.mBaselineDistance > 0) {
                        constraintWidget2.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget2.mBaselineDistance);
                    }
                } else if (constraintWidget2.mTop.mTarget == null && constraintWidget2.mBottom.mTarget != null) {
                    resolutionNode2.setType(1);
                    resolutionNode4.setType(1);
                    if (z) {
                        resolutionNode2.dependsOn(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                    } else {
                        resolutionNode2.dependsOn(resolutionNode4, -constraintWidget.getHeight());
                    }
                    if (constraintWidget2.mBaselineDistance > 0) {
                        constraintWidget2.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget2.mBaselineDistance);
                    }
                } else if (constraintWidget2.mTop.mTarget != null && constraintWidget2.mBottom.mTarget != null) {
                    resolutionNode2.setType(2);
                    resolutionNode4.setType(2);
                    if (z) {
                        resolutionNode2.setOpposite(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                        resolutionNode4.setOpposite(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                        constraintWidget.getResolutionHeight().addDependent(resolutionNode2);
                        constraintWidget.getResolutionWidth().addDependent(resolutionNode4);
                    } else {
                        resolutionNode2.setOpposite(resolutionNode4, (float) (-constraintWidget.getHeight()));
                        resolutionNode4.setOpposite(resolutionNode2, (float) constraintWidget.getHeight());
                    }
                    if (constraintWidget2.mBaselineDistance > 0) {
                        constraintWidget2.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget2.mBaselineDistance);
                    }
                }
            } else if (z3) {
                int height = constraintWidget.getHeight();
                resolutionNode2.setType(1);
                resolutionNode4.setType(1);
                if (constraintWidget2.mTop.mTarget == null && constraintWidget2.mBottom.mTarget == null) {
                    if (z) {
                        resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                    } else {
                        resolutionNode4.dependsOn(resolutionNode2, height);
                    }
                } else if (constraintWidget2.mTop.mTarget == null || constraintWidget2.mBottom.mTarget != null) {
                    if (constraintWidget2.mTop.mTarget != null || constraintWidget2.mBottom.mTarget == null) {
                        if (constraintWidget2.mTop.mTarget != null && constraintWidget2.mBottom.mTarget != null) {
                            if (z) {
                                constraintWidget.getResolutionHeight().addDependent(resolutionNode2);
                                constraintWidget.getResolutionWidth().addDependent(resolutionNode4);
                            }
                            if (constraintWidget2.mDimensionRatio == 0.0f) {
                                resolutionNode2.setType(3);
                                resolutionNode4.setType(3);
                                resolutionNode2.setOpposite(resolutionNode4, 0.0f);
                                resolutionNode4.setOpposite(resolutionNode2, 0.0f);
                                return;
                            }
                            resolutionNode2.setType(2);
                            resolutionNode4.setType(2);
                            resolutionNode2.setOpposite(resolutionNode4, (float) (-height));
                            resolutionNode4.setOpposite(resolutionNode2, (float) height);
                            constraintWidget2.setHeight(height);
                            if (constraintWidget2.mBaselineDistance > 0) {
                                constraintWidget2.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget2.mBaselineDistance);
                            }
                        }
                    } else if (z) {
                        resolutionNode2.dependsOn(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                    } else {
                        resolutionNode2.dependsOn(resolutionNode4, -height);
                    }
                } else if (z) {
                    resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                } else {
                    resolutionNode4.dependsOn(resolutionNode2, height);
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0030, code lost:
        if (r6.mHorizontalChainStyle == 2) goto L_0x0032;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0034, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0046, code lost:
        if (r6.mVerticalChainStyle == 2) goto L_0x0032;
     */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00e8  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00ea  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean applyChainOptimized(androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r20, androidx.constraintlayout.solver.LinearSystem r21, int r22, int r23, androidx.constraintlayout.solver.widgets.ChainHead r24) {
        /*
            r0 = r21
            r1 = r24
            androidx.constraintlayout.solver.widgets.ConstraintWidget r2 = r1.mFirst
            androidx.constraintlayout.solver.widgets.ConstraintWidget r3 = r1.mLast
            androidx.constraintlayout.solver.widgets.ConstraintWidget r4 = r1.mFirstVisibleWidget
            androidx.constraintlayout.solver.widgets.ConstraintWidget r5 = r1.mLastVisibleWidget
            androidx.constraintlayout.solver.widgets.ConstraintWidget r6 = r1.mHead
            float r7 = r1.mTotalWeight
            androidx.constraintlayout.solver.widgets.ConstraintWidget r8 = r1.mFirstMatchConstraintWidget
            androidx.constraintlayout.solver.widgets.ConstraintWidget r1 = r1.mLastMatchConstraintWidget
            r8 = r20
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r1 = r8.mListDimensionBehaviors
            r1 = r1[r22]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r8 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            r1 = 2
            r9 = 1
            if (r22 != 0) goto L_0x0036
            int r10 = r6.mHorizontalChainStyle
            if (r10 != 0) goto L_0x0026
            r10 = r9
            goto L_0x0027
        L_0x0026:
            r10 = 0
        L_0x0027:
            int r11 = r6.mHorizontalChainStyle
            if (r11 != r9) goto L_0x002d
            r11 = r9
            goto L_0x002e
        L_0x002d:
            r11 = 0
        L_0x002e:
            int r6 = r6.mHorizontalChainStyle
            if (r6 != r1) goto L_0x0034
        L_0x0032:
            r1 = r9
            goto L_0x0049
        L_0x0034:
            r1 = 0
            goto L_0x0049
        L_0x0036:
            int r10 = r6.mVerticalChainStyle
            if (r10 != 0) goto L_0x003c
            r10 = r9
            goto L_0x003d
        L_0x003c:
            r10 = 0
        L_0x003d:
            int r11 = r6.mVerticalChainStyle
            if (r11 != r9) goto L_0x0043
            r11 = r9
            goto L_0x0044
        L_0x0043:
            r11 = 0
        L_0x0044:
            int r6 = r6.mVerticalChainStyle
            if (r6 != r1) goto L_0x0034
            goto L_0x0032
        L_0x0049:
            r13 = r2
            r6 = 0
            r12 = 0
            r14 = 0
            r15 = 0
            r16 = 0
        L_0x0050:
            if (r12 != 0) goto L_0x00f0
            int r9 = r13.getVisibility()
            r8 = 8
            if (r9 == r8) goto L_0x008d
            int r14 = r14 + 1
            if (r22 != 0) goto L_0x0063
            int r9 = r13.getWidth()
            goto L_0x0067
        L_0x0063:
            int r9 = r13.getHeight()
        L_0x0067:
            float r9 = (float) r9
            float r15 = r15 + r9
            if (r13 == r4) goto L_0x0075
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r9 = r13.mListAnchors
            r9 = r9[r23]
            int r9 = r9.getMargin()
            float r9 = (float) r9
            float r15 = r15 + r9
        L_0x0075:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r9 = r13.mListAnchors
            r9 = r9[r23]
            int r9 = r9.getMargin()
            float r9 = (float) r9
            float r16 = r16 + r9
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r9 = r13.mListAnchors
            int r17 = r23 + 1
            r9 = r9[r17]
            int r9 = r9.getMargin()
            float r9 = (float) r9
            float r16 = r16 + r9
        L_0x008d:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r9 = r13.mListAnchors
            r9 = r9[r23]
            int r9 = r13.getVisibility()
            if (r9 == r8) goto L_0x00c2
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r8 = r13.mListDimensionBehaviors
            r8 = r8[r22]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r9 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r8 != r9) goto L_0x00c2
            int r6 = r6 + 1
            if (r22 != 0) goto L_0x00b3
            int r8 = r13.mMatchConstraintDefaultWidth
            if (r8 == 0) goto L_0x00a9
            r8 = 0
            return r8
        L_0x00a9:
            r8 = 0
            int r9 = r13.mMatchConstraintMinWidth
            if (r9 != 0) goto L_0x00b2
            int r9 = r13.mMatchConstraintMaxWidth
            if (r9 == 0) goto L_0x00c2
        L_0x00b2:
            return r8
        L_0x00b3:
            r8 = 0
            int r9 = r13.mMatchConstraintDefaultHeight
            if (r9 == 0) goto L_0x00b9
            return r8
        L_0x00b9:
            int r9 = r13.mMatchConstraintMinHeight
            if (r9 != 0) goto L_0x00c1
            int r9 = r13.mMatchConstraintMaxHeight
            if (r9 == 0) goto L_0x00c2
        L_0x00c1:
            return r8
        L_0x00c2:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r8 = r13.mListAnchors
            int r9 = r23 + 1
            r8 = r8[r9]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r8 = r8.mTarget
            if (r8 == 0) goto L_0x00e3
            androidx.constraintlayout.solver.widgets.ConstraintWidget r8 = r8.mOwner
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r9 = r8.mListAnchors
            r18 = r6
            r6 = r9[r23]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r6 = r6.mTarget
            if (r6 == 0) goto L_0x00e5
            r6 = r9[r23]
            androidx.constraintlayout.solver.widgets.ConstraintAnchor r6 = r6.mTarget
            androidx.constraintlayout.solver.widgets.ConstraintWidget r6 = r6.mOwner
            if (r6 == r13) goto L_0x00e1
            goto L_0x00e5
        L_0x00e1:
            r9 = r8
            goto L_0x00e6
        L_0x00e3:
            r18 = r6
        L_0x00e5:
            r9 = 0
        L_0x00e6:
            if (r9 == 0) goto L_0x00ea
            r13 = r9
            goto L_0x00eb
        L_0x00ea:
            r12 = 1
        L_0x00eb:
            r6 = r18
            r9 = 1
            goto L_0x0050
        L_0x00f0:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r8 = r2.mListAnchors
            r8 = r8[r23]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r8 = r8.getResolutionNode()
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r3 = r3.mListAnchors
            int r9 = r23 + 1
            r3 = r3[r9]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r3 = r3.getResolutionNode()
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r12 = r8.target
            if (r12 == 0) goto L_0x0336
            r17 = r2
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r2 = r3.target
            if (r2 != 0) goto L_0x010e
            goto L_0x0336
        L_0x010e:
            int r12 = r12.state
            r0 = 1
            if (r12 == r0) goto L_0x0119
            int r2 = r2.state
            if (r2 == r0) goto L_0x0119
            r0 = 0
            return r0
        L_0x0119:
            r0 = 0
            if (r6 <= 0) goto L_0x011f
            if (r6 == r14) goto L_0x011f
            return r0
        L_0x011f:
            if (r1 != 0) goto L_0x0128
            if (r10 != 0) goto L_0x0128
            if (r11 == 0) goto L_0x0126
            goto L_0x0128
        L_0x0126:
            r0 = 0
            goto L_0x0141
        L_0x0128:
            if (r4 == 0) goto L_0x0134
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r4.mListAnchors
            r0 = r0[r23]
            int r0 = r0.getMargin()
            float r0 = (float) r0
            goto L_0x0135
        L_0x0134:
            r0 = 0
        L_0x0135:
            if (r5 == 0) goto L_0x0141
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r2 = r5.mListAnchors
            r2 = r2[r9]
            int r2 = r2.getMargin()
            float r2 = (float) r2
            float r0 = r0 + r2
        L_0x0141:
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r2 = r8.target
            float r2 = r2.resolvedOffset
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r3 = r3.target
            float r3 = r3.resolvedOffset
            int r12 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r12 >= 0) goto L_0x014f
            float r3 = r3 - r2
            goto L_0x0151
        L_0x014f:
            float r3 = r2 - r3
        L_0x0151:
            float r3 = r3 - r15
            r18 = 1
            if (r6 <= 0) goto L_0x020f
            if (r6 != r14) goto L_0x020f
            androidx.constraintlayout.solver.widgets.ConstraintWidget r1 = r13.getParent()
            if (r1 == 0) goto L_0x016c
            androidx.constraintlayout.solver.widgets.ConstraintWidget r1 = r13.getParent()
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour[] r1 = r1.mListDimensionBehaviors
            r1 = r1[r22]
            androidx.constraintlayout.solver.widgets.ConstraintWidget$DimensionBehaviour r11 = androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r1 != r11) goto L_0x016c
            r1 = 0
            return r1
        L_0x016c:
            float r3 = r3 + r15
            float r3 = r3 - r16
            if (r10 == 0) goto L_0x0175
            float r16 = r16 - r0
            float r3 = r3 - r16
        L_0x0175:
            if (r10 == 0) goto L_0x0191
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r4.mListAnchors
            r0 = r0[r9]
            int r0 = r0.getMargin()
            float r0 = (float) r0
            float r2 = r2 + r0
            androidx.constraintlayout.solver.widgets.ConstraintWidget[] r0 = r4.mListNextVisibleWidget
            r0 = r0[r22]
            if (r0 == 0) goto L_0x0191
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r0 = r0.mListAnchors
            r0 = r0[r23]
            int r0 = r0.getMargin()
            float r0 = (float) r0
            float r2 = r2 + r0
        L_0x0191:
            if (r4 == 0) goto L_0x020d
            androidx.constraintlayout.solver.Metrics r0 = androidx.constraintlayout.solver.LinearSystem.sMetrics
            if (r0 == 0) goto L_0x01a9
            long r10 = r0.nonresolvedWidgets
            long r10 = r10 - r18
            r0.nonresolvedWidgets = r10
            long r10 = r0.resolvedWidgets
            long r10 = r10 + r18
            r0.resolvedWidgets = r10
            long r10 = r0.chainConnectionResolved
            long r10 = r10 + r18
            r0.chainConnectionResolved = r10
        L_0x01a9:
            androidx.constraintlayout.solver.widgets.ConstraintWidget[] r0 = r4.mListNextVisibleWidget
            r0 = r0[r22]
            if (r0 != 0) goto L_0x01b6
            if (r4 != r5) goto L_0x01b2
            goto L_0x01b6
        L_0x01b2:
            r12 = r21
            r10 = 0
            goto L_0x020b
        L_0x01b6:
            float r1 = (float) r6
            float r1 = r3 / r1
            r10 = 0
            int r11 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r11 <= 0) goto L_0x01c4
            float[] r1 = r4.mWeight
            r1 = r1[r22]
            float r1 = r1 * r3
            float r1 = r1 / r7
        L_0x01c4:
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r11 = r4.mListAnchors
            r11 = r11[r23]
            int r11 = r11.getMargin()
            float r11 = (float) r11
            float r2 = r2 + r11
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r11 = r4.mListAnchors
            r11 = r11[r23]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r11 = r11.getResolutionNode()
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r12 = r8.resolvedTarget
            r11.resolve(r12, r2)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r11 = r4.mListAnchors
            r11 = r11[r9]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r11 = r11.getResolutionNode()
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r12 = r8.resolvedTarget
            float r2 = r2 + r1
            r11.resolve(r12, r2)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r1 = r4.mListAnchors
            r1 = r1[r23]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r1 = r1.getResolutionNode()
            r12 = r21
            r1.addResolvedValue(r12)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r1 = r4.mListAnchors
            r1 = r1[r9]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r1 = r1.getResolutionNode()
            r1.addResolvedValue(r12)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r1 = r4.mListAnchors
            r1 = r1[r9]
            int r1 = r1.getMargin()
            float r1 = (float) r1
            float r2 = r2 + r1
        L_0x020b:
            r4 = r0
            goto L_0x0191
        L_0x020d:
            r0 = 1
            return r0
        L_0x020f:
            r12 = r21
            int r6 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r6 >= 0) goto L_0x0217
            r6 = 0
            return r6
        L_0x0217:
            if (r1 == 0) goto L_0x0293
            float r3 = r3 - r0
            float r0 = r17.getHorizontalBiasPercent()
            float r3 = r3 * r0
            float r2 = r2 + r3
        L_0x0220:
            if (r4 == 0) goto L_0x0298
            androidx.constraintlayout.solver.Metrics r0 = androidx.constraintlayout.solver.LinearSystem.sMetrics
            if (r0 == 0) goto L_0x0238
            long r6 = r0.nonresolvedWidgets
            long r6 = r6 - r18
            r0.nonresolvedWidgets = r6
            long r6 = r0.resolvedWidgets
            long r6 = r6 + r18
            r0.resolvedWidgets = r6
            long r6 = r0.chainConnectionResolved
            long r6 = r6 + r18
            r0.chainConnectionResolved = r6
        L_0x0238:
            androidx.constraintlayout.solver.widgets.ConstraintWidget[] r0 = r4.mListNextVisibleWidget
            r0 = r0[r22]
            if (r0 != 0) goto L_0x0240
            if (r4 != r5) goto L_0x0291
        L_0x0240:
            if (r22 != 0) goto L_0x0247
            int r1 = r4.getWidth()
            goto L_0x024b
        L_0x0247:
            int r1 = r4.getHeight()
        L_0x024b:
            float r1 = (float) r1
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r3 = r4.mListAnchors
            r3 = r3[r23]
            int r3 = r3.getMargin()
            float r3 = (float) r3
            float r2 = r2 + r3
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r3 = r4.mListAnchors
            r3 = r3[r23]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r3 = r3.getResolutionNode()
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r6 = r8.resolvedTarget
            r3.resolve(r6, r2)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r3 = r4.mListAnchors
            r3 = r3[r9]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r3 = r3.getResolutionNode()
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r6 = r8.resolvedTarget
            float r2 = r2 + r1
            r3.resolve(r6, r2)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r1 = r4.mListAnchors
            r1 = r1[r23]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r1 = r1.getResolutionNode()
            r1.addResolvedValue(r12)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r1 = r4.mListAnchors
            r1 = r1[r9]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r1 = r1.getResolutionNode()
            r1.addResolvedValue(r12)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r1 = r4.mListAnchors
            r1 = r1[r9]
            int r1 = r1.getMargin()
            float r1 = (float) r1
            float r2 = r2 + r1
        L_0x0291:
            r4 = r0
            goto L_0x0220
        L_0x0293:
            if (r10 != 0) goto L_0x029b
            if (r11 == 0) goto L_0x0298
            goto L_0x029b
        L_0x0298:
            r0 = 1
            goto L_0x0335
        L_0x029b:
            if (r10 == 0) goto L_0x029f
        L_0x029d:
            float r3 = r3 - r0
            goto L_0x02a2
        L_0x029f:
            if (r11 == 0) goto L_0x02a2
            goto L_0x029d
        L_0x02a2:
            int r0 = r14 + 1
            float r0 = (float) r0
            float r0 = r3 / r0
            if (r11 == 0) goto L_0x02b4
            r1 = 1
            if (r14 <= r1) goto L_0x02b0
            int r0 = r14 + -1
            float r0 = (float) r0
            goto L_0x02b2
        L_0x02b0:
            r0 = 1073741824(0x40000000, float:2.0)
        L_0x02b2:
            float r0 = r3 / r0
        L_0x02b4:
            float r1 = r2 + r0
            if (r11 == 0) goto L_0x02c5
            r3 = 1
            if (r14 <= r3) goto L_0x02c5
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r1 = r4.mListAnchors
            r1 = r1[r23]
            int r1 = r1.getMargin()
            float r1 = (float) r1
            float r1 = r1 + r2
        L_0x02c5:
            if (r10 == 0) goto L_0x02d3
            if (r4 == 0) goto L_0x02d3
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r2 = r4.mListAnchors
            r2 = r2[r23]
            int r2 = r2.getMargin()
            float r2 = (float) r2
            float r1 = r1 + r2
        L_0x02d3:
            if (r4 == 0) goto L_0x0298
            androidx.constraintlayout.solver.Metrics r2 = androidx.constraintlayout.solver.LinearSystem.sMetrics
            if (r2 == 0) goto L_0x02eb
            long r6 = r2.nonresolvedWidgets
            long r6 = r6 - r18
            r2.nonresolvedWidgets = r6
            long r6 = r2.resolvedWidgets
            long r6 = r6 + r18
            r2.resolvedWidgets = r6
            long r6 = r2.chainConnectionResolved
            long r6 = r6 + r18
            r2.chainConnectionResolved = r6
        L_0x02eb:
            androidx.constraintlayout.solver.widgets.ConstraintWidget[] r2 = r4.mListNextVisibleWidget
            r2 = r2[r22]
            if (r2 != 0) goto L_0x02f3
            if (r4 != r5) goto L_0x0333
        L_0x02f3:
            if (r22 != 0) goto L_0x02fa
            int r3 = r4.getWidth()
            goto L_0x02fe
        L_0x02fa:
            int r3 = r4.getHeight()
        L_0x02fe:
            float r3 = (float) r3
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r6 = r4.mListAnchors
            r6 = r6[r23]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r6 = r6.getResolutionNode()
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r7 = r8.resolvedTarget
            r6.resolve(r7, r1)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r6 = r4.mListAnchors
            r6 = r6[r9]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r6 = r6.getResolutionNode()
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r7 = r8.resolvedTarget
            float r10 = r1 + r3
            r6.resolve(r7, r10)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r6 = r4.mListAnchors
            r6 = r6[r23]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r6 = r6.getResolutionNode()
            r6.addResolvedValue(r12)
            androidx.constraintlayout.solver.widgets.ConstraintAnchor[] r4 = r4.mListAnchors
            r4 = r4[r9]
            androidx.constraintlayout.solver.widgets.ResolutionAnchor r4 = r4.getResolutionNode()
            r4.addResolvedValue(r12)
            float r3 = r3 + r0
            float r1 = r1 + r3
        L_0x0333:
            r4 = r2
            goto L_0x02d3
        L_0x0335:
            return r0
        L_0x0336:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.solver.widgets.Optimizer.applyChainOptimized(androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer, androidx.constraintlayout.solver.LinearSystem, int, int, androidx.constraintlayout.solver.widgets.ChainHead):boolean");
    }
}
