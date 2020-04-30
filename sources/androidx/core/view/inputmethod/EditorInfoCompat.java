package androidx.core.view.inputmethod;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;

public final class EditorInfoCompat {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static void setContentMimeTypes(EditorInfo editorInfo, String[] strArr) {
        if (VERSION.SDK_INT >= 25) {
            editorInfo.contentMimeTypes = strArr;
            return;
        }
        if (editorInfo.extras == null) {
            editorInfo.extras = new Bundle();
        }
        editorInfo.extras.putStringArray("androidx.core.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES", strArr);
        editorInfo.extras.putStringArray("android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES", strArr);
    }

    public static String[] getContentMimeTypes(EditorInfo editorInfo) {
        String[] strArr = EMPTY_STRING_ARRAY;
        if (VERSION.SDK_INT >= 25) {
            String[] strArr2 = editorInfo.contentMimeTypes;
            if (strArr2 != null) {
                strArr = strArr2;
            }
            return strArr;
        }
        Bundle bundle = editorInfo.extras;
        if (bundle == null) {
            return strArr;
        }
        String[] stringArray = bundle.getStringArray("androidx.core.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES");
        if (stringArray == null) {
            stringArray = editorInfo.extras.getStringArray("android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES");
        }
        if (stringArray != null) {
            strArr = stringArray;
        }
        return strArr;
    }
}
