package com.android.systemui.biometrics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2008R$color;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2017R$string;
import java.util.ArrayList;

public abstract class AuthBiometricView extends LinearLayout {
    /* access modifiers changed from: private */
    public final AccessibilityManager mAccessibilityManager;
    private final OnClickListener mBackgroundClickListener;
    private Bundle mBiometricPromptBundle;
    private Callback mCallback;
    /* access modifiers changed from: private */
    public TextView mDescriptionView;
    protected boolean mDialogSizeAnimating;
    private int mEffectiveUserId;
    private final Handler mHandler;
    private float mIconOriginalY;
    protected ImageView mIconView;
    @VisibleForTesting
    protected TextView mIndicatorView;
    private final Injector mInjector;
    private int mMediumHeight;
    private int mMediumWidth;
    @VisibleForTesting
    Button mNegativeButton;
    private AuthPanelController mPanelController;
    @VisibleForTesting
    Button mPositiveButton;
    private boolean mRequireConfirmation;
    private final Runnable mResetErrorRunnable;
    private final Runnable mResetHelpRunnable;
    protected Bundle mSavedState;
    int mSize;
    protected int mState;
    /* access modifiers changed from: private */
    public TextView mSubtitleView;
    private final int mTextColorError;
    private final int mTextColorHint;
    /* access modifiers changed from: private */
    public TextView mTitleView;
    @VisibleForTesting
    Button mTryAgainButton;

    interface Callback {
        void onAction(int i);
    }

    @VisibleForTesting
    static class Injector {
        AuthBiometricView mBiometricView;

        public int getDelayAfterError() {
            return 2000;
        }

        public int getMediumToLargeAnimationDurationMs() {
            return 450;
        }

        Injector() {
        }

        public Button getNegativeButton() {
            return (Button) this.mBiometricView.findViewById(C2011R$id.button_negative);
        }

        public Button getPositiveButton() {
            return (Button) this.mBiometricView.findViewById(C2011R$id.button_positive);
        }

        public Button getTryAgainButton() {
            return (Button) this.mBiometricView.findViewById(C2011R$id.button_try_again);
        }

        public TextView getTitleView() {
            return (TextView) this.mBiometricView.findViewById(C2011R$id.title);
        }

        public TextView getSubtitleView() {
            return (TextView) this.mBiometricView.findViewById(C2011R$id.subtitle);
        }

        public TextView getDescriptionView() {
            return (TextView) this.mBiometricView.findViewById(C2011R$id.description);
        }

        public TextView getIndicatorView() {
            return (TextView) this.mBiometricView.findViewById(C2011R$id.indicator);
        }

        public ImageView getIconView() {
            return (ImageView) this.mBiometricView.findViewById(C2011R$id.biometric_icon);
        }
    }

    /* access modifiers changed from: protected */
    public abstract int getDelayAfterAuthenticatedDurationMs();

    /* access modifiers changed from: protected */
    public abstract int getStateForAfterError();

    /* access modifiers changed from: protected */
    public abstract void handleResetAfterError();

    /* access modifiers changed from: protected */
    public abstract void handleResetAfterHelp();

    public void setUserId(int i) {
    }

    /* access modifiers changed from: protected */
    public abstract boolean supportsSmallDialog();

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$AuthBiometricView(View view) {
        String str = "BiometricPrompt/AuthBiometricView";
        if (this.mState == 6) {
            Log.w(str, "Ignoring background click after authenticated");
            return;
        }
        int i = this.mSize;
        if (i == 1) {
            Log.w(str, "Ignoring background click during small dialog");
        } else if (i == 3) {
            Log.w(str, "Ignoring background click during large dialog");
        } else {
            this.mCallback.onAction(2);
        }
    }

    public AuthBiometricView(Context context) {
        this(context, null);
    }

    public AuthBiometricView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, new Injector());
    }

    @VisibleForTesting
    AuthBiometricView(Context context, AttributeSet attributeSet, Injector injector) {
        super(context, attributeSet);
        this.mSize = 0;
        this.mBackgroundClickListener = new OnClickListener() {
            public final void onClick(View view) {
                AuthBiometricView.this.lambda$new$0$AuthBiometricView(view);
            }
        };
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mTextColorError = getResources().getColor(C2008R$color.biometric_dialog_error, context.getTheme());
        this.mTextColorHint = getResources().getColor(C2008R$color.biometric_dialog_gray, context.getTheme());
        this.mInjector = injector;
        injector.mBiometricView = this;
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
        this.mResetErrorRunnable = new Runnable() {
            public final void run() {
                AuthBiometricView.this.lambda$new$1$AuthBiometricView();
            }
        };
        this.mResetHelpRunnable = new Runnable() {
            public final void run() {
                AuthBiometricView.this.lambda$new$2$AuthBiometricView();
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$AuthBiometricView() {
        updateState(getStateForAfterError());
        handleResetAfterError();
        Utils.notifyAccessibilityContentChanged(this.mAccessibilityManager, this);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$AuthBiometricView() {
        updateState(2);
        handleResetAfterHelp();
        Utils.notifyAccessibilityContentChanged(this.mAccessibilityManager, this);
    }

    public void setPanelController(AuthPanelController authPanelController) {
        this.mPanelController = authPanelController;
    }

    public void setBiometricPromptBundle(Bundle bundle) {
        this.mBiometricPromptBundle = bundle;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void setBackgroundView(View view) {
        view.setOnClickListener(this.mBackgroundClickListener);
    }

    public void setEffectiveUserId(int i) {
        this.mEffectiveUserId = i;
    }

    public void setRequireConfirmation(boolean z) {
        this.mRequireConfirmation = z;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void updateSize(final int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("Current size: ");
        sb.append(this.mSize);
        sb.append(" New size: ");
        sb.append(i);
        String str = "BiometricPrompt/AuthBiometricView";
        Log.v(str, sb.toString());
        if (i == 1) {
            this.mTitleView.setVisibility(8);
            this.mSubtitleView.setVisibility(8);
            this.mDescriptionView.setVisibility(8);
            this.mIndicatorView.setVisibility(8);
            this.mNegativeButton.setVisibility(8);
            float dimension = getResources().getDimension(C2009R$dimen.biometric_dialog_icon_padding);
            this.mIconView.setY(((float) (getHeight() - this.mIconView.getHeight())) - dimension);
            this.mPanelController.updateForContentDimensions(this.mMediumWidth, ((this.mIconView.getHeight() + (((int) dimension) * 2)) - this.mIconView.getPaddingTop()) - this.mIconView.getPaddingBottom(), 0);
            this.mSize = i;
        } else if (this.mSize == 1 && i == 2) {
            if (!this.mDialogSizeAnimating) {
                this.mDialogSizeAnimating = true;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.mIconView.getY(), this.mIconOriginalY});
                ofFloat.addUpdateListener(new AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        AuthBiometricView.this.lambda$updateSize$3$AuthBiometricView(valueAnimator);
                    }
                });
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                ofFloat2.addUpdateListener(new AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        AuthBiometricView.this.lambda$updateSize$4$AuthBiometricView(valueAnimator);
                    }
                });
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setDuration(150);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        super.onAnimationStart(animator);
                        AuthBiometricView.this.mTitleView.setVisibility(0);
                        AuthBiometricView.this.mIndicatorView.setVisibility(0);
                        AuthBiometricView.this.mNegativeButton.setVisibility(0);
                        AuthBiometricView.this.mTryAgainButton.setVisibility(0);
                        if (!TextUtils.isEmpty(AuthBiometricView.this.mSubtitleView.getText())) {
                            AuthBiometricView.this.mSubtitleView.setVisibility(0);
                        }
                        if (!TextUtils.isEmpty(AuthBiometricView.this.mDescriptionView.getText())) {
                            AuthBiometricView.this.mDescriptionView.setVisibility(0);
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        super.onAnimationEnd(animator);
                        AuthBiometricView authBiometricView = AuthBiometricView.this;
                        authBiometricView.mSize = i;
                        authBiometricView.mDialogSizeAnimating = false;
                        Utils.notifyAccessibilityContentChanged(authBiometricView.mAccessibilityManager, AuthBiometricView.this);
                    }
                });
                animatorSet.play(ofFloat).with(ofFloat2);
                animatorSet.start();
                this.mPanelController.updateForContentDimensions(this.mMediumWidth, this.mMediumHeight, 150);
            } else {
                return;
            }
        } else if (i == 2) {
            this.mPanelController.updateForContentDimensions(this.mMediumWidth, this.mMediumHeight, 0);
            this.mSize = i;
        } else if (i == 3) {
            ValueAnimator ofFloat3 = ValueAnimator.ofFloat(new float[]{getY(), getY() - getResources().getDimension(C2009R$dimen.biometric_dialog_medium_to_large_translation_offset)});
            ofFloat3.setDuration((long) this.mInjector.getMediumToLargeAnimationDurationMs());
            ofFloat3.addUpdateListener(new AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    AuthBiometricView.this.setTranslationY(((Float) valueAnimator.getAnimatedValue()).floatValue());
                }
            });
            ofFloat3.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    if (this.getParent() != null) {
                        ((ViewGroup) this.getParent()).removeView(this);
                    }
                    AuthBiometricView.this.mSize = i;
                }
            });
            ValueAnimator ofFloat4 = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            ofFloat4.setDuration((long) (this.mInjector.getMediumToLargeAnimationDurationMs() / 2));
            ofFloat4.addUpdateListener(new AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    AuthBiometricView.this.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
                }
            });
            this.mPanelController.setUseFullScreen(true);
            AuthPanelController authPanelController = this.mPanelController;
            authPanelController.updateForContentDimensions(authPanelController.getContainerWidth(), this.mPanelController.getContainerHeight(), this.mInjector.getMediumToLargeAnimationDurationMs());
            AnimatorSet animatorSet2 = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            arrayList.add(ofFloat3);
            arrayList.add(ofFloat4);
            animatorSet2.playTogether(arrayList);
            animatorSet2.setDuration((long) ((this.mInjector.getMediumToLargeAnimationDurationMs() * 2) / 3));
            animatorSet2.start();
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Unknown transition from: ");
            sb2.append(this.mSize);
            sb2.append(" to: ");
            sb2.append(i);
            Log.e(str, sb2.toString());
        }
        Utils.notifyAccessibilityContentChanged(this.mAccessibilityManager, this);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateSize$3 */
    public /* synthetic */ void lambda$updateSize$3$AuthBiometricView(ValueAnimator valueAnimator) {
        this.mIconView.setY(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateSize$4 */
    public /* synthetic */ void lambda$updateSize$4$AuthBiometricView(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.mTitleView.setAlpha(floatValue);
        this.mIndicatorView.setAlpha(floatValue);
        this.mNegativeButton.setAlpha(floatValue);
        this.mTryAgainButton.setAlpha(floatValue);
        if (!TextUtils.isEmpty(this.mSubtitleView.getText())) {
            this.mSubtitleView.setAlpha(floatValue);
        }
        if (!TextUtils.isEmpty(this.mDescriptionView.getText())) {
            this.mDescriptionView.setAlpha(floatValue);
        }
    }

    public void updateState(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("newState: ");
        sb.append(i);
        String str = "BiometricPrompt/AuthBiometricView";
        Log.v(str, sb.toString());
        if (i == 1 || i == 2) {
            removePendingAnimations();
            if (this.mRequireConfirmation) {
                this.mPositiveButton.setEnabled(false);
                this.mPositiveButton.setVisibility(0);
            }
        } else if (i != 4) {
            if (i == 5) {
                removePendingAnimations();
                this.mNegativeButton.setText(C2017R$string.cancel);
                this.mNegativeButton.setContentDescription(getResources().getString(C2017R$string.cancel));
                this.mPositiveButton.setEnabled(true);
                this.mPositiveButton.setVisibility(0);
                this.mIndicatorView.setTextColor(this.mTextColorHint);
                this.mIndicatorView.setText(C2017R$string.biometric_dialog_tap_confirm);
                this.mIndicatorView.setVisibility(0);
            } else if (i != 6) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Unhandled state: ");
                sb2.append(i);
                Log.w(str, sb2.toString());
            } else {
                if (this.mSize != 1) {
                    this.mPositiveButton.setVisibility(8);
                    this.mNegativeButton.setVisibility(8);
                    this.mIndicatorView.setVisibility(4);
                }
                announceForAccessibility(getResources().getString(C2017R$string.biometric_dialog_authenticated));
                this.mHandler.postDelayed(new Runnable() {
                    public final void run() {
                        AuthBiometricView.this.lambda$updateState$7$AuthBiometricView();
                    }
                }, (long) getDelayAfterAuthenticatedDurationMs());
            }
        } else if (this.mSize == 1) {
            updateSize(2);
        }
        Utils.notifyAccessibilityContentChanged(this.mAccessibilityManager, this);
        this.mState = i;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateState$7 */
    public /* synthetic */ void lambda$updateState$7$AuthBiometricView() {
        this.mCallback.onAction(1);
    }

    public void onDialogAnimatedIn() {
        updateState(2);
    }

    public void onAuthenticationSucceeded() {
        removePendingAnimations();
        if (this.mRequireConfirmation) {
            updateState(5);
        } else {
            updateState(6);
        }
    }

    public void onAuthenticationFailed(String str) {
        showTemporaryMessage(str, this.mResetErrorRunnable);
        updateState(4);
    }

    public void onError(String str) {
        showTemporaryMessage(str, this.mResetErrorRunnable);
        updateState(4);
        this.mHandler.postDelayed(new Runnable() {
            public final void run() {
                AuthBiometricView.this.lambda$onError$8$AuthBiometricView();
            }
        }, (long) this.mInjector.getDelayAfterError());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onError$8 */
    public /* synthetic */ void lambda$onError$8$AuthBiometricView() {
        this.mCallback.onAction(5);
    }

    public void onHelp(String str) {
        if (this.mSize != 2) {
            StringBuilder sb = new StringBuilder();
            sb.append("Help received in size: ");
            sb.append(this.mSize);
            Log.w("BiometricPrompt/AuthBiometricView", sb.toString());
            return;
        }
        showTemporaryMessage(str, this.mResetHelpRunnable);
        updateState(3);
    }

    public void onSaveState(Bundle bundle) {
        bundle.putInt("try_agian_visibility", this.mTryAgainButton.getVisibility());
        bundle.putInt("state", this.mState);
        bundle.putString("indicator_string", this.mIndicatorView.getText().toString());
        bundle.putBoolean("error_is_temporary", this.mHandler.hasCallbacks(this.mResetErrorRunnable));
        bundle.putBoolean("hint_is_temporary", this.mHandler.hasCallbacks(this.mResetHelpRunnable));
        bundle.putInt("size", this.mSize);
    }

    public void restoreState(Bundle bundle) {
        this.mSavedState = bundle;
    }

    private void setTextOrHide(TextView textView, String str) {
        if (TextUtils.isEmpty(str)) {
            textView.setVisibility(8);
        } else {
            textView.setText(str);
        }
        Utils.notifyAccessibilityContentChanged(this.mAccessibilityManager, this);
    }

    private void setText(TextView textView, String str) {
        textView.setText(str);
    }

    private void removePendingAnimations() {
        this.mHandler.removeCallbacks(this.mResetHelpRunnable);
        this.mHandler.removeCallbacks(this.mResetErrorRunnable);
    }

    private void showTemporaryMessage(String str, Runnable runnable) {
        removePendingAnimations();
        this.mIndicatorView.setText(str);
        this.mIndicatorView.setTextColor(this.mTextColorError);
        this.mIndicatorView.setVisibility(0);
        this.mHandler.postDelayed(runnable, 2000);
        Utils.notifyAccessibilityContentChanged(this.mAccessibilityManager, this);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        onFinishInflateInternal();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void onFinishInflateInternal() {
        this.mTitleView = this.mInjector.getTitleView();
        this.mSubtitleView = this.mInjector.getSubtitleView();
        this.mDescriptionView = this.mInjector.getDescriptionView();
        this.mIconView = this.mInjector.getIconView();
        this.mIndicatorView = this.mInjector.getIndicatorView();
        this.mNegativeButton = this.mInjector.getNegativeButton();
        this.mPositiveButton = this.mInjector.getPositiveButton();
        this.mTryAgainButton = this.mInjector.getTryAgainButton();
        this.mNegativeButton.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                AuthBiometricView.this.lambda$onFinishInflateInternal$9$AuthBiometricView(view);
            }
        });
        this.mPositiveButton.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                AuthBiometricView.this.lambda$onFinishInflateInternal$10$AuthBiometricView(view);
            }
        });
        this.mTryAgainButton.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                AuthBiometricView.this.lambda$onFinishInflateInternal$11$AuthBiometricView(view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflateInternal$9 */
    public /* synthetic */ void lambda$onFinishInflateInternal$9$AuthBiometricView(View view) {
        if (this.mState == 5) {
            this.mCallback.onAction(2);
        } else if (isDeviceCredentialAllowed()) {
            startTransitionToCredentialUI();
        } else {
            this.mCallback.onAction(3);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflateInternal$10 */
    public /* synthetic */ void lambda$onFinishInflateInternal$10$AuthBiometricView(View view) {
        updateState(6);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflateInternal$11 */
    public /* synthetic */ void lambda$onFinishInflateInternal$11$AuthBiometricView(View view) {
        updateState(2);
        this.mCallback.onAction(4);
        this.mTryAgainButton.setVisibility(8);
        Utils.notifyAccessibilityContentChanged(this.mAccessibilityManager, this);
    }

    /* access modifiers changed from: 0000 */
    public void startTransitionToCredentialUI() {
        updateSize(3);
        this.mCallback.onAction(6);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        onAttachedToWindowInternal();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void onAttachedToWindowInternal() {
        String str;
        setText(this.mTitleView, this.mBiometricPromptBundle.getString("title"));
        if (isDeviceCredentialAllowed()) {
            int credentialType = Utils.getCredentialType(this.mContext, this.mEffectiveUserId);
            str = credentialType != 1 ? credentialType != 2 ? credentialType != 3 ? getResources().getString(C2017R$string.biometric_dialog_use_password) : getResources().getString(C2017R$string.biometric_dialog_use_password) : getResources().getString(C2017R$string.biometric_dialog_use_pattern) : getResources().getString(C2017R$string.biometric_dialog_use_pin);
        } else {
            str = this.mBiometricPromptBundle.getString("negative_text");
        }
        setText(this.mNegativeButton, str);
        setTextOrHide(this.mSubtitleView, this.mBiometricPromptBundle.getString("subtitle"));
        setTextOrHide(this.mDescriptionView, this.mBiometricPromptBundle.getString("description"));
        Bundle bundle = this.mSavedState;
        if (bundle == null) {
            updateState(1);
            return;
        }
        updateState(bundle.getInt("state"));
        this.mTryAgainButton.setVisibility(this.mSavedState.getInt("try_agian_visibility"));
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mHandler.removeCallbacksAndMessages(null);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        int min = Math.min(size, size2);
        int childCount = getChildCount();
        int i3 = 0;
        for (int i4 = 0; i4 < childCount; i4++) {
            View childAt = getChildAt(i4);
            if (childAt.getId() == C2011R$id.biometric_icon) {
                childAt.measure(MeasureSpec.makeMeasureSpec(min, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE));
            } else if (childAt.getId() == C2011R$id.button_bar) {
                childAt.measure(MeasureSpec.makeMeasureSpec(min, 1073741824), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, 1073741824));
            } else {
                childAt.measure(MeasureSpec.makeMeasureSpec(min, 1073741824), MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE));
            }
            if (childAt.getVisibility() != 8) {
                i3 += childAt.getMeasuredHeight();
            }
        }
        setMeasuredDimension(min, i3);
        this.mMediumHeight = i3;
        this.mMediumWidth = getMeasuredWidth();
    }

    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        onLayoutInternal();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void onLayoutInternal() {
        if (this.mIconOriginalY == 0.0f) {
            this.mIconOriginalY = this.mIconView.getY();
            Bundle bundle = this.mSavedState;
            if (bundle == null) {
                updateSize((this.mRequireConfirmation || !supportsSmallDialog()) ? 2 : 1);
                return;
            }
            updateSize(bundle.getInt("size"));
            String string = this.mSavedState.getString("indicator_string");
            if (this.mSavedState.getBoolean("hint_is_temporary")) {
                onHelp(string);
            } else if (this.mSavedState.getBoolean("error_is_temporary")) {
                onAuthenticationFailed(string);
            }
        }
    }

    private boolean isDeviceCredentialAllowed() {
        return Utils.isDeviceCredentialAllowed(this.mBiometricPromptBundle);
    }
}
