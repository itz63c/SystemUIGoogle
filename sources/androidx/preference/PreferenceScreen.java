package androidx.preference;

import android.content.Context;
import android.util.AttributeSet;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.PreferenceManager.OnNavigateToScreenListener;

public final class PreferenceScreen extends PreferenceGroup {
    private boolean mShouldUseGeneratedIds = true;

    /* access modifiers changed from: protected */
    public boolean isOnSameScreenAsChildren() {
        return false;
    }

    public PreferenceScreen(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.preferenceScreenStyle, 16842891));
    }

    /* access modifiers changed from: protected */
    public void onClick() {
        if (getIntent() == null && getFragment() == null && getPreferenceCount() != 0) {
            OnNavigateToScreenListener onNavigateToScreenListener = getPreferenceManager().getOnNavigateToScreenListener();
            if (onNavigateToScreenListener != null) {
                onNavigateToScreenListener.onNavigateToScreen(this);
            }
        }
    }

    public boolean shouldUseGeneratedIds() {
        return this.mShouldUseGeneratedIds;
    }
}
