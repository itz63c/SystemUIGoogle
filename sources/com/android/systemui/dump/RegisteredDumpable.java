package com.android.systemui.dump;

import kotlin.jvm.internal.Intrinsics;

/* compiled from: DumpManager.kt */
final class RegisteredDumpable<T> {
    private final T dumpable;
    private final String name;

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001a, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2.dumpable, (java.lang.Object) r3.dumpable) != false) goto L_0x001f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x001f
            boolean r0 = r3 instanceof com.android.systemui.dump.RegisteredDumpable
            if (r0 == 0) goto L_0x001d
            com.android.systemui.dump.RegisteredDumpable r3 = (com.android.systemui.dump.RegisteredDumpable) r3
            java.lang.String r0 = r2.name
            java.lang.String r1 = r3.name
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x001d
            T r2 = r2.dumpable
            T r3 = r3.dumpable
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.dump.RegisteredDumpable.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        String str = this.name;
        int i = 0;
        int hashCode = (str != null ? str.hashCode() : 0) * 31;
        T t = this.dumpable;
        if (t != null) {
            i = t.hashCode();
        }
        return hashCode + i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RegisteredDumpable(name=");
        sb.append(this.name);
        sb.append(", dumpable=");
        sb.append(this.dumpable);
        sb.append(")");
        return sb.toString();
    }

    public RegisteredDumpable(String str, T t) {
        Intrinsics.checkParameterIsNotNull(str, "name");
        this.name = str;
        this.dumpable = t;
    }

    public final String getName() {
        return this.name;
    }

    public final T getDumpable() {
        return this.dumpable;
    }
}
