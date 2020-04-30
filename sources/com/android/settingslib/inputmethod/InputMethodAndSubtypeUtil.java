package com.android.settingslib.inputmethod;

import android.text.TextUtils.SimpleStringSplitter;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;

public class InputMethodAndSubtypeUtil {
    static {
        new SimpleStringSplitter(':');
        new SimpleStringSplitter(';');
    }

    public static boolean isValidNonAuxAsciiCapableIme(InputMethodInfo inputMethodInfo) {
        if (inputMethodInfo.isAuxiliaryIme()) {
            return false;
        }
        int subtypeCount = inputMethodInfo.getSubtypeCount();
        for (int i = 0; i < subtypeCount; i++) {
            InputMethodSubtype subtypeAt = inputMethodInfo.getSubtypeAt(i);
            if ("keyboard".equalsIgnoreCase(subtypeAt.getMode()) && subtypeAt.isAsciiCapable()) {
                return true;
            }
        }
        return false;
    }
}
