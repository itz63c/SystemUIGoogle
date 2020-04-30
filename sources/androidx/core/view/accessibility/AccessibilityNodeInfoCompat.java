package androidx.core.view.accessibility;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.constraint.R$styleable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo.CollectionInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo;
import androidx.core.R$id;
import androidx.core.view.accessibility.AccessibilityViewCommand.CommandArguments;
import androidx.core.view.accessibility.AccessibilityViewCommand.MoveAtGranularityArguments;
import androidx.core.view.accessibility.AccessibilityViewCommand.MoveHtmlArguments;
import androidx.core.view.accessibility.AccessibilityViewCommand.MoveWindowArguments;
import androidx.core.view.accessibility.AccessibilityViewCommand.ScrollToPositionArguments;
import androidx.core.view.accessibility.AccessibilityViewCommand.SetProgressArguments;
import androidx.core.view.accessibility.AccessibilityViewCommand.SetSelectionArguments;
import androidx.core.view.accessibility.AccessibilityViewCommand.SetTextArguments;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccessibilityNodeInfoCompat {
    private static int sClickableSpanId;
    private final AccessibilityNodeInfo mInfo;
    public int mParentVirtualDescendantId = -1;
    private int mVirtualDescendantId = -1;

    public static class AccessibilityActionCompat {
        public static final AccessibilityActionCompat ACTION_CLEAR_FOCUS = new AccessibilityActionCompat(2, null);
        public static final AccessibilityActionCompat ACTION_CLICK = new AccessibilityActionCompat(16, null);
        public static final AccessibilityActionCompat ACTION_DISMISS = new AccessibilityActionCompat(1048576, null);
        public static final AccessibilityActionCompat ACTION_FOCUS = new AccessibilityActionCompat(1, null);
        public static final AccessibilityActionCompat ACTION_SCROLL_BACKWARD = new AccessibilityActionCompat(8192, null);
        public static final AccessibilityActionCompat ACTION_SCROLL_DOWN;
        public static final AccessibilityActionCompat ACTION_SCROLL_FORWARD = new AccessibilityActionCompat(4096, null);
        public static final AccessibilityActionCompat ACTION_SCROLL_LEFT;
        public static final AccessibilityActionCompat ACTION_SCROLL_RIGHT;
        public static final AccessibilityActionCompat ACTION_SCROLL_UP;
        final Object mAction;
        protected final AccessibilityViewCommand mCommand;
        private final int mId;
        private final Class<? extends CommandArguments> mViewCommandArgumentClass;

        static {
            Class<MoveHtmlArguments> cls = MoveHtmlArguments.class;
            Class<MoveAtGranularityArguments> cls2 = MoveAtGranularityArguments.class;
            int i = VERSION.SDK_INT;
            AccessibilityAction accessibilityAction = null;
            new AccessibilityActionCompat(4, null);
            new AccessibilityActionCompat(8, null);
            new AccessibilityActionCompat(32, null);
            new AccessibilityActionCompat(64, null);
            new AccessibilityActionCompat(128, null);
            new AccessibilityActionCompat(256, null, cls2);
            new AccessibilityActionCompat(512, null, cls2);
            new AccessibilityActionCompat(1024, null, cls);
            new AccessibilityActionCompat(2048, null, cls);
            new AccessibilityActionCompat(16384, null);
            new AccessibilityActionCompat(32768, null);
            new AccessibilityActionCompat(65536, null);
            new AccessibilityActionCompat(131072, null, SetSelectionArguments.class);
            new AccessibilityActionCompat(262144, null);
            new AccessibilityActionCompat(524288, null);
            new AccessibilityActionCompat(2097152, null, SetTextArguments.class);
            new AccessibilityActionCompat(i >= 23 ? AccessibilityAction.ACTION_SHOW_ON_SCREEN : null, 16908342, null, null, null);
            new AccessibilityActionCompat(i >= 23 ? AccessibilityAction.ACTION_SCROLL_TO_POSITION : null, 16908343, null, null, ScrollToPositionArguments.class);
            AccessibilityActionCompat accessibilityActionCompat = new AccessibilityActionCompat(i >= 23 ? AccessibilityAction.ACTION_SCROLL_UP : null, 16908344, null, null, null);
            ACTION_SCROLL_UP = accessibilityActionCompat;
            AccessibilityActionCompat accessibilityActionCompat2 = new AccessibilityActionCompat(i >= 23 ? AccessibilityAction.ACTION_SCROLL_LEFT : null, 16908345, null, null, null);
            ACTION_SCROLL_LEFT = accessibilityActionCompat2;
            AccessibilityActionCompat accessibilityActionCompat3 = new AccessibilityActionCompat(i >= 23 ? AccessibilityAction.ACTION_SCROLL_DOWN : null, 16908346, null, null, null);
            ACTION_SCROLL_DOWN = accessibilityActionCompat3;
            AccessibilityActionCompat accessibilityActionCompat4 = new AccessibilityActionCompat(i >= 23 ? AccessibilityAction.ACTION_SCROLL_RIGHT : null, 16908347, null, null, null);
            ACTION_SCROLL_RIGHT = accessibilityActionCompat4;
            new AccessibilityActionCompat(i >= 29 ? AccessibilityAction.ACTION_PAGE_UP : null, 16908358, null, null, null);
            new AccessibilityActionCompat(i >= 29 ? AccessibilityAction.ACTION_PAGE_DOWN : null, 16908359, null, null, null);
            new AccessibilityActionCompat(i >= 29 ? AccessibilityAction.ACTION_PAGE_LEFT : null, 16908360, null, null, null);
            new AccessibilityActionCompat(i >= 29 ? AccessibilityAction.ACTION_PAGE_RIGHT : null, 16908361, null, null, null);
            new AccessibilityActionCompat(i >= 23 ? AccessibilityAction.ACTION_CONTEXT_CLICK : null, 16908348, null, null, null);
            new AccessibilityActionCompat(i >= 24 ? AccessibilityAction.ACTION_SET_PROGRESS : null, 16908349, null, null, SetProgressArguments.class);
            new AccessibilityActionCompat(i >= 26 ? AccessibilityAction.ACTION_MOVE_WINDOW : null, 16908354, null, null, MoveWindowArguments.class);
            new AccessibilityActionCompat(i >= 28 ? AccessibilityAction.ACTION_SHOW_TOOLTIP : null, 16908356, null, null, null);
            if (i >= 28) {
                accessibilityAction = AccessibilityAction.ACTION_HIDE_TOOLTIP;
            }
            new AccessibilityActionCompat(accessibilityAction, 16908357, null, null, null);
        }

        public AccessibilityActionCompat(int i, CharSequence charSequence) {
            this(null, i, charSequence, null, null);
        }

        AccessibilityActionCompat(Object obj) {
            this(obj, 0, null, null, null);
        }

        private AccessibilityActionCompat(int i, CharSequence charSequence, Class<? extends CommandArguments> cls) {
            this(null, i, charSequence, null, cls);
        }

        AccessibilityActionCompat(Object obj, int i, CharSequence charSequence, AccessibilityViewCommand accessibilityViewCommand, Class<? extends CommandArguments> cls) {
            this.mId = i;
            this.mCommand = accessibilityViewCommand;
            if (VERSION.SDK_INT < 21 || obj != null) {
                this.mAction = obj;
            } else {
                this.mAction = new AccessibilityAction(i, charSequence);
            }
            this.mViewCommandArgumentClass = cls;
        }

        public int getId() {
            if (VERSION.SDK_INT >= 21) {
                return ((AccessibilityAction) this.mAction).getId();
            }
            return 0;
        }

        public CharSequence getLabel() {
            if (VERSION.SDK_INT >= 21) {
                return ((AccessibilityAction) this.mAction).getLabel();
            }
            return null;
        }

        /* JADX WARNING: Removed duplicated region for block: B:14:0x0025  */
        /* JADX WARNING: Removed duplicated region for block: B:15:0x0028  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean perform(android.view.View r5, android.os.Bundle r6) {
            /*
                r4 = this;
                androidx.core.view.accessibility.AccessibilityViewCommand r0 = r4.mCommand
                r1 = 0
                if (r0 == 0) goto L_0x0049
                r0 = 0
                java.lang.Class<? extends androidx.core.view.accessibility.AccessibilityViewCommand$CommandArguments> r2 = r4.mViewCommandArgumentClass
                if (r2 == 0) goto L_0x0042
                java.lang.Class[] r3 = new java.lang.Class[r1]     // Catch:{ Exception -> 0x0020 }
                java.lang.reflect.Constructor r2 = r2.getDeclaredConstructor(r3)     // Catch:{ Exception -> 0x0020 }
                java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ Exception -> 0x0020 }
                java.lang.Object r1 = r2.newInstance(r1)     // Catch:{ Exception -> 0x0020 }
                androidx.core.view.accessibility.AccessibilityViewCommand$CommandArguments r1 = (androidx.core.view.accessibility.AccessibilityViewCommand.CommandArguments) r1     // Catch:{ Exception -> 0x0020 }
                r1.setBundle(r6)     // Catch:{ Exception -> 0x001d }
                r0 = r1
                goto L_0x0042
            L_0x001d:
                r6 = move-exception
                r0 = r1
                goto L_0x0021
            L_0x0020:
                r6 = move-exception
            L_0x0021:
                java.lang.Class<? extends androidx.core.view.accessibility.AccessibilityViewCommand$CommandArguments> r1 = r4.mViewCommandArgumentClass
                if (r1 != 0) goto L_0x0028
                java.lang.String r1 = "null"
                goto L_0x002c
            L_0x0028:
                java.lang.String r1 = r1.getName()
            L_0x002c:
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "Failed to execute command with argument class ViewCommandArgument: "
                r2.append(r3)
                r2.append(r1)
                java.lang.String r1 = r2.toString()
                java.lang.String r2 = "A11yActionCompat"
                android.util.Log.e(r2, r1, r6)
            L_0x0042:
                androidx.core.view.accessibility.AccessibilityViewCommand r4 = r4.mCommand
                boolean r4 = r4.perform(r5, r0)
                return r4
            L_0x0049:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.perform(android.view.View, android.os.Bundle):boolean");
        }

        public AccessibilityActionCompat createReplacementAction(CharSequence charSequence, AccessibilityViewCommand accessibilityViewCommand) {
            AccessibilityActionCompat accessibilityActionCompat = new AccessibilityActionCompat(null, this.mId, charSequence, accessibilityViewCommand, this.mViewCommandArgumentClass);
            return accessibilityActionCompat;
        }

        public int hashCode() {
            Object obj = this.mAction;
            if (obj != null) {
                return obj.hashCode();
            }
            return 0;
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof AccessibilityActionCompat)) {
                return false;
            }
            AccessibilityActionCompat accessibilityActionCompat = (AccessibilityActionCompat) obj;
            Object obj2 = this.mAction;
            if (obj2 == null) {
                if (accessibilityActionCompat.mAction != null) {
                    return false;
                }
            } else if (!obj2.equals(accessibilityActionCompat.mAction)) {
                return false;
            }
            return true;
        }
    }

    public static class CollectionInfoCompat {
        final Object mInfo;

        public static CollectionInfoCompat obtain(int i, int i2, boolean z, int i3) {
            int i4 = VERSION.SDK_INT;
            if (i4 >= 21) {
                return new CollectionInfoCompat(CollectionInfo.obtain(i, i2, z, i3));
            }
            if (i4 >= 19) {
                return new CollectionInfoCompat(CollectionInfo.obtain(i, i2, z));
            }
            return new CollectionInfoCompat(null);
        }

        CollectionInfoCompat(Object obj) {
            this.mInfo = obj;
        }
    }

    public static class CollectionItemInfoCompat {
        final Object mInfo;

        public static CollectionItemInfoCompat obtain(int i, int i2, int i3, int i4, boolean z, boolean z2) {
            int i5 = VERSION.SDK_INT;
            if (i5 >= 21) {
                return new CollectionItemInfoCompat(CollectionItemInfo.obtain(i, i2, i3, i4, z, z2));
            }
            if (i5 >= 19) {
                return new CollectionItemInfoCompat(CollectionItemInfo.obtain(i, i2, i3, i4, z));
            }
            return new CollectionItemInfoCompat(null);
        }

        CollectionItemInfoCompat(Object obj) {
            this.mInfo = obj;
        }
    }

    private static String getActionSymbolicName(int i) {
        if (i == 1) {
            return "ACTION_FOCUS";
        }
        if (i == 2) {
            return "ACTION_CLEAR_FOCUS";
        }
        switch (i) {
            case 4:
                return "ACTION_SELECT";
            case 8:
                return "ACTION_CLEAR_SELECTION";
            case 16:
                return "ACTION_CLICK";
            case 32:
                return "ACTION_LONG_CLICK";
            case R$styleable.ConstraintSet_layout_editor_absoluteX /*64*/:
                return "ACTION_ACCESSIBILITY_FOCUS";
            case 128:
                return "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
            case 256:
                return "ACTION_NEXT_AT_MOVEMENT_GRANULARITY";
            case 512:
                return "ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY";
            case 1024:
                return "ACTION_NEXT_HTML_ELEMENT";
            case 2048:
                return "ACTION_PREVIOUS_HTML_ELEMENT";
            case 4096:
                return "ACTION_SCROLL_FORWARD";
            case 8192:
                return "ACTION_SCROLL_BACKWARD";
            case 16384:
                return "ACTION_COPY";
            case 32768:
                return "ACTION_PASTE";
            case 65536:
                return "ACTION_CUT";
            case 131072:
                return "ACTION_SET_SELECTION";
            case 262144:
                return "ACTION_EXPAND";
            case 524288:
                return "ACTION_COLLAPSE";
            case 2097152:
                return "ACTION_SET_TEXT";
            case 16908354:
                return "ACTION_MOVE_WINDOW";
            default:
                switch (i) {
                    case 16908342:
                        return "ACTION_SHOW_ON_SCREEN";
                    case 16908343:
                        return "ACTION_SCROLL_TO_POSITION";
                    case 16908344:
                        return "ACTION_SCROLL_UP";
                    case 16908345:
                        return "ACTION_SCROLL_LEFT";
                    case 16908346:
                        return "ACTION_SCROLL_DOWN";
                    case 16908347:
                        return "ACTION_SCROLL_RIGHT";
                    case 16908348:
                        return "ACTION_CONTEXT_CLICK";
                    case 16908349:
                        return "ACTION_SET_PROGRESS";
                    default:
                        switch (i) {
                            case 16908356:
                                return "ACTION_SHOW_TOOLTIP";
                            case 16908357:
                                return "ACTION_HIDE_TOOLTIP";
                            case 16908358:
                                return "ACTION_PAGE_UP";
                            case 16908359:
                                return "ACTION_PAGE_DOWN";
                            case 16908360:
                                return "ACTION_PAGE_LEFT";
                            case 16908361:
                                return "ACTION_PAGE_RIGHT";
                            default:
                                return "ACTION_UNKNOWN";
                        }
                }
        }
    }

    private AccessibilityNodeInfoCompat(AccessibilityNodeInfo accessibilityNodeInfo) {
        this.mInfo = accessibilityNodeInfo;
    }

    public static AccessibilityNodeInfoCompat wrap(AccessibilityNodeInfo accessibilityNodeInfo) {
        return new AccessibilityNodeInfoCompat(accessibilityNodeInfo);
    }

    public AccessibilityNodeInfo unwrap() {
        return this.mInfo;
    }

    public static AccessibilityNodeInfoCompat obtain(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        return wrap(AccessibilityNodeInfo.obtain(accessibilityNodeInfoCompat.mInfo));
    }

    public void setSource(View view) {
        this.mVirtualDescendantId = -1;
        this.mInfo.setSource(view);
    }

    public void addChild(View view) {
        this.mInfo.addChild(view);
    }

    public int getActions() {
        return this.mInfo.getActions();
    }

    public void addAction(int i) {
        this.mInfo.addAction(i);
    }

    private List<Integer> extrasIntList(String str) {
        if (VERSION.SDK_INT < 19) {
            return new ArrayList();
        }
        ArrayList integerArrayList = this.mInfo.getExtras().getIntegerArrayList(str);
        if (integerArrayList == null) {
            integerArrayList = new ArrayList();
            this.mInfo.getExtras().putIntegerArrayList(str, integerArrayList);
        }
        return integerArrayList;
    }

    public void addAction(AccessibilityActionCompat accessibilityActionCompat) {
        if (VERSION.SDK_INT >= 21) {
            this.mInfo.addAction((AccessibilityAction) accessibilityActionCompat.mAction);
        }
    }

    public boolean removeAction(AccessibilityActionCompat accessibilityActionCompat) {
        if (VERSION.SDK_INT >= 21) {
            return this.mInfo.removeAction((AccessibilityAction) accessibilityActionCompat.mAction);
        }
        return false;
    }

    public boolean performAction(int i, Bundle bundle) {
        if (VERSION.SDK_INT >= 16) {
            return this.mInfo.performAction(i, bundle);
        }
        return false;
    }

    public void setMovementGranularities(int i) {
        if (VERSION.SDK_INT >= 16) {
            this.mInfo.setMovementGranularities(i);
        }
    }

    public int getMovementGranularities() {
        if (VERSION.SDK_INT >= 16) {
            return this.mInfo.getMovementGranularities();
        }
        return 0;
    }

    public void setParent(View view) {
        this.mParentVirtualDescendantId = -1;
        this.mInfo.setParent(view);
    }

    @Deprecated
    public void getBoundsInParent(Rect rect) {
        this.mInfo.getBoundsInParent(rect);
    }

    public void getBoundsInScreen(Rect rect) {
        this.mInfo.getBoundsInScreen(rect);
    }

    public void setBoundsInScreen(Rect rect) {
        this.mInfo.setBoundsInScreen(rect);
    }

    public boolean isCheckable() {
        return this.mInfo.isCheckable();
    }

    public boolean isChecked() {
        return this.mInfo.isChecked();
    }

    public boolean isFocusable() {
        return this.mInfo.isFocusable();
    }

    public void setFocusable(boolean z) {
        this.mInfo.setFocusable(z);
    }

    public boolean isFocused() {
        return this.mInfo.isFocused();
    }

    public void setFocused(boolean z) {
        this.mInfo.setFocused(z);
    }

    public boolean isVisibleToUser() {
        if (VERSION.SDK_INT >= 16) {
            return this.mInfo.isVisibleToUser();
        }
        return false;
    }

    public void setVisibleToUser(boolean z) {
        if (VERSION.SDK_INT >= 16) {
            this.mInfo.setVisibleToUser(z);
        }
    }

    public boolean isAccessibilityFocused() {
        if (VERSION.SDK_INT >= 16) {
            return this.mInfo.isAccessibilityFocused();
        }
        return false;
    }

    public void setAccessibilityFocused(boolean z) {
        if (VERSION.SDK_INT >= 16) {
            this.mInfo.setAccessibilityFocused(z);
        }
    }

    public boolean isSelected() {
        return this.mInfo.isSelected();
    }

    public void setSelected(boolean z) {
        this.mInfo.setSelected(z);
    }

    public boolean isClickable() {
        return this.mInfo.isClickable();
    }

    public void setClickable(boolean z) {
        this.mInfo.setClickable(z);
    }

    public boolean isLongClickable() {
        return this.mInfo.isLongClickable();
    }

    public void setLongClickable(boolean z) {
        this.mInfo.setLongClickable(z);
    }

    public boolean isEnabled() {
        return this.mInfo.isEnabled();
    }

    public void setEnabled(boolean z) {
        this.mInfo.setEnabled(z);
    }

    public boolean isPassword() {
        return this.mInfo.isPassword();
    }

    public boolean isScrollable() {
        return this.mInfo.isScrollable();
    }

    public void setScrollable(boolean z) {
        this.mInfo.setScrollable(z);
    }

    public CharSequence getPackageName() {
        return this.mInfo.getPackageName();
    }

    public void setPackageName(CharSequence charSequence) {
        this.mInfo.setPackageName(charSequence);
    }

    public CharSequence getClassName() {
        return this.mInfo.getClassName();
    }

    public void setClassName(CharSequence charSequence) {
        this.mInfo.setClassName(charSequence);
    }

    public CharSequence getText() {
        if (!hasSpans()) {
            return this.mInfo.getText();
        }
        List extrasIntList = extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_START_KEY");
        List extrasIntList2 = extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_END_KEY");
        List extrasIntList3 = extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_FLAGS_KEY");
        List extrasIntList4 = extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ID_KEY");
        SpannableString spannableString = new SpannableString(TextUtils.substring(this.mInfo.getText(), 0, this.mInfo.getText().length()));
        for (int i = 0; i < extrasIntList.size(); i++) {
            spannableString.setSpan(new AccessibilityClickableSpanCompat(((Integer) extrasIntList4.get(i)).intValue(), this, getExtras().getInt("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ACTION_ID_KEY")), ((Integer) extrasIntList.get(i)).intValue(), ((Integer) extrasIntList2.get(i)).intValue(), ((Integer) extrasIntList3.get(i)).intValue());
        }
        return spannableString;
    }

    public void addSpansToExtras(CharSequence charSequence, View view) {
        int i = VERSION.SDK_INT;
        if (i >= 19 && i < 26) {
            clearExtrasSpans();
            removeCollectedSpans(view);
            ClickableSpan[] clickableSpans = getClickableSpans(charSequence);
            if (clickableSpans != null && clickableSpans.length > 0) {
                getExtras().putInt("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ACTION_ID_KEY", R$id.accessibility_action_clickable_span);
                SparseArray orCreateSpansFromViewTags = getOrCreateSpansFromViewTags(view);
                int i2 = 0;
                while (clickableSpans != null && i2 < clickableSpans.length) {
                    int idForClickableSpan = idForClickableSpan(clickableSpans[i2], orCreateSpansFromViewTags);
                    orCreateSpansFromViewTags.put(idForClickableSpan, new WeakReference(clickableSpans[i2]));
                    addSpanLocationToExtras(clickableSpans[i2], (Spanned) charSequence, idForClickableSpan);
                    i2++;
                }
            }
        }
    }

    private SparseArray<WeakReference<ClickableSpan>> getOrCreateSpansFromViewTags(View view) {
        SparseArray<WeakReference<ClickableSpan>> spansFromViewTags = getSpansFromViewTags(view);
        if (spansFromViewTags != null) {
            return spansFromViewTags;
        }
        SparseArray<WeakReference<ClickableSpan>> sparseArray = new SparseArray<>();
        view.setTag(R$id.tag_accessibility_clickable_spans, sparseArray);
        return sparseArray;
    }

    private SparseArray<WeakReference<ClickableSpan>> getSpansFromViewTags(View view) {
        return (SparseArray) view.getTag(R$id.tag_accessibility_clickable_spans);
    }

    public static ClickableSpan[] getClickableSpans(CharSequence charSequence) {
        if (charSequence instanceof Spanned) {
            return (ClickableSpan[]) ((Spanned) charSequence).getSpans(0, charSequence.length(), ClickableSpan.class);
        }
        return null;
    }

    private int idForClickableSpan(ClickableSpan clickableSpan, SparseArray<WeakReference<ClickableSpan>> sparseArray) {
        if (sparseArray != null) {
            for (int i = 0; i < sparseArray.size(); i++) {
                if (clickableSpan.equals((ClickableSpan) ((WeakReference) sparseArray.valueAt(i)).get())) {
                    return sparseArray.keyAt(i);
                }
            }
        }
        int i2 = sClickableSpanId;
        sClickableSpanId = i2 + 1;
        return i2;
    }

    private boolean hasSpans() {
        return !extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_START_KEY").isEmpty();
    }

    private void clearExtrasSpans() {
        if (VERSION.SDK_INT >= 19) {
            this.mInfo.getExtras().remove("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_START_KEY");
            this.mInfo.getExtras().remove("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_END_KEY");
            this.mInfo.getExtras().remove("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_FLAGS_KEY");
            this.mInfo.getExtras().remove("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ID_KEY");
        }
    }

    private void addSpanLocationToExtras(ClickableSpan clickableSpan, Spanned spanned, int i) {
        extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_START_KEY").add(Integer.valueOf(spanned.getSpanStart(clickableSpan)));
        extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_END_KEY").add(Integer.valueOf(spanned.getSpanEnd(clickableSpan)));
        extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_FLAGS_KEY").add(Integer.valueOf(spanned.getSpanFlags(clickableSpan)));
        extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ID_KEY").add(Integer.valueOf(i));
    }

    private void removeCollectedSpans(View view) {
        SparseArray spansFromViewTags = getSpansFromViewTags(view);
        if (spansFromViewTags != null) {
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < spansFromViewTags.size(); i++) {
                if (((WeakReference) spansFromViewTags.valueAt(i)).get() == null) {
                    arrayList.add(Integer.valueOf(i));
                }
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                spansFromViewTags.remove(((Integer) arrayList.get(i2)).intValue());
            }
        }
    }

    public CharSequence getContentDescription() {
        return this.mInfo.getContentDescription();
    }

    public void setContentDescription(CharSequence charSequence) {
        this.mInfo.setContentDescription(charSequence);
    }

    public void recycle() {
        this.mInfo.recycle();
    }

    public String getViewIdResourceName() {
        if (VERSION.SDK_INT >= 18) {
            return this.mInfo.getViewIdResourceName();
        }
        return null;
    }

    public void setCollectionInfo(Object obj) {
        if (VERSION.SDK_INT >= 19) {
            this.mInfo.setCollectionInfo(obj == null ? null : (CollectionInfo) ((CollectionInfoCompat) obj).mInfo);
        }
    }

    public void setCollectionItemInfo(Object obj) {
        if (VERSION.SDK_INT >= 19) {
            this.mInfo.setCollectionItemInfo(obj == null ? null : (CollectionItemInfo) ((CollectionItemInfoCompat) obj).mInfo);
        }
    }

    public List<AccessibilityActionCompat> getActionList() {
        List actionList = VERSION.SDK_INT >= 21 ? this.mInfo.getActionList() : null;
        if (actionList == null) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        int size = actionList.size();
        for (int i = 0; i < size; i++) {
            arrayList.add(new AccessibilityActionCompat(actionList.get(i)));
        }
        return arrayList;
    }

    public void setCanOpenPopup(boolean z) {
        if (VERSION.SDK_INT >= 19) {
            this.mInfo.setCanOpenPopup(z);
        }
    }

    public Bundle getExtras() {
        if (VERSION.SDK_INT >= 19) {
            return this.mInfo.getExtras();
        }
        return new Bundle();
    }

    public void setPaneTitle(CharSequence charSequence) {
        int i = VERSION.SDK_INT;
        if (i >= 28) {
            this.mInfo.setPaneTitle(charSequence);
        } else if (i >= 19) {
            this.mInfo.getExtras().putCharSequence("androidx.view.accessibility.AccessibilityNodeInfoCompat.PANE_TITLE_KEY", charSequence);
        }
    }

    public void setScreenReaderFocusable(boolean z) {
        if (VERSION.SDK_INT >= 28) {
            this.mInfo.setScreenReaderFocusable(z);
        } else {
            setBooleanProperty(1, z);
        }
    }

    public void setHeading(boolean z) {
        if (VERSION.SDK_INT >= 28) {
            this.mInfo.setHeading(z);
        } else {
            setBooleanProperty(2, z);
        }
    }

    public int hashCode() {
        AccessibilityNodeInfo accessibilityNodeInfo = this.mInfo;
        if (accessibilityNodeInfo == null) {
            return 0;
        }
        return accessibilityNodeInfo.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof AccessibilityNodeInfoCompat)) {
            return false;
        }
        AccessibilityNodeInfoCompat accessibilityNodeInfoCompat = (AccessibilityNodeInfoCompat) obj;
        AccessibilityNodeInfo accessibilityNodeInfo = this.mInfo;
        if (accessibilityNodeInfo == null) {
            if (accessibilityNodeInfoCompat.mInfo != null) {
                return false;
            }
        } else if (!accessibilityNodeInfo.equals(accessibilityNodeInfoCompat.mInfo)) {
            return false;
        }
        return this.mVirtualDescendantId == accessibilityNodeInfoCompat.mVirtualDescendantId && this.mParentVirtualDescendantId == accessibilityNodeInfoCompat.mParentVirtualDescendantId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        Rect rect = new Rect();
        getBoundsInParent(rect);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("; boundsInParent: ");
        sb2.append(rect);
        sb.append(sb2.toString());
        getBoundsInScreen(rect);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("; boundsInScreen: ");
        sb3.append(rect);
        sb.append(sb3.toString());
        sb.append("; packageName: ");
        sb.append(getPackageName());
        sb.append("; className: ");
        sb.append(getClassName());
        sb.append("; text: ");
        sb.append(getText());
        sb.append("; contentDescription: ");
        sb.append(getContentDescription());
        sb.append("; viewId: ");
        sb.append(getViewIdResourceName());
        sb.append("; checkable: ");
        sb.append(isCheckable());
        sb.append("; checked: ");
        sb.append(isChecked());
        sb.append("; focusable: ");
        sb.append(isFocusable());
        sb.append("; focused: ");
        sb.append(isFocused());
        sb.append("; selected: ");
        sb.append(isSelected());
        sb.append("; clickable: ");
        sb.append(isClickable());
        sb.append("; longClickable: ");
        sb.append(isLongClickable());
        sb.append("; enabled: ");
        sb.append(isEnabled());
        sb.append("; password: ");
        sb.append(isPassword());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("; scrollable: ");
        sb4.append(isScrollable());
        sb.append(sb4.toString());
        sb.append("; [");
        String str = ", ";
        if (VERSION.SDK_INT >= 21) {
            List actionList = getActionList();
            for (int i = 0; i < actionList.size(); i++) {
                AccessibilityActionCompat accessibilityActionCompat = (AccessibilityActionCompat) actionList.get(i);
                String actionSymbolicName = getActionSymbolicName(accessibilityActionCompat.getId());
                if (actionSymbolicName.equals("ACTION_UNKNOWN") && accessibilityActionCompat.getLabel() != null) {
                    actionSymbolicName = accessibilityActionCompat.getLabel().toString();
                }
                sb.append(actionSymbolicName);
                if (i != actionList.size() - 1) {
                    sb.append(str);
                }
            }
        } else {
            int actions = getActions();
            while (actions != 0) {
                int numberOfTrailingZeros = 1 << Integer.numberOfTrailingZeros(actions);
                actions &= ~numberOfTrailingZeros;
                sb.append(getActionSymbolicName(numberOfTrailingZeros));
                if (actions != 0) {
                    sb.append(str);
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private void setBooleanProperty(int i, boolean z) {
        Bundle extras = getExtras();
        if (extras != null) {
            String str = "androidx.view.accessibility.AccessibilityNodeInfoCompat.BOOLEAN_PROPERTY_KEY";
            int i2 = extras.getInt(str, 0) & (~i);
            if (!z) {
                i = 0;
            }
            extras.putInt(str, i | i2);
        }
    }
}
