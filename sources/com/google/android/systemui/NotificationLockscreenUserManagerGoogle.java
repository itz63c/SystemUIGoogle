package com.google.android.systemui;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Handler;
import android.os.UserManager;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.google.android.systemui.smartspace.SmartSpaceController;
import dagger.Lazy;

public class NotificationLockscreenUserManagerGoogle extends NotificationLockscreenUserManagerImpl {
    private final Lazy<KeyguardBypassController> mKeyguardBypassControllerLazy;
    private final SmartSpaceController mSmartSpaceController;

    public NotificationLockscreenUserManagerGoogle(Context context, BroadcastDispatcher broadcastDispatcher, DevicePolicyManager devicePolicyManager, UserManager userManager, IStatusBarService iStatusBarService, KeyguardManager keyguardManager, StatusBarStateController statusBarStateController, Handler handler, DeviceProvisionedController deviceProvisionedController, KeyguardStateController keyguardStateController, Lazy<KeyguardBypassController> lazy, SmartSpaceController smartSpaceController) {
        super(context, broadcastDispatcher, devicePolicyManager, userManager, iStatusBarService, keyguardManager, statusBarStateController, handler, deviceProvisionedController, keyguardStateController);
        this.mKeyguardBypassControllerLazy = lazy;
        this.mSmartSpaceController = smartSpaceController;
    }

    /* access modifiers changed from: protected */
    public void updateLockscreenNotificationSetting() {
        super.updateLockscreenNotificationSetting();
        updateSmartSpaceVisibilitySettings();
    }

    public void updateSmartSpaceVisibilitySettings() {
        boolean z = false;
        boolean z2 = !userAllowsPrivateNotificationsInPublic(this.mCurrentUserId) && isAnyProfilePublicMode();
        boolean z3 = !allowsManagedPrivateNotificationsInPublic();
        if (!((KeyguardBypassController) this.mKeyguardBypassControllerLazy.get()).getBypassEnabled()) {
            if (z3 && isAnyManagedProfilePublicMode()) {
                z = true;
            }
            z3 = z;
        }
        this.mSmartSpaceController.setHideSensitiveData(z2, z3);
    }

    public void updatePublicMode() {
        super.updatePublicMode();
        updateLockscreenNotificationSetting();
    }
}
