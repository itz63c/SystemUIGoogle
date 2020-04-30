package kotlin.ranges;

/* compiled from: Ranges.kt */
public final class IntRange extends IntProgression {
    public static final Companion Companion = new Companion(null);
    /* access modifiers changed from: private */
    public static final IntRange EMPTY = new IntRange(1, 0);

    /* compiled from: Ranges.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final IntRange getEMPTY() {
            return IntRange.EMPTY;
        }
    }

    public IntRange(int i, int i2) {
        super(i, i2, 1);
    }

    public boolean isEmpty() {
        return getFirst() > getLast();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0027, code lost:
        if (getLast() == r3.getLast()) goto L_0x0029;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            boolean r0 = r3 instanceof kotlin.ranges.IntRange
            if (r0 == 0) goto L_0x002b
            boolean r0 = r2.isEmpty()
            if (r0 == 0) goto L_0x0013
            r0 = r3
            kotlin.ranges.IntRange r0 = (kotlin.ranges.IntRange) r0
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0029
        L_0x0013:
            int r0 = r2.getFirst()
            kotlin.ranges.IntRange r3 = (kotlin.ranges.IntRange) r3
            int r1 = r3.getFirst()
            if (r0 != r1) goto L_0x002b
            int r2 = r2.getLast()
            int r3 = r3.getLast()
            if (r2 != r3) goto L_0x002b
        L_0x0029:
            r2 = 1
            goto L_0x002c
        L_0x002b:
            r2 = 0
        L_0x002c:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.ranges.IntRange.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        if (isEmpty()) {
            return -1;
        }
        return getLast() + (getFirst() * 31);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFirst());
        sb.append("..");
        sb.append(getLast());
        return sb.toString();
    }
}
