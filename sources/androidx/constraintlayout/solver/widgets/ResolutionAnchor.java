package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.Metrics;
import androidx.constraintlayout.solver.SolverVariable;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type;

public class ResolutionAnchor extends ResolutionNode {
    private ResolutionDimension dimension = null;
    private int dimensionMultiplier = 1;
    ConstraintAnchor myAnchor;
    float offset;
    private ResolutionAnchor opposite;
    private ResolutionDimension oppositeDimension = null;
    private int oppositeDimensionMultiplier = 1;
    float resolvedOffset;
    ResolutionAnchor resolvedTarget;
    ResolutionAnchor target;
    int type = 0;

    /* access modifiers changed from: 0000 */
    public String sType(int i) {
        return i == 1 ? "DIRECT" : i == 2 ? "CENTER" : i == 3 ? "MATCH" : i == 4 ? "CHAIN" : i == 5 ? "BARRIER" : "UNCONNECTED";
    }

    public ResolutionAnchor(ConstraintAnchor constraintAnchor) {
        this.myAnchor = constraintAnchor;
    }

    public String toString() {
        if (this.state == 1) {
            String str = ", RESOLVED: ";
            String str2 = "[";
            if (this.resolvedTarget == this) {
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append(this.myAnchor);
                sb.append(str);
                sb.append(this.resolvedOffset);
                sb.append("]  type: ");
                sb.append(sType(this.type));
                return sb.toString();
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str2);
            sb2.append(this.myAnchor);
            sb2.append(str);
            sb2.append(this.resolvedTarget);
            sb2.append(":");
            sb2.append(this.resolvedOffset);
            sb2.append("] type: ");
            sb2.append(sType(this.type));
            return sb2.toString();
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("{ ");
        sb3.append(this.myAnchor);
        sb3.append(" UNRESOLVED} type: ");
        sb3.append(sType(this.type));
        return sb3.toString();
    }

    public void resolve(ResolutionAnchor resolutionAnchor, float f) {
        if (this.state == 0 || !(this.resolvedTarget == resolutionAnchor || this.resolvedOffset == f)) {
            this.resolvedTarget = resolutionAnchor;
            this.resolvedOffset = f;
            if (this.state == 1) {
                invalidate();
            }
            didResolve();
        }
    }

    public void resolve() {
        float f;
        float f2;
        float f3;
        float f4;
        boolean z = true;
        if (this.state != 1 && this.type != 4) {
            ResolutionDimension resolutionDimension = this.dimension;
            if (resolutionDimension != null) {
                if (resolutionDimension.state == 1) {
                    this.offset = ((float) this.dimensionMultiplier) * resolutionDimension.value;
                } else {
                    return;
                }
            }
            ResolutionDimension resolutionDimension2 = this.oppositeDimension;
            if (resolutionDimension2 != null) {
                if (resolutionDimension2.state == 1) {
                    float f5 = resolutionDimension2.value;
                } else {
                    return;
                }
            }
            if (this.type == 1) {
                ResolutionAnchor resolutionAnchor = this.target;
                if (resolutionAnchor == null || resolutionAnchor.state == 1) {
                    ResolutionAnchor resolutionAnchor2 = this.target;
                    if (resolutionAnchor2 == null) {
                        this.resolvedTarget = this;
                        this.resolvedOffset = this.offset;
                    } else {
                        this.resolvedTarget = resolutionAnchor2.resolvedTarget;
                        this.resolvedOffset = resolutionAnchor2.resolvedOffset + this.offset;
                    }
                    didResolve();
                }
            }
            if (this.type == 2) {
                ResolutionAnchor resolutionAnchor3 = this.target;
                if (resolutionAnchor3 != null && resolutionAnchor3.state == 1) {
                    ResolutionAnchor resolutionAnchor4 = this.opposite;
                    if (resolutionAnchor4 != null) {
                        ResolutionAnchor resolutionAnchor5 = resolutionAnchor4.target;
                        if (resolutionAnchor5 != null && resolutionAnchor5.state == 1) {
                            if (LinearSystem.getMetrics() != null) {
                                Metrics metrics = LinearSystem.getMetrics();
                                metrics.centerConnectionResolved++;
                            }
                            this.resolvedTarget = this.target.resolvedTarget;
                            ResolutionAnchor resolutionAnchor6 = this.opposite;
                            resolutionAnchor6.resolvedTarget = resolutionAnchor6.target.resolvedTarget;
                            Type type2 = this.myAnchor.mType;
                            int i = 0;
                            if (!(type2 == Type.RIGHT || type2 == Type.BOTTOM)) {
                                z = false;
                            }
                            if (z) {
                                f2 = this.target.resolvedOffset;
                                f = this.opposite.target.resolvedOffset;
                            } else {
                                f2 = this.opposite.target.resolvedOffset;
                                f = this.target.resolvedOffset;
                            }
                            float f6 = f2 - f;
                            ConstraintAnchor constraintAnchor = this.myAnchor;
                            Type type3 = constraintAnchor.mType;
                            if (type3 == Type.LEFT || type3 == Type.RIGHT) {
                                f4 = f6 - ((float) this.myAnchor.mOwner.getWidth());
                                f3 = this.myAnchor.mOwner.mHorizontalBiasPercent;
                            } else {
                                f4 = f6 - ((float) constraintAnchor.mOwner.getHeight());
                                f3 = this.myAnchor.mOwner.mVerticalBiasPercent;
                            }
                            int margin = this.myAnchor.getMargin();
                            int margin2 = this.opposite.myAnchor.getMargin();
                            if (this.myAnchor.getTarget() == this.opposite.myAnchor.getTarget()) {
                                f3 = 0.5f;
                                margin2 = 0;
                            } else {
                                i = margin;
                            }
                            float f7 = (float) i;
                            float f8 = (float) margin2;
                            float f9 = (f4 - f7) - f8;
                            if (z) {
                                ResolutionAnchor resolutionAnchor7 = this.opposite;
                                resolutionAnchor7.resolvedOffset = resolutionAnchor7.target.resolvedOffset + f8 + (f9 * f3);
                                this.resolvedOffset = (this.target.resolvedOffset - f7) - (f9 * (1.0f - f3));
                            } else {
                                this.resolvedOffset = this.target.resolvedOffset + f7 + (f9 * f3);
                                ResolutionAnchor resolutionAnchor8 = this.opposite;
                                resolutionAnchor8.resolvedOffset = (resolutionAnchor8.target.resolvedOffset - f8) - (f9 * (1.0f - f3));
                            }
                            didResolve();
                            this.opposite.didResolve();
                        }
                    }
                }
            }
            if (this.type == 3) {
                ResolutionAnchor resolutionAnchor9 = this.target;
                if (resolutionAnchor9 != null && resolutionAnchor9.state == 1) {
                    ResolutionAnchor resolutionAnchor10 = this.opposite;
                    if (resolutionAnchor10 != null) {
                        ResolutionAnchor resolutionAnchor11 = resolutionAnchor10.target;
                        if (resolutionAnchor11 != null && resolutionAnchor11.state == 1) {
                            if (LinearSystem.getMetrics() != null) {
                                Metrics metrics2 = LinearSystem.getMetrics();
                                metrics2.matchConnectionResolved++;
                            }
                            ResolutionAnchor resolutionAnchor12 = this.target;
                            this.resolvedTarget = resolutionAnchor12.resolvedTarget;
                            ResolutionAnchor resolutionAnchor13 = this.opposite;
                            ResolutionAnchor resolutionAnchor14 = resolutionAnchor13.target;
                            resolutionAnchor13.resolvedTarget = resolutionAnchor14.resolvedTarget;
                            this.resolvedOffset = resolutionAnchor12.resolvedOffset + this.offset;
                            resolutionAnchor13.resolvedOffset = resolutionAnchor14.resolvedOffset + resolutionAnchor13.offset;
                            didResolve();
                            this.opposite.didResolve();
                        }
                    }
                }
            }
            if (this.type == 5) {
                this.myAnchor.mOwner.resolve();
            }
        }
    }

    public void setType(int i) {
        this.type = i;
    }

    public void reset() {
        super.reset();
        this.target = null;
        this.offset = 0.0f;
        this.dimension = null;
        this.dimensionMultiplier = 1;
        this.oppositeDimension = null;
        this.oppositeDimensionMultiplier = 1;
        this.resolvedTarget = null;
        this.resolvedOffset = 0.0f;
        this.opposite = null;
        this.type = 0;
    }

    public void update() {
        ConstraintAnchor target2 = this.myAnchor.getTarget();
        if (target2 != null) {
            if (target2.getTarget() == this.myAnchor) {
                this.type = 4;
                target2.getResolutionNode().type = 4;
            }
            int margin = this.myAnchor.getMargin();
            Type type2 = this.myAnchor.mType;
            if (type2 == Type.RIGHT || type2 == Type.BOTTOM) {
                margin = -margin;
            }
            dependsOn(target2.getResolutionNode(), margin);
        }
    }

    public void dependsOn(int i, ResolutionAnchor resolutionAnchor, int i2) {
        this.type = i;
        this.target = resolutionAnchor;
        this.offset = (float) i2;
        resolutionAnchor.addDependent(this);
    }

    public void dependsOn(ResolutionAnchor resolutionAnchor, int i) {
        this.target = resolutionAnchor;
        this.offset = (float) i;
        resolutionAnchor.addDependent(this);
    }

    public void dependsOn(ResolutionAnchor resolutionAnchor, int i, ResolutionDimension resolutionDimension) {
        this.target = resolutionAnchor;
        resolutionAnchor.addDependent(this);
        this.dimension = resolutionDimension;
        this.dimensionMultiplier = i;
        resolutionDimension.addDependent(this);
    }

    public void setOpposite(ResolutionAnchor resolutionAnchor, float f) {
        this.opposite = resolutionAnchor;
    }

    public void setOpposite(ResolutionAnchor resolutionAnchor, int i, ResolutionDimension resolutionDimension) {
        this.opposite = resolutionAnchor;
        this.oppositeDimension = resolutionDimension;
        this.oppositeDimensionMultiplier = i;
    }

    /* access modifiers changed from: 0000 */
    public void addResolvedValue(LinearSystem linearSystem) {
        SolverVariable solverVariable = this.myAnchor.getSolverVariable();
        ResolutionAnchor resolutionAnchor = this.resolvedTarget;
        if (resolutionAnchor == null) {
            linearSystem.addEquality(solverVariable, (int) this.resolvedOffset);
        } else {
            linearSystem.addEquality(solverVariable, linearSystem.createObjectVariable(resolutionAnchor.myAnchor), (int) this.resolvedOffset, 6);
        }
    }

    public float getResolvedValue() {
        return this.resolvedOffset;
    }
}
