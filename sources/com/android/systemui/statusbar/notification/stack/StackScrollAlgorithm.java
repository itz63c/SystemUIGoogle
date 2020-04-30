package com.android.systemui.statusbar.notification.stack;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.statusbar.EmptyShadeView;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.row.FooterView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StackScrollAlgorithm {
    private boolean mClipNotificationScrollToTop;
    private int mCollapsedSize;
    private int mGapHeight;
    private float mHeadsUpInset;
    private final ViewGroup mHostView;
    private int mIncreasedPaddingBetweenElements;
    private boolean mIsExpanded;
    /* access modifiers changed from: private */
    public int mPaddingBetweenElements;
    private int mPinnedZTranslationExtra;
    private int mStatusBarHeight;
    private StackScrollAlgorithmState mTempAlgorithmState = new StackScrollAlgorithmState();

    public interface SectionProvider {
        boolean beginsSection(View view, View view2);
    }

    public class StackScrollAlgorithmState {
        /* access modifiers changed from: private */
        public int indexOfExpandingNotification;
        public final HashMap<ExpandableView, Float> paddingMap = new HashMap<>();
        public int scrollY;
        public final ArrayList<ExpandableView> visibleChildren = new ArrayList<>();

        public StackScrollAlgorithmState() {
        }

        public int getPaddingAfterChild(ExpandableView expandableView) {
            Float f = (Float) this.paddingMap.get(expandableView);
            if (f == null) {
                return StackScrollAlgorithm.this.mPaddingBetweenElements;
            }
            return (int) f.floatValue();
        }

        public int getIndexOfExpandingNotification() {
            return this.indexOfExpandingNotification;
        }
    }

    public StackScrollAlgorithm(Context context, ViewGroup viewGroup) {
        this.mHostView = viewGroup;
        initView(context);
    }

    public void initView(Context context) {
        initConstants(context);
    }

    private void initConstants(Context context) {
        Resources resources = context.getResources();
        this.mPaddingBetweenElements = resources.getDimensionPixelSize(C2009R$dimen.notification_divider_height);
        this.mIncreasedPaddingBetweenElements = resources.getDimensionPixelSize(C2009R$dimen.notification_divider_height_increased);
        this.mCollapsedSize = resources.getDimensionPixelSize(C2009R$dimen.notification_min_height);
        this.mStatusBarHeight = resources.getDimensionPixelSize(C2009R$dimen.status_bar_height);
        this.mClipNotificationScrollToTop = resources.getBoolean(C2007R$bool.config_clipNotificationScrollToTop);
        this.mHeadsUpInset = (float) (this.mStatusBarHeight + resources.getDimensionPixelSize(C2009R$dimen.heads_up_status_bar_padding));
        this.mPinnedZTranslationExtra = resources.getDimensionPixelSize(C2009R$dimen.heads_up_pinned_elevation);
        this.mGapHeight = resources.getDimensionPixelSize(C2009R$dimen.notification_section_divider_height);
    }

    public void resetViewStates(AmbientState ambientState) {
        StackScrollAlgorithmState stackScrollAlgorithmState = this.mTempAlgorithmState;
        resetChildViewStates();
        initAlgorithmState(this.mHostView, stackScrollAlgorithmState, ambientState);
        updatePositionsForState(stackScrollAlgorithmState, ambientState);
        updateZValuesForState(stackScrollAlgorithmState, ambientState);
        updateHeadsUpStates(stackScrollAlgorithmState, ambientState);
        updatePulsingStates(stackScrollAlgorithmState, ambientState);
        updateDimmedActivatedHideSensitive(ambientState, stackScrollAlgorithmState);
        updateClipping(stackScrollAlgorithmState, ambientState);
        updateSpeedBumpState(stackScrollAlgorithmState, ambientState);
        updateShelfState(ambientState);
        getNotificationChildrenStates(stackScrollAlgorithmState, ambientState);
    }

    private void resetChildViewStates() {
        int childCount = this.mHostView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((ExpandableView) this.mHostView.getChildAt(i)).resetViewState();
        }
    }

    private void getNotificationChildrenStates(StackScrollAlgorithmState stackScrollAlgorithmState, AmbientState ambientState) {
        int size = stackScrollAlgorithmState.visibleChildren.size();
        for (int i = 0; i < size; i++) {
            ExpandableView expandableView = (ExpandableView) stackScrollAlgorithmState.visibleChildren.get(i);
            if (expandableView instanceof ExpandableNotificationRow) {
                ((ExpandableNotificationRow) expandableView).updateChildrenStates(ambientState);
            }
        }
    }

    private void updateSpeedBumpState(StackScrollAlgorithmState stackScrollAlgorithmState, AmbientState ambientState) {
        int size = stackScrollAlgorithmState.visibleChildren.size();
        int speedBumpIndex = ambientState.getSpeedBumpIndex();
        int i = 0;
        while (i < size) {
            ((ExpandableView) stackScrollAlgorithmState.visibleChildren.get(i)).getViewState().belowSpeedBump = i >= speedBumpIndex;
            i++;
        }
    }

    private void updateShelfState(AmbientState ambientState) {
        NotificationShelf shelf = ambientState.getShelf();
        if (shelf != null) {
            shelf.updateState(ambientState);
        }
    }

    private void updateClipping(StackScrollAlgorithmState stackScrollAlgorithmState, AmbientState ambientState) {
        float f = 0.0f;
        float topPadding = !ambientState.isOnKeyguard() ? ambientState.getTopPadding() + ambientState.getStackTranslation() + ((float) ambientState.getExpandAnimationTopChange()) : 0.0f;
        int size = stackScrollAlgorithmState.visibleChildren.size();
        boolean z = true;
        for (int i = 0; i < size; i++) {
            ExpandableView expandableView = (ExpandableView) stackScrollAlgorithmState.visibleChildren.get(i);
            ExpandableViewState viewState = expandableView.getViewState();
            if (!expandableView.mustStayOnScreen() || viewState.headsUpIsVisible) {
                f = Math.max(topPadding, f);
            }
            float f2 = viewState.yTranslation;
            float f3 = ((float) viewState.height) + f2;
            boolean z2 = (expandableView instanceof ExpandableNotificationRow) && ((ExpandableNotificationRow) expandableView).isPinned();
            if (!this.mClipNotificationScrollToTop || ((viewState.inShelf && (!z2 || z)) || f2 >= f)) {
                viewState.clipTopAmount = 0;
            } else {
                viewState.clipTopAmount = (int) (f - f2);
            }
            if (z2) {
                z = false;
            }
            if (!expandableView.isTransparent()) {
                if (!z2) {
                    f2 = f3;
                }
                f = Math.max(f, f2);
            }
        }
    }

    private void updateDimmedActivatedHideSensitive(AmbientState ambientState, StackScrollAlgorithmState stackScrollAlgorithmState) {
        boolean isDimmed = ambientState.isDimmed();
        boolean isHideSensitive = ambientState.isHideSensitive();
        ActivatableNotificationView activatedChild = ambientState.getActivatedChild();
        int size = stackScrollAlgorithmState.visibleChildren.size();
        for (int i = 0; i < size; i++) {
            ExpandableView expandableView = (ExpandableView) stackScrollAlgorithmState.visibleChildren.get(i);
            ExpandableViewState viewState = expandableView.getViewState();
            viewState.dimmed = isDimmed;
            viewState.hideSensitive = isHideSensitive;
            boolean z = activatedChild == expandableView;
            if (isDimmed && z) {
                viewState.zTranslation += ((float) ambientState.getZDistanceBetweenElements()) * 2.0f;
            }
        }
    }

    private void initAlgorithmState(ViewGroup viewGroup, StackScrollAlgorithmState stackScrollAlgorithmState, AmbientState ambientState) {
        StackScrollAlgorithmState stackScrollAlgorithmState2 = stackScrollAlgorithmState;
        stackScrollAlgorithmState2.scrollY = (int) (((float) Math.max(0, ambientState.getScrollY())) + ambientState.getOverScrollAmount(false));
        int childCount = viewGroup.getChildCount();
        stackScrollAlgorithmState2.visibleChildren.clear();
        stackScrollAlgorithmState2.visibleChildren.ensureCapacity(childCount);
        stackScrollAlgorithmState2.paddingMap.clear();
        int i = ambientState.isDozing() ? ambientState.hasPulsingNotifications() ? 1 : 0 : childCount;
        int i2 = 0;
        ExpandableView expandableView = null;
        for (int i3 = 0; i3 < childCount; i3++) {
            ExpandableView expandableView2 = (ExpandableView) viewGroup.getChildAt(i3);
            if (!(expandableView2.getVisibility() == 8 || expandableView2 == ambientState.getShelf())) {
                if (i3 >= i) {
                    expandableView = null;
                }
                i2 = updateNotGoneIndex(stackScrollAlgorithmState2, i2, expandableView2);
                float increasedPaddingAmount = expandableView2.getIncreasedPaddingAmount();
                int i4 = (increasedPaddingAmount > 0.0f ? 1 : (increasedPaddingAmount == 0.0f ? 0 : -1));
                if (i4 != 0) {
                    stackScrollAlgorithmState2.paddingMap.put(expandableView2, Float.valueOf(increasedPaddingAmount));
                    if (expandableView != null) {
                        Float f = (Float) stackScrollAlgorithmState2.paddingMap.get(expandableView);
                        float paddingForValue = getPaddingForValue(Float.valueOf(increasedPaddingAmount));
                        if (f != null) {
                            float paddingForValue2 = getPaddingForValue(f);
                            if (i4 > 0) {
                                paddingForValue = NotificationUtils.interpolate(paddingForValue2, paddingForValue, increasedPaddingAmount);
                            } else if (f.floatValue() > 0.0f) {
                                paddingForValue = NotificationUtils.interpolate(paddingForValue, paddingForValue2, f.floatValue());
                            }
                        }
                        stackScrollAlgorithmState2.paddingMap.put(expandableView, Float.valueOf(paddingForValue));
                    }
                } else if (expandableView != null) {
                    stackScrollAlgorithmState2.paddingMap.put(expandableView, Float.valueOf(getPaddingForValue((Float) stackScrollAlgorithmState2.paddingMap.get(expandableView))));
                }
                if (expandableView2 instanceof ExpandableNotificationRow) {
                    ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) expandableView2;
                    List<ExpandableNotificationRow> notificationChildren = expandableNotificationRow.getNotificationChildren();
                    if (expandableNotificationRow.isSummaryWithChildren() && notificationChildren != null) {
                        for (ExpandableNotificationRow expandableNotificationRow2 : notificationChildren) {
                            if (expandableNotificationRow2.getVisibility() != 8) {
                                expandableNotificationRow2.getViewState().notGoneIndex = i2;
                                i2++;
                            }
                        }
                    }
                }
                expandableView = expandableView2;
            }
        }
        ExpandableNotificationRow expandingNotification = ambientState.getExpandingNotification();
        int i5 = expandingNotification != null ? expandingNotification.isChildInGroup() ? stackScrollAlgorithmState2.visibleChildren.indexOf(expandingNotification.getNotificationParent()) : stackScrollAlgorithmState2.visibleChildren.indexOf(expandingNotification) : -1;
        stackScrollAlgorithmState2.indexOfExpandingNotification = i5;
    }

    private float getPaddingForValue(Float f) {
        if (f == null) {
            return (float) this.mPaddingBetweenElements;
        }
        if (f.floatValue() >= 0.0f) {
            return NotificationUtils.interpolate((float) this.mPaddingBetweenElements, (float) this.mIncreasedPaddingBetweenElements, f.floatValue());
        }
        return NotificationUtils.interpolate(0.0f, (float) this.mPaddingBetweenElements, f.floatValue() + 1.0f);
    }

    private int updateNotGoneIndex(StackScrollAlgorithmState stackScrollAlgorithmState, int i, ExpandableView expandableView) {
        expandableView.getViewState().notGoneIndex = i;
        stackScrollAlgorithmState.visibleChildren.add(expandableView);
        return i + 1;
    }

    private void updatePositionsForState(StackScrollAlgorithmState stackScrollAlgorithmState, AmbientState ambientState) {
        float f = (float) (-stackScrollAlgorithmState.scrollY);
        int size = stackScrollAlgorithmState.visibleChildren.size();
        float f2 = f;
        for (int i = 0; i < size; i++) {
            f2 = updateChild(i, stackScrollAlgorithmState, ambientState, f2, false);
        }
    }

    /* access modifiers changed from: protected */
    public float updateChild(int i, StackScrollAlgorithmState stackScrollAlgorithmState, AmbientState ambientState, float f, boolean z) {
        float f2;
        int i2 = i;
        StackScrollAlgorithmState stackScrollAlgorithmState2 = stackScrollAlgorithmState;
        ExpandableView expandableView = (ExpandableView) stackScrollAlgorithmState2.visibleChildren.get(i2);
        boolean childNeedsGapHeight = childNeedsGapHeight(ambientState.getSectionProvider(), stackScrollAlgorithmState, i, expandableView, i2 > 0 ? (ExpandableView) stackScrollAlgorithmState2.visibleChildren.get(i2 - 1) : null);
        ExpandableViewState viewState = expandableView.getViewState();
        boolean z2 = false;
        viewState.location = 0;
        float f3 = (!childNeedsGapHeight || z) ? f : f + ((float) this.mGapHeight);
        int paddingAfterChild = getPaddingAfterChild(stackScrollAlgorithmState2, expandableView);
        int maxAllowedChildHeight = getMaxAllowedChildHeight(expandableView);
        if (z) {
            viewState.yTranslation = f3 - ((float) (maxAllowedChildHeight + paddingAfterChild));
            if (f3 <= 0.0f) {
                viewState.location = 2;
            }
        } else {
            viewState.yTranslation = f3;
        }
        boolean z3 = expandableView instanceof FooterView;
        boolean z4 = expandableView instanceof EmptyShadeView;
        viewState.location = 4;
        float topPadding = ambientState.getTopPadding() + ambientState.getStackTranslation();
        if (i2 <= stackScrollAlgorithmState.getIndexOfExpandingNotification()) {
            topPadding += (float) ambientState.getExpandAnimationTopChange();
        }
        if (expandableView.mustStayOnScreen()) {
            float f4 = viewState.yTranslation;
            if (f4 >= 0.0f) {
                if (f4 + ((float) viewState.height) + topPadding < ambientState.getMaxHeadsUpTranslation()) {
                    z2 = true;
                }
                viewState.headsUpIsVisible = z2;
            }
        }
        if (z3) {
            viewState.yTranslation = Math.min(viewState.yTranslation, (float) (ambientState.getInnerHeight() - maxAllowedChildHeight));
        } else if (z4) {
            viewState.yTranslation = ((float) (ambientState.getInnerHeight() - maxAllowedChildHeight)) + (ambientState.getStackTranslation() * 0.25f);
        } else {
            clampPositionToShelf(expandableView, viewState, ambientState);
        }
        if (z) {
            f2 = viewState.yTranslation;
            if (childNeedsGapHeight) {
                f2 -= (float) this.mGapHeight;
            }
        } else {
            f2 = ((float) paddingAfterChild) + viewState.yTranslation + ((float) maxAllowedChildHeight);
            if (f2 <= 0.0f) {
                viewState.location = 2;
            }
        }
        if (viewState.location == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to assign location for child ");
            sb.append(i2);
            Log.wtf("StackScrollAlgorithm", sb.toString());
        }
        viewState.yTranslation += topPadding;
        return f2;
    }

    private boolean childNeedsGapHeight(SectionProvider sectionProvider, StackScrollAlgorithmState stackScrollAlgorithmState, int i, View view, View view2) {
        return sectionProvider.beginsSection(view, view2) && i > 0;
    }

    /* access modifiers changed from: protected */
    public int getPaddingAfterChild(StackScrollAlgorithmState stackScrollAlgorithmState, ExpandableView expandableView) {
        return stackScrollAlgorithmState.getPaddingAfterChild(expandableView);
    }

    private void updatePulsingStates(StackScrollAlgorithmState stackScrollAlgorithmState, AmbientState ambientState) {
        int size = stackScrollAlgorithmState.visibleChildren.size();
        for (int i = 0; i < size; i++) {
            View view = (View) stackScrollAlgorithmState.visibleChildren.get(i);
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
                if (expandableNotificationRow.showingPulsing() && (i != 0 || !ambientState.isPulseExpanding())) {
                    expandableNotificationRow.getViewState().hidden = false;
                }
            }
        }
    }

    private void updateHeadsUpStates(StackScrollAlgorithmState stackScrollAlgorithmState, AmbientState ambientState) {
        ExpandableViewState expandableViewState;
        int size = stackScrollAlgorithmState.visibleChildren.size();
        ExpandableNotificationRow expandableNotificationRow = null;
        for (int i = 0; i < size; i++) {
            View view = (View) stackScrollAlgorithmState.visibleChildren.get(i);
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow2 = (ExpandableNotificationRow) view;
                if (expandableNotificationRow2.isHeadsUp()) {
                    ExpandableViewState viewState = expandableNotificationRow2.getViewState();
                    boolean z = true;
                    if (expandableNotificationRow == null && expandableNotificationRow2.mustStayOnScreen() && !viewState.headsUpIsVisible) {
                        viewState.location = 1;
                        expandableNotificationRow = expandableNotificationRow2;
                    }
                    if (expandableNotificationRow != expandableNotificationRow2) {
                        z = false;
                    }
                    float f = viewState.yTranslation + ((float) viewState.height);
                    if (this.mIsExpanded && expandableNotificationRow2.mustStayOnScreen() && !viewState.headsUpIsVisible && !expandableNotificationRow2.showingPulsing()) {
                        clampHunToTop(ambientState, expandableNotificationRow2, viewState);
                        if (i == 0 && expandableNotificationRow2.isAboveShelf()) {
                            clampHunToMaxTranslation(ambientState, expandableNotificationRow2, viewState);
                            viewState.hidden = false;
                        }
                    }
                    if (expandableNotificationRow2.isPinned()) {
                        viewState.yTranslation = Math.max(viewState.yTranslation, this.mHeadsUpInset);
                        viewState.height = Math.max(expandableNotificationRow2.getIntrinsicHeight(), viewState.height);
                        viewState.hidden = false;
                        if (expandableNotificationRow == null) {
                            expandableViewState = null;
                        } else {
                            expandableViewState = expandableNotificationRow.getViewState();
                        }
                        if (expandableViewState != null && !z && (!this.mIsExpanded || f > expandableViewState.yTranslation + ((float) expandableViewState.height))) {
                            int intrinsicHeight = expandableNotificationRow2.getIntrinsicHeight();
                            viewState.height = intrinsicHeight;
                            viewState.yTranslation = Math.min((expandableViewState.yTranslation + ((float) expandableViewState.height)) - ((float) intrinsicHeight), viewState.yTranslation);
                        }
                        if (!this.mIsExpanded && z && ambientState.getScrollY() > 0) {
                            viewState.yTranslation -= (float) ambientState.getScrollY();
                        }
                    }
                    if (expandableNotificationRow2.isHeadsUpAnimatingAway()) {
                        viewState.hidden = false;
                    }
                }
            }
        }
    }

    private void clampHunToTop(AmbientState ambientState, ExpandableNotificationRow expandableNotificationRow, ExpandableViewState expandableViewState) {
        float max = Math.max(ambientState.getTopPadding() + ambientState.getStackTranslation(), expandableViewState.yTranslation);
        expandableViewState.height = (int) Math.max(((float) expandableViewState.height) - (max - expandableViewState.yTranslation), (float) expandableNotificationRow.getCollapsedHeight());
        expandableViewState.yTranslation = max;
    }

    private void clampHunToMaxTranslation(AmbientState ambientState, ExpandableNotificationRow expandableNotificationRow, ExpandableViewState expandableViewState) {
        float min = Math.min(ambientState.getMaxHeadsUpTranslation(), ((float) ambientState.getInnerHeight()) + ambientState.getTopPadding() + ambientState.getStackTranslation());
        float min2 = Math.min(expandableViewState.yTranslation, min - ((float) expandableNotificationRow.getCollapsedHeight()));
        expandableViewState.height = (int) Math.min((float) expandableViewState.height, min - min2);
        expandableViewState.yTranslation = min2;
    }

    private void clampPositionToShelf(ExpandableView expandableView, ExpandableViewState expandableViewState, AmbientState ambientState) {
        if (ambientState.getShelf() != null) {
            int innerHeight = ambientState.getInnerHeight() - ambientState.getShelf().getIntrinsicHeight();
            if (ambientState.isAppearing() && !expandableView.isAboveShelf()) {
                expandableViewState.yTranslation = Math.max(expandableViewState.yTranslation, (float) innerHeight);
            }
            float f = (float) innerHeight;
            float min = Math.min(expandableViewState.yTranslation, f);
            expandableViewState.yTranslation = min;
            if (min >= f) {
                expandableViewState.hidden = !expandableView.isExpandAnimationRunning() && !expandableView.hasExpandingChild();
                expandableViewState.inShelf = true;
                expandableViewState.headsUpIsVisible = false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getMaxAllowedChildHeight(View view) {
        if (view instanceof ExpandableView) {
            return ((ExpandableView) view).getIntrinsicHeight();
        }
        return view == null ? this.mCollapsedSize : view.getHeight();
    }

    private void updateZValuesForState(StackScrollAlgorithmState stackScrollAlgorithmState, AmbientState ambientState) {
        float f = 0.0f;
        for (int size = stackScrollAlgorithmState.visibleChildren.size() - 1; size >= 0; size--) {
            f = updateChildZValue(size, f, stackScrollAlgorithmState, ambientState);
        }
    }

    /* access modifiers changed from: protected */
    public float updateChildZValue(int i, float f, StackScrollAlgorithmState stackScrollAlgorithmState, AmbientState ambientState) {
        int i2;
        ExpandableView expandableView = (ExpandableView) stackScrollAlgorithmState.visibleChildren.get(i);
        ExpandableViewState viewState = expandableView.getViewState();
        int zDistanceBetweenElements = ambientState.getZDistanceBetweenElements();
        float baseZHeight = (float) ambientState.getBaseZHeight();
        if (expandableView.mustStayOnScreen() && !viewState.headsUpIsVisible && !ambientState.isDozingAndNotPulsing(expandableView) && viewState.yTranslation < ambientState.getTopPadding() + ambientState.getStackTranslation()) {
            if (f != 0.0f) {
                f += 1.0f;
            } else {
                f += Math.min(1.0f, ((ambientState.getTopPadding() + ambientState.getStackTranslation()) - viewState.yTranslation) / ((float) viewState.height));
            }
            viewState.zTranslation = baseZHeight + (((float) zDistanceBetweenElements) * f);
        } else if (i != 0 || (!expandableView.isAboveShelf() && !expandableView.showingPulsing())) {
            viewState.zTranslation = baseZHeight;
        } else {
            if (ambientState.getShelf() == null) {
                i2 = 0;
            } else {
                i2 = ambientState.getShelf().getIntrinsicHeight();
            }
            float innerHeight = ((float) (ambientState.getInnerHeight() - i2)) + ambientState.getTopPadding() + ambientState.getStackTranslation();
            float pinnedHeadsUpHeight = viewState.yTranslation + ((float) expandableView.getPinnedHeadsUpHeight()) + ((float) this.mPaddingBetweenElements);
            if (innerHeight > pinnedHeadsUpHeight) {
                viewState.zTranslation = baseZHeight;
            } else {
                viewState.zTranslation = baseZHeight + (Math.min((pinnedHeadsUpHeight - innerHeight) / ((float) i2), 1.0f) * ((float) zDistanceBetweenElements));
            }
        }
        viewState.zTranslation += (1.0f - expandableView.getHeaderVisibleAmount()) * ((float) this.mPinnedZTranslationExtra);
        return f;
    }

    public void setIsExpanded(boolean z) {
        this.mIsExpanded = z;
    }
}
