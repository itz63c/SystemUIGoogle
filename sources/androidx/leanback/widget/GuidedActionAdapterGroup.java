package androidx.leanback.widget;

import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import androidx.leanback.widget.GuidedActionAdapter.EditListener;
import androidx.leanback.widget.GuidedActionsStylist.ViewHolder;
import java.util.ArrayList;

public class GuidedActionAdapterGroup {
    ArrayList<Pair<GuidedActionAdapter, GuidedActionAdapter>> mAdapters = new ArrayList<>();
    private EditListener mEditListener;
    private boolean mImeOpened;

    public void addAdpter(GuidedActionAdapter guidedActionAdapter, GuidedActionAdapter guidedActionAdapter2) {
        this.mAdapters.add(new Pair(guidedActionAdapter, guidedActionAdapter2));
        if (guidedActionAdapter != null) {
            guidedActionAdapter.mGroup = this;
        }
        if (guidedActionAdapter2 != null) {
            guidedActionAdapter2.mGroup = this;
        }
    }

    public GuidedActionAdapter getNextAdapter(GuidedActionAdapter guidedActionAdapter) {
        for (int i = 0; i < this.mAdapters.size(); i++) {
            Pair pair = (Pair) this.mAdapters.get(i);
            if (pair.first == guidedActionAdapter) {
                return (GuidedActionAdapter) pair.second;
            }
        }
        return null;
    }

    public void setEditListener(EditListener editListener) {
        this.mEditListener = editListener;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0027 A[LOOP:1: B:13:0x0027->B:16:0x0035, LOOP_START, PHI: r8 
      PHI: (r8v5 int) = (r8v1 int), (r8v6 int) binds: [B:8:0x0016, B:16:0x0035] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0063 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0064  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0018 A[LOOP:0: B:9:0x0018->B:12:0x0024, LOOP_START, PHI: r8 
      PHI: (r8v7 int) = (r8v1 int), (r8v8 int) binds: [B:8:0x0016, B:12:0x0024] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean focusToNextAction(androidx.leanback.widget.GuidedActionAdapter r7, androidx.leanback.widget.GuidedAction r8, long r9) {
        /*
            r6 = this;
            r0 = -2
            int r0 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x0011
            int r8 = r7.indexOf(r8)
            if (r8 >= 0) goto L_0x000f
            return r2
        L_0x000f:
            int r8 = r8 + r1
            goto L_0x0012
        L_0x0011:
            r8 = r2
        L_0x0012:
            int r3 = r7.getCount()
            if (r0 != 0) goto L_0x0027
        L_0x0018:
            if (r8 >= r3) goto L_0x0038
            androidx.leanback.widget.GuidedAction r4 = r7.getItem(r8)
            boolean r4 = r4.isFocusable()
            if (r4 != 0) goto L_0x0038
            int r8 = r8 + 1
            goto L_0x0018
        L_0x0027:
            if (r8 >= r3) goto L_0x0038
            androidx.leanback.widget.GuidedAction r4 = r7.getItem(r8)
            long r4 = r4.getId()
            int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r4 == 0) goto L_0x0038
            int r8 = r8 + 1
            goto L_0x0027
        L_0x0038:
            if (r8 >= r3) goto L_0x0064
            androidx.leanback.widget.GuidedActionsStylist r9 = r7.getGuidedActionsStylist()
            androidx.leanback.widget.VerticalGridView r9 = r9.getActionsGridView()
            androidx.recyclerview.widget.RecyclerView$ViewHolder r8 = r9.findViewHolderForPosition(r8)
            androidx.leanback.widget.GuidedActionsStylist$ViewHolder r8 = (androidx.leanback.widget.GuidedActionsStylist.ViewHolder) r8
            if (r8 == 0) goto L_0x0063
            androidx.leanback.widget.GuidedAction r9 = r8.getAction()
            boolean r9 = r9.hasTextEditable()
            if (r9 == 0) goto L_0x0058
            r6.openIme(r7, r8)
            goto L_0x0062
        L_0x0058:
            android.view.View r7 = r8.itemView
            r6.closeIme(r7)
            android.view.View r6 = r8.itemView
            r6.requestFocus()
        L_0x0062:
            return r1
        L_0x0063:
            return r2
        L_0x0064:
            androidx.leanback.widget.GuidedActionAdapter r7 = r6.getNextAdapter(r7)
            if (r7 != 0) goto L_0x0011
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GuidedActionAdapterGroup.focusToNextAction(androidx.leanback.widget.GuidedActionAdapter, androidx.leanback.widget.GuidedAction, long):boolean");
    }

    public void openIme(GuidedActionAdapter guidedActionAdapter, ViewHolder viewHolder) {
        guidedActionAdapter.getGuidedActionsStylist().setEditingMode(viewHolder, true);
        View editingView = viewHolder.getEditingView();
        if (editingView != null && viewHolder.isInEditingText()) {
            InputMethodManager inputMethodManager = (InputMethodManager) editingView.getContext().getSystemService("input_method");
            editingView.setFocusable(true);
            editingView.requestFocus();
            inputMethodManager.showSoftInput(editingView, 0);
            if (!this.mImeOpened) {
                this.mImeOpened = true;
                this.mEditListener.onImeOpen();
            }
        }
    }

    public void closeIme(View view) {
        if (this.mImeOpened) {
            this.mImeOpened = false;
            ((InputMethodManager) view.getContext().getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
            this.mEditListener.onImeClose();
        }
    }

    public void fillAndStay(GuidedActionAdapter guidedActionAdapter, TextView textView) {
        ViewHolder findSubChildViewHolder = guidedActionAdapter.findSubChildViewHolder(textView);
        updateTextIntoAction(findSubChildViewHolder, textView);
        this.mEditListener.onGuidedActionEditCanceled(findSubChildViewHolder.getAction());
        guidedActionAdapter.getGuidedActionsStylist().setEditingMode(findSubChildViewHolder, false);
        closeIme(textView);
        findSubChildViewHolder.itemView.requestFocus();
    }

    public void fillAndGoNext(GuidedActionAdapter guidedActionAdapter, TextView textView) {
        ViewHolder findSubChildViewHolder = guidedActionAdapter.findSubChildViewHolder(textView);
        updateTextIntoAction(findSubChildViewHolder, textView);
        guidedActionAdapter.performOnActionClick(findSubChildViewHolder);
        long onGuidedActionEditedAndProceed = this.mEditListener.onGuidedActionEditedAndProceed(findSubChildViewHolder.getAction());
        boolean z = false;
        guidedActionAdapter.getGuidedActionsStylist().setEditingMode(findSubChildViewHolder, false);
        if (!(onGuidedActionEditedAndProceed == -3 || onGuidedActionEditedAndProceed == findSubChildViewHolder.getAction().getId())) {
            z = focusToNextAction(guidedActionAdapter, findSubChildViewHolder.getAction(), onGuidedActionEditedAndProceed);
        }
        if (!z) {
            closeIme(textView);
            findSubChildViewHolder.itemView.requestFocus();
        }
    }

    private void updateTextIntoAction(ViewHolder viewHolder, TextView textView) {
        GuidedAction action = viewHolder.getAction();
        if (textView == viewHolder.getDescriptionView()) {
            if (action.getEditDescription() != null) {
                action.setEditDescription(textView.getText());
            } else {
                action.setDescription(textView.getText());
            }
        } else if (textView != viewHolder.getTitleView()) {
        } else {
            if (action.getEditTitle() != null) {
                action.setEditTitle(textView.getText());
            } else {
                action.setTitle(textView.getText());
            }
        }
    }
}
