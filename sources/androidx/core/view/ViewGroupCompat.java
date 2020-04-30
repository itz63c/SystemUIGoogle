package androidx.core.view;

import android.os.Build.VERSION;
import android.view.ViewGroup;
import androidx.core.R$id;

public final class ViewGroupCompat {
    public static boolean isTransitionGroup(ViewGroup viewGroup) {
        if (VERSION.SDK_INT >= 21) {
            return viewGroup.isTransitionGroup();
        }
        Boolean bool = (Boolean) viewGroup.getTag(R$id.tag_transition_group);
        return ((bool == null || !bool.booleanValue()) && viewGroup.getBackground() == null && ViewCompat.getTransitionName(viewGroup) == null) ? false : true;
    }
}
