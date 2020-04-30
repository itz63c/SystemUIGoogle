package androidx.core.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;

public interface TintableCompoundDrawablesView {
    void setSupportCompoundDrawablesTintList(ColorStateList colorStateList);

    void setSupportCompoundDrawablesTintMode(Mode mode);
}
