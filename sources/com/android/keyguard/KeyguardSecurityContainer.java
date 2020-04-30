package com.android.keyguard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Slog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewRootImpl;
import android.view.WindowInsets;
import android.view.WindowInsets.Type;
import android.widget.FrameLayout;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardSecurityModel.SecurityMode;
import com.android.settingslib.utils.ThreadUtils;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.InjectionInflationController;
import java.util.function.Supplier;

public class KeyguardSecurityContainer extends FrameLayout implements KeyguardSecurityView {
    private int mActivePointerId;
    private AlertDialog mAlertDialog;
    private KeyguardSecurityCallback mCallback;
    private SecurityMode mCurrentSecuritySelection;
    private KeyguardSecurityView mCurrentSecurityView;
    private InjectionInflationController mInjectionInflationController;
    private boolean mIsDragging;
    private final KeyguardStateController mKeyguardStateController;
    private float mLastTouchY;
    /* access modifiers changed from: private */
    public LockPatternUtils mLockPatternUtils;
    /* access modifiers changed from: private */
    public final MetricsLogger mMetricsLogger;
    private KeyguardSecurityCallback mNullCallback;
    private AdminSecondaryLockScreenController mSecondaryLockScreenController;
    /* access modifiers changed from: private */
    public SecurityCallback mSecurityCallback;
    private KeyguardSecurityModel mSecurityModel;
    private KeyguardSecurityViewFlipper mSecurityViewFlipper;
    private final SpringAnimation mSpringAnimation;
    private float mStartTouchY;
    private boolean mSwipeUpToRetry;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mUpdateMonitor;
    private final VelocityTracker mVelocityTracker;
    private final ViewConfiguration mViewConfiguration;

    /* renamed from: com.android.keyguard.KeyguardSecurityContainer$3 */
    static /* synthetic */ class C05513 {

        /* renamed from: $SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode */
        static final /* synthetic */ int[] f22xdc0e830a;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|16) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.keyguard.KeyguardSecurityModel$SecurityMode[] r0 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f22xdc0e830a = r0
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.Pattern     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f22xdc0e830a     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.PIN     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f22xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.Password     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = f22xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.Invalid     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = f22xdc0e830a     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.None     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = f22xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.SimPin     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = f22xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.SimPuk     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardSecurityContainer.C05513.<clinit>():void");
        }
    }

    public interface SecurityCallback {
        boolean dismiss(boolean z, int i);

        void finish(boolean z, int i);

        void onCancelClicked();

        void onSecurityModeChanged(SecurityMode securityMode, boolean z);

        void reset();

        void userActivity();
    }

    public boolean shouldDelayChildPressedState() {
        return true;
    }

    public KeyguardSecurityContainer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KeyguardSecurityContainer(Context context) {
        this(context, null, 0);
    }

    public KeyguardSecurityContainer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCurrentSecuritySelection = SecurityMode.Invalid;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mMetricsLogger = (MetricsLogger) Dependency.get(MetricsLogger.class);
        this.mLastTouchY = -1.0f;
        this.mActivePointerId = -1;
        this.mStartTouchY = -1.0f;
        this.mCallback = new KeyguardSecurityCallback() {
            public void userActivity() {
                if (KeyguardSecurityContainer.this.mSecurityCallback != null) {
                    KeyguardSecurityContainer.this.mSecurityCallback.userActivity();
                }
            }

            public void onUserInput() {
                KeyguardSecurityContainer.this.mUpdateMonitor.cancelFaceAuth();
            }

            public void dismiss(boolean z, int i) {
                KeyguardSecurityContainer.this.mSecurityCallback.dismiss(z, i);
            }

            public void reportUnlockAttempt(int i, boolean z, int i2) {
                if (z) {
                    SysUiStatsLog.write(64, 2);
                    KeyguardSecurityContainer.this.mLockPatternUtils.reportSuccessfulPasswordAttempt(i);
                    ThreadUtils.postOnBackgroundThread(C0511x8e4c0c84.INSTANCE);
                } else {
                    SysUiStatsLog.write(64, 1);
                    KeyguardSecurityContainer.this.reportFailedUnlockAttempt(i, i2);
                }
                KeyguardSecurityContainer.this.mMetricsLogger.write(new LogMaker(197).setType(z ? 10 : 11));
            }

            static /* synthetic */ void lambda$reportUnlockAttempt$0() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException unused) {
                }
                Runtime.getRuntime().gc();
            }

            public void reset() {
                KeyguardSecurityContainer.this.mSecurityCallback.reset();
            }

            public void onCancelClicked() {
                KeyguardSecurityContainer.this.mSecurityCallback.onCancelClicked();
            }
        };
        this.mNullCallback = new KeyguardSecurityCallback(this) {
            public void dismiss(boolean z, int i) {
            }

            public void onUserInput() {
            }

            public void reportUnlockAttempt(int i, boolean z, int i2) {
            }

            public void reset() {
            }

            public void userActivity() {
            }
        };
        this.mSecurityModel = (KeyguardSecurityModel) Dependency.get(KeyguardSecurityModel.class);
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        this.mSpringAnimation = new SpringAnimation(this, DynamicAnimation.f13Y);
        this.mInjectionInflationController = new InjectionInflationController(SystemUIFactory.getInstance().getRootComponent());
        this.mViewConfiguration = ViewConfiguration.get(context);
        this.mKeyguardStateController = (KeyguardStateController) Dependency.get(KeyguardStateController.class);
        AdminSecondaryLockScreenController adminSecondaryLockScreenController = new AdminSecondaryLockScreenController(context, this, this.mUpdateMonitor, this.mCallback, new Handler(Looper.myLooper()));
        this.mSecondaryLockScreenController = adminSecondaryLockScreenController;
    }

    public void setSecurityCallback(SecurityCallback securityCallback) {
        this.mSecurityCallback = securityCallback;
    }

    public void onResume(int i) {
        SecurityMode securityMode = this.mCurrentSecuritySelection;
        if (securityMode != SecurityMode.None) {
            getSecurityView(securityMode).onResume(i);
        }
        updateBiometricRetry();
    }

    public void onPause() {
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mAlertDialog = null;
        }
        this.mSecondaryLockScreenController.hide();
        SecurityMode securityMode = this.mCurrentSecuritySelection;
        if (securityMode != SecurityMode.None) {
            getSecurityView(securityMode).onPause();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x000e, code lost:
        if (r0 != 3) goto L_0x0061;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            int r0 = r6.getActionMasked()
            r1 = 0
            if (r0 == 0) goto L_0x004c
            r2 = 1
            if (r0 == r2) goto L_0x0049
            r3 = 2
            if (r0 == r3) goto L_0x0011
            r6 = 3
            if (r0 == r6) goto L_0x0049
            goto L_0x0061
        L_0x0011:
            boolean r0 = r5.mIsDragging
            if (r0 == 0) goto L_0x0016
            return r2
        L_0x0016:
            boolean r0 = r5.mSwipeUpToRetry
            if (r0 != 0) goto L_0x001b
            return r1
        L_0x001b:
            com.android.keyguard.KeyguardSecurityView r0 = r5.mCurrentSecurityView
            boolean r0 = r0.disallowInterceptTouch(r6)
            if (r0 == 0) goto L_0x0024
            return r1
        L_0x0024:
            int r0 = r5.mActivePointerId
            int r0 = r6.findPointerIndex(r0)
            android.view.ViewConfiguration r3 = r5.mViewConfiguration
            int r3 = r3.getScaledTouchSlop()
            float r3 = (float) r3
            r4 = 1082130432(0x40800000, float:4.0)
            float r3 = r3 * r4
            com.android.keyguard.KeyguardSecurityView r4 = r5.mCurrentSecurityView
            if (r4 == 0) goto L_0x0061
            r4 = -1
            if (r0 == r4) goto L_0x0061
            float r4 = r5.mStartTouchY
            float r6 = r6.getY(r0)
            float r4 = r4 - r6
            int r6 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x0061
            r5.mIsDragging = r2
            return r2
        L_0x0049:
            r5.mIsDragging = r1
            goto L_0x0061
        L_0x004c:
            int r0 = r6.getActionIndex()
            float r2 = r6.getY(r0)
            r5.mStartTouchY = r2
            int r6 = r6.getPointerId(r0)
            r5.mActivePointerId = r6
            android.view.VelocityTracker r5 = r5.mVelocityTracker
            r5.clear()
        L_0x0061:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardSecurityContainer.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        int i = 0;
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                this.mVelocityTracker.addMovement(motionEvent);
                float y = motionEvent.getY(motionEvent.findPointerIndex(this.mActivePointerId));
                float f = this.mLastTouchY;
                if (f != -1.0f) {
                    setTranslationY(getTranslationY() + ((y - f) * 0.25f));
                }
                this.mLastTouchY = y;
            } else if (actionMasked != 3) {
                if (actionMasked == 6) {
                    int actionIndex = motionEvent.getActionIndex();
                    if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
                        if (actionIndex == 0) {
                            i = 1;
                        }
                        this.mLastTouchY = motionEvent.getY(i);
                        this.mActivePointerId = motionEvent.getPointerId(i);
                    }
                }
            }
            if (actionMasked == 1 && (-getTranslationY()) > TypedValue.applyDimension(1, 10.0f, getResources().getDisplayMetrics()) && !this.mUpdateMonitor.isFaceDetectionRunning()) {
                this.mUpdateMonitor.requestFaceAuth();
                this.mCallback.userActivity();
                showMessage(null, null);
            }
            return true;
        }
        this.mActivePointerId = -1;
        this.mLastTouchY = -1.0f;
        this.mIsDragging = false;
        startSpringAnimation(this.mVelocityTracker.getYVelocity());
        this.mUpdateMonitor.requestFaceAuth();
        this.mCallback.userActivity();
        showMessage(null, null);
        return true;
    }

    private void startSpringAnimation(float f) {
        SpringAnimation springAnimation = this.mSpringAnimation;
        springAnimation.setStartVelocity(f);
        springAnimation.animateToFinalPosition(0.0f);
    }

    public void startAppearAnimation() {
        SecurityMode securityMode = this.mCurrentSecuritySelection;
        if (securityMode != SecurityMode.None) {
            getSecurityView(securityMode).startAppearAnimation();
        }
    }

    public boolean startDisappearAnimation(Runnable runnable) {
        SecurityMode securityMode = this.mCurrentSecuritySelection;
        if (securityMode != SecurityMode.None) {
            return getSecurityView(securityMode).startDisappearAnimation(runnable);
        }
        return false;
    }

    private void updateBiometricRetry() {
        SecurityMode securityMode = getSecurityMode();
        this.mSwipeUpToRetry = (!this.mKeyguardStateController.isFaceAuthEnabled() || securityMode == SecurityMode.SimPin || securityMode == SecurityMode.SimPuk || securityMode == SecurityMode.None) ? false : true;
    }

    public CharSequence getTitle() {
        return this.mSecurityViewFlipper.getTitle();
    }

    private KeyguardSecurityView getSecurityView(SecurityMode securityMode) {
        KeyguardSecurityView keyguardSecurityView;
        int securityViewIdForMode = getSecurityViewIdForMode(securityMode);
        int childCount = this.mSecurityViewFlipper.getChildCount();
        int i = 0;
        while (true) {
            if (i >= childCount) {
                keyguardSecurityView = null;
                break;
            } else if (this.mSecurityViewFlipper.getChildAt(i).getId() == securityViewIdForMode) {
                keyguardSecurityView = (KeyguardSecurityView) this.mSecurityViewFlipper.getChildAt(i);
                break;
            } else {
                i++;
            }
        }
        int layoutIdFor = getLayoutIdFor(securityMode);
        if (keyguardSecurityView != null || layoutIdFor == 0) {
            return keyguardSecurityView;
        }
        View inflate = this.mInjectionInflationController.injectable(LayoutInflater.from(this.mContext)).inflate(layoutIdFor, this.mSecurityViewFlipper, false);
        this.mSecurityViewFlipper.addView(inflate);
        updateSecurityView(inflate);
        KeyguardSecurityView keyguardSecurityView2 = (KeyguardSecurityView) inflate;
        keyguardSecurityView2.reset();
        return keyguardSecurityView2;
    }

    private void updateSecurityView(View view) {
        if (view instanceof KeyguardSecurityView) {
            KeyguardSecurityView keyguardSecurityView = (KeyguardSecurityView) view;
            keyguardSecurityView.setKeyguardCallback(this.mCallback);
            keyguardSecurityView.setLockPatternUtils(this.mLockPatternUtils);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("View ");
        sb.append(view);
        sb.append(" is not a KeyguardSecurityView");
        Log.w("KeyguardSecurityView", sb.toString());
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        KeyguardSecurityViewFlipper keyguardSecurityViewFlipper = (KeyguardSecurityViewFlipper) findViewById(C2011R$id.view_flipper);
        this.mSecurityViewFlipper = keyguardSecurityViewFlipper;
        keyguardSecurityViewFlipper.setLockPatternUtils(this.mLockPatternUtils);
    }

    public void setLockPatternUtils(LockPatternUtils lockPatternUtils) {
        this.mLockPatternUtils = lockPatternUtils;
        this.mSecurityModel.setLockPatternUtils(lockPatternUtils);
        this.mSecurityViewFlipper.setLockPatternUtils(this.mLockPatternUtils);
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        int i;
        if (ViewRootImpl.sNewInsetsMode == 2) {
            i = Integer.max(windowInsets.getInsetsIgnoringVisibility(Type.systemBars()).bottom, windowInsets.getInsets(Type.ime()).bottom);
        } else {
            i = windowInsets.getSystemWindowInsetBottom();
        }
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), i);
        return windowInsets.inset(0, 0, 0, i);
    }

    private void showDialog(String str, String str2) {
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        AlertDialog create = new Builder(this.mContext).setTitle(str).setMessage(str2).setCancelable(false).setNeutralButton(C2017R$string.f31ok, null).create();
        this.mAlertDialog = create;
        if (!(this.mContext instanceof Activity)) {
            create.getWindow().setType(2009);
        }
        this.mAlertDialog.show();
    }

    private void showTimeoutDialog(int i, int i2) {
        int i3;
        int i4 = i2 / 1000;
        int i5 = C05513.f22xdc0e830a[this.mSecurityModel.getSecurityMode(i).ordinal()];
        if (i5 == 1) {
            i3 = C2017R$string.kg_too_many_failed_pattern_attempts_dialog_message;
        } else if (i5 == 2) {
            i3 = C2017R$string.kg_too_many_failed_pin_attempts_dialog_message;
        } else if (i5 != 3) {
            i3 = 0;
        } else {
            i3 = C2017R$string.kg_too_many_failed_password_attempts_dialog_message;
        }
        if (i3 != 0) {
            showDialog(null, this.mContext.getString(i3, new Object[]{Integer.valueOf(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(i)), Integer.valueOf(i4)}));
        }
    }

    private void showAlmostAtWipeDialog(int i, int i2, int i3) {
        String str;
        if (i3 == 1) {
            str = this.mContext.getString(C2017R$string.kg_failed_attempts_almost_at_wipe, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        } else if (i3 == 2) {
            str = this.mContext.getString(C2017R$string.kg_failed_attempts_almost_at_erase_profile, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        } else if (i3 != 3) {
            str = null;
        } else {
            str = this.mContext.getString(C2017R$string.kg_failed_attempts_almost_at_erase_user, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        }
        showDialog(null, str);
    }

    private void showWipeDialog(int i, int i2) {
        String str;
        if (i2 == 1) {
            str = this.mContext.getString(C2017R$string.kg_failed_attempts_now_wiping, new Object[]{Integer.valueOf(i)});
        } else if (i2 == 2) {
            str = this.mContext.getString(C2017R$string.kg_failed_attempts_now_erasing_profile, new Object[]{Integer.valueOf(i)});
        } else if (i2 != 3) {
            str = null;
        } else {
            str = this.mContext.getString(C2017R$string.kg_failed_attempts_now_erasing_user, new Object[]{Integer.valueOf(i)});
        }
        showDialog(null, str);
    }

    /* access modifiers changed from: private */
    public void reportFailedUnlockAttempt(int i, int i2) {
        int i3 = 1;
        int currentFailedPasswordAttempts = this.mLockPatternUtils.getCurrentFailedPasswordAttempts(i) + 1;
        DevicePolicyManager devicePolicyManager = this.mLockPatternUtils.getDevicePolicyManager();
        int maximumFailedPasswordsForWipe = devicePolicyManager.getMaximumFailedPasswordsForWipe(null, i);
        int i4 = maximumFailedPasswordsForWipe > 0 ? maximumFailedPasswordsForWipe - currentFailedPasswordAttempts : Integer.MAX_VALUE;
        if (i4 < 5) {
            int profileWithMinimumFailedPasswordsForWipe = devicePolicyManager.getProfileWithMinimumFailedPasswordsForWipe(i);
            if (profileWithMinimumFailedPasswordsForWipe == i) {
                if (profileWithMinimumFailedPasswordsForWipe != 0) {
                    i3 = 3;
                }
            } else if (profileWithMinimumFailedPasswordsForWipe != -10000) {
                i3 = 2;
            }
            if (i4 > 0) {
                showAlmostAtWipeDialog(currentFailedPasswordAttempts, i4, i3);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Too many unlock attempts; user ");
                sb.append(profileWithMinimumFailedPasswordsForWipe);
                sb.append(" will be wiped!");
                Slog.i("KeyguardSecurityView", sb.toString());
                showWipeDialog(currentFailedPasswordAttempts, i3);
            }
        }
        this.mLockPatternUtils.reportFailedPasswordAttempt(i);
        if (i2 > 0) {
            this.mLockPatternUtils.reportPasswordLockout(i2, i);
            showTimeoutDialog(i, i2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPrimarySecurityScreen$0 */
    public /* synthetic */ SecurityMode lambda$showPrimarySecurityScreen$0$KeyguardSecurityContainer() {
        return this.mSecurityModel.getSecurityMode(KeyguardUpdateMonitor.getCurrentUser());
    }

    /* access modifiers changed from: 0000 */
    public void showPrimarySecurityScreen(boolean z) {
        showSecurityScreen((SecurityMode) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier() {
            public final Object get() {
                return KeyguardSecurityContainer.this.lambda$showPrimarySecurityScreen$0$KeyguardSecurityContainer();
            }
        }));
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x009d  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00b4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean showNextSecurityScreenOrFinish(boolean r8, int r9) {
        /*
            r7 = this;
            com.android.keyguard.KeyguardUpdateMonitor r0 = r7.mUpdateMonitor
            boolean r0 = r0.getUserHasTrust(r9)
            r1 = 2
            r2 = 3
            r3 = -1
            r4 = 0
            r5 = 1
            if (r0 == 0) goto L_0x0011
            r1 = r2
        L_0x000e:
            r8 = r4
            goto L_0x008b
        L_0x0011:
            com.android.keyguard.KeyguardUpdateMonitor r0 = r7.mUpdateMonitor
            boolean r0 = r0.getUserUnlockedWithBiometric(r9)
            if (r0 == 0) goto L_0x001a
            goto L_0x000e
        L_0x001a:
            com.android.keyguard.KeyguardSecurityModel$SecurityMode r0 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.None
            com.android.keyguard.KeyguardSecurityModel$SecurityMode r6 = r7.mCurrentSecuritySelection
            if (r0 != r6) goto L_0x0032
            com.android.keyguard.KeyguardSecurityModel r8 = r7.mSecurityModel
            com.android.keyguard.KeyguardSecurityModel$SecurityMode r8 = r8.getSecurityMode(r9)
            com.android.keyguard.KeyguardSecurityModel$SecurityMode r0 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.None
            if (r0 != r8) goto L_0x002c
            r1 = r4
            goto L_0x000e
        L_0x002c:
            r7.showSecurityScreen(r8)
            r1 = r3
            r5 = r4
            goto L_0x000e
        L_0x0032:
            if (r8 == 0) goto L_0x0088
            int[] r8 = com.android.keyguard.KeyguardSecurityContainer.C05513.f22xdc0e830a
            int r0 = r6.ordinal()
            r8 = r8[r0]
            if (r8 == r5) goto L_0x0085
            if (r8 == r1) goto L_0x0085
            if (r8 == r2) goto L_0x0085
            r0 = 6
            if (r8 == r0) goto L_0x0069
            r0 = 7
            if (r8 == r0) goto L_0x0069
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r0 = "Bad security screen "
            r8.append(r0)
            com.android.keyguard.KeyguardSecurityModel$SecurityMode r0 = r7.mCurrentSecuritySelection
            r8.append(r0)
            java.lang.String r0 = ", fail safe"
            r8.append(r0)
            java.lang.String r8 = r8.toString()
            java.lang.String r0 = "KeyguardSecurityView"
            android.util.Log.v(r0, r8)
            r7.showPrimarySecurityScreen(r4)
            goto L_0x0088
        L_0x0069:
            com.android.keyguard.KeyguardSecurityModel r8 = r7.mSecurityModel
            com.android.keyguard.KeyguardSecurityModel$SecurityMode r8 = r8.getSecurityMode(r9)
            com.android.keyguard.KeyguardSecurityModel$SecurityMode r0 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.None
            if (r8 != r0) goto L_0x0081
            com.android.internal.widget.LockPatternUtils r0 = r7.mLockPatternUtils
            int r1 = com.android.keyguard.KeyguardUpdateMonitor.getCurrentUser()
            boolean r0 = r0.isLockScreenDisabled(r1)
            if (r0 == 0) goto L_0x0081
            r1 = 4
            goto L_0x000e
        L_0x0081:
            r7.showSecurityScreen(r8)
            goto L_0x0088
        L_0x0085:
            r8 = r5
            r1 = r8
            goto L_0x008b
        L_0x0088:
            r1 = r3
            r8 = r4
            r5 = r8
        L_0x008b:
            if (r5 == 0) goto L_0x009b
            com.android.keyguard.KeyguardUpdateMonitor r0 = r7.mUpdateMonitor
            android.content.Intent r0 = r0.getSecondaryLockscreenRequirement(r9)
            if (r0 == 0) goto L_0x009b
            com.android.keyguard.AdminSecondaryLockScreenController r7 = r7.mSecondaryLockScreenController
            r7.show(r0)
            return r4
        L_0x009b:
            if (r1 == r3) goto L_0x00b2
            com.android.internal.logging.MetricsLogger r0 = r7.mMetricsLogger
            android.metrics.LogMaker r2 = new android.metrics.LogMaker
            r3 = 197(0xc5, float:2.76E-43)
            r2.<init>(r3)
            r3 = 5
            android.metrics.LogMaker r2 = r2.setType(r3)
            android.metrics.LogMaker r1 = r2.setSubtype(r1)
            r0.write(r1)
        L_0x00b2:
            if (r5 == 0) goto L_0x00b9
            com.android.keyguard.KeyguardSecurityContainer$SecurityCallback r7 = r7.mSecurityCallback
            r7.finish(r8, r9)
        L_0x00b9:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardSecurityContainer.showNextSecurityScreenOrFinish(boolean, int):boolean");
    }

    private void showSecurityScreen(SecurityMode securityMode) {
        SecurityMode securityMode2 = this.mCurrentSecuritySelection;
        if (securityMode != securityMode2) {
            KeyguardSecurityView securityView = getSecurityView(securityMode2);
            KeyguardSecurityView securityView2 = getSecurityView(securityMode);
            if (securityView != null) {
                securityView.onPause();
                securityView.setKeyguardCallback(this.mNullCallback);
            }
            if (securityMode != SecurityMode.None) {
                securityView2.onResume(2);
                securityView2.setKeyguardCallback(this.mCallback);
            }
            int childCount = this.mSecurityViewFlipper.getChildCount();
            int securityViewIdForMode = getSecurityViewIdForMode(securityMode);
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= childCount) {
                    break;
                } else if (this.mSecurityViewFlipper.getChildAt(i).getId() == securityViewIdForMode) {
                    this.mSecurityViewFlipper.setDisplayedChild(i);
                    break;
                } else {
                    i++;
                }
            }
            this.mCurrentSecuritySelection = securityMode;
            this.mCurrentSecurityView = securityView2;
            SecurityCallback securityCallback = this.mSecurityCallback;
            if (securityMode != SecurityMode.None && securityView2.needsInput()) {
                z = true;
            }
            securityCallback.onSecurityModeChanged(securityMode, z);
        }
    }

    private int getSecurityViewIdForMode(SecurityMode securityMode) {
        int i = C05513.f22xdc0e830a[securityMode.ordinal()];
        if (i == 1) {
            return C2011R$id.keyguard_pattern_view;
        }
        if (i == 2) {
            return C2011R$id.keyguard_pin_view;
        }
        if (i == 3) {
            return C2011R$id.keyguard_password_view;
        }
        if (i == 6) {
            return C2011R$id.keyguard_sim_pin_view;
        }
        if (i != 7) {
            return 0;
        }
        return C2011R$id.keyguard_sim_puk_view;
    }

    public int getLayoutIdFor(SecurityMode securityMode) {
        int i = C05513.f22xdc0e830a[securityMode.ordinal()];
        if (i == 1) {
            return C2013R$layout.keyguard_pattern_view;
        }
        if (i == 2) {
            return C2013R$layout.keyguard_pin_view;
        }
        if (i == 3) {
            return C2013R$layout.keyguard_password_view;
        }
        if (i == 6) {
            return C2013R$layout.keyguard_sim_pin_view;
        }
        if (i != 7) {
            return 0;
        }
        return C2013R$layout.keyguard_sim_puk_view;
    }

    public SecurityMode getSecurityMode() {
        return this.mSecurityModel.getSecurityMode(KeyguardUpdateMonitor.getCurrentUser());
    }

    public SecurityMode getCurrentSecurityMode() {
        return this.mCurrentSecuritySelection;
    }

    public boolean needsInput() {
        return this.mSecurityViewFlipper.needsInput();
    }

    public void setKeyguardCallback(KeyguardSecurityCallback keyguardSecurityCallback) {
        this.mSecurityViewFlipper.setKeyguardCallback(keyguardSecurityCallback);
    }

    public void reset() {
        this.mSecurityViewFlipper.reset();
    }

    public void showPromptReason(int i) {
        if (this.mCurrentSecuritySelection != SecurityMode.None) {
            if (i != 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("Strong auth required, reason: ");
                sb.append(i);
                Log.i("KeyguardSecurityView", sb.toString());
            }
            getSecurityView(this.mCurrentSecuritySelection).showPromptReason(i);
        }
    }

    public void showMessage(CharSequence charSequence, ColorStateList colorStateList) {
        SecurityMode securityMode = this.mCurrentSecuritySelection;
        if (securityMode != SecurityMode.None) {
            getSecurityView(securityMode).showMessage(charSequence, colorStateList);
        }
    }
}
