package com.android.systemui.statusbar.phone;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import com.android.systemui.C2007R$bool;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.p007qs.QSPanel;
import com.android.systemui.plugins.p006qs.DetailAdapter;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcher;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.UserSwitcherController.BaseUserAdapter;
import java.util.function.Supplier;

public class MultiUserSwitch extends FrameLayout implements OnClickListener {
    private boolean mKeyguardMode;
    private KeyguardUserSwitcher mKeyguardUserSwitcher;
    protected QSPanel mQsPanel;
    private final int[] mTmpInt2 = new int[2];
    private BaseUserAdapter mUserListener;
    final UserManager mUserManager = UserManager.get(getContext());
    protected UserSwitcherController mUserSwitcherController;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public MultiUserSwitch(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(this);
        refreshContentDescription();
    }

    public void setQsPanel(QSPanel qSPanel) {
        this.mQsPanel = qSPanel;
        setUserSwitcherController((UserSwitcherController) Dependency.get(UserSwitcherController.class));
    }

    public void setUserSwitcherController(UserSwitcherController userSwitcherController) {
        this.mUserSwitcherController = userSwitcherController;
        registerListener();
        refreshContentDescription();
    }

    public void setKeyguardUserSwitcher(KeyguardUserSwitcher keyguardUserSwitcher) {
        this.mKeyguardUserSwitcher = keyguardUserSwitcher;
    }

    public void setKeyguardMode(boolean z) {
        this.mKeyguardMode = z;
        registerListener();
    }

    public boolean isMultiUserEnabled() {
        return ((Boolean) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier() {
            public final Object get() {
                return MultiUserSwitch.this.lambda$isMultiUserEnabled$0$MultiUserSwitch();
            }
        })).booleanValue();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$isMultiUserEnabled$0 */
    public /* synthetic */ Boolean lambda$isMultiUserEnabled$0$MultiUserSwitch() {
        boolean z = false;
        if (!(Global.getInt(this.mContext.getContentResolver(), "user_switcher_enabled", 0) != 0) || !UserManager.supportsMultipleUsers() || UserManager.isDeviceInDemoMode(this.mContext) || this.mUserManager.hasUserRestriction("no_user_switch")) {
            return Boolean.FALSE;
        }
        boolean z2 = !((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)).getGuestUserDisabled(null);
        if (this.mUserSwitcherController.getSwitchableUserCount() > 1 || ((z2 && !this.mUserManager.hasUserRestriction("no_add_user")) || this.mContext.getResources().getBoolean(C2007R$bool.qs_show_user_switcher_for_single_user))) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    private void registerListener() {
        if (this.mUserManager.isUserSwitcherEnabled() && this.mUserListener == null) {
            UserSwitcherController userSwitcherController = this.mUserSwitcherController;
            if (userSwitcherController != null) {
                this.mUserListener = new BaseUserAdapter(userSwitcherController) {
                    public View getView(int i, View view, ViewGroup viewGroup) {
                        return null;
                    }

                    public void notifyDataSetChanged() {
                        MultiUserSwitch.this.refreshContentDescription();
                    }
                };
                refreshContentDescription();
            }
        }
    }

    public void onClick(View view) {
        if (this.mKeyguardMode) {
            KeyguardUserSwitcher keyguardUserSwitcher = this.mKeyguardUserSwitcher;
            if (keyguardUserSwitcher != null) {
                keyguardUserSwitcher.show(true);
            }
        } else if (this.mQsPanel != null && this.mUserSwitcherController != null) {
            View childAt = getChildCount() > 0 ? getChildAt(0) : this;
            childAt.getLocationInWindow(this.mTmpInt2);
            int[] iArr = this.mTmpInt2;
            iArr[0] = iArr[0] + (childAt.getWidth() / 2);
            int[] iArr2 = this.mTmpInt2;
            iArr2[1] = iArr2[1] + (childAt.getHeight() / 2);
            this.mQsPanel.showDetailAdapter(true, getUserDetailAdapter(), this.mTmpInt2);
        }
    }

    public void setClickable(boolean z) {
        super.setClickable(z);
        refreshContentDescription();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0041  */
    /* JADX WARNING: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0029  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void refreshContentDescription() {
        /*
            r5 = this;
            android.os.UserManager r0 = r5.mUserManager
            java.util.Objects.requireNonNull(r0)
            com.android.systemui.statusbar.phone.-$$Lambda$lI0G44FrWq1y6zRNSfoZ1zlECXw r1 = new com.android.systemui.statusbar.phone.-$$Lambda$lI0G44FrWq1y6zRNSfoZ1zlECXw
            r1.<init>(r0)
            java.lang.Object r0 = com.android.systemui.DejankUtils.whitelistIpcs(r1)
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            r1 = 0
            if (r0 == 0) goto L_0x0022
            com.android.systemui.statusbar.policy.UserSwitcherController r0 = r5.mUserSwitcherController
            if (r0 == 0) goto L_0x0022
            android.content.Context r2 = r5.mContext
            java.lang.String r0 = r0.getCurrentUserName(r2)
            goto L_0x0023
        L_0x0022:
            r0 = r1
        L_0x0023:
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x0037
            android.content.Context r1 = r5.mContext
            int r2 = com.android.systemui.C2017R$string.accessibility_quick_settings_user
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r0
            java.lang.String r1 = r1.getString(r2, r3)
        L_0x0037:
            java.lang.CharSequence r0 = r5.getContentDescription()
            boolean r0 = android.text.TextUtils.equals(r0, r1)
            if (r0 != 0) goto L_0x0044
            r5.setContentDescription(r1)
        L_0x0044:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.MultiUserSwitch.refreshContentDescription():void");
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(Button.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(Button.class.getName());
    }

    /* access modifiers changed from: protected */
    public DetailAdapter getUserDetailAdapter() {
        return this.mUserSwitcherController.userDetailAdapter;
    }
}
