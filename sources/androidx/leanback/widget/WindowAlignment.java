package androidx.leanback.widget;

class WindowAlignment {
    public final Axis horizontal;
    private Axis mMainAxis;
    private int mOrientation = 0;
    private Axis mSecondAxis;
    public final Axis vertical = new Axis("vertical");

    public static class Axis {
        private int mMaxEdge;
        private int mMaxScroll;
        private int mMinEdge;
        private int mMinScroll;
        private int mPaddingMax;
        private int mPaddingMin;
        private int mPreferredKeyLine = 2;
        private boolean mReversedFlow;
        private int mSize;
        private int mWindowAlignment = 3;
        private int mWindowAlignmentOffset = 0;
        private float mWindowAlignmentOffsetPercent = 50.0f;

        /* access modifiers changed from: 0000 */
        public final int calculateScrollToKeyLine(int i, int i2) {
            return i - i2;
        }

        public Axis(String str) {
            reset();
        }

        public final void setWindowAlignment(int i) {
            this.mWindowAlignment = i;
        }

        /* access modifiers changed from: 0000 */
        public final boolean isPreferKeylineOverHighEdge() {
            return (this.mPreferredKeyLine & 2) != 0;
        }

        /* access modifiers changed from: 0000 */
        public final boolean isPreferKeylineOverLowEdge() {
            return (this.mPreferredKeyLine & 1) != 0;
        }

        public final void setWindowAlignmentOffset(int i) {
            this.mWindowAlignmentOffset = i;
        }

        public final void setWindowAlignmentOffsetPercent(float f) {
            if ((f < 0.0f || f > 100.0f) && f != -1.0f) {
                throw new IllegalArgumentException();
            }
            this.mWindowAlignmentOffsetPercent = f;
        }

        public final int getMinScroll() {
            return this.mMinScroll;
        }

        public final void invalidateScrollMin() {
            this.mMinEdge = Integer.MIN_VALUE;
            this.mMinScroll = Integer.MIN_VALUE;
        }

        public final int getMaxScroll() {
            return this.mMaxScroll;
        }

        public final void invalidateScrollMax() {
            this.mMaxEdge = Integer.MAX_VALUE;
            this.mMaxScroll = Integer.MAX_VALUE;
        }

        /* access modifiers changed from: 0000 */
        public void reset() {
            this.mMinEdge = Integer.MIN_VALUE;
            this.mMaxEdge = Integer.MAX_VALUE;
        }

        public final boolean isMinUnknown() {
            return this.mMinEdge == Integer.MIN_VALUE;
        }

        public final boolean isMaxUnknown() {
            return this.mMaxEdge == Integer.MAX_VALUE;
        }

        public final void setSize(int i) {
            this.mSize = i;
        }

        public final int getSize() {
            return this.mSize;
        }

        public final void setPadding(int i, int i2) {
            this.mPaddingMin = i;
            this.mPaddingMax = i2;
        }

        public final int getPaddingMin() {
            return this.mPaddingMin;
        }

        public final int getPaddingMax() {
            return this.mPaddingMax;
        }

        public final int getClientSize() {
            return (this.mSize - this.mPaddingMin) - this.mPaddingMax;
        }

        /* access modifiers changed from: 0000 */
        public final int calculateKeyline() {
            if (!this.mReversedFlow) {
                int i = this.mWindowAlignmentOffset;
                if (i < 0) {
                    i += this.mSize;
                }
                float f = this.mWindowAlignmentOffsetPercent;
                if (f != -1.0f) {
                    return i + ((int) ((((float) this.mSize) * f) / 100.0f));
                }
                return i;
            }
            int i2 = this.mWindowAlignmentOffset;
            int i3 = i2 >= 0 ? this.mSize - i2 : -i2;
            float f2 = this.mWindowAlignmentOffsetPercent;
            return f2 != -1.0f ? i3 - ((int) ((((float) this.mSize) * f2) / 100.0f)) : i3;
        }

        public final void updateMinMax(int i, int i2, int i3, int i4) {
            this.mMinEdge = i;
            this.mMaxEdge = i2;
            int clientSize = getClientSize();
            int calculateKeyline = calculateKeyline();
            boolean isMinUnknown = isMinUnknown();
            boolean isMaxUnknown = isMaxUnknown();
            if (!isMinUnknown) {
                if (!this.mReversedFlow) {
                    this.mMinScroll = calculateScrollToKeyLine(i3, calculateKeyline);
                } else {
                    this.mMinScroll = calculateScrollToKeyLine(i3, calculateKeyline);
                }
                this.mMinScroll = this.mMinEdge - this.mPaddingMin;
            }
            if (!isMaxUnknown) {
                if (!this.mReversedFlow) {
                    this.mMaxScroll = calculateScrollToKeyLine(i4, calculateKeyline);
                } else {
                    this.mMaxScroll = calculateScrollToKeyLine(i4, calculateKeyline);
                }
                this.mMaxScroll = (this.mMaxEdge - this.mPaddingMin) - clientSize;
            }
            if (!isMaxUnknown && !isMinUnknown) {
                if (!this.mReversedFlow) {
                    int i5 = this.mWindowAlignment;
                    if ((i5 & 1) != 0) {
                        if (isPreferKeylineOverLowEdge()) {
                            this.mMinScroll = Math.min(this.mMinScroll, calculateScrollToKeyLine(i4, calculateKeyline));
                        }
                        this.mMaxScroll = Math.max(this.mMinScroll, this.mMaxScroll);
                    } else if ((i5 & 2) != 0) {
                        if (isPreferKeylineOverHighEdge()) {
                            this.mMaxScroll = Math.max(this.mMaxScroll, calculateScrollToKeyLine(i3, calculateKeyline));
                        }
                        this.mMinScroll = Math.min(this.mMinScroll, this.mMaxScroll);
                    }
                } else {
                    int i6 = this.mWindowAlignment;
                    if ((i6 & 1) != 0) {
                        if (isPreferKeylineOverLowEdge()) {
                            this.mMaxScroll = Math.max(this.mMaxScroll, calculateScrollToKeyLine(i3, calculateKeyline));
                        }
                        this.mMinScroll = Math.min(this.mMinScroll, this.mMaxScroll);
                    } else if ((i6 & 2) != 0) {
                        if (isPreferKeylineOverHighEdge()) {
                            this.mMinScroll = Math.min(this.mMinScroll, calculateScrollToKeyLine(i4, calculateKeyline));
                        }
                        this.mMaxScroll = Math.max(this.mMinScroll, this.mMaxScroll);
                    }
                }
            }
        }

        public final int getScroll(int i) {
            int size = getSize();
            int calculateKeyline = calculateKeyline();
            boolean isMinUnknown = isMinUnknown();
            boolean isMaxUnknown = isMaxUnknown();
            if (!isMinUnknown) {
                int i2 = calculateKeyline - this.mPaddingMin;
                if (this.mReversedFlow ? (this.mWindowAlignment & 2) != 0 : (this.mWindowAlignment & 1) != 0) {
                    int i3 = this.mMinEdge;
                    if (i - i3 <= i2) {
                        int i4 = i3 - this.mPaddingMin;
                        if (!isMaxUnknown) {
                            int i5 = this.mMaxScroll;
                            if (i4 > i5) {
                                i4 = i5;
                            }
                        }
                        return i4;
                    }
                }
            }
            if (!isMaxUnknown) {
                int i6 = (size - calculateKeyline) - this.mPaddingMax;
                if (this.mReversedFlow ? (this.mWindowAlignment & 1) != 0 : (this.mWindowAlignment & 2) != 0) {
                    int i7 = this.mMaxEdge;
                    if (i7 - i <= i6) {
                        int i8 = i7 - (size - this.mPaddingMax);
                        if (!isMinUnknown) {
                            int i9 = this.mMinScroll;
                            if (i8 < i9) {
                                i8 = i9;
                            }
                        }
                        return i8;
                    }
                }
            }
            return calculateScrollToKeyLine(i, calculateKeyline);
        }

        public final void setReversedFlow(boolean z) {
            this.mReversedFlow = z;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(" min:");
            sb.append(this.mMinEdge);
            String str = " ";
            sb.append(str);
            sb.append(this.mMinScroll);
            sb.append(" max:");
            sb.append(this.mMaxEdge);
            sb.append(str);
            sb.append(this.mMaxScroll);
            return sb.toString();
        }
    }

    WindowAlignment() {
        Axis axis = new Axis("horizontal");
        this.horizontal = axis;
        this.mMainAxis = axis;
        this.mSecondAxis = this.vertical;
    }

    public final Axis mainAxis() {
        return this.mMainAxis;
    }

    public final Axis secondAxis() {
        return this.mSecondAxis;
    }

    public final void setOrientation(int i) {
        this.mOrientation = i;
        if (i == 0) {
            this.mMainAxis = this.horizontal;
            this.mSecondAxis = this.vertical;
            return;
        }
        this.mMainAxis = this.vertical;
        this.mSecondAxis = this.horizontal;
    }

    public final void reset() {
        mainAxis().reset();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("horizontal=");
        sb.append(this.horizontal);
        sb.append("; vertical=");
        sb.append(this.vertical);
        return sb.toString();
    }
}
