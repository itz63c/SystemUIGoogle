package com.android.systemui.statusbar.notification.stack;

import android.util.MathUtils;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.util.HashSet;

public class NotificationRoundnessManager implements OnHeadsUpChangedListener {
    private HashSet<ExpandableView> mAnimatedChildren;
    private float mAppearFraction;
    private final KeyguardBypassController mBypassController;
    private boolean mExpanded;
    private final ActivatableNotificationView[] mFirstInSectionViews;
    private final ActivatableNotificationView[] mLastInSectionViews;
    private Runnable mRoundingChangedCallback;
    private final ActivatableNotificationView[] mTmpFirstInSectionViews;
    private final ActivatableNotificationView[] mTmpLastInSectionViews;
    private ExpandableNotificationRow mTrackedHeadsUp;

    NotificationRoundnessManager(KeyguardBypassController keyguardBypassController, NotificationSectionsFeatureManager notificationSectionsFeatureManager) {
        int numberOfBuckets = notificationSectionsFeatureManager.getNumberOfBuckets();
        this.mFirstInSectionViews = new ActivatableNotificationView[numberOfBuckets];
        this.mLastInSectionViews = new ActivatableNotificationView[numberOfBuckets];
        this.mTmpFirstInSectionViews = new ActivatableNotificationView[numberOfBuckets];
        this.mTmpLastInSectionViews = new ActivatableNotificationView[numberOfBuckets];
        this.mBypassController = keyguardBypassController;
    }

    public void onHeadsUpPinned(NotificationEntry notificationEntry) {
        updateView(notificationEntry.getRow(), false);
    }

    public void onHeadsUpUnPinned(NotificationEntry notificationEntry) {
        updateView(notificationEntry.getRow(), true);
    }

    public void onHeadsupAnimatingAwayChanged(ExpandableNotificationRow expandableNotificationRow, boolean z) {
        updateView(expandableNotificationRow, false);
    }

    public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
        updateView(notificationEntry.getRow(), false);
    }

    private void updateView(ActivatableNotificationView activatableNotificationView, boolean z) {
        if (updateViewWithoutCallback(activatableNotificationView, z)) {
            this.mRoundingChangedCallback.run();
        }
    }

    private boolean updateViewWithoutCallback(ActivatableNotificationView activatableNotificationView, boolean z) {
        float roundness = getRoundness(activatableNotificationView, true);
        float roundness2 = getRoundness(activatableNotificationView, false);
        boolean topRoundness = activatableNotificationView.setTopRoundness(roundness, z);
        boolean bottomRoundness = activatableNotificationView.setBottomRoundness(roundness2, z);
        boolean isFirstInSection = isFirstInSection(activatableNotificationView, false);
        boolean isLastInSection = isLastInSection(activatableNotificationView, false);
        activatableNotificationView.setFirstInSection(isFirstInSection);
        activatableNotificationView.setLastInSection(isLastInSection);
        if ((isFirstInSection || isLastInSection) && (topRoundness || bottomRoundness)) {
            return true;
        }
        return false;
    }

    private boolean isFirstInSection(ActivatableNotificationView activatableNotificationView, boolean z) {
        boolean z2 = false;
        int i = 0;
        int i2 = 0;
        while (true) {
            ActivatableNotificationView[] activatableNotificationViewArr = this.mFirstInSectionViews;
            if (i >= activatableNotificationViewArr.length) {
                return false;
            }
            if (activatableNotificationView == activatableNotificationViewArr[i]) {
                if (z || i2 > 0) {
                    z2 = true;
                }
                return z2;
            }
            if (activatableNotificationViewArr[i] != null) {
                i2++;
            }
            i++;
        }
    }

    private boolean isLastInSection(ActivatableNotificationView activatableNotificationView, boolean z) {
        boolean z2 = true;
        int i = 0;
        for (int length = this.mLastInSectionViews.length - 1; length >= 0; length--) {
            ActivatableNotificationView[] activatableNotificationViewArr = this.mLastInSectionViews;
            if (activatableNotificationView == activatableNotificationViewArr[length]) {
                if (!z && i <= 0) {
                    z2 = false;
                }
                return z2;
            }
            if (activatableNotificationViewArr[length] != null) {
                i++;
            }
        }
        return false;
    }

    private float getRoundness(ActivatableNotificationView activatableNotificationView, boolean z) {
        if ((activatableNotificationView.isPinned() || activatableNotificationView.isHeadsUpAnimatingAway()) && !this.mExpanded) {
            return 1.0f;
        }
        if (isFirstInSection(activatableNotificationView, true) && z) {
            return 1.0f;
        }
        if (isLastInSection(activatableNotificationView, true) && !z) {
            return 1.0f;
        }
        if (activatableNotificationView == this.mTrackedHeadsUp) {
            return MathUtils.saturate(1.0f - this.mAppearFraction);
        }
        if (!activatableNotificationView.showingPulsing() || this.mBypassController.getBypassEnabled()) {
            return 0.0f;
        }
        return 1.0f;
    }

    public void setExpanded(float f, float f2) {
        this.mExpanded = f != 0.0f;
        this.mAppearFraction = f2;
        ExpandableNotificationRow expandableNotificationRow = this.mTrackedHeadsUp;
        if (expandableNotificationRow != null) {
            updateView(expandableNotificationRow, true);
        }
    }

    public void updateRoundedChildren(NotificationSection[] notificationSectionArr) {
        boolean handleRemovedOldViews;
        for (int i = 0; i < notificationSectionArr.length; i++) {
            ActivatableNotificationView[] activatableNotificationViewArr = this.mTmpFirstInSectionViews;
            ActivatableNotificationView[] activatableNotificationViewArr2 = this.mFirstInSectionViews;
            activatableNotificationViewArr[i] = activatableNotificationViewArr2[i];
            this.mTmpLastInSectionViews[i] = this.mLastInSectionViews[i];
            activatableNotificationViewArr2[i] = notificationSectionArr[i].getFirstVisibleChild();
            this.mLastInSectionViews[i] = notificationSectionArr[i].getLastVisibleChild();
        }
        if (handleAddedNewViews(notificationSectionArr, this.mTmpLastInSectionViews, false) || (((handleRemovedOldViews(notificationSectionArr, this.mTmpFirstInSectionViews, true) | false) | handleRemovedOldViews(notificationSectionArr, this.mTmpLastInSectionViews, false)) | handleAddedNewViews(notificationSectionArr, this.mTmpFirstInSectionViews, true))) {
            this.mRoundingChangedCallback.run();
        }
    }

    private boolean handleRemovedOldViews(NotificationSection[] notificationSectionArr, ActivatableNotificationView[] activatableNotificationViewArr, boolean z) {
        boolean z2;
        boolean z3;
        ActivatableNotificationView activatableNotificationView;
        boolean z4 = false;
        for (ActivatableNotificationView activatableNotificationView2 : activatableNotificationViewArr) {
            if (activatableNotificationView2 != null) {
                int length = notificationSectionArr.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        z2 = false;
                        break;
                    }
                    NotificationSection notificationSection = notificationSectionArr[i];
                    if (z) {
                        activatableNotificationView = notificationSection.getFirstVisibleChild();
                    } else {
                        activatableNotificationView = notificationSection.getLastVisibleChild();
                    }
                    if (activatableNotificationView != activatableNotificationView2) {
                        i++;
                    } else if (activatableNotificationView2.isFirstInSection() == isFirstInSection(activatableNotificationView2, false) && activatableNotificationView2.isLastInSection() == isLastInSection(activatableNotificationView2, false)) {
                        z3 = false;
                        z2 = true;
                    } else {
                        z2 = true;
                    }
                }
                z3 = z2;
                if (!z2 || z3) {
                    if (!activatableNotificationView2.isRemoved()) {
                        updateViewWithoutCallback(activatableNotificationView2, activatableNotificationView2.isShown());
                    }
                    z4 = true;
                }
            }
        }
        return z4;
    }

    private boolean handleAddedNewViews(NotificationSection[] notificationSectionArr, ActivatableNotificationView[] activatableNotificationViewArr, boolean z) {
        boolean z2;
        int length = notificationSectionArr.length;
        boolean z3 = false;
        for (int i = 0; i < length; i++) {
            NotificationSection notificationSection = notificationSectionArr[i];
            ActivatableNotificationView firstVisibleChild = z ? notificationSection.getFirstVisibleChild() : notificationSection.getLastVisibleChild();
            if (firstVisibleChild != null) {
                int length2 = activatableNotificationViewArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length2) {
                        z2 = false;
                        break;
                    } else if (activatableNotificationViewArr[i2] == firstVisibleChild) {
                        z2 = true;
                        break;
                    } else {
                        i2++;
                    }
                }
                if (!z2) {
                    updateViewWithoutCallback(firstVisibleChild, firstVisibleChild.isShown() && !this.mAnimatedChildren.contains(firstVisibleChild));
                    z3 = true;
                }
            }
        }
        return z3;
    }

    public void setAnimatedChildren(HashSet<ExpandableView> hashSet) {
        this.mAnimatedChildren = hashSet;
    }

    public void setOnRoundingChangedCallback(Runnable runnable) {
        this.mRoundingChangedCallback = runnable;
    }

    public void setTrackingHeadsUp(ExpandableNotificationRow expandableNotificationRow) {
        ExpandableNotificationRow expandableNotificationRow2 = this.mTrackedHeadsUp;
        this.mTrackedHeadsUp = expandableNotificationRow;
        if (expandableNotificationRow2 != null) {
            updateView(expandableNotificationRow2, true);
        }
    }
}
