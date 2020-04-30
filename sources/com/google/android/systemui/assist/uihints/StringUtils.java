package com.google.android.systemui.assist.uihints;

import java.lang.reflect.Array;

final class StringUtils {

    public static final class StringStabilityInfo {
        final String stable;
        final String unstable;

        StringStabilityInfo(String str, String str2) {
            String str3 = "";
            if (str == null) {
                str = str3;
            }
            this.stable = str;
            if (str2 == null) {
                str2 = str3;
            }
            this.unstable = str2;
        }

        StringStabilityInfo(String str, int i) {
            if (i >= str.length()) {
                this.stable = str;
                this.unstable = "";
                return;
            }
            int i2 = i + 1;
            this.stable = str.substring(0, i2);
            this.unstable = str.substring(i2);
        }
    }

    public static StringStabilityInfo calculateStringStabilityInfo(String str, String str2) {
        if (isNullOrEmpty(str) || isNullOrEmpty(str2)) {
            return new StringStabilityInfo("", str2);
        }
        return getRightMostStabilityInfoLeaf(str2, 0, str.length(), 0, str2.length(), calculateLongestCommonSubstringMatrix(str.toLowerCase(), str2.toLowerCase()));
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    private static int[][] calculateLongestCommonSubstringMatrix(String str, String str2) {
        int length = str.length();
        int[] iArr = new int[2];
        iArr[1] = str2.length();
        iArr[0] = length;
        int[][] iArr2 = (int[][]) Array.newInstance(int.class, iArr);
        int i = 0;
        while (i < str.length()) {
            char charAt = str.charAt(i);
            int i2 = 0;
            while (i2 < str2.length()) {
                if (charAt == str2.charAt(i2)) {
                    iArr2[i][i2] = ((i == 0 || i2 == 0) ? 0 : iArr2[i - 1][i2 - 1]) + (charAt == ' ' ? 0 : 1);
                }
                i2++;
            }
            i++;
        }
        return iArr2;
    }

    private static StringStabilityInfo getRightMostStabilityInfoLeaf(String str, int i, int i2, int i3, int i4, int[][] iArr) {
        int i5 = -1;
        int i6 = 0;
        int i7 = -1;
        while (i < i2) {
            for (int i8 = i3; i8 < i4; i8++) {
                if (iArr[i][i8] > i6) {
                    i6 = iArr[i][i8];
                    i5 = i;
                    i7 = i8;
                }
            }
            i++;
        }
        if (i6 == 0) {
            return new StringStabilityInfo(str, i3 - 1);
        }
        int i9 = i5 + 1;
        if (i9 != i2) {
            int i10 = i7 + 1;
            if (i10 != i4) {
                return getRightMostStabilityInfoLeaf(str, i9, i2, i10, i4, iArr);
            }
        }
        return new StringStabilityInfo(str, i7);
    }
}
