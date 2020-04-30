package com.google.common.base;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Strings {
    static boolean validSurrogatePairAt(CharSequence charSequence, int i) {
        if (i < 0 || i > charSequence.length() - 2 || !Character.isHighSurrogate(charSequence.charAt(i)) || !Character.isLowSurrogate(charSequence.charAt(i + 1))) {
            return false;
        }
        return true;
    }

    public static String lenientFormat(String str, Object... objArr) {
        String valueOf = String.valueOf(str);
        int i = 0;
        if (objArr == null) {
            objArr = new Object[]{"(Object[])null"};
        } else {
            for (int i2 = 0; i2 < objArr.length; i2++) {
                objArr[i2] = lenientToString(objArr[i2]);
            }
        }
        StringBuilder sb = new StringBuilder(valueOf.length() + (objArr.length * 16));
        int i3 = 0;
        while (i < objArr.length) {
            int indexOf = valueOf.indexOf("%s", i3);
            if (indexOf == -1) {
                break;
            }
            sb.append(valueOf, i3, indexOf);
            int i4 = i + 1;
            sb.append(objArr[i]);
            int i5 = i4;
            i3 = indexOf + 2;
            i = i5;
        }
        sb.append(valueOf, i3, valueOf.length());
        if (i < objArr.length) {
            sb.append(" [");
            int i6 = i + 1;
            sb.append(objArr[i]);
            while (i6 < objArr.length) {
                sb.append(", ");
                int i7 = i6 + 1;
                sb.append(objArr[i6]);
                i6 = i7;
            }
            sb.append(']');
        }
        return sb.toString();
    }

    private static String lenientToString(Object obj) {
        try {
            return String.valueOf(obj);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append(obj.getClass().getName());
            sb.append('@');
            sb.append(Integer.toHexString(System.identityHashCode(obj)));
            String sb2 = sb.toString();
            Logger logger = Logger.getLogger("com.google.common.base.Strings");
            Level level = Level.WARNING;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Exception during lenientFormat for ");
            sb3.append(sb2);
            logger.log(level, sb3.toString(), e);
            StringBuilder sb4 = new StringBuilder();
            sb4.append("<");
            sb4.append(sb2);
            sb4.append(" threw ");
            sb4.append(e.getClass().getName());
            sb4.append(">");
            return sb4.toString();
        }
    }
}
