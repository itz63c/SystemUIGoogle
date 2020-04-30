package kotlin.text;

import kotlin.jvm.internal.Intrinsics;

/* compiled from: StringsJVM.kt */
class StringsKt__StringsJVMKt extends StringsKt__StringNumberConversionsKt {
    public static boolean equals(String str, String str2, boolean z) {
        boolean z2;
        if (str == null) {
            return str2 == null;
        }
        if (!z) {
            z2 = str.equals(str2);
        } else {
            z2 = str.equalsIgnoreCase(str2);
        }
        return z2;
    }

    public static /* synthetic */ boolean startsWith$default(String str, String str2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return startsWith(str, str2, z);
    }

    public static final boolean startsWith(String str, String str2, boolean z) {
        Intrinsics.checkParameterIsNotNull(str, "$this$startsWith");
        Intrinsics.checkParameterIsNotNull(str2, "prefix");
        if (!z) {
            return str.startsWith(str2);
        }
        return regionMatches(str, 0, str2, 0, str2.length(), z);
    }

    public static /* synthetic */ boolean endsWith$default(String str, String str2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return endsWith(str, str2, z);
    }

    public static final boolean endsWith(String str, String str2, boolean z) {
        Intrinsics.checkParameterIsNotNull(str, "$this$endsWith");
        Intrinsics.checkParameterIsNotNull(str2, "suffix");
        if (!z) {
            return str.endsWith(str2);
        }
        return regionMatches(str, str.length() - str2.length(), str2, 0, str2.length(), true);
    }

    public static final boolean regionMatches(String str, int i, String str2, int i2, int i3, boolean z) {
        Intrinsics.checkParameterIsNotNull(str, "$this$regionMatches");
        Intrinsics.checkParameterIsNotNull(str2, "other");
        if (!z) {
            return str.regionMatches(i, str2, i2, i3);
        }
        return str.regionMatches(z, i, str2, i2, i3);
    }
}
