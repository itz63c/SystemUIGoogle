package kotlin;

import java.io.Serializable;

/* compiled from: Tuples.kt */
public final class Pair<A, B> implements Serializable {
    private final A first;
    private final B second;

    public final A component1() {
        return this.first;
    }

    public final B component2() {
        return this.second;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001a, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2.second, (java.lang.Object) r3.second) != false) goto L_0x001f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x001f
            boolean r0 = r3 instanceof kotlin.Pair
            if (r0 == 0) goto L_0x001d
            kotlin.Pair r3 = (kotlin.Pair) r3
            A r0 = r2.first
            A r1 = r3.first
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x001d
            B r2 = r2.second
            B r3 = r3.second
            boolean r2 = kotlin.jvm.internal.Intrinsics.areEqual(r2, r3)
            if (r2 == 0) goto L_0x001d
            goto L_0x001f
        L_0x001d:
            r2 = 0
            return r2
        L_0x001f:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.Pair.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        A a = this.first;
        int i = 0;
        int hashCode = (a != null ? a.hashCode() : 0) * 31;
        B b = this.second;
        if (b != null) {
            i = b.hashCode();
        }
        return hashCode + i;
    }

    public Pair(A a, B b) {
        this.first = a;
        this.second = b;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append(this.first);
        sb.append(", ");
        sb.append(this.second);
        sb.append(')');
        return sb.toString();
    }
}
