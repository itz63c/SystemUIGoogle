package androidx.leanback.widget;

import android.text.TextUtils;

public class GuidedActionDiffCallback extends DiffCallback<GuidedAction> {
    static final GuidedActionDiffCallback sInstance = new GuidedActionDiffCallback();

    public static GuidedActionDiffCallback getInstance() {
        return sInstance;
    }

    public boolean areItemsTheSame(GuidedAction guidedAction, GuidedAction guidedAction2) {
        boolean z = true;
        if (guidedAction == null) {
            if (guidedAction2 != null) {
                z = false;
            }
            return z;
        } else if (guidedAction2 == null) {
            return false;
        } else {
            if (guidedAction.getId() != guidedAction2.getId()) {
                z = false;
            }
            return z;
        }
    }

    public boolean areContentsTheSame(GuidedAction guidedAction, GuidedAction guidedAction2) {
        boolean z = true;
        if (guidedAction == null) {
            if (guidedAction2 != null) {
                z = false;
            }
            return z;
        } else if (guidedAction2 == null) {
            return false;
        } else {
            if (!(guidedAction.getCheckSetId() == guidedAction2.getCheckSetId() && guidedAction.mActionFlags == guidedAction2.mActionFlags && TextUtils.equals(guidedAction.getTitle(), guidedAction2.getTitle()) && TextUtils.equals(guidedAction.getDescription(), guidedAction2.getDescription()) && guidedAction.getInputType() == guidedAction2.getInputType() && TextUtils.equals(guidedAction.getEditTitle(), guidedAction2.getEditTitle()) && TextUtils.equals(guidedAction.getEditDescription(), guidedAction2.getEditDescription()) && guidedAction.getEditInputType() == guidedAction2.getEditInputType() && guidedAction.getDescriptionEditInputType() == guidedAction2.getDescriptionEditInputType())) {
                z = false;
            }
            return z;
        }
    }
}
