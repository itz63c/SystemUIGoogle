package com.android.systemui.statusbar.notification.row;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2008R$color;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.Interpolators;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin.MenuItem;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin.OnMenuEventListener;
import com.android.systemui.statusbar.AlphaOptimizedImageView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.LayoutListener;
import com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import java.util.ArrayList;
import java.util.Map;

public class NotificationMenuRow implements NotificationMenuRowPlugin, OnClickListener, LayoutListener {
    /* access modifiers changed from: private */
    public float mAlpha = 0.0f;
    /* access modifiers changed from: private */
    public boolean mAnimating;
    private MenuItem mAppOpsItem;
    private CheckForDrag mCheckForDrag;
    private Context mContext;
    private boolean mDismissing;
    private ValueAnimator mFadeAnimator;
    private Handler mHandler;
    private int mHorizSpaceForIcon = -1;
    private int[] mIconLocation = new int[2];
    private int mIconPadding = -1;
    private boolean mIconsPlaced;
    private NotificationMenuItem mInfoItem;
    private boolean mIsForeground;
    private boolean mIsUserTouching;
    private ArrayList<MenuItem> mLeftMenuItems;
    private FrameLayout mMenuContainer;
    /* access modifiers changed from: private */
    public boolean mMenuFadedIn;
    private final Map<View, MenuItem> mMenuItemsByView = new ArrayMap();
    private OnMenuEventListener mMenuListener;
    private boolean mMenuSnapped;
    private boolean mMenuSnappedOnLeft;
    private boolean mOnLeft;
    /* access modifiers changed from: private */
    public ExpandableNotificationRow mParent;
    private int[] mParentLocation = new int[2];
    private ArrayList<MenuItem> mRightMenuItems;
    private boolean mShouldShowMenu;
    private boolean mSnapping;
    private MenuItem mSnoozeItem;
    /* access modifiers changed from: private */
    public float mTranslation;
    private int mVertSpaceForIcons = -1;

    private final class CheckForDrag implements Runnable {
        private CheckForDrag() {
        }

        public void run() {
            float abs = Math.abs(NotificationMenuRow.this.mTranslation);
            float spaceForMenu = (float) NotificationMenuRow.this.getSpaceForMenu();
            float width = ((float) NotificationMenuRow.this.mParent.getWidth()) * 0.4f;
            if ((!NotificationMenuRow.this.isMenuVisible() || NotificationMenuRow.this.isMenuLocationChange()) && ((double) abs) >= ((double) spaceForMenu) * 0.4d && abs < width) {
                NotificationMenuRow.this.fadeInMenu(width);
            }
        }
    }

    public static class NotificationMenuItem implements MenuItem {
        String mContentDescription;
        GutsContent mGutsContent;
        View mMenuView;

        public NotificationMenuItem(Context context, String str, GutsContent gutsContent, int i) {
            Resources resources = context.getResources();
            int dimensionPixelSize = resources.getDimensionPixelSize(C2009R$dimen.notification_menu_icon_padding);
            int color = resources.getColor(C2008R$color.notification_gear_color);
            if (i >= 0) {
                AlphaOptimizedImageView alphaOptimizedImageView = new AlphaOptimizedImageView(context);
                alphaOptimizedImageView.setPadding(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
                alphaOptimizedImageView.setImageDrawable(context.getResources().getDrawable(i));
                alphaOptimizedImageView.setColorFilter(color);
                alphaOptimizedImageView.setAlpha(1.0f);
                this.mMenuView = alphaOptimizedImageView;
            }
            this.mContentDescription = str;
            this.mGutsContent = gutsContent;
        }

        public View getMenuView() {
            return this.mMenuView;
        }

        public View getGutsView() {
            return this.mGutsContent.getContentView();
        }

        public String getContentDescription() {
            return this.mContentDescription;
        }
    }

    public MenuItem menuItemToExposeOnSnap() {
        return null;
    }

    public void setMenuItems(ArrayList<MenuItem> arrayList) {
    }

    public boolean shouldShowGutsOnSnapOpen() {
        return false;
    }

    public NotificationMenuRow(Context context) {
        this.mContext = context;
        this.mShouldShowMenu = context.getResources().getBoolean(C2007R$bool.config_showNotificationGear);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mLeftMenuItems = new ArrayList<>();
        this.mRightMenuItems = new ArrayList<>();
    }

    public ArrayList<MenuItem> getMenuItems(Context context) {
        return this.mOnLeft ? this.mLeftMenuItems : this.mRightMenuItems;
    }

    public MenuItem getLongpressMenuItem(Context context) {
        return this.mInfoItem;
    }

    public MenuItem getAppOpsMenuItem(Context context) {
        return this.mAppOpsItem;
    }

    public MenuItem getSnoozeMenuItem(Context context) {
        return this.mSnoozeItem;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public ExpandableNotificationRow getParent() {
        return this.mParent;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean isMenuOnLeft() {
        return this.mOnLeft;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean isMenuSnappedOnLeft() {
        return this.mMenuSnappedOnLeft;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean isMenuSnapped() {
        return this.mMenuSnapped;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean isDismissing() {
        return this.mDismissing;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean isSnapping() {
        return this.mSnapping;
    }

    public void setMenuClickListener(OnMenuEventListener onMenuEventListener) {
        this.mMenuListener = onMenuEventListener;
    }

    public void createMenu(ViewGroup viewGroup, StatusBarNotification statusBarNotification) {
        this.mParent = (ExpandableNotificationRow) viewGroup;
        createMenuViews(true, (statusBarNotification == null || (statusBarNotification.getNotification().flags & 64) == 0) ? false : true);
    }

    public boolean isMenuVisible() {
        return this.mAlpha > 0.0f;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean isUserTouching() {
        return this.mIsUserTouching;
    }

    public boolean shouldShowMenu() {
        return this.mShouldShowMenu;
    }

    public View getMenuView() {
        return this.mMenuContainer;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public float getTranslation() {
        return this.mTranslation;
    }

    public void resetMenu() {
        resetState(true);
    }

    public void onTouchEnd() {
        this.mIsUserTouching = false;
    }

    public void onNotificationUpdated(StatusBarNotification statusBarNotification) {
        if (this.mMenuContainer != null) {
            boolean z = true;
            boolean z2 = !isMenuVisible();
            if ((statusBarNotification.getNotification().flags & 64) == 0) {
                z = false;
            }
            createMenuViews(z2, z);
        }
    }

    public void onConfigurationChanged() {
        this.mParent.setLayoutListener(this);
    }

    public void onLayout() {
        this.mIconsPlaced = false;
        setMenuLocation();
        this.mParent.removeListener();
    }

    private void createMenuViews(boolean z, boolean z2) {
        this.mIsForeground = z2;
        Resources resources = this.mContext.getResources();
        this.mHorizSpaceForIcon = resources.getDimensionPixelSize(C2009R$dimen.notification_menu_icon_size);
        this.mVertSpaceForIcons = resources.getDimensionPixelSize(C2009R$dimen.notification_min_height);
        this.mLeftMenuItems.clear();
        this.mRightMenuItems.clear();
        boolean z3 = Secure.getInt(this.mContext.getContentResolver(), "show_notification_snooze", 0) == 1;
        if (!z2 && z3) {
            this.mSnoozeItem = createSnoozeItem(this.mContext);
        }
        this.mAppOpsItem = createAppOpsItem(this.mContext);
        if (this.mParent.getEntry().getBucket() == 1) {
            this.mInfoItem = createConversationItem(this.mContext);
        } else {
            this.mInfoItem = createInfoItem(this.mContext);
        }
        if (!z2 && z3) {
            this.mRightMenuItems.add(this.mSnoozeItem);
        }
        this.mRightMenuItems.add(this.mInfoItem);
        this.mRightMenuItems.add(this.mAppOpsItem);
        this.mLeftMenuItems.addAll(this.mRightMenuItems);
        populateMenuViews();
        if (z) {
            resetState(false);
            return;
        }
        this.mIconsPlaced = false;
        setMenuLocation();
        if (!this.mIsUserTouching) {
            onSnapOpen();
        }
    }

    private void populateMenuViews() {
        FrameLayout frameLayout = this.mMenuContainer;
        if (frameLayout != null) {
            frameLayout.removeAllViews();
            this.mMenuItemsByView.clear();
        } else {
            this.mMenuContainer = new FrameLayout(this.mContext);
        }
        ArrayList<MenuItem> arrayList = this.mOnLeft ? this.mLeftMenuItems : this.mRightMenuItems;
        for (int i = 0; i < arrayList.size(); i++) {
            addMenuView((MenuItem) arrayList.get(i), this.mMenuContainer);
        }
    }

    private void resetState(boolean z) {
        setMenuAlpha(0.0f);
        this.mIconsPlaced = false;
        this.mMenuFadedIn = false;
        this.mAnimating = false;
        this.mSnapping = false;
        this.mDismissing = false;
        this.mMenuSnapped = false;
        setMenuLocation();
        OnMenuEventListener onMenuEventListener = this.mMenuListener;
        if (onMenuEventListener != null && z) {
            onMenuEventListener.onMenuReset(this.mParent);
        }
    }

    public void onTouchMove(float f) {
        this.mSnapping = false;
        if (!isTowardsMenu(f) && isMenuLocationChange()) {
            this.mMenuSnapped = false;
            if (!this.mHandler.hasCallbacks(this.mCheckForDrag)) {
                this.mCheckForDrag = null;
            } else {
                setMenuAlpha(0.0f);
                setMenuLocation();
            }
        }
        if (this.mShouldShowMenu && !NotificationStackScrollLayout.isPinnedHeadsUp(getParent()) && !this.mParent.areGutsExposed() && !this.mParent.showingPulsing()) {
            CheckForDrag checkForDrag = this.mCheckForDrag;
            if (checkForDrag == null || !this.mHandler.hasCallbacks(checkForDrag)) {
                CheckForDrag checkForDrag2 = new CheckForDrag();
                this.mCheckForDrag = checkForDrag2;
                this.mHandler.postDelayed(checkForDrag2, 60);
            }
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void beginDrag() {
        this.mSnapping = false;
        ValueAnimator valueAnimator = this.mFadeAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mHandler.removeCallbacks(this.mCheckForDrag);
        this.mCheckForDrag = null;
        this.mIsUserTouching = true;
    }

    public void onTouchStart() {
        beginDrag();
    }

    public void onSnapOpen() {
        this.mMenuSnapped = true;
        this.mMenuSnappedOnLeft = isMenuOnLeft();
        if (this.mAlpha == 0.0f) {
            ExpandableNotificationRow expandableNotificationRow = this.mParent;
            if (expandableNotificationRow != null) {
                fadeInMenu((float) expandableNotificationRow.getWidth());
            }
        }
        OnMenuEventListener onMenuEventListener = this.mMenuListener;
        if (onMenuEventListener != null) {
            onMenuEventListener.onMenuShown(getParent());
        }
    }

    public void onSnapClosed() {
        cancelDrag();
        this.mMenuSnapped = false;
        this.mSnapping = true;
    }

    public void onDismiss() {
        cancelDrag();
        this.mMenuSnapped = false;
        this.mDismissing = true;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void cancelDrag() {
        ValueAnimator valueAnimator = this.mFadeAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mHandler.removeCallbacks(this.mCheckForDrag);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public float getMinimumSwipeDistance() {
        return ((float) this.mHorizSpaceForIcon) * (getParent().canViewBeDismissed() ? 0.25f : 0.15f);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public float getMaximumSwipeDistance() {
        return ((float) this.mHorizSpaceForIcon) * 0.2f;
    }

    public boolean isTowardsMenu(float f) {
        return isMenuVisible() && ((isMenuOnLeft() && f <= 0.0f) || (!isMenuOnLeft() && f >= 0.0f));
    }

    public void setAppName(String str) {
        if (str != null) {
            setAppName(str, this.mLeftMenuItems);
            setAppName(str, this.mRightMenuItems);
        }
    }

    private void setAppName(String str, ArrayList<MenuItem> arrayList) {
        Resources resources = this.mContext.getResources();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            MenuItem menuItem = (MenuItem) arrayList.get(i);
            String format = String.format(resources.getString(C2017R$string.notification_menu_accessibility), new Object[]{str, menuItem.getContentDescription()});
            View menuView = menuItem.getMenuView();
            if (menuView != null) {
                menuView.setContentDescription(format);
            }
        }
    }

    public void onParentHeightUpdate() {
        float f;
        if (this.mParent == null) {
            return;
        }
        if ((!this.mLeftMenuItems.isEmpty() || !this.mRightMenuItems.isEmpty()) && this.mMenuContainer != null) {
            int actualHeight = this.mParent.getActualHeight();
            int i = this.mVertSpaceForIcons;
            if (actualHeight < i) {
                f = (float) ((actualHeight / 2) - (this.mHorizSpaceForIcon / 2));
            } else {
                f = (float) ((i - this.mHorizSpaceForIcon) / 2);
            }
            this.mMenuContainer.setTranslationY(f);
        }
    }

    public void onParentTranslationUpdate(float f) {
        this.mTranslation = f;
        if (!this.mAnimating && this.mMenuFadedIn) {
            float width = ((float) this.mParent.getWidth()) * 0.3f;
            float abs = Math.abs(f);
            float f2 = 0.0f;
            if (abs != 0.0f) {
                f2 = abs <= width ? 1.0f : 1.0f - ((abs - width) / (((float) this.mParent.getWidth()) - width));
            }
            setMenuAlpha(f2);
        }
    }

    public void onClick(View view) {
        if (this.mMenuListener != null) {
            view.getLocationOnScreen(this.mIconLocation);
            this.mParent.getLocationOnScreen(this.mParentLocation);
            int i = this.mHorizSpaceForIcon / 2;
            int height = view.getHeight() / 2;
            int[] iArr = this.mIconLocation;
            int i2 = iArr[0];
            int[] iArr2 = this.mParentLocation;
            int i3 = (i2 - iArr2[0]) + i;
            int i4 = (iArr[1] - iArr2[1]) + height;
            if (this.mMenuItemsByView.containsKey(view)) {
                this.mMenuListener.onMenuClicked(this.mParent, i3, i4, (MenuItem) this.mMenuItemsByView.get(view));
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isMenuLocationChange() {
        return (isMenuOnLeft() && ((this.mTranslation > ((float) (-this.mIconPadding)) ? 1 : (this.mTranslation == ((float) (-this.mIconPadding)) ? 0 : -1)) < 0)) || (!isMenuOnLeft() && ((this.mTranslation > ((float) this.mIconPadding) ? 1 : (this.mTranslation == ((float) this.mIconPadding) ? 0 : -1)) > 0));
    }

    private void setMenuLocation() {
        int i = 0;
        boolean z = this.mTranslation > 0.0f;
        if ((!this.mIconsPlaced || z != isMenuOnLeft()) && !isSnapping()) {
            FrameLayout frameLayout = this.mMenuContainer;
            if (frameLayout != null && frameLayout.isAttachedToWindow()) {
                boolean z2 = this.mOnLeft;
                this.mOnLeft = z;
                if (z2 != z) {
                    populateMenuViews();
                }
                int childCount = this.mMenuContainer.getChildCount();
                while (i < childCount) {
                    View childAt = this.mMenuContainer.getChildAt(i);
                    float f = (float) (this.mHorizSpaceForIcon * i);
                    i++;
                    float width = (float) (this.mParent.getWidth() - (this.mHorizSpaceForIcon * i));
                    if (!z) {
                        f = width;
                    }
                    childAt.setX(f);
                }
                this.mIconsPlaced = true;
            }
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void setMenuAlpha(float f) {
        this.mAlpha = f;
        FrameLayout frameLayout = this.mMenuContainer;
        if (frameLayout != null) {
            if (f == 0.0f) {
                this.mMenuFadedIn = false;
                frameLayout.setVisibility(4);
            } else {
                frameLayout.setVisibility(0);
            }
            int childCount = this.mMenuContainer.getChildCount();
            for (int i = 0; i < childCount; i++) {
                this.mMenuContainer.getChildAt(i).setAlpha(this.mAlpha);
            }
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public int getSpaceForMenu() {
        return this.mHorizSpaceForIcon * this.mMenuContainer.getChildCount();
    }

    /* access modifiers changed from: private */
    public void fadeInMenu(final float f) {
        if (!this.mDismissing && !this.mAnimating) {
            if (isMenuLocationChange()) {
                setMenuAlpha(0.0f);
            }
            final float f2 = this.mTranslation;
            final boolean z = f2 > 0.0f;
            setMenuLocation();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.mAlpha, 1.0f});
            this.mFadeAnimator = ofFloat;
            ofFloat.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if (((z && f2 <= f) || (!z && Math.abs(f2) <= f)) && !NotificationMenuRow.this.mMenuFadedIn) {
                        NotificationMenuRow.this.setMenuAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
                    }
                }
            });
            this.mFadeAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    NotificationMenuRow.this.mAnimating = true;
                }

                public void onAnimationCancel(Animator animator) {
                    NotificationMenuRow.this.setMenuAlpha(0.0f);
                }

                public void onAnimationEnd(Animator animator) {
                    boolean z = false;
                    NotificationMenuRow.this.mAnimating = false;
                    NotificationMenuRow notificationMenuRow = NotificationMenuRow.this;
                    if (notificationMenuRow.mAlpha == 1.0f) {
                        z = true;
                    }
                    notificationMenuRow.mMenuFadedIn = z;
                }
            });
            this.mFadeAnimator.setInterpolator(Interpolators.ALPHA_IN);
            this.mFadeAnimator.setDuration(200);
            this.mFadeAnimator.start();
        }
    }

    public Point getRevealAnimationOrigin() {
        View menuView = this.mInfoItem.getMenuView();
        int left = menuView.getLeft() + menuView.getPaddingLeft() + (menuView.getWidth() / 2);
        int top = menuView.getTop() + menuView.getPaddingTop() + (menuView.getHeight() / 2);
        if (isMenuOnLeft()) {
            return new Point(left, top);
        }
        return new Point(this.mParent.getRight() - left, top);
    }

    static MenuItem createSnoozeItem(Context context) {
        return new NotificationMenuItem(context, context.getResources().getString(C2017R$string.notification_menu_snooze_description), (NotificationSnooze) LayoutInflater.from(context).inflate(C2013R$layout.notification_snooze, null, false), C2010R$drawable.ic_snooze);
    }

    static NotificationMenuItem createConversationItem(Context context) {
        return new NotificationMenuItem(context, context.getResources().getString(C2017R$string.notification_menu_gear_description), (NotificationConversationInfo) LayoutInflater.from(context).inflate(C2013R$layout.notification_conversation_info, null, false), C2010R$drawable.ic_settings);
    }

    static NotificationMenuItem createInfoItem(Context context) {
        return new NotificationMenuItem(context, context.getResources().getString(C2017R$string.notification_menu_gear_description), (NotificationInfo) LayoutInflater.from(context).inflate(C2013R$layout.notification_info, null, false), C2010R$drawable.ic_settings);
    }

    static MenuItem createAppOpsItem(Context context) {
        return new NotificationMenuItem(context, null, (AppOpsInfo) LayoutInflater.from(context).inflate(C2013R$layout.app_ops_info, null, false), -1);
    }

    private void addMenuView(MenuItem menuItem, ViewGroup viewGroup) {
        View menuView = menuItem.getMenuView();
        if (menuView != null) {
            menuView.setAlpha(this.mAlpha);
            viewGroup.addView(menuView);
            menuView.setOnClickListener(this);
            LayoutParams layoutParams = (LayoutParams) menuView.getLayoutParams();
            int i = this.mHorizSpaceForIcon;
            layoutParams.width = i;
            layoutParams.height = i;
            menuView.setLayoutParams(layoutParams);
        }
        this.mMenuItemsByView.put(menuView, menuItem);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public float getSnapBackThreshold() {
        return ((float) getSpaceForMenu()) - getMaximumSwipeDistance();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public float getDismissThreshold() {
        return ((float) getParent().getWidth()) * 0.6f;
    }

    public boolean isWithinSnapMenuThreshold() {
        float translation = getTranslation();
        float snapBackThreshold = getSnapBackThreshold();
        float dismissThreshold = getDismissThreshold();
        if (isMenuOnLeft()) {
            if (translation > snapBackThreshold && translation < dismissThreshold) {
                return true;
            }
        } else if (translation < (-snapBackThreshold) && translation > (-dismissThreshold)) {
            return true;
        }
        return false;
    }

    public boolean isSwipedEnoughToShowMenu() {
        float minimumSwipeDistance = getMinimumSwipeDistance();
        float translation = getTranslation();
        return isMenuVisible() && (!isMenuOnLeft() ? translation < (-minimumSwipeDistance) : translation > minimumSwipeDistance);
    }

    public int getMenuSnapTarget() {
        boolean isMenuOnLeft = isMenuOnLeft();
        int spaceForMenu = getSpaceForMenu();
        return isMenuOnLeft ? spaceForMenu : -spaceForMenu;
    }

    public boolean shouldSnapBack() {
        float translation = getTranslation();
        float snapBackThreshold = getSnapBackThreshold();
        if (isMenuOnLeft()) {
            if (translation < snapBackThreshold) {
                return true;
            }
        } else if (translation > (-snapBackThreshold)) {
            return true;
        }
        return false;
    }

    public boolean isSnappedAndOnSameSide() {
        return isMenuSnapped() && isMenuVisible() && isMenuSnappedOnLeft() == isMenuOnLeft();
    }

    public boolean canBeDismissed() {
        return getParent().canViewBeDismissed();
    }

    public void setDismissRtl(boolean z) {
        if (this.mMenuContainer != null) {
            createMenuViews(true, this.mIsForeground);
        }
    }
}
