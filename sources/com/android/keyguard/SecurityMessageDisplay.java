package com.android.keyguard;

import android.content.res.ColorStateList;

public interface SecurityMessageDisplay {
    void setMessage(int i);

    void setMessage(CharSequence charSequence);

    void setNextMessageColor(ColorStateList colorStateList);
}
