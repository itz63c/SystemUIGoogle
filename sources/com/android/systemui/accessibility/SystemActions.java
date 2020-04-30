package com.android.systemui.accessibility;

import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.util.ScreenshotHelper;
import com.android.systemui.Dependency;
import com.android.systemui.SystemUI;
import com.android.systemui.recents.Recents;
import com.android.systemui.statusbar.phone.StatusBar;

public class SystemActions extends SystemUI {
    private SystemActionsBroadcastReceiver mReceiver = new SystemActionsBroadcastReceiver();
    private Recents mRecents = ((Recents) Dependency.get(Recents.class));
    private StatusBar mStatusBar = ((StatusBar) Dependency.get(StatusBar.class));

    private class SystemActionsBroadcastReceiver extends BroadcastReceiver {
        private SystemActionsBroadcastReceiver() {
        }

        /* access modifiers changed from: private */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.app.PendingIntent createPendingIntent(android.content.Context r2, java.lang.String r3) {
            /*
                r1 = this;
                int r1 = r3.hashCode()
                r0 = 0
                switch(r1) {
                    case -1173809047: goto L_0x0065;
                    case -1103811776: goto L_0x005b;
                    case -1103619272: goto L_0x0051;
                    case -720484549: goto L_0x0047;
                    case -535129457: goto L_0x003d;
                    case -153384569: goto L_0x0033;
                    case 42571871: goto L_0x0029;
                    case 1579999269: goto L_0x001e;
                    case 1668921710: goto L_0x0014;
                    case 1962121443: goto L_0x000a;
                    default: goto L_0x0008;
                }
            L_0x0008:
                goto L_0x0070
            L_0x000a:
                java.lang.String r1 = "SYSTEM_ACTION_TOGGLE_SPLIT_SCREEN"
                boolean r1 = r3.equals(r1)
                if (r1 == 0) goto L_0x0070
                r1 = 6
                goto L_0x0071
            L_0x0014:
                java.lang.String r1 = "SYSTEM_ACTION_QUICK_SETTINGS"
                boolean r1 = r3.equals(r1)
                if (r1 == 0) goto L_0x0070
                r1 = 4
                goto L_0x0071
            L_0x001e:
                java.lang.String r1 = "SYSTEM_ACTION_TAKE_SCREENSHOT"
                boolean r1 = r3.equals(r1)
                if (r1 == 0) goto L_0x0070
                r1 = 8
                goto L_0x0071
            L_0x0029:
                java.lang.String r1 = "SYSTEM_ACTION_RECENTS"
                boolean r1 = r3.equals(r1)
                if (r1 == 0) goto L_0x0070
                r1 = 2
                goto L_0x0071
            L_0x0033:
                java.lang.String r1 = "SYSTEM_ACTION_LOCK_SCREEN"
                boolean r1 = r3.equals(r1)
                if (r1 == 0) goto L_0x0070
                r1 = 7
                goto L_0x0071
            L_0x003d:
                java.lang.String r1 = "SYSTEM_ACTION_NOTIFICATIONS"
                boolean r1 = r3.equals(r1)
                if (r1 == 0) goto L_0x0070
                r1 = 3
                goto L_0x0071
            L_0x0047:
                java.lang.String r1 = "SYSTEM_ACTION_POWER_DIALOG"
                boolean r1 = r3.equals(r1)
                if (r1 == 0) goto L_0x0070
                r1 = 5
                goto L_0x0071
            L_0x0051:
                java.lang.String r1 = "SYSTEM_ACTION_HOME"
                boolean r1 = r3.equals(r1)
                if (r1 == 0) goto L_0x0070
                r1 = 1
                goto L_0x0071
            L_0x005b:
                java.lang.String r1 = "SYSTEM_ACTION_BACK"
                boolean r1 = r3.equals(r1)
                if (r1 == 0) goto L_0x0070
                r1 = r0
                goto L_0x0071
            L_0x0065:
                java.lang.String r1 = "SYSTEM_ACTION_ACCESSIBILITY_MENU"
                boolean r1 = r3.equals(r1)
                if (r1 == 0) goto L_0x0070
                r1 = 9
                goto L_0x0071
            L_0x0070:
                r1 = -1
            L_0x0071:
                switch(r1) {
                    case 0: goto L_0x0076;
                    case 1: goto L_0x0076;
                    case 2: goto L_0x0076;
                    case 3: goto L_0x0076;
                    case 4: goto L_0x0076;
                    case 5: goto L_0x0076;
                    case 6: goto L_0x0076;
                    case 7: goto L_0x0076;
                    case 8: goto L_0x0076;
                    case 9: goto L_0x0076;
                    default: goto L_0x0074;
                }
            L_0x0074:
                r1 = 0
                return r1
            L_0x0076:
                android.content.Intent r1 = new android.content.Intent
                r1.<init>(r3)
                android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r2, r0, r1, r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.accessibility.SystemActions.SystemActionsBroadcastReceiver.createPendingIntent(android.content.Context, java.lang.String):android.app.PendingIntent");
        }

        /* access modifiers changed from: private */
        public IntentFilter createIntentFilter() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("SYSTEM_ACTION_BACK");
            intentFilter.addAction("SYSTEM_ACTION_HOME");
            intentFilter.addAction("SYSTEM_ACTION_RECENTS");
            intentFilter.addAction("SYSTEM_ACTION_NOTIFICATIONS");
            intentFilter.addAction("SYSTEM_ACTION_QUICK_SETTINGS");
            intentFilter.addAction("SYSTEM_ACTION_POWER_DIALOG");
            intentFilter.addAction("SYSTEM_ACTION_TOGGLE_SPLIT_SCREEN");
            intentFilter.addAction("SYSTEM_ACTION_LOCK_SCREEN");
            intentFilter.addAction("SYSTEM_ACTION_TAKE_SCREENSHOT");
            intentFilter.addAction("SYSTEM_ACTION_ACCESSIBILITY_MENU");
            return intentFilter;
        }

        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r1, android.content.Intent r2) {
            /*
                r0 = this;
                java.lang.String r1 = r2.getAction()
                int r2 = r1.hashCode()
                switch(r2) {
                    case -1173809047: goto L_0x0068;
                    case -1103811776: goto L_0x005e;
                    case -1103619272: goto L_0x0054;
                    case -720484549: goto L_0x004a;
                    case -535129457: goto L_0x0040;
                    case -153384569: goto L_0x0036;
                    case 42571871: goto L_0x002c;
                    case 1579999269: goto L_0x0021;
                    case 1668921710: goto L_0x0017;
                    case 1962121443: goto L_0x000d;
                    default: goto L_0x000b;
                }
            L_0x000b:
                goto L_0x0073
            L_0x000d:
                java.lang.String r2 = "SYSTEM_ACTION_TOGGLE_SPLIT_SCREEN"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0073
                r1 = 6
                goto L_0x0074
            L_0x0017:
                java.lang.String r2 = "SYSTEM_ACTION_QUICK_SETTINGS"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0073
                r1 = 4
                goto L_0x0074
            L_0x0021:
                java.lang.String r2 = "SYSTEM_ACTION_TAKE_SCREENSHOT"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0073
                r1 = 8
                goto L_0x0074
            L_0x002c:
                java.lang.String r2 = "SYSTEM_ACTION_RECENTS"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0073
                r1 = 2
                goto L_0x0074
            L_0x0036:
                java.lang.String r2 = "SYSTEM_ACTION_LOCK_SCREEN"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0073
                r1 = 7
                goto L_0x0074
            L_0x0040:
                java.lang.String r2 = "SYSTEM_ACTION_NOTIFICATIONS"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0073
                r1 = 3
                goto L_0x0074
            L_0x004a:
                java.lang.String r2 = "SYSTEM_ACTION_POWER_DIALOG"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0073
                r1 = 5
                goto L_0x0074
            L_0x0054:
                java.lang.String r2 = "SYSTEM_ACTION_HOME"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0073
                r1 = 1
                goto L_0x0074
            L_0x005e:
                java.lang.String r2 = "SYSTEM_ACTION_BACK"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0073
                r1 = 0
                goto L_0x0074
            L_0x0068:
                java.lang.String r2 = "SYSTEM_ACTION_ACCESSIBILITY_MENU"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0073
                r1 = 9
                goto L_0x0074
            L_0x0073:
                r1 = -1
            L_0x0074:
                switch(r1) {
                    case 0: goto L_0x00ae;
                    case 1: goto L_0x00a8;
                    case 2: goto L_0x00a2;
                    case 3: goto L_0x009c;
                    case 4: goto L_0x0096;
                    case 5: goto L_0x0090;
                    case 6: goto L_0x008a;
                    case 7: goto L_0x0084;
                    case 8: goto L_0x007e;
                    case 9: goto L_0x0078;
                    default: goto L_0x0077;
                }
            L_0x0077:
                goto L_0x00b3
            L_0x0078:
                com.android.systemui.accessibility.SystemActions r0 = com.android.systemui.accessibility.SystemActions.this
                r0.handleAccessibilityMenu()
                goto L_0x00b3
            L_0x007e:
                com.android.systemui.accessibility.SystemActions r0 = com.android.systemui.accessibility.SystemActions.this
                r0.handleTakeScreenshot()
                goto L_0x00b3
            L_0x0084:
                com.android.systemui.accessibility.SystemActions r0 = com.android.systemui.accessibility.SystemActions.this
                r0.handleLockScreen()
                goto L_0x00b3
            L_0x008a:
                com.android.systemui.accessibility.SystemActions r0 = com.android.systemui.accessibility.SystemActions.this
                r0.handleToggleSplitScreen()
                goto L_0x00b3
            L_0x0090:
                com.android.systemui.accessibility.SystemActions r0 = com.android.systemui.accessibility.SystemActions.this
                r0.handlePowerDialog()
                goto L_0x00b3
            L_0x0096:
                com.android.systemui.accessibility.SystemActions r0 = com.android.systemui.accessibility.SystemActions.this
                r0.handleQuickSettings()
                goto L_0x00b3
            L_0x009c:
                com.android.systemui.accessibility.SystemActions r0 = com.android.systemui.accessibility.SystemActions.this
                r0.handleNotifications()
                goto L_0x00b3
            L_0x00a2:
                com.android.systemui.accessibility.SystemActions r0 = com.android.systemui.accessibility.SystemActions.this
                r0.handleRecents()
                goto L_0x00b3
            L_0x00a8:
                com.android.systemui.accessibility.SystemActions r0 = com.android.systemui.accessibility.SystemActions.this
                r0.handleHome()
                goto L_0x00b3
            L_0x00ae:
                com.android.systemui.accessibility.SystemActions r0 = com.android.systemui.accessibility.SystemActions.this
                r0.handleBack()
            L_0x00b3:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.accessibility.SystemActions.SystemActionsBroadcastReceiver.onReceive(android.content.Context, android.content.Intent):void");
        }
    }

    public SystemActions(Context context) {
        super(context);
    }

    public void start() {
        Context context = this.mContext;
        SystemActionsBroadcastReceiver systemActionsBroadcastReceiver = this.mReceiver;
        context.registerReceiverForAllUsers(systemActionsBroadcastReceiver, systemActionsBroadcastReceiver.createIntentFilter(), null, null);
        RemoteAction remoteAction = new RemoteAction(Icon.createWithResource(this.mContext, 17301684), this.mContext.getString(17039466), this.mContext.getString(17039466), this.mReceiver.createPendingIntent(this.mContext, "SYSTEM_ACTION_BACK"));
        RemoteAction remoteAction2 = new RemoteAction(Icon.createWithResource(this.mContext, 17301684), this.mContext.getString(17039467), this.mContext.getString(17039467), this.mReceiver.createPendingIntent(this.mContext, "SYSTEM_ACTION_HOME"));
        RemoteAction remoteAction3 = new RemoteAction(Icon.createWithResource(this.mContext, 17301684), this.mContext.getString(17039472), this.mContext.getString(17039472), this.mReceiver.createPendingIntent(this.mContext, "SYSTEM_ACTION_RECENTS"));
        RemoteAction remoteAction4 = new RemoteAction(Icon.createWithResource(this.mContext, 17301684), this.mContext.getString(17039469), this.mContext.getString(17039469), this.mReceiver.createPendingIntent(this.mContext, "SYSTEM_ACTION_NOTIFICATIONS"));
        RemoteAction remoteAction5 = new RemoteAction(Icon.createWithResource(this.mContext, 17301684), this.mContext.getString(17039471), this.mContext.getString(17039471), this.mReceiver.createPendingIntent(this.mContext, "SYSTEM_ACTION_QUICK_SETTINGS"));
        RemoteAction remoteAction6 = new RemoteAction(Icon.createWithResource(this.mContext, 17301684), this.mContext.getString(17039470), this.mContext.getString(17039470), this.mReceiver.createPendingIntent(this.mContext, "SYSTEM_ACTION_POWER_DIALOG"));
        RemoteAction remoteAction7 = new RemoteAction(Icon.createWithResource(this.mContext, 17301684), this.mContext.getString(17039474), this.mContext.getString(17039474), this.mReceiver.createPendingIntent(this.mContext, "SYSTEM_ACTION_TOGGLE_SPLIT_SCREEN"));
        RemoteAction remoteAction8 = new RemoteAction(Icon.createWithResource(this.mContext, 17301684), this.mContext.getString(17039468), this.mContext.getString(17039468), this.mReceiver.createPendingIntent(this.mContext, "SYSTEM_ACTION_LOCK_SCREEN"));
        RemoteAction remoteAction9 = new RemoteAction(Icon.createWithResource(this.mContext, 17301684), this.mContext.getString(17039473), this.mContext.getString(17039473), this.mReceiver.createPendingIntent(this.mContext, "SYSTEM_ACTION_TAKE_SCREENSHOT"));
        RemoteAction remoteAction10 = new RemoteAction(Icon.createWithResource(this.mContext, 17301684), this.mContext.getString(17039465), this.mContext.getString(17039465), this.mReceiver.createPendingIntent(this.mContext, "SYSTEM_ACTION_ACCESSIBILITY_MENU"));
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        accessibilityManager.registerSystemAction(remoteAction, 1);
        accessibilityManager.registerSystemAction(remoteAction2, 2);
        accessibilityManager.registerSystemAction(remoteAction3, 3);
        accessibilityManager.registerSystemAction(remoteAction4, 4);
        accessibilityManager.registerSystemAction(remoteAction5, 5);
        accessibilityManager.registerSystemAction(remoteAction6, 6);
        accessibilityManager.registerSystemAction(remoteAction7, 7);
        accessibilityManager.registerSystemAction(remoteAction8, 8);
        accessibilityManager.registerSystemAction(remoteAction9, 9);
        accessibilityManager.registerSystemAction(remoteAction10, 10);
    }

    /* access modifiers changed from: private */
    public void handleBack() {
        sendDownAndUpKeyEvents(4);
    }

    /* access modifiers changed from: private */
    public void handleHome() {
        sendDownAndUpKeyEvents(3);
    }

    private void sendDownAndUpKeyEvents(int i) {
        long uptimeMillis = SystemClock.uptimeMillis();
        int i2 = i;
        long j = uptimeMillis;
        sendKeyEventIdentityCleared(i2, 0, j, uptimeMillis);
        sendKeyEventIdentityCleared(i2, 1, j, SystemClock.uptimeMillis());
    }

    private void sendKeyEventIdentityCleared(int i, int i2, long j, long j2) {
        KeyEvent obtain = KeyEvent.obtain(j, j2, i2, i, 0, 0, -1, 0, 8, 257, null);
        InputManager.getInstance().injectInputEvent(obtain, 0);
        obtain.recycle();
    }

    /* access modifiers changed from: private */
    public void handleRecents() {
        this.mRecents.toggleRecentApps();
    }

    /* access modifiers changed from: private */
    public void handleNotifications() {
        this.mStatusBar.animateExpandNotificationsPanel();
    }

    /* access modifiers changed from: private */
    public void handleQuickSettings() {
        this.mStatusBar.animateExpandSettingsPanel(null);
    }

    /* access modifiers changed from: private */
    public void handlePowerDialog() {
        try {
            WindowManagerGlobal.getWindowManagerService().showGlobalActions();
        } catch (RemoteException unused) {
            Log.e("SystemActions", "failed to display power dialog.");
        }
    }

    /* access modifiers changed from: private */
    public void handleToggleSplitScreen() {
        this.mStatusBar.toggleSplitScreen();
    }

    /* access modifiers changed from: private */
    public void handleLockScreen() {
        IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
        ((PowerManager) this.mContext.getSystemService(PowerManager.class)).goToSleep(SystemClock.uptimeMillis(), 7, 0);
        try {
            windowManagerService.lockNow(null);
        } catch (RemoteException unused) {
            Log.e("SystemActions", "failed to lock screen.");
        }
    }

    /* access modifiers changed from: private */
    public void handleTakeScreenshot() {
        new ScreenshotHelper(this.mContext).takeScreenshot(1, true, true, new Handler(Looper.getMainLooper()), null);
    }

    /* access modifiers changed from: private */
    public void handleAccessibilityMenu() {
        AccessibilityManager.getInstance(this.mContext).notifyAccessibilityButtonClicked(0);
    }
}
