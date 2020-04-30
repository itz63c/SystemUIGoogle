package com.android.systemui.statusbar;

import android.app.ITransientNotificationCallback;
import android.content.ComponentName;
import android.content.Context;
import android.hardware.biometrics.IBiometricServiceReceiverInternal;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import android.util.SparseArray;
import com.android.internal.os.SomeArgs;
import com.android.internal.statusbar.IStatusBar.Stub;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.view.AppearanceRegion;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.CallbackController;
import com.android.systemui.tracing.ProtoTracer;
import java.util.ArrayList;

public class CommandQueue extends Stub implements CallbackController<Callbacks>, DisplayListener {
    /* access modifiers changed from: private */
    public ArrayList<Callbacks> mCallbacks = new ArrayList<>();
    private SparseArray<Pair<Integer, Integer>> mDisplayDisabled = new SparseArray<>();
    /* access modifiers changed from: private */
    public Handler mHandler = new C1114H(Looper.getMainLooper());
    private int mLastUpdatedImeDisplayId = -1;
    private final Object mLock = new Object();
    private ProtoTracer mProtoTracer;

    public interface Callbacks {
        void abortTransient(int i, int[] iArr) {
        }

        void addQsTile(ComponentName componentName) {
        }

        void animateCollapsePanels(int i, boolean z) {
        }

        void animateExpandNotificationsPanel() {
        }

        void animateExpandSettingsPanel(String str) {
        }

        void appTransitionCancelled(int i) {
        }

        void appTransitionFinished(int i) {
        }

        void appTransitionPending(int i, boolean z) {
        }

        void appTransitionStarting(int i, long j, long j2, boolean z) {
        }

        void cancelPreloadRecentApps() {
        }

        void clickTile(ComponentName componentName) {
        }

        void disable(int i, int i2, int i3, boolean z) {
        }

        void dismissInattentiveSleepWarning(boolean z) {
        }

        void dismissKeyboardShortcutsMenu() {
        }

        void handleShowGlobalActionsMenu() {
        }

        void handleShowShutdownUi(boolean z, String str) {
        }

        void handleSystemKey(int i) {
        }

        void hideAuthenticationDialog() {
        }

        void hideRecentApps(boolean z, boolean z2) {
        }

        void hideToast(String str, IBinder iBinder) {
        }

        void onBiometricAuthenticated() {
        }

        void onBiometricError(int i, int i2, int i3) {
        }

        void onBiometricHelp(String str) {
        }

        void onCameraLaunchGestureDetected(int i) {
        }

        void onDisplayReady(int i) {
        }

        void onDisplayRemoved(int i) {
        }

        void onRecentsAnimationStateChanged(boolean z) {
        }

        void onRotationProposal(int i, boolean z) {
        }

        void onSystemBarAppearanceChanged(int i, int i2, AppearanceRegion[] appearanceRegionArr, boolean z) {
        }

        void onTracingStateChanged(boolean z) {
        }

        void preloadRecentApps() {
        }

        void remQsTile(ComponentName componentName) {
        }

        void removeIcon(String str) {
        }

        void setIcon(String str, StatusBarIcon statusBarIcon) {
        }

        void setImeWindowStatus(int i, IBinder iBinder, int i2, int i3, boolean z) {
        }

        void setTopAppHidesStatusBar(boolean z) {
        }

        void setWindowState(int i, int i2, int i3) {
        }

        void showAssistDisclosure() {
        }

        void showAuthenticationDialog(Bundle bundle, IBiometricServiceReceiverInternal iBiometricServiceReceiverInternal, int i, boolean z, int i2, String str, long j) {
        }

        void showInattentiveSleepWarning() {
        }

        void showPictureInPictureMenu() {
        }

        void showPinningEnterExitToast(boolean z) {
        }

        void showPinningEscapeToast() {
        }

        void showRecentApps(boolean z) {
        }

        void showScreenPinningRequest(int i) {
        }

        void showToast(String str, IBinder iBinder, CharSequence charSequence, IBinder iBinder2, int i, ITransientNotificationCallback iTransientNotificationCallback) {
        }

        void showTransient(int i, int[] iArr) {
        }

        void showWirelessChargingAnimation(int i) {
        }

        void startAssist(Bundle bundle) {
        }

        void suppressAmbientDisplay(boolean z) {
        }

        void toggleKeyboardShortcutsMenu(int i) {
        }

        void togglePanel() {
        }

        void toggleRecentApps() {
        }

        void toggleSplitScreen() {
        }

        void topAppWindowChanged(int i, boolean z, boolean z2) {
        }
    }

    /* renamed from: com.android.systemui.statusbar.CommandQueue$H */
    private final class C1114H extends Handler {
        private C1114H(Looper looper) {
            super(looper);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:105:0x037f, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:106:0x0381, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).togglePanel();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:108:0x039d, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:109:0x039f, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).handleShowGlobalActionsMenu();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:111:0x03bb, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:112:0x03bd, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).handleSystemKey(r13.arg1);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:114:0x03db, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:115:0x03dd, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).dismissKeyboardShortcutsMenu();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:117:0x03f9, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:118:0x03fb, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).appTransitionFinished(r13.arg1);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:120:0x0419, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:121:0x041b, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).toggleSplitScreen();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:123:0x0437, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:124:0x0439, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).clickTile((android.content.ComponentName) r13.obj);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:126:0x0459, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:127:0x045b, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).remQsTile((android.content.ComponentName) r13.obj);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:129:0x047b, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:130:0x047d, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).addQsTile((android.content.ComponentName) r13.obj);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:132:0x049d, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:133:0x049f, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).showPictureInPictureMenu();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:135:0x04bb, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:136:0x04bd, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).toggleKeyboardShortcutsMenu(r13.arg1);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:138:0x04db, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:139:0x04dd, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).onCameraLaunchGestureDetected(r13.arg1);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:141:0x04fb, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:142:0x04fd, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).startAssist((android.os.Bundle) r13.obj);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:144:0x051d, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:145:0x051f, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).showAssistDisclosure();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:155:0x0578, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:156:0x057a, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).appTransitionCancelled(r13.arg1);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:166:0x05c0, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:167:0x05c2, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).showScreenPinningRequest(r13.arg1);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:189:0x0633, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x00c2, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:190:0x0635, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).setWindowState(r13.arg1, r13.arg2, ((java.lang.Integer) r13.obj).intValue());
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:192:0x065d, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:193:0x065f, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).cancelPreloadRecentApps();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:195:0x067b, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:196:0x067d, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).preloadRecentApps();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:198:0x0699, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:199:0x069b, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).toggleRecentApps();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x00c4, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).dismissInattentiveSleepWarning(((java.lang.Boolean) r13.obj).booleanValue());
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:210:0x06db, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:211:0x06dd, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).onDisplayReady(r13.arg1);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x00e8, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:222:0x0732, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:223:0x0734, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).animateExpandSettingsPanel((java.lang.String) r13.obj);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x00ea, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).showInattentiveSleepWarning();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:233:0x077c, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:234:0x077e, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).animateExpandNotificationsPanel();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:339:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:342:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:343:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:347:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:348:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:349:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:350:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:351:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:352:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:356:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:357:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:358:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:359:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:360:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:361:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:362:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:363:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:364:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:365:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:366:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:367:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:368:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:369:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:371:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:373:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:376:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:377:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:378:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:379:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:380:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:381:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:383:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:53:0x01ac, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x01ae, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).showPinningEscapeToast();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:0x01ca, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:57:0x01cc, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).showPinningEnterExitToast(((java.lang.Boolean) r13.obj).booleanValue());
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:59:0x01f0, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:60:0x01f2, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).showWirelessChargingAnimation(r13.arg1);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x0210, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:63:0x0212, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).hideAuthenticationDialog();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:70:0x025b, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:71:0x025d, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).onBiometricHelp((java.lang.String) r13.obj);
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:73:0x027d, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:74:0x027f, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).onBiometricAuthenticated();
            r1 = r1 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:7:0x0038, code lost:
            if (r1 >= com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).size()) goto L_0x0814;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x003a, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$100(r12.this$0).get(r1)).onTracingStateChanged(((java.lang.Boolean) r13.obj).booleanValue());
            r1 = r1 + 1;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r13) {
            /*
                r12 = this;
                int r0 = r13.what
                r1 = -65536(0xffffffffffff0000, float:NaN)
                r0 = r0 & r1
                r1 = 0
                r2 = 1
                switch(r0) {
                    case 65536: goto L_0x07c0;
                    case 131072: goto L_0x0790;
                    case 196608: goto L_0x0772;
                    case 262144: goto L_0x074a;
                    case 327680: goto L_0x0728;
                    case 393216: goto L_0x06f1;
                    case 458752: goto L_0x06d1;
                    case 524288: goto L_0x06ad;
                    case 589824: goto L_0x068f;
                    case 655360: goto L_0x0671;
                    case 720896: goto L_0x0653;
                    case 786432: goto L_0x0629;
                    case 851968: goto L_0x0603;
                    case 917504: goto L_0x05d6;
                    case 1179648: goto L_0x05b6;
                    case 1245184: goto L_0x058e;
                    case 1310720: goto L_0x056e;
                    case 1376256: goto L_0x0531;
                    case 1441792: goto L_0x0513;
                    case 1507328: goto L_0x04f1;
                    case 1572864: goto L_0x04d1;
                    case 1638400: goto L_0x04b1;
                    case 1703936: goto L_0x0493;
                    case 1769472: goto L_0x0471;
                    case 1835008: goto L_0x044f;
                    case 1900544: goto L_0x042d;
                    case 1966080: goto L_0x040f;
                    case 2031616: goto L_0x03ef;
                    case 2097152: goto L_0x03d1;
                    case 2162688: goto L_0x03b1;
                    case 2228224: goto L_0x0393;
                    case 2293760: goto L_0x0375;
                    case 2359296: goto L_0x034b;
                    case 2424832: goto L_0x0325;
                    case 2490368: goto L_0x02fd;
                    case 2555904: goto L_0x0291;
                    case 2621440: goto L_0x0273;
                    case 2686976: goto L_0x0251;
                    case 2752512: goto L_0x0224;
                    case 2818048: goto L_0x0206;
                    case 2883584: goto L_0x01e6;
                    case 2949120: goto L_0x01c0;
                    case 3014656: goto L_0x01a2;
                    case 3080192: goto L_0x017c;
                    case 3145728: goto L_0x0158;
                    case 3211264: goto L_0x0134;
                    case 3276800: goto L_0x00fc;
                    case 3342336: goto L_0x00de;
                    case 3407872: goto L_0x00b8;
                    case 3473408: goto L_0x007a;
                    case 3538944: goto L_0x0054;
                    case 3604480: goto L_0x002e;
                    case 3670016: goto L_0x000c;
                    default: goto L_0x000a;
                }
            L_0x000a:
                goto L_0x0814
            L_0x000c:
                com.android.systemui.statusbar.CommandQueue r12 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r12 = r12.mCallbacks
                java.util.Iterator r12 = r12.iterator()
            L_0x0016:
                boolean r0 = r12.hasNext()
                if (r0 == 0) goto L_0x0814
                java.lang.Object r0 = r12.next()
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                java.lang.Object r1 = r13.obj
                java.lang.Boolean r1 = (java.lang.Boolean) r1
                boolean r1 = r1.booleanValue()
                r0.suppressAmbientDisplay(r1)
                goto L_0x0016
            L_0x002e:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                java.lang.Object r2 = r13.obj
                java.lang.Boolean r2 = (java.lang.Boolean) r2
                boolean r2 = r2.booleanValue()
                r0.onTracingStateChanged(r2)
                int r1 = r1 + 1
                goto L_0x002e
            L_0x0054:
                java.lang.Object r13 = r13.obj
                com.android.internal.os.SomeArgs r13 = (com.android.internal.os.SomeArgs) r13
                java.lang.Object r0 = r13.arg1
                java.lang.String r0 = (java.lang.String) r0
                java.lang.Object r13 = r13.arg2
                android.os.IBinder r13 = (android.os.IBinder) r13
                com.android.systemui.statusbar.CommandQueue r12 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r12 = r12.mCallbacks
                java.util.Iterator r12 = r12.iterator()
            L_0x006a:
                boolean r1 = r12.hasNext()
                if (r1 == 0) goto L_0x0814
                java.lang.Object r1 = r12.next()
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.hideToast(r0, r13)
                goto L_0x006a
            L_0x007a:
                java.lang.Object r13 = r13.obj
                com.android.internal.os.SomeArgs r13 = (com.android.internal.os.SomeArgs) r13
                java.lang.Object r0 = r13.arg1
                java.lang.String r0 = (java.lang.String) r0
                java.lang.Object r1 = r13.arg2
                r8 = r1
                android.os.IBinder r8 = (android.os.IBinder) r8
                java.lang.Object r1 = r13.arg3
                r9 = r1
                java.lang.CharSequence r9 = (java.lang.CharSequence) r9
                java.lang.Object r1 = r13.arg4
                r10 = r1
                android.os.IBinder r10 = (android.os.IBinder) r10
                java.lang.Object r1 = r13.arg5
                r11 = r1
                android.app.ITransientNotificationCallback r11 = (android.app.ITransientNotificationCallback) r11
                int r13 = r13.argi1
                com.android.systemui.statusbar.CommandQueue r12 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r12 = r12.mCallbacks
                java.util.Iterator r12 = r12.iterator()
            L_0x00a2:
                boolean r1 = r12.hasNext()
                if (r1 == 0) goto L_0x0814
                java.lang.Object r1 = r12.next()
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r2 = r0
                r3 = r8
                r4 = r9
                r5 = r10
                r6 = r13
                r7 = r11
                r1.showToast(r2, r3, r4, r5, r6, r7)
                goto L_0x00a2
            L_0x00b8:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                java.lang.Object r2 = r13.obj
                java.lang.Boolean r2 = (java.lang.Boolean) r2
                boolean r2 = r2.booleanValue()
                r0.dismissInattentiveSleepWarning(r2)
                int r1 = r1 + 1
                goto L_0x00b8
            L_0x00de:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.showInattentiveSleepWarning()
                int r1 = r1 + 1
                goto L_0x00de
            L_0x00fc:
                java.lang.Object r13 = r13.obj
                com.android.internal.os.SomeArgs r13 = (com.android.internal.os.SomeArgs) r13
                r0 = r1
            L_0x0101:
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x012f
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                java.lang.Object r3 = r3.get(r0)
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                int r4 = r13.argi1
                int r5 = r13.argi2
                if (r5 == 0) goto L_0x0121
                r5 = r2
                goto L_0x0122
            L_0x0121:
                r5 = r1
            L_0x0122:
                int r6 = r13.argi3
                if (r6 == 0) goto L_0x0128
                r6 = r2
                goto L_0x0129
            L_0x0128:
                r6 = r1
            L_0x0129:
                r3.topAppWindowChanged(r4, r5, r6)
                int r0 = r0 + 1
                goto L_0x0101
            L_0x012f:
                r13.recycle()
                goto L_0x0814
            L_0x0134:
                int r0 = r13.arg1
                java.lang.Object r13 = r13.obj
                int[] r13 = (int[]) r13
            L_0x013a:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r1 >= r2) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                r2.abortTransient(r0, r13)
                int r1 = r1 + 1
                goto L_0x013a
            L_0x0158:
                int r0 = r13.arg1
                java.lang.Object r13 = r13.obj
                int[] r13 = (int[]) r13
            L_0x015e:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r1 >= r2) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                r2.showTransient(r0, r13)
                int r1 = r1 + 1
                goto L_0x015e
            L_0x017c:
                r0 = r1
            L_0x017d:
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                java.lang.Object r3 = r3.get(r0)
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                int r4 = r13.arg1
                if (r4 <= 0) goto L_0x019b
                r4 = r2
                goto L_0x019c
            L_0x019b:
                r4 = r1
            L_0x019c:
                r3.onRecentsAnimationStateChanged(r4)
                int r0 = r0 + 1
                goto L_0x017d
            L_0x01a2:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.showPinningEscapeToast()
                int r1 = r1 + 1
                goto L_0x01a2
            L_0x01c0:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                java.lang.Object r2 = r13.obj
                java.lang.Boolean r2 = (java.lang.Boolean) r2
                boolean r2 = r2.booleanValue()
                r0.showPinningEnterExitToast(r2)
                int r1 = r1 + 1
                goto L_0x01c0
            L_0x01e6:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                int r2 = r13.arg1
                r0.showWirelessChargingAnimation(r2)
                int r1 = r1 + 1
                goto L_0x01e6
            L_0x0206:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.hideAuthenticationDialog()
                int r1 = r1 + 1
                goto L_0x0206
            L_0x0224:
                java.lang.Object r13 = r13.obj
                com.android.internal.os.SomeArgs r13 = (com.android.internal.os.SomeArgs) r13
            L_0x0228:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x024c
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                int r2 = r13.argi1
                int r3 = r13.argi2
                int r4 = r13.argi3
                r0.onBiometricError(r2, r3, r4)
                int r1 = r1 + 1
                goto L_0x0228
            L_0x024c:
                r13.recycle()
                goto L_0x0814
            L_0x0251:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                java.lang.Object r2 = r13.obj
                java.lang.String r2 = (java.lang.String) r2
                r0.onBiometricHelp(r2)
                int r1 = r1 + 1
                goto L_0x0251
            L_0x0273:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.onBiometricAuthenticated()
                int r1 = r1 + 1
                goto L_0x0273
            L_0x0291:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                android.os.Handler r0 = r0.mHandler
                r2 = 2752512(0x2a0000, float:3.857091E-39)
                r0.removeMessages(r2)
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                android.os.Handler r0 = r0.mHandler
                r2 = 2686976(0x290000, float:3.765255E-39)
                r0.removeMessages(r2)
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                android.os.Handler r0 = r0.mHandler
                r2 = 2621440(0x280000, float:3.67342E-39)
                r0.removeMessages(r2)
                java.lang.Object r13 = r13.obj
                com.android.internal.os.SomeArgs r13 = (com.android.internal.os.SomeArgs) r13
            L_0x02b6:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x02f8
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                r2 = r0
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r0 = r13.arg1
                r3 = r0
                android.os.Bundle r3 = (android.os.Bundle) r3
                java.lang.Object r0 = r13.arg2
                r4 = r0
                android.hardware.biometrics.IBiometricServiceReceiverInternal r4 = (android.hardware.biometrics.IBiometricServiceReceiverInternal) r4
                int r5 = r13.argi1
                java.lang.Object r0 = r13.arg3
                java.lang.Boolean r0 = (java.lang.Boolean) r0
                boolean r6 = r0.booleanValue()
                int r7 = r13.argi2
                java.lang.Object r0 = r13.arg4
                r8 = r0
                java.lang.String r8 = (java.lang.String) r8
                java.lang.Object r0 = r13.arg5
                java.lang.Long r0 = (java.lang.Long) r0
                long r9 = r0.longValue()
                r2.showAuthenticationDialog(r3, r4, r5, r6, r7, r8, r9)
                int r1 = r1 + 1
                goto L_0x02b6
            L_0x02f8:
                r13.recycle()
                goto L_0x0814
            L_0x02fd:
                r0 = r1
            L_0x02fe:
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                java.lang.Object r3 = r3.get(r0)
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                int r4 = r13.arg1
                int r5 = r13.arg2
                if (r5 == 0) goto L_0x031e
                r5 = r2
                goto L_0x031f
            L_0x031e:
                r5 = r1
            L_0x031f:
                r3.onRotationProposal(r4, r5)
                int r0 = r0 + 1
                goto L_0x02fe
            L_0x0325:
                r0 = r1
            L_0x0326:
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                java.lang.Object r3 = r3.get(r0)
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                int r4 = r13.arg1
                if (r4 == 0) goto L_0x0344
                r4 = r2
                goto L_0x0345
            L_0x0344:
                r4 = r1
            L_0x0345:
                r3.setTopAppHidesStatusBar(r4)
                int r0 = r0 + 1
                goto L_0x0326
            L_0x034b:
                r0 = r1
            L_0x034c:
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                java.lang.Object r3 = r3.get(r0)
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                int r4 = r13.arg1
                if (r4 == 0) goto L_0x036a
                r4 = r2
                goto L_0x036b
            L_0x036a:
                r4 = r1
            L_0x036b:
                java.lang.Object r5 = r13.obj
                java.lang.String r5 = (java.lang.String) r5
                r3.handleShowShutdownUi(r4, r5)
                int r0 = r0 + 1
                goto L_0x034c
            L_0x0375:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.togglePanel()
                int r1 = r1 + 1
                goto L_0x0375
            L_0x0393:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.handleShowGlobalActionsMenu()
                int r1 = r1 + 1
                goto L_0x0393
            L_0x03b1:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                int r2 = r13.arg1
                r0.handleSystemKey(r2)
                int r1 = r1 + 1
                goto L_0x03b1
            L_0x03d1:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.dismissKeyboardShortcutsMenu()
                int r1 = r1 + 1
                goto L_0x03d1
            L_0x03ef:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                int r2 = r13.arg1
                r0.appTransitionFinished(r2)
                int r1 = r1 + 1
                goto L_0x03ef
            L_0x040f:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.toggleSplitScreen()
                int r1 = r1 + 1
                goto L_0x040f
            L_0x042d:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                java.lang.Object r2 = r13.obj
                android.content.ComponentName r2 = (android.content.ComponentName) r2
                r0.clickTile(r2)
                int r1 = r1 + 1
                goto L_0x042d
            L_0x044f:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                java.lang.Object r2 = r13.obj
                android.content.ComponentName r2 = (android.content.ComponentName) r2
                r0.remQsTile(r2)
                int r1 = r1 + 1
                goto L_0x044f
            L_0x0471:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                java.lang.Object r2 = r13.obj
                android.content.ComponentName r2 = (android.content.ComponentName) r2
                r0.addQsTile(r2)
                int r1 = r1 + 1
                goto L_0x0471
            L_0x0493:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.showPictureInPictureMenu()
                int r1 = r1 + 1
                goto L_0x0493
            L_0x04b1:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                int r2 = r13.arg1
                r0.toggleKeyboardShortcutsMenu(r2)
                int r1 = r1 + 1
                goto L_0x04b1
            L_0x04d1:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                int r2 = r13.arg1
                r0.onCameraLaunchGestureDetected(r2)
                int r1 = r1 + 1
                goto L_0x04d1
            L_0x04f1:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                java.lang.Object r2 = r13.obj
                android.os.Bundle r2 = (android.os.Bundle) r2
                r0.startAssist(r2)
                int r1 = r1 + 1
                goto L_0x04f1
            L_0x0513:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.showAssistDisclosure()
                int r1 = r1 + 1
                goto L_0x0513
            L_0x0531:
                java.lang.Object r13 = r13.obj
                com.android.internal.os.SomeArgs r13 = (com.android.internal.os.SomeArgs) r13
                r0 = r1
            L_0x0536:
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                java.lang.Object r3 = r3.get(r0)
                r4 = r3
                com.android.systemui.statusbar.CommandQueue$Callbacks r4 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r4
                int r5 = r13.argi1
                java.lang.Object r3 = r13.arg1
                java.lang.Long r3 = (java.lang.Long) r3
                long r6 = r3.longValue()
                java.lang.Object r3 = r13.arg2
                java.lang.Long r3 = (java.lang.Long) r3
                long r8 = r3.longValue()
                int r3 = r13.argi2
                if (r3 == 0) goto L_0x0567
                r10 = r2
                goto L_0x0568
            L_0x0567:
                r10 = r1
            L_0x0568:
                r4.appTransitionStarting(r5, r6, r8, r10)
                int r0 = r0 + 1
                goto L_0x0536
            L_0x056e:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                int r2 = r13.arg1
                r0.appTransitionCancelled(r2)
                int r1 = r1 + 1
                goto L_0x056e
            L_0x058e:
                r0 = r1
            L_0x058f:
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                java.lang.Object r3 = r3.get(r0)
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                int r4 = r13.arg1
                int r5 = r13.arg2
                if (r5 == 0) goto L_0x05af
                r5 = r2
                goto L_0x05b0
            L_0x05af:
                r5 = r1
            L_0x05b0:
                r3.appTransitionPending(r4, r5)
                int r0 = r0 + 1
                goto L_0x058f
            L_0x05b6:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                int r2 = r13.arg1
                r0.showScreenPinningRequest(r2)
                int r1 = r1 + 1
                goto L_0x05b6
            L_0x05d6:
                r0 = r1
            L_0x05d7:
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                java.lang.Object r3 = r3.get(r0)
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                int r4 = r13.arg1
                if (r4 == 0) goto L_0x05f5
                r4 = r2
                goto L_0x05f6
            L_0x05f5:
                r4 = r1
            L_0x05f6:
                int r5 = r13.arg2
                if (r5 == 0) goto L_0x05fc
                r5 = r2
                goto L_0x05fd
            L_0x05fc:
                r5 = r1
            L_0x05fd:
                r3.hideRecentApps(r4, r5)
                int r0 = r0 + 1
                goto L_0x05d7
            L_0x0603:
                r0 = r1
            L_0x0604:
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                java.lang.Object r3 = r3.get(r0)
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                int r4 = r13.arg1
                if (r4 == 0) goto L_0x0622
                r4 = r2
                goto L_0x0623
            L_0x0622:
                r4 = r1
            L_0x0623:
                r3.showRecentApps(r4)
                int r0 = r0 + 1
                goto L_0x0604
            L_0x0629:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                int r2 = r13.arg1
                int r3 = r13.arg2
                java.lang.Object r4 = r13.obj
                java.lang.Integer r4 = (java.lang.Integer) r4
                int r4 = r4.intValue()
                r0.setWindowState(r2, r3, r4)
                int r1 = r1 + 1
                goto L_0x0629
            L_0x0653:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.cancelPreloadRecentApps()
                int r1 = r1 + 1
                goto L_0x0653
            L_0x0671:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.preloadRecentApps()
                int r1 = r1 + 1
                goto L_0x0671
            L_0x068f:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.toggleRecentApps()
                int r1 = r1 + 1
                goto L_0x068f
            L_0x06ad:
                java.lang.Object r13 = r13.obj
                com.android.internal.os.SomeArgs r13 = (com.android.internal.os.SomeArgs) r13
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                int r4 = r13.argi1
                java.lang.Object r12 = r13.arg1
                r5 = r12
                android.os.IBinder r5 = (android.os.IBinder) r5
                int r6 = r13.argi2
                int r7 = r13.argi3
                int r12 = r13.argi4
                if (r12 == 0) goto L_0x06c4
                r8 = r2
                goto L_0x06c5
            L_0x06c4:
                r8 = r1
            L_0x06c5:
                int r12 = r13.argi5
                if (r12 == 0) goto L_0x06cb
                r9 = r2
                goto L_0x06cc
            L_0x06cb:
                r9 = r1
            L_0x06cc:
                r3.handleShowImeButton(r4, r5, r6, r7, r8, r9)
                goto L_0x0814
            L_0x06d1:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                int r2 = r13.arg1
                r0.onDisplayReady(r2)
                int r1 = r1 + 1
                goto L_0x06d1
            L_0x06f1:
                java.lang.Object r13 = r13.obj
                com.android.internal.os.SomeArgs r13 = (com.android.internal.os.SomeArgs) r13
                r0 = r1
            L_0x06f6:
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x0723
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                java.lang.Object r3 = r3.get(r0)
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                int r4 = r13.argi1
                int r5 = r13.argi2
                java.lang.Object r6 = r13.arg1
                com.android.internal.view.AppearanceRegion[] r6 = (com.android.internal.view.AppearanceRegion[]) r6
                int r7 = r13.argi3
                if (r7 != r2) goto L_0x071c
                r7 = r2
                goto L_0x071d
            L_0x071c:
                r7 = r1
            L_0x071d:
                r3.onSystemBarAppearanceChanged(r4, r5, r6, r7)
                int r0 = r0 + 1
                goto L_0x06f6
            L_0x0723:
                r13.recycle()
                goto L_0x0814
            L_0x0728:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                java.lang.Object r2 = r13.obj
                java.lang.String r2 = (java.lang.String) r2
                r0.animateExpandSettingsPanel(r2)
                int r1 = r1 + 1
                goto L_0x0728
            L_0x074a:
                r0 = r1
            L_0x074b:
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                java.lang.Object r3 = r3.get(r0)
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                int r4 = r13.arg1
                int r5 = r13.arg2
                if (r5 == 0) goto L_0x076b
                r5 = r2
                goto L_0x076c
            L_0x076b:
                r5 = r1
            L_0x076c:
                r3.animateCollapsePanels(r4, r5)
                int r0 = r0 + 1
                goto L_0x074b
            L_0x0772:
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                int r13 = r13.size()
                if (r1 >= r13) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r13 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r13 = r13.mCallbacks
                java.lang.Object r13 = r13.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r13 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r13
                r13.animateExpandNotificationsPanel()
                int r1 = r1 + 1
                goto L_0x0772
            L_0x0790:
                java.lang.Object r13 = r13.obj
                com.android.internal.os.SomeArgs r13 = (com.android.internal.os.SomeArgs) r13
                r0 = r1
            L_0x0795:
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r3 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r3 = r3.mCallbacks
                java.lang.Object r3 = r3.get(r0)
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                int r4 = r13.argi1
                int r5 = r13.argi2
                int r6 = r13.argi3
                int r7 = r13.argi4
                if (r7 == 0) goto L_0x07b9
                r7 = r2
                goto L_0x07ba
            L_0x07b9:
                r7 = r1
            L_0x07ba:
                r3.disable(r4, r5, r6, r7)
                int r0 = r0 + 1
                goto L_0x0795
            L_0x07c0:
                int r0 = r13.arg1
                if (r0 == r2) goto L_0x07ea
                r2 = 2
                if (r0 == r2) goto L_0x07c8
                goto L_0x0814
            L_0x07c8:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                java.lang.Object r2 = r13.obj
                java.lang.String r2 = (java.lang.String) r2
                r0.removeIcon(r2)
                int r1 = r1 + 1
                goto L_0x07c8
            L_0x07ea:
                java.lang.Object r13 = r13.obj
                android.util.Pair r13 = (android.util.Pair) r13
            L_0x07ee:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                int r0 = r0.size()
                if (r1 >= r0) goto L_0x0814
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.lang.Object r0 = r0.get(r1)
                com.android.systemui.statusbar.CommandQueue$Callbacks r0 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r0
                java.lang.Object r2 = r13.first
                java.lang.String r2 = (java.lang.String) r2
                java.lang.Object r3 = r13.second
                com.android.internal.statusbar.StatusBarIcon r3 = (com.android.internal.statusbar.StatusBarIcon) r3
                r0.setIcon(r2, r3)
                int r1 = r1 + 1
                goto L_0x07ee
            L_0x0814:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.CommandQueue.C1114H.handleMessage(android.os.Message):void");
        }
    }

    public void onDisplayAdded(int i) {
    }

    public void onDisplayChanged(int i) {
    }

    public CommandQueue(Context context, ProtoTracer protoTracer) {
        this.mProtoTracer = protoTracer;
        ((DisplayManager) context.getSystemService(DisplayManager.class)).registerDisplayListener(this, this.mHandler);
        setDisabled(0, 0, 0);
    }

    public void onDisplayRemoved(int i) {
        synchronized (this.mLock) {
            this.mDisplayDisabled.remove(i);
        }
        for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
            ((Callbacks) this.mCallbacks.get(size)).onDisplayRemoved(i);
        }
    }

    public boolean panelsEnabled() {
        int disabled1 = getDisabled1(0);
        int disabled2 = getDisabled2(0);
        if ((disabled1 & 65536) == 0 && (disabled2 & 4) == 0 && !StatusBar.ONLY_CORE_APPS) {
            return true;
        }
        return false;
    }

    public void addCallback(Callbacks callbacks) {
        this.mCallbacks.add(callbacks);
        for (int i = 0; i < this.mDisplayDisabled.size(); i++) {
            int keyAt = this.mDisplayDisabled.keyAt(i);
            callbacks.disable(keyAt, getDisabled1(keyAt), getDisabled2(keyAt), false);
        }
    }

    public void removeCallback(Callbacks callbacks) {
        this.mCallbacks.remove(callbacks);
    }

    public void setIcon(String str, StatusBarIcon statusBarIcon) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(65536, 1, 0, new Pair(str, statusBarIcon)).sendToTarget();
        }
    }

    public void removeIcon(String str) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(65536, 2, 0, str).sendToTarget();
        }
    }

    public void disable(int i, int i2, int i3, boolean z) {
        synchronized (this.mLock) {
            setDisabled(i, i2, i3);
            this.mHandler.removeMessages(131072);
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = i;
            obtain.argi2 = i2;
            obtain.argi3 = i3;
            obtain.argi4 = z ? 1 : 0;
            Message obtainMessage = this.mHandler.obtainMessage(131072, obtain);
            if (Looper.myLooper() == this.mHandler.getLooper()) {
                this.mHandler.handleMessage(obtainMessage);
                obtainMessage.recycle();
            } else {
                obtainMessage.sendToTarget();
            }
        }
    }

    public void disable(int i, int i2, int i3) {
        disable(i, i2, i3, true);
    }

    public void recomputeDisableFlags(int i, boolean z) {
        disable(i, getDisabled1(i), getDisabled2(i), z);
    }

    private void setDisabled(int i, int i2, int i3) {
        this.mDisplayDisabled.put(i, new Pair(Integer.valueOf(i2), Integer.valueOf(i3)));
    }

    private int getDisabled1(int i) {
        return ((Integer) getDisabled(i).first).intValue();
    }

    private int getDisabled2(int i) {
        return ((Integer) getDisabled(i).second).intValue();
    }

    private Pair<Integer, Integer> getDisabled(int i) {
        Pair<Integer, Integer> pair = (Pair) this.mDisplayDisabled.get(i);
        if (pair != null) {
            return pair;
        }
        Pair<Integer, Integer> pair2 = new Pair<>(Integer.valueOf(0), Integer.valueOf(0));
        this.mDisplayDisabled.put(i, pair2);
        return pair2;
    }

    public void animateExpandNotificationsPanel() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(196608);
            this.mHandler.sendEmptyMessage(196608);
        }
    }

    public void animateCollapsePanels() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(262144);
            this.mHandler.obtainMessage(262144, 0, 0).sendToTarget();
        }
    }

    public void animateCollapsePanels(int i, boolean z) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(262144);
            this.mHandler.obtainMessage(262144, i, z ? 1 : 0).sendToTarget();
        }
    }

    public void togglePanel() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2293760);
            this.mHandler.obtainMessage(2293760, 0, 0).sendToTarget();
        }
    }

    public void animateExpandSettingsPanel(String str) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(327680);
            this.mHandler.obtainMessage(327680, str).sendToTarget();
        }
    }

    public void topAppWindowChanged(int i, boolean z, boolean z2) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = i;
            int i2 = 1;
            obtain.argi2 = z ? 1 : 0;
            if (!z2) {
                i2 = 0;
            }
            obtain.argi3 = i2;
            this.mHandler.obtainMessage(3276800, obtain).sendToTarget();
        }
    }

    public void setImeWindowStatus(int i, IBinder iBinder, int i2, int i3, boolean z, boolean z2) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(524288);
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = i;
            obtain.argi2 = i2;
            obtain.argi3 = i3;
            int i4 = 1;
            obtain.argi4 = z ? 1 : 0;
            if (!z2) {
                i4 = 0;
            }
            obtain.argi5 = i4;
            obtain.arg1 = iBinder;
            this.mHandler.obtainMessage(524288, obtain).sendToTarget();
        }
    }

    public void showRecentApps(boolean z) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(851968);
            this.mHandler.obtainMessage(851968, z ? 1 : 0, 0, null).sendToTarget();
        }
    }

    public void hideRecentApps(boolean z, boolean z2) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(917504);
            this.mHandler.obtainMessage(917504, z ? 1 : 0, z2 ? 1 : 0, null).sendToTarget();
        }
    }

    public void toggleSplitScreen() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1966080);
            this.mHandler.obtainMessage(1966080, 0, 0, null).sendToTarget();
        }
    }

    public void toggleRecentApps() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(589824);
            Message obtainMessage = this.mHandler.obtainMessage(589824, 0, 0, null);
            obtainMessage.setAsynchronous(true);
            obtainMessage.sendToTarget();
        }
    }

    public void preloadRecentApps() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(655360);
            this.mHandler.obtainMessage(655360, 0, 0, null).sendToTarget();
        }
    }

    public void cancelPreloadRecentApps() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(720896);
            this.mHandler.obtainMessage(720896, 0, 0, null).sendToTarget();
        }
    }

    public void dismissKeyboardShortcutsMenu() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2097152);
            this.mHandler.obtainMessage(2097152).sendToTarget();
        }
    }

    public void toggleKeyboardShortcutsMenu(int i) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1638400);
            this.mHandler.obtainMessage(1638400, i, 0).sendToTarget();
        }
    }

    public void showPictureInPictureMenu() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1703936);
            this.mHandler.obtainMessage(1703936).sendToTarget();
        }
    }

    public void setWindowState(int i, int i2, int i3) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(786432, i, i2, Integer.valueOf(i3)).sendToTarget();
        }
    }

    public void showScreenPinningRequest(int i) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1179648, i, 0, null).sendToTarget();
        }
    }

    public void appTransitionPending(int i) {
        appTransitionPending(i, false);
    }

    public void appTransitionPending(int i, boolean z) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1245184, i, z ? 1 : 0).sendToTarget();
        }
    }

    public void appTransitionCancelled(int i) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1310720, i, 0).sendToTarget();
        }
    }

    public void appTransitionStarting(int i, long j, long j2) {
        appTransitionStarting(i, j, j2, false);
    }

    public void appTransitionStarting(int i, long j, long j2, boolean z) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = i;
            obtain.argi2 = z ? 1 : 0;
            obtain.arg1 = Long.valueOf(j);
            obtain.arg2 = Long.valueOf(j2);
            this.mHandler.obtainMessage(1376256, obtain).sendToTarget();
        }
    }

    public void appTransitionFinished(int i) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2031616, i, 0).sendToTarget();
        }
    }

    public void showAssistDisclosure() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1441792);
            this.mHandler.obtainMessage(1441792).sendToTarget();
        }
    }

    public void startAssist(Bundle bundle) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1507328);
            this.mHandler.obtainMessage(1507328, bundle).sendToTarget();
        }
    }

    public void onCameraLaunchGestureDetected(int i) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1572864);
            this.mHandler.obtainMessage(1572864, i, 0).sendToTarget();
        }
    }

    public void addQsTile(ComponentName componentName) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1769472, componentName).sendToTarget();
        }
    }

    public void remQsTile(ComponentName componentName) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1835008, componentName).sendToTarget();
        }
    }

    public void clickQsTile(ComponentName componentName) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1900544, componentName).sendToTarget();
        }
    }

    public void handleSystemKey(int i) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2162688, i, 0).sendToTarget();
        }
    }

    public void showPinningEnterExitToast(boolean z) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2949120, Boolean.valueOf(z)).sendToTarget();
        }
    }

    public void showPinningEscapeToast() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3014656).sendToTarget();
        }
    }

    public void showGlobalActionsMenu() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2228224);
            this.mHandler.obtainMessage(2228224).sendToTarget();
        }
    }

    public void setTopAppHidesStatusBar(boolean z) {
        this.mHandler.removeMessages(2424832);
        this.mHandler.obtainMessage(2424832, z ? 1 : 0, 0).sendToTarget();
    }

    public void showShutdownUi(boolean z, String str) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2359296);
            this.mHandler.obtainMessage(2359296, z ? 1 : 0, 0, str).sendToTarget();
        }
    }

    public void showWirelessChargingAnimation(int i) {
        this.mHandler.removeMessages(2883584);
        this.mHandler.obtainMessage(2883584, i, 0).sendToTarget();
    }

    public void onProposedRotationChanged(int i, boolean z) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2490368);
            this.mHandler.obtainMessage(2490368, i, z ? 1 : 0, null).sendToTarget();
        }
    }

    public void showAuthenticationDialog(Bundle bundle, IBiometricServiceReceiverInternal iBiometricServiceReceiverInternal, int i, boolean z, int i2, String str, long j) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = bundle;
            obtain.arg2 = iBiometricServiceReceiverInternal;
            obtain.argi1 = i;
            obtain.arg3 = Boolean.valueOf(z);
            obtain.argi2 = i2;
            obtain.arg4 = str;
            obtain.arg5 = Long.valueOf(j);
            this.mHandler.obtainMessage(2555904, obtain).sendToTarget();
        }
    }

    public void showToast(String str, IBinder iBinder, CharSequence charSequence, IBinder iBinder2, int i, ITransientNotificationCallback iTransientNotificationCallback) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = str;
            obtain.arg2 = iBinder;
            obtain.arg3 = charSequence;
            obtain.arg4 = iBinder2;
            obtain.arg5 = iTransientNotificationCallback;
            obtain.argi1 = i;
            this.mHandler.obtainMessage(3473408, obtain).sendToTarget();
        }
    }

    public void hideToast(String str, IBinder iBinder) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = str;
            obtain.arg2 = iBinder;
            this.mHandler.obtainMessage(3538944, obtain).sendToTarget();
        }
    }

    public void onBiometricAuthenticated() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2621440).sendToTarget();
        }
    }

    public void onBiometricHelp(String str) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2686976, str).sendToTarget();
        }
    }

    public void onBiometricError(int i, int i2, int i3) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = i;
            obtain.argi2 = i2;
            obtain.argi3 = i3;
            this.mHandler.obtainMessage(2752512, obtain).sendToTarget();
        }
    }

    public void hideAuthenticationDialog() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2818048).sendToTarget();
        }
    }

    public void onDisplayReady(int i) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(458752, i, 0).sendToTarget();
        }
    }

    public void onRecentsAnimationStateChanged(boolean z) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3080192, z ? 1 : 0, 0).sendToTarget();
        }
    }

    public void showInattentiveSleepWarning() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3342336).sendToTarget();
        }
    }

    public void dismissInattentiveSleepWarning(boolean z) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3407872, Boolean.valueOf(z)).sendToTarget();
        }
    }

    /* access modifiers changed from: private */
    public void handleShowImeButton(int i, IBinder iBinder, int i2, int i3, boolean z, boolean z2) {
        if (i != -1) {
            if (!z2) {
                int i4 = this.mLastUpdatedImeDisplayId;
                if (!(i4 == i || i4 == -1)) {
                    sendImeInvisibleStatusForPrevNavBar();
                }
            }
            for (int i5 = 0; i5 < this.mCallbacks.size(); i5++) {
                ((Callbacks) this.mCallbacks.get(i5)).setImeWindowStatus(i, iBinder, i2, i3, z);
            }
            this.mLastUpdatedImeDisplayId = i;
        }
    }

    private void sendImeInvisibleStatusForPrevNavBar() {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            ((Callbacks) this.mCallbacks.get(i)).setImeWindowStatus(this.mLastUpdatedImeDisplayId, null, 4, 0, false);
        }
    }

    public void onSystemBarAppearanceChanged(int i, int i2, AppearanceRegion[] appearanceRegionArr, boolean z) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = i;
            obtain.argi2 = i2;
            obtain.argi3 = z ? 1 : 0;
            obtain.arg1 = appearanceRegionArr;
            this.mHandler.obtainMessage(393216, obtain).sendToTarget();
        }
    }

    public void showTransient(int i, int[] iArr) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3145728, i, 0, iArr).sendToTarget();
        }
    }

    public void abortTransient(int i, int[] iArr) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3211264, i, 0, iArr).sendToTarget();
        }
    }

    public void startTracing() {
        synchronized (this.mLock) {
            if (this.mProtoTracer != null) {
                this.mProtoTracer.start();
            }
            this.mHandler.obtainMessage(3604480, Boolean.TRUE).sendToTarget();
        }
    }

    public void stopTracing() {
        synchronized (this.mLock) {
            if (this.mProtoTracer != null) {
                this.mProtoTracer.stop();
            }
            this.mHandler.obtainMessage(3604480, Boolean.FALSE).sendToTarget();
        }
    }

    public void suppressAmbientDisplay(boolean z) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3670016, Boolean.valueOf(z)).sendToTarget();
        }
    }
}
