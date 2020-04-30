package androidx.constraintlayout.widget;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

public class Group extends ConstraintHelper {
    public Group(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void init(AttributeSet attributeSet) {
        super.init(attributeSet);
        this.mUseViewMeasure = false;
    }

    public void updatePreLayout(ConstraintLayout constraintLayout) {
        int i = VERSION.SDK_INT;
        int visibility = getVisibility();
        float elevation = i >= 21 ? getElevation() : 0.0f;
        for (int i2 = 0; i2 < this.mCount; i2++) {
            View viewById = constraintLayout.getViewById(this.mIds[i2]);
            if (viewById != null) {
                viewById.setVisibility(visibility);
                if (elevation > 0.0f && i >= 21) {
                    viewById.setElevation(elevation);
                }
            }
        }
    }

    public void updatePostLayout(ConstraintLayout constraintLayout) {
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.widget.setWidth(0);
        layoutParams.widget.setHeight(0);
    }
}
