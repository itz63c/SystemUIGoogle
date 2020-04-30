package com.android.systemui.biometrics;

import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.UserManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowInsets.Type;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle.Observer;

public class AuthContainerView extends LinearLayout implements AuthDialog, Observer {
    @VisibleForTesting
    final ImageView mBackgroundView;
    @VisibleForTesting
    final BiometricCallback mBiometricCallback;
    @VisibleForTesting
    final ScrollView mBiometricScrollView;
    @VisibleForTesting
    AuthBiometricView mBiometricView;
    final Config mConfig;
    private int mContainerState = 0;
    byte[] mCredentialAttestation;
    private final CredentialCallback mCredentialCallback;
    @VisibleForTesting
    AuthCredentialView mCredentialView;
    final int mEffectiveUserId;
    @VisibleForTesting
    final FrameLayout mFrameLayout;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    /* access modifiers changed from: private */
    public final Injector mInjector;
    private final Interpolator mLinearOutSlowIn;
    private final AuthPanelController mPanelController;
    private final View mPanelView;
    Integer mPendingCallbackReason;
    private final float mTranslationY;
    @VisibleForTesting
    final WakefulnessLifecycle mWakefulnessLifecycle;
    private final WindowManager mWindowManager;
    private final IBinder mWindowToken = new Binder();

    @VisibleForTesting
    final class BiometricCallback implements Callback {
        BiometricCallback() {
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onAction$0 */
        public /* synthetic */ void lambda$onAction$0$AuthContainerView$BiometricCallback() {
            AuthContainerView.this.addCredentialView(false, true);
        }

        public void onAction(int i) {
            switch (i) {
                case 1:
                    AuthContainerView.this.animateAway(4);
                    return;
                case 2:
                    AuthContainerView.this.animateAway(1);
                    return;
                case 3:
                    AuthContainerView.this.animateAway(2);
                    return;
                case 4:
                    AuthContainerView.this.mConfig.mCallback.onTryAgainPressed();
                    return;
                case 5:
                    AuthContainerView.this.animateAway(5);
                    return;
                case 6:
                    AuthContainerView.this.mConfig.mCallback.onDeviceCredentialPressed();
                    AuthContainerView.this.mHandler.postDelayed(new Runnable() {
                        public final void run() {
                            BiometricCallback.this.lambda$onAction$0$AuthContainerView$BiometricCallback();
                        }
                    }, (long) AuthContainerView.this.mInjector.getAnimateCredentialStartDelayMs());
                    return;
                default:
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unhandled action: ");
                    sb.append(i);
                    Log.e("BiometricPrompt/AuthContainerView", sb.toString());
                    return;
            }
        }
    }

    public static class Builder {
        Config mConfig;

        public Builder(Context context) {
            Config config = new Config();
            this.mConfig = config;
            config.mContext = context;
        }

        public Builder setCallback(AuthDialogCallback authDialogCallback) {
            this.mConfig.mCallback = authDialogCallback;
            return this;
        }

        public Builder setBiometricPromptBundle(Bundle bundle) {
            this.mConfig.mBiometricPromptBundle = bundle;
            return this;
        }

        public Builder setRequireConfirmation(boolean z) {
            this.mConfig.mRequireConfirmation = z;
            return this;
        }

        public Builder setUserId(int i) {
            this.mConfig.mUserId = i;
            return this;
        }

        public Builder setOpPackageName(String str) {
            this.mConfig.mOpPackageName = str;
            return this;
        }

        public Builder setSkipIntro(boolean z) {
            this.mConfig.mSkipIntro = z;
            return this;
        }

        public Builder setOperationId(long j) {
            this.mConfig.mOperationId = j;
            return this;
        }

        public AuthContainerView build(int i) {
            this.mConfig.mModalityMask = i;
            return new AuthContainerView(this.mConfig, new Injector());
        }
    }

    static class Config {
        Bundle mBiometricPromptBundle;
        AuthDialogCallback mCallback;
        Context mContext;
        int mModalityMask;
        String mOpPackageName;
        long mOperationId;
        boolean mRequireConfirmation;
        boolean mSkipIntro;
        int mUserId;

        Config() {
        }
    }

    final class CredentialCallback implements Callback {
        CredentialCallback() {
        }

        public void onCredentialMatched(byte[] bArr) {
            AuthContainerView authContainerView = AuthContainerView.this;
            authContainerView.mCredentialAttestation = bArr;
            authContainerView.animateAway(7);
        }
    }

    public static class Injector {
        /* access modifiers changed from: 0000 */
        public int getAnimateCredentialStartDelayMs() {
            return 300;
        }

        /* access modifiers changed from: 0000 */
        public ScrollView getBiometricScrollView(FrameLayout frameLayout) {
            return (ScrollView) frameLayout.findViewById(C2011R$id.biometric_scrollview);
        }

        /* access modifiers changed from: 0000 */
        public FrameLayout inflateContainerView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
            return (FrameLayout) layoutInflater.inflate(C2013R$layout.auth_container_view, viewGroup, false);
        }

        /* access modifiers changed from: 0000 */
        public AuthPanelController getPanelController(Context context, View view) {
            return new AuthPanelController(context, view);
        }

        /* access modifiers changed from: 0000 */
        public ImageView getBackgroundView(FrameLayout frameLayout) {
            return (ImageView) frameLayout.findViewById(C2011R$id.background);
        }

        /* access modifiers changed from: 0000 */
        public View getPanelView(FrameLayout frameLayout) {
            return frameLayout.findViewById(C2011R$id.panel);
        }

        /* access modifiers changed from: 0000 */
        public UserManager getUserManager(Context context) {
            return UserManager.get(context);
        }

        /* access modifiers changed from: 0000 */
        public int getCredentialType(Context context, int i) {
            return Utils.getCredentialType(context, i);
        }
    }

    @VisibleForTesting
    AuthContainerView(Config config, Injector injector) {
        super(config.mContext);
        this.mConfig = config;
        this.mInjector = injector;
        this.mEffectiveUserId = injector.getUserManager(this.mContext).getCredentialOwnerProfile(this.mConfig.mUserId);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mWindowManager = (WindowManager) this.mContext.getSystemService(WindowManager.class);
        this.mWakefulnessLifecycle = (WakefulnessLifecycle) Dependency.get(WakefulnessLifecycle.class);
        this.mTranslationY = getResources().getDimension(C2009R$dimen.biometric_dialog_animation_translation_offset);
        this.mLinearOutSlowIn = Interpolators.LINEAR_OUT_SLOW_IN;
        this.mBiometricCallback = new BiometricCallback();
        this.mCredentialCallback = new CredentialCallback();
        LayoutInflater from = LayoutInflater.from(this.mContext);
        FrameLayout inflateContainerView = this.mInjector.inflateContainerView(from, this);
        this.mFrameLayout = inflateContainerView;
        View panelView = this.mInjector.getPanelView(inflateContainerView);
        this.mPanelView = panelView;
        this.mPanelController = this.mInjector.getPanelController(this.mContext, panelView);
        if (Utils.isBiometricAllowed(this.mConfig.mBiometricPromptBundle)) {
            int i = config.mModalityMask;
            if (i == 2) {
                this.mBiometricView = (AuthBiometricFingerprintView) from.inflate(C2013R$layout.auth_biometric_fingerprint_view, null, false);
            } else if (i == 8) {
                this.mBiometricView = (AuthBiometricFaceView) from.inflate(C2013R$layout.auth_biometric_face_view, null, false);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Unsupported biometric modality: ");
                sb.append(config.mModalityMask);
                Log.e("BiometricPrompt/AuthContainerView", sb.toString());
                this.mBiometricView = null;
                this.mBackgroundView = null;
                this.mBiometricScrollView = null;
                return;
            }
        }
        this.mBiometricScrollView = this.mInjector.getBiometricScrollView(this.mFrameLayout);
        this.mBackgroundView = this.mInjector.getBackgroundView(this.mFrameLayout);
        addView(this.mFrameLayout);
        setOnKeyListener(new OnKeyListener() {
            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                return AuthContainerView.this.lambda$new$0$AuthContainerView(view, i, keyEvent);
            }
        });
        setFocusableInTouchMode(true);
        requestFocus();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ boolean lambda$new$0$AuthContainerView(View view, int i, KeyEvent keyEvent) {
        if (i != 4) {
            return false;
        }
        if (keyEvent.getAction() == 1) {
            animateAway(1);
        }
        return true;
    }

    public boolean isAllowDeviceCredentials() {
        return Utils.isDeviceCredentialAllowed(this.mConfig.mBiometricPromptBundle);
    }

    private void addBiometricView() {
        this.mBiometricView.setRequireConfirmation(this.mConfig.mRequireConfirmation);
        this.mBiometricView.setPanelController(this.mPanelController);
        this.mBiometricView.setBiometricPromptBundle(this.mConfig.mBiometricPromptBundle);
        this.mBiometricView.setCallback(this.mBiometricCallback);
        this.mBiometricView.setBackgroundView(this.mBackgroundView);
        this.mBiometricView.setUserId(this.mConfig.mUserId);
        this.mBiometricView.setEffectiveUserId(this.mEffectiveUserId);
        this.mBiometricScrollView.addView(this.mBiometricView);
    }

    /* access modifiers changed from: private */
    public void addCredentialView(boolean z, boolean z2) {
        LayoutInflater from = LayoutInflater.from(this.mContext);
        int credentialType = this.mInjector.getCredentialType(this.mContext, this.mEffectiveUserId);
        if (credentialType != 1) {
            if (credentialType == 2) {
                this.mCredentialView = (AuthCredentialView) from.inflate(C2013R$layout.auth_credential_pattern_view, null, false);
                this.mBackgroundView.setOnClickListener(null);
                this.mBackgroundView.setImportantForAccessibility(2);
                this.mCredentialView.setContainerView(this);
                this.mCredentialView.setUserId(this.mConfig.mUserId);
                this.mCredentialView.setOperationId(this.mConfig.mOperationId);
                this.mCredentialView.setEffectiveUserId(this.mEffectiveUserId);
                this.mCredentialView.setCredentialType(credentialType);
                this.mCredentialView.setCallback(this.mCredentialCallback);
                this.mCredentialView.setBiometricPromptBundle(this.mConfig.mBiometricPromptBundle);
                this.mCredentialView.setPanelController(this.mPanelController, z);
                this.mCredentialView.setShouldAnimateContents(z2);
                this.mFrameLayout.addView(this.mCredentialView);
            } else if (credentialType != 3) {
                StringBuilder sb = new StringBuilder();
                sb.append("Unknown credential type: ");
                sb.append(credentialType);
                throw new IllegalStateException(sb.toString());
            }
        }
        this.mCredentialView = (AuthCredentialView) from.inflate(C2013R$layout.auth_credential_password_view, null, false);
        this.mBackgroundView.setOnClickListener(null);
        this.mBackgroundView.setImportantForAccessibility(2);
        this.mCredentialView.setContainerView(this);
        this.mCredentialView.setUserId(this.mConfig.mUserId);
        this.mCredentialView.setOperationId(this.mConfig.mOperationId);
        this.mCredentialView.setEffectiveUserId(this.mEffectiveUserId);
        this.mCredentialView.setCredentialType(credentialType);
        this.mCredentialView.setCallback(this.mCredentialCallback);
        this.mCredentialView.setBiometricPromptBundle(this.mConfig.mBiometricPromptBundle);
        this.mCredentialView.setPanelController(this.mPanelController, z);
        this.mCredentialView.setShouldAnimateContents(z2);
        this.mFrameLayout.addView(this.mCredentialView);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mPanelController.setContainerDimensions(getMeasuredWidth(), getMeasuredHeight());
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        onAttachedToWindowInternal();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void onAttachedToWindowInternal() {
        this.mWakefulnessLifecycle.addObserver(this);
        if (Utils.isBiometricAllowed(this.mConfig.mBiometricPromptBundle)) {
            addBiometricView();
        } else if (Utils.isDeviceCredentialAllowed(this.mConfig.mBiometricPromptBundle)) {
            addCredentialView(true, false);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Unknown configuration: ");
            sb.append(Utils.getAuthenticators(this.mConfig.mBiometricPromptBundle));
            throw new IllegalStateException(sb.toString());
        }
        if (this.mConfig.mSkipIntro) {
            this.mContainerState = 3;
            return;
        }
        this.mContainerState = 1;
        this.mPanelView.setY(this.mTranslationY);
        this.mBiometricScrollView.setY(this.mTranslationY);
        setAlpha(0.0f);
        postOnAnimation(new Runnable() {
            public final void run() {
                AuthContainerView.this.lambda$onAttachedToWindowInternal$1$AuthContainerView();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onAttachedToWindowInternal$1 */
    public /* synthetic */ void lambda$onAttachedToWindowInternal$1$AuthContainerView() {
        this.mPanelView.animate().translationY(0.0f).setDuration(250).setInterpolator(this.mLinearOutSlowIn).withLayer().withEndAction(new Runnable() {
            public final void run() {
                AuthContainerView.this.onDialogAnimatedIn();
            }
        }).start();
        this.mBiometricScrollView.animate().translationY(0.0f).setDuration(250).setInterpolator(this.mLinearOutSlowIn).withLayer().start();
        AuthCredentialView authCredentialView = this.mCredentialView;
        if (authCredentialView != null && authCredentialView.isAttachedToWindow()) {
            this.mCredentialView.setY(this.mTranslationY);
            this.mCredentialView.animate().translationY(0.0f).setDuration(250).setInterpolator(this.mLinearOutSlowIn).withLayer().start();
        }
        animate().alpha(1.0f).setDuration(250).setInterpolator(this.mLinearOutSlowIn).withLayer().start();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mWakefulnessLifecycle.removeObserver(this);
    }

    public void onStartedGoingToSleep() {
        animateAway(1);
    }

    public void show(WindowManager windowManager, Bundle bundle) {
        AuthBiometricView authBiometricView = this.mBiometricView;
        if (authBiometricView != null) {
            authBiometricView.restoreState(bundle);
        }
        windowManager.addView(this, getLayoutParams(this.mWindowToken));
    }

    public void dismissWithoutCallback(boolean z) {
        if (z) {
            animateAway(false, 0);
        } else {
            removeWindowIfAttached();
        }
    }

    public void dismissFromSystemServer() {
        removeWindowIfAttached();
    }

    public void onAuthenticationSucceeded() {
        this.mBiometricView.onAuthenticationSucceeded();
    }

    public void onAuthenticationFailed(String str) {
        this.mBiometricView.onAuthenticationFailed(str);
    }

    public void onHelp(String str) {
        this.mBiometricView.onHelp(str);
    }

    public void onError(String str) {
        this.mBiometricView.onError(str);
    }

    public void onSaveState(Bundle bundle) {
        bundle.putInt("container_state", this.mContainerState);
        boolean z = true;
        bundle.putBoolean("biometric_showing", this.mBiometricView != null && this.mCredentialView == null);
        if (this.mCredentialView == null) {
            z = false;
        }
        bundle.putBoolean("credential_showing", z);
        AuthBiometricView authBiometricView = this.mBiometricView;
        if (authBiometricView != null) {
            authBiometricView.onSaveState(bundle);
        }
    }

    public String getOpPackageName() {
        return this.mConfig.mOpPackageName;
    }

    public void animateToCredentialUI() {
        this.mBiometricView.startTransitionToCredentialUI();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void animateAway(int i) {
        animateAway(true, i);
    }

    private void animateAway(boolean z, int i) {
        int i2 = this.mContainerState;
        String str = "BiometricPrompt/AuthContainerView";
        if (i2 == 1) {
            Log.w(str, "startDismiss(): waiting for onDialogAnimatedIn");
            this.mContainerState = 2;
        } else if (i2 == 4) {
            StringBuilder sb = new StringBuilder();
            sb.append("Already dismissing, sendReason: ");
            sb.append(z);
            sb.append(" reason: ");
            sb.append(i);
            Log.w(str, sb.toString());
        } else {
            this.mContainerState = 4;
            if (z) {
                this.mPendingCallbackReason = Integer.valueOf(i);
            } else {
                this.mPendingCallbackReason = null;
            }
            postOnAnimation(new Runnable(new Runnable() {
                public final void run() {
                    AuthContainerView.this.lambda$animateAway$2$AuthContainerView();
                }
            }) {
                public final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AuthContainerView.this.lambda$animateAway$3$AuthContainerView(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateAway$2 */
    public /* synthetic */ void lambda$animateAway$2$AuthContainerView() {
        setVisibility(4);
        removeWindowIfAttached();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateAway$3 */
    public /* synthetic */ void lambda$animateAway$3$AuthContainerView(Runnable runnable) {
        this.mPanelView.animate().translationY(this.mTranslationY).setDuration(350).setInterpolator(this.mLinearOutSlowIn).withLayer().withEndAction(runnable).start();
        this.mBiometricScrollView.animate().translationY(this.mTranslationY).setDuration(350).setInterpolator(this.mLinearOutSlowIn).withLayer().start();
        AuthCredentialView authCredentialView = this.mCredentialView;
        if (authCredentialView != null && authCredentialView.isAttachedToWindow()) {
            this.mCredentialView.animate().translationY(this.mTranslationY).setDuration(350).setInterpolator(this.mLinearOutSlowIn).withLayer().start();
        }
        animate().alpha(0.0f).setDuration(350).setInterpolator(this.mLinearOutSlowIn).withLayer().start();
    }

    private void sendPendingCallbackIfNotNull() {
        StringBuilder sb = new StringBuilder();
        sb.append("pendingCallback: ");
        sb.append(this.mPendingCallbackReason);
        Log.d("BiometricPrompt/AuthContainerView", sb.toString());
        Integer num = this.mPendingCallbackReason;
        if (num != null) {
            this.mConfig.mCallback.onDismissed(num.intValue(), this.mCredentialAttestation);
            this.mPendingCallbackReason = null;
        }
    }

    private void removeWindowIfAttached() {
        sendPendingCallbackIfNotNull();
        if (this.mContainerState != 5) {
            this.mContainerState = 5;
            this.mWindowManager.removeView(this);
        }
    }

    /* access modifiers changed from: private */
    public void onDialogAnimatedIn() {
        if (this.mContainerState == 2) {
            Log.d("BiometricPrompt/AuthContainerView", "onDialogAnimatedIn(): mPendingDismissDialog=true, dismissing now");
            animateAway(false, 0);
            return;
        }
        this.mContainerState = 3;
        AuthBiometricView authBiometricView = this.mBiometricView;
        if (authBiometricView != null) {
            authBiometricView.onDialogAnimatedIn();
        }
    }

    public static LayoutParams getLayoutParams(IBinder iBinder) {
        LayoutParams layoutParams = new LayoutParams(-1, -1, 2017, 16785408, -3);
        layoutParams.privateFlags |= 16;
        layoutParams.setTitle("BiometricPrompt");
        layoutParams.token = iBinder;
        layoutParams.setFitInsetsTypes(layoutParams.getFitInsetsTypes() & (~Type.statusBars()));
        return layoutParams;
    }
}
