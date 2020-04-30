package androidx.leanback.widget;

import android.os.Bundle;
import java.util.List;

public class GuidedAction extends Action {
    int mActionFlags;
    String[] mAutofillHints;
    int mCheckSetId;
    int mDescriptionEditInputType;
    int mDescriptionInputType;
    private CharSequence mEditDescription;
    int mEditInputType;
    private CharSequence mEditTitle;
    int mEditable;
    int mInputType;
    List<GuidedAction> mSubActions;

    static boolean isPasswordVariant(int i) {
        int i2 = i & 4080;
        return i2 == 128 || i2 == 144 || i2 == 224;
    }

    protected GuidedAction() {
        super(0);
    }

    private void setFlags(int i, int i2) {
        this.mActionFlags = (i & i2) | (this.mActionFlags & (~i2));
    }

    public CharSequence getTitle() {
        return getLabel1();
    }

    public void setTitle(CharSequence charSequence) {
        setLabel1(charSequence);
    }

    public CharSequence getEditTitle() {
        return this.mEditTitle;
    }

    public void setEditTitle(CharSequence charSequence) {
        this.mEditTitle = charSequence;
    }

    public CharSequence getEditDescription() {
        return this.mEditDescription;
    }

    public void setEditDescription(CharSequence charSequence) {
        this.mEditDescription = charSequence;
    }

    public CharSequence getDescription() {
        return getLabel2();
    }

    public void setDescription(CharSequence charSequence) {
        setLabel2(charSequence);
    }

    public boolean isEditable() {
        return this.mEditable == 1;
    }

    public boolean isDescriptionEditable() {
        return this.mEditable == 2;
    }

    public boolean hasTextEditable() {
        int i = this.mEditable;
        return i == 1 || i == 2;
    }

    public boolean hasEditableActivatorView() {
        return this.mEditable == 3;
    }

    public int getEditInputType() {
        return this.mEditInputType;
    }

    public int getDescriptionEditInputType() {
        return this.mDescriptionEditInputType;
    }

    public int getInputType() {
        return this.mInputType;
    }

    public int getDescriptionInputType() {
        return this.mDescriptionInputType;
    }

    public boolean isChecked() {
        return (this.mActionFlags & 1) == 1;
    }

    public void setChecked(boolean z) {
        setFlags(z ? 1 : 0, 1);
    }

    public int getCheckSetId() {
        return this.mCheckSetId;
    }

    public boolean hasMultilineDescription() {
        return (this.mActionFlags & 2) == 2;
    }

    public boolean isEnabled() {
        return (this.mActionFlags & 16) == 16;
    }

    public boolean isFocusable() {
        return (this.mActionFlags & 32) == 32;
    }

    public String[] getAutofillHints() {
        return this.mAutofillHints;
    }

    public boolean hasNext() {
        return (this.mActionFlags & 4) == 4;
    }

    public boolean infoOnly() {
        return (this.mActionFlags & 8) == 8;
    }

    public List<GuidedAction> getSubActions() {
        return this.mSubActions;
    }

    public boolean hasSubActions() {
        return this.mSubActions != null;
    }

    public final boolean isAutoSaveRestoreEnabled() {
        return (this.mActionFlags & 64) == 64;
    }

    public void onSaveInstanceState(Bundle bundle, String str) {
        if (needAutoSaveTitle() && getTitle() != null) {
            bundle.putString(str, getTitle().toString());
        } else if (needAutoSaveDescription() && getDescription() != null) {
            bundle.putString(str, getDescription().toString());
        } else if (getCheckSetId() != 0) {
            bundle.putBoolean(str, isChecked());
        }
    }

    public void onRestoreInstanceState(Bundle bundle, String str) {
        if (needAutoSaveTitle()) {
            String string = bundle.getString(str);
            if (string != null) {
                setTitle(string);
            }
        } else if (needAutoSaveDescription()) {
            String string2 = bundle.getString(str);
            if (string2 != null) {
                setDescription(string2);
            }
        } else if (getCheckSetId() != 0) {
            setChecked(bundle.getBoolean(str, isChecked()));
        }
    }

    /* access modifiers changed from: 0000 */
    public final boolean needAutoSaveTitle() {
        return isEditable() && !isPasswordVariant(getEditInputType());
    }

    /* access modifiers changed from: 0000 */
    public final boolean needAutoSaveDescription() {
        return isDescriptionEditable() && !isPasswordVariant(getDescriptionEditInputType());
    }
}
