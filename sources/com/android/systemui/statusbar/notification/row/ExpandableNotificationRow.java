package com.android.systemui.statusbar.notification.row;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.util.MathUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.NotificationHeaderView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewStub.OnInflateListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.widget.Chronometer;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.ContrastColorUtil;
import com.android.internal.widget.CachingIconView;
import com.android.settingslib.Utils;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin.MenuItem;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.AboveShelfChangedListener;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator.ExpandAnimationParameters;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager.Callback;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.OnAppOpsClickListener;
import com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper;
import com.android.systemui.statusbar.notification.stack.AmbientState;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.notification.stack.ExpandableViewState;
import com.android.systemui.statusbar.notification.stack.NotificationChildrenContainer;
import com.android.systemui.statusbar.notification.stack.NotificationListItem;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.notification.stack.SwipeableView;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.InflatedSmartReplies.SmartRepliesAndActions;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ExpandableNotificationRow extends ActivatableNotificationView implements PluginListener<NotificationMenuRowPlugin>, SwipeableView, NotificationListItem {
    private static final long RECENTLY_ALERTED_THRESHOLD_MS = TimeUnit.SECONDS.toMillis(30);
    private static final Property<ExpandableNotificationRow, Float> TRANSLATE_CONTENT = new FloatProperty<ExpandableNotificationRow>("translate") {
        public void setValue(ExpandableNotificationRow expandableNotificationRow, float f) {
            expandableNotificationRow.setTranslation(f);
        }

        public Float get(ExpandableNotificationRow expandableNotificationRow) {
            return Float.valueOf(expandableNotificationRow.getTranslation());
        }
    };
    private boolean mAboveShelf;
    private AboveShelfChangedListener mAboveShelfChangedListener;
    private String mAppName;
    private KeyguardBypassController mBypassController;
    private View mChildAfterViewWhenDismissed;
    private boolean mChildIsExpanding;
    /* access modifiers changed from: private */
    public NotificationChildrenContainer mChildrenContainer;
    private ViewStub mChildrenContainerStub;
    private boolean mChildrenExpanded;
    /* access modifiers changed from: private */
    public boolean mEnableNonGroupedNotificationExpand;
    /* access modifiers changed from: private */
    public NotificationEntry mEntry;
    private boolean mExpandAnimationRunning;
    private OnClickListener mExpandClickListener = new OnClickListener() {
        public void onClick(View view) {
            boolean z;
            if (!ExpandableNotificationRow.this.shouldShowPublic() && ((!ExpandableNotificationRow.this.mIsLowPriority || ExpandableNotificationRow.this.isExpanded()) && ExpandableNotificationRow.this.mGroupManager.isSummaryOfGroup(ExpandableNotificationRow.this.mEntry.getSbn()))) {
                ExpandableNotificationRow.this.mGroupExpansionChanging = true;
                boolean isGroupExpanded = ExpandableNotificationRow.this.mGroupManager.isGroupExpanded(ExpandableNotificationRow.this.mEntry.getSbn());
                boolean z2 = ExpandableNotificationRow.this.mGroupManager.toggleGroupExpansion(ExpandableNotificationRow.this.mEntry.getSbn());
                ExpandableNotificationRow.this.mOnExpandClickListener.onExpandClicked(ExpandableNotificationRow.this.mEntry, z2);
                MetricsLogger.action(ExpandableNotificationRow.this.mContext, 408, z2);
                ExpandableNotificationRow.this.onExpansionChanged(true, isGroupExpanded);
            } else if (ExpandableNotificationRow.this.mEnableNonGroupedNotificationExpand) {
                if (view.isAccessibilityFocused()) {
                    ExpandableNotificationRow.this.mPrivateLayout.setFocusOnVisibilityChange();
                }
                if (ExpandableNotificationRow.this.isPinned()) {
                    z = !ExpandableNotificationRow.this.mExpandedWhenPinned;
                    ExpandableNotificationRow.this.mExpandedWhenPinned = z;
                } else {
                    z = !ExpandableNotificationRow.this.isExpanded();
                    ExpandableNotificationRow.this.setUserExpanded(z);
                }
                ExpandableNotificationRow.this.notifyHeightChanged(true);
                ExpandableNotificationRow.this.mOnExpandClickListener.onExpandClicked(ExpandableNotificationRow.this.mEntry, z);
                MetricsLogger.action(ExpandableNotificationRow.this.mContext, 407, z);
            }
        }
    };
    private boolean mExpandable;
    /* access modifiers changed from: private */
    public boolean mExpandedWhenPinned;
    private final Runnable mExpireRecentlyAlertedFlag = new Runnable() {
        public final void run() {
            ExpandableNotificationRow.this.lambda$new$1$ExpandableNotificationRow();
        }
    };
    private FalsingManager mFalsingManager;
    private boolean mForceUnlocked;
    /* access modifiers changed from: private */
    public boolean mGroupExpansionChanging;
    /* access modifiers changed from: private */
    public NotificationGroupManager mGroupManager;
    private View mGroupParentWhenDismissed;
    /* access modifiers changed from: private */
    public NotificationGuts mGuts;
    /* access modifiers changed from: private */
    public ViewStub mGutsStub;
    private boolean mHasUserChangedExpansion;
    private float mHeaderVisibleAmount = 1.0f;
    private Consumer<Boolean> mHeadsUpAnimatingAwayListener;
    private HeadsUpManager mHeadsUpManager;
    private boolean mHeadsupDisappearRunning;
    private boolean mHideSensitiveForIntrinsicHeight;
    private boolean mIconAnimationRunning;
    private int mIconTransformContentShift;
    private boolean mIconsVisible = true;
    private NotificationInlineImageResolver mImageResolver;
    private int mIncreasedPaddingBetweenElements;
    /* access modifiers changed from: private */
    public boolean mIsBlockingHelperShowing;
    private boolean mIsChildInGroup;
    private boolean mIsColorized;
    private boolean mIsHeadsUp;
    /* access modifiers changed from: private */
    public boolean mIsLowPriority;
    private boolean mIsPinned;
    private boolean mIsSummaryWithChildren;
    private boolean mIsSystemChildExpanded;
    private boolean mIsSystemExpanded;
    private boolean mJustClicked;
    private boolean mKeepInParent;
    private boolean mLastChronometerRunning = true;
    private LayoutListener mLayoutListener;
    private NotificationContentView[] mLayouts;
    private ExpansionLogger mLogger;
    private String mLoggingKey;
    private LongPressListener mLongPressListener;
    private int mMaxHeadsUpHeight;
    private int mMaxHeadsUpHeightBeforeN;
    private int mMaxHeadsUpHeightBeforeP;
    private int mMaxHeadsUpHeightIncreased;
    private NotificationMediaManager mMediaManager;
    /* access modifiers changed from: private */
    public NotificationMenuRowPlugin mMenuRow = new NotificationMenuRow(this.mContext);
    private boolean mMustStayOnScreen;
    private boolean mNeedsRedaction;
    private int mNotificationColor;
    private int mNotificationLaunchHeight;
    private int mNotificationMaxHeight;
    private int mNotificationMinHeight;
    private int mNotificationMinHeightBeforeN;
    private int mNotificationMinHeightBeforeP;
    private int mNotificationMinHeightLarge;
    private int mNotificationMinHeightMedia;
    private ExpandableNotificationRow mNotificationParent;
    /* access modifiers changed from: private */
    public boolean mNotificationTranslationFinished = false;
    private OnClickListener mOnAppOpsClickListener;
    private OnClickListener mOnClickListener;
    private Runnable mOnDismissRunnable;
    /* access modifiers changed from: private */
    public OnExpandClickListener mOnExpandClickListener;
    private boolean mOnKeyguard;
    /* access modifiers changed from: private */
    public NotificationContentView mPrivateLayout;
    private NotificationContentView mPublicLayout;
    private boolean mRemoved;
    private RowContentBindStage mRowContentBindStage;
    private BooleanSupplier mSecureStateProvider;
    private boolean mSensitive;
    private boolean mSensitiveHiddenInGeneral;
    private boolean mShowGroupBackgroundWhenExpanded;
    private boolean mShowNoBackground;
    private boolean mShowingPublic;
    private boolean mShowingPublicInitialized;
    private StatusBarStateController mStatusbarStateController;
    private SystemNotificationAsyncTask mSystemNotificationAsyncTask = new SystemNotificationAsyncTask();
    /* access modifiers changed from: private */
    public Animator mTranslateAnim;
    /* access modifiers changed from: private */
    public ArrayList<View> mTranslateableViews;
    private float mTranslationWhenRemoved;
    private boolean mUpdateBackgroundOnUpdate;
    private boolean mUseIncreasedCollapsedHeight;
    private boolean mUseIncreasedHeadsUpHeight;
    private boolean mUserExpanded;
    private boolean mUserLocked;
    private boolean mWasChildInGroupWhenRemoved;

    public interface ExpansionLogger {
        void logNotificationExpansion(String str, boolean z, boolean z2);
    }

    public interface LayoutListener {
        void onLayout();
    }

    public interface LongPressListener {
        boolean onLongPress(View view, int i, int i2, MenuItem menuItem);
    }

    private static class NotificationViewState extends ExpandableViewState {
        private NotificationViewState() {
        }

        public void applyToView(View view) {
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
                if (!expandableNotificationRow.isExpandAnimationRunning()) {
                    handleFixedTranslationZ(expandableNotificationRow);
                    super.applyToView(view);
                    expandableNotificationRow.applyChildrenState();
                }
            }
        }

        private void handleFixedTranslationZ(ExpandableNotificationRow expandableNotificationRow) {
            if (expandableNotificationRow.hasExpandingChild()) {
                this.zTranslation = expandableNotificationRow.getTranslationZ();
                this.clipTopAmount = expandableNotificationRow.getClipTopAmount();
            }
        }

        /* access modifiers changed from: protected */
        public void onYTranslationAnimationFinished(View view) {
            super.onYTranslationAnimationFinished(view);
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
                if (expandableNotificationRow.isHeadsUpAnimatingAway()) {
                    expandableNotificationRow.setHeadsUpAnimatingAway(false);
                }
            }
        }

        public void animateTo(View view, AnimationProperties animationProperties) {
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
                if (!expandableNotificationRow.isExpandAnimationRunning()) {
                    handleFixedTranslationZ(expandableNotificationRow);
                    super.animateTo(view, animationProperties);
                    expandableNotificationRow.startChildAnimation(animationProperties);
                }
            }
        }
    }

    public interface OnAppOpsClickListener {
        boolean onClick(View view, int i, int i2, MenuItem menuItem);
    }

    public interface OnExpandClickListener {
        void onExpandClicked(NotificationEntry notificationEntry, boolean z);
    }

    private class SystemNotificationAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private SystemNotificationAsyncTask() {
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voidArr) {
            return ExpandableNotificationRow.isSystemNotification(ExpandableNotificationRow.this.mContext, ExpandableNotificationRow.this.mEntry.getSbn());
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            if (ExpandableNotificationRow.this.mEntry != null) {
                ExpandableNotificationRow.this.mEntry.mIsSystemNotification = bool;
            }
        }
    }

    public View getView() {
        return this;
    }

    /* access modifiers changed from: private */
    public static Boolean isSystemNotification(Context context, StatusBarNotification statusBarNotification) {
        PackageManager packageManagerForUser = StatusBar.getPackageManagerForUser(context, statusBarNotification.getUser().getIdentifier());
        try {
            return Boolean.valueOf(Utils.isSystemPackage(context.getResources(), packageManagerForUser, packageManagerForUser.getPackageInfo(statusBarNotification.getPackageName(), 64)));
        } catch (NameNotFoundException unused) {
            Log.e("ExpandableNotifRow", "cacheIsSystemNotification: Could not find package info");
            return null;
        }
    }

    public boolean isGroupExpansionChanging() {
        if (isChildInGroup()) {
            return this.mNotificationParent.isGroupExpansionChanging();
        }
        return this.mGroupExpansionChanging;
    }

    public void setGroupExpansionChanging(boolean z) {
        this.mGroupExpansionChanging = z;
    }

    public void setActualHeightAnimating(boolean z) {
        NotificationContentView notificationContentView = this.mPrivateLayout;
        if (notificationContentView != null) {
            notificationContentView.setContentHeightAnimating(z);
        }
    }

    public NotificationContentView getPrivateLayout() {
        return this.mPrivateLayout;
    }

    public NotificationContentView getPublicLayout() {
        return this.mPublicLayout;
    }

    public void setIconAnimationRunning(boolean z) {
        for (NotificationContentView iconAnimationRunning : this.mLayouts) {
            setIconAnimationRunning(z, iconAnimationRunning);
        }
        if (this.mIsSummaryWithChildren) {
            setIconAnimationRunningForChild(z, this.mChildrenContainer.getHeaderView());
            setIconAnimationRunningForChild(z, this.mChildrenContainer.getLowPriorityHeaderView());
            List notificationChildren = this.mChildrenContainer.getNotificationChildren();
            for (int i = 0; i < notificationChildren.size(); i++) {
                ((ExpandableNotificationRow) notificationChildren.get(i)).setIconAnimationRunning(z);
            }
        }
        this.mIconAnimationRunning = z;
    }

    private void setIconAnimationRunning(boolean z, NotificationContentView notificationContentView) {
        if (notificationContentView != null) {
            View contractedChild = notificationContentView.getContractedChild();
            View expandedChild = notificationContentView.getExpandedChild();
            View headsUpChild = notificationContentView.getHeadsUpChild();
            setIconAnimationRunningForChild(z, contractedChild);
            setIconAnimationRunningForChild(z, expandedChild);
            setIconAnimationRunningForChild(z, headsUpChild);
        }
    }

    private void setIconAnimationRunningForChild(boolean z, View view) {
        if (view != null) {
            setIconRunning((ImageView) view.findViewById(16908294), z);
            setIconRunning((ImageView) view.findViewById(16909342), z);
        }
    }

    private void setIconRunning(ImageView imageView, boolean z) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                if (z) {
                    animationDrawable.start();
                } else {
                    animationDrawable.stop();
                }
            } else if (drawable instanceof AnimatedVectorDrawable) {
                AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) drawable;
                if (z) {
                    animatedVectorDrawable.start();
                } else {
                    animatedVectorDrawable.stop();
                }
            }
        }
    }

    public void setEntry(NotificationEntry notificationEntry) {
        this.mEntry = notificationEntry;
        cacheIsSystemNotification();
    }

    public void freeContentViewWhenSafe(int i) {
        $$Lambda$ExpandableNotificationRow$RmUEmS0GEHf9L7pp2cHmxPWsfmA r0 = new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ExpandableNotificationRow.this.lambda$freeContentViewWhenSafe$0$ExpandableNotificationRow(this.f$1);
            }
        };
        if (i == 1) {
            getPrivateLayout().performWhenContentInactive(0, r0);
        } else if (i == 2) {
            getPrivateLayout().performWhenContentInactive(1, r0);
        } else if (i == 4) {
            getPrivateLayout().performWhenContentInactive(2, r0);
        } else if (i == 8) {
            getPublicLayout().performWhenContentInactive(0, r0);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$freeContentViewWhenSafe$0 */
    public /* synthetic */ void lambda$freeContentViewWhenSafe$0$ExpandableNotificationRow(int i) {
        if (!isRemoved()) {
            ((RowContentBindParams) this.mRowContentBindStage.getStageParams(this.mEntry)).freeContentViews(i);
            this.mRowContentBindStage.requestRebind(this.mEntry, null);
        }
    }

    private void cacheIsSystemNotification() {
        NotificationEntry notificationEntry = this.mEntry;
        if (notificationEntry != null && notificationEntry.mIsSystemNotification == null && this.mSystemNotificationAsyncTask.getStatus() == Status.PENDING) {
            this.mSystemNotificationAsyncTask.execute(new Void[0]);
        }
    }

    public boolean getIsNonblockable() {
        boolean isNonblockable = ((NotificationBlockingHelperManager) Dependency.get(NotificationBlockingHelperManager.class)).isNonblockable(this.mEntry.getSbn().getPackageName(), this.mEntry.getChannel().getId());
        NotificationEntry notificationEntry = this.mEntry;
        if (notificationEntry != null && notificationEntry.mIsSystemNotification == null) {
            this.mSystemNotificationAsyncTask.cancel(true);
            NotificationEntry notificationEntry2 = this.mEntry;
            notificationEntry2.mIsSystemNotification = isSystemNotification(this.mContext, notificationEntry2.getSbn());
        }
        boolean isImportanceLockedByOEM = isNonblockable | this.mEntry.getChannel().isImportanceLockedByOEM() | this.mEntry.getChannel().isImportanceLockedByCriticalDeviceFunction();
        if (!isImportanceLockedByOEM) {
            NotificationEntry notificationEntry3 = this.mEntry;
            if (notificationEntry3 != null) {
                Boolean bool = notificationEntry3.mIsSystemNotification;
                if (bool != null && bool.booleanValue() && this.mEntry.getChannel() != null && !this.mEntry.getChannel().isBlockable()) {
                    return true;
                }
            }
        }
        return isImportanceLockedByOEM;
    }

    public void onNotificationUpdated() {
        for (NotificationContentView onNotificationUpdated : this.mLayouts) {
            onNotificationUpdated.onNotificationUpdated(this.mEntry);
        }
        this.mIsColorized = this.mEntry.getSbn().getNotification().isColorized();
        this.mShowingPublicInitialized = false;
        updateNotificationColor();
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null) {
            notificationMenuRowPlugin.onNotificationUpdated(this.mEntry.getSbn());
            this.mMenuRow.setAppName(this.mAppName);
        }
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.recreateNotificationHeader(this.mExpandClickListener);
            this.mChildrenContainer.onNotificationUpdated();
        }
        if (this.mIconAnimationRunning) {
            setIconAnimationRunning(true);
        }
        if (this.mLastChronometerRunning) {
            setChronometerRunning(true);
        }
        ExpandableNotificationRow expandableNotificationRow = this.mNotificationParent;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.updateChildrenHeaderAppearance();
        }
        onChildrenCountChanged();
        this.mPublicLayout.updateExpandButtons(true);
        updateLimits();
        updateIconVisibilities();
        updateShelfIconColor();
        updateRippleAllowed();
        if (this.mUpdateBackgroundOnUpdate) {
            this.mUpdateBackgroundOnUpdate = false;
            updateBackgroundColors();
        }
    }

    public void onNotificationRankingUpdated() {
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null) {
            notificationMenuRowPlugin.onNotificationUpdated(this.mEntry.getSbn());
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void updateShelfIconColor() {
        StatusBarIconView shelfIcon = this.mEntry.getIcons().getShelfIcon();
        boolean z = true;
        int i = 0;
        if (!Boolean.TRUE.equals(shelfIcon.getTag(C2011R$id.icon_is_pre_L)) || NotificationUtils.isGrayscale(shelfIcon, ContrastColorUtil.getInstance(this.mContext))) {
            NotificationHeaderView visibleNotificationHeader = getVisibleNotificationHeader();
            if (visibleNotificationHeader != null) {
                i = visibleNotificationHeader.getOriginalIconColor();
            } else {
                NotificationEntry notificationEntry = this.mEntry;
                Context context = this.mContext;
                if (!this.mIsLowPriority || isExpanded()) {
                    z = false;
                }
                i = notificationEntry.getContrastedColor(context, z, getBackgroundColorWithoutTint());
            }
        }
        shelfIcon.setStaticDrawableColor(i);
    }

    public void setAboveShelfChangedListener(AboveShelfChangedListener aboveShelfChangedListener) {
        this.mAboveShelfChangedListener = aboveShelfChangedListener;
    }

    public void setSecureStateProvider(BooleanSupplier booleanSupplier) {
        this.mSecureStateProvider = booleanSupplier;
    }

    public boolean isDimmable() {
        if (getShowingLayout().isDimmable() && !showingPulsing()) {
            return super.isDimmable();
        }
        return false;
    }

    private void updateLimits() {
        for (NotificationContentView updateLimitsForView : this.mLayouts) {
            updateLimitsForView(updateLimitsForView);
        }
    }

    private void updateLimitsForView(NotificationContentView notificationContentView) {
        boolean z = true;
        boolean z2 = (notificationContentView.getContractedChild() == null || notificationContentView.getContractedChild().getId() == 16909454) ? false : true;
        boolean z3 = this.mEntry.targetSdk < 24;
        boolean z4 = this.mEntry.targetSdk < 28;
        View expandedChild = notificationContentView.getExpandedChild();
        int i = (!z2 || !z4 || this.mIsSummaryWithChildren) ? (!(expandedChild != null && expandedChild.findViewById(16909130) != null) || !this.mMediaManager.getShowCompactMediaSeekbar()) ? (!this.mUseIncreasedCollapsedHeight || notificationContentView != this.mPrivateLayout) ? this.mNotificationMinHeight : this.mNotificationMinHeightLarge : this.mNotificationMinHeightMedia : z3 ? this.mNotificationMinHeightBeforeN : this.mNotificationMinHeightBeforeP;
        if (notificationContentView.getHeadsUpChild() == null || notificationContentView.getHeadsUpChild().getId() == 16909454) {
            z = false;
        }
        int i2 = (!z || !z4) ? (!this.mUseIncreasedHeadsUpHeight || notificationContentView != this.mPrivateLayout) ? this.mMaxHeadsUpHeight : this.mMaxHeadsUpHeightIncreased : z3 ? this.mMaxHeadsUpHeightBeforeN : this.mMaxHeadsUpHeightBeforeP;
        NotificationViewWrapper visibleWrapper = notificationContentView.getVisibleWrapper(2);
        if (visibleWrapper != null) {
            i2 = Math.max(i2, visibleWrapper.getMinLayoutHeight());
        }
        notificationContentView.setHeights(i, i2, this.mNotificationMaxHeight);
    }

    public NotificationEntry getEntry() {
        return this.mEntry;
    }

    public boolean isHeadsUp() {
        return this.mIsHeadsUp;
    }

    public void setHeadsUp(boolean z) {
        boolean isAboveShelf = isAboveShelf();
        int intrinsicHeight = getIntrinsicHeight();
        this.mIsHeadsUp = z;
        this.mPrivateLayout.setHeadsUp(z);
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.updateGroupOverflow();
        }
        if (intrinsicHeight != getIntrinsicHeight()) {
            notifyHeightChanged(false);
        }
        if (z) {
            this.mMustStayOnScreen = true;
            setAboveShelf(true);
        } else if (isAboveShelf() != isAboveShelf) {
            this.mAboveShelfChangedListener.onAboveShelfStateChanged(!isAboveShelf);
        }
    }

    public boolean showingPulsing() {
        return isHeadsUpState() && (isDozing() || (this.mOnKeyguard && isBypassEnabled()));
    }

    public boolean isHeadsUpState() {
        return this.mIsHeadsUp || this.mHeadsupDisappearRunning;
    }

    public void setRemoteInputController(RemoteInputController remoteInputController) {
        this.mPrivateLayout.setRemoteInputController(remoteInputController);
    }

    /* access modifiers changed from: 0000 */
    public String getAppName() {
        return this.mAppName;
    }

    public void setHeaderVisibleAmount(float f) {
        if (this.mHeaderVisibleAmount != f) {
            this.mHeaderVisibleAmount = f;
            for (NotificationContentView headerVisibleAmount : this.mLayouts) {
                headerVisibleAmount.setHeaderVisibleAmount(f);
            }
            NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
            if (notificationChildrenContainer != null) {
                notificationChildrenContainer.setHeaderVisibleAmount(f);
            }
            notifyHeightChanged(false);
        }
    }

    public float getHeaderVisibleAmount() {
        return this.mHeaderVisibleAmount;
    }

    public void setHeadsUpIsVisible() {
        super.setHeadsUpIsVisible();
        this.mMustStayOnScreen = false;
    }

    public void addChildNotification(ExpandableNotificationRow expandableNotificationRow, int i) {
        if (this.mChildrenContainer == null) {
            this.mChildrenContainerStub.inflate();
        }
        this.mChildrenContainer.addNotification(expandableNotificationRow, i);
        onChildrenCountChanged();
        expandableNotificationRow.setIsChildInGroup(true, this);
    }

    public void addChildNotification(NotificationListItem notificationListItem, int i) {
        addChildNotification((ExpandableNotificationRow) notificationListItem.getView(), i);
    }

    public void removeChildNotification(ExpandableNotificationRow expandableNotificationRow) {
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.removeNotification(expandableNotificationRow);
        }
        onChildrenCountChanged();
        expandableNotificationRow.setIsChildInGroup(false, null);
        expandableNotificationRow.setBottomRoundness(0.0f, false);
    }

    public void removeChildNotification(NotificationListItem notificationListItem) {
        removeChildNotification((ExpandableNotificationRow) notificationListItem.getView());
    }

    public boolean isChildInGroup() {
        return this.mNotificationParent != null;
    }

    public boolean isOnlyChildInGroup() {
        return this.mGroupManager.isOnlyChildInGroup(this.mEntry.getSbn());
    }

    public ExpandableNotificationRow getNotificationParent() {
        return this.mNotificationParent;
    }

    public void setIsChildInGroup(boolean z, ExpandableNotificationRow expandableNotificationRow) {
        if (this.mExpandAnimationRunning && !z) {
            ExpandableNotificationRow expandableNotificationRow2 = this.mNotificationParent;
            if (expandableNotificationRow2 != null) {
                expandableNotificationRow2.setChildIsExpanding(false);
                this.mNotificationParent.setExtraWidthForClipping(0.0f);
                this.mNotificationParent.setMinimumHeightForClipping(0);
            }
        }
        if (!z) {
            expandableNotificationRow = null;
        }
        this.mNotificationParent = expandableNotificationRow;
        this.mPrivateLayout.setIsChildInGroup(z);
        if (this.mIsChildInGroup != z) {
            this.mIsChildInGroup = z;
            if (!isRemoved() && this.mIsLowPriority) {
                ((RowContentBindParams) this.mRowContentBindStage.getStageParams(this.mEntry)).setUseLowPriority(this.mIsLowPriority);
                this.mRowContentBindStage.requestRebind(this.mEntry, null);
            }
        }
        resetBackgroundAlpha();
        updateBackgroundForGroupState();
        updateClickAndFocus();
        if (this.mNotificationParent != null) {
            setOverrideTintColor(0, 0.0f);
            setDistanceToTopRoundness(-1.0f);
            this.mNotificationParent.updateBackgroundForGroupState();
        }
        updateIconVisibilities();
        updateBackgroundClipping();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() != 0 || !isChildInGroup() || isGroupExpanded()) {
            return super.onTouchEvent(motionEvent);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean handleSlideBack() {
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin == null || !notificationMenuRowPlugin.isMenuVisible()) {
            return false;
        }
        animateTranslateNotification(0.0f);
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean shouldHideBackground() {
        return super.shouldHideBackground() || this.mShowNoBackground;
    }

    public boolean isSummaryWithChildren() {
        return this.mIsSummaryWithChildren;
    }

    public boolean areChildrenExpanded() {
        return this.mChildrenExpanded;
    }

    public List<ExpandableNotificationRow> getNotificationChildren() {
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer == null) {
            return null;
        }
        return notificationChildrenContainer.getNotificationChildren();
    }

    public boolean applyChildOrder(List<? extends NotificationListItem> list, VisualStabilityManager visualStabilityManager, Callback callback) {
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        return notificationChildrenContainer != null && notificationChildrenContainer.applyChildOrder(list, visualStabilityManager, callback);
    }

    public void updateChildrenStates(AmbientState ambientState) {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.updateState(getViewState(), ambientState);
        }
    }

    public void applyChildrenState() {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.applyState();
        }
    }

    public void prepareExpansionChanged() {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.prepareExpansionChanged();
        }
    }

    public void startChildAnimation(AnimationProperties animationProperties) {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.startAnimationToState(animationProperties);
        }
    }

    public ExpandableNotificationRow getViewAtPosition(float f) {
        if (!this.mIsSummaryWithChildren || !this.mChildrenExpanded) {
            return this;
        }
        ExpandableNotificationRow viewAtPosition = this.mChildrenContainer.getViewAtPosition(f);
        return viewAtPosition == null ? this : viewAtPosition;
    }

    public NotificationGuts getGuts() {
        return this.mGuts;
    }

    public void setPinned(boolean z) {
        int intrinsicHeight = getIntrinsicHeight();
        boolean isAboveShelf = isAboveShelf();
        this.mIsPinned = z;
        if (intrinsicHeight != getIntrinsicHeight()) {
            notifyHeightChanged(false);
        }
        if (z) {
            setIconAnimationRunning(true);
            this.mExpandedWhenPinned = false;
        } else if (this.mExpandedWhenPinned) {
            setUserExpanded(true);
        }
        setChronometerRunning(this.mLastChronometerRunning);
        if (isAboveShelf() != isAboveShelf) {
            this.mAboveShelfChangedListener.onAboveShelfStateChanged(!isAboveShelf);
        }
    }

    public boolean isPinned() {
        return this.mIsPinned;
    }

    public int getPinnedHeadsUpHeight() {
        return getPinnedHeadsUpHeight(true);
    }

    private int getPinnedHeadsUpHeight(boolean z) {
        if (this.mIsSummaryWithChildren) {
            return this.mChildrenContainer.getIntrinsicHeight();
        }
        if (this.mExpandedWhenPinned) {
            return Math.max(getMaxExpandHeight(), getHeadsUpHeight());
        }
        if (z) {
            return Math.max(getCollapsedHeight(), getHeadsUpHeight());
        }
        return getHeadsUpHeight();
    }

    public void setJustClicked(boolean z) {
        this.mJustClicked = z;
    }

    public boolean wasJustClicked() {
        return this.mJustClicked;
    }

    public void setChronometerRunning(boolean z) {
        this.mLastChronometerRunning = z;
        setChronometerRunning(z, this.mPrivateLayout);
        setChronometerRunning(z, this.mPublicLayout);
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            List notificationChildren = notificationChildrenContainer.getNotificationChildren();
            for (int i = 0; i < notificationChildren.size(); i++) {
                ((ExpandableNotificationRow) notificationChildren.get(i)).setChronometerRunning(z);
            }
        }
    }

    private void setChronometerRunning(boolean z, NotificationContentView notificationContentView) {
        if (notificationContentView != null) {
            boolean z2 = z || isPinned();
            View contractedChild = notificationContentView.getContractedChild();
            View expandedChild = notificationContentView.getExpandedChild();
            View headsUpChild = notificationContentView.getHeadsUpChild();
            setChronometerRunningForChild(z2, contractedChild);
            setChronometerRunningForChild(z2, expandedChild);
            setChronometerRunningForChild(z2, headsUpChild);
        }
    }

    private void setChronometerRunningForChild(boolean z, View view) {
        if (view != null) {
            View findViewById = view.findViewById(16908832);
            if (findViewById instanceof Chronometer) {
                ((Chronometer) findViewById).setStarted(z);
            }
        }
    }

    public NotificationHeaderView getNotificationHeader() {
        if (this.mIsSummaryWithChildren) {
            return this.mChildrenContainer.getHeaderView();
        }
        return this.mPrivateLayout.getNotificationHeader();
    }

    public NotificationHeaderView getVisibleNotificationHeader() {
        if (!this.mIsSummaryWithChildren || shouldShowPublic()) {
            return getShowingLayout().getVisibleNotificationHeader();
        }
        return this.mChildrenContainer.getVisibleHeader();
    }

    public void setLongPressListener(LongPressListener longPressListener) {
        this.mLongPressListener = longPressListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        super.setOnClickListener(onClickListener);
        this.mOnClickListener = onClickListener;
        updateClickAndFocus();
    }

    private void updateClickAndFocus() {
        boolean z = false;
        boolean z2 = !isChildInGroup() || isGroupExpanded();
        if (this.mOnClickListener != null && z2) {
            z = true;
        }
        if (isFocusable() != z2) {
            setFocusable(z2);
        }
        if (isClickable() != z) {
            setClickable(z);
        }
    }

    public HeadsUpManager getHeadsUpManager() {
        return this.mHeadsUpManager;
    }

    public void setGutsView(MenuItem menuItem) {
        if (this.mGuts != null && (menuItem.getGutsView() instanceof GutsContent)) {
            ((GutsContent) menuItem.getGutsView()).setGutsParent(this.mGuts);
            this.mGuts.setGutsContent((GutsContent) menuItem.getGutsView());
        }
    }

    public void onPluginConnected(NotificationMenuRowPlugin notificationMenuRowPlugin, Context context) {
        NotificationMenuRowPlugin notificationMenuRowPlugin2 = this.mMenuRow;
        boolean z = (notificationMenuRowPlugin2 == null || notificationMenuRowPlugin2.getMenuView() == null) ? false : true;
        if (z) {
            removeView(this.mMenuRow.getMenuView());
        }
        if (notificationMenuRowPlugin != null) {
            this.mMenuRow = notificationMenuRowPlugin;
            if (notificationMenuRowPlugin.shouldUseDefaultMenuItems()) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(NotificationMenuRow.createConversationItem(this.mContext));
                arrayList.add(NotificationMenuRow.createInfoItem(this.mContext));
                arrayList.add(NotificationMenuRow.createSnoozeItem(this.mContext));
                arrayList.add(NotificationMenuRow.createAppOpsItem(this.mContext));
                this.mMenuRow.setMenuItems(arrayList);
            }
            if (z) {
                createMenu();
            }
        }
    }

    public void onPluginDisconnected(NotificationMenuRowPlugin notificationMenuRowPlugin) {
        boolean z = this.mMenuRow.getMenuView() != null;
        this.mMenuRow = new NotificationMenuRow(this.mContext);
        if (z) {
            createMenu();
        }
    }

    public boolean hasFinishedInitialization() {
        return getEntry().hasFinishedInitialization();
    }

    public NotificationMenuRowPlugin createMenu() {
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin == null) {
            return null;
        }
        if (notificationMenuRowPlugin.getMenuView() == null) {
            this.mMenuRow.createMenu(this, this.mEntry.getSbn());
            this.mMenuRow.setAppName(this.mAppName);
            addView(this.mMenuRow.getMenuView(), 0, new LayoutParams(-1, -1));
        }
        return this.mMenuRow;
    }

    public NotificationMenuRowPlugin getProvider() {
        return this.mMenuRow;
    }

    public void onDensityOrFontScaleChanged() {
        super.onDensityOrFontScaleChanged();
        initDimens();
        initBackground();
        reInflateViews();
    }

    private void reInflateViews() {
        NotificationContentView[] notificationContentViewArr;
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.reInflateViews(this.mExpandClickListener, this.mEntry.getSbn());
        }
        NotificationGuts notificationGuts = this.mGuts;
        if (notificationGuts != null) {
            int indexOfChild = indexOfChild(notificationGuts);
            removeView(notificationGuts);
            NotificationGuts notificationGuts2 = (NotificationGuts) LayoutInflater.from(this.mContext).inflate(C2013R$layout.notification_guts, this, false);
            this.mGuts = notificationGuts2;
            notificationGuts2.setVisibility(notificationGuts.isExposed() ? 0 : 8);
            addView(this.mGuts, indexOfChild);
        }
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        View menuView = notificationMenuRowPlugin == null ? null : notificationMenuRowPlugin.getMenuView();
        if (menuView != null) {
            int indexOfChild2 = indexOfChild(menuView);
            removeView(menuView);
            this.mMenuRow.createMenu(this, this.mEntry.getSbn());
            this.mMenuRow.setAppName(this.mAppName);
            addView(this.mMenuRow.getMenuView(), indexOfChild2);
        }
        for (NotificationContentView notificationContentView : this.mLayouts) {
            notificationContentView.initView();
            notificationContentView.reInflateViews();
        }
        this.mEntry.getSbn().clearPackageContext();
        ((RowContentBindParams) this.mRowContentBindStage.getStageParams(this.mEntry)).setNeedsReinflation(true);
        this.mRowContentBindStage.requestRebind(this.mEntry, null);
    }

    public void onConfigurationChanged(Configuration configuration) {
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (!(notificationMenuRowPlugin == null || notificationMenuRowPlugin.getMenuView() == null)) {
            this.mMenuRow.onConfigurationChanged();
        }
        NotificationInlineImageResolver notificationInlineImageResolver = this.mImageResolver;
        if (notificationInlineImageResolver != null) {
            notificationInlineImageResolver.updateMaxImageSizes();
        }
    }

    public void onUiModeChanged() {
        this.mUpdateBackgroundOnUpdate = true;
        reInflateViews();
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            for (ExpandableNotificationRow onUiModeChanged : notificationChildrenContainer.getNotificationChildren()) {
                onUiModeChanged.onUiModeChanged();
            }
        }
    }

    public void setContentBackground(int i, boolean z, NotificationContentView notificationContentView) {
        if (getShowingLayout() == notificationContentView) {
            setTintColor(i, z);
        }
    }

    /* access modifiers changed from: protected */
    public void setBackgroundTintColor(int i) {
        super.setBackgroundTintColor(i);
        NotificationContentView showingLayout = getShowingLayout();
        if (showingLayout != null) {
            showingLayout.setBackgroundTintColor(i);
        }
    }

    public void closeRemoteInput() {
        for (NotificationContentView closeRemoteInput : this.mLayouts) {
            closeRemoteInput.closeRemoteInput();
        }
    }

    public void setSingleLineWidthIndention(int i) {
        this.mPrivateLayout.setSingleLineWidthIndention(i);
    }

    public int getNotificationColor() {
        return this.mNotificationColor;
    }

    private void updateNotificationColor() {
        this.mNotificationColor = ContrastColorUtil.resolveContrastColor(this.mContext, this.mEntry.getSbn().getNotification().color, getBackgroundColorWithoutTint(), (getResources().getConfiguration().uiMode & 48) == 32);
    }

    public HybridNotificationView getSingleLineView() {
        return this.mPrivateLayout.getSingleLineView();
    }

    public boolean isOnKeyguard() {
        return this.mOnKeyguard;
    }

    public void removeAllChildren() {
        ArrayList arrayList = new ArrayList(this.mChildrenContainer.getNotificationChildren());
        for (int i = 0; i < arrayList.size(); i++) {
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) arrayList.get(i);
            if (!expandableNotificationRow.keepInParent()) {
                this.mChildrenContainer.removeNotification(expandableNotificationRow);
                expandableNotificationRow.setIsChildInGroup(false, null);
            }
        }
        onChildrenCountChanged();
    }

    public void dismiss(boolean z) {
        super.dismiss(z);
        setLongPressListener(null);
        this.mGroupParentWhenDismissed = this.mNotificationParent;
        this.mChildAfterViewWhenDismissed = null;
        this.mEntry.getIcons().getStatusBarIcon().setDismissed();
        if (isChildInGroup()) {
            List notificationChildren = this.mNotificationParent.getNotificationChildren();
            int indexOf = notificationChildren.indexOf(this);
            if (indexOf != -1 && indexOf < notificationChildren.size() - 1) {
                this.mChildAfterViewWhenDismissed = (View) notificationChildren.get(indexOf + 1);
            }
        }
    }

    public boolean keepInParent() {
        return this.mKeepInParent;
    }

    public void setKeepInParent(boolean z) {
        this.mKeepInParent = z;
    }

    public boolean isRemoved() {
        return this.mRemoved;
    }

    public void setRemoved() {
        this.mRemoved = true;
        this.mTranslationWhenRemoved = getTranslationY();
        this.mWasChildInGroupWhenRemoved = isChildInGroup();
        if (isChildInGroup()) {
            this.mTranslationWhenRemoved += getNotificationParent().getTranslationY();
        }
        for (NotificationContentView removed : this.mLayouts) {
            removed.setRemoved();
        }
    }

    public boolean wasChildInGroupWhenRemoved() {
        return this.mWasChildInGroupWhenRemoved;
    }

    public float getTranslationWhenRemoved() {
        return this.mTranslationWhenRemoved;
    }

    public NotificationChildrenContainer getChildrenContainer() {
        return this.mChildrenContainer;
    }

    public void setHeadsUpAnimatingAway(boolean z) {
        boolean isAboveShelf = isAboveShelf();
        boolean z2 = z != this.mHeadsupDisappearRunning;
        this.mHeadsupDisappearRunning = z;
        this.mPrivateLayout.setHeadsUpAnimatingAway(z);
        if (z2) {
            Consumer<Boolean> consumer = this.mHeadsUpAnimatingAwayListener;
            if (consumer != null) {
                consumer.accept(Boolean.valueOf(z));
            }
        }
        if (isAboveShelf() != isAboveShelf) {
            this.mAboveShelfChangedListener.onAboveShelfStateChanged(!isAboveShelf);
        }
    }

    public void setHeadsUpAnimatingAwayListener(Consumer<Boolean> consumer) {
        this.mHeadsUpAnimatingAwayListener = consumer;
    }

    public boolean isHeadsUpAnimatingAway() {
        return this.mHeadsupDisappearRunning;
    }

    public View getChildAfterViewWhenDismissed() {
        return this.mChildAfterViewWhenDismissed;
    }

    public View getGroupParentWhenDismissed() {
        return this.mGroupParentWhenDismissed;
    }

    public boolean performDismissWithBlockingHelper(boolean z) {
        boolean perhapsShowBlockingHelper = ((NotificationBlockingHelperManager) Dependency.get(NotificationBlockingHelperManager.class)).perhapsShowBlockingHelper(this, this.mMenuRow);
        ((MetricsLogger) Dependency.get(MetricsLogger.class)).count("notification_dismissed", 1);
        performDismiss(z);
        return perhapsShowBlockingHelper;
    }

    public void performDismiss(boolean z) {
        if (isOnlyChildInGroup()) {
            NotificationEntry logicalGroupSummary = this.mGroupManager.getLogicalGroupSummary(this.mEntry.getSbn());
            if (logicalGroupSummary.isClearable()) {
                logicalGroupSummary.getRow().performDismiss(z);
            }
        }
        dismiss(z);
        if (this.mEntry.isClearable()) {
            Runnable runnable = this.mOnDismissRunnable;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public void setBlockingHelperShowing(boolean z) {
        this.mIsBlockingHelperShowing = z;
    }

    public boolean isBlockingHelperShowing() {
        return this.mIsBlockingHelperShowing;
    }

    public boolean isBlockingHelperShowingAndTranslationFinished() {
        return this.mIsBlockingHelperShowing && this.mNotificationTranslationFinished;
    }

    /* access modifiers changed from: 0000 */
    public void setOnDismissRunnable(Runnable runnable) {
        this.mOnDismissRunnable = runnable;
    }

    public View getNotificationIcon() {
        NotificationHeaderView visibleNotificationHeader = getVisibleNotificationHeader();
        if (visibleNotificationHeader != null) {
            return visibleNotificationHeader.getIcon();
        }
        return null;
    }

    public boolean isShowingIcon() {
        boolean z = false;
        if (areGutsExposed()) {
            return false;
        }
        if (getVisibleNotificationHeader() != null) {
            z = true;
        }
        return z;
    }

    public void setContentTransformationAmount(float f, boolean z) {
        boolean z2 = true;
        boolean z3 = z != this.mIsLastChild;
        if (this.mContentTransformationAmount == f) {
            z2 = false;
        }
        boolean z4 = z3 | z2;
        this.mIsLastChild = z;
        this.mContentTransformationAmount = f;
        if (z4) {
            updateContentTransformation();
        }
    }

    public void setIconsVisible(boolean z) {
        if (z != this.mIconsVisible) {
            this.mIconsVisible = z;
            updateIconVisibilities();
        }
    }

    /* access modifiers changed from: protected */
    public void onBelowSpeedBumpChanged() {
        updateIconVisibilities();
    }

    /* access modifiers changed from: protected */
    public void updateContentTransformation() {
        if (!this.mExpandAnimationRunning) {
            super.updateContentTransformation();
        }
    }

    /* access modifiers changed from: protected */
    public void applyContentTransformation(float f, float f2) {
        NotificationContentView[] notificationContentViewArr;
        super.applyContentTransformation(f, f2);
        if (!this.mIsLastChild) {
            f = 1.0f;
        }
        for (NotificationContentView notificationContentView : this.mLayouts) {
            notificationContentView.setAlpha(f);
            notificationContentView.setTranslationY(f2);
        }
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.setAlpha(f);
            this.mChildrenContainer.setTranslationY(f2);
        }
    }

    private void updateIconVisibilities() {
        boolean z = isChildInGroup() || this.mIconsVisible;
        for (NotificationContentView iconsVisible : this.mLayouts) {
            iconsVisible.setIconsVisible(z);
        }
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.setIconsVisible(z);
        }
    }

    public int getRelativeTopPadding(View view) {
        int i = 0;
        while (view.getParent() instanceof ViewGroup) {
            i += view.getTop();
            view = (View) view.getParent();
            if (view instanceof ExpandableNotificationRow) {
                break;
            }
        }
        return i;
    }

    public float getContentTranslation() {
        return this.mPrivateLayout.getTranslationY();
    }

    public void setIsLowPriority(boolean z) {
        this.mIsLowPriority = z;
        this.mPrivateLayout.setIsLowPriority(z);
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.setIsLowPriority(z);
        }
    }

    public boolean isLowPriority() {
        return this.mIsLowPriority;
    }

    public void setUsesIncreasedCollapsedHeight(boolean z) {
        this.mUseIncreasedCollapsedHeight = z;
    }

    public void setUsesIncreasedHeadsUpHeight(boolean z) {
        this.mUseIncreasedHeadsUpHeight = z;
    }

    public void setNeedsRedaction(boolean z) {
        if (this.mNeedsRedaction != z) {
            this.mNeedsRedaction = z;
            if (!isRemoved()) {
                RowContentBindParams rowContentBindParams = (RowContentBindParams) this.mRowContentBindStage.getStageParams(this.mEntry);
                if (z) {
                    rowContentBindParams.requireContentViews(8);
                } else {
                    rowContentBindParams.freeContentViews(8);
                }
                this.mRowContentBindStage.requestRebind(this.mEntry, null);
            }
        }
    }

    public ExpandableNotificationRow(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mImageResolver = new NotificationInlineImageResolver(context, new NotificationInlineImageCache());
        initDimens();
    }

    public void initialize(String str, String str2, ExpansionLogger expansionLogger, KeyguardBypassController keyguardBypassController, NotificationGroupManager notificationGroupManager, HeadsUpManager headsUpManager, RowContentBindStage rowContentBindStage, OnExpandClickListener onExpandClickListener, NotificationMediaManager notificationMediaManager, OnAppOpsClickListener onAppOpsClickListener, FalsingManager falsingManager, StatusBarStateController statusBarStateController) {
        this.mAppName = str;
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (!(notificationMenuRowPlugin == null || notificationMenuRowPlugin.getMenuView() == null)) {
            this.mMenuRow.setAppName(this.mAppName);
        }
        this.mLogger = expansionLogger;
        this.mLoggingKey = str2;
        this.mBypassController = keyguardBypassController;
        this.mGroupManager = notificationGroupManager;
        this.mPrivateLayout.setGroupManager(notificationGroupManager);
        this.mHeadsUpManager = headsUpManager;
        this.mRowContentBindStage = rowContentBindStage;
        this.mOnExpandClickListener = onExpandClickListener;
        this.mMediaManager = notificationMediaManager;
        setAppOpsOnClickListener(onAppOpsClickListener);
        this.mFalsingManager = falsingManager;
        this.mStatusbarStateController = statusBarStateController;
    }

    private void initDimens() {
        this.mNotificationMinHeightBeforeN = NotificationUtils.getFontScaledHeight(this.mContext, C2009R$dimen.notification_min_height_legacy);
        this.mNotificationMinHeightBeforeP = NotificationUtils.getFontScaledHeight(this.mContext, C2009R$dimen.notification_min_height_before_p);
        this.mNotificationMinHeight = NotificationUtils.getFontScaledHeight(this.mContext, C2009R$dimen.notification_min_height);
        this.mNotificationMinHeightLarge = NotificationUtils.getFontScaledHeight(this.mContext, C2009R$dimen.notification_min_height_increased);
        this.mNotificationMinHeightMedia = NotificationUtils.getFontScaledHeight(this.mContext, C2009R$dimen.notification_min_height_media);
        this.mNotificationMaxHeight = NotificationUtils.getFontScaledHeight(this.mContext, C2009R$dimen.notification_max_height);
        this.mMaxHeadsUpHeightBeforeN = NotificationUtils.getFontScaledHeight(this.mContext, C2009R$dimen.notification_max_heads_up_height_legacy);
        this.mMaxHeadsUpHeightBeforeP = NotificationUtils.getFontScaledHeight(this.mContext, C2009R$dimen.notification_max_heads_up_height_before_p);
        this.mMaxHeadsUpHeight = NotificationUtils.getFontScaledHeight(this.mContext, C2009R$dimen.notification_max_heads_up_height);
        this.mMaxHeadsUpHeightIncreased = NotificationUtils.getFontScaledHeight(this.mContext, C2009R$dimen.notification_max_heads_up_height_increased);
        Resources resources = getResources();
        this.mIncreasedPaddingBetweenElements = resources.getDimensionPixelSize(C2009R$dimen.notification_divider_height_increased);
        this.mEnableNonGroupedNotificationExpand = resources.getBoolean(C2007R$bool.config_enableNonGroupedNotificationExpand);
        this.mShowGroupBackgroundWhenExpanded = resources.getBoolean(C2007R$bool.config_showGroupNotificationBgWhenExpanded);
    }

    /* access modifiers changed from: 0000 */
    public NotificationInlineImageResolver getImageResolver() {
        return this.mImageResolver;
    }

    public void reset() {
        this.mShowingPublicInitialized = false;
        unDismiss();
        resetTranslation();
        onHeightReset();
        requestLayout();
    }

    public void showAppOpsIcons(ArraySet<Integer> arraySet) {
        if (this.mIsSummaryWithChildren && this.mChildrenContainer.getHeaderView() != null) {
            this.mChildrenContainer.getHeaderView().showAppOpsIcons(arraySet);
        }
        this.mPrivateLayout.showAppOpsIcons(arraySet);
        this.mPublicLayout.showAppOpsIcons(arraySet);
    }

    public void setLastAudiblyAlertedMs(long j) {
        if (NotificationUtils.useNewInterruptionModel(this.mContext)) {
            long currentTimeMillis = System.currentTimeMillis() - j;
            boolean z = currentTimeMillis < RECENTLY_ALERTED_THRESHOLD_MS;
            applyAudiblyAlertedRecently(z);
            removeCallbacks(this.mExpireRecentlyAlertedFlag);
            if (z) {
                postDelayed(this.mExpireRecentlyAlertedFlag, RECENTLY_ALERTED_THRESHOLD_MS - currentTimeMillis);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$ExpandableNotificationRow() {
        applyAudiblyAlertedRecently(false);
    }

    private void applyAudiblyAlertedRecently(boolean z) {
        if (this.mIsSummaryWithChildren && this.mChildrenContainer.getHeaderView() != null) {
            this.mChildrenContainer.getHeaderView().setRecentlyAudiblyAlerted(z);
        }
        this.mPrivateLayout.setRecentlyAudiblyAlerted(z);
        this.mPublicLayout.setRecentlyAudiblyAlerted(z);
    }

    public OnClickListener getAppOpsOnClickListener() {
        return this.mOnAppOpsClickListener;
    }

    /* access modifiers changed from: 0000 */
    public void setAppOpsOnClickListener(OnAppOpsClickListener onAppOpsClickListener) {
        this.mOnAppOpsClickListener = new OnClickListener(onAppOpsClickListener) {
            public final /* synthetic */ OnAppOpsClickListener f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ExpandableNotificationRow.this.lambda$setAppOpsOnClickListener$2$ExpandableNotificationRow(this.f$1, view);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setAppOpsOnClickListener$2 */
    public /* synthetic */ void lambda$setAppOpsOnClickListener$2$ExpandableNotificationRow(OnAppOpsClickListener onAppOpsClickListener, View view) {
        createMenu();
        NotificationMenuRowPlugin provider = getProvider();
        if (provider != null) {
            MenuItem appOpsMenuItem = provider.getAppOpsMenuItem(this.mContext);
            if (appOpsMenuItem != null) {
                onAppOpsClickListener.onClick(this, view.getWidth() / 2, view.getHeight() / 2, appOpsMenuItem);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mPublicLayout = (NotificationContentView) findViewById(C2011R$id.expandedPublic);
        NotificationContentView notificationContentView = (NotificationContentView) findViewById(C2011R$id.expanded);
        this.mPrivateLayout = notificationContentView;
        NotificationContentView[] notificationContentViewArr = {notificationContentView, this.mPublicLayout};
        this.mLayouts = notificationContentViewArr;
        for (NotificationContentView notificationContentView2 : notificationContentViewArr) {
            notificationContentView2.setExpandClickListener(this.mExpandClickListener);
            notificationContentView2.setContainingNotification(this);
        }
        ViewStub viewStub = (ViewStub) findViewById(C2011R$id.notification_guts_stub);
        this.mGutsStub = viewStub;
        viewStub.setOnInflateListener(new OnInflateListener() {
            public void onInflate(ViewStub viewStub, View view) {
                ExpandableNotificationRow.this.mGuts = (NotificationGuts) view;
                ExpandableNotificationRow.this.mGuts.setClipTopAmount(ExpandableNotificationRow.this.getClipTopAmount());
                ExpandableNotificationRow.this.mGuts.setActualHeight(ExpandableNotificationRow.this.getActualHeight());
                ExpandableNotificationRow.this.mGutsStub = null;
            }
        });
        ViewStub viewStub2 = (ViewStub) findViewById(C2011R$id.child_container_stub);
        this.mChildrenContainerStub = viewStub2;
        viewStub2.setOnInflateListener(new OnInflateListener() {
            public void onInflate(ViewStub viewStub, View view) {
                ExpandableNotificationRow.this.mChildrenContainer = (NotificationChildrenContainer) view;
                ExpandableNotificationRow.this.mChildrenContainer.setIsLowPriority(ExpandableNotificationRow.this.mIsLowPriority);
                ExpandableNotificationRow.this.mChildrenContainer.setContainingNotification(ExpandableNotificationRow.this);
                ExpandableNotificationRow.this.mChildrenContainer.onNotificationUpdated();
                ExpandableNotificationRow expandableNotificationRow = ExpandableNotificationRow.this;
                if (expandableNotificationRow.mShouldTranslateContents) {
                    expandableNotificationRow.mTranslateableViews.add(ExpandableNotificationRow.this.mChildrenContainer);
                }
            }
        });
        if (this.mShouldTranslateContents) {
            this.mTranslateableViews = new ArrayList<>();
            for (int i = 0; i < getChildCount(); i++) {
                this.mTranslateableViews.add(getChildAt(i));
            }
            this.mTranslateableViews.remove(this.mChildrenContainerStub);
            this.mTranslateableViews.remove(this.mGutsStub);
        }
    }

    private void doLongClickCallback() {
        doLongClickCallback(getWidth() / 2, getHeight() / 2);
    }

    public void doLongClickCallback(int i, int i2) {
        createMenu();
        NotificationMenuRowPlugin provider = getProvider();
        doLongClickCallback(i, i2, provider != null ? provider.getLongpressMenuItem(this.mContext) : null);
    }

    private void doLongClickCallback(int i, int i2, MenuItem menuItem) {
        LongPressListener longPressListener = this.mLongPressListener;
        if (longPressListener != null && menuItem != null) {
            longPressListener.onLongPress(this, i, i2, menuItem);
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (!KeyEvent.isConfirmKey(i)) {
            return super.onKeyDown(i, keyEvent);
        }
        keyEvent.startTracking();
        return true;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (!KeyEvent.isConfirmKey(i)) {
            return super.onKeyUp(i, keyEvent);
        }
        if (!keyEvent.isCanceled()) {
            performClick();
        }
        return true;
    }

    public boolean onKeyLongPress(int i, KeyEvent keyEvent) {
        if (!KeyEvent.isConfirmKey(i)) {
            return false;
        }
        doLongClickCallback();
        return true;
    }

    public void resetTranslation() {
        Animator animator = this.mTranslateAnim;
        if (animator != null) {
            animator.cancel();
        }
        if (!this.mShouldTranslateContents) {
            setTranslationX(0.0f);
        } else if (this.mTranslateableViews != null) {
            for (int i = 0; i < this.mTranslateableViews.size(); i++) {
                ((View) this.mTranslateableViews.get(i)).setTranslationX(0.0f);
            }
            invalidateOutline();
            getEntry().getIcons().getShelfIcon().setScrollX(0);
        }
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null) {
            notificationMenuRowPlugin.resetMenu();
        }
    }

    /* access modifiers changed from: 0000 */
    public void onGutsOpened() {
        resetTranslation();
        updateContentAccessibilityImportanceForGuts(false);
    }

    /* access modifiers changed from: 0000 */
    public void onGutsClosed() {
        updateContentAccessibilityImportanceForGuts(true);
    }

    private void updateContentAccessibilityImportanceForGuts(boolean z) {
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            updateChildAccessibilityImportance(notificationChildrenContainer, z);
        }
        NotificationContentView[] notificationContentViewArr = this.mLayouts;
        if (notificationContentViewArr != null) {
            for (NotificationContentView updateChildAccessibilityImportance : notificationContentViewArr) {
                updateChildAccessibilityImportance(updateChildAccessibilityImportance, z);
            }
        }
        if (z) {
            requestAccessibilityFocus();
        }
    }

    private void updateChildAccessibilityImportance(View view, boolean z) {
        view.setImportantForAccessibility(z ? 0 : 4);
    }

    public CharSequence getActiveRemoteInputText() {
        return this.mPrivateLayout.getActiveRemoteInputText();
    }

    public void animateTranslateNotification(float f) {
        Animator animator = this.mTranslateAnim;
        if (animator != null) {
            animator.cancel();
        }
        Animator translateViewAnimator = getTranslateViewAnimator(f, null);
        this.mTranslateAnim = translateViewAnimator;
        if (translateViewAnimator != null) {
            translateViewAnimator.start();
        }
    }

    public void setTranslation(float f) {
        if (isBlockingHelperShowingAndTranslationFinished()) {
            this.mGuts.setTranslationX(f);
            return;
        }
        if (!this.mShouldTranslateContents) {
            setTranslationX(f);
        } else if (this.mTranslateableViews != null) {
            for (int i = 0; i < this.mTranslateableViews.size(); i++) {
                if (this.mTranslateableViews.get(i) != null) {
                    ((View) this.mTranslateableViews.get(i)).setTranslationX(f);
                }
            }
            invalidateOutline();
            getEntry().getIcons().getShelfIcon().setScrollX((int) (-f));
        }
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (!(notificationMenuRowPlugin == null || notificationMenuRowPlugin.getMenuView() == null)) {
            this.mMenuRow.onParentTranslationUpdate(f);
        }
    }

    public float getTranslation() {
        if (!this.mShouldTranslateContents) {
            return getTranslationX();
        }
        if (isBlockingHelperShowingAndCanTranslate()) {
            return this.mGuts.getTranslationX();
        }
        ArrayList<View> arrayList = this.mTranslateableViews;
        if (arrayList == null || arrayList.size() <= 0) {
            return 0.0f;
        }
        return ((View) this.mTranslateableViews.get(0)).getTranslationX();
    }

    private boolean isBlockingHelperShowingAndCanTranslate() {
        return areGutsExposed() && this.mIsBlockingHelperShowing && this.mNotificationTranslationFinished;
    }

    public Animator getTranslateViewAnimator(final float f, AnimatorUpdateListener animatorUpdateListener) {
        Animator animator = this.mTranslateAnim;
        if (animator != null) {
            animator.cancel();
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, TRANSLATE_CONTENT, new float[]{f});
        if (animatorUpdateListener != null) {
            ofFloat.addUpdateListener(animatorUpdateListener);
        }
        ofFloat.addListener(new AnimatorListenerAdapter() {
            boolean cancelled = false;

            public void onAnimationCancel(Animator animator) {
                this.cancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                if (ExpandableNotificationRow.this.mIsBlockingHelperShowing) {
                    ExpandableNotificationRow.this.mNotificationTranslationFinished = true;
                }
                if (!this.cancelled && f == 0.0f) {
                    if (ExpandableNotificationRow.this.mMenuRow != null) {
                        ExpandableNotificationRow.this.mMenuRow.resetMenu();
                    }
                    ExpandableNotificationRow.this.mTranslateAnim = null;
                }
            }
        });
        this.mTranslateAnim = ofFloat;
        return ofFloat;
    }

    /* access modifiers changed from: 0000 */
    public void ensureGutsInflated() {
        if (this.mGuts == null) {
            this.mGutsStub.inflate();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0029  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateChildrenVisibility() {
        /*
            r5 = this;
            boolean r0 = r5.mExpandAnimationRunning
            r1 = 0
            if (r0 == 0) goto L_0x0011
            com.android.systemui.statusbar.notification.row.NotificationGuts r0 = r5.mGuts
            if (r0 == 0) goto L_0x0011
            boolean r0 = r0.isExposed()
            if (r0 == 0) goto L_0x0011
            r0 = 1
            goto L_0x0012
        L_0x0011:
            r0 = r1
        L_0x0012:
            com.android.systemui.statusbar.notification.row.NotificationContentView r2 = r5.mPrivateLayout
            boolean r3 = r5.mShowingPublic
            r4 = 4
            if (r3 != 0) goto L_0x0021
            boolean r3 = r5.mIsSummaryWithChildren
            if (r3 != 0) goto L_0x0021
            if (r0 != 0) goto L_0x0021
            r3 = r1
            goto L_0x0022
        L_0x0021:
            r3 = r4
        L_0x0022:
            r2.setVisibility(r3)
            com.android.systemui.statusbar.notification.stack.NotificationChildrenContainer r2 = r5.mChildrenContainer
            if (r2 == 0) goto L_0x0038
            boolean r3 = r5.mShowingPublic
            if (r3 != 0) goto L_0x0034
            boolean r3 = r5.mIsSummaryWithChildren
            if (r3 == 0) goto L_0x0034
            if (r0 != 0) goto L_0x0034
            goto L_0x0035
        L_0x0034:
            r1 = r4
        L_0x0035:
            r2.setVisibility(r1)
        L_0x0038:
            r5.updateLimits()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.updateChildrenVisibility():void");
    }

    public boolean onRequestSendAccessibilityEventInternal(View view, AccessibilityEvent accessibilityEvent) {
        if (!super.onRequestSendAccessibilityEventInternal(view, accessibilityEvent)) {
            return false;
        }
        AccessibilityEvent obtain = AccessibilityEvent.obtain();
        onInitializeAccessibilityEvent(obtain);
        dispatchPopulateAccessibilityEvent(obtain);
        accessibilityEvent.appendRecord(obtain);
        return true;
    }

    public void applyExpandAnimationParams(ExpandAnimationParameters expandAnimationParameters) {
        if (expandAnimationParameters != null) {
            float lerp = MathUtils.lerp(expandAnimationParameters.getStartTranslationZ(), (float) this.mNotificationLaunchHeight, Interpolators.FAST_OUT_SLOW_IN.getInterpolation(expandAnimationParameters.getProgress(0, 50)));
            setTranslationZ(lerp);
            float width = ((float) (expandAnimationParameters.getWidth() - getWidth())) + MathUtils.lerp(0.0f, this.mOutlineRadius * 2.0f, expandAnimationParameters.getProgress());
            setExtraWidthForClipping(width);
            int top = expandAnimationParameters.getTop();
            float interpolation = Interpolators.FAST_OUT_SLOW_IN.getInterpolation(expandAnimationParameters.getProgress());
            int startClipTopAmount = expandAnimationParameters.getStartClipTopAmount();
            ExpandableNotificationRow expandableNotificationRow = this.mNotificationParent;
            if (expandableNotificationRow != null) {
                float translationY = expandableNotificationRow.getTranslationY();
                top = (int) (((float) top) - translationY);
                this.mNotificationParent.setTranslationZ(lerp);
                int parentStartClipTopAmount = expandAnimationParameters.getParentStartClipTopAmount();
                if (startClipTopAmount != 0) {
                    this.mNotificationParent.setClipTopAmount((int) MathUtils.lerp((float) parentStartClipTopAmount, (float) (parentStartClipTopAmount - startClipTopAmount), interpolation));
                }
                this.mNotificationParent.setExtraWidthForClipping(width);
                this.mNotificationParent.setMinimumHeightForClipping((int) (Math.max((float) expandAnimationParameters.getBottom(), (((float) this.mNotificationParent.getActualHeight()) + translationY) - ((float) this.mNotificationParent.getClipBottomAmount())) - Math.min((float) expandAnimationParameters.getTop(), translationY)));
            } else if (startClipTopAmount != 0) {
                setClipTopAmount((int) MathUtils.lerp((float) startClipTopAmount, 0.0f, interpolation));
            }
            setTranslationY((float) top);
            setActualHeight(expandAnimationParameters.getHeight());
            this.mBackgroundNormal.setExpandAnimationParams(expandAnimationParameters);
        }
    }

    public void setExpandAnimationRunning(boolean z) {
        View view;
        if (this.mIsSummaryWithChildren) {
            view = this.mChildrenContainer;
        } else {
            view = getShowingLayout();
        }
        NotificationGuts notificationGuts = this.mGuts;
        if (notificationGuts != null && notificationGuts.isExposed()) {
            view = this.mGuts;
        }
        if (z) {
            view.animate().alpha(0.0f).setDuration(67).setInterpolator(Interpolators.ALPHA_OUT);
            setAboveShelf(true);
            this.mExpandAnimationRunning = true;
            getViewState().cancelAnimations(this);
            this.mNotificationLaunchHeight = AmbientState.getNotificationLaunchHeight(getContext());
        } else {
            this.mExpandAnimationRunning = false;
            setAboveShelf(isAboveShelf());
            NotificationGuts notificationGuts2 = this.mGuts;
            if (notificationGuts2 != null) {
                notificationGuts2.setAlpha(1.0f);
            }
            if (view != null) {
                view.setAlpha(1.0f);
            }
            setExtraWidthForClipping(0.0f);
            ExpandableNotificationRow expandableNotificationRow = this.mNotificationParent;
            if (expandableNotificationRow != null) {
                expandableNotificationRow.setExtraWidthForClipping(0.0f);
                this.mNotificationParent.setMinimumHeightForClipping(0);
            }
        }
        ExpandableNotificationRow expandableNotificationRow2 = this.mNotificationParent;
        if (expandableNotificationRow2 != null) {
            expandableNotificationRow2.setChildIsExpanding(this.mExpandAnimationRunning);
        }
        updateChildrenVisibility();
        updateClipping();
        this.mBackgroundNormal.setExpandAnimationRunning(z);
    }

    private void setChildIsExpanding(boolean z) {
        this.mChildIsExpanding = z;
        updateClipping();
        invalidate();
    }

    public boolean hasExpandingChild() {
        return this.mChildIsExpanding;
    }

    public StatusBarIconView getShelfIcon() {
        return getEntry().getIcons().getShelfIcon();
    }

    /* access modifiers changed from: protected */
    public boolean shouldClipToActualHeight() {
        return super.shouldClipToActualHeight() && !this.mExpandAnimationRunning;
    }

    public boolean isExpandAnimationRunning() {
        return this.mExpandAnimationRunning;
    }

    public boolean isSoundEffectsEnabled() {
        boolean z;
        StatusBarStateController statusBarStateController = this.mStatusbarStateController;
        if (statusBarStateController != null && statusBarStateController.isDozing()) {
            BooleanSupplier booleanSupplier = this.mSecureStateProvider;
            if (booleanSupplier != null && !booleanSupplier.getAsBoolean()) {
                z = true;
                if (!z || !super.isSoundEffectsEnabled()) {
                    return false;
                }
                return true;
            }
        }
        z = false;
        if (!z) {
        }
        return false;
    }

    public boolean isExpandable() {
        boolean z = true;
        if (this.mIsSummaryWithChildren && !shouldShowPublic()) {
            return !this.mChildrenExpanded;
        }
        if (!this.mEnableNonGroupedNotificationExpand || !this.mExpandable) {
            z = false;
        }
        return z;
    }

    public void setExpandable(boolean z) {
        this.mExpandable = z;
        this.mPrivateLayout.updateExpandButtons(isExpandable());
    }

    public void setClipToActualHeight(boolean z) {
        boolean z2 = false;
        super.setClipToActualHeight(z || isUserLocked());
        NotificationContentView showingLayout = getShowingLayout();
        if (z || isUserLocked()) {
            z2 = true;
        }
        showingLayout.setClipToActualHeight(z2);
    }

    public boolean hasUserChangedExpansion() {
        return this.mHasUserChangedExpansion;
    }

    public boolean isUserExpanded() {
        return this.mUserExpanded;
    }

    public void setUserExpanded(boolean z) {
        setUserExpanded(z, false);
    }

    public void setUserExpanded(boolean z, boolean z2) {
        this.mFalsingManager.setNotificationExpanded();
        if (this.mIsSummaryWithChildren && !shouldShowPublic() && z2 && !this.mChildrenContainer.showingAsLowPriority()) {
            boolean isGroupExpanded = this.mGroupManager.isGroupExpanded(this.mEntry.getSbn());
            this.mGroupManager.setGroupExpanded(this.mEntry.getSbn(), z);
            onExpansionChanged(true, isGroupExpanded);
        } else if (!z || this.mExpandable) {
            boolean isExpanded = isExpanded();
            this.mHasUserChangedExpansion = true;
            this.mUserExpanded = z;
            onExpansionChanged(true, isExpanded);
            if (!isExpanded && isExpanded() && getActualHeight() != getIntrinsicHeight()) {
                notifyHeightChanged(true);
            }
        }
    }

    public void resetUserExpansion() {
        boolean isExpanded = isExpanded();
        this.mHasUserChangedExpansion = false;
        this.mUserExpanded = false;
        if (isExpanded != isExpanded()) {
            if (this.mIsSummaryWithChildren) {
                this.mChildrenContainer.onExpansionChanged();
            }
            notifyHeightChanged(false);
        }
        updateShelfIconColor();
    }

    public boolean isUserLocked() {
        return this.mUserLocked && !this.mForceUnlocked;
    }

    public void setUserLocked(boolean z) {
        this.mUserLocked = z;
        this.mPrivateLayout.setUserExpanding(z);
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.setUserLocked(z);
            if (!this.mIsSummaryWithChildren) {
                return;
            }
            if (z || !isGroupExpanded()) {
                updateBackgroundForGroupState();
            }
        }
    }

    public boolean isSystemExpanded() {
        return this.mIsSystemExpanded;
    }

    public void setSystemExpanded(boolean z) {
        if (z != this.mIsSystemExpanded) {
            boolean isExpanded = isExpanded();
            this.mIsSystemExpanded = z;
            notifyHeightChanged(false);
            onExpansionChanged(false, isExpanded);
            if (this.mIsSummaryWithChildren) {
                this.mChildrenContainer.updateGroupOverflow();
            }
        }
    }

    public void setOnKeyguard(boolean z) {
        if (z != this.mOnKeyguard) {
            boolean isAboveShelf = isAboveShelf();
            boolean isExpanded = isExpanded();
            this.mOnKeyguard = z;
            onExpansionChanged(false, isExpanded);
            if (isExpanded != isExpanded()) {
                if (this.mIsSummaryWithChildren) {
                    this.mChildrenContainer.updateGroupOverflow();
                }
                notifyHeightChanged(false);
            }
            if (isAboveShelf() != isAboveShelf) {
                this.mAboveShelfChangedListener.onAboveShelfStateChanged(!isAboveShelf);
            }
        }
        updateRippleAllowed();
    }

    private void updateRippleAllowed() {
        setRippleAllowed(isOnKeyguard() || this.mEntry.getSbn().getNotification().contentIntent == null);
    }

    public int getIntrinsicHeight() {
        if (isUserLocked()) {
            return getActualHeight();
        }
        NotificationGuts notificationGuts = this.mGuts;
        if (notificationGuts != null && notificationGuts.isExposed()) {
            return this.mGuts.getIntrinsicHeight();
        }
        if (isChildInGroup() && !isGroupExpanded()) {
            return this.mPrivateLayout.getMinHeight();
        }
        if (this.mSensitive && this.mHideSensitiveForIntrinsicHeight) {
            return getMinHeight();
        }
        if (this.mIsSummaryWithChildren) {
            return this.mChildrenContainer.getIntrinsicHeight();
        }
        if (!canShowHeadsUp() || !isHeadsUpState()) {
            if (isExpanded()) {
                return getMaxExpandHeight();
            }
            return getCollapsedHeight();
        } else if (isPinned() || this.mHeadsupDisappearRunning) {
            return getPinnedHeadsUpHeight(true);
        } else {
            if (isExpanded()) {
                return Math.max(getMaxExpandHeight(), getHeadsUpHeight());
            }
            return Math.max(getCollapsedHeight(), getHeadsUpHeight());
        }
    }

    public boolean canShowHeadsUp() {
        return !this.mOnKeyguard || isDozing() || isBypassEnabled();
    }

    private boolean isBypassEnabled() {
        KeyguardBypassController keyguardBypassController = this.mBypassController;
        return keyguardBypassController == null || keyguardBypassController.getBypassEnabled();
    }

    private boolean isDozing() {
        StatusBarStateController statusBarStateController = this.mStatusbarStateController;
        return statusBarStateController != null && statusBarStateController.isDozing();
    }

    public boolean isGroupExpanded() {
        return this.mGroupManager.isGroupExpanded(this.mEntry.getSbn());
    }

    private void onChildrenCountChanged() {
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        boolean z = notificationChildrenContainer != null && notificationChildrenContainer.getNotificationChildCount() > 0;
        this.mIsSummaryWithChildren = z;
        if (z && this.mChildrenContainer.getHeaderView() == null) {
            this.mChildrenContainer.recreateNotificationHeader(this.mExpandClickListener);
        }
        getShowingLayout().updateBackgroundColor(false);
        this.mPrivateLayout.updateExpandButtons(isExpandable());
        updateChildrenHeaderAppearance();
        updateChildrenVisibility();
        applyChildrenRoundness();
    }

    public int getNumUniqueChannels() {
        return getUniqueChannels().size();
    }

    public ArraySet<NotificationChannel> getUniqueChannels() {
        ArraySet<NotificationChannel> arraySet = new ArraySet<>();
        arraySet.add(this.mEntry.getChannel());
        if (this.mIsSummaryWithChildren) {
            List notificationChildren = getNotificationChildren();
            int size = notificationChildren.size();
            for (int i = 0; i < size; i++) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) notificationChildren.get(i);
                NotificationChannel channel = expandableNotificationRow.getEntry().getChannel();
                StatusBarNotification sbn = expandableNotificationRow.getEntry().getSbn();
                if (sbn.getUser().equals(this.mEntry.getSbn().getUser()) && sbn.getPackageName().equals(this.mEntry.getSbn().getPackageName())) {
                    arraySet.add(channel);
                }
            }
        }
        return arraySet;
    }

    public void updateChildrenHeaderAppearance() {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.updateChildrenHeaderAppearance();
        }
    }

    public boolean isExpanded() {
        return isExpanded(false);
    }

    public boolean isExpanded(boolean z) {
        return (!this.mOnKeyguard || z) && ((!hasUserChangedExpansion() && (isSystemExpanded() || isSystemChildExpanded())) || isUserExpanded());
    }

    private boolean isSystemChildExpanded() {
        return this.mIsSystemChildExpanded;
    }

    public void setSystemChildExpanded(boolean z) {
        this.mIsSystemChildExpanded = z;
    }

    public void setLayoutListener(LayoutListener layoutListener) {
        this.mLayoutListener = layoutListener;
    }

    public void removeListener() {
        this.mLayoutListener = null;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int intrinsicHeight = getIntrinsicHeight();
        super.onLayout(z, i, i2, i3, i4);
        if (!(intrinsicHeight == getIntrinsicHeight() || intrinsicHeight == 0)) {
            notifyHeightChanged(true);
        }
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (!(notificationMenuRowPlugin == null || notificationMenuRowPlugin.getMenuView() == null)) {
            this.mMenuRow.onParentHeightUpdate();
        }
        updateContentShiftHeight();
        LayoutListener layoutListener = this.mLayoutListener;
        if (layoutListener != null) {
            layoutListener.onLayout();
        }
    }

    private void updateContentShiftHeight() {
        NotificationHeaderView visibleNotificationHeader = getVisibleNotificationHeader();
        if (visibleNotificationHeader != null) {
            CachingIconView icon = visibleNotificationHeader.getIcon();
            this.mIconTransformContentShift = getRelativeTopPadding(icon) + icon.getHeight();
            return;
        }
        this.mIconTransformContentShift = this.mContentShift;
    }

    /* access modifiers changed from: protected */
    public float getContentTransformationShift() {
        return (float) this.mIconTransformContentShift;
    }

    public void notifyHeightChanged(boolean z) {
        super.notifyHeightChanged(z);
        getShowingLayout().requestSelectLayout(z || isUserLocked());
    }

    public void setSensitive(boolean z, boolean z2) {
        this.mSensitive = z;
        this.mSensitiveHiddenInGeneral = z2;
    }

    public void setHideSensitiveForIntrinsicHeight(boolean z) {
        this.mHideSensitiveForIntrinsicHeight = z;
        if (this.mIsSummaryWithChildren) {
            List notificationChildren = this.mChildrenContainer.getNotificationChildren();
            for (int i = 0; i < notificationChildren.size(); i++) {
                ((ExpandableNotificationRow) notificationChildren.get(i)).setHideSensitiveForIntrinsicHeight(z);
            }
        }
    }

    public void setHideSensitive(boolean z, boolean z2, long j, long j2) {
        if (getVisibility() != 8) {
            boolean z3 = this.mShowingPublic;
            int i = 0;
            boolean z4 = this.mSensitive && z;
            this.mShowingPublic = z4;
            if ((!this.mShowingPublicInitialized || z4 != z3) && this.mPublicLayout.getChildCount() != 0) {
                if (!z2) {
                    this.mPublicLayout.animate().cancel();
                    this.mPrivateLayout.animate().cancel();
                    NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
                    if (notificationChildrenContainer != null) {
                        notificationChildrenContainer.animate().cancel();
                        this.mChildrenContainer.setAlpha(1.0f);
                    }
                    this.mPublicLayout.setAlpha(1.0f);
                    this.mPrivateLayout.setAlpha(1.0f);
                    NotificationContentView notificationContentView = this.mPublicLayout;
                    if (!this.mShowingPublic) {
                        i = 4;
                    }
                    notificationContentView.setVisibility(i);
                    updateChildrenVisibility();
                } else {
                    animateShowingPublic(j, j2, this.mShowingPublic);
                }
                getShowingLayout().updateBackgroundColor(z2);
                this.mPrivateLayout.updateExpandButtons(isExpandable());
                updateShelfIconColor();
                this.mShowingPublicInitialized = true;
            }
        }
    }

    private void animateShowingPublic(long j, long j2, boolean z) {
        View[] viewArr = this.mIsSummaryWithChildren ? new View[]{this.mChildrenContainer} : new View[]{this.mPrivateLayout};
        View[] viewArr2 = {this.mPublicLayout};
        View[] viewArr3 = z ? viewArr : viewArr2;
        if (z) {
            viewArr = viewArr2;
        }
        for (final View view : viewArr3) {
            view.setVisibility(0);
            view.animate().cancel();
            view.animate().alpha(0.0f).setStartDelay(j).setDuration(j2).withEndAction(new Runnable(this) {
                public void run() {
                    view.setVisibility(4);
                }
            });
        }
        for (View view2 : viewArr) {
            view2.setVisibility(0);
            view2.setAlpha(0.0f);
            view2.animate().cancel();
            view2.animate().alpha(1.0f).setStartDelay(j).setDuration(j2);
        }
    }

    public boolean mustStayOnScreen() {
        return this.mIsHeadsUp && this.mMustStayOnScreen;
    }

    public boolean canViewBeDismissed() {
        return this.mEntry.isClearable() && (!shouldShowPublic() || !this.mSensitiveHiddenInGeneral);
    }

    /* access modifiers changed from: private */
    public boolean shouldShowPublic() {
        return this.mSensitive && this.mHideSensitiveForIntrinsicHeight;
    }

    public void makeActionsVisibile() {
        setUserExpanded(true, true);
        if (isChildInGroup()) {
            this.mGroupManager.setGroupExpanded(this.mEntry.getSbn(), true);
        }
        notifyHeightChanged(false);
    }

    public void setChildrenExpanded(boolean z, boolean z2) {
        this.mChildrenExpanded = z;
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.setChildrenExpanded(z);
        }
        updateBackgroundForGroupState();
        updateClickAndFocus();
    }

    public int getMaxExpandHeight() {
        return this.mPrivateLayout.getExpandHeight();
    }

    private int getHeadsUpHeight() {
        return getShowingLayout().getHeadsUpHeight(false);
    }

    public boolean areGutsExposed() {
        NotificationGuts notificationGuts = this.mGuts;
        return notificationGuts != null && notificationGuts.isExposed();
    }

    public boolean isContentExpandable() {
        if (!this.mIsSummaryWithChildren || shouldShowPublic()) {
            return getShowingLayout().isContentExpandable();
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public View getContentView() {
        if (!this.mIsSummaryWithChildren || shouldShowPublic()) {
            return getShowingLayout();
        }
        return this.mChildrenContainer;
    }

    public long performRemoveAnimation(long j, long j2, float f, boolean z, float f2, Runnable runnable, AnimatorListenerAdapter animatorListenerAdapter) {
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null && notificationMenuRowPlugin.isMenuVisible()) {
            Animator translateViewAnimator = getTranslateViewAnimator(0.0f, null);
            if (translateViewAnimator != null) {
                final long j3 = j;
                final long j4 = j2;
                final float f3 = f;
                final boolean z2 = z;
                final float f4 = f2;
                final Runnable runnable2 = runnable;
                final AnimatorListenerAdapter animatorListenerAdapter2 = animatorListenerAdapter;
                C12727 r0 = new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ExpandableNotificationRow.super.performRemoveAnimation(j3, j4, f3, z2, f4, runnable2, animatorListenerAdapter2);
                    }
                };
                translateViewAnimator.addListener(r0);
                translateViewAnimator.start();
                return translateViewAnimator.getDuration();
            }
        }
        return super.performRemoveAnimation(j, j2, f, z, f2, runnable, animatorListenerAdapter);
    }

    /* access modifiers changed from: protected */
    public void onAppearAnimationFinished(boolean z) {
        NotificationContentView[] notificationContentViewArr;
        super.onAppearAnimationFinished(z);
        if (z) {
            NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
            if (notificationChildrenContainer != null) {
                notificationChildrenContainer.setAlpha(1.0f);
                this.mChildrenContainer.setLayerType(0, null);
            }
            for (NotificationContentView notificationContentView : this.mLayouts) {
                notificationContentView.setAlpha(1.0f);
                notificationContentView.setLayerType(0, null);
            }
            return;
        }
        setHeadsUpAnimatingAway(false);
    }

    public int getExtraBottomPadding() {
        if (!this.mIsSummaryWithChildren || !isGroupExpanded()) {
            return 0;
        }
        return this.mIncreasedPaddingBetweenElements;
    }

    public void setActualHeight(int i, boolean z) {
        boolean z2 = i != getActualHeight();
        super.setActualHeight(i, z);
        if (z2 && isRemoved()) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            if (viewGroup != null) {
                viewGroup.invalidate();
            }
        }
        NotificationGuts notificationGuts = this.mGuts;
        if (notificationGuts == null || !notificationGuts.isExposed()) {
            int max = Math.max(getMinHeight(), i);
            for (NotificationContentView contentHeight : this.mLayouts) {
                contentHeight.setContentHeight(max);
            }
            if (this.mIsSummaryWithChildren) {
                this.mChildrenContainer.setActualHeight(i);
            }
            NotificationGuts notificationGuts2 = this.mGuts;
            if (notificationGuts2 != null) {
                notificationGuts2.setActualHeight(i);
            }
            NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
            if (!(notificationMenuRowPlugin == null || notificationMenuRowPlugin.getMenuView() == null)) {
                this.mMenuRow.onParentHeightUpdate();
            }
            return;
        }
        this.mGuts.setActualHeight(i);
    }

    public int getMaxContentHeight() {
        if (!this.mIsSummaryWithChildren || shouldShowPublic()) {
            return getShowingLayout().getMaxHeight();
        }
        return this.mChildrenContainer.getMaxContentHeight();
    }

    public int getMinHeight(boolean z) {
        if (!z) {
            NotificationGuts notificationGuts = this.mGuts;
            if (notificationGuts != null && notificationGuts.isExposed()) {
                return this.mGuts.getIntrinsicHeight();
            }
        }
        if (!z && canShowHeadsUp() && this.mIsHeadsUp && this.mHeadsUpManager.isTrackingHeadsUp()) {
            return getPinnedHeadsUpHeight(false);
        }
        if (this.mIsSummaryWithChildren && !isGroupExpanded() && !shouldShowPublic()) {
            return this.mChildrenContainer.getMinHeight();
        }
        if (z || !canShowHeadsUp() || !this.mIsHeadsUp) {
            return getShowingLayout().getMinHeight();
        }
        return getHeadsUpHeight();
    }

    public int getCollapsedHeight() {
        if (!this.mIsSummaryWithChildren || shouldShowPublic()) {
            return getMinHeight();
        }
        return this.mChildrenContainer.getCollapsedHeight();
    }

    public int getHeadsUpHeightWithoutHeader() {
        if (!canShowHeadsUp() || !this.mIsHeadsUp) {
            return getCollapsedHeight();
        }
        if (!this.mIsSummaryWithChildren || shouldShowPublic()) {
            return getShowingLayout().getHeadsUpHeight(true);
        }
        return this.mChildrenContainer.getCollapsedHeightWithoutHeader();
    }

    public void setClipTopAmount(int i) {
        super.setClipTopAmount(i);
        for (NotificationContentView clipTopAmount : this.mLayouts) {
            clipTopAmount.setClipTopAmount(i);
        }
        NotificationGuts notificationGuts = this.mGuts;
        if (notificationGuts != null) {
            notificationGuts.setClipTopAmount(i);
        }
    }

    public void setClipBottomAmount(int i) {
        if (!this.mExpandAnimationRunning) {
            if (i != this.mClipBottomAmount) {
                super.setClipBottomAmount(i);
                for (NotificationContentView clipBottomAmount : this.mLayouts) {
                    clipBottomAmount.setClipBottomAmount(i);
                }
                NotificationGuts notificationGuts = this.mGuts;
                if (notificationGuts != null) {
                    notificationGuts.setClipBottomAmount(i);
                }
            }
            NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
            if (notificationChildrenContainer != null && !this.mChildIsExpanding) {
                notificationChildrenContainer.setClipBottomAmount(i);
            }
        }
    }

    public NotificationContentView getShowingLayout() {
        return shouldShowPublic() ? this.mPublicLayout : this.mPrivateLayout;
    }

    public View getExpandedContentView() {
        return getPrivateLayout().getExpandedChild();
    }

    public void setLegacy(boolean z) {
        for (NotificationContentView legacy : this.mLayouts) {
            legacy.setLegacy(z);
        }
    }

    /* access modifiers changed from: protected */
    public void updateBackgroundTint() {
        super.updateBackgroundTint();
        updateBackgroundForGroupState();
        if (this.mIsSummaryWithChildren) {
            List notificationChildren = this.mChildrenContainer.getNotificationChildren();
            for (int i = 0; i < notificationChildren.size(); i++) {
                ((ExpandableNotificationRow) notificationChildren.get(i)).updateBackgroundForGroupState();
            }
        }
    }

    public void onFinishedExpansionChange() {
        this.mGroupExpansionChanging = false;
        updateBackgroundForGroupState();
    }

    public void updateBackgroundForGroupState() {
        boolean z = true;
        int i = 0;
        if (this.mIsSummaryWithChildren) {
            if (this.mShowGroupBackgroundWhenExpanded || !isGroupExpanded() || isGroupExpansionChanging() || isUserLocked()) {
                z = false;
            }
            this.mShowNoBackground = z;
            this.mChildrenContainer.updateHeaderForExpansion(z);
            List notificationChildren = this.mChildrenContainer.getNotificationChildren();
            while (i < notificationChildren.size()) {
                ((ExpandableNotificationRow) notificationChildren.get(i)).updateBackgroundForGroupState();
                i++;
            }
        } else if (isChildInGroup()) {
            int backgroundColorForExpansionState = getShowingLayout().getBackgroundColorForExpansionState();
            if (isGroupExpanded() || ((this.mNotificationParent.isGroupExpansionChanging() || this.mNotificationParent.isUserLocked()) && backgroundColorForExpansionState != 0)) {
                i = 1;
            }
            this.mShowNoBackground = i ^ true;
        } else {
            this.mShowNoBackground = false;
        }
        updateOutline();
        updateBackground();
    }

    public int getPositionOfChild(ExpandableNotificationRow expandableNotificationRow) {
        if (this.mIsSummaryWithChildren) {
            return this.mChildrenContainer.getPositionInLinearLayout(expandableNotificationRow);
        }
        return 0;
    }

    public void onExpandedByGesture(boolean z) {
        MetricsLogger.action(this.mContext, this.mGroupManager.isSummaryOfGroup(this.mEntry.getSbn()) ? 410 : 409, z);
    }

    public float getIncreasedPaddingAmount() {
        if (this.mIsSummaryWithChildren) {
            if (isGroupExpanded()) {
                return 1.0f;
            }
            if (isUserLocked()) {
                return this.mChildrenContainer.getIncreasedPaddingAmount();
            }
        } else if (isColorized() && (!this.mIsLowPriority || isExpanded())) {
            return -1.0f;
        }
        return 0.0f;
    }

    private boolean isColorized() {
        return this.mIsColorized && this.mBgTint != 0;
    }

    /* access modifiers changed from: protected */
    public boolean disallowSingleClick(MotionEvent motionEvent) {
        if (areGutsExposed()) {
            return false;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        NotificationHeaderView visibleNotificationHeader = getVisibleNotificationHeader();
        if (visibleNotificationHeader != null && visibleNotificationHeader.isInTouchRect(x - getTranslation(), y)) {
            return true;
        }
        if ((!this.mIsSummaryWithChildren || shouldShowPublic()) && getShowingLayout().disallowSingleClick(x, y)) {
            return true;
        }
        return super.disallowSingleClick(motionEvent);
    }

    /* access modifiers changed from: private */
    public void onExpansionChanged(boolean z, boolean z2) {
        boolean isExpanded = isExpanded();
        if (this.mIsSummaryWithChildren && (!this.mIsLowPriority || z2)) {
            isExpanded = this.mGroupManager.isGroupExpanded(this.mEntry.getSbn());
        }
        if (isExpanded != z2) {
            updateShelfIconColor();
            ExpansionLogger expansionLogger = this.mLogger;
            if (expansionLogger != null) {
                expansionLogger.logNotificationExpansion(this.mLoggingKey, z, isExpanded);
            }
            if (this.mIsSummaryWithChildren) {
                this.mChildrenContainer.onExpansionChanged();
            }
        }
    }

    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(AccessibilityAction.ACTION_LONG_CLICK);
        if (canViewBeDismissed()) {
            accessibilityNodeInfo.addAction(AccessibilityAction.ACTION_DISMISS);
        }
        boolean shouldShowPublic = shouldShowPublic();
        boolean z = false;
        if (!shouldShowPublic) {
            if (this.mIsSummaryWithChildren) {
                shouldShowPublic = true;
                if (!this.mIsLowPriority || isExpanded()) {
                    z = isGroupExpanded();
                }
            } else {
                shouldShowPublic = this.mPrivateLayout.isContentExpandable();
                z = isExpanded();
            }
        }
        if (shouldShowPublic) {
            if (z) {
                accessibilityNodeInfo.addAction(AccessibilityAction.ACTION_COLLAPSE);
            } else {
                accessibilityNodeInfo.addAction(AccessibilityAction.ACTION_EXPAND);
            }
        }
        NotificationMenuRowPlugin provider = getProvider();
        if (provider != null && provider.getSnoozeMenuItem(getContext()) != null) {
            accessibilityNodeInfo.addAction(new AccessibilityAction(C2011R$id.action_snooze, getContext().getResources().getString(C2017R$string.notification_menu_snooze_action)));
        }
    }

    public boolean performAccessibilityActionInternal(int i, Bundle bundle) {
        if (super.performAccessibilityActionInternal(i, bundle)) {
            return true;
        }
        if (i == 32) {
            doLongClickCallback();
            return true;
        } else if (i == 262144 || i == 524288) {
            this.mExpandClickListener.onClick(this);
            return true;
        } else if (i == 1048576) {
            performDismissWithBlockingHelper(true);
            return true;
        } else if (i != C2011R$id.action_snooze || getProvider() != null || this.mMenuRow == null) {
            return false;
        } else {
            MenuItem snoozeMenuItem = createMenu().getSnoozeMenuItem(getContext());
            if (snoozeMenuItem != null) {
                doLongClickCallback(getWidth() / 2, getHeight() / 2, snoozeMenuItem);
            }
            return true;
        }
    }

    public ExpandableViewState createExpandableViewState() {
        return new NotificationViewState();
    }

    public boolean isAboveShelf() {
        return canShowHeadsUp() && (this.mIsPinned || this.mHeadsupDisappearRunning || ((this.mIsHeadsUp && this.mAboveShelf) || this.mExpandAnimationRunning || this.mChildIsExpanding));
    }

    public boolean topAmountNeedsClipping() {
        if (isGroupExpanded() || isGroupExpansionChanging() || getShowingLayout().shouldClipToRounding(true, false)) {
            return true;
        }
        NotificationGuts notificationGuts = this.mGuts;
        if (notificationGuts == null || notificationGuts.getAlpha() == 0.0f) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean childNeedsClipping(View view) {
        if (view instanceof NotificationContentView) {
            NotificationContentView notificationContentView = (NotificationContentView) view;
            if (isClippingNeeded()) {
                return true;
            }
            if (!hasNoRounding()) {
                boolean z = false;
                boolean z2 = getCurrentTopRoundness() != 0.0f;
                if (getCurrentBottomRoundness() != 0.0f) {
                    z = true;
                }
                if (notificationContentView.shouldClipToRounding(z2, z)) {
                    return true;
                }
            }
        } else if (view == this.mChildrenContainer) {
            if (isClippingNeeded() || !hasNoRounding()) {
                return true;
            }
        } else if (view instanceof NotificationGuts) {
            return !hasNoRounding();
        }
        return super.childNeedsClipping(view);
    }

    /* access modifiers changed from: protected */
    public void applyRoundness() {
        super.applyRoundness();
        applyChildrenRoundness();
    }

    private void applyChildrenRoundness() {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.setCurrentBottomRoundness(getCurrentBottomRoundness());
        }
    }

    public Path getCustomClipPath(View view) {
        if (view instanceof NotificationGuts) {
            return getClipPath(true);
        }
        return super.getCustomClipPath(view);
    }

    private boolean hasNoRounding() {
        return getCurrentBottomRoundness() == 0.0f && getCurrentTopRoundness() == 0.0f;
    }

    public boolean isMediaRow() {
        return (getExpandedContentView() == null || getExpandedContentView().findViewById(16909130) == null) ? false : true;
    }

    public boolean isTopLevelChild() {
        return getParent() instanceof NotificationStackScrollLayout;
    }

    public boolean isGroupNotFullyVisible() {
        return getClipTopAmount() > 0 || getTranslationY() < 0.0f;
    }

    public void setAboveShelf(boolean z) {
        boolean isAboveShelf = isAboveShelf();
        this.mAboveShelf = z;
        if (isAboveShelf() != isAboveShelf) {
            this.mAboveShelfChangedListener.onAboveShelfStateChanged(!isAboveShelf);
        }
    }

    public void setDismissRtl(boolean z) {
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null) {
            notificationMenuRowPlugin.setDismissRtl(z);
        }
    }

    public SmartRepliesAndActions getExistingSmartRepliesAndActions() {
        return this.mPrivateLayout.getCurrentSmartRepliesAndActions();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void setChildrenContainer(NotificationChildrenContainer notificationChildrenContainer) {
        this.mChildrenContainer = notificationChildrenContainer;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void setPrivateLayout(NotificationContentView notificationContentView) {
        this.mPrivateLayout = notificationContentView;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void setPublicLayout(NotificationContentView notificationContentView) {
        this.mPublicLayout = notificationContentView;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(fileDescriptor, printWriter, strArr);
        StringBuilder sb = new StringBuilder();
        sb.append("  Notification: ");
        sb.append(this.mEntry.getKey());
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("    visibility: ");
        sb2.append(getVisibility());
        printWriter.print(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        String str = ", alpha: ";
        sb3.append(str);
        sb3.append(getAlpha());
        printWriter.print(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(", translation: ");
        sb4.append(getTranslation());
        printWriter.print(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append(", removed: ");
        sb5.append(isRemoved());
        printWriter.print(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append(", expandAnimationRunning: ");
        sb6.append(this.mExpandAnimationRunning);
        printWriter.print(sb6.toString());
        NotificationContentView showingLayout = getShowingLayout();
        StringBuilder sb7 = new StringBuilder();
        sb7.append(", privateShowing: ");
        sb7.append(showingLayout == this.mPrivateLayout);
        printWriter.print(sb7.toString());
        printWriter.println();
        showingLayout.dump(fileDescriptor, printWriter, strArr);
        printWriter.print("    ");
        if (getViewState() != null) {
            getViewState().dump(fileDescriptor, printWriter, strArr);
        } else {
            printWriter.print("no viewState!!!");
        }
        printWriter.println();
        printWriter.println();
        if (this.mIsSummaryWithChildren) {
            printWriter.print("  ChildrenContainer");
            StringBuilder sb8 = new StringBuilder();
            sb8.append(" visibility: ");
            sb8.append(this.mChildrenContainer.getVisibility());
            printWriter.print(sb8.toString());
            StringBuilder sb9 = new StringBuilder();
            sb9.append(str);
            sb9.append(this.mChildrenContainer.getAlpha());
            printWriter.print(sb9.toString());
            StringBuilder sb10 = new StringBuilder();
            sb10.append(", translationY: ");
            sb10.append(this.mChildrenContainer.getTranslationY());
            printWriter.print(sb10.toString());
            printWriter.println();
            List<ExpandableNotificationRow> notificationChildren = getNotificationChildren();
            StringBuilder sb11 = new StringBuilder();
            sb11.append("  Children: ");
            sb11.append(notificationChildren.size());
            printWriter.println(sb11.toString());
            printWriter.println("  {");
            for (ExpandableNotificationRow dump : notificationChildren) {
                dump.dump(fileDescriptor, printWriter, strArr);
            }
            printWriter.println("  }");
            printWriter.println();
        }
    }
}
