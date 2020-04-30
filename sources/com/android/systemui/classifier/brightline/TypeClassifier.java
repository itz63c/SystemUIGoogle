package com.android.systemui.classifier.brightline;

public class TypeClassifier extends FalsingClassifier {
    TypeClassifier(FalsingDataProvider falsingDataProvider) {
        super(falsingDataProvider);
    }

    public boolean isFalseTouch() {
        boolean isVertical = isVertical();
        boolean isUp = isUp();
        boolean isRight = isRight();
        int interactionType = getInteractionType();
        boolean z = false;
        if (interactionType != 0) {
            if (interactionType == 1) {
                return isVertical;
            }
            if (interactionType != 2) {
                if (interactionType != 4) {
                    if (interactionType == 5) {
                        if (!isRight || !isUp) {
                            z = true;
                        }
                        return z;
                    } else if (interactionType == 6) {
                        if (isRight || !isUp) {
                            z = true;
                        }
                        return z;
                    } else if (interactionType != 8) {
                        if (interactionType != 9) {
                            return true;
                        }
                    }
                }
                if (!isVertical || !isUp) {
                    z = true;
                }
                return z;
            }
        }
        if (!isVertical || isUp) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public String getReason() {
        return String.format("{vertical=%s, up=%s, right=%s}", new Object[]{Boolean.valueOf(isVertical()), Boolean.valueOf(isUp()), Boolean.valueOf(isRight())});
    }
}
