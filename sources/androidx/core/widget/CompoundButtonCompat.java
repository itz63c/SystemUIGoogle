package androidx.core.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.Log;
import android.widget.CompoundButton;
import java.lang.reflect.Field;

public final class CompoundButtonCompat {
    private static Field sButtonDrawableField;
    private static boolean sButtonDrawableFieldFetched;

    public static void setButtonTintList(CompoundButton compoundButton, ColorStateList colorStateList) {
        if (VERSION.SDK_INT >= 21) {
            compoundButton.setButtonTintList(colorStateList);
        } else if (compoundButton instanceof TintableCompoundButton) {
            ((TintableCompoundButton) compoundButton).setSupportButtonTintList(colorStateList);
        }
    }

    public static void setButtonTintMode(CompoundButton compoundButton, Mode mode) {
        if (VERSION.SDK_INT >= 21) {
            compoundButton.setButtonTintMode(mode);
        } else if (compoundButton instanceof TintableCompoundButton) {
            ((TintableCompoundButton) compoundButton).setSupportButtonTintMode(mode);
        }
    }

    public static Drawable getButtonDrawable(CompoundButton compoundButton) {
        if (VERSION.SDK_INT >= 23) {
            return compoundButton.getButtonDrawable();
        }
        String str = "CompoundButtonCompat";
        if (!sButtonDrawableFieldFetched) {
            try {
                Field declaredField = CompoundButton.class.getDeclaredField("mButtonDrawable");
                sButtonDrawableField = declaredField;
                declaredField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                Log.i(str, "Failed to retrieve mButtonDrawable field", e);
            }
            sButtonDrawableFieldFetched = true;
        }
        Field field = sButtonDrawableField;
        if (field != null) {
            try {
                return (Drawable) field.get(compoundButton);
            } catch (IllegalAccessException e2) {
                Log.i(str, "Failed to get button drawable via reflection", e2);
                sButtonDrawableField = null;
            }
        }
        return null;
    }
}
