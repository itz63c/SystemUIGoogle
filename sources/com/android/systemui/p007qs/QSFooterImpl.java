package com.android.systemui.p007qs;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.settingslib.Utils;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.drawable.UserIconDrawable;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.p007qs.TouchAnimator.Builder;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.phone.MultiUserSwitch;
import com.android.systemui.statusbar.phone.SettingsButton;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserInfoController.OnUserInfoChangedListener;
import com.android.systemui.tuner.TunerService;

/* renamed from: com.android.systemui.qs.QSFooterImpl */
public class QSFooterImpl extends FrameLayout implements QSFooter, OnClickListener, OnUserInfoChangedListener {
    private View mActionsContainer;
    private final ActivityStarter mActivityStarter;
    private final ContentObserver mDeveloperSettingsObserver;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private View mDragHandle;
    protected View mEdit;
    protected View mEditContainer;
    private OnClickListener mExpandClickListener;
    private boolean mExpanded;
    private float mExpansionAmount;
    protected TouchAnimator mFooterAnimator;
    private boolean mListening;
    private ImageView mMultiUserAvatar;
    protected MultiUserSwitch mMultiUserSwitch;
    private PageIndicator mPageIndicator;
    private boolean mQsDisabled;
    private QSPanel mQsPanel;
    private QuickQSPanel mQuickQsPanel;
    private SettingsButton mSettingsButton;
    private TouchAnimator mSettingsCogAnimator;
    protected View mSettingsContainer;
    private final UserInfoController mUserInfoController;

    static /* synthetic */ void lambda$onClick$4() {
    }

    public QSFooterImpl(Context context, AttributeSet attributeSet, ActivityStarter activityStarter, UserInfoController userInfoController, DeviceProvisionedController deviceProvisionedController) {
        super(context, attributeSet);
        this.mDeveloperSettingsObserver = new ContentObserver(new Handler(this.mContext.getMainLooper())) {
            public void onChange(boolean z, Uri uri) {
                super.onChange(z, uri);
                QSFooterImpl.this.setBuildText();
            }
        };
        this.mActivityStarter = activityStarter;
        this.mUserInfoController = userInfoController;
        this.mDeviceProvisionedController = deviceProvisionedController;
    }

    public QSFooterImpl(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, (ActivityStarter) Dependency.get(ActivityStarter.class), (UserInfoController) Dependency.get(UserInfoController.class), (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class));
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        View findViewById = findViewById(16908291);
        this.mEdit = findViewById;
        findViewById.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                QSFooterImpl.this.lambda$onFinishInflate$1$QSFooterImpl(view);
            }
        });
        this.mPageIndicator = (PageIndicator) findViewById(C2011R$id.footer_page_indicator);
        this.mSettingsButton = (SettingsButton) findViewById(C2011R$id.settings_button);
        this.mSettingsContainer = findViewById(C2011R$id.settings_button_container);
        this.mSettingsButton.setOnClickListener(this);
        MultiUserSwitch multiUserSwitch = (MultiUserSwitch) findViewById(C2011R$id.multi_user_switch);
        this.mMultiUserSwitch = multiUserSwitch;
        this.mMultiUserAvatar = (ImageView) multiUserSwitch.findViewById(C2011R$id.multi_user_avatar);
        this.mDragHandle = findViewById(C2011R$id.qs_drag_handle_view);
        this.mActionsContainer = findViewById(C2011R$id.qs_footer_actions_container);
        this.mEditContainer = findViewById(C2011R$id.qs_footer_actions_edit_container);
        ((RippleDrawable) this.mSettingsButton.getBackground()).setForceSoftware(true);
        updateResources();
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                QSFooterImpl.this.lambda$onFinishInflate$2$QSFooterImpl(view, i, i2, i3, i4, i5, i6, i7, i8);
            }
        });
        setImportantForAccessibility(1);
        updateEverything();
        setBuildText();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflate$1 */
    public /* synthetic */ void lambda$onFinishInflate$1$QSFooterImpl(View view) {
        this.mActivityStarter.postQSRunnableDismissingKeyguard(new Runnable(view) {
            public final /* synthetic */ View f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                QSFooterImpl.this.lambda$onFinishInflate$0$QSFooterImpl(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflate$0 */
    public /* synthetic */ void lambda$onFinishInflate$0$QSFooterImpl(View view) {
        this.mQsPanel.showEdit(view);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflate$2 */
    public /* synthetic */ void lambda$onFinishInflate$2$QSFooterImpl(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        updateAnimator(i3 - i);
    }

    /* access modifiers changed from: private */
    public void setBuildText() {
        TextView textView = (TextView) findViewById(C2011R$id.build);
        if (textView != null) {
            if (DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(this.mContext)) {
                textView.setText(this.mContext.getString(17039668, new Object[]{VERSION.RELEASE_OR_CODENAME, Build.ID}));
                textView.setVisibility(0);
            } else {
                textView.setVisibility(8);
            }
        }
    }

    private void updateAnimator(int i) {
        int i2;
        QuickQSPanel quickQSPanel = this.mQuickQsPanel;
        if (quickQSPanel != null) {
            i2 = quickQSPanel.getNumQuickTiles();
        } else {
            i2 = QuickQSPanel.getDefaultMaxTiles();
        }
        int dimensionPixelSize = (i - ((this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.qs_quick_tile_size) - this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.qs_quick_tile_padding)) * i2)) / (i2 - 1);
        int dimensionPixelOffset = this.mContext.getResources().getDimensionPixelOffset(C2009R$dimen.default_gear_space);
        Builder builder = new Builder();
        View view = this.mSettingsContainer;
        float[] fArr = new float[2];
        int i3 = dimensionPixelSize - dimensionPixelOffset;
        if (!isLayoutRtl()) {
            i3 = -i3;
        }
        fArr[0] = (float) i3;
        fArr[1] = 0.0f;
        builder.addFloat(view, "translationX", fArr);
        builder.addFloat(this.mSettingsButton, "rotation", -120.0f, 0.0f);
        this.mSettingsCogAnimator = builder.build();
        setExpansion(this.mExpansionAmount);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateResources();
    }

    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        updateResources();
    }

    private void updateResources() {
        updateFooterAnimator();
    }

    private void updateFooterAnimator() {
        this.mFooterAnimator = createFooterAnimator();
    }

    private TouchAnimator createFooterAnimator() {
        Builder builder = new Builder();
        String str = "alpha";
        builder.addFloat(this.mActionsContainer, str, 0.0f, 1.0f);
        builder.addFloat(this.mEditContainer, str, 0.0f, 1.0f);
        builder.addFloat(this.mDragHandle, str, 1.0f, 0.0f, 0.0f);
        builder.addFloat(this.mPageIndicator, str, 0.0f, 1.0f);
        builder.setStartDelay(0.15f);
        return builder.build();
    }

    public void setKeyguardShowing(boolean z) {
        setExpansion(this.mExpansionAmount);
    }

    public void setExpandClickListener(OnClickListener onClickListener) {
        this.mExpandClickListener = onClickListener;
    }

    public void setExpanded(boolean z) {
        if (this.mExpanded != z) {
            this.mExpanded = z;
            updateEverything();
        }
    }

    public void setExpansion(float f) {
        this.mExpansionAmount = f;
        TouchAnimator touchAnimator = this.mSettingsCogAnimator;
        if (touchAnimator != null) {
            touchAnimator.setPosition(f);
        }
        TouchAnimator touchAnimator2 = this.mFooterAnimator;
        if (touchAnimator2 != null) {
            touchAnimator2.setPosition(f);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mContext.getContentResolver().registerContentObserver(Global.getUriFor("development_settings_enabled"), false, this.mDeveloperSettingsObserver, -1);
    }

    public void onDetachedFromWindow() {
        setListening(false);
        this.mContext.getContentResolver().unregisterContentObserver(this.mDeveloperSettingsObserver);
        super.onDetachedFromWindow();
    }

    public void setListening(boolean z) {
        if (z != this.mListening) {
            this.mListening = z;
            updateListeners();
        }
    }

    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (i == 262144) {
            OnClickListener onClickListener = this.mExpandClickListener;
            if (onClickListener != null) {
                onClickListener.onClick(null);
                return true;
            }
        }
        return super.performAccessibilityAction(i, bundle);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(AccessibilityAction.ACTION_EXPAND);
    }

    public void disable(int i, int i2, boolean z) {
        boolean z2 = true;
        if ((i2 & 1) == 0) {
            z2 = false;
        }
        if (z2 != this.mQsDisabled) {
            this.mQsDisabled = z2;
            updateEverything();
        }
    }

    public void updateEverything() {
        post(new Runnable() {
            public final void run() {
                QSFooterImpl.this.lambda$updateEverything$3$QSFooterImpl();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateEverything$3 */
    public /* synthetic */ void lambda$updateEverything$3$QSFooterImpl() {
        updateVisibilities();
        updateClickabilities();
        setClickable(false);
    }

    private void updateClickabilities() {
        MultiUserSwitch multiUserSwitch = this.mMultiUserSwitch;
        boolean z = true;
        multiUserSwitch.setClickable(multiUserSwitch.getVisibility() == 0);
        View view = this.mEdit;
        view.setClickable(view.getVisibility() == 0);
        SettingsButton settingsButton = this.mSettingsButton;
        if (settingsButton.getVisibility() != 0) {
            z = false;
        }
        settingsButton.setClickable(z);
    }

    private void updateVisibilities() {
        int i = 0;
        this.mSettingsContainer.setVisibility(this.mQsDisabled ? 8 : 0);
        this.mSettingsContainer.findViewById(C2011R$id.tuner_icon).setVisibility(TunerService.isTunerEnabled(this.mContext) ? 0 : 4);
        boolean isDeviceInDemoMode = UserManager.isDeviceInDemoMode(this.mContext);
        this.mMultiUserSwitch.setVisibility(showUserSwitcher() ? 0 : 4);
        this.mEditContainer.setVisibility((isDeviceInDemoMode || !this.mExpanded) ? 4 : 0);
        SettingsButton settingsButton = this.mSettingsButton;
        if (isDeviceInDemoMode || !this.mExpanded) {
            i = 4;
        }
        settingsButton.setVisibility(i);
    }

    private boolean showUserSwitcher() {
        return this.mExpanded && this.mMultiUserSwitch.isMultiUserEnabled();
    }

    private void updateListeners() {
        if (this.mListening) {
            this.mUserInfoController.addCallback(this);
        } else {
            this.mUserInfoController.removeCallback(this);
        }
    }

    public void setQSPanel(QSPanel qSPanel) {
        this.mQsPanel = qSPanel;
        if (qSPanel != null) {
            this.mMultiUserSwitch.setQsPanel(qSPanel);
            this.mQsPanel.setFooterPageIndicator(this.mPageIndicator);
        }
    }

    public void onClick(View view) {
        if (this.mExpanded && view == this.mSettingsButton) {
            if (!this.mDeviceProvisionedController.isCurrentUserSetup()) {
                this.mActivityStarter.postQSRunnableDismissingKeyguard($$Lambda$QSFooterImpl$ORlOcuwnOcEc1bdhJcTagEFJfI4.INSTANCE);
                return;
            }
            MetricsLogger.action(this.mContext, this.mExpanded ? 406 : 490);
            if (this.mSettingsButton.isTunerClick()) {
                this.mActivityStarter.postQSRunnableDismissingKeyguard(new Runnable() {
                    public final void run() {
                        QSFooterImpl.this.lambda$onClick$6$QSFooterImpl();
                    }
                });
            } else {
                lambda$onClick$5();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onClick$6 */
    public /* synthetic */ void lambda$onClick$6$QSFooterImpl() {
        if (TunerService.isTunerEnabled(this.mContext)) {
            TunerService.showResetRequest(this.mContext, new Runnable() {
                public final void run() {
                    QSFooterImpl.this.lambda$onClick$5$QSFooterImpl();
                }
            });
        } else {
            Toast.makeText(getContext(), C2017R$string.tuner_toast, 1).show();
            TunerService.setTunerEnabled(this.mContext, true);
        }
        lambda$onClick$5();
    }

    /* access modifiers changed from: private */
    /* renamed from: startSettingsActivity */
    public void lambda$onClick$5() {
        this.mActivityStarter.startActivity(new Intent("android.settings.SETTINGS"), true);
    }

    public void onUserInfoChanged(String str, Drawable drawable, String str2) {
        if (drawable != null && UserManager.get(this.mContext).isGuestUser(KeyguardUpdateMonitor.getCurrentUser()) && !(drawable instanceof UserIconDrawable)) {
            drawable = drawable.getConstantState().newDrawable(this.mContext.getResources()).mutate();
            drawable.setColorFilter(Utils.getColorAttrDefaultColor(this.mContext, 16842800), Mode.SRC_IN);
        }
        this.mMultiUserAvatar.setImageDrawable(drawable);
    }
}
