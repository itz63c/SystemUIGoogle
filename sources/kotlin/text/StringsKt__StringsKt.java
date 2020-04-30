package kotlin.text;

import kotlin.jvm.internal.Intrinsics;

/* compiled from: Strings.kt */
class StringsKt__StringsKt extends StringsKt__StringsJVMKt {
    public static CharSequence trim(CharSequence charSequence) {
        Intrinsics.checkParameterIsNotNull(charSequence, "$this$trim");
        int length = charSequence.length() - 1;
        int i = 0;
        boolean z = false;
        while (i <= length) {
            boolean isWhitespace = CharsKt__CharJVMKt.isWhitespace(charSequence.charAt(!z ? i : length));
            if (!z) {
                if (!isWhitespace) {
                    z = true;
                } else {
                    i++;
                }
            } else if (!isWhitespace) {
                break;
            } else {
                length--;
            }
        }
        return charSequence.subSequence(i, length + 1);
    }

    public static final int getLastIndex(CharSequence charSequence) {
        Intrinsics.checkParameterIsNotNull(charSequence, "$this$lastIndex");
        return charSequence.length() - 1;
    }

    public static final int indexOfAny(CharSequence charSequence, char[] cArr, int i, boolean z) {
        boolean z2;
        Intrinsics.checkParameterIsNotNull(charSequence, "$this$indexOfAny");
        Intrinsics.checkParameterIsNotNull(cArr, "chars");
        if (z || cArr.length != 1 || !(charSequence instanceof String)) {
            int coerceAtLeast = RangesKt___RangesKt.coerceAtLeast(i, 0);
            int lastIndex = getLastIndex(charSequence);
            if (coerceAtLeast <= lastIndex) {
                while (true) {
                    char charAt = charSequence.charAt(coerceAtLeast);
                    int length = cArr.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length) {
                            z2 = false;
                            break;
                        } else if (CharsKt__CharKt.equals(cArr[i2], charAt, z)) {
                            z2 = true;
                            break;
                        } else {
                            i2++;
                        }
                    }
                    if (!z2) {
                        if (coerceAtLeast == lastIndex) {
                            break;
                        }
                        coerceAtLeast++;
                    } else {
                        return coerceAtLeast;
                    }
                }
            }
            return -1;
        }
        return ((String) charSequence).indexOf(ArraysKt___ArraysKt.single(cArr), i);
    }

    public static /* synthetic */ int indexOf$default(CharSequence charSequence, char c, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return indexOf(charSequence, c, i, z);
    }

    public static final int indexOf(CharSequence charSequence, char c, int i, boolean z) {
        Intrinsics.checkParameterIsNotNull(charSequence, "$this$indexOf");
        if (!z && (charSequence instanceof String)) {
            return ((String) charSequence).indexOf(c, i);
        }
        return indexOfAny(charSequence, new char[]{c}, i, z);
    }
}
