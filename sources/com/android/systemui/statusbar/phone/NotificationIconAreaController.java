package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import androidx.collection.ArrayMap;
import com.android.internal.util.ContrastColorUtil;
import com.android.settingslib.Utils;
import com.android.systemui.C2006R$attr;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.Interpolators;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationListener.NotificationSettingsListener;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator.WakeUpListener;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

public class NotificationIconAreaController implements DarkReceiver, StateListener, WakeUpListener {
    private boolean mAnimationsEnabled;
    private int mAodIconAppearTranslation;
    private int mAodIconTint;
    private NotificationIconContainer mAodIcons;
    private boolean mAodIconsVisible;
    private final KeyguardBypassController mBypassController;
    private NotificationIconContainer mCenteredIcon;
    protected View mCenteredIconArea;
    private int mCenteredIconTint = -1;
    private StatusBarIconView mCenteredIconView;
    private Context mContext;
    private final ContrastColorUtil mContrastColorUtil;
    private final DozeParameters mDozeParameters;
    private int mIconHPadding;
    private int mIconSize;
    private int mIconTint = -1;
    private final NotificationMediaManager mMediaManager;
    protected View mNotificationIconArea;
    private NotificationIconContainer mNotificationIcons;
    /* access modifiers changed from: private */
    public ViewGroup mNotificationScrollLayout;
    final NotificationSettingsListener mSettingsListener = new NotificationSettingsListener() {
        public void onStatusBarIconsBehaviorChanged(boolean z) {
            NotificationIconAreaController.this.mShowLowPriority = !z;
            if (NotificationIconAreaController.this.mNotificationScrollLayout != null) {
                NotificationIconAreaController.this.updateStatusBarIcons();
            }
        }
    };
    private NotificationIconContainer mShelfIcons;
    /* access modifiers changed from: private */
    public boolean mShowLowPriority = true;
    private StatusBar mStatusBar;
    private final StatusBarStateController mStatusBarStateController;
    private final Rect mTintArea = new Rect();
    private final Runnable mUpdateStatusBarIcons = new Runnable() {
        public final void run() {
            NotificationIconAreaController.this.updateStatusBarIcons();
        }
    };
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;

    public NotificationIconAreaController(Context context, StatusBar statusBar, StatusBarStateController statusBarStateController, NotificationWakeUpCoordinator notificationWakeUpCoordinator, KeyguardBypassController keyguardBypassController, NotificationMediaManager notificationMediaManager, NotificationListener notificationListener, DozeParameters dozeParameters) {
        this.mStatusBar = statusBar;
        this.mContrastColorUtil = ContrastColorUtil.getInstance(context);
        this.mContext = context;
        this.mStatusBarStateController = statusBarStateController;
        statusBarStateController.addCallback(this);
        this.mMediaManager = notificationMediaManager;
        this.mDozeParameters = dozeParameters;
        this.mWakeUpCoordinator = notificationWakeUpCoordinator;
        notificationWakeUpCoordinator.addListener(this);
        this.mBypassController = keyguardBypassController;
        notificationListener.addNotificationSettingsListener(this.mSettingsListener);
        initializeNotificationAreaViews(context);
        reloadAodColor();
    }

    /* access modifiers changed from: protected */
    public View inflateIconArea(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(C2013R$layout.notification_icon_area, null);
    }

    /* access modifiers changed from: protected */
    public void initializeNotificationAreaViews(Context context) {
        reloadDimens(context);
        LayoutInflater from = LayoutInflater.from(context);
        View inflateIconArea = inflateIconArea(from);
        this.mNotificationIconArea = inflateIconArea;
        this.mNotificationIcons = (NotificationIconContainer) inflateIconArea.findViewById(C2011R$id.notificationIcons);
        this.mNotificationScrollLayout = this.mStatusBar.getNotificationScrollLayout();
        View inflate = from.inflate(C2013R$layout.center_icon_area, null);
        this.mCenteredIconArea = inflate;
        this.mCenteredIcon = (NotificationIconContainer) inflate.findViewById(C2011R$id.centeredIcon);
        initAodIcons();
    }

    public void initAodIcons() {
        boolean z = this.mAodIcons != null;
        if (z) {
            this.mAodIcons.setAnimationsEnabled(false);
            this.mAodIcons.removeAllViews();
        }
        NotificationIconContainer notificationIconContainer = (NotificationIconContainer) this.mStatusBar.getNotificationShadeWindowView().findViewById(C2011R$id.clock_notification_icon_container);
        this.mAodIcons = notificationIconContainer;
        notificationIconContainer.setOnLockScreen(true);
        updateAodIconsVisibility(false);
        updateAnimations();
        if (z) {
            updateAodNotificationIcons();
        }
    }

    public void setupShelf(NotificationShelf notificationShelf) {
        this.mShelfIcons = notificationShelf.getShelfIcons();
        notificationShelf.setCollapsedIcons(this.mNotificationIcons);
    }

    public void onDensityOrFontScaleChanged(Context context) {
        reloadDimens(context);
        LayoutParams generateIconLayoutParams = generateIconLayoutParams();
        for (int i = 0; i < this.mNotificationIcons.getChildCount(); i++) {
            this.mNotificationIcons.getChildAt(i).setLayoutParams(generateIconLayoutParams);
        }
        for (int i2 = 0; i2 < this.mShelfIcons.getChildCount(); i2++) {
            this.mShelfIcons.getChildAt(i2).setLayoutParams(generateIconLayoutParams);
        }
        for (int i3 = 0; i3 < this.mCenteredIcon.getChildCount(); i3++) {
            this.mCenteredIcon.getChildAt(i3).setLayoutParams(generateIconLayoutParams);
        }
        for (int i4 = 0; i4 < this.mAodIcons.getChildCount(); i4++) {
            this.mAodIcons.getChildAt(i4).setLayoutParams(generateIconLayoutParams);
        }
    }

    private LayoutParams generateIconLayoutParams() {
        return new LayoutParams(this.mIconSize + (this.mIconHPadding * 2), getHeight());
    }

    private void reloadDimens(Context context) {
        Resources resources = context.getResources();
        this.mIconSize = resources.getDimensionPixelSize(17105465);
        this.mIconHPadding = resources.getDimensionPixelSize(C2009R$dimen.status_bar_icon_padding);
        this.mAodIconAppearTranslation = resources.getDimensionPixelSize(C2009R$dimen.shelf_appear_translation);
    }

    public View getNotificationInnerAreaView() {
        return this.mNotificationIconArea;
    }

    public View getCenteredNotificationAreaView() {
        return this.mCenteredIconArea;
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        if (rect == null) {
            this.mTintArea.setEmpty();
        } else {
            this.mTintArea.set(rect);
        }
        View view = this.mNotificationIconArea;
        if (view == null) {
            this.mIconTint = i;
        } else if (DarkIconDispatcher.isInArea(rect, view)) {
            this.mIconTint = i;
        }
        View view2 = this.mCenteredIconArea;
        if (view2 == null) {
            this.mCenteredIconTint = i;
        } else if (DarkIconDispatcher.isInArea(rect, view2)) {
            this.mCenteredIconTint = i;
        }
        applyNotificationIconsTint();
    }

    /* access modifiers changed from: protected */
    public int getHeight() {
        return this.mStatusBar.getStatusBarHeight();
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowNotificationIcon(NotificationEntry notificationEntry, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8) {
        boolean z9 = (this.mCenteredIconView == null || notificationEntry.getIcons().getCenteredIcon() == null || !Objects.equals(notificationEntry.getIcons().getCenteredIcon(), this.mCenteredIconView)) ? false : true;
        if (z8) {
            return z9;
        }
        if (z6 && z9 && !notificationEntry.isRowHeadsUp()) {
            return false;
        }
        if (notificationEntry.getRanking().isAmbient() && !z) {
            return false;
        }
        if (z5 && notificationEntry.getKey().equals(this.mMediaManager.getMediaNotificationKey())) {
            return false;
        }
        if ((!z2 && notificationEntry.getImportance() < 3) || !notificationEntry.isTopLevelChild() || notificationEntry.getRow().getVisibility() == 8) {
            return false;
        }
        if (notificationEntry.isRowDismissed() && z3) {
            return false;
        }
        if (z4 && notificationEntry.isLastMessageFromReply()) {
            return false;
        }
        if (z || !notificationEntry.shouldSuppressStatusBar()) {
            return !z7 || !notificationEntry.showingPulsing() || (this.mWakeUpCoordinator.getNotificationsFullyHidden() && notificationEntry.isPulseSuppressed());
        }
        return false;
    }

    public void updateNotificationIcons() {
        updateStatusBarIcons();
        updateShelfIcons();
        updateCenterIcon();
        updateAodNotificationIcons();
        applyNotificationIconsTint();
    }

    private void updateShelfIcons() {
        updateIconsForLayout(C1390xeccd2fb2.INSTANCE, this.mShelfIcons, true, true, false, false, false, false, false, false);
    }

    public void updateStatusBarIcons() {
        updateIconsForLayout(C1393x339b1a97.INSTANCE, this.mNotificationIcons, false, this.mShowLowPriority, true, true, false, true, false, false);
    }

    private void updateCenterIcon() {
        updateIconsForLayout(C1389x603b2dbf.INSTANCE, this.mCenteredIcon, false, true, false, false, false, false, false, true);
    }

    public void updateAodNotificationIcons() {
        updateIconsForLayout(C1391x43d84486.INSTANCE, this.mAodIcons, false, true, true, true, true, true, this.mBypassController.getBypassEnabled(), false);
    }

    /* access modifiers changed from: 0000 */
    public boolean shouldShouldLowPriorityIcons() {
        return this.mShowLowPriority;
    }

    private void updateIconsForLayout(Function<NotificationEntry, StatusBarIconView> function, NotificationIconContainer notificationIconContainer, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8) {
        NotificationIconContainer notificationIconContainer2 = notificationIconContainer;
        ArrayList arrayList = new ArrayList(this.mNotificationScrollLayout.getChildCount());
        for (int i = 0; i < this.mNotificationScrollLayout.getChildCount(); i++) {
            View childAt = this.mNotificationScrollLayout.getChildAt(i);
            if (childAt instanceof ExpandableNotificationRow) {
                NotificationEntry entry = ((ExpandableNotificationRow) childAt).getEntry();
                if (shouldShowNotificationIcon(entry, z, z2, z3, z4, z5, z6, z7, z8)) {
                    StatusBarIconView statusBarIconView = (StatusBarIconView) function.apply(entry);
                    if (statusBarIconView != null) {
                        arrayList.add(statusBarIconView);
                    }
                }
            }
            Function<NotificationEntry, StatusBarIconView> function2 = function;
        }
        ArrayMap arrayMap = new ArrayMap();
        ArrayList arrayList2 = new ArrayList();
        for (int i2 = 0; i2 < notificationIconContainer.getChildCount(); i2++) {
            View childAt2 = notificationIconContainer2.getChildAt(i2);
            if ((childAt2 instanceof StatusBarIconView) && !arrayList.contains(childAt2)) {
                StatusBarIconView statusBarIconView2 = (StatusBarIconView) childAt2;
                String groupKey = statusBarIconView2.getNotification().getGroupKey();
                int i3 = 0;
                boolean z9 = false;
                while (true) {
                    if (i3 >= arrayList.size()) {
                        break;
                    }
                    StatusBarIconView statusBarIconView3 = (StatusBarIconView) arrayList.get(i3);
                    if (statusBarIconView3.getSourceIcon().sameAs(statusBarIconView2.getSourceIcon()) && statusBarIconView3.getNotification().getGroupKey().equals(groupKey)) {
                        if (z9) {
                            z9 = false;
                            break;
                        }
                        z9 = true;
                    }
                    i3++;
                }
                if (z9) {
                    ArrayList arrayList3 = (ArrayList) arrayMap.get(groupKey);
                    if (arrayList3 == null) {
                        arrayList3 = new ArrayList();
                        arrayMap.put(groupKey, arrayList3);
                    }
                    arrayList3.add(statusBarIconView2.getStatusBarIcon());
                }
                arrayList2.add(statusBarIconView2);
            }
        }
        ArrayList arrayList4 = new ArrayList();
        for (String str : arrayMap.keySet()) {
            if (((ArrayList) arrayMap.get(str)).size() != 1) {
                arrayList4.add(str);
            }
        }
        arrayMap.removeAll(arrayList4);
        notificationIconContainer2.setReplacingIcons(arrayMap);
        int size = arrayList2.size();
        for (int i4 = 0; i4 < size; i4++) {
            notificationIconContainer2.removeView((View) arrayList2.get(i4));
        }
        LayoutParams generateIconLayoutParams = generateIconLayoutParams();
        for (int i5 = 0; i5 < arrayList.size(); i5++) {
            StatusBarIconView statusBarIconView4 = (StatusBarIconView) arrayList.get(i5);
            notificationIconContainer2.removeTransientView(statusBarIconView4);
            if (statusBarIconView4.getParent() == null) {
                if (z3) {
                    statusBarIconView4.setOnDismissListener(this.mUpdateStatusBarIcons);
                }
                notificationIconContainer2.addView(statusBarIconView4, i5, generateIconLayoutParams);
            }
        }
        notificationIconContainer2.setChangingViewPositions(true);
        int childCount = notificationIconContainer.getChildCount();
        for (int i6 = 0; i6 < childCount; i6++) {
            StatusBarIconView statusBarIconView5 = (StatusBarIconView) arrayList.get(i6);
            if (notificationIconContainer2.getChildAt(i6) != statusBarIconView5) {
                notificationIconContainer2.removeView(statusBarIconView5);
                notificationIconContainer2.addView(statusBarIconView5, i6);
            }
        }
        notificationIconContainer2.setChangingViewPositions(false);
        notificationIconContainer2.setReplacingIcons(null);
    }

    private void applyNotificationIconsTint() {
        for (int i = 0; i < this.mNotificationIcons.getChildCount(); i++) {
            StatusBarIconView statusBarIconView = (StatusBarIconView) this.mNotificationIcons.getChildAt(i);
            if (statusBarIconView.getWidth() != 0) {
                updateTintForIcon(statusBarIconView, this.mIconTint);
            } else {
                statusBarIconView.executeOnLayout(new Runnable(statusBarIconView) {
                    public final /* synthetic */ StatusBarIconView f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        NotificationIconAreaController.this.mo17580x7468f248(this.f$1);
                    }
                });
            }
        }
        for (int i2 = 0; i2 < this.mCenteredIcon.getChildCount(); i2++) {
            StatusBarIconView statusBarIconView2 = (StatusBarIconView) this.mCenteredIcon.getChildAt(i2);
            if (statusBarIconView2.getWidth() != 0) {
                updateTintForIcon(statusBarIconView2, this.mCenteredIconTint);
            } else {
                statusBarIconView2.executeOnLayout(new Runnable(statusBarIconView2) {
                    public final /* synthetic */ StatusBarIconView f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        NotificationIconAreaController.this.mo17581xfc993227(this.f$1);
                    }
                });
            }
        }
        updateAodIconColors();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyNotificationIconsTint$4 */
    public /* synthetic */ void mo17580x7468f248(StatusBarIconView statusBarIconView) {
        updateTintForIcon(statusBarIconView, this.mIconTint);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyNotificationIconsTint$5 */
    public /* synthetic */ void mo17581xfc993227(StatusBarIconView statusBarIconView) {
        updateTintForIcon(statusBarIconView, this.mCenteredIconTint);
    }

    private void updateTintForIcon(StatusBarIconView statusBarIconView, int i) {
        int i2 = 0;
        if (!Boolean.TRUE.equals(statusBarIconView.getTag(C2011R$id.icon_is_pre_L)) || NotificationUtils.isGrayscale(statusBarIconView, this.mContrastColorUtil)) {
            i2 = DarkIconDispatcher.getTint(this.mTintArea, statusBarIconView, i);
        }
        statusBarIconView.setStaticDrawableColor(i2);
        statusBarIconView.setDecorColor(i);
    }

    public void showIconCentered(NotificationEntry notificationEntry) {
        StatusBarIconView centeredIcon = notificationEntry == null ? null : notificationEntry.getIcons().getCenteredIcon();
        if (!Objects.equals(this.mCenteredIconView, centeredIcon)) {
            this.mCenteredIconView = centeredIcon;
            updateNotificationIcons();
        }
    }

    public void showIconIsolated(StatusBarIconView statusBarIconView, boolean z) {
        this.mNotificationIcons.showIconIsolated(statusBarIconView, z);
    }

    public void setIsolatedIconLocation(Rect rect, boolean z) {
        this.mNotificationIcons.setIsolatedIconLocation(rect, z);
    }

    public void onDozingChanged(boolean z) {
        this.mAodIcons.setDozing(z, this.mDozeParameters.getAlwaysOn() && !this.mDozeParameters.getDisplayNeedsBlanking(), 0);
    }

    public void setAnimationsEnabled(boolean z) {
        this.mAnimationsEnabled = z;
        updateAnimations();
    }

    public void onStateChanged(int i) {
        updateAodIconsVisibility(false);
        updateAnimations();
    }

    private void updateAnimations() {
        boolean z = true;
        boolean z2 = this.mStatusBarStateController.getState() == 0;
        this.mAodIcons.setAnimationsEnabled(this.mAnimationsEnabled && !z2);
        this.mCenteredIcon.setAnimationsEnabled(this.mAnimationsEnabled && z2);
        NotificationIconContainer notificationIconContainer = this.mNotificationIcons;
        if (!this.mAnimationsEnabled || !z2) {
            z = false;
        }
        notificationIconContainer.setAnimationsEnabled(z);
    }

    public void onThemeChanged() {
        reloadAodColor();
        updateAodIconColors();
    }

    public void appearAodIcons() {
        if (this.mDozeParameters.shouldControlScreenOff()) {
            this.mAodIcons.setTranslationY((float) (-this.mAodIconAppearTranslation));
            this.mAodIcons.setAlpha(0.0f);
            animateInAodIconTranslation();
            this.mAodIcons.animate().alpha(1.0f).setInterpolator(Interpolators.LINEAR).setDuration(200).start();
        }
    }

    private void animateInAodIconTranslation() {
        this.mAodIcons.animate().setInterpolator(Interpolators.DECELERATE_QUINT).translationY(0.0f).setDuration(200).start();
    }

    private void reloadAodColor() {
        this.mAodIconTint = Utils.getColorAttrDefaultColor(this.mContext, C2006R$attr.wallpaperTextColor);
    }

    private void updateAodIconColors() {
        for (int i = 0; i < this.mAodIcons.getChildCount(); i++) {
            StatusBarIconView statusBarIconView = (StatusBarIconView) this.mAodIcons.getChildAt(i);
            if (statusBarIconView.getWidth() != 0) {
                updateTintForIcon(statusBarIconView, this.mAodIconTint);
            } else {
                statusBarIconView.executeOnLayout(new Runnable(statusBarIconView) {
                    public final /* synthetic */ StatusBarIconView f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        NotificationIconAreaController.this.lambda$updateAodIconColors$6$NotificationIconAreaController(this.f$1);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateAodIconColors$6 */
    public /* synthetic */ void lambda$updateAodIconColors$6$NotificationIconAreaController(StatusBarIconView statusBarIconView) {
        updateTintForIcon(statusBarIconView, this.mAodIconTint);
    }

    public void onFullyHiddenChanged(boolean z) {
        boolean z2 = true;
        if (!this.mBypassController.getBypassEnabled()) {
            if (!this.mDozeParameters.getAlwaysOn() || this.mDozeParameters.getDisplayNeedsBlanking()) {
                z2 = false;
            }
            z2 &= z;
        }
        updateAodIconsVisibility(z2);
        updateAodNotificationIcons();
    }

    public void onPulseExpansionChanged(boolean z) {
        if (z) {
            updateAodIconsVisibility(true);
        }
    }

    private void updateAodIconsVisibility(boolean z) {
        boolean z2 = true;
        int i = 0;
        boolean z3 = this.mBypassController.getBypassEnabled() || this.mWakeUpCoordinator.getNotificationsFullyHidden();
        if (this.mStatusBarStateController.getState() != 1) {
            z3 = false;
        }
        if (z3 && this.mWakeUpCoordinator.isPulseExpanding()) {
            z3 = false;
        }
        if (this.mAodIconsVisible != z3) {
            this.mAodIconsVisible = z3;
            this.mAodIcons.animate().cancel();
            if (z) {
                if (this.mAodIcons.getVisibility() == 0) {
                    z2 = false;
                }
                if (!this.mAodIconsVisible) {
                    animateInAodIconTranslation();
                    CrossFadeHelper.fadeOut(this.mAodIcons);
                } else if (z2) {
                    this.mAodIcons.setVisibility(0);
                    this.mAodIcons.setAlpha(1.0f);
                    appearAodIcons();
                } else {
                    animateInAodIconTranslation();
                    CrossFadeHelper.fadeIn(this.mAodIcons);
                }
            } else {
                this.mAodIcons.setAlpha(1.0f);
                this.mAodIcons.setTranslationY(0.0f);
                NotificationIconContainer notificationIconContainer = this.mAodIcons;
                if (!z3) {
                    i = 4;
                }
                notificationIconContainer.setVisibility(i);
            }
        }
    }
}
