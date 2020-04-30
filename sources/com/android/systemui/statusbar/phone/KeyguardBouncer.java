package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.UserManager;
import android.util.Log;
import android.util.MathUtils;
import android.util.Slog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowInsets;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardHostView;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.keyguard.KeyguardSecurityModel.SecurityMode;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.plugins.ActivityStarter.OnDismissAction;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.PrintWriter;

public class KeyguardBouncer {
    /* access modifiers changed from: private */
    public int mBouncerPromptReason;
    protected final ViewMediatorCallback mCallback;
    protected final ViewGroup mContainer;
    protected final Context mContext;
    private final DismissCallbackRegistry mDismissCallbackRegistry;
    /* access modifiers changed from: private */
    public float mExpansion = 1.0f;
    private final BouncerExpansionCallback mExpansionCallback;
    private final FalsingManager mFalsingManager;
    private final Handler mHandler;
    private boolean mIsAnimatingAway;
    private boolean mIsScrimmed;
    private final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    protected KeyguardHostView mKeyguardView;
    protected final LockPatternUtils mLockPatternUtils;
    private final Runnable mRemoveViewRunnable = new Runnable() {
        public final void run() {
            KeyguardBouncer.this.removeView();
        }
    };
    private final Runnable mResetRunnable = new Runnable() {
        public final void run() {
            KeyguardBouncer.this.lambda$new$0$KeyguardBouncer();
        }
    };
    protected ViewGroup mRoot;
    private final Runnable mShowRunnable = new Runnable() {
        public void run() {
            KeyguardBouncer.this.mRoot.setVisibility(0);
            KeyguardBouncer keyguardBouncer = KeyguardBouncer.this;
            keyguardBouncer.showPromptReason(keyguardBouncer.mBouncerPromptReason);
            CharSequence consumeCustomMessage = KeyguardBouncer.this.mCallback.consumeCustomMessage();
            if (consumeCustomMessage != null) {
                KeyguardBouncer.this.mKeyguardView.showErrorMessage(consumeCustomMessage);
            }
            if (KeyguardBouncer.this.mKeyguardView.getHeight() == 0 || KeyguardBouncer.this.mKeyguardView.getHeight() == KeyguardBouncer.this.mStatusBarHeight) {
                KeyguardBouncer.this.mKeyguardView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                    public boolean onPreDraw() {
                        KeyguardBouncer.this.mKeyguardView.getViewTreeObserver().removeOnPreDrawListener(this);
                        KeyguardBouncer.this.mKeyguardView.startAppearAnimation();
                        return true;
                    }
                });
                KeyguardBouncer.this.mKeyguardView.requestLayout();
            } else {
                KeyguardBouncer.this.mKeyguardView.startAppearAnimation();
            }
            KeyguardBouncer.this.mShowingSoon = false;
            if (KeyguardBouncer.this.mExpansion == 0.0f) {
                KeyguardBouncer.this.mKeyguardView.onResume();
                KeyguardBouncer.this.mKeyguardView.resetSecurityContainer();
            }
            SysUiStatsLog.write(63, 2);
        }
    };
    /* access modifiers changed from: private */
    public boolean mShowingSoon;
    /* access modifiers changed from: private */
    public int mStatusBarHeight;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
        public void onStrongAuthStateChanged(int i) {
            KeyguardBouncer keyguardBouncer = KeyguardBouncer.this;
            keyguardBouncer.mBouncerPromptReason = keyguardBouncer.mCallback.getBouncerPromptReason();
        }
    };

    public interface BouncerExpansionCallback {
        void onFullyHidden();

        void onFullyShown();

        void onStartingToHide();

        void onStartingToShow();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$KeyguardBouncer() {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView != null) {
            keyguardHostView.resetSecurityContainer();
        }
    }

    public KeyguardBouncer(Context context, ViewMediatorCallback viewMediatorCallback, LockPatternUtils lockPatternUtils, ViewGroup viewGroup, DismissCallbackRegistry dismissCallbackRegistry, FalsingManager falsingManager, BouncerExpansionCallback bouncerExpansionCallback, KeyguardStateController keyguardStateController, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardBypassController keyguardBypassController, Handler handler) {
        this.mContext = context;
        this.mCallback = viewMediatorCallback;
        this.mLockPatternUtils = lockPatternUtils;
        this.mContainer = viewGroup;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mFalsingManager = falsingManager;
        this.mDismissCallbackRegistry = dismissCallbackRegistry;
        this.mExpansionCallback = bouncerExpansionCallback;
        this.mHandler = handler;
        this.mKeyguardStateController = keyguardStateController;
        keyguardUpdateMonitor.registerCallback(this.mUpdateMonitorCallback);
        this.mKeyguardBypassController = keyguardBypassController;
    }

    public void show(boolean z) {
        show(z, true);
    }

    public void show(boolean z, boolean z2) {
        int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        if (currentUser != 0 || !UserManager.isSplitSystemUser()) {
            ensureView();
            this.mIsScrimmed = z2;
            if (z2) {
                setExpansion(0.0f);
            }
            if (z) {
                showPrimarySecurityScreen();
            }
            if (this.mRoot.getVisibility() != 0 && !this.mShowingSoon) {
                int currentUser2 = KeyguardUpdateMonitor.getCurrentUser();
                boolean z3 = false;
                if (!(UserManager.isSplitSystemUser() && currentUser2 == 0) && currentUser2 == currentUser) {
                    z3 = true;
                }
                if (!z3 || !this.mKeyguardView.dismiss(currentUser2)) {
                    if (!z3) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("User can't dismiss keyguard: ");
                        sb.append(currentUser2);
                        sb.append(" != ");
                        sb.append(currentUser);
                        Slog.w("KeyguardBouncer", sb.toString());
                    }
                    this.mShowingSoon = true;
                    DejankUtils.removeCallbacks(this.mResetRunnable);
                    if (!this.mKeyguardStateController.isFaceAuthEnabled() || needsFullscreenBouncer() || this.mKeyguardUpdateMonitor.userNeedsStrongAuth() || this.mKeyguardBypassController.getBypassEnabled()) {
                        DejankUtils.postAfterTraversal(this.mShowRunnable);
                    } else {
                        this.mHandler.postDelayed(this.mShowRunnable, 1200);
                    }
                    this.mCallback.onBouncerVisiblityChanged(true);
                    this.mExpansionCallback.onStartingToShow();
                }
            }
        }
    }

    public boolean isScrimmed() {
        return this.mIsScrimmed;
    }

    private void onFullyShown() {
        this.mFalsingManager.onBouncerShown();
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView == null) {
            Log.wtf("KeyguardBouncer", "onFullyShown when view was null");
        } else {
            keyguardHostView.onResume();
        }
    }

    private void onFullyHidden() {
        cancelShowRunnable();
        ViewGroup viewGroup = this.mRoot;
        if (viewGroup != null) {
            viewGroup.setVisibility(4);
        }
        this.mFalsingManager.onBouncerHidden();
        DejankUtils.postAfterTraversal(this.mResetRunnable);
    }

    public void showPromptReason(int i) {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView != null) {
            keyguardHostView.showPromptReason(i);
        } else {
            Log.w("KeyguardBouncer", "Trying to show prompt reason on empty bouncer");
        }
    }

    public void showMessage(String str, ColorStateList colorStateList) {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView != null) {
            keyguardHostView.showMessage(str, colorStateList);
        } else {
            Log.w("KeyguardBouncer", "Trying to show message on empty bouncer");
        }
    }

    private void cancelShowRunnable() {
        DejankUtils.removeCallbacks(this.mShowRunnable);
        this.mHandler.removeCallbacks(this.mShowRunnable);
        this.mShowingSoon = false;
    }

    public void showWithDismissAction(OnDismissAction onDismissAction, Runnable runnable) {
        ensureView();
        this.mKeyguardView.setOnDismissAction(onDismissAction, runnable);
        show(false);
    }

    public void hide(boolean z) {
        if (isShowing()) {
            SysUiStatsLog.write(63, 1);
            this.mDismissCallbackRegistry.notifyDismissCancelled();
        }
        this.mIsScrimmed = false;
        this.mFalsingManager.onBouncerHidden();
        this.mCallback.onBouncerVisiblityChanged(false);
        cancelShowRunnable();
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView != null) {
            keyguardHostView.cancelDismissAction();
            this.mKeyguardView.cleanUp();
        }
        this.mIsAnimatingAway = false;
        ViewGroup viewGroup = this.mRoot;
        if (viewGroup != null) {
            viewGroup.setVisibility(4);
            if (z) {
                this.mHandler.postDelayed(this.mRemoveViewRunnable, 50);
            }
        }
    }

    public void startPreHideAnimation(Runnable runnable) {
        this.mIsAnimatingAway = true;
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView != null) {
            keyguardHostView.startDisappearAnimation(runnable);
        } else if (runnable != null) {
            runnable.run();
        }
    }

    public void onScreenTurnedOff() {
        if (this.mKeyguardView != null) {
            ViewGroup viewGroup = this.mRoot;
            if (viewGroup != null && viewGroup.getVisibility() == 0) {
                this.mKeyguardView.onPause();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000c, code lost:
        if (r0.getVisibility() == 0) goto L_0x000e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isShowing() {
        /*
            r2 = this;
            boolean r0 = r2.mShowingSoon
            if (r0 != 0) goto L_0x000e
            android.view.ViewGroup r0 = r2.mRoot
            if (r0 == 0) goto L_0x001d
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x001d
        L_0x000e:
            float r0 = r2.mExpansion
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x001d
            boolean r2 = r2.isAnimatingAway()
            if (r2 != 0) goto L_0x001d
            r2 = 1
            goto L_0x001e
        L_0x001d:
            r2 = 0
        L_0x001e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.KeyguardBouncer.isShowing():boolean");
    }

    public boolean inTransit() {
        if (!this.mShowingSoon) {
            float f = this.mExpansion;
            if (f == 1.0f || f == 0.0f) {
                return false;
            }
        }
        return true;
    }

    public boolean isAnimatingAway() {
        return this.mIsAnimatingAway;
    }

    public void prepare() {
        boolean z = this.mRoot != null;
        ensureView();
        if (z) {
            showPrimarySecurityScreen();
        }
        this.mBouncerPromptReason = this.mCallback.getBouncerPromptReason();
    }

    private void showPrimarySecurityScreen() {
        this.mKeyguardView.showPrimarySecurityScreen();
    }

    public void setExpansion(float f) {
        float f2 = this.mExpansion;
        this.mExpansion = f;
        if (this.mKeyguardView != null && !this.mIsAnimatingAway) {
            this.mKeyguardView.setAlpha(MathUtils.constrain(MathUtils.map(0.95f, 1.0f, 1.0f, 0.0f, f), 0.0f, 1.0f));
            KeyguardHostView keyguardHostView = this.mKeyguardView;
            keyguardHostView.setTranslationY(((float) keyguardHostView.getHeight()) * f);
        }
        int i = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
        if (i == 0 && f2 != 0.0f) {
            onFullyShown();
            this.mExpansionCallback.onFullyShown();
        } else if (f == 1.0f && f2 != 1.0f) {
            onFullyHidden();
            this.mExpansionCallback.onFullyHidden();
        } else if (i != 0 && f2 == 0.0f) {
            this.mExpansionCallback.onStartingToHide();
        }
    }

    public boolean willDismissWithAction() {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        return keyguardHostView != null && keyguardHostView.hasDismissActions();
    }

    /* access modifiers changed from: protected */
    public void ensureView() {
        boolean hasCallbacks = this.mHandler.hasCallbacks(this.mRemoveViewRunnable);
        if (this.mRoot == null || hasCallbacks) {
            inflateView();
        }
    }

    /* access modifiers changed from: protected */
    public void inflateView() {
        removeView();
        this.mHandler.removeCallbacks(this.mRemoveViewRunnable);
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(this.mContext).inflate(C2013R$layout.keyguard_bouncer, null);
        this.mRoot = viewGroup;
        KeyguardHostView keyguardHostView = (KeyguardHostView) viewGroup.findViewById(C2011R$id.keyguard_host_view);
        this.mKeyguardView = keyguardHostView;
        keyguardHostView.setLockPatternUtils(this.mLockPatternUtils);
        this.mKeyguardView.setViewMediatorCallback(this.mCallback);
        ViewGroup viewGroup2 = this.mContainer;
        viewGroup2.addView(this.mRoot, viewGroup2.getChildCount());
        this.mStatusBarHeight = this.mRoot.getResources().getDimensionPixelOffset(C2009R$dimen.status_bar_height);
        this.mRoot.setVisibility(4);
        this.mRoot.setAccessibilityPaneTitle(this.mKeyguardView.getAccessibilityTitleForCurrentMode());
        WindowInsets rootWindowInsets = this.mRoot.getRootWindowInsets();
        if (rootWindowInsets != null) {
            this.mRoot.dispatchApplyWindowInsets(rootWindowInsets);
        }
    }

    /* access modifiers changed from: protected */
    public void removeView() {
        ViewGroup viewGroup = this.mRoot;
        if (viewGroup != null) {
            ViewParent parent = viewGroup.getParent();
            ViewGroup viewGroup2 = this.mContainer;
            if (parent == viewGroup2) {
                viewGroup2.removeView(this.mRoot);
                this.mRoot = null;
            }
        }
    }

    public boolean needsFullscreenBouncer() {
        SecurityMode securityMode = ((KeyguardSecurityModel) Dependency.get(KeyguardSecurityModel.class)).getSecurityMode(KeyguardUpdateMonitor.getCurrentUser());
        return securityMode == SecurityMode.SimPin || securityMode == SecurityMode.SimPuk;
    }

    public boolean isFullscreenBouncer() {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView == null) {
            return false;
        }
        SecurityMode currentSecurityMode = keyguardHostView.getCurrentSecurityMode();
        if (currentSecurityMode == SecurityMode.SimPin || currentSecurityMode == SecurityMode.SimPuk) {
            return true;
        }
        return false;
    }

    public boolean isSecure() {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        return keyguardHostView == null || keyguardHostView.getSecurityMode() != SecurityMode.None;
    }

    public boolean shouldDismissOnMenuPressed() {
        return this.mKeyguardView.shouldEnableMenuKey();
    }

    public boolean interceptMediaKey(KeyEvent keyEvent) {
        ensureView();
        return this.mKeyguardView.interceptMediaKey(keyEvent);
    }

    public void notifyKeyguardAuthenticated(boolean z) {
        ensureView();
        this.mKeyguardView.finish(z, KeyguardUpdateMonitor.getCurrentUser());
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("KeyguardBouncer");
        StringBuilder sb = new StringBuilder();
        sb.append("  isShowing(): ");
        sb.append(isShowing());
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mStatusBarHeight: ");
        sb2.append(this.mStatusBarHeight);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  mExpansion: ");
        sb3.append(this.mExpansion);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("  mKeyguardView; ");
        sb4.append(this.mKeyguardView);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("  mShowingSoon: ");
        sb5.append(this.mKeyguardView);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("  mBouncerPromptReason: ");
        sb6.append(this.mBouncerPromptReason);
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append("  mIsAnimatingAway: ");
        sb7.append(this.mIsAnimatingAway);
        printWriter.println(sb7.toString());
    }
}
