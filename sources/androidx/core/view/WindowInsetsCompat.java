package androidx.core.view;

import android.os.Build.VERSION;
import android.view.WindowInsets;
import androidx.core.graphics.Insets;
import androidx.core.util.ObjectsCompat;
import java.util.Objects;

public class WindowInsetsCompat {
    private final Object mInsets;
    private Insets mSystemGestureInsets;
    private Insets mSystemWindowInsets;

    WindowInsetsCompat(Object obj) {
        this.mInsets = obj;
    }

    public int getSystemWindowInsetLeft() {
        if (VERSION.SDK_INT >= 20) {
            return ((WindowInsets) this.mInsets).getSystemWindowInsetLeft();
        }
        return 0;
    }

    public int getSystemWindowInsetTop() {
        if (VERSION.SDK_INT >= 20) {
            return ((WindowInsets) this.mInsets).getSystemWindowInsetTop();
        }
        return 0;
    }

    public int getSystemWindowInsetRight() {
        if (VERSION.SDK_INT >= 20) {
            return ((WindowInsets) this.mInsets).getSystemWindowInsetRight();
        }
        return 0;
    }

    public int getSystemWindowInsetBottom() {
        if (VERSION.SDK_INT >= 20) {
            return ((WindowInsets) this.mInsets).getSystemWindowInsetBottom();
        }
        return 0;
    }

    public boolean isConsumed() {
        if (VERSION.SDK_INT >= 21) {
            return ((WindowInsets) this.mInsets).isConsumed();
        }
        return false;
    }

    @Deprecated
    public WindowInsetsCompat replaceSystemWindowInsets(int i, int i2, int i3, int i4) {
        if (VERSION.SDK_INT >= 20) {
            return new WindowInsetsCompat(((WindowInsets) this.mInsets).replaceSystemWindowInsets(i, i2, i3, i4));
        }
        return null;
    }

    public Insets getSystemWindowInsets() {
        if (this.mSystemWindowInsets == null) {
            if (VERSION.SDK_INT >= 29) {
                this.mSystemWindowInsets = Insets.toCompatInsets(((WindowInsets) this.mInsets).getSystemWindowInsets());
            } else {
                this.mSystemWindowInsets = Insets.m2of(getSystemWindowInsetLeft(), getSystemWindowInsetTop(), getSystemWindowInsetRight(), getSystemWindowInsetBottom());
            }
        }
        return this.mSystemWindowInsets;
    }

    public Insets getSystemGestureInsets() {
        if (this.mSystemGestureInsets == null) {
            if (VERSION.SDK_INT >= 29) {
                this.mSystemGestureInsets = Insets.toCompatInsets(((WindowInsets) this.mInsets).getSystemGestureInsets());
            } else {
                this.mSystemGestureInsets = getSystemWindowInsets();
            }
        }
        return this.mSystemGestureInsets;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WindowInsetsCompat)) {
            return false;
        }
        return ObjectsCompat.equals(this.mInsets, ((WindowInsetsCompat) obj).mInsets);
    }

    public int hashCode() {
        Object obj = this.mInsets;
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }

    public WindowInsets toWindowInsets() {
        return (WindowInsets) this.mInsets;
    }

    public static WindowInsetsCompat toWindowInsetsCompat(WindowInsets windowInsets) {
        Objects.requireNonNull(windowInsets);
        return new WindowInsetsCompat(windowInsets);
    }
}
