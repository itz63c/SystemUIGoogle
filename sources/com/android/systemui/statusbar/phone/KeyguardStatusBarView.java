package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.systemui.BatteryMeterView;
import com.android.systemui.C2006R$attr;
import com.android.systemui.C2008R$color;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.ScreenDecorations.DisplayCutoutView;
import com.android.systemui.p007qs.QSPanel;
import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.StatusBarIconController.TintedIconManager;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcher;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserInfoController.OnUserInfoChangedListener;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class KeyguardStatusBarView extends RelativeLayout implements BatteryStateChangeCallback, OnUserInfoChangedListener, ConfigurationListener {
    private boolean mBatteryCharging;
    private BatteryController mBatteryController;
    private boolean mBatteryListening;
    private BatteryMeterView mBatteryView;
    private TextView mCarrierLabel;
    private int mCutoutSideNudge = 0;
    private View mCutoutSpace;
    private DisplayCutout mDisplayCutout;
    private final Rect mEmptyRect = new Rect(0, 0, 0, 0);
    private TintedIconManager mIconManager;
    private KeyguardUserSwitcher mKeyguardUserSwitcher;
    private boolean mKeyguardUserSwitcherShowing;
    private int mLayoutState = 0;
    private ImageView mMultiUserAvatar;
    /* access modifiers changed from: private */
    public MultiUserSwitch mMultiUserSwitch;
    private int mRoundedCornerPadding = 0;
    private boolean mShowPercentAvailable;
    /* access modifiers changed from: private */
    public ViewGroup mStatusIconArea;
    private int mSystemIconsBaseMargin;
    /* access modifiers changed from: private */
    public View mSystemIconsContainer;
    private int mSystemIconsSwitcherHiddenExpandedMargin;
    private UserSwitcherController mUserSwitcherController;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void onPowerSaveChanged(boolean z) {
    }

    public KeyguardStatusBarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mSystemIconsContainer = findViewById(C2011R$id.system_icons_container);
        this.mMultiUserSwitch = (MultiUserSwitch) findViewById(C2011R$id.multi_user_switch);
        this.mMultiUserAvatar = (ImageView) findViewById(C2011R$id.multi_user_avatar);
        this.mCarrierLabel = (TextView) findViewById(C2011R$id.keyguard_carrier_text);
        this.mBatteryView = (BatteryMeterView) this.mSystemIconsContainer.findViewById(C2011R$id.battery);
        this.mCutoutSpace = findViewById(C2011R$id.cutout_space_view);
        this.mStatusIconArea = (ViewGroup) findViewById(C2011R$id.status_icon_area);
        StatusIconContainer statusIconContainer = (StatusIconContainer) findViewById(C2011R$id.statusIcons);
        loadDimens();
        updateUserSwitcher();
        this.mBatteryController = (BatteryController) Dependency.get(BatteryController.class);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) this.mMultiUserAvatar.getLayoutParams();
        int dimensionPixelSize = getResources().getDimensionPixelSize(C2009R$dimen.multi_user_avatar_keyguard_size);
        marginLayoutParams.height = dimensionPixelSize;
        marginLayoutParams.width = dimensionPixelSize;
        this.mMultiUserAvatar.setLayoutParams(marginLayoutParams);
        MarginLayoutParams marginLayoutParams2 = (MarginLayoutParams) this.mMultiUserSwitch.getLayoutParams();
        marginLayoutParams2.width = getResources().getDimensionPixelSize(C2009R$dimen.multi_user_switch_width_keyguard);
        marginLayoutParams2.setMarginEnd(getResources().getDimensionPixelSize(C2009R$dimen.multi_user_switch_keyguard_margin));
        this.mMultiUserSwitch.setLayoutParams(marginLayoutParams2);
        MarginLayoutParams marginLayoutParams3 = (MarginLayoutParams) this.mSystemIconsContainer.getLayoutParams();
        marginLayoutParams3.setMarginStart(getResources().getDimensionPixelSize(C2009R$dimen.system_icons_super_container_margin_start));
        this.mSystemIconsContainer.setLayoutParams(marginLayoutParams3);
        View view = this.mSystemIconsContainer;
        view.setPaddingRelative(view.getPaddingStart(), this.mSystemIconsContainer.getPaddingTop(), getResources().getDimensionPixelSize(C2009R$dimen.system_icons_keyguard_padding_end), this.mSystemIconsContainer.getPaddingBottom());
        this.mCarrierLabel.setTextSize(0, (float) getResources().getDimensionPixelSize(17105490));
        MarginLayoutParams marginLayoutParams4 = (MarginLayoutParams) this.mCarrierLabel.getLayoutParams();
        marginLayoutParams4.setMarginStart(getResources().getDimensionPixelSize(C2009R$dimen.keyguard_carrier_text_margin));
        this.mCarrierLabel.setLayoutParams(marginLayoutParams4);
        updateKeyguardStatusBarHeight();
    }

    private void updateKeyguardStatusBarHeight() {
        DisplayCutout displayCutout = this.mDisplayCutout;
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) getLayoutParams();
        marginLayoutParams.height = getResources().getDimensionPixelSize(C2009R$dimen.status_bar_header_height_keyguard) + (displayCutout == null ? 0 : displayCutout.getWaterfallInsets().top);
        setLayoutParams(marginLayoutParams);
    }

    private void loadDimens() {
        Resources resources = getResources();
        this.mSystemIconsSwitcherHiddenExpandedMargin = resources.getDimensionPixelSize(C2009R$dimen.system_icons_switcher_hidden_expanded_margin);
        this.mSystemIconsBaseMargin = resources.getDimensionPixelSize(C2009R$dimen.system_icons_super_container_avatarless_margin_end);
        this.mCutoutSideNudge = getResources().getDimensionPixelSize(C2009R$dimen.display_cutout_margin_consumption);
        this.mShowPercentAvailable = getContext().getResources().getBoolean(17891374);
        this.mRoundedCornerPadding = resources.getDimensionPixelSize(C2009R$dimen.rounded_corner_content_padding);
    }

    private void updateVisibilities() {
        boolean z = false;
        if (this.mMultiUserSwitch.getParent() == this.mStatusIconArea || this.mKeyguardUserSwitcherShowing) {
            ViewParent parent = this.mMultiUserSwitch.getParent();
            ViewGroup viewGroup = this.mStatusIconArea;
            if (parent == viewGroup && this.mKeyguardUserSwitcherShowing) {
                viewGroup.removeView(this.mMultiUserSwitch);
            }
        } else {
            if (this.mMultiUserSwitch.getParent() != null) {
                getOverlay().remove(this.mMultiUserSwitch);
            }
            this.mStatusIconArea.addView(this.mMultiUserSwitch, 0);
        }
        if (this.mKeyguardUserSwitcher == null) {
            if (this.mMultiUserSwitch.isMultiUserEnabled()) {
                this.mMultiUserSwitch.setVisibility(0);
            } else {
                this.mMultiUserSwitch.setVisibility(8);
            }
        }
        BatteryMeterView batteryMeterView = this.mBatteryView;
        if (this.mBatteryCharging && this.mShowPercentAvailable) {
            z = true;
        }
        batteryMeterView.setForceShowPercent(z);
    }

    private void updateSystemIconsLayoutParams() {
        LayoutParams layoutParams = (LayoutParams) this.mSystemIconsContainer.getLayoutParams();
        int i = this.mMultiUserSwitch.getVisibility() == 8 ? this.mSystemIconsBaseMargin : 0;
        if (this.mKeyguardUserSwitcherShowing) {
            i = this.mSystemIconsSwitcherHiddenExpandedMargin;
        }
        if (i != layoutParams.getMarginEnd()) {
            layoutParams.setMarginEnd(i);
            this.mSystemIconsContainer.setLayoutParams(layoutParams);
        }
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        this.mLayoutState = 0;
        if (updateLayoutConsideringCutout()) {
            requestLayout();
        }
        return super.onApplyWindowInsets(windowInsets);
    }

    private boolean updateLayoutConsideringCutout() {
        this.mDisplayCutout = getRootWindowInsets().getDisplayCutout();
        updateKeyguardStatusBarHeight();
        Pair cornerCutoutMargins = StatusBarWindowView.cornerCutoutMargins(this.mDisplayCutout, getDisplay());
        updatePadding(cornerCutoutMargins);
        if (this.mDisplayCutout == null || cornerCutoutMargins != null) {
            return updateLayoutParamsNoCutout();
        }
        return updateLayoutParamsForCutout();
    }

    private void updatePadding(Pair<Integer, Integer> pair) {
        DisplayCutout displayCutout = this.mDisplayCutout;
        int i = displayCutout == null ? 0 : displayCutout.getWaterfallInsets().top;
        Pair paddingNeededForCutoutAndRoundedCorner = StatusBarWindowView.paddingNeededForCutoutAndRoundedCorner(this.mDisplayCutout, pair, this.mRoundedCornerPadding);
        setPadding(((Integer) paddingNeededForCutoutAndRoundedCorner.first).intValue(), i, ((Integer) paddingNeededForCutoutAndRoundedCorner.second).intValue(), 0);
    }

    private boolean updateLayoutParamsNoCutout() {
        if (this.mLayoutState == 2) {
            return false;
        }
        this.mLayoutState = 2;
        View view = this.mCutoutSpace;
        if (view != null) {
            view.setVisibility(8);
        }
        ((RelativeLayout.LayoutParams) this.mCarrierLabel.getLayoutParams()).addRule(16, C2011R$id.status_icon_area);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mStatusIconArea.getLayoutParams();
        layoutParams.removeRule(1);
        layoutParams.width = -2;
        ((LayoutParams) this.mSystemIconsContainer.getLayoutParams()).setMarginStart(getResources().getDimensionPixelSize(C2009R$dimen.system_icons_super_container_margin_start));
        return true;
    }

    private boolean updateLayoutParamsForCutout() {
        if (this.mLayoutState == 1) {
            return false;
        }
        this.mLayoutState = 1;
        if (this.mCutoutSpace == null) {
            updateLayoutParamsNoCutout();
        }
        Rect rect = new Rect();
        DisplayCutoutView.boundsFromDirection(this.mDisplayCutout, 48, rect);
        this.mCutoutSpace.setVisibility(0);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mCutoutSpace.getLayoutParams();
        int i = rect.left;
        int i2 = this.mCutoutSideNudge;
        rect.left = i + i2;
        rect.right -= i2;
        layoutParams.width = rect.width();
        layoutParams.height = rect.height();
        layoutParams.addRule(13);
        ((RelativeLayout.LayoutParams) this.mCarrierLabel.getLayoutParams()).addRule(16, C2011R$id.cutout_space_view);
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.mStatusIconArea.getLayoutParams();
        layoutParams2.addRule(1, C2011R$id.cutout_space_view);
        layoutParams2.width = -1;
        ((LayoutParams) this.mSystemIconsContainer.getLayoutParams()).setMarginStart(0);
        return true;
    }

    public void setListening(boolean z) {
        if (z != this.mBatteryListening) {
            this.mBatteryListening = z;
            if (z) {
                this.mBatteryController.addCallback(this);
            } else {
                this.mBatteryController.removeCallback(this);
            }
        }
    }

    private void updateUserSwitcher() {
        boolean z = this.mKeyguardUserSwitcher != null;
        this.mMultiUserSwitch.setClickable(z);
        this.mMultiUserSwitch.setFocusable(z);
        this.mMultiUserSwitch.setKeyguardMode(z);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        UserInfoController userInfoController = (UserInfoController) Dependency.get(UserInfoController.class);
        userInfoController.addCallback(this);
        UserSwitcherController userSwitcherController = (UserSwitcherController) Dependency.get(UserSwitcherController.class);
        this.mUserSwitcherController = userSwitcherController;
        this.mMultiUserSwitch.setUserSwitcherController(userSwitcherController);
        userInfoController.reloadUserInfo();
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        this.mIconManager = new TintedIconManager((ViewGroup) findViewById(C2011R$id.statusIcons), (CommandQueue) Dependency.get(CommandQueue.class));
        ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).addIconGroup(this.mIconManager);
        onThemeChanged();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((UserInfoController) Dependency.get(UserInfoController.class)).removeCallback(this);
        ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).removeIconGroup(this.mIconManager);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
    }

    public void onUserInfoChanged(String str, Drawable drawable, String str2) {
        this.mMultiUserAvatar.setImageDrawable(drawable);
    }

    public void setQSPanel(QSPanel qSPanel) {
        this.mMultiUserSwitch.setQsPanel(qSPanel);
    }

    public void onBatteryLevelChanged(int i, boolean z, boolean z2) {
        if (this.mBatteryCharging != z2) {
            this.mBatteryCharging = z2;
            updateVisibilities();
        }
    }

    public void setKeyguardUserSwitcher(KeyguardUserSwitcher keyguardUserSwitcher) {
        this.mKeyguardUserSwitcher = keyguardUserSwitcher;
        this.mMultiUserSwitch.setKeyguardUserSwitcher(keyguardUserSwitcher);
        updateUserSwitcher();
    }

    public void setKeyguardUserSwitcherShowing(boolean z, boolean z2) {
        this.mKeyguardUserSwitcherShowing = z;
        if (z2) {
            animateNextLayoutChange();
        }
        updateVisibilities();
        updateLayoutConsideringCutout();
        updateSystemIconsLayoutParams();
    }

    private void animateNextLayoutChange() {
        final int left = this.mSystemIconsContainer.getLeft();
        final boolean z = this.mMultiUserSwitch.getParent() == this.mStatusIconArea;
        getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            public boolean onPreDraw() {
                KeyguardStatusBarView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                boolean z = z && KeyguardStatusBarView.this.mMultiUserSwitch.getParent() != KeyguardStatusBarView.this.mStatusIconArea;
                KeyguardStatusBarView.this.mSystemIconsContainer.setX((float) left);
                KeyguardStatusBarView.this.mSystemIconsContainer.animate().translationX(0.0f).setDuration(400).setStartDelay(z ? 300 : 0).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).start();
                if (z) {
                    KeyguardStatusBarView.this.getOverlay().add(KeyguardStatusBarView.this.mMultiUserSwitch);
                    KeyguardStatusBarView.this.mMultiUserSwitch.animate().alpha(0.0f).setDuration(300).setStartDelay(0).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(new Runnable() {
                        public final void run() {
                            C14831.this.lambda$onPreDraw$0$KeyguardStatusBarView$1();
                        }
                    }).start();
                } else {
                    KeyguardStatusBarView.this.mMultiUserSwitch.setAlpha(0.0f);
                    KeyguardStatusBarView.this.mMultiUserSwitch.animate().alpha(1.0f).setDuration(300).setStartDelay(200).setInterpolator(Interpolators.ALPHA_IN);
                }
                return true;
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onPreDraw$0 */
            public /* synthetic */ void lambda$onPreDraw$0$KeyguardStatusBarView$1() {
                KeyguardStatusBarView.this.mMultiUserSwitch.setAlpha(1.0f);
                KeyguardStatusBarView.this.getOverlay().remove(KeyguardStatusBarView.this.mMultiUserSwitch);
            }
        });
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 0) {
            this.mSystemIconsContainer.animate().cancel();
            this.mSystemIconsContainer.setTranslationX(0.0f);
            this.mMultiUserSwitch.animate().cancel();
            this.mMultiUserSwitch.setAlpha(1.0f);
            return;
        }
        updateVisibilities();
        updateSystemIconsLayoutParams();
    }

    public void onThemeChanged() {
        this.mBatteryView.setColorsFromContext(this.mContext);
        updateIconsAndTextColors();
        ((UserInfoControllerImpl) Dependency.get(UserInfoController.class)).onDensityOrFontScaleChanged();
    }

    public void onDensityOrFontScaleChanged() {
        loadDimens();
    }

    public void onOverlayChanged() {
        this.mCarrierLabel.setTextAppearance(Utils.getThemeAttr(this.mContext, 16842818));
        onThemeChanged();
        this.mBatteryView.updatePercentView();
    }

    private void updateIconsAndTextColors() {
        int i;
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(this.mContext, C2006R$attr.wallpaperTextColor);
        Context context = this.mContext;
        if (((double) Color.luminance(colorAttrDefaultColor)) < 0.5d) {
            i = C2008R$color.dark_mode_icon_color_single_tone;
        } else {
            i = C2008R$color.light_mode_icon_color_single_tone;
        }
        int colorStateListDefaultColor = Utils.getColorStateListDefaultColor(context, i);
        float f = colorAttrDefaultColor == -1 ? 0.0f : 1.0f;
        this.mCarrierLabel.setTextColor(colorStateListDefaultColor);
        TintedIconManager tintedIconManager = this.mIconManager;
        if (tintedIconManager != null) {
            tintedIconManager.setTint(colorStateListDefaultColor);
        }
        applyDarkness(C2011R$id.battery, this.mEmptyRect, f, colorStateListDefaultColor);
        applyDarkness(C2011R$id.clock, this.mEmptyRect, f, colorStateListDefaultColor);
    }

    private void applyDarkness(int i, Rect rect, float f, int i2) {
        View findViewById = findViewById(i);
        if (findViewById instanceof DarkReceiver) {
            ((DarkReceiver) findViewById).onDarkChanged(rect, f, i2);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("KeyguardStatusBarView:");
        StringBuilder sb = new StringBuilder();
        sb.append("  mBatteryCharging: ");
        sb.append(this.mBatteryCharging);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mKeyguardUserSwitcherShowing: ");
        sb2.append(this.mKeyguardUserSwitcherShowing);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  mBatteryListening: ");
        sb3.append(this.mBatteryListening);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("  mLayoutState: ");
        sb4.append(this.mLayoutState);
        printWriter.println(sb4.toString());
        BatteryMeterView batteryMeterView = this.mBatteryView;
        if (batteryMeterView != null) {
            batteryMeterView.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
